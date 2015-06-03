package com.mulesoft.domains;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TLSKeystoreInformation
{

    private final String path;
    private final String keyPassword;
    private final String storePassword;

    public TLSKeystoreInformation(final String path, final String keyPassword, final String storePassword)
    {
        this.path = path;
        this.keyPassword = keyPassword;
        this.storePassword = storePassword;
    }

    public String getPath()
    {
        return path;
    }

    public String getKeyPassword()
    {
        return keyPassword;
    }

    public String getStorePassword()
    {
        return storePassword;
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

        TLSKeystoreInformation that = (TLSKeystoreInformation) o;

        return new EqualsBuilder()
                .append(path, that.path)
                .append(keyPassword, that.keyPassword)
                .append(storePassword, that.storePassword)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(path)
                .append(keyPassword)
                .append(storePassword)
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return "{" +
               "path='" + path + '\'' +
               ", keyPassword='" + keyPassword + '\'' +
               ", storePassword='" + storePassword + '\'' +
               '}';
    }
}
