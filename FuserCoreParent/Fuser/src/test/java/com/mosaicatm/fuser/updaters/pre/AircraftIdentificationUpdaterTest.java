package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class AircraftIdentificationUpdaterTest
{
    private AircraftIdentificationUpdater updater;
    
    @Before
    public void setup()
    {
        updater = new AircraftIdentificationUpdater();
    }
    
    @Test
    public void testDeriveAircraftRegistrationValid()
    {
        MatmFlight update = new MatmFlight();
        update.setAircraftAddress("10908975");
        
        assertNull(update.getAircraftRegistration());
        
        updater.update(update, null);
        
        assertEquals("N515NA", update.getAircraftRegistration());
    }
    
    @Test
    public void testDeriveAircraftRegistrationInvalid()
    {
        MatmFlight update = new MatmFlight();
        update.setAircraftAddress(null);
        
        MatmFlight target = new MatmFlight();
        target.setAircraftAddress(null);
        
        assertNull(update.getAircraftRegistration());
        
        updater.update(update, target);
        
        assertNull(update.getAircraftRegistration());
    }
    
    @Test
    public void testDeriveAircraftAddressValid()
    {
        MatmFlight update = new MatmFlight();
        update.setAircraftRegistration("N515NA");
        
        assertNull(update.getAircraftAddress());
        
        updater.update(update, null);
        
        assertEquals("10908975", update.getAircraftAddress());
    }
    
    @Test
    public void testDeriveAircraftAddressInvalid()
    {
        MatmFlight update = new MatmFlight();
        update.setAircraftRegistration(null);
        
        MatmFlight target = new MatmFlight();
        target.setAircraftAddress(null);
        
        assertNull(update.getAircraftAddress());
        
        updater.update(update, target);
        
        assertNull(update.getAircraftAddress());
    }
}
