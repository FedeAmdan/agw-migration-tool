package com.mulesoft;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileContentAnalyzer
{
    private final String content;
    private static final Charset DEFAULT_CHARSET =  StandardCharsets.UTF_8;
    File xmlFile = null;
    public FileContentAnalyzer(String appPath) throws IOException
    {
        xmlFile = FileManager.getXmlFile(appPath);
        byte[] encoded = Files.readAllBytes(xmlFile.toPath());
        content = new String(encoded, DEFAULT_CHARSET);
    }

    public boolean containsDescription()
    {
        return content.contains("![p['api.description']]");
    }

    public boolean apiIsHttps()
    {
        return content.contains("https:out");
    }

    public boolean proxyIsHttps()
    {
        return content.contains("https:in");
    }

    public File getXmlFile()
    {
        return xmlFile;
    }
    public String showResults()
    {
        return "Description: " + containsDescription() + ", apiIsHttps: " + apiIsHttps() + ", proxyIsHttps: " + proxyIsHttps();
    }
 }
