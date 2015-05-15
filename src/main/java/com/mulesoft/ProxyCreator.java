package com.mulesoft;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class ProxyCreator
{
    final static Logger logger = Logger.getLogger(ProxyCreator.class);

    private boolean hasDescription = true;
    private boolean isApiHttps = false;
    private boolean isProxyHttps = false;
    private boolean hasApikitRef = false;

    public void setHasDescription (boolean hasDescription)
    {
        this.hasDescription = hasDescription;
    }

    public void setApiHttps (boolean isApiHttps)
    {
        this.isApiHttps = isApiHttps;
    }

    public void setProxyHttps (boolean isProxyHttps)
    {
        this.isProxyHttps = isProxyHttps;
    }

    public void setHasApikitRef (boolean hasApikitRef)
    {
        this.hasApikitRef = hasApikitRef;
    }

    private static final String NEW_WSDL_PROXY_PATH = "/wsdl-proxy.xml";
    private static final String NEW_APIKIT_PROXY_PATH = "/apikit-proxy.xml";
    private static final String NEW_HTTP_PROXY_PATH = "/bare-http-proxy.xml";
    private File xmlFile;

    public ProxyCreator (File xmlFile, boolean hasApikitRef, boolean apiIsHttps, boolean proxyIsHttps, boolean containsDescription)
    {
//        PropertyConfigurator.configure("log4j.properties");
        this.xmlFile = xmlFile;
        setHasApikitRef(hasApikitRef);
        setApiHttps(apiIsHttps);
        setHasDescription(containsDescription);
        setProxyHttps(proxyIsHttps);
    }

    public void processTemplate(int proxyType) throws IOException
    {
        try {

            logger.debug("Adding properties to velocity context");
            org.apache.velocity.context.Context context = new VelocityContext();
            context.put("hasDescription", hasDescription);
            context.put("proxyHttps", isProxyHttps);
            context.put("apiHttps", isApiHttps);
            context.put("hasApikitRef", hasApikitRef);

            Properties props = new Properties();
            props.setProperty("resource.loader", "class");
            props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            Velocity.init(props);
            logger.debug("Properties created, velocity initialized");

            Template t = Velocity.getTemplate(getTemplate(proxyType),"UTF-8");
            FileWriter writer = new FileWriter(xmlFile.getPath());
            BufferedWriter buffWriter = new BufferedWriter(writer);
            t.merge(context,buffWriter);

            logger.debug("Template merged to properties");
            buffWriter.flush();
        }
        catch (  Exception e) {
            throw new IOException(e);
        }
    }

    private String getTemplate(int proxyType) throws FileNotFoundException
    {
        if (proxyType == ProxyType.BARE_HTTP_PROXY) {
            return NEW_HTTP_PROXY_PATH;
        }
        else if (proxyType == ProxyType.APIKIT_PROXY){
            return NEW_APIKIT_PROXY_PATH;
        }
        else if (proxyType == ProxyType.WSDL_PROXY){
            return NEW_WSDL_PROXY_PATH;
        }
        throw new FileNotFoundException("New version of the proxy could not be found.");
    }
}
