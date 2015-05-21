package com.mulesoft;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

public class Main {

    public static final String ROOT_FOLDER_PROPERTY = "-DagwRootFolder";
    public static final String APPS_FOLDER = "apps";
    public static final String APPS_BACKUP_FOLDER = "appsBackup";
    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException
    {
        String rootFolder = getRootFolder(args);//"/Users/federicoamdan/api-gateway-proxies-test/";
        if(logger.isDebugEnabled()){
            logger.debug("Root folder: " + rootFolder);
        }
        logger.info("");
        logger.info("Creating backup of apps folder in : " + rootFolder + APPS_BACKUP_FOLDER);
        FileManager.copyFolder(rootFolder + APPS_FOLDER, rootFolder + APPS_BACKUP_FOLDER);
        logger.info("Backup completed");
        logger.info("");
        logger.info("Proxies analysis:");
        File[] proxies = FileManager.listApps(rootFolder + APPS_FOLDER);
        if (proxies == null || proxies.length == 0)
        {
            logger.info("No apps found in " + rootFolder + APPS_FOLDER);
            return;
        }
        for (File proxy : proxies)
        {
            int proxyType = getProxyType(proxy.getPath());
            if (proxyType == ProxyType.INVALID)
            {
                logger.info(proxy.getPath() + " was not updated");
                logger.info("");
                continue;
            }
            FileContentAnalizer contentAnalizer = new FileContentAnalizer(proxy.getPath());
            logger.debug("Proxy analizer starting");
            logger.debug(contentAnalizer.showResults());
            logger.debug("Proxy analizer finished");

            logger.debug("Properties updater starting");
            PropertiesManager propertiesManager = new PropertiesManager(proxy.getPath(), contentAnalizer.proxyIsHttps());
            String content = propertiesManager.getFileContent();
            FileManager.replacePropertiesFile(proxy.getPath(), content);
            logger.debug("Properties updater finished");

            logger.debug("Proxy config generator starting");
            ProxyCreator proxyCreator = new ProxyCreator(contentAnalizer.getXmlFile(), contentAnalizer.hasApikitRef(), contentAnalizer.apiIsHttps(), contentAnalizer.proxyIsHttps(), contentAnalizer.containsDescription());
            proxyCreator.processTemplate(proxyType);
            logger.debug("Proxy config generator finished");
            logger.info(proxy.getPath() + " was updated");
            logger.info("");
        }
        logger.info("Migration process finished");
    }

    private static String getRootFolder(String[] args)
    {
        if (args.length != 1 || !args[0].contains(ROOT_FOLDER_PROPERTY))
        {
            throw new IllegalArgumentException("Invalid property. Please use " + ROOT_FOLDER_PROPERTY);
        }
        String folder = args[0].substring(args[0].indexOf("=") + 1);
        if (!folder.endsWith("/"))
        {
            folder = folder.concat("/");
        }
        return folder;

    }


    private static int getProxyType(String proxyPath) throws IOException, NoSuchAlgorithmException
    {
        if (!Files.isDirectory(Paths.get(proxyPath)))
        {
            logger.info(proxyPath + " is NOT a generated proxy.");
            return ProxyType.INVALID;
        }
        File oldXml = FileManager.getXmlFile(proxyPath);
        String md5 = FileManager.getMD5(oldXml);
        logger.debug("MD5 "+ md5);
        if (FileManager.isHttpProxy(md5))
        {
            logger.info(proxyPath + " detected as BARE HTTP PROXY");
            return ProxyType.BARE_HTTP_PROXY;
        }
        if (FileManager.isRamlProxy(md5))
        {
            logger.info(proxyPath + " detected as RAML PROXY");
            return ProxyType.APIKIT_PROXY;
        }
        if (FileManager.isWsdlProxy(md5))
        {
            logger.info(proxyPath + " detected as WSDL PROXY");
            return ProxyType.WSDL_PROXY;
        }
        logger.info(proxyPath + " is NOT a generated proxy.");
        return ProxyType.INVALID;
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
