package com.mosaicatm.fuser.updaters.post;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;

public class NullFieldUpdaterTest
{
    private ObjectFactory objectFactory;

    @Before
    public void setUp() throws Exception
    {
        objectFactory = new ObjectFactory();
    }

    @Test
    public void testJaxbType()
    {
        NullFieldUpdater updater = new NullFieldUpdater();
        updater.setActive( true );
        updater.setFieldsToNullFromString( "departureStandActualTime" );
        
        MatmFlight flight = new MatmFlight();
        
        flight.setDepartureStandActualTime(
                objectFactory.createMatmFlightDepartureStandActualTime( new Date( 1_000_000 ) ) );
        
        assertNotNull( flight.getDepartureStandActualTime() );
        
        updater.update( flight, flight );
        
        assertNull( flight.getDepartureStandActualTime() );
    }
    
    @Test
    public void testJaxbTypeValue()
    {
        NullFieldUpdater updater = new NullFieldUpdater();
        updater.setActive( true );
        updater.setFieldsToNullFromString( "departureStandActualTime.value" );
        
        MatmFlight flight = new MatmFlight();
        
        flight.setDepartureStandActualTime(
                objectFactory.createMatmFlightDepartureStandActualTime( new Date( 1_000_000 ) ) );
        
        assertNotNull( flight.getDepartureStandActualTime().getValue() );
        
        updater.update( flight, flight );
        
        assertNull( flight.getDepartureStandActualTime().getValue() );
    }

}
