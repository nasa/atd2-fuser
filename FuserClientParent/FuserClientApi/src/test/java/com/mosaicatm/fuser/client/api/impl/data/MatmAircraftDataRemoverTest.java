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
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.time.SystemClock;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmAircraftDataRemoverTest 
{
	private FuserClientStore<MatmAircraft> store;
	private DataRemover<MatmAircraft> remover;
	private Clock clock;
	
	@Before
	public void setup ()
	{
		store = new FuserLockingProxyStore<>(new FuserClientMatmAircraftStore ());
		clock = new SystemClock ();
		remover = new MatmAircraftDataRemover (store, clock);
	}
	
	@Test
	public void testExpiredRemover()
	{
        
        assertEquals (0, store.size());
        
        store.add(createAircraft(UUID.randomUUID().toString()));
        store.add(createAircraft(UUID.randomUUID().toString()));
        store.add(createAircraft(UUID.randomUUID().toString()));
        store.add(createAircraft(UUID.randomUUID().toString()));
        store.add(createAircraft(UUID.randomUUID().toString()));
        
        assertEquals (5, store.size());

        remover.remove();
        
        // aircraft are not configured to expire so change should be reflected
        assertEquals (5, store.size());   
	}
	
	@Test
	public void testExpirationReceiveEvents ()
	{
		MockEventHandler handler = new MockEventHandler ();
		
		remover.setReceivedEventListener(handler);
		remover.setProcessedEventListener(handler);
		
		assertEquals (0, store.size());
		
		store.add(createAircraft(UUID.randomUUID().toString()));
		store.add(createAircraft(UUID.randomUUID().toString()));
		store.add(createAircraft(UUID.randomUUID().toString()));
		store.add(createAircraft(UUID.randomUUID().toString()));
		store.add(createAircraft(UUID.randomUUID().toString()));
		
		assertEquals (5, store.size());
		
		for (MatmAircraft aircraft : store.getAll())
		{
		    remover.remove(aircraft);
		}
		
		assertEquals (0, store.size());
		
		assertEquals (5, handler.getNotifyEvents().size());
		assertEquals (5, handler.getStoreEvents().size());
	}
	
	private MatmAircraft createAircraft (String tail)
	{
	    MatmAircraft aircraft = new MatmAircraft();
		aircraft.setRegistration(tail);
		
		return aircraft;
	}
	
	private class MockEventHandler
	implements FuserProcessedEventListener<MatmAircraft>, 
	           FuserReceivedEventListener<MatmAircraft>
	{
		private List<MatmAircraft> storeEvents = new ArrayList<> ();
		private List<MatmAircraft> notifyEvents = new ArrayList<> ();
		
		@Override
		public void receivedAdd(MatmAircraft data)
		{
			// do nothing
		}

		@Override
		public void receivedUpdate(MatmAircraft existingBeforeUpdating, MatmAircraft data)
		{
			// do nothing
		}

		@Override
		public void receivedRemove(MatmAircraft data)
		{
			notifyEvents.add(data);
		}

		@Override
		public void dataAdded(MatmAircraft data)
		{
			// do nothing
		}

		@Override
		public void dataUpdated(MatmAircraft data, MatmAircraft update)
		{
			// do nothing
		}

		@Override
		public void dataRemoved(MatmAircraft data)
		{
			storeEvents.add(data);
		}
		
		public List<MatmAircraft> getStoreEvents ()
		{
			return storeEvents;
		}
		
		public List<MatmAircraft> getNotifyEvents ()
		{
			return notifyEvents;
		}
	}
}
