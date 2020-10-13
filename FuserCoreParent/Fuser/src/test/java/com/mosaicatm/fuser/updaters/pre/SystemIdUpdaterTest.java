package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class SystemIdUpdaterTest
{
    private SystemIdUpdater updater;
    
    @Before
    public void setup(){
        updater = new SystemIdUpdater();
    }
    
    @Test
    public void testFilterSystemId(){
        MatmFlight flight = new MatmFlight();
        flight.setLastUpdateSource("FMC");
        
        updater.update(flight, null);
        assertEquals(flight.getLastUpdateSource(), flight.getSystemId());
        
        flight = new MatmFlight();
        flight.setLastUpdateSource("AIRLINE");
        flight.setSystemId("FLIGHTHUB");
        
        updater.update(flight, null);
        assertEquals("FLIGHTHUB", flight.getSystemId());        
    }

}
