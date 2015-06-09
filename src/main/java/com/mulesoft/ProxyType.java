package com.mulesoft;

public enum ProxyType
{
    BARE_HTTP_PROXY
            {
                @Override
                public String getTemplateName()
                {
                    return "/bare-http-proxy.xml";

                }
            }, APIKIT_PROXY
        {
            @Override
            public String getTemplateName()
            {
                return "/apikit-proxy.xml";
            }
        }, WSDL_PROXY
        {
            @Override
            public String getTemplateName()
            {
                return "/wsdl-proxy.xml";
            }
        }, INVALID
        {
            @Override
            public String getTemplateName()
            {
                throw new IllegalArgumentException("New version of the proxy could not be found.");
            }
        };

    public abstract String getTemplateName();
}
