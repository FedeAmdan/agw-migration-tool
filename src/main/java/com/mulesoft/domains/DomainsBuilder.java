package com.mulesoft.domains;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomainsBuilder
{

    private static final Logger LOGGER = Logger.getLogger(DomainsBuilder.class);
    private static final String HTTPS_SHARED_CONNECTOR_NAME = "HTTPS-shared-connector";
    private static final String HTTP_SHARED_CONNECTOR_NAME = "HTTP-shared-connector";

    private final Set<ListenerConfigEntry> configEntries = new HashSet<>();
    private TLSKeystoreInformation tlsInformation;
    private String defaultDomainFile;
    private String targetDomainLocation;

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
        getLogger().info("Building new domain configuration...");

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
            //Files.copy(Paths.get(defaultDomainFile), Paths.get(targetDomainLocation + ".bkp"));
            writer = new FileWriter(targetDomainLocation);
            final BufferedWriter buffWriter = new BufferedWriter(writer);
            template.merge(context, buffWriter);

            buffWriter.flush();

            getLogger().info("");
            getLogger().info("Domain configuration successfully updated");
        }
        catch (IOException e)
        {
            getLogger().error("Unable to create domain configuration file", e);
        }

    }

    protected Logger getLogger()
    {
        return LOGGER;
    }

    private void analyzeCurrentDomain()
    {
        getLogger().info("--Analyzing domain configuration in: " + defaultDomainFile);
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try
        {
            final Document dom = factory.newDocumentBuilder().parse(new File(this.defaultDomainFile));
            final Element rootElement = dom.getDocumentElement();

            // fetches HTTPS connectors
            onConnectorElementDo(rootElement, "https:connector", HTTPS_SHARED_CONNECTOR_NAME, new NodeElementClosure()
            {
                @Override
                public void execute(Element childNode)
                {
                    if ("https:tls-key-store".equals(childNode.getTagName()))
                    {
                        setTlsInformation(new TLSKeystoreInformation(childNode.getAttribute("path"), childNode.getAttribute("keyPassword"), childNode.getAttribute("storePassword")));
                        getLogger().info("----tls information found: " + tlsInformation);
                    }
                    else
                    {
                        validateDefaultConfiguration(childNode);
                    }
                }
            });

            // fetches HTTP connectors
            onConnectorElementDo(rootElement, "http:connector", HTTP_SHARED_CONNECTOR_NAME, new NodeElementClosure()
            {
                @Override
                public void execute(Element element)
                {
                    validateDefaultConfiguration(element);
                }
            });

        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            getLogger().warn("--could not read domains configuration file", e);
        }

    }

    private void validateDefaultConfiguration(Element childNode)
    {
        if ("core:service-overrides".equals(childNode.getTagName()))
        {
            if (childNode.getChildNodes().getLength() > 0 ||
                childNode.getAttributes().getLength() > 1 ||
                !childNode.getAttribute("sessionHandler").equals("org.mule.session.NullSessionHandler"))
            {
                getLogger().warn("----custom configuration found in core:service-overrides. It must be migrated manually");
            }
        }
        else
        {
            getLogger().warn(String.format("----custom element found {%s}. It must be migrated manually", childNode.getTagName()));
        }
    }

    public void setDefaultDomainFile(final String defaultDomainFile)
    {
        this.defaultDomainFile = defaultDomainFile;
    }

    public void setTlsInformation(TLSKeystoreInformation tlsInformation)
    {
        this.tlsInformation = tlsInformation;
    }

    private void onConnectorElementDo(Element rootElement, String tagName, String connectorName, NodeElementClosure closure)
    {
        // Search the connector node
        final NodeList nodeList = rootElement.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0)
        {
            for (int i = 0; i < nodeList.getLength(); i++)
            {
                getLogger().info("");
                final Element element = (Element) nodeList.item(i);
                final String elementName = element.getAttribute("name");

                // Look for default connector
                if (connectorName.equals(elementName))
                {
                    getLogger().info(String.format("---Migrating default connector {%s}", connectorName));
                    final NodeList childNodes = element.getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++)
                    {
                        if (childNodes.item(j).getNodeType() != Node.ELEMENT_NODE)
                        {
                            continue;
                        }

                        // execute on child elements
                        closure.execute((Element) childNodes.item(j));
                    }
                }
                else
                {
                    getLogger().warn(String.format("---Custom connector {%s} must be migrated manually", elementName));
                }
            }
        }
    }

    public void setTargetDomainLocation(String targetDomainLocation)
    {
        this.targetDomainLocation = targetDomainLocation;
    }

    public String getTargetDomainLocation()
    {
        return targetDomainLocation;
    }

    private interface NodeElementClosure
    {

        void execute(Element element);
    }
}
