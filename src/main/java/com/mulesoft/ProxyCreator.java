package com.mulesoft;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
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
    private boolean hasDescription = true;// TODO SETTERS
    private boolean isApiHttps = false;
    private boolean isProxyHttps = false;



    public String processTemplate(int proxyType) throws IOException
    {
        try {
            VelocityContext context=new VelocityContext();
            context.put("hasDescription", hasDescription);
            context.put("proxyHttps", isProxyHttps);
            context.put("apiHttps", isApiHttps);
            Template t = Velocity.getTemplate(getTemplate(proxyType));
            StringWriter sw=new StringWriter();
            BufferedWriter bw=new BufferedWriter(sw);
            t.merge(context,bw);
            bw.flush();
            return sw.toString();
        }
        catch (  Exception e) {
            throw new IOException(e);
        }
    }

    private String getTemplate(int proxyType)
    {
        return null;//TODO GET TEMPLATE
    }
}
