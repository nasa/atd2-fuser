package com.mosaicatm.fuser.client.api.impl.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventManager;
import com.mosaicatm.fuser.client.api.impl.event.GenericFuserProcessedEventManager;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFlightEventListenerManagerTest 
{
	private GenericFuserProcessedEventManager<MatmFlight> manager;
	
	@Before
	public void setup ()
	{
		manager = new GenericFuserProcessedEventManager<> ();
	}
	
	@Test
	public void registerTest ()
	{
		MockEventListener listener = new MockEventListener ();
		manager.registerListener(listener);
		
		MatmFlight addFlight = createFlight ();
		MatmFlight updateFlight = createFlight ();
		MatmFlight removeFlight = createFlight ();
		
		// the FuserMatmFlightEventListenerManager also implements the event
		// listener interface
		
		manager.dataAdded(addFlight);
		manager.dataUpdated(updateFlight, updateFlight);
		manager.dataRemoved(removeFlight);
		
		MatmFlight added = listener.getAdded();
		MatmFlight updated = listener.getUpdated();
		MatmFlight removed = listener.getRemoved();
		
		assertNotNull (added);
		assertNotNull (updated);
		assertNotNull (removed);
		
		assertEquals (addFlight.getGufi(), added.getGufi());
		assertEquals (updateFlight.getGufi(), updated.getGufi());
		assertEquals (removeFlight.getGufi(), removed.getGufi());
	}
	
	@Test
	public void unregisterTest ()
	{
		MockEventListener listener = new MockEventListener ();
		manager.registerListener(listener);
		
		MatmFlight addFlight = createFlight ();
		MatmFlight updateFlight = createFlight ();
		MatmFlight removeFlight = createFlight ();
		
		// the FuserMatmFlightEventListenerManager also implements the event
		// listener interface
		manager.dataAdded(addFlight);
		manager.dataUpdated(updateFlight, updateFlight);
		manager.dataRemoved(removeFlight);
		
		assertNotNull (listener.getAdded());
		assertNotNull (listener.getUpdated());
		assertNotNull (listener.getRemoved());
		
		listener.reset();
		
		assertNull (listener.getAdded());
		assertNull (listener.getUpdated());
		assertNull (listener.getRemoved());
		
		manager.unregisterListener(listener);
		
		addFlight = createFlight ();
		updateFlight = createFlight ();
		removeFlight = createFlight ();
		
		manager.dataAdded(addFlight);
		manager.dataUpdated(updateFlight, updateFlight);
		manager.dataRemoved(removeFlight);
		
		assertNull (listener.getAdded());
		assertNull (listener.getUpdated());
		assertNull (listener.getRemoved());		
	}
	
	private MatmFlight createFlight ()
	{
		MatmFlight flight = new MatmFlight();
		flight.setGufi(UUID.randomUUID().toString());
		return flight;
	}
	
	private class MockEventListener
	implements FuserProcessedEventListener<MatmFlight>
	{
		private MatmFlight added;
		private MatmFlight updated;
		private MatmFlight removed;

		@Override
		public void dataAdded(MatmFlight data)
		{
			added = data;
		}

		@Override
		public void dataUpdated(MatmFlight data, MatmFlight update)
		{
			updated = data;
		}

		@Override
		public void dataRemoved(MatmFlight data) 
		{
			removed = data;
		}
		
		public MatmFlight getAdded ()
		{
			return added;
		}
		
		public MatmFlight getUpdated ()
		{
			return updated;
		}
		
		public MatmFlight getRemoved ()
		{
			return removed;
		}
		
		public void reset ()
		{
			added = null;
			updated = null;
			removed = null;
		}
	}
}
