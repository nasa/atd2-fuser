package com.mosaicatm.fuser.transform.util;

import java.util.Date;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DurationConverter
{
	public static DatatypeFactory dataFactory;
	
	private final Log log = LogFactory.getLog(getClass());
	
	public DurationConverter()
	{
		try
		{
			dataFactory = DatatypeFactory.newInstance();
		}
		catch (Exception e)
		{
			log.error("Fail to initialize DurationConverter ", e);
		}
	}
	
	public Duration convert(long milliseconds)
	{
		if (dataFactory != null)
		{
			return dataFactory.newDuration(milliseconds);
		}
		
		return null;
	}
	
	public Long fromDuration(Duration duration)
	{
		if (duration != null)
		{
			return duration.getTimeInMillis(new Date(0));
		}
		
		return null;
	}
}
