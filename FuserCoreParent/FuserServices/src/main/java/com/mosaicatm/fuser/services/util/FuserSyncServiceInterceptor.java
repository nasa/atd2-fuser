package com.mosaicatm.fuser.services.util;

import java.net.ConnectException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

public class FuserSyncServiceInterceptor
extends AbstractPhaseInterceptor<Message>
{
	static final String NOTICE =
			"Copyright (c), 2011 by Mosaic ATM\nAll Rights Reserved " +
			"This file has been developed by Mosaic ATM under US federal " +
			"government funding.  The US federal government has a world-wide, " +
			"royalty-free and irrevocable license to use this file for any " +
			"government purpose.";
	
	private final Log logger = LogFactory.getLog(getClass());

	public FuserSyncServiceInterceptor()
	{
		super(Phase.SETUP);
	}

	@Override
	public void handleFault(Message message)
	{
        Fault fault = (Fault) message.getContent(Exception.class);
        if(fault != null)
        {
        	if(fault.getCause() != null && fault.getCause() instanceof ConnectException)
        		logger.warn("Cannot connect to the FuserSyncService, skipping initialization");		// TODO this would be a great place to communicate with a web client via JMX :)
        	else
                logger.error("Error communicating with the FuserSyncService", fault);
        }
	}

	@Override
	public void handleMessage(Message message) throws Fault
	{
		// nothing
	}
}