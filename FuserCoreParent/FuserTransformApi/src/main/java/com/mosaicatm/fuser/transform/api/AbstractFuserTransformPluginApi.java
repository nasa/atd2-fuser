package com.mosaicatm.fuser.transform.api;

import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.camel.FuserAmqConnectionFactory;
import com.mosaicatm.fuser.common.camel.FuserAmqProps;

public abstract class AbstractFuserTransformPluginApi<E,F>
implements FuserTransformPluginApi<E,F>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private Properties pluginProps;
    private FuserAmqConnectionFactory connectionFactory;
    
    private String defaultJmsUrl = null;
    private String defaultJmsUser = null;
    private String defaultJmsPassword = null;
    
    public abstract FuserAmqProps getFuserAmqProps();
    
    @Override
    public void initialize(FuserPluginProps props)
    {        
        if (props != null)
        {
            this.pluginProps = props.getProperties();
            this.connectionFactory = props.getConnectionFactory();
            
            defaultJmsUrl = getProperty("fuser.jms.url");
            defaultJmsUser = getProperty("fuser.jms.user");
            defaultJmsPassword = getProperty("fuser.jms.password");
        }
    }
    
    @Override
    public void addCamelComponent(CamelContext camelContext)
    {
        String componentName = getComponentName();
        
        if (componentName != null && !componentName.trim().isEmpty())
        {            
            if( camelContext.hasComponent( componentName ) == null )
            {                
                ActiveMQComponent matmJms = getConnectionComponent(getFuserAmqProps());
                
                if (matmJms != null)
                {
                    camelContext.addComponent( componentName, matmJms );
                }
            }
        }
    }
    
    protected ActiveMQComponent getConnectionComponent(FuserAmqProps props)
    {
        ActiveMQComponent component = null;
        
        if (connectionFactory != null && props != null)
        {
            component = connectionFactory.createComponents(props);
        }
        
        return component;
    }
    
    protected String getProperty(String prop)
    {
        return getProperty(prop, null);
    }
    
    protected String getProperty(String prop, String defaultValue)
    {
        String rtnProp = defaultValue;
        
        if (pluginProps != null && prop != null && !prop.trim().isEmpty())
        {
            String property = pluginProps.getProperty(prop);
            
            if (isValidProperty(property))
            {
                rtnProp = property;
            }
        }
        
        return rtnProp;
    }
    
    protected boolean isValidProperty( String property )
    {
        return property != null && !property.isEmpty();
    }
    
    public String getDefaultUrl()
    {
        return defaultJmsUrl;
    }
    
    public String getDefaultUser()
    {
        return defaultJmsUser;
    }
    
    public String getDefaultPassword()
    {
        return defaultJmsPassword;
    }
}
