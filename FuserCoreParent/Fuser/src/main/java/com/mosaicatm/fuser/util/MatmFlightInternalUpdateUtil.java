package com.mosaicatm.fuser.util;

import java.util.Date;

import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFlightInternalUpdateUtil
{
    public static MatmFlight getFuserInternalUpdate( Date currentTime, MatmFlight baseFlight, String systemId )
    {
        MatmFlight matm = new MatmFlight();

        matm.setLastUpdateSource( FuserSource.FUSER.value() );
        matm.setSystemId( systemId );
        
        matm.setGufi( baseFlight.getGufi() );
        
        // All timestamps
        matm.setTimestamp( currentTime );
        matm.setTimestampFuserReceived( currentTime );
        matm.setTimestampSource( currentTime );
        matm.setTimestampSourceReceived( currentTime );
        matm.setTimestampSourceProcessed( currentTime );            
        
        //Needed to pass filters
        matm.setSurfaceAirport( baseFlight.getSurfaceAirport() );
        matm.setDepartureAerodrome( baseFlight.getDepartureAerodrome() );
        matm.setArrivalAerodrome( baseFlight.getArrivalAerodrome() );            
        
        return( matm );
    }
}
