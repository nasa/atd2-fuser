package com.mosaicatm.fuser.util;

import java.util.List;
import java.util.Map;

import org.apache.camel.Headers;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.messaging.MessageProducer;
import com.mosaicatm.lib.messaging.MessageProducerOwner;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;

public class EnvelopeMessageSender
implements MessageProducerOwner
{

    private final Log log = LogFactory.getLog(getClass());
    
    private MessageProducer producer;
    private String destination;
    private boolean active;
    
    public void publishFlights(MatmTransferEnvelope message, @Headers Map<String, Object> headers)
        throws Exception
    {
        if (!active && message !=null && message.getFlights() != null && !message.getFlights().isEmpty())
            return;
        
        publishList(message.getFlights(), headers);
    }
    
    public void publishAircraft(MatmTransferEnvelope message, @Headers Map<String, Object> headers)
        throws Exception
    {
        if (!active && message !=null && message.getAircraft() != null && !message.getAircraft().isEmpty())
            return;
        
        publishList(message.getAircraft(), headers);
    }
    
    private <T extends MatmObject> void publishList(List<T> message, Map<String, Object> headers)
    {
        if (message == null || message.isEmpty())
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
        
        for (T matm : message)
        {
            producer.publish(destination, matm, headers);
        }
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
