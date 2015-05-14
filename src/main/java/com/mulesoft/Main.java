package com.mulesoft;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Main {

    public static final String ROOT_FOLDER_PROPERTY = "-DagwRootFolder";
    public static final String APPS_FOLDER = "apps";
    public static final String APPS_BACKUP_FOLDER = "appsBackup";
    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException
    {
        BasicConfigurator.configure();
        String rootFolder = getRootFolder(args);
        if(logger.isDebugEnabled()){
            logger.debug("Root folder: " + rootFolder);
        }
        logger.info("");
        logger.info("Creating backup of apps folder in : " + rootFolder + APPS_BACKUP_FOLDER);
        FileManager.copyFolder(rootFolder + APPS_FOLDER, rootFolder + APPS_BACKUP_FOLDER);
        logger.info("Backup completed");
        logger.info("");
        logger.info("Proxies analisis:");
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
                continue;
            }
            PropertiesManager propertiesManager = new PropertiesManager(proxy.getPath());
            String content = propertiesManager.getFileContent();
            FileManager.replacePropertiesFile(proxy.getPath(), content);
            logger.info("Properties file created");

            FileContentAnalizer contentAnalizer = new FileContentAnalizer(proxy.getPath());
            logger.debug(contentAnalizer.showResults());
            logger.info("Content analizer finished");
            ProxyCreator proxyCreator = new ProxyCreator(contentAnalizer.getXmlFile(), contentAnalizer.hasApikitRef(), contentAnalizer.apiIsHttps(), contentAnalizer.proxyIsHttps(), contentAnalizer.containsDescription());
            proxyCreator.processTemplate(proxyType);
            logger.debug("Xml file created");

        }
        logger.info("");
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
            logger.info(proxyPath + " detected as APIKIT PROXY");
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

}
