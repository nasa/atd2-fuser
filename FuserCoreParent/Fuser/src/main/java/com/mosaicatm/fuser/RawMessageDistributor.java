package com.mosaicatm.fuser;

import com.mosaicatm.lib.messaging.MessageProducer;
import com.mosaicatm.lib.messaging.MessageProducerOwner;

/**
 * Utility class for receiving a message
 * @param <T>
 */
public class RawMessageDistributor <T>
implements MessageProducerOwner
{
	private MessageProducer messageProducer;
	private String messageDestination;
	
	public void distribute (T message)
	{
		if (message != null)
		{
			if (messageProducer != null &&
				messageDestination != null)
			{
				messageProducer.publish(messageDestination, message);
			}
		}
	}
	
	@Override
	public void setMessageProducer(MessageProducer messageProducer) 
	{
		this.messageProducer = messageProducer;
	}
	
	public void setMessageDestination (String messageDestination)
	{
		this.messageDestination = messageDestination;
	}

}
