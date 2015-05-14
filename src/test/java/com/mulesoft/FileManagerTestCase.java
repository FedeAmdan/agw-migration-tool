package com.mulesoft;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.mulesoft.FileManager;
import com.mulesoft.Main;
import com.mulesoft.ProxyCreator;
import com.mulesoft.ProxyType;

import org.junit.Test;
import sun.misc.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class FileManagerTestCase {

    @Test
    public void testValidMD5() throws IOException, NoSuchAlgorithmException
    {
        File file = new File(getClass().getResource("/inputs/http-25-v2.xml").getPath());
        String md5 = FileManager.getMD5(file);
        assertTrue(FileManager.isHttpProxy(md5));
        assertFalse(FileManager.isRamlProxy(md5));
        assertFalse(FileManager.isWsdlProxy(md5));
    }

    @Test
    public void testInvalidMD5() throws IOException, NoSuchAlgorithmException
    {
        File file = new File(getClass().getResource("/inputs/invalid.xml").getPath());
        String md5 = FileManager.getMD5(file);
        assertFalse(FileManager.isHttpProxy(md5));
        assertFalse(FileManager.isRamlProxy(md5));
        assertFalse(FileManager.isWsdlProxy(md5));
    }

}
