package com.mosaicatm.fuser.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.lib.playback.util.DataSignaler;

/**
 * This class will tell the dataSignaler that we are done processing the message.
 */
public class SignalingMessageSender<T> extends MessageSender<T>
{
    private final static Logger logger = LoggerFactory.getLogger( SignalingMessageSender.class );
    
    private DataSignaler dataSignaler;
    
    @Override
    protected void publishMessage( Object message, Map<String, Object> headers )
    {
        // If the message is null then tell the dataSignaler we're done with it
        if( message == null )
        {
            if( dataSignaler != null )
            {
                dataSignaler.messageProcessed( headers );
            }
            return;
        }
        
        super.publishMessage( message, headers );
    }

    public void setDataSignaler( DataSignaler dataSignaler )
    {
        this.dataSignaler = dataSignaler;
    }
}
