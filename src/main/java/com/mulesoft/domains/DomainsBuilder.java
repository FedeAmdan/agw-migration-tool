package com.mulesoft.domains;

import com.mulesoft.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.Closure;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomainsBuilder
{

    private static final Logger LOGGER = Logger.getLogger(Main.class);
    private static final String HTTPS_SHARED_CONNECTOR_NAME = "HTTPS-shared-connector";
    private static final String HTTP_SHARED_CONNECTOR_NAME = "HTTP-shared-connector";

    private final Set<ListenerConfigEntry> configEntries = new HashSet<>();
    private TLSKeystoreInformation tlsInformation;
    private String defaultDomainFile;

    public DomainsBuilder()
    {
    }

    /**
     * Each proxy added to the set will be transformed to a listener-config entry in the default domains file.
     */
    public ListenerConfigEntry addProxy(boolean https, String host, int port)
    {
        final ListenerConfigEntry configEntry = new ListenerConfigEntry(https, host, port);
        configEntries.add(configEntry);
        return configEntry;
    }

    public void build()
    {
        LOGGER.debug("Building new domain configuration...");

        analyzeCurrentDomain();

        final Context context = new VelocityContext();
        context.put("tlsInformation", tlsInformation);
        context.put("configEntries", configEntries);

        final Properties properties = new Properties();
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(properties);

        final Template template = Velocity.getTemplate("/mule-domain-config.xml", "UTF-8");
        final FileWriter writer;
        try
        {
            Files.move(Paths.get(defaultDomainFile), Paths.get(defaultDomainFile + ".bkp"));
            writer = new FileWriter(defaultDomainFile);
            final BufferedWriter buffWriter = new BufferedWriter(writer);
            template.merge(context, buffWriter);

            buffWriter.flush();

            LOGGER.info("Domain configuration successfully updated");
        }
        catch (IOException e)
        {
            LOGGER.error("Unable to create domain configuration file", e);
        }

    }

    private void analyzeCurrentDomain() {
        LOGGER.info("--analyzing domain configuration in: " + defaultDomainFile);
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try
        {
            final Document dom = factory.newDocumentBuilder().parse(new File(this.defaultDomainFile));
            final Element rootElement = dom.getDocumentElement();

            findConnectors(rootElement, "https:connector", HTTPS_SHARED_CONNECTOR_NAME, new Closure()
            {
                @Override
                public void execute(Object o)
                {
                    final Element element = (Element) o;
                    final Element tlsKeyStore = (Element) element.getElementsByTagName("https:tls-key-store").item(0);
                    setTlsInformation(new TLSKeystoreInformation(tlsKeyStore.getAttribute("path"), tlsKeyStore.getAttribute("keyPassword"), tlsKeyStore.getAttribute("storePassword")));
                    LOGGER.info("--default HTTPS Shared Connector found: " + tlsInformation);
                }
            });

            findConnectors(rootElement, "http:connector", HTTP_SHARED_CONNECTOR_NAME, new Closure()
            {
                @Override
                public void execute(Object o)
                {
                    LOGGER.info("--default HTTP Shared Connector found");
                }
            });

        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            LOGGER.warn("--could not read domains configuration file", e);
        }

    }

    /**
     * Adds the default domain to this builder. Parses the file and store default HTTP shared connector and tls-key-store
     */
    public void setDefaultDomainLocation(final String defaultDomainFile)
    {
        this.defaultDomainFile = defaultDomainFile;
    }

    public void setTlsInformation(TLSKeystoreInformation tlsInformation)
    {
        this.tlsInformation = tlsInformation;
    }

    private void findConnectors(Element rootElement, String tagName, String connectorName, Closure closure)
    {
        final NodeList nodeList = rootElement.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0)
        {
            for (int i = 0; i < nodeList.getLength(); i++)
            {
                final Element element = (Element) nodeList.item(i);
                final String elementName = element.getAttribute("name");

                if (connectorName.equals(elementName))
                {
                    closure.execute(element);
                }
                else
                {
                    LOGGER.warn(String.format("--there is a custom connector {%s} that cannot be migrated automatically", elementName));
                }
            }
        }
    }

}
