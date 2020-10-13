package com.mosaicatm.fuser.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class GufiFilter 
implements MatmFilter<MatmFlight>
{
	private final Log log = LogFactory.getLog(getClass());
	
	private static final String IGNORE_GUFI = "IGNORE";

	private boolean active = false;
	
	private boolean filterNullGufi = true;
	
	@Override
	public MatmFlight filter (MatmFlight flight) 
	{
		if (!isActive() || flight == null)
		{
			return flight;
		}
		
		if (filterNullGufi && flight.getGufi() == null)
		{
			if (log.isDebugEnabled())
			{
				log.debug("Filtering flight with null GUFI: " + flight.getAcid()
							+ " Source: " + flight.getLastUpdateSource());
			}
			
			return null;
		}
		
		if(IGNORE_GUFI.equalsIgnoreCase(flight.getGufi()))
		{
			if(log.isDebugEnabled())
			{
				log.debug("Filtering flight with GUFI value '" + IGNORE_GUFI + "' : " + (flight.getAcid()));
			}
				
			return null;
		}

		return flight;
	}

	@Override
	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public boolean isFilterNullGufi()
	{
		return filterNullGufi;
	}

	public void setFilterNullGufi(boolean filterNullGufi)
	{
		this.filterNullGufi = filterNullGufi;
	}

}
