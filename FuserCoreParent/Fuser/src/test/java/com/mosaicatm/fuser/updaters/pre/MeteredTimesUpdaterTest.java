package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.matmdata.common.MeteredTime;
import com.mosaicatm.matmdata.common.SurfaceFlightState;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;

public class MeteredTimesUpdaterTest
{
    private MeteredTimesUpdater updater;
    private ObjectFactory objectFactory;

    @Before
    public void setup ()
    {
        updater = new MeteredTimesUpdater();
        objectFactory = new ObjectFactory();
    }
    
    @Test
    public void testNullMeteredTimes()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        assertNull(update.getArrivalRunwayMeteredTime());
        assertNull(target.getArrivalRunwayMeteredTime());
        
        updater.update(update, target);
        
        assertNull(update.getArrivalRunwayMeteredTime());
        assertNull(target.getArrivalRunwayMeteredTime());
    }
    
    @Test
    public void testMeteredTimeFilterOutArrivalMeteredTime()
    {
        MatmFlight update = new MatmFlight();
        update.setArrivalRunwayMeteredTime( new MeteredTime() );
        update.getArrivalRunwayMeteredTime().setFrozen( Boolean.FALSE );
        update.getArrivalRunwayMeteredTime().setValue( new Date( 1 ));
        
        MatmFlight target = new MatmFlight();
        
        //Try for no indications of post-departure
        MatmFlight update_test = (MatmFlight) update.clone();
        updater.update(update_test, target);
        
        assertNull(update_test.getArrivalRunwayMeteredTime().getValue());
        assertNull(update_test.getArrivalRunwayMeteredTime().isFrozen());
        
        //Try for update state indication of pre-departure
        update_test = (MatmFlight) update.clone();
        update_test.setDepartureSurfaceFlightState( SurfaceFlightState.IN_QUEUE );
        updater.update(update_test, target);
        
        assertNull(update_test.getArrivalRunwayMeteredTime().getValue());
        assertNull(update_test.getArrivalRunwayMeteredTime().isFrozen());     
        
        assertNull(update_test.getArrivalRunwayMeteredTime().getValue());
        assertNull(update_test.getArrivalRunwayMeteredTime().isFrozen());         
        
        //Try for no time indication of post-departure
        update_test = (MatmFlight) update.clone();
        update_test.setDepartureRunwayProposedTime( new Date() );
        updater.update(update_test, target);
        
        assertNull(update_test.getArrivalRunwayMeteredTime().getValue());
        assertNull(update_test.getArrivalRunwayMeteredTime().isFrozen());    
        
        //Try for target state indication of pre-departure
        update_test = (MatmFlight) update.clone();
        target.setDepartureSurfaceFlightState( SurfaceFlightState.IN_QUEUE );
        updater.update(update_test, target);        
    }    
    
    @Test
    public void testMeteredTimeFilterInArrivalMeteredTime()
    {
        MatmFlight update = new MatmFlight();
        update.setArrivalRunwayMeteredTime( new MeteredTime() );
        update.getArrivalRunwayMeteredTime().setFrozen( Boolean.FALSE );
        update.getArrivalRunwayMeteredTime().setValue( new Date( 1 ));
        
        MatmFlight target = new MatmFlight();
        target.setArrivalRunwayMeteredTime( new MeteredTime() );
        target.getArrivalRunwayMeteredTime().setFrozen( Boolean.TRUE );
        target.getArrivalRunwayMeteredTime().setValue( new Date( 2 ));        
        
        //Try update state indication of post-departure
        MatmFlight update_test = (MatmFlight) update.clone();
        update_test.setDepartureSurfaceFlightState( SurfaceFlightState.OFF );
        updater.update(update_test, target);
        
        assertNotNull(update_test.getArrivalRunwayMeteredTime().getValue());
        assertNotNull(update_test.getArrivalRunwayMeteredTime().isFrozen());
        
        
        //Try update time indication of post-departure
        update_test = (MatmFlight) update.clone();
        update_test.setDepartureRunwayActualTime(objectFactory.createMatmFlightDepartureRunwayActualTime(new Date()));
        updater.update(update_test, target);
        
        assertNotNull(update_test.getArrivalRunwayMeteredTime().getValue());
        assertNotNull(update_test.getArrivalRunwayMeteredTime().isFrozen());        
        
        
        
        //Try target state indication of post-departure
        update_test = (MatmFlight) update.clone();
        target = (MatmFlight) target.clone();
        target.setDepartureSurfaceFlightState( SurfaceFlightState.TERMINAL_AIRSPACE );
        updater.update(update_test, target);
        
        assertNotNull(update_test.getArrivalRunwayMeteredTime().getValue());
        assertNotNull(update_test.getArrivalRunwayMeteredTime().isFrozen());          
        
        
        
        //Try target time indication of post-departure
        update_test = (MatmFlight) update.clone();
        target = (MatmFlight) target.clone();
        target.setDepartureFixActualTime( new Date() );
        updater.update(update_test, target);
        
        assertNotNull(update_test.getArrivalRunwayMeteredTime().getValue());
        assertNotNull(update_test.getArrivalRunwayMeteredTime().isFrozen());    
    }        
}
