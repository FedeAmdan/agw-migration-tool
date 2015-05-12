package com.mulesoft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

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

    private static final String NEW_WSDL_PROXY_PATH = "/wsdl-proxy/wsdl-proxy.xml";
    private static final String NEW_APIKIT_PROXY_PATH = "/apikit-proxy/apikit-proxy.xml";
    private static final String NEW_HTTP_PROXY_PATH = "/bare-http-proxy/bare-http-proxy.xml";
    private String appPath;

    public ProxyCreator (String appPath, boolean hasDescription, boolean isProxyHttps, boolean isApiHttps, boolean hasApikitRef)
    {
        this.appPath = appPath;
        setHasApikitRef(hasApikitRef);
        setApiHttps(isApiHttps);
        setHasDescription(hasDescription);
        setProxyHttps(isProxyHttps);
    }

    public ProxyCreator (String appPath, FileContentAnalizer contentAnalizer)
    {
        this.appPath = appPath;
        setHasApikitRef(contentAnalizer.hasApikitRef());
        setApiHttps(contentAnalizer.apiIsHttps());
        setHasDescription(contentAnalizer.containsDescription());
        setProxyHttps(contentAnalizer.proxyIsHttps());
    }

    public void processTemplate(int proxyType) throws IOException
    {
        try {

            VelocityContext context = new VelocityContext();
            context.put("hasDescription", hasDescription);
            context.put("proxyHttps", isProxyHttps);
            context.put("apiHttps", isApiHttps);
            //TODO APIKITREF

            Properties props = new Properties();
            props.put("file.resource.loader.path", "/Users/federicoamdan/Projects/agw-migration-tool/src/main/resources/proxies"); //TODO REMOVE HARDCODED URI
            Velocity.init(props);
            Template t = Velocity.getTemplate(getTemplate(proxyType),"UTF-8");
            //StringWriter sw = new StringWriter();
            //BufferedWriter bw = new BufferedWriter(sw);
            FileWriter writer = new FileWriter(appPath + PropertiesManager.PROPERTIES_RELATIVE_PATH);
            BufferedWriter buffWriter = new BufferedWriter(writer);
            //buffWriter.write(newFileContent);
            //buffWriter.close();
            t.merge(context,buffWriter);
            buffWriter.flush();
        }
        catch (  Exception e) {
            throw new IOException(e);
        }
    }

    private String getTemplate(int proxyType) throws FileNotFoundException
    {
        String newXmlLines = null;
        if (proxyType == ProxyType.BARE_HTTP_PROXY) {
            newXmlLines = readFileFromClassPath(NEW_HTTP_PROXY_PATH);
        }
        else if (proxyType == ProxyType.APIKIT_PROXY){
            newXmlLines = readFileFromClassPath(NEW_APIKIT_PROXY_PATH);
        }
        else if (proxyType == ProxyType.WSDL_PROXY){
            newXmlLines = readFileFromClassPath(NEW_WSDL_PROXY_PATH);
        }
        if (newXmlLines == null)
        {
            throw new FileNotFoundException("New version of the proxy could not be found.");
        }
        return newXmlLines;
    }

    public String readFileFromClassPath(String file){
        String everything = "";

        ClassLoader classLoader = getClass().getClassLoader();
        InputStreamReader stream = new InputStreamReader(classLoader.getResourceAsStream(file));
        try(BufferedReader br = new BufferedReader(stream)) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return everything;
    }
}
