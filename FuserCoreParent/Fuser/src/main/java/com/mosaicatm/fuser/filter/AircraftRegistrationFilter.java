package com.mosaicatm.fuser.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class AircraftRegistrationFilter 
implements MatmFilter<MatmAircraft>
{
	private final Log log = LogFactory.getLog(getClass());

	private boolean active = false;
	
	private boolean filterNullRegistration = true;
	
	@Override
	public MatmAircraft filter (MatmAircraft aircraft) 
	{
		if (!isActive() || aircraft == null)
		{
			return aircraft;
		}
		
		if (filterNullRegistration && aircraft.getRegistration() == null)
		{
			if (log.isDebugEnabled())
			{
				log.debug("Filtering aircraft with null registration.  " +
				            "Source: " + aircraft.getLastUpdateSource());
			}
			
			return null;
		}

		return aircraft;
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

	public boolean isFilterNullRegistration()
	{
		return filterNullRegistration;
	}

	public void setFilterNullRegistration(boolean filterNullRegistration)
	{
		this.filterNullRegistration = filterNullRegistration;
	}

}
