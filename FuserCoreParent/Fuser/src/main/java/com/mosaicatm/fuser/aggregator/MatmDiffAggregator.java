package com.mosaicatm.fuser.aggregator;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmDiffAggregator 
extends MatmAggregator<MatmFlight>
{
    private final Log log = LogFactory.getLog( getClass() );

    public MatmDiffAggregator()
    {
        super();
    }
    
    @Override
    public MatmFlight createComponent()
    {
        return new MatmFlight();
    }

    @Override
    public void copyComponentFromRightToLeft(MatmFlight left, MatmFlight right)
	{
		left.setAcid(right.getAcid());
		left.setGufi(right.getGufi());
		left.setArrivalAerodrome(right.getArrivalAerodrome());
		left.setDepartureAerodrome(right.getDepartureAerodrome());
		left.setSurfaceAirport(right.getSurfaceAirport());
		left.setLastUpdateSource(right.getLastUpdateSource());
		left.setSystemId(right.getSystemId());
		left.setTimestamp(right.getTimestamp());
	}

    @Override
    public void excludeCommonFields( Set<String> fields )
    {
    	if (fields == null || fields.isEmpty())
    		return;
    	
    	fields.remove( MatmProperties.PROPERTY_TIMESTAMP );
    	fields.remove( MatmProperties.PROPERTY_LAST_UPDATE_SOURCE);
    	fields.remove( MatmProperties.PROPERTY_TIMESTAMP_SOURCE );
    	fields.remove( MatmProperties.PROPERTY_TIMESTAMP_SOURCE_RECEIVED );
        fields.remove( MatmProperties.PROPERTY_TIMESTAMP_SOURCE_PROCESSED );
        fields.remove( MatmProperties.PROPERTY_TIMESTAMP_FUSER_RECEIVED );
        fields.remove( MatmProperties.PROPERTY_TIMESTAMP_FUSER_PROCESSED );
        fields.remove( MatmProperties.PROPERTY_SYSTEM_ID );
        fields.remove( MatmProperties.PROPERTY_UPDATE_SOURCES );
        fields.remove( MatmProperties.PROPERTY_CHANGES );
        fields.remove( MatmProperties.PROPERTY_GUFI );
    }
}
