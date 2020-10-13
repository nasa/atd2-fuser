package com.mosaicatm.fuser.client.api.impl.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class FuserClientMatmAircraftStore 
extends AbstractFuserClientStore <MatmAircraft>
{
	private final Log log = LogFactory.getLog(getClass());
	
	public FuserClientMatmAircraftStore ()
	{
		super();
	}
	
	@Override
	public String getKey (MatmAircraft aircraft)
	{
		if (aircraft != null)
			return aircraft.getRegistration();
		return null;
	}
}
