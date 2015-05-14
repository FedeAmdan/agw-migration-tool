package com.mulesoft;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.mulesoft.FileContentAnalizer;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

public class FileContentAnalizerTestCase
{
    @Test
    public void readFile() throws IOException
    {
        URL appUrl = getClass().getClassLoader().getResource("appTest");
        FileContentAnalizer analizer = new FileContentAnalizer(appUrl.getPath());
        assertFalse(analizer.hasApikitRef());
        assertFalse(analizer.proxyIsHttps());
        assertFalse(analizer.apiIsHttps());
        assertTrue(analizer.containsDescription());
    }
}
