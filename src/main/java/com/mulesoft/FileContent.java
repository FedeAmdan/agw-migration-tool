package com.mulesoft;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileContent
{
    private final String content;
    private static final Charset DEFAULT_CHARSET =  StandardCharsets.UTF_8;
    public FileContent(String path) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        content = new String(encoded, DEFAULT_CHARSET);
    }

    public List<String> getFileContentAsList(String path) throws IOException
    {
        return Files.readAllLines(Paths.get(path), DEFAULT_CHARSET);
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
}
