package com.mosaicatm.fuser.client.api.impl.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.client.api.data.DataRemover;
import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;
import com.mosaicatm.fuser.client.api.impl.data.MatmFlightDataRemover;
import com.mosaicatm.fuser.client.api.impl.data.FuserClientMatmFlightStore;
import com.mosaicatm.fuser.client.api.impl.data.FuserLockingProxyStore;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.time.SystemClock;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFlightDataRemoverTest 
{
	private FuserClientStore<MatmFlight> store;
	private DataRemover<MatmFlight> remover;
	private Clock clock;
	
	@Before
	public void setup ()
	{
		store = new FuserLockingProxyStore<>(new FuserClientMatmFlightStore ());
		clock = new SystemClock ();
		remover = new MatmFlightDataRemover (store, clock);
	}
	
	@Test
	public void testExpiredRemover()
	{
		MatmFlight flight = new MatmFlight();
		flight.setGufi(UUID.randomUUID().toString());
		flight.setTimestamp(new Date(clock.getTimeInMillis() - (48 * TimeFactory.HOUR_IN_MILLIS)));
		
		store.add(flight);
		
		assertEquals (1, store.size());
		assertNotNull (store.get(flight.getGufi()));
		
		remover.remove();
		
		assertEquals (0, store.size());
		assertNull (store.get(flight.getGufi()));
		
        //test removing an arrived flight
        flight = new MatmFlight();
		flight.setGufi(UUID.randomUUID().toString());
        flight.setTimestamp(new Date(clock.getTimeInMillis() ));
        flight.setTimestamp(new Date(clock.getTimeInMillis() - (long)(2.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        flight.setArrivalRunwayActualTime( new Date(clock.getTimeInMillis() - (long)(2.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        store.add(flight);
        
		remover.remove();
		
		assertEquals (0, store.size());
		assertNull (store.get(flight.getGufi())); 

		
		 //test removing an active flight
        flight = new MatmFlight();
		flight.setGufi(UUID.randomUUID().toString());
        Position pos = new Position();
        pos.setTimestamp( new Date(clock.getTimeInMillis() - (long)(20.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        flight.setPosition( pos );
		
        store.add(flight);
        
		remover.remove();
		
		assertEquals (0, store.size());
		assertNull (store.get(flight.getGufi())); 


        //test removing an asdex-only flight
        flight = new MatmFlight();
		flight.setGufi(UUID.randomUUID().toString());
        flight.setLastUpdateSource( "ASDEX" );
        pos = new Position();
        pos.setTimestamp( new Date(clock.getTimeInMillis() - (long)(2.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        flight.setPosition( pos );
		
        store.add(flight);
        
		remover.remove();
		
		assertEquals (0, store.size());
		assertNull (store.get(flight.getGufi())); 

        
        //test removing a flight that never departed
        flight = new MatmFlight();
		flight.setGufi(UUID.randomUUID().toString());
        flight.setDepartureRunwayEstimatedTime( new Date(clock.getTimeInMillis() - (long)( 8.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
		
        store.add(flight);
        
		remover.remove();
		
		assertEquals (0, store.size());
		assertNull (store.get(flight.getGufi()));   
	}
	@Test
	public void testExpiration ()
	{
		assertEquals (0, store.size());
		
		store.add(createFlight(UUID.randomUUID().toString(), "TST111", getNegativeOffsetTime(0)));
		store.add(createFlight(UUID.randomUUID().toString(), "TST222", getNegativeOffsetTime(12)));
		store.add(createFlight(UUID.randomUUID().toString(), "TST333", getNegativeOffsetTime(24)));
		store.add(createFlight(UUID.randomUUID().toString(), "TST444", getNegativeOffsetTime(36)));
		store.add(createFlight(UUID.randomUUID().toString(), "TST555", getNegativeOffsetTime(48)));
		
		assertEquals (5, store.size());
		
		remover.remove();
		
		assertEquals (3, store.size());
	}
	
	
	@Test
	public void testRemover()
	{
		MatmFlight flight1 = new MatmFlight();
		flight1.setGufi(UUID.randomUUID().toString());
		flight1.setTimestamp(new Date(clock.getTimeInMillis() - (48 * TimeFactory.HOUR_IN_MILLIS)));
		store.add(flight1);
		
		MatmFlight flight2 = new MatmFlight();
		flight2.setGufi(UUID.randomUUID().toString());
		flight2.setArrivalRunwayActualTime( new Date(clock.getTimeInMillis() - (long)(3 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
		flight2.setTimestamp(new Date(clock.getTimeInMillis() - (3 * TimeFactory.HOUR_IN_MILLIS)));
		store.add(flight2);
		
		MatmFlight flight3 = new MatmFlight();
		flight3.setGufi(UUID.randomUUID().toString());
        flight3.setDepartureRunwayEstimatedTime( new Date(clock.getTimeInMillis() - (long)( 9 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        store.add(flight3);
		
        MatmFlight flight4 = new MatmFlight();
		flight4.setGufi(UUID.randomUUID().toString());
        Position pos = new Position();
        pos.setTimestamp( new Date(clock.getTimeInMillis() - (long)(20.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        flight4.setPosition( pos );
        store.add(flight4);
        
		remover.remove();
		
		assertEquals (0, store.size());
		assertNull (store.get(flight1.getGufi())); 
		
		MatmFlight flight5 = new MatmFlight();
		flight5.setGufi(UUID.randomUUID().toString());
        pos.setTimestamp( new Date(clock.getTimeInMillis() - (long)(1 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        store.add(flight5);
        
        remover.remove();
        
        assertEquals(1, store.size());
        assertNotNull (store.get(flight5.getGufi()));
        
	}
	
	@Test
	public void testExpirationReceiveEvents ()
	{
		MockEventHandler handler = new MockEventHandler ();
		
		((MatmFlightDataRemover)remover).setReceivedEventListener(handler);
		((MatmFlightDataRemover)remover).setProcessedEventListener(handler);
		
		assertEquals (0, store.size());
		
		store.add(createFlight(UUID.randomUUID().toString(), "TST111", getNegativeOffsetTime(0)));
		store.add(createFlight(UUID.randomUUID().toString(), "TST222", getNegativeOffsetTime(12)));
		store.add(createFlight(UUID.randomUUID().toString(), "TST333", getNegativeOffsetTime(24)));
		store.add(createFlight(UUID.randomUUID().toString(), "TST444", getNegativeOffsetTime(36)));
		store.add(createFlight(UUID.randomUUID().toString(), "TST555", getNegativeOffsetTime(48)));
		
		assertEquals (5, store.size());
		
		remover.remove();
		
		assertEquals (3, store.size());
		
		assertEquals (2, handler.getNotifyEvents().size());
		assertEquals (2, handler.getStoreEvents().size());
	}
	
	private Date getNegativeOffsetTime (int negativeOffsetHours)
	{
		long offset = negativeOffsetHours * 60L * 60L * 1000L;
		return new Date (System.currentTimeMillis() - offset);
	}
	
	private MatmFlight createFlight (String gufi, String acid, Date timestamp)
	{
		MatmFlight flight = new MatmFlight();
		flight.setGufi(gufi);
		flight.setAcid(acid);
		flight.setTimestamp(timestamp);
		
		return flight;
	}
	
	private class MockEventHandler
	implements FuserProcessedEventListener<MatmFlight>, FuserReceivedEventListener<MatmFlight>
	{
		private List<MatmFlight> storeEvents = new ArrayList<> ();
		private List<MatmFlight> notifyEvents = new ArrayList<> ();
		
		@Override
		public void receivedAdd(MatmFlight data)
		{
			// do nothing
		}

		@Override
		public void receivedUpdate(MatmFlight existingBeforeUpdating, MatmFlight data)
		{
			// do nothing
		}

		@Override
		public void receivedRemove(MatmFlight data)
		{
			notifyEvents.add(data);
		}

		@Override
		public void dataAdded(MatmFlight data)
		{
			// do nothing
		}

		@Override
		public void dataUpdated(MatmFlight data, MatmFlight update)
		{
			// do nothing
		}

		@Override
		public void dataRemoved(MatmFlight data)
		{
			storeEvents.add(data);
		}
		
		public List<MatmFlight> getStoreEvents ()
		{
			return storeEvents;
		}
		
		public List<MatmFlight> getNotifyEvents ()
		{
			return notifyEvents;
		}
		
	}
}
