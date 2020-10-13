package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.matm.actypelib.api.AircraftTypeApi;
import com.matm.actypelib.api.AircraftTypeApiConfig;
import com.matm.actypelib.api.AircraftTypeApiImpl;
import com.matm.actypelib.manager.AircraftTypeManager;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class AircraftTypeUpdaterTest
{
    private AircraftTypeUpdater updater;
    
    @Before
    public void setup ()
    {
        AircraftTypeApiConfig config = new AircraftTypeApiConfig();
        config.setAllowClassPathLoading(true);
        
        AircraftTypeApi api = new AircraftTypeApiImpl(config);
        api.initialize();
        
        updater = new AircraftTypeUpdater();
        updater.setAircraftTypeManager(api.getAircraftTypeManager());
    }
    
    @Test
    public void testDefaultAircraftType()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        assertNull(update.getAircraftType());
        
        updater.update(update, target);
        
        assertNotNull(update.getAircraftType());
        assertEquals (AircraftTypeManager.DEFAULT_AIRCRAFT_TYPE, update.getAircraftType());
    }
    
    @Test
    public void testUpdateAircraftType()
    {
        MatmFlight update = new MatmFlight();
        update.setAircraftType("B757");
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getAircraftType());
        
        updater.update(update, target);
        
        assertNotNull(update.getAircraftType());
        
        assertEquals("B752", update.getAircraftType());
    }
    
    @Test
    public void testUpdateAircraftTypeMapping()
    {
        MatmFlight update = new MatmFlight();
        update.setAircraftType("CR9");
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getAircraftType());
        
        updater.update(update, target);
        
        assertNotNull(update.getAircraftType());
        
        assertEquals("CRJ9", update.getAircraftType());
    }
    
    @Test
    public void testUpdateUnknownAircraftTypeMapping()
    {
        MatmFlight update = new MatmFlight();
        update.setAircraftType("UNKOWN AIRCRAFT TYPE");
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getAircraftType());
        
        updater.update(update, target);
        
        assertNotNull(update.getAircraftType());
        
        assertEquals(AircraftTypeManager.DEFAULT_AIRCRAFT_TYPE, update.getAircraftType());
    }
    
    @Test
    public void testEmptyUpdateAircraftType()
    {
        MatmFlight update = new MatmFlight();
        update.setAircraftType("");
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getAircraftType());
        
        updater.update(update, target);
        
        assertNotNull(update.getAircraftType());
        
        assertEquals(AircraftTypeManager.DEFAULT_AIRCRAFT_TYPE, update.getAircraftType());
    }
}
