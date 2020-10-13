package com.mosaicatm.fuser.util;

import java.util.List;
import java.util.Map;

import org.apache.camel.Headers;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.lib.messaging.MessageProducer;
import com.mosaicatm.lib.messaging.MessageProducerOwner;

public class MessageSender<T>
implements MessageProducerOwner
{

    private final Log log = LogFactory.getLog(getClass());
    
    private GenericMarshaller marshaller;
    private MessageProducer producer;
    private String destination;
    private boolean active;
    
    public void publish(T message, @Headers Map<String,Object> headers) throws Exception
    {
        if (!active)
            return;
        
        if (marshaller == null)
        {
            publishMessage(message, headers);
        }
        else
        {    
            publishMessage(toXml(message), headers);
        }
    }
    
    public void publishList(List<T> messageList, @Headers Map<String,Object> headers) throws Exception
    {
        if (!active || messageList == null || messageList.isEmpty())
        {
            return;
        }
        
        for(T message : messageList)
        {
            publish(message, headers);
        }
    }
    
    public void publish(T message) throws Exception
    {
        publish(message, null);
    } 
    
    public void publishList(List<T> message) throws Exception
    {
        publishList(message, null);
    }    
    
    protected void publishMessage (Object message, Map<String,Object> headers)
    {
        if (message == null)
            return;
        
        if (destination == null || destination.trim().isEmpty())
        {
            log.warn("Failed to publish message, undefined destination");
            return;
        }
        
        if (producer == null)
        {
            log.error("Failed publish message, undefined producer");
            return;
        }
        
        producer.publish(destination, message, headers);
    }
    
    private String toXml (T message)
    {
        String xml = null;
        
        if (message != null)
        {
            try
            {
                if (marshaller != null)
                    xml = marshaller.marshall(message);
            }
            catch (Exception e)
            {
                log.error("Error marshalling message " + message, e);
            }
        }
        
        return xml;
    }
    
    public void setMarshaller(GenericMarshaller marshaller)
    {
        this.marshaller = marshaller;
    }
        
    public void setDestination(String destination)
    {
        this.destination = destination;
    }
    
    @Override
    public void setMessageProducer(MessageProducer producer)
    {
        this.producer = producer;
    }
        
    public void setActive(boolean active)
    {
        this.active = active;
    }
}
