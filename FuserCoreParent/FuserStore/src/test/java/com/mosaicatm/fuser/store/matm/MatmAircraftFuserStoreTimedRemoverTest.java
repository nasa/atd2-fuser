package com.mosaicatm.fuser.store.matm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.aircraft.MatmAircraftIdLookup;
import com.mosaicatm.fuser.store.FuserStoreProxy;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.time.SystemClock;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.common.MetaData;

public class MatmAircraftFuserStoreTimedRemoverTest
{
    private Clock clock;
    private FuserStoreProxy<MatmAircraft, MetaData> proxy;
    private MatmAircraftFuserStoreTimedRemover remover;
    
    @Before
    public void setup()
    {
        clock = new SystemClock();
        
        MatmAircraftFuserStore store = new MatmAircraftFuserStore(1);
        store.setIdLookup(new MatmAircraftIdLookup());
        proxy = new FuserStoreProxy<>(store);
        
        remover = new MatmAircraftFuserStoreTimedRemover(proxy);
        remover.setActive(true);
        remover.setExpirationTimeDays(1);
        remover.setClock(clock);
    }
    
    @Test
    public void testMatmAircraftFuserStoreTimedRemover()
    {        
        MatmAircraft aircraft = new MatmAircraft();
        aircraft.setRegistration("TEST_REGISTRATION");
        aircraft.setTimestamp(new Date(clock.getTimeInMillis()));
        proxy.add(aircraft);
        
        assertEquals(1, proxy.size());
        assertNotNull(proxy.get(aircraft.getRegistration()));
        
        remover.remove();
        
        assertEquals(1, proxy.size());
        assertNotNull(proxy.get(aircraft.getRegistration()));
     
        long aircraftTimestamp = clock.getTimeInMillis() - (TimeFactory.DAY_IN_MILLIS * 2);
        aircraft.setTimestamp(new Date(aircraftTimestamp));
        
        remover.remove();
        
        assertEquals(0, proxy.size());
        assertNull(proxy.get(aircraft.getRegistration()));
    }
    
    @Test
    public void testMatmAircraftStoreTimedRemoverThread()
    {
        long aircraftTimestamp = clock.getTimeInMillis() - (TimeFactory.DAY_IN_MILLIS * 2);
        
        MatmAircraft aircraft = new MatmAircraft();
        aircraft.setRegistration("TEST_REGISTRATION");
        aircraft.setTimestamp(new Date(aircraftTimestamp));
        proxy.add(aircraft);
        
        assertEquals(1, proxy.size());
        assertNotNull(proxy.get(aircraft.getRegistration()));
        
        remover.setCheckIntervalMillis(1000L);
        remover.start();
        
        try
        {
            Thread.sleep(2000L);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        
        assertEquals(0, proxy.size());
        assertNull(proxy.get(aircraft.getRegistration()));
    }
}
