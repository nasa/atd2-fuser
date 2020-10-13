package com.mosaicatm.fuser.rules;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.matm.actypelib.manager.AircraftTypeManager;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class AircraftTypeRule
extends MultiplePriorityMediationRule<MatmFlight>
{
	private final Log log = LogFactory.getLog(getClass());

    @Override
    public Class getRuleClassType()
    {
        return( MatmFlight.class );
    }
    
	@Override
	protected boolean handleDifferences(MatmFlight update, MatmFlight target, MetaData history, String field)
	{
	    boolean diff = super.handleDifferences(update, target, history, field);
	    
	    if (diff)
	    {
	        if (AircraftTypeManager.DEFAULT_AIRCRAFT_TYPE.equals(update.getAircraftType()))
	        {
	            if (log.isDebugEnabled()) 
	            {
	                log.debug("Filtering " + update.getGufi() + " aircraft type because update value is " + update.getAircraftType());
	            }
	        
	            filterUpdateField(update, field);
	            diff = false;
	        }  
	    }
	    
	    return diff;
	}
}
