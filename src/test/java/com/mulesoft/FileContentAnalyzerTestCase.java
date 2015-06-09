package com.mulesoft;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

public class FileContentAnalyzerTestCase
{
    @Test
    public void readFile() throws IOException
    {
        URL appUrl = getClass().getClassLoader().getResource("appTest");
        FileContentAnalyzer analizer = new FileContentAnalyzer(appUrl.getPath());
        assertFalse(analizer.hasApikitRef());
        assertFalse(analizer.proxyIsHttps());
        assertFalse(analizer.apiIsHttps());
        assertTrue(analizer.containsDescription());
    }
}
