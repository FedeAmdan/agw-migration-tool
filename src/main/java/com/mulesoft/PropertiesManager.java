package com.mulesoft;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;

public class PropertiesManager
{
    private final static Logger LOGGER = Logger.getLogger(ProxyCreator.class);

    public static final String PROPERTIES_RELATIVE_PATH = "/classes/config.properties";
    private static final String DEFAULT_PORT_HTTP = "80";
    private static final String DEFAULT_PORT_HTTPS = "443";
    private Map<String, String> oldProperties;
    private Map<String, String> newProperties;
    private String proxyPort;
    private String proxyHost;

    public PropertiesManager(String proxyPath) throws IOException
    {
        oldProperties = parse(proxyPath);
        newProperties = modifyProperties(oldProperties);
    }

    protected PropertiesManager()
    {
    }

    private Map<String, String> parse(String proxyPath) throws IOException
    {
        Map<String, String> properties = new HashMap<>();
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

    protected Map<String, String> modifyProperties(Map<String, String> oldProperties)
    {
        if (isOldTypeOfProxy(oldProperties))
        {
            return modifyPropertiesOfOldTypeOfProxy(oldProperties);
        }
        else
        {
            return modifyPropertiesOfNewTypeOfProxy(oldProperties);
        }
    }

    protected Map<String, String> modifyPropertiesOfOldTypeOfProxy(Map<String, String> oldProperties)
    {
        Map<String, String> newProperties = new HashMap<>();

        Iterator iterator = oldProperties.entrySet().iterator();
        boolean generateMissingProperties = true;
        while (iterator.hasNext())
        {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            String key = mapEntry.getKey().toString();
            String value = mapEntry.getValue().toString();
            if (key.equals("http.port"))
            {
                proxyHost = "0.0.0.0";
                proxyPort = value;
                newProperties.put("proxy.path", "/*");
            }
            else if (key.equals("proxy.uri"))
            {
                newProperties.put("implementation.host", getHostFromUri(value));
                newProperties.put("implementation.port", getPortFromUri(value));
                newProperties.put("implementation.path", getPathFromUri(value, false));
            }
            else if (key.equals("raml.uri") || key.equals("raml.location"))
            {
                newProperties.put("console.path", "/console/*");
                newProperties.put("raml.location", value);
            }
            else if (key.equals("wsdl.uri"))
            {
                newProperties.put(key, correctWsdlUri(value));
            }
            else
            {
                if (key.equals("api.id"))
                {
                    generateMissingProperties = false;
                }
                newProperties.put(key, value);
            }
        }

        if (generateMissingProperties)
        {
            newProperties.put("api.id", "<insert api id here>");
            newProperties.put("api.name", "<insert api name here>");
            newProperties.put("api.version", "<insert api version here>");
            newProperties.put("api.description", "<insert api description here>");
            newProperties.put("implementation.host", "<insert implementation host here>");
            newProperties.put("implementation.port", "<insert implementation port here>");
            newProperties.put("implementation.path", "<insert implementation path here>");
            LOGGER.warn("- Some properties are not provided (They have to be filled in /classes/config.properties): \"api.id, api.version, api.name, api.description, implementation.host, implementation.port, implementation.path\"");
        }
        return newProperties;
    }

    protected Map<String, String> modifyPropertiesOfNewTypeOfProxy(Map<String, String> oldProperties)
    {
        Map<String, String> newProperties = new HashMap<>();
        boolean consolePathRequired = false;
        String proxyPath = "";
        Iterator iterator = oldProperties.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            String key = mapEntry.getKey().toString();
            String value = mapEntry.getValue().toString();
            if (key.equals("wsdl.uri"))
            {
                newProperties.put(key, correctWsdlUri(value));
            }
            else if (key.contains(".uri"))
            {
                String[] split = key.split("\\.");
                String name = split[0];

                boolean addAsterisk = name.equals("proxy") || name.equals("console");

                if (name.equals("console"))
                {
                    newProperties.put(name + ".path", getPathFromUri(value, addAsterisk));
                }
                else
                {
                    String host = getHostFromUri(value);
                    String port = getPortFromUri(value);
                    if (key.equals("proxy.uri"))
                    {
                        proxyHost = host;
                        proxyPort = port;
                    }
                    else
                    {
                        newProperties.put(name + ".host", host);
                        newProperties.put(name + ".port", port);
                    }
                    newProperties.put(name + ".path", getPathFromUri(value, addAsterisk));
                    proxyPath = getPathFromUri(value, false);
                }
            }
            else
            {
                if (key.equals("raml.location"))
                {
                    consolePathRequired = true;
                }
                newProperties.put(key, value);
            }

        }
        if (consolePathRequired && !newProperties.keySet().contains("console.path"))
        {
            if (proxyPath.equals(""))
            {
                newProperties.put("console.path", "/console/*");
            }
            else
            {
                newProperties.put("console.path", proxyPath + "-console/*");
            }
        }
        return newProperties;
    }

    protected boolean isOldTypeOfProxy(Map<String, String> oldProperties)
    {
        for (String key : oldProperties.keySet())
        {
            if ("http.port".equals(key))
            {
                return true;
            }
        }
        return false;
        /*


        Iterator iterator = oldProperties.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            if (mapEntry.getKey().toString().equals("http.port"))
            {
                return true;
            }
        }
        return false;
        */
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


    private String getPathFromUri(String baseUri, boolean addAsterisk)
    {
        int start = baseUri.indexOf("//") + 2;
        if (start == -1)
        {
            start = 0;
        }

        int slash = baseUri.indexOf("/", start);
        if (slash == -1 || slash == baseUri.length())
        {
            return addAsterisk ? "/*" : "/";
        }
        String path = baseUri.substring(slash, baseUri.length());
        int curlyBrace = baseUri.indexOf("{", slash);
        if (curlyBrace == -1)
        {
            return addAsterisk ? addAsteriskToPath(path) : path;
        }
        path = baseUri.substring(slash, curlyBrace);
        return addAsterisk ? addAsteriskToPath(path) : path;
    }

    public String addAsteriskToPath(String path)
    {
        if (path == null)
        {
            return "/*";
        }
        if (!path.endsWith("*"))
        {
            path = path.endsWith("/") ? path + "*" : path + "/*";
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
        return baseUri.substring(start, hostEnd);
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
            if (baseUri.startsWith("https"))
            {
                return DEFAULT_PORT_HTTPS;
            }
            else
            {
                return DEFAULT_PORT_HTTP;
            }
        }
        return baseUri.substring(twoDots + 1, slash);
    }

    public int getProxyPort()
    {
        return Integer.parseInt(proxyPort);
    }

    public String getProxyHost()
    {
        return proxyHost;
    }

    protected String correctWsdlUri(String wsdl)
    {
        URI wsdlUri = URI.create(wsdl);
        if (wsdlUri.getScheme() != null && wsdlUri.getScheme().startsWith("http"))
        {
            String path = wsdlUri.getPath() == null ? "" : wsdlUri.getPath();

            if (!path.startsWith("/"))
            {
                path = "/" + path;
                UriBuilder uriBuilder = UriBuilder.fromUri(wsdlUri);
                uriBuilder.replacePath(path);
                return uriBuilder.build().toString();
            }
        }

        return wsdlUri.toString();
    }
}
