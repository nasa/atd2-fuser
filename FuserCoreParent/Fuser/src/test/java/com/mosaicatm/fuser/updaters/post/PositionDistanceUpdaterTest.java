package com.mosaicatm.fuser.updaters.post;

import java.util.Date;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class PositionDistanceUpdaterTest
{
    private PositionDistanceUpdater updater;
    
    @Before
    public void setup ()
    {
        updater = new PositionDistanceUpdater();
    }
    
    @Test
    public void testNullUpdates()
    {        
        MatmFlight update = getMatmFlight( null, null );        
        updater.update( update, null );        
        assertNull( update.getPosition().getDistanceTotal() );        
        
        MatmFlight target = getMatmFlight( null, null );        
        update = getMatmFlight( 35.2, null );
        updater.update( update, target );
        assertNull( update.getPosition().getDistanceTotal() );          
        
        update = getMatmFlight( null, -80.9 );
        updater.update( update, target );
        assertNull( update.getPosition().getDistanceTotal() );         
        
        target = getMatmFlight( 35.2, -80.9 );
        target.getPosition().setDistanceTotal( 50.5 );
        update = getMatmFlight( 35.2, -80.9 );
        updater.update( update, target );
        assertNull( update.getPosition().getDistanceTotal() );             
    }
    
    @Test
    public void testUpdates()
    { 
        MatmFlight update = getMatmFlight( 35.2, -80.9 );
        updater.update( update, null );
        assertEquals( Double.valueOf( 0 ), update.getPosition().getDistanceTotal() );
        
        MatmFlight target = getMatmFlight( 35.2, -80.9 );
        update = getMatmFlight( 35.2, -80.9 );
        updater.update( update, target );
        assertEquals( Double.valueOf( 0 ), update.getPosition().getDistanceTotal() );         
        
        target = getMatmFlight( 36., -81. );
        update = getMatmFlight( 35.2, -80.9 );
        updater.update( update, target );
        assertEquals( 48.25, update.getPosition().getDistanceTotal(), 0.1 ); 
                
        target = getMatmFlight( 36., -81. );
        target.getPosition().setDistanceTotal( 100. );
        update = getMatmFlight( 35.2, -80.9 );
        updater.update( update, target );
        assertEquals( 148.25, update.getPosition().getDistanceTotal(), 0.1 );         
    }    
    
    private MatmFlight getMatmFlight( Double latitude, Double longitude )
    {
        MatmFlight matm = new MatmFlight();
        matm.setGufi("gufi");
        matm.setTimestamp( new Date() );        
        matm.setLastUpdateSource( "TFM" );        

        matm.setPosition( new Position() );
        matm.getPosition().setLatitude( latitude );
        matm.getPosition().setLongitude( longitude );
        
        return( matm );
    }
}
