package com.mosaicatm.fuser.client.api.impl.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class FuserClientMatmFlightStore 
extends AbstractFuserClientStore <MatmFlight>
{
	private final Log log = LogFactory.getLog(getClass());
	
	public FuserClientMatmFlightStore ()
	{
		super();
	}
	
	@Override
	public String getKey (MatmFlight flight)
	{
		if (flight != null)
			return flight.getGufi();
		return null;
	}
}
