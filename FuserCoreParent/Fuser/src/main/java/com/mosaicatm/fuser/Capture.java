package com.mosaicatm.fuser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Capture {

	private final Log log = LogFactory.getLog(getClass());
	
	public void capture (String message)
	{
		log.info (message);
	}
	
}
