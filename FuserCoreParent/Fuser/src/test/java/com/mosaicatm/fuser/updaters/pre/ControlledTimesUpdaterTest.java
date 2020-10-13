package com.mosaicatm.fuser.updaters.pre;

import java.util.Date;
import javax.xml.bind.JAXBElement;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;

public class ControlledTimesUpdaterTest
{
    private ControlledTimesUpdater updater;
    private JAXBElement<Date> nilTime = null;
    private JAXBElement<Date> time = null;
    
    @Before
    public void setup ()
    {
        ObjectFactory factory = new ObjectFactory();
        time = factory.createMatmFlightEstimatedDepartureClearanceTime( new Date() );
        nilTime = factory.createMatmFlightEstimatedDepartureClearanceTime( null );
        
        updater = new ControlledTimesUpdater();
    }
    
    @Test
    public void testNull()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        assertNull( update.getEstimatedDepartureClearanceTime() );
        assertNull( update.getArrivalRunwayControlledTime() );
        
        assertNull( target.getEstimatedDepartureClearanceTime() );
        assertNull( target.getArrivalRunwayControlledTime() );
        
        updater.update( update, target);
        
        assertNull( update.getEstimatedDepartureClearanceTime() );
        assertNull( update.getArrivalRunwayControlledTime() );
    }
    
    @Test
    public void testUpateNormal()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();

        target.setLastUpdateSource( "TFM" );
        update.setLastUpdateSource( "TMA" );
        
        update.setEstimatedDepartureClearanceTime( time );
        update.setArrivalRunwayControlledTime( time );
        
        updater.update( update, target );
        
        assertNotNull( update.getEstimatedDepartureClearanceTime() );
        assertNotNull( update.getArrivalRunwayControlledTime() );   
        
        target.setEstimatedDepartureClearanceTime( time );
        target.setArrivalRunwayControlledTime( time );
        
        updater.update( update, target );
        
        assertNotNull( update.getEstimatedDepartureClearanceTime() );
        assertNotNull( update.getArrivalRunwayControlledTime() );        
        
        update.setLastUpdateSource( "TFM" );
        
        updater.update( update, target );
        
        assertNotNull( update.getEstimatedDepartureClearanceTime() );
        assertNotNull( update.getArrivalRunwayControlledTime() );              
    }   
    
    @Test
    public void testUpatePurged()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();

        target.setLastUpdateSource( "TFM" );
        update.setLastUpdateSource( "TFM" );
        
        target.setEstimatedDepartureClearanceTime( nilTime );
        target.setArrivalRunwayControlledTime( nilTime );                
                
        update.setEstimatedDepartureClearanceTime( time );
        update.setArrivalRunwayControlledTime( time );
        
        // TFM source can override purged controlled times
        updater.update( update, target );
        
        assertNotNull( update.getEstimatedDepartureClearanceTime() );
        assertNotNull( update.getArrivalRunwayControlledTime() );   
        
        update.setLastUpdateSource( "TMA" );
        
        // TMA cannot override purged controlled times        
        updater.update( update, target );

        assertNull( update.getEstimatedDepartureClearanceTime() );
        assertNull( update.getArrivalRunwayControlledTime() );         
    }
}
