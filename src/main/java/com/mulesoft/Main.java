package com.mulesoft;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class Main {

    public static final String ROOT_FOLDER_PROPERTY = "-DagwRootFolder";
    public static final String APPS_FOLDER = "apps";
    public static final String APPS_BACKUP_FOLDER = "appsBackup";

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException
    {

        String rootFolder = getRootFolder(args);
        System.out.println("ROOT FOLDER: " + rootFolder);
        System.out.println("Creating backup of apps folder in : " + rootFolder + APPS_BACKUP_FOLDER);
        FileManager.copyFolder(rootFolder + APPS_FOLDER, rootFolder + APPS_BACKUP_FOLDER);
        System.out.println("Backup completed");

        File[] proxies = FileManager.listApps(rootFolder + APPS_FOLDER);
        for (File proxy : proxies)
        {
            int proxyType = getProxyType(proxy.getPath());
            if (proxyType == -1)
            {
                continue;
            }

            FileManager.replaceXmlFile(proxy.getPath(), proxyType);
            PropertiesManager propertiesManager = new PropertiesManager(proxy.getPath());
            String content = propertiesManager.getFileContent();
            FileManager.replacePropertiesFile(proxy.getPath(), content);
        }
        System.out.println("Migration process finished");
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
        File oldXml = FileManager.getXmlFile(proxyPath);
        String md5 = FileManager.getMD5(oldXml);
        System.out.println("MD5 "+ md5);
        if (FileManager.isHttpProxy(md5))
        {
            System.out.println(proxyPath + " detected as BARE HTTP PROXY");
            return ProxyType.BARE_HTTP_PROXY;
        }
        if (FileManager.isRamlProxy(md5))
        {
            System.out.println(proxyPath + " detected as APIKIT PROXY");
            return ProxyType.APIKIT_PROXY;
        }
        if (FileManager.isWsdlProxy(md5))
        {
            System.out.println(proxyPath + " detected as WSDL PROXY");
            return ProxyType.WSDL_PROXY;
        }
        System.out.println(proxyPath + " is NOT a generated proxy.");
        return -1;
        //String xmlContent = FileManager.getProxyXmlContent(proxyPath);
        //if (!xmlContent.contains("![p['proxy.uri']]"))
        //{
        //  System.out.println(proxyPath + " is NOT a generated proxy.");
        //  return -1;
        //}
        //if (xmlContent.contains("![wsdl(p['wsdl.uri']).services[0].preferredPort.addresses[0].location]"))
        //{
        //  System.out.println(proxyPath + " detected as WSDL PROXY");
        //  return ProxyType.WSDL_PROXY;
        //}
        //if (xmlContent.contains("![p['console.uri']]"))
        //{
        //  System.out.println(proxyPath + " detected as APIKIT PROXY");
        //  return ProxyType.APIKIT_PROXY;
        //}
        //System.out.println(proxyPath + " detected as BARE HTTP PROXY");
        //return ProxyType.BARE_HTTP_PROXY;
    }

}
