package com.mulesoft;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.mulesoft.PropertiesManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class PropertiesManagerTestCase
{
    @Test
    public void ramlPropertiesV1() throws IOException
    {
        PropertiesManager propertiesManager = new PropertiesManager();
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("raml.uri", "http://endpointUri.com");
        properties.put("http.port", "8081");
        boolean isOldProxy = propertiesManager.isOldTypeOfProxy(properties);
        assertTrue(isOldProxy);
        Map<String,String> newProperties = propertiesManager.modifyPropertiesOfOldTypeOfProxy(properties);
        Map<String,String> expectedProperties = new HashMap<>();
        expectedProperties.put("raml.location","http://endpointUri.com");
        expectedProperties.put("proxy.path","/*");
        expectedProperties.put("api.id","<insert api id here>");
        expectedProperties.put("api.name","<insert api name here>");
        expectedProperties.put("api.version","<insert api version here>");
        expectedProperties.put("api.description","<insert api description here>");
        expectedProperties.put("implementation.host", "<insert implementation host here>");
        expectedProperties.put("implementation.port", "<insert implementation port here>");
        expectedProperties.put("implementation.path", "<insert implementation path here>");
        expectedProperties.put("console.path","/console/*");
        assertEqualsMap(expectedProperties, newProperties);
    }

    @Test
    public void ramlPropertiesV2Http() throws IOException
    {
        PropertiesManager propertiesManager = new PropertiesManager();
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("api.id", "1");
        properties.put("api.name", "apiName");
        properties.put("api.version", "1.1.1");
        properties.put("api.description", "apiDescription");
        properties.put("proxy.uri", "http://endpointUri.com");
        properties.put("raml.location","ramlLocation");
        properties.put("http.port", "8081");
        boolean isOldProxy = propertiesManager.isOldTypeOfProxy(properties);
        assertTrue(isOldProxy);
        Map<String,String> newProperties = propertiesManager.modifyPropertiesOfOldTypeOfProxy(properties);
        Map<String,String> expectedProperties = new HashMap<>();
        expectedProperties.put("api.id","1");
        expectedProperties.put("api.name","apiName");
        expectedProperties.put("api.version","1.1.1");
        expectedProperties.put("api.description","apiDescription");
        expectedProperties.put("proxy.path","/*");
        expectedProperties.put("implementation.host","endpointUri.com");
        expectedProperties.put("implementation.port","80");
        expectedProperties.put("implementation.path","/");
        expectedProperties.put("console.path","/console/*");
        expectedProperties.put("raml.location","ramlLocation");
        assertEqualsMap(expectedProperties, newProperties);
    }

    @Test
    public void ramlPropertiesV2Https() throws IOException
    {
        PropertiesManager propertiesManager = new PropertiesManager();
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("api.id", "1");
        properties.put("api.name", "apiName");
        properties.put("api.version", "1.1.1");
        properties.put("api.description", "apiDescription");
        properties.put("proxy.uri", "https://endpointUri.com");
        properties.put("raml.location","ramlLocation");
        properties.put("http.port", "8081");
        boolean isOldProxy = propertiesManager.isOldTypeOfProxy(properties);
        assertTrue(isOldProxy);
        Map<String,String> newProperties = propertiesManager.modifyPropertiesOfOldTypeOfProxy(properties);
        Map<String,String> expectedProperties = new HashMap<>();
        expectedProperties.put("api.id","1");
        expectedProperties.put("api.name","apiName");
        expectedProperties.put("api.version","1.1.1");
        expectedProperties.put("api.description","apiDescription");
        expectedProperties.put("proxy.path","/*");
        expectedProperties.put("implementation.host","endpointUri.com");
        expectedProperties.put("implementation.port","443");
        expectedProperties.put("implementation.path","/");
        expectedProperties.put("console.path","/console/*");
        expectedProperties.put("raml.location","ramlLocation");
        assertEqualsMap(expectedProperties, newProperties);
    }

    @Test
    public void ramlPropertiesV3WithDescription() throws IOException
    {
        PropertiesManager propertiesManager = new PropertiesManager();
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("api.id", "1");
        properties.put("api.name", "apiName");
        properties.put("api.version", "1.1.1");
        properties.put("api.description", "apiDescription");
        properties.put("proxy.uri", "http://0.0.0.0:8081/api");
        properties.put("console.uri", "http://0.0.0.0:8081/console");
        properties.put("implementation.uri","http://endpointUri.com");
        properties.put("raml.location","ramlLocation");
        boolean isOldProxy = propertiesManager.isOldTypeOfProxy(properties);
        assertFalse(isOldProxy);
        Map<String,String> newProperties = propertiesManager.modifyPropertiesOfNewTypeOfProxy(properties);
        Map<String,String> expectedProperties = new HashMap<>();
        expectedProperties.put("api.id","1");
        expectedProperties.put("api.name","apiName");
        expectedProperties.put("api.version","1.1.1");
        expectedProperties.put("api.description","apiDescription");
        expectedProperties.put("proxy.path","/api/*");
        expectedProperties.put("implementation.host","endpointUri.com");
        expectedProperties.put("implementation.port","80");
        expectedProperties.put("implementation.path","/");
        expectedProperties.put("console.path","/console/*");
        expectedProperties.put("raml.location","ramlLocation");
        assertEqualsMap(expectedProperties, newProperties);
    }

    @Test
    public void ramlPropertiesV3WithoutDescription() throws IOException
    {
        PropertiesManager propertiesManager = new PropertiesManager();
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("api.id", "1");
        properties.put("api.name", "apiName");
        properties.put("api.version", "1.1.1");
        properties.put("proxy.uri", "http://0.0.0.0:8081/api");
        properties.put("console.uri", "http://0.0.0.0:8081/console");
        properties.put("implementation.uri","http://endpointUri.com");
        properties.put("raml.location","ramlLocation");
        boolean isOldProxy = propertiesManager.isOldTypeOfProxy(properties);
        assertFalse(isOldProxy);
        Map<String,String> newProperties = propertiesManager.modifyPropertiesOfNewTypeOfProxy(properties);
        Map<String,String> expectedProperties = new HashMap<>();
        expectedProperties.put("api.id","1");
        expectedProperties.put("api.name","apiName");
        expectedProperties.put("api.version","1.1.1");
        expectedProperties.put("proxy.path","/api/*");
        expectedProperties.put("implementation.host","endpointUri.com");
        expectedProperties.put("implementation.port","80");
        expectedProperties.put("implementation.path","/");
        expectedProperties.put("console.path","/console/*");
        expectedProperties.put("raml.location","ramlLocation");
        assertEqualsMap(expectedProperties, newProperties);
    }

    @Test
    public void httpPropertiesV1() throws IOException
    {
        PropertiesManager propertiesManager = new PropertiesManager();
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("api.id", "1");
        properties.put("api.name", "apiName");
        properties.put("api.version", "1.1.1");
        properties.put("api.description", "apiDescription");
        properties.put("proxy.uri", "http://endpointUri.com");
        properties.put("http.port", "8081");
        boolean isOldProxy = propertiesManager.isOldTypeOfProxy(properties);
        assertTrue(isOldProxy);
        Map<String,String> newProperties = propertiesManager.modifyPropertiesOfOldTypeOfProxy(properties);
        Map<String,String> expectedProperties = new HashMap<>();
        expectedProperties.put("api.id","1");
        expectedProperties.put("api.name","apiName");
        expectedProperties.put("api.version","1.1.1");
        expectedProperties.put("api.description","apiDescription");
        expectedProperties.put("proxy.path","/*");
        expectedProperties.put("implementation.host","endpointUri.com");
        expectedProperties.put("implementation.port","80");
        expectedProperties.put("implementation.path","/");
        assertEqualsMap(expectedProperties, newProperties);
    }

    @Test
    public void httpPropertiesV1WithHttps() throws IOException
    {
        PropertiesManager propertiesManager = new PropertiesManager();
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("api.id", "1");
        properties.put("api.name", "apiName");
        properties.put("api.version", "1.1.1");
        properties.put("api.description", "apiDescription");
        properties.put("proxy.uri", "http://endpointUri.com");
        properties.put("http.port", "8081");
        boolean isOldProxy = propertiesManager.isOldTypeOfProxy(properties);
        assertTrue(isOldProxy);
        Map<String,String> newProperties = propertiesManager.modifyPropertiesOfOldTypeOfProxy(properties);
        Map<String,String> expectedProperties = new HashMap<>();
        expectedProperties.put("api.id","1");
        expectedProperties.put("api.name","apiName");
        expectedProperties.put("api.version","1.1.1");
        expectedProperties.put("api.description","apiDescription");
        expectedProperties.put("proxy.path","/*");
        expectedProperties.put("implementation.host","endpointUri.com");
        expectedProperties.put("implementation.port","80");
        expectedProperties.put("implementation.path","/");
        assertEqualsMap(expectedProperties, newProperties);
    }

    @Test
    public void httpPropertiesV2() throws IOException
    {
        PropertiesManager propertiesManager = new PropertiesManager();
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("api.id", "1");
        properties.put("api.name", "apiName");
        properties.put("api.version", "1.1.1");
        properties.put("api.description", "apiDescription");
        properties.put("proxy.uri", "http://0.0.0.0:8081/api");
        properties.put("implementation.uri","http://endpointUri.com");
        boolean isOldProxy = propertiesManager.isOldTypeOfProxy(properties);
        assertFalse(isOldProxy);
        Map<String,String> newProperties = propertiesManager.modifyPropertiesOfNewTypeOfProxy(properties);
        Map<String,String> expectedProperties = new HashMap<>();
        expectedProperties.put("api.id","1");
        expectedProperties.put("api.name","apiName");
        expectedProperties.put("api.version","1.1.1");
        expectedProperties.put("api.description","apiDescription");
        expectedProperties.put("proxy.path","/api/*");
        expectedProperties.put("implementation.host","endpointUri.com");
        expectedProperties.put("implementation.port","80");
        expectedProperties.put("implementation.path","/");
        assertEqualsMap(expectedProperties, newProperties);
    }

    @Test
    public void wsdlPropertiesV1() throws IOException
    {
        PropertiesManager propertiesManager = new PropertiesManager();
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("api.id", "1");
        properties.put("api.name", "apiName");
        properties.put("api.version", "1.1.1");
        properties.put("api.description", "apiDescription");
        properties.put("wsdl.uri", "endpointUri?wsdl");
        properties.put("http.port", "8081");
        boolean isOldProxy = propertiesManager.isOldTypeOfProxy(properties);
        assertTrue(isOldProxy);
        Map<String,String> newProperties = propertiesManager.modifyPropertiesOfOldTypeOfProxy(properties);
        Map<String,String> expectedProperties = new HashMap<>();
        expectedProperties.put("api.id","1");
        expectedProperties.put("api.name","apiName");
        expectedProperties.put("api.version","1.1.1");
        expectedProperties.put("api.description","apiDescription");
        expectedProperties.put("proxy.path","/*");
        expectedProperties.put("wsdl.uri", "endpointUri?wsdl");
        assertEqualsMap(expectedProperties, newProperties);
    }


    @Test
    public void wsdlPropertiesV2() throws IOException
    {
        PropertiesManager propertiesManager = new PropertiesManager();
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("api.id", "1");
        properties.put("api.name", "apiName");
        properties.put("api.version", "1.1.1");
        properties.put("api.description", "apiDescription");
        properties.put("proxy.uri", "http://0.0.0.0:8081/api");
        properties.put("wsdl.uri","http://endpointUri?wsdl");
        boolean isOldProxy = propertiesManager.isOldTypeOfProxy(properties);
        assertFalse(isOldProxy);
        Map<String,String> newProperties = propertiesManager.modifyPropertiesOfNewTypeOfProxy(properties);
        Map<String,String> expectedProperties = new HashMap<>();
        expectedProperties.put("api.id","1");
        expectedProperties.put("api.name","apiName");
        expectedProperties.put("api.version","1.1.1");
        expectedProperties.put("api.description","apiDescription");
        expectedProperties.put("proxy.path","/api/*");
        expectedProperties.put("wsdl.uri","http://endpointUri/?wsdl");
        assertEqualsMap(expectedProperties, newProperties);
    }


    private void assertEqualsMap(Map<String, String> expected, Map<String, String> actual) {
        assertEquals(expected.size(), actual.size());
        for(Map.Entry<String,String> value:expected.entrySet()){
            String actualValue = actual.get(value.getKey());
            assertNotNull(actualValue);
            assertEquals(value.getValue(), actualValue);
        }
    }


}
