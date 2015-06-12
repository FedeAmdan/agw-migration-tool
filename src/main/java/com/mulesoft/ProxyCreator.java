package com.mulesoft;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class ProxyCreator
{

    private static final Logger logger = Logger.getLogger(ProxyCreator.class);

    private final String listenerConfigName;
    private boolean hasDescription = true;
    private boolean isApiHttps = false;
    private boolean isProxyHttps = false;

    public void setHasDescription(boolean hasDescription)
    {
        this.hasDescription = hasDescription;
    }

    public void setApiHttps(boolean isApiHttps)
    {
        this.isApiHttps = isApiHttps;
    }

    public void setProxyHttps(boolean isProxyHttps)
    {
        this.isProxyHttps = isProxyHttps;
    }

    private File xmlFile;

    public ProxyCreator(File xmlFile, boolean apiIsHttps, boolean proxyIsHttps, boolean containsDescription, String listenerConfigName)
    {
        //        PropertyConfigurator.configure("log4j.properties");
        this.xmlFile = xmlFile;
        setApiHttps(apiIsHttps);
        setHasDescription(containsDescription);
        setProxyHttps(proxyIsHttps);
        this.listenerConfigName = listenerConfigName;
    }

    public void processTemplate(final ProxyType proxyType) throws IOException
    {
        try
        {

            logger.debug("Adding properties to velocity context");
            org.apache.velocity.context.Context context = new VelocityContext();
            context.put("listenerConfigName", listenerConfigName);
            context.put("hasDescription", hasDescription);
            context.put("proxyHttps", isProxyHttps);
            context.put("apiHttps", isApiHttps);

            Properties props = new Properties();
            props.setProperty("resource.loader", "class");
            props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            Velocity.init(props);
            logger.debug("Properties created, velocity initialized");

            Template t = Velocity.getTemplate(proxyType.getTemplateName(), "UTF-8");
            FileWriter writer = new FileWriter(xmlFile.getPath());
            BufferedWriter buffWriter = new BufferedWriter(writer);
            t.merge(context, buffWriter);

            logger.debug("Template merged to properties");
            buffWriter.flush();
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
    }
}
