package com.mosaicatm.fuser.common.camel;

import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FuserAmqConnectionFactory
{
    private final Log log = LogFactory.getLog(getClass());
    
    private Map<String, ActiveMQComponent> components;
    
    public FuserAmqConnectionFactory ()
    {
        this.components = new HashMap<>();
    }
    
    public ActiveMQComponent createComponents (FuserAmqProps props)
    {
        ActiveMQComponent component = null;
        
        if (props != null && props.getUrl() != null)
        {
            if (components.containsKey(props.getUrl()))
            {
                if (log.isDebugEnabled())
                    log.debug("Connection factory contains reference to " + props.getUrl());
                component = components.get(props.getUrl());
            }
            else
            {
                log.info("Creating new connection for " + props.getUrl());
                
                ActiveMQConnectionFactory amqConnectionFactory = new ActiveMQConnectionFactory();
                amqConnectionFactory.setBrokerURL( props.getUrl() );
                amqConnectionFactory.setUserName( props.getUser() );
                amqConnectionFactory.setPassword( props.getPassword() );
                amqConnectionFactory.setTrustAllPackages(true);
                
                PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
                pooledConnectionFactory.setMaxConnections( 8 );
                pooledConnectionFactory.setMaximumActiveSessionPerConnection( 500 );
                pooledConnectionFactory.setConnectionFactory( amqConnectionFactory );
                
                JmsConfiguration jmsConfig = new JmsConfiguration();
                jmsConfig.setConnectionFactory( pooledConnectionFactory );
                jmsConfig.setTransacted( false );
                jmsConfig.setConcurrentConsumers( 1 );
                jmsConfig.setDeliveryPersistent( false );
                
                component = new ActiveMQComponent();
                component.setConfiguration( jmsConfig );
                
                components.put(props.getUrl(), component);
            }
        }
        
        return component;
    }
}
