package com.mosaicatm.fuser.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.aircraft.MatmAircraftIdLookup;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.matm.MatmAircraftFuserStore;
import com.mosaicatm.lib.util.concurrent.SyncPoint;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.aircraftcomposite.MatmAircraftComposite;
import com.mosaicatm.matmdata.common.MetaData;

public class FuserAircraftSyncServiceTest 
{
    private final int AIRCRAFT_COUNT = 5;
    
    private FuserAircraftSyncServiceImpl syncService;
    private FuserStore<MatmAircraft, MetaData> store;
    
    @Before
    public void setup()
    {
        int processingThreadCount = 1;
        store = new MatmAircraftFuserStore( processingThreadCount );
        ((MatmAircraftFuserStore)store).setIdLookup(new MatmAircraftIdLookup());
        
        syncService = new FuserAircraftSyncServiceImpl();
        syncService.setSyncPoint(new SyncPoint(false));
        syncService.setStore(store);
        
        for (int i = 0; i < AIRCRAFT_COUNT; ++i)
        {
            MatmAircraft aircraft = new MatmAircraft();
            aircraft.setRegistration("TEST-CLT-" + i);
            
            MetaData meta = new MetaData();
            meta.setFieldName("registration");
            meta.setSource("test");
            
            store.add(aircraft);
            store.updateMetaData(aircraft, meta);
        }
    }
    
    @Test
    public void testSyncAircraft()
    {
        Collection<MatmAircraft> aircraftList = syncService.getAircraft();
        assertNotNull(aircraftList);
        assertEquals(AIRCRAFT_COUNT, aircraftList.size());
        
        for (MatmAircraft aircraft : aircraftList)
        {
            assertNotNull(aircraft);
        }
    }
    
    @Test
    public void testSyncCompositeAircraft()
    {
        Collection<MatmAircraftComposite> aircraftList = syncService.getCompositeAircraft();
        assertNotNull(aircraftList);
        assertEquals(AIRCRAFT_COUNT, aircraftList.size());
        
        for (MatmAircraftComposite aircraft : aircraftList)
        {
            assertNotNull(aircraft);
            assertNotNull(aircraft.getAircraft());
            assertNotNull(aircraft.getMetaData());
            assertEquals(1, aircraft.getMetaData().size());
        }
    }
    
    @Test
    public void testSyncFlightCount()
    {
        int totalSize = syncService.getAircraftCount();
        assertEquals(AIRCRAFT_COUNT, totalSize);
    }
}
