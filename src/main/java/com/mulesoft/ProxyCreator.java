package com.mulesoft;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class ProxyCreator
{
    //VelocityEngine ve = new VelocityEngine();
    //ve.init();
    ///*  next, get the Template  */
    //Template t = ve.getTemplate( "helloworld.vm" );
    ///*  create a context and add data */
    //VelocityContext context = new VelocityContext();
    //context.put("name", "World");
    ///* now render the template into a StringWriter */
    //StringWriter writer = new StringWriter();
    //t.merge( context, writer );
    //    /* show the World */
    //System.out.println( writer.toString() );

    //Properties properties = new Properties();
    //properties.load( getClass().getClassLoader().getResourceAsStream( "velocity.properties" ) );
    //
    //// Create and initialize the template engine
    //velocityEngine = new VelocityEngine( properties );
    //public void test (){
    //VelocityContext ctx = new VelocityContext();
    //ctx.put("parseCheck",new TemplateChecker());
    //for (  Map.Entry<Object,Object> entry : props.entrySet())
    //{
    //    ctx.put(entry.getKey().toString(), entry.getValue());
    //}

    //final StringWriter stringWriter=new StringWriter();
    //Template template = Velocity.getTemplate(templatePath);
    //template.merge(ctx,stringWriter);
    //return stringWriter.toString();
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

    public void setHasApikitRef(boolean hasApikitRef)
    {
        this.hasApikitRef = hasApikitRef;
    }

    private static final String NEW_WSDL_PROXY_PATH = "/wsdl-proxy.xml";
    private static final String NEW_APIKIT_PROXY_PATH = "/apikit-proxy.xml";
    private static final String NEW_HTTP_PROXY_PATH = "/bare-http-proxy.xml";
    private String appPath;
    private File xmlFile;
    //
    //public ProxyCreator (String appPath, boolean hasDescription, boolean isProxyHttps, boolean isApiHttps, boolean hasApikitRef)
    //{
    //    this.appPath = appPath;
    //    setHasApikitRef(hasApikitRef);
    //    setApiHttps(isApiHttps);
    //    setHasDescription(hasDescription);
    //    setProxyHttps(isProxyHttps);
    //}

    public ProxyCreator (FileContentAnalizer contentAnalizer)
    {
        xmlFile = contentAnalizer.getXmlFile();
        setHasApikitRef(contentAnalizer.hasApikitRef());
        setApiHttps(contentAnalizer.apiIsHttps());
        setHasDescription(contentAnalizer.containsDescription());
        setProxyHttps(contentAnalizer.proxyIsHttps());
    }

    public void processTemplate(int proxyType) throws IOException
    {
        try {

            System.out.println("Adding properties to velocity context");
            org.apache.velocity.context.Context context = new VelocityContext();
            context.put("hasDescription", hasDescription);
            context.put("proxyHttps", isProxyHttps);
            context.put("apiHttps", isApiHttps);
            context.put("hasApikitRef", hasApikitRef);

            Properties props = new Properties();
            props.put("file.resource.loader.path", "/Users/federicoamdan/Projects/agw-migration-tool/src/main/resources"); //TODO REMOVE HARDCODED URI
            Velocity.init(props);
            //Velocity.init();
            System.out.println("Properties created, velocity initialized");

            Template t = Velocity.getTemplate(getTemplate(proxyType),"UTF-8");
            FileWriter writer = new FileWriter(xmlFile.getPath());
            BufferedWriter buffWriter = new BufferedWriter(writer);
            t.merge(context,buffWriter);

            System.out.println("Template merged to properties");
            buffWriter.flush();
        }
        catch (  Exception e) {
            throw new IOException(e);
        }
    }

    private String getTemplate(int proxyType) throws FileNotFoundException
    {
        if (proxyType == ProxyType.BARE_HTTP_PROXY) {
            return NEW_HTTP_PROXY_PATH;//readFileFromClassPath(NEW_HTTP_PROXY_PATH);
        }
        else if (proxyType == ProxyType.APIKIT_PROXY){
            return NEW_APIKIT_PROXY_PATH;//readFileFromClassPath(NEW_APIKIT_PROXY_PATH);
        }
        else if (proxyType == ProxyType.WSDL_PROXY){
            return NEW_WSDL_PROXY_PATH;//readFileFromClassPath(NEW_WSDL_PROXY_PATH);
        }
        throw new FileNotFoundException("New version of the proxy could not be found.");
    }
}
