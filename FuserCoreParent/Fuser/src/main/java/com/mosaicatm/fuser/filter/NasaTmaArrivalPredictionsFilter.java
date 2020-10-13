package com.mosaicatm.fuser.filter;

import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class NasaTmaArrivalPredictionsFilter 
implements MatmFilter<MatmFlight>
{
    private static final String TMA_SOURCE = FuserSource.TMA.value();
    private static final String NASA_SYSTEM_ID_INDICATOR = "-NASA";
    
    private boolean active;

    @Override
    public MatmFlight filter( MatmFlight flight )
    {
        if( isActive() && ( flight != null ))
        {
            if( TMA_SOURCE.equals( flight.getLastUpdateSource() ) && 
                    ( flight.getSystemId() != null ) && 
                    flight.getSystemId().contains( NASA_SYSTEM_ID_INDICATOR ))
            {
                flight.setArrivalRunwayAssigned( null );
                flight.setArrivalRunwayEstimatedTime( null );                
                flight.setArrivalRunwayMeteredTime( null );
            }
        }

        return flight;
    }

    @Override
    public boolean isActive()
    {
        return active;
    }

    public void setActive( boolean active )
    {
        this.active = active;
    }
}
