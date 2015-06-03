package com.mulesoft;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class ProxyCreatorTestCase
{
    @Test
    public void createHttpV1() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.BARE_HTTP_PROXY, false, false, false, false);
        String expectedContent = FileManager.getFileContent(getClass().getResource("/outputs/http-v1.xml").getPath(), FileManager.DEFAULT_CHARSET);
        assertEquals(expectedContent, content);
    }

    @Test
    public void createHttpV2() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.BARE_HTTP_PROXY, false, false, false, true);
        assertTrue(content.contains("![p['api.description']]"));
    }

    @Test
    public void createHttpV3() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.BARE_HTTP_PROXY, false, false, true, false);
        assertFalse(content.contains("![p['api.description']]"));
        //assertTrue(content.contains("tls-context-config"));
        //assertTrue(content.contains("tls:key-store"));
    }

    @Test
    public void createHttpV4() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.BARE_HTTP_PROXY, false, false, true, true);
        assertTrue(content.contains("![p['api.description']]"));
        //assertTrue(content.contains("tls-context-config"));
        //assertTrue(content.contains("tls:key-store"));
    }
    @Test
    public void createHttpV5() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.BARE_HTTP_PROXY, false, true, false, false);
        assertFalse(content.contains("![p['api.description']]"));
        //assertFalse(content.contains("tls-context-config"));
        assertTrue(content.contains("basePath=\"![p['implementation.path']]\" protocol=\"HTTPS\""));
    }

    @Test
    public void createHttpV6() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.BARE_HTTP_PROXY, false, true, false, true);
        assertTrue(content.contains("![p['api.description']]"));
        //assertFalse(content.contains("tls-context-config"));
        assertTrue(content.contains("basePath=\"![p['implementation.path']]\" protocol=\"HTTPS\""));
    }

    @Test
    public void createHttpV7() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.BARE_HTTP_PROXY, false, true, true, false);
        assertFalse(content.contains("![p['api.description']]"));
        //assertTrue(content.contains("tls-context-config"));
        assertTrue(content.contains("basePath=\"![p['implementation.path']]\" protocol=\"HTTPS\""));
    }

    @Test
    public void createHttpV8() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.BARE_HTTP_PROXY, false, true, true, true);
        assertTrue(content.contains("![p['api.description']]"));
        //assertTrue(content.contains("tls-context-config"));
        assertTrue(content.contains("basePath=\"![p['implementation.path']]\" protocol=\"HTTPS\""));
    }

    @Test
    public void createRamlV1() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.APIKIT_PROXY, false, false, false, false);
        String expectedContent = FileManager.getFileContent(getClass().getResource("/outputs/raml-v1.xml").getPath(), FileManager.DEFAULT_CHARSET);
        assertEquals(expectedContent, content);
    }

    @Test
    public void createRamlV2() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.APIKIT_PROXY, false, false, false, true);
        assertTrue(content.contains("![p['api.description']]"));
    }

    @Test
    public void createRamlV3() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.APIKIT_PROXY, false, false, true, false);
        assertFalse(content.contains("![p['api.description']]"));
        //assertTrue(content.contains("tls-context-config"));
        //assertTrue(content.contains("tls:key-store"));
    }

    @Test
    public void createRamlV4() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.APIKIT_PROXY, false, false, true, true);
        assertTrue(content.contains("![p['api.description']]"));
        //assertTrue(content.contains("tls-context-config"));
        //assertTrue(content.contains("tls:key-store"));
    }

    @Test
    public void createRamlV5() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.APIKIT_PROXY, true, false, true, true);
        assertTrue(content.contains("![p['api.description']]"));
        //assertTrue(content.contains("tls-context-config"));
        //assertTrue(content.contains("tls:key-store"));
        assertTrue(content.contains("apikitRef"));
    }

    @Test
    public void createWsdlV1() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.WSDL_PROXY, false, false, false, false);
        String expectedContent = FileManager.getFileContent(getClass().getResource("/outputs/wsdl-v1.xml").getPath(), FileManager.DEFAULT_CHARSET);
        assertEquals(expectedContent, content);
    }

    @Test
    public void createWsdlV2() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.WSDL_PROXY, false, false, false, true);
        assertTrue(content.contains("![p['api.description']]"));
    }

    @Test
    public void createWsdlV3() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.WSDL_PROXY, false, false, true, false);
        assertFalse(content.contains("![p['api.description']]"));
        //assertTrue(content.contains("tls-context-config"));
        //assertTrue(content.contains("tls:key-store"));
    }

    @Test
    public void createWsdlV4() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.WSDL_PROXY, false, false, true, true);
        assertTrue(content.contains("![p['api.description']]"));
        //assertTrue(content.contains("tls-context-config"));
        //assertTrue(content.contains("tls:key-store"));
    }
    @Test
    public void createWsdlV5() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.WSDL_PROXY, false, true, false, false);
        assertFalse(content.contains("![p['api.description']]"));
        //assertFalse(content.contains("tls-context-config"));
        assertTrue(content.contains("basePath=\"![p['implementation.path']]\" protocol=\"HTTPS\""));
    }

    @Test
    public void createWsdlV6() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.WSDL_PROXY, false, true, false, true);
        assertTrue(content.contains("![p['api.description']]"));
        assertFalse(content.contains("tls-context-config"));
        assertTrue(content.contains("basePath=\"![p['implementation.path']]\" protocol=\"HTTPS\""));
    }

    @Test
    public void createWsdlV7() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.WSDL_PROXY, false, true, true, false);
        assertFalse(content.contains("![p['api.description']]"));
        //assertTrue(content.contains("tls-context-config"));
        assertTrue(content.contains("basePath=\"![p['implementation.path']]\" protocol=\"HTTPS\""));
    }

    @Test
    public void createWsdlV8() throws IOException
    {
        String content = createProxyAndGetContent(ProxyType.WSDL_PROXY, false, true, true, true);
        assertTrue(content.contains("![p['api.description']]"));
        //assertTrue(content.contains("tls-context-config"));
        assertTrue(content.contains("basePath=\"![p['implementation.path']]\" protocol=\"HTTPS\""));
    }

    private String createProxyAndGetContent(ProxyType proxyType, boolean hasApikitRef, boolean apiIsHttps, boolean proxyIsHttps, boolean containsDescription) throws IOException
    {
        File destiny = File.createTempFile("temp-file", ".xml");
        ProxyCreator creator = new ProxyCreator(destiny, hasApikitRef, apiIsHttps, proxyIsHttps, containsDescription, "http-listener-config");
        creator.processTemplate(proxyType);
        return FileManager.getFileContent(destiny.getPath(),FileManager.DEFAULT_CHARSET);
    }

}
