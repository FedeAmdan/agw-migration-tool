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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Main
{

    public static final String SOURCE_FOLDER_PROPERTY = "-DagwSourceFolder";
    public static final String TARGET_FOLDER_PROPERTY = "-DagwTargetFolder";
    public static final String APPS_FOLDER = "apps";
    public static final String APPS_BACKUP_FOLDER = "appsBackup";
    public static final String DEFAULT_DOMAIN_FILE = "/domains/default/mule-domain-config.xml";

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    private final String sourceFolder;
    private final String targetFolder;

    public Main(final String sourceFolder, String targetFolder)
    {
        this.sourceFolder = sourceFolder;
        this.targetFolder = targetFolder;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException
    {
        final String message = "Usage: java -jar [gateway-2.0-home]/tools/agw-migration-tool.jar " + SOURCE_FOLDER_PROPERTY + "=[gateway-1.X-home] " + TARGET_FOLDER_PROPERTY + "=[gateway-2.0-home]";

        if (args.length < 2)
        {
            LOGGER.error(message);
            return;
        }

        final String source = getFolderFromArguments(args[0], SOURCE_FOLDER_PROPERTY);
        final String target = getFolderFromArguments(args[1], TARGET_FOLDER_PROPERTY);
        if (source == null || target == null)
        {
            LOGGER.error(message);
            return;
        }
        final Main main = new Main(source, target);//"/Users/federicoamdan/api-gateway-proxies-test/";
        main.migrate();
    }

    private static String getFolderFromArguments(String arg, String property)
    {
        if (StringUtils.isBlank(arg) || !arg.contains(property))
        {
            return null;
        }
        String folder = arg.substring(arg.indexOf("=") + 1);
        if (!folder.endsWith("/"))
        {
            folder = folder.concat("/");
        }
        return folder;
    }

    private void migrate() throws IOException, NoSuchAlgorithmException
    {
        LOGGER.debug("Migrating proxies from : " + sourceFolder + " to " + targetFolder);

        copyApps();

        LOGGER.info("");
        LOGGER.info("Proxies analysis:");
        File[] proxies = FileManager.listApps(targetFolder + APPS_FOLDER);
        if (proxies == null || proxies.length == 0)
        {
            LOGGER.info("No apps found in " + targetFolder + APPS_FOLDER);
            return;
        }

        final DomainsBuilder domainsBuilder = new DomainsBuilder();
        domainsBuilder.setDefaultDomainFile(sourceFolder + DEFAULT_DOMAIN_FILE);
        domainsBuilder.setTargetDomainLocation(targetFolder + DEFAULT_DOMAIN_FILE);

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

    private void copyApps() throws IOException
    {
        LOGGER.info("");
        LOGGER.info("Copying apps to : target gateway");

        FileUtils.copyDirectory(new File(sourceFolder + APPS_FOLDER), new File(targetFolder + APPS_FOLDER));
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
