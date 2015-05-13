package com.mulesoft;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FileManager {


    private static final Charset DEFAULT_CHARSET =  StandardCharsets.UTF_8;
    private static final List<String> HTTP_MD5 = Arrays.asList("43b3c949368a85a4fe9b9bf5e37b66f1", "55127b10cb0618bec8eecf105cedb995", "f1745d2d58ec6659b2e2af0c9fdd02c8", "53c0e605b89130027dcf71d0c21ac591", "cdd1fadd40e5e9085e81b8b84b750b0c", "53c0e605b89130027dcf71d0c21ac591", "93ce349081e19a9b960b905eace7c2df", "f1745d2d58ec6659b2e2af0c9fdd02c8", "5bb56e89fe7890c3f1dd0530d15504bc", "dd0495a8ccbeda6dfa1bd1aa85d06538", "4eafaf3ae7bb7e75fa6f7df2593665c1", "194e4f4f972eac7fe01f1bae242fadca", "b6dcd56d453ead3ae51e218bbd875021", "a5b02e7959f702900432bf45cd1ec6e4", "2804ffa6036af89cb6aebdeb83d8ca1b", "7e1f8bb2262bf03018c0032c9df5bb61", "d8b28cda68c86bce3b4ee0909b79edd6", "1ccb397414977c9203c760852025b8bc", "6d318a87b8413d907c7e4f15ef6ef17f", "b12374526e37fd9e3f73b6ec6f1feee5", "2064701b601a84285f1fa7665a40e1b3", "71ec87c6698d8cf900f9cd99397823fa", "25af3eccb5d9adc9486eb99d3ba63ea8", "6de4ab912680fe8748d7a6e153c1579e");
    private static final List<String> RAML_MD5 = Arrays.asList("f6b01af9ac6ae2bdfa6ddc4a21f15906", "fd61886aeae09778c2af2ef48711ca93", "467187e5dfa8ebb445e79b4e4ee6561e", "d231465c0757c2ab1a018acaf405b9b5", "f627351cf7c19fa3d5fd811c5d862adf", "d231465c0757c2ab1a018acaf405b9b5", "bed4b03040b9dd3e90c5a0c62ada7fc0", "467187e5dfa8ebb445e79b4e4ee6561e", "d6a302f9c60631f481045f69dd014520", "7b078d3ff7d7d35657e425a0adf97654", "dadb530eb8807f1459f109b5f786b533", "675a4626ecb5cc43d5407479850085d1", "2db1ec37353b4cc6b31d4478274eb6a6", "eeb164b66cafe6c88b1714a430c7c411", "40167f5ea7e9563c6444c91a9b8affe2", "62839b277be7e8ae0fede0b8c87e9424", "da451d280d046830e4655f6281815c1e", "4c90b1c304e6b222f331bc05d2d543d0", "da8a76118f1ab74ba8b88e260b9ef8cb", "fe6000bf2afd6ecf1487397a0332f275", "7339de9bc9b69956168206d04418620c", "be1d607ac4e172bb49f39a8f04ecd4c0", "d4777d13805e02c9d2ac9b25848c2ab2", "be1d490d65ead87c58d287e850ae1b8f", "f406a95a03c29e563d6c852a78dd2cd4", "ba264621dbff1d65460791369f786b63", "999a9c8340837a294b67305e921e480a", "9db08a859027467f91675a479ea871e2", "47f66871322e7db8f0a2da574aa00b42", "6efa5629b206c8076fd531df5e437660", "d3636f8845609c22e70074bbe74c733f", "936b8acb105b2ec139c2dfe67b910c2b", "afdd9b097d0e686093725843a480fd77", "c0cf8c625e47637db54833d4860ea3f4", "4ea8814ccb5955cfa402506c02f676ed", "eab9c78877482d4ee771739847edf2c1", "06479e61330964eb2ff6a4833d579fed", "c0929cc02dd832f55239914603218a57", "5954dec7bb0766fae36b8c96723766ba", "4226542bc00262f7b0ec04c6db19e827");
    private static final List<String> WSDL_MD5 = Arrays.asList("3a810db8e361eb3059a2d045751a3b80", "c5b2d04d255432ac02ba5f8c61eae3df", "9c37fec6fdfaed574c23d490955f3e21", "9b12d707fcd926988fa6a467a7fc0603", "efe36dcf13989fa26b19d3f7f36d2de0", "9b12d707fcd926988fa6a467a7fc0603", "ff7855d53954cdcd677d094c6a284e3c", "9c37fec6fdfaed574c23d490955f3e21", "1c08fc075ad255358590929cf3315a90", "787bb021d921a5acdbc59f89f8971106", "a5ac3b6ff7cdded9164b3e0325c4f1f1", "8e62484a8725c1e85004bfae5cf8bb44", "a4c5111db8f2c03bc1fc12d3fc63f05f", "90287237ab703ea1afb418e483c250e7", "0d84711ae35d3ae206cdea7a6230303d", "0e71931927ac6b53fce3693bb7a8911f", "667882701d4f1cead715ddf550bf927f", "8d6ec8bbfb48076c950e619ce4f2e979", "583c91655646da374bfd107330baf075", "4c5b35d737515070344fa116c504ed64", "fe7bdf5c60b9db8ff4f189c8888281df", "3bf2083b9bfac24cbc0fa5a8c5c6fe40", "3ef7c2e1d3d1aa0ab9dd7875362e0e97", "935888c2ca66ee98543fe5815fac4dfd");

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


    //public static String getFileContent(String path)
    //        throws IOException
    //{
    //    return getFileContent(path, DEFAULT_CHARSET);
    //}

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
//                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }
        //System.out.println(fList);
        return resultList;
    }

    //public static String getProxyXmlContent(String appPath) throws IOException {
    //    File xmlFile = getXmlFile(appPath);
    //    //The app only has one xml file
    //    return getFileContent(xmlFile.getAbsolutePath(), DEFAULT_CHARSET);
    //}

    public static File getXmlFile(String appPath) throws FileNotFoundException {
        System.out.println("Fetching xml file from: " + appPath);
        List<File> files = listf(appPath);
        for(File file : files) {
            if (file.getName().endsWith(".xml")) {
                return file;
            }
        }
        throw new FileNotFoundException("No XML file found in " + appPath);
    }

    //public static void replaceXmlFile(String appPath, int proxyType) throws IOException, NoSuchAlgorithmException
    //{
    //    File oldXmlFile = getXmlFile(appPath);
    //    String newXmlLines = null;
    //    if (proxyType == ProxyType.BARE_HTTP_PROXY) {
    //        newXmlLines = new FileManager().readFileFromClassPath(NEW_HTTP_PROXY_PATH);
    //    }
    //    else if (proxyType == ProxyType.APIKIT_PROXY){
    //        newXmlLines = new FileManager().readFileFromClassPath(NEW_APIKIT_PROXY_PATH);
    //    }
    //    else if (proxyType == ProxyType.WSDL_PROXY){
    //        newXmlLines = new FileManager().readFileFromClassPath(NEW_WSDL_PROXY_PATH);
    //    }
    //    if (newXmlLines == null)
    //    {
    //        throw new FileNotFoundException("New version of the proxy could not be found.");
    //    }
    //    Path dest = Paths.get(oldXmlFile.getPath());
    //    Files.write(dest, newXmlLines.getBytes());
    //}

    public static void replacePropertiesFile(String appPath, String newFileContent) throws IOException
    {
//        File propertiesFile = new File(appPath + PropertiesManager.PROPERTIES_RELATIVE_PATH);
        FileWriter writer = new FileWriter(appPath + PropertiesManager.PROPERTIES_RELATIVE_PATH);
        BufferedWriter buffWriter = new BufferedWriter(writer);
        buffWriter.write(newFileContent);
        buffWriter.close();
     }


    public static String getMD5(File file) throws NoSuchAlgorithmException, IOException
    {
        FileInputStream inputStream = new FileInputStream(file);
        MessageDigest digest = MessageDigest.getInstance("MD5");

        byte[] bytesBuffer = new byte[1024];
        int bytesRead = -1;

        while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
            digest.update(bytesBuffer, 0, bytesRead);
        }

        byte[] hashedBytes = digest.digest();

        return convertByteArrayToHexString(hashedBytes);

    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                                        .substring(1));
        }
        return stringBuffer.toString();
    }

    public static boolean isHttpProxy(String md5)
    {
        return HTTP_MD5.contains(md5);
    }

    public static boolean isRamlProxy(String md5)
    {
        return RAML_MD5.contains(md5);
    }

    public static boolean isWsdlProxy(String md5)
    {
        return WSDL_MD5.contains(md5);
    }

    public static List<String> getFileContentAsList(String path) throws IOException
    {
        return Files.readAllLines(Paths.get(path), DEFAULT_CHARSET);
    }

}
