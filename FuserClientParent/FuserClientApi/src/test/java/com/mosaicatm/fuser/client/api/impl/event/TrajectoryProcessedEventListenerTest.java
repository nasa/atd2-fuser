package com.mosaicatm.fuser.client.api.impl.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.impl.data.FuserClientMatmFlightStore;
import com.mosaicatm.fuser.client.api.impl.event.TrajectoryProcessedEventListener;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class TrajectoryProcessedEventListenerTest
{
    private FuserClientStore<MatmFlight> store;
    private TrajectoryProcessedEventListener processor;
    
    @Before
    public void setup ()
    {
        store = new FuserClientMatmFlightStore ();
        processor = new TrajectoryProcessedEventListener ();
        processor.setStore(store);
        processor.setActive(true);
    }
    
    @Test
    public void testDataAdded ()
    {
        String gufi = UUID.randomUUID().toString();
        Date timestamp = new Date ();
        
        MatmFlight flight = createFlight (gufi, 11.111d, 22.222d, 100d, timestamp);
        store.add(flight);
        
        processor.dataAdded(flight);
        
        MatmFlight storeFlight = store.get(gufi);
        assertNotNull (storeFlight);
        
        List<Position> trajectories = storeFlight.getTrajectory();
        
        assertNotNull (trajectories);
        assertEquals (1, trajectories.size());
        
        Position orig = flight.getPosition();
        Position point = trajectories.get(0);
        
        assertEquals (orig.getTimestamp(), point.getTimestamp());
        assertEquals (orig.getAltitude(), point.getAltitude());
        assertEquals(orig.getLatitude(), point.getLatitude());
        assertEquals(orig.getLongitude(), point.getLongitude());
    }
    
    @Test
    public void testMultipleData ()
    {
        String gufi = UUID.randomUUID().toString();
        Date timestamp = new Date (System.currentTimeMillis());
        Date timestamp2 = new Date (System.currentTimeMillis() + (1000 * 60 * 3));
        
        MatmFlight flight = createFlight (gufi, 11.111d, 22.222d, 100d, timestamp);
        MatmFlight flight2 = createFlight (gufi, 33.333d, 44.444d, 500d, timestamp2);
        store.add(flight);
        
        processor.dataAdded(flight);
        processor.dataUpdated(null, flight2);
        
        MatmFlight storeFlight = store.get(gufi);
        assertNotNull (storeFlight);
        
        List<Position> trajectories = storeFlight.getTrajectory();
        
        assertNotNull (trajectories);
        assertEquals (2, trajectories.size());
        
        Position orig = flight.getPosition();
        Position orig2 = flight2.getPosition();
        Position point = trajectories.get(0);
        Position point2 = trajectories.get(1);
        
        assertEquals (orig.getTimestamp(), point.getTimestamp());
        assertEquals (orig.getAltitude(), point.getAltitude());
        assertEquals(orig.getLatitude(), point.getLatitude());
        assertEquals(orig.getLongitude(), point.getLongitude());
        
        assertEquals (orig2.getTimestamp(), point2.getTimestamp());
        assertEquals (orig2.getAltitude(), point2.getAltitude());
        assertEquals(orig2.getLatitude(), point2.getLatitude());
        assertEquals(orig2.getLongitude(), point2.getLongitude());
    }
    
    @Test
    public void testDuplicateDataAdded ()
    {
        String gufi = UUID.randomUUID().toString();
        Date timestamp = new Date ();
        
        MatmFlight flight = createFlight (gufi, 11.111d, 22.222d, 100d, timestamp);
        MatmFlight flight2 = createFlight (gufi, 11.111d, 22.222d, 100d, timestamp);
        store.add(flight);
        
        processor.dataAdded(flight);
        processor.dataUpdated(null, flight2);
        
        MatmFlight storeFlight = store.get(gufi);
        assertNotNull (storeFlight);
        
        List<Position> trajectories = storeFlight.getTrajectory();
        
        assertNotNull (trajectories);
        assertEquals (1, trajectories.size());
        
        Position orig = flight.getPosition();
        Position point = trajectories.get(0);
        
        assertEquals (orig.getTimestamp(), point.getTimestamp());
        assertEquals (orig.getAltitude(), point.getAltitude());
        assertEquals(orig.getLatitude(), point.getLatitude());
        assertEquals(orig.getLongitude(), point.getLongitude());
    }
    
    @Test
    public void testDataUpdated ()
    {
        String gufi = UUID.randomUUID().toString();
        Date timestamp = new Date ();
        
        MatmFlight flight = createFlight (gufi, 33.333d, 44.444d, 100d, timestamp);
        store.add(flight);
        
        /*
         * data updated uses the same logic as the dataAdded, so no need to pass
         * in the target flight since that original message will always be used
         */
        processor.dataUpdated(null, flight);
        
        MatmFlight storeFlight = store.get(gufi);
        assertNotNull (storeFlight);
        
        List<Position> trajectories = storeFlight.getTrajectory();
        
        assertNotNull (trajectories);
        assertEquals (1, trajectories.size());
        
        Position orig = flight.getPosition();
        Position point = trajectories.get(0);
        
        assertEquals (orig.getTimestamp(), point.getTimestamp());
        assertEquals (orig.getAltitude(), point.getAltitude());
        assertEquals(orig.getLatitude(), point.getLatitude());
        assertEquals(orig.getLongitude(), point.getLongitude());
    }
    
    
    private MatmFlight createFlight (String gufi, Double lat, Double lon, Double alt, Date time)
    {   
    	 MatmFlight flight = new MatmFlight();
         
         flight.setGufi(gufi);
         Position pos = new Position();
         pos.setLatitude(lat);
         pos.setLongitude(lon);
         pos.setTimestamp(time);
         pos.setAltitude(alt);
         
         flight.setPosition(pos);
         
         return flight;
    }
}
