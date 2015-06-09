package com.mulesoft.domains;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomainsBuilderTest
{

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private String domainFile;
    private DomainsBuilder builder;
    private List<String> loggerList;

    @Before
    public void before() throws IOException, URISyntaxException
    {
        setDomainsFile("mule-domain-config.xml");

        builder = spy(new DomainsBuilder());

        final Logger logger = spy(Logger.getLogger(DomainsBuilder.class));

        loggerList = new ArrayList<>();
        doAnswer(new Answer<Object>()
        {
            public Object answer(InvocationOnMock invocation)
            {
                Object[] args = invocation.getArguments();
                loggerList.add((String) args[0]);
                return "called with arguments: " + Arrays.toString(args);
            }
        }).when(logger).warn(anyString());

        when(builder.getLogger()).thenReturn(logger);
    }

    private void setDomainsFile(String fileName) throws IOException, URISyntaxException
    {
        domainFile = tempFolder.getRoot().getAbsolutePath() + fileName;
        Files.copy(
                Paths.get(getClass().getResource("/domains/" + fileName).toURI()),
                Paths.get(domainFile));
    }

    @Test
    public void domains() throws IOException, URISyntaxException
    {
        builder.setDefaultDomainLocation(domainFile);
        builder.addProxy(false, "localhost", 8443);
        builder.addProxy(false, "localhost2", 8443);
        builder.addProxy(false, "localhost", 8081);
        builder.addProxy(true, "google.com", 8081);

        builder.build();

        final Domain domain = getDomain();
        assertEquals(domain.getKeystore(), new TLSKeystoreInformation("${mule.home}/conf/keystore.jks", "mule123", "mule123"));
        assertTrue(domain.containsListenerConfig(new ListenerConfigEntry(false, "localhost", 8443, "http-lc-localhost-8443")));
        assertTrue(domain.containsListenerConfig(new ListenerConfigEntry(false, "localhost2", 8443, "http-lc-localhost2-8443")));
        assertTrue(domain.containsListenerConfig(new ListenerConfigEntry(false, "localhost", 8081, "http-lc-localhost-8081")));
        assertTrue(domain.containsListenerConfig(new ListenerConfigEntry(true, "google.com", 8081, "https-lc-google.com-8081")));

        assertTrue(loggerList.contains("---Custom connector {HTaTP-shared-connector} must be migrated manually"));
    }

    @Test
    public void sessionHandler() throws IOException, URISyntaxException
    {
        setDomainsFile("mule-domain-config-sessionHandler.xml");
        builder.setDefaultDomainLocation(domainFile);
        builder.build();
        assertThat(loggerList.size(), is(1));
        assertThat(loggerList.get(0), is("----custom configuration found in core:service-overrides. It must be migrated manually"));
    }

    @Test
    public void extraElement() throws IOException, URISyntaxException
    {
        setDomainsFile("mule-domain-config-extraElement.xml");
        builder.setDefaultDomainLocation(domainFile);
        builder.build();
        assertThat(loggerList.size(), is(1));
        assertThat(loggerList.get(0), is("----custom element found {core:other}. It must be migrated manually"));
    }

    private Domain getDomain()
    {
        final Domain domain = new Domain();
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try
        {
            final Document dom = factory.newDocumentBuilder().parse(new File(domainFile));
            final Element rootElement = dom.getDocumentElement();

            final NodeList configList = rootElement.getElementsByTagName("http:listener-config");
            if (configList != null && configList.getLength() > 0)
            {
                for (int i = 0; i < configList.getLength(); i++)
                {
                    final Element element = (Element) configList.item(i);
                    final String protocol = element.getAttribute("protocol");

                    domain.addListenerConfig(new ListenerConfigEntry(
                            "HTTPS".equals(protocol),
                            element.getAttribute("host"),
                            Integer.parseInt(element.getAttribute("port")),
                            element.getAttribute("name")));
                }
            }

            final NodeList tlsList = rootElement.getElementsByTagName("tls:context");
            if (tlsList != null && tlsList.getLength() > 0)
            {
                final Element element = (Element) tlsList.item(0);
                final Element keyStore = (Element) element.getElementsByTagName("tls:key-store").item(0);

                domain.setKeystore(new TLSKeystoreInformation(keyStore.getAttribute("path"), keyStore.getAttribute("password"), keyStore.getAttribute("keyPassword")));
            }

        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            fail("Unable to parse domains file");
        }

        return domain;
    }

    private static class Domain
    {

        private List<ListenerConfigEntry> listenerConfigs = new ArrayList<>();
        private TLSKeystoreInformation keystore;

        public void addListenerConfig(ListenerConfigEntry entry)
        {
            this.listenerConfigs.add(entry);
        }

        public void setKeystore(TLSKeystoreInformation keystore)
        {
            this.keystore = keystore;
        }

        public TLSKeystoreInformation getKeystore()
        {
            return keystore;
        }

        public boolean containsListenerConfig(ListenerConfigEntry entry)
        {
            return listenerConfigs.contains(entry);
        }
    }
}
