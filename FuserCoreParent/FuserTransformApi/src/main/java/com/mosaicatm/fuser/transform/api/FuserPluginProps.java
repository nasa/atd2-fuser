package com.mosaicatm.fuser.transform.api;

import java.util.Properties;

import com.mosaicatm.fuser.common.camel.FuserAmqConnectionFactory;

public class FuserPluginProps
{
    private Properties properties;
    private FuserAmqConnectionFactory connectionFactory;
    
    public void setProperties (Properties properties)
    {
        this.properties = properties;
    }
    
    public Properties getProperties ()
    {
        return properties;
    }
    
    public void setConnectionFactory(FuserAmqConnectionFactory connectionFactory)
    {
        this.connectionFactory = connectionFactory;
    }
    
    public FuserAmqConnectionFactory getConnectionFactory()
    {
        return connectionFactory;
    }
}
