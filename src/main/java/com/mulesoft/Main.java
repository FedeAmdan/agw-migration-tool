package com.mulesoft;

import static com.mulesoft.ProxyType.INVALID;

import com.mulesoft.domains.DomainsBuilder;
import com.mulesoft.domains.ListenerConfigEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class Main
{
    public static final String ROOT_FOLDER_PROPERTY = "-DagwRootFolder";
    public static final String APPS_FOLDER = "apps";
    public static final String APPS_BACKUP_FOLDER = "appsBackup";
    public static final String DEFAULT_DOMAIN_FILE = "/domains/default/mule-domain-config.xml";

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    private final String rootFolder;

    public Main(final String rootFolder)
    {
        this.rootFolder = rootFolder;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException
    {
        final String rootFolder = getRootFolder(args);
        if (rootFolder == null)
        {
            LOGGER.error("Usage: java -jar agw-migration-tool.jar " + ROOT_FOLDER_PROPERTY + "=[GATEWAY_ROOT_FOLDER]");
            return;
        }
        final Main main = new Main(rootFolder);
        main.migrate();
    }

    private static String getRootFolder(String[] args)
    {
        if (args.length != 1 || !args[0].contains(ROOT_FOLDER_PROPERTY))
        {
            return null;
        }
        String folder = args[0].substring(args[0].indexOf("=") + 1);
        if (!folder.endsWith("/"))
        {
            folder = folder.concat("/");
        }
        return folder;
    }

    private void migrate() throws IOException, NoSuchAlgorithmException
    {
        LOGGER.debug("Root folder: " + rootFolder);

        backupAppsFolder();

        LOGGER.info("");
        LOGGER.info("Proxies analysis:");
        File[] proxies = FileManager.listApps(rootFolder + APPS_FOLDER);
        if (proxies == null || proxies.length == 0)
        {
            LOGGER.info("No apps found in " + rootFolder + APPS_FOLDER);
            return;
        }

        final DomainsBuilder domainsBuilder = new DomainsBuilder();
        domainsBuilder.setDefaultDomainLocation(rootFolder + DEFAULT_DOMAIN_FILE);

        for (File proxy : proxies)
        {
            final ProxyType proxyType = getProxyType(proxy.getPath());
            if (INVALID.equals(proxyType))
            {
                LOGGER.info(proxy.getPath() + " was not updated");
                LOGGER.info("");
                continue;
            }
            FileContentAnalyzer contentAnalyzer = new FileContentAnalyzer(proxy.getPath());
            LOGGER.debug("Proxy analyzer starting");
            LOGGER.debug(contentAnalyzer.showResults());
            LOGGER.debug("Proxy analyzer finished");

            LOGGER.debug("Properties updater starting");
            PropertiesManager propertiesManager = new PropertiesManager(proxy.getPath());
            String content = propertiesManager.getFileContent();
            FileManager.replacePropertiesFile(proxy.getPath(), content);
            LOGGER.debug("Properties updater finished");

            final ListenerConfigEntry listenerConfigEntry = domainsBuilder.addProxy(contentAnalyzer.proxyIsHttps(), propertiesManager.getProxyHost(), propertiesManager.getProxyPort());

            LOGGER.debug("Proxy config generator starting");
            ProxyCreator proxyCreator = new ProxyCreator(contentAnalyzer.getXmlFile(), contentAnalyzer.apiIsHttps(),
                                                         contentAnalyzer.proxyIsHttps(), contentAnalyzer.containsDescription(), listenerConfigEntry.getName());
            proxyCreator.processTemplate(proxyType);

            LOGGER.debug("Proxy config generator finished");
            LOGGER.info(proxy.getPath() + " was updated");
            LOGGER.info("");
        }

        domainsBuilder.build();

        LOGGER.info("Migration process finished");
    }

    private void backupAppsFolder() throws IOException
    {
        LOGGER.info("");
        LOGGER.info("Creating backup of apps folder in : " + rootFolder + APPS_BACKUP_FOLDER);

        FileUtils.copyDirectory(new File(rootFolder + APPS_FOLDER), new File(rootFolder + APPS_BACKUP_FOLDER));

        LOGGER.info("Backup completed");
    }


    private ProxyType getProxyType(String proxyPath) throws IOException, NoSuchAlgorithmException
    {
        if (!Files.isDirectory(Paths.get(proxyPath)))
        {
            LOGGER.info(proxyPath + " is NOT a generated proxy.");
            return INVALID;
        }
        File oldXml = FileManager.getXmlFile(proxyPath);
        String md5 = FileManager.getMD5(oldXml);
        LOGGER.debug("MD5 " + md5);
        if (FileManager.isHttpProxy(md5))
        {
            LOGGER.info(proxyPath + " detected as BARE HTTP PROXY");
            return ProxyType.BARE_HTTP_PROXY;
        }
        if (FileManager.isRamlProxy(md5))
        {
            LOGGER.info(proxyPath + " detected as RAML PROXY");
            return ProxyType.APIKIT_PROXY;
        }
        if (FileManager.isWsdlProxy(md5))
        {
            LOGGER.info(proxyPath + " detected as WSDL PROXY");
            return ProxyType.WSDL_PROXY;
        }
        LOGGER.info(proxyPath + " is NOT a generated proxy.");
        return INVALID;
    }

    //private static void configureLog4j()
    //{
    //    //BasicConfigurator.configure();
    //    ConsoleAppender console = new ConsoleAppender(); //create appender
    //    //configure the appender
    //    String PATTERN = "%d [%p|%c|%C{1}] %m%n";
    //    console.setLayout(new PatternLayout(PATTERN));
    //    console.setThreshold(Level.INFO);
    //    console.activateOptions();
    //    //add appender to any Logger (here is root)
    //    Logger.getRootLogger().addAppender(console);
    //
    //    FileAppender fa = new FileAppender();
    //    fa.setName("FileLogger");
    //    fa.setFile("mylog.log");
    //    fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
    //    fa.setThreshold(Level.DEBUG);
    //    fa.setAppend(true);
    //    fa.activateOptions();
    //
    //    //add appender to any Logger (here is root)
    //    Logger.getRootLogger().addAppender(fa);
    //}

}
