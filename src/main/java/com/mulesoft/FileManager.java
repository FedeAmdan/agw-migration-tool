package com.mulesoft;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FileManager {


    private static final Charset DEFAULT_CHARSET =  StandardCharsets.UTF_8;
    private static final String NEW_WSDL_PROXY_PATH = "proxies/wsdl-proxy/wsdl-proxy.xml";
    private static final String NEW_APIKIT_PROXY_PATH = "proxies/apikit-proxy/apikit-proxy.xml";
    private static final String NEW_HTTP_PROXY_PATH = "proxies/bare-http-proxy/bare-http-proxy.xml";

    public static void copyFolder(String src, String dest) throws IOException
    {
        copyFolder(new File(src), new File(dest));
    }

    public static void copyFolder(File src, File dest)
            throws IOException {

        if (src.isDirectory()) {

            //if directory not exists, create it
            if (!dest.exists()) {
                dest.mkdir();
//                System.out.println("Directory copied from "
//                        + src + "  to " + dest);
            }

            //list all the directory contents
            String files[] = src.list();

            for (String file : files) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copyFolder(srcFile, destFile);
            }

        } else {
            //if file, then copy it
            //Use bytes stream to support all file types
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
//            System.out.println("File copied from " + src + " to " + dest);
        }
    }


    public static String getFileContent(String path)
            throws IOException
    {
        return getFileContent(path, DEFAULT_CHARSET);
    }

    public static String getFileContent(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static File[] listApps(String appsFolder)
    {
        File directory = new File(appsFolder);
        return directory.listFiles();
    }



    public static List<File> listf(String directoryName) {
        File directory = new File(directoryName);

        List<File> resultList = new ArrayList<File>();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }
        //System.out.println(fList);
        return resultList;
    }

    public static List<String> getFileContentAsList(String path) throws IOException {
        return Files.readAllLines(Paths.get(path), DEFAULT_CHARSET);
    }

    public static String getProxyXmlContent(String appPath) throws IOException {
        File xmlFile = getXmlFile(appPath);
        //The app only has one xml file
        return getFileContent(xmlFile.getAbsolutePath(), DEFAULT_CHARSET);
    }

    private static File getXmlFile(String appPath) throws FileNotFoundException {
        List<File> files = listf(appPath);
        for(File file : files) {
            if (file.getName().endsWith(".xml")) {
                return file;
            }
        }
        throw new FileNotFoundException("No XML file found in " + appPath);
    }

    public static void replaceXmlFile(String appPath, int proxyType) throws IOException {
        File oldXmlFile = getXmlFile(appPath);
        File newXmlFile = null;
        if (proxyType == ProxyType.BARE_HTTP_PROXY) {
            newXmlFile = new FileManager().getFileFromClassLoader(NEW_HTTP_PROXY_PATH);
        }
        else if (proxyType == ProxyType.APIKIT_PROXY){
            newXmlFile = new FileManager().getFileFromClassLoader(NEW_APIKIT_PROXY_PATH);
        }
        else if (proxyType == ProxyType.WSDL_PROXY){
            newXmlFile = new FileManager().getFileFromClassLoader(NEW_WSDL_PROXY_PATH);
        }
        if (newXmlFile == null)
        {
            throw new FileNotFoundException("New version of the proxy could not be found.");
        }
        Files.copy(Paths.get(newXmlFile.getPath()),Paths.get(oldXmlFile.getPath()),
                StandardCopyOption.COPY_ATTRIBUTES,StandardCopyOption.REPLACE_EXISTING);

    }

    public File getFileFromClassLoader(String file)
    {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource(file).getFile());
    }
}
