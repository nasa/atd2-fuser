package com.mosaicatm.fuser.client.api.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RouteErrorHandler 
implements Processor
{
	public static final String NOTICE = 
		"Copyright (c) 2012 by Mosaic ATM, Inc.  All rights reserved."
		+ "This file has been developed by Mosaic ATM under US federal "
		+ "government funding via FAA contract DTFAWA-08-C-00076. The US "
		+ "federal government has a world-wide, royalty-free and irrevocable "
		+ "license to use this file for any government purpose.";

	private final Log logger = LogFactory.getLog(getClass());

	@Override
	public void process(Exchange exchange) throws Exception
	{
		Exception e = exchange.getException();
		
		if(e != null)
		{
			logger.error("Error in camel", e);
		}
		else
		{
			//prior to camel to 2.3
			Throwable caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
		
			if(caused != null)
				logger.error("Error in camel", caused);
		
		}
	}
}