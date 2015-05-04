package com.mulesoft;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PropertiesManager {

    public static final String PROPERTIES_RELATIVE_PATH = "/classes/config.properties";
    private static final String DEFAULT_PORT = "80";
    private Map<String, String> oldProperties;
    private Map<String, String> newProperties;

    public PropertiesManager (String proxyPath) throws IOException
    {
        oldProperties = parse(proxyPath);
        newProperties = modifyProperties(oldProperties);

    }

    private Map<String, String> parse (String proxyPath) throws IOException {
        Map <String,String> properties = new HashMap<>();
        List<String> lines = FileManager.getFileContentAsList(proxyPath + PROPERTIES_RELATIVE_PATH);
        for (String line : lines)
        {
            String[] split = line.split("=");
            if (split.length == 2)
            {
                properties.put(split[0], split[1]);
            }
        }
        return properties;
    }

    private Map<String, String> modifyProperties( Map<String, String> oldProperties)
    {
        Map<String, String> newProperties = new HashMap<>();
        Iterator iterator = oldProperties.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            if (mapEntry.getKey().toString().contains(".uri"))
            {
                String[] split = mapEntry.getKey().toString().split("\\.");
                String name = split[0];

                boolean addAsterisk = name.equals("proxy");
                String url = mapEntry.getValue().toString();
                if (name.equals("console"))
                {
                    newProperties.put(name + ".path", getPathFromUri(url,addAsterisk));
                }
                else
                {
                    newProperties.put(name + ".host", getHostFromUri(url));
                    newProperties.put(name + ".port", getPortFromUri(url));
                    newProperties.put(name + ".path", getPathFromUri(url, addAsterisk));
                }
            }
            else
            {
                newProperties.put(mapEntry.getKey().toString(), mapEntry.getValue().toString());
            }
        }
        return newProperties;
    }

    public String getFileContent()
    {
        Iterator iterator = newProperties.entrySet().iterator();
        String content = "";
        while (iterator.hasNext())
        {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            content += mapEntry.getKey().toString() + "=" + mapEntry.getValue().toString() + "\n";
        }
        return content;
    }


    private String getPathFromUri(String baseUri, boolean addAsterisk) {
        int start = baseUri.indexOf("//") + 2;
        if (start == -1)
        {
            start = 0;
        }

        int slash = baseUri.indexOf("/", start);
        if (slash == -1 || slash == baseUri.length())
        {
            return addAsterisk? "/*" : "/";
        }
        String path = baseUri.substring(slash, baseUri.length());
        int curlyBrace = baseUri.indexOf("{",slash);
        if (curlyBrace == -1)
        {
            return addAsterisk? addAsteriskToPath(path): path;
        }
        path = baseUri.substring(slash,curlyBrace);
        return addAsterisk? addAsteriskToPath(path): path;
    }

    public String addAsteriskToPath(String path)
    {
        if (path == null)
        {
            return "/*";
        }
        if (!path.endsWith("*"))
        {
            path = path.endsWith("/")? path + "*" : path + "/*";
        }
        return path;
    }

    public static String getHostFromUri(String baseUri)
    {
        int start = baseUri.indexOf("//") + 2;
        if (start == -1)
        {
            start = 0;
        }

        int twoDots = baseUri.indexOf(":", start);
        if (twoDots == -1)
        {
            twoDots = baseUri.length();
        }
        int slash = baseUri.indexOf("/", start);
        if (slash == -1)
        {
            slash = baseUri.length();
        }
        int hostEnd = twoDots < slash ? twoDots : slash;
        return baseUri.substring(start,hostEnd);
    }

    public static String getPortFromUri(String baseUri)
    {
        int hostStart = baseUri.indexOf("//") + 2;
        if (hostStart == -1)
        {
            hostStart = 0;
        }
        int slash = baseUri.indexOf("/", hostStart);
        if (slash == -1)
        {
            slash = baseUri.length();
        }
        int twoDots = baseUri.indexOf(":", hostStart);
        if (twoDots == -1 || twoDots > slash)
        {
            return (DEFAULT_PORT);
        }
        return baseUri.substring(twoDots + 1, slash);
    }

}
