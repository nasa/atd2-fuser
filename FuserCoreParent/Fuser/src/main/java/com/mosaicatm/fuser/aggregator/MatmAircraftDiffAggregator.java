package com.mosaicatm.fuser.aggregator;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class MatmAircraftDiffAggregator
extends MatmAggregator<MatmAircraft>
{
    private final Log log = LogFactory.getLog( getClass() );

    public MatmAircraftDiffAggregator()
    {
        super();
    }

    @Override
    public MatmAircraft createComponent()
    {
        return new MatmAircraft();
    }
    
    @Override
    public void copyComponentFromRightToLeft(MatmAircraft left, MatmAircraft right)
    {
        left.setRegistration(right.getRegistration());
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
        fields.remove( MatmProperties.PROPERTY_REGISTRATION );
    }
}
