package com.mosaicatm.fuser.aggregator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MatmObjectDiffTest
{
    private MatmObjectDiff diff;
    
    @Before
    public void setup()
    {
        diff = new MatmObjectDiff();
    }
    
    @Ignore
    @Test
    public void testGenerateSampleFlightData()
    {
        com.mosaicatm.matmdata.flight.ObjectFactory objFactory = new com.mosaicatm.matmdata.flight.ObjectFactory();
        
        com.mosaicatm.matmdata.flight.MatmFlight target = new com.mosaicatm.matmdata.flight.MatmFlight();
        target.setExtensions( new com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions() );
        target.getExtensions().setTbfmExtension( new com.mosaicatm.matmdata.flight.extension.TbfmExtension() );
        target.setEstimatedDepartureClearanceTime( objFactory.createMatmFlightEstimatedDepartureClearanceTime( new java.util.Date( 1000 )));
        target.getEstimatedDepartureClearanceTime().setNil( false );
        
        com.mosaicatm.matmdata.flight.MatmFlight update = new com.mosaicatm.matmdata.flight.MatmFlight();
        update.setExtensions( new com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions() );
        update.getExtensions().setTbfmExtension( new com.mosaicatm.matmdata.flight.extension.TbfmExtension() );

        update.setAcid( "acid" );
        com.mosaicatm.matmdata.common.MeteredTime metered_time = new com.mosaicatm.matmdata.common.MeteredTime();
        metered_time.setFrozen( true );
        metered_time.setValue( new java.util.Date() );
        update.setArrivalRunwayMeteredTime( metered_time );
        
        update.setEstimatedDepartureClearanceTime( objFactory.createMatmFlightEstimatedDepartureClearanceTime( null ));

        update.getExtensions().getTbfmExtension().setArrivalMeterFix( "ARR_FIX" );

        try
        {
            AggregationResult result = diff.compareObjects( update, target );
            if (result == null)
                System.out.println( "null is returned from the aggregation." );
            else
                System.out.println( result.getChanges() );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}
