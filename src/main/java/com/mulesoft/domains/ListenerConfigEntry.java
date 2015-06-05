package com.mulesoft.domains;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ListenerConfigEntry
{

    private final int port;
    private final String host;
    private final boolean https;
    private final String name;

    public ListenerConfigEntry(boolean https, String host, int port)
    {
        this(https, host, port, (https ? "https-lc-" : "http-lc-") + host + "-" + port);
    }

    public ListenerConfigEntry(boolean https, String host, int port, String name)
    {
        this.https = https;
        this.host = host;
        this.port = port;
        this.name = name;
    }

    public int getPort()
    {
        return port;
    }

    public String getHost()
    {
        return host;
    }

    public boolean isHttps()
    {
        return https;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        ListenerConfigEntry that = (ListenerConfigEntry) o;

        return new EqualsBuilder()
                .append(port, that.port)
                .append(https, that.https)
                .append(host, that.host)
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(port)
                .append(host)
                .append(https)
                .append(name)
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return "{" +
               "port=" + port +
               ", host='" + host + '\'' +
               ", https=" + https +
               '}';
    }
}

