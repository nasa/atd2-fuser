package com.mosaicatm.fuser.client.api.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SyncPoint
{
	static final String NOTICE =
			"Copyright (c), 2011 by Mosaic ATM\nAll Rights Reserved " +
			"This file has been developed by Mosaic ATM under US federal " +
			"government funding.  The US federal government has a world-wide, " +
			"royalty-free and irrevocable license to use this file for any " +
			"government purpose.";
	
	private Log logger = LogFactory.getLog(getClass());
	
	private boolean locked;
	private Object lockObject;
	
	public SyncPoint()
	{
		this(true);
	}
	
	public SyncPoint(boolean locked) 
	{
		this.locked = locked;
		lockObject = new Object();
		if(locked)
		{
			if (logger.isDebugEnabled())
				logger.debug("SyncPoint is locked");
		}
	}
	
	public void lock()
	{
		synchronized(lockObject)
		{
			locked = true;
		}
		
		if (logger.isDebugEnabled())
			logger.debug("SyncPoint is locked");
	}
	
	public void unlock()
	{
		synchronized(lockObject)
		{
			locked = false;
			lockObject.notifyAll();
		}
		
		if (logger.isDebugEnabled())
			logger.debug("SyncPoint is unlocked");
	}
	
	public Object sync(Object object)
	{
		if(locked)
		{
			if (logger.isTraceEnabled())
				logger.trace("Locking("+Thread.currentThread().getId()+")...");
			
			synchronized(lockObject)
			{
				try { lockObject.wait(); }
				catch(InterruptedException e) { logger.error("Could not sync", e); }
			}
			
			if (logger.isTraceEnabled())
				logger.trace("...unlocked("+Thread.currentThread().getId()+")");
		}
		
		if (logger.isTraceEnabled())
			logger.trace("Returning Object");
		
		return object;
	}
}