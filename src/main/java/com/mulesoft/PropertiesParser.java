package com.mulesoft;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertiesParser {

    private static final String PROPERTIES_RELATIVE_PATH = "classes/config.properties";

    public Map<String, String> parse (String proxyPath) throws IOException {
        Map <String,String> properties = new HashMap<>();
        List<String> lines = FileManager.getFileContentAsList(proxyPath + PROPERTIES_RELATIVE_PATH);
        for (String line : lines)
        {
            String[] split = line.split("=");
            properties.put(split[0],split[1]);

        }
        return properties;
    }


}
