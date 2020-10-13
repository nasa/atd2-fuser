package com.mosaicatm.fuser.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightIdLookup;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.matm.MatmFuserStore;
import com.mosaicatm.lib.util.concurrent.SyncPoint;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flightcomposite.MatmFlightComposite;

public class FuserFlightSyncServiceTest 
{
    private final int CLT_FLIGHTS = 5;
    private final int DFW_FLIGHTS = 3;
    private final int MEM_FLIGHTS = 2;
    
    private FuserFlightSyncServiceImpl syncService;
    private FuserStore<MatmFlight, MetaData> store;
    
    @Before
    public void setup()
    {
        int processingThreadCount = 1;
        store = new MatmFuserStore( processingThreadCount );
        ((MatmFuserStore)store).setIdLookup(new MatmFlightIdLookup());
        
        syncService = new FuserFlightSyncServiceImpl();
        syncService.setSyncPoint(new SyncPoint(false));
        syncService.setStore(store);
        
        for (int i = 0; i < CLT_FLIGHTS; ++i)
        {
            MatmFlight flight = new MatmFlight();
            flight.setGufi("TEST-CLT-" + i);
            
            Aerodrome aerodrome = new Aerodrome();
            aerodrome.setIataName("CLT");
            flight.setDepartureAerodrome(aerodrome);
            
            MetaData meta = new MetaData();
            meta.setFieldName("gufi");
            meta.setSource("test");
            
            store.add(flight);
            store.updateMetaData(flight, meta);
        }
        
        for (int i = 0; i < DFW_FLIGHTS; ++i)
        {
            MatmFlight flight = new MatmFlight();
            flight.setGufi("TEST-DFW-" + i);
            
            Aerodrome aerodrome = new Aerodrome();
            aerodrome.setIataName("DFW");
            flight.setArrivalAerodrome(aerodrome);
            
            MetaData meta = new MetaData();
            meta.setFieldName("gufi");
            meta.setSource("test");
            
            store.add(flight);
            store.updateMetaData(flight, meta);
        }
        
        for (int i = 0; i < MEM_FLIGHTS; ++i)
        {
            MatmFlight flight = new MatmFlight();
            flight.setGufi("TEST-MEM-" + i);
            
            Aerodrome aerodrome = new Aerodrome();
            aerodrome.setIataName("MEM");
            flight.setSurfaceAirport(aerodrome);
            
            MetaData meta = new MetaData();
            meta.setFieldName("gufi");
            meta.setSource("test");
            
            store.add(flight);
            store.updateMetaData(flight, meta);
        }
    }
    
    @Test
    public void testSyncFlights()
    {
        Collection<MatmFlight> flights = syncService.getFlights();
        assertNotNull(flights);
        assertEquals((CLT_FLIGHTS + DFW_FLIGHTS + MEM_FLIGHTS), flights.size());
        
        for (MatmFlight flight : flights)
        {
            assertNotNull(flight);
        }
    }
    
    @Test
    public void testSyncFlightsByAirport()
    {
        Collection<MatmFlight> flights = syncService.getFlightsByAirport("CLT");
        assertNotNull(flights);
        assertEquals(CLT_FLIGHTS, flights.size());
        
        for (MatmFlight flight : flights)
        {
            assertNotNull(flight);
        }
        
        flights = syncService.getFlightsByAirport("DFW");
        assertNotNull(flights);
        assertEquals(DFW_FLIGHTS, flights.size());
        
        for (MatmFlight flight : flights)
        {
            assertNotNull(flight);
        }
        
        // this tests flights where the surface airport is set
        flights = syncService.getFlightsByAirport("MEM");
        assertNotNull(flights);
        assertEquals(MEM_FLIGHTS, flights.size());
        
        for (MatmFlight flight : flights)
        {
            assertNotNull(flight);
        }
    }
    
    @Test
    public void testSyncCompositeFlights()
    {
        Collection<MatmFlightComposite> flights = syncService.getCompositeFlights();
        assertNotNull(flights);
        assertEquals((CLT_FLIGHTS + DFW_FLIGHTS + MEM_FLIGHTS), flights.size());
        
        for (MatmFlightComposite flight : flights)
        {
            assertNotNull(flight);
            assertNotNull(flight.getFlight());
            assertNotNull(flight.getMetaData());
            assertEquals(1, flight.getMetaData().size());
        }
    }
    
    @Test
    public void testSyncCompositeFligthsByAirport()
    {
        Collection<MatmFlightComposite> flights = syncService.getCompositeFlightsByAirport("CLT");
        assertNotNull(flights);
        assertEquals(CLT_FLIGHTS, flights.size());
        
        for (MatmFlightComposite flight : flights)
        {
            assertNotNull(flight);
            assertNotNull(flight.getFlight());
            assertNotNull(flight.getMetaData());
            assertEquals(1, flight.getMetaData().size());
        }
        
        flights = syncService.getCompositeFlightsByAirport("DFW");
        assertNotNull(flights);
        assertEquals(DFW_FLIGHTS, flights.size());
        
        for (MatmFlightComposite flight : flights)
        {
            assertNotNull(flight);
            assertNotNull(flight.getFlight());
            assertNotNull(flight.getMetaData());
            assertEquals(1, flight.getMetaData().size());
        }
        
        // tests flights where the surface airport is set
        flights = syncService.getCompositeFlightsByAirport("MEM");
        assertNotNull(flights);
        assertEquals(MEM_FLIGHTS, flights.size());
        
        for (MatmFlightComposite flight : flights)
        {
            assertNotNull(flight);
            assertNotNull(flight.getFlight());
            assertNotNull(flight.getMetaData());
            assertEquals(1, flight.getMetaData().size());
        }
    }
    
    @Test
    public void testSyncFlightCount()
    {
        int totalSize = syncService.getFlightCount();
        assertEquals((CLT_FLIGHTS + DFW_FLIGHTS + MEM_FLIGHTS), totalSize);
    }
    
    @Test
    public void testSyncFlightCountByAirports()
    {
        int size = syncService.getFlightCountByAirport("CLT");
        assertEquals (CLT_FLIGHTS, size);
        
        size = syncService.getFlightCountByAirport("DFW");
        assertEquals (DFW_FLIGHTS, size);
    }
}
