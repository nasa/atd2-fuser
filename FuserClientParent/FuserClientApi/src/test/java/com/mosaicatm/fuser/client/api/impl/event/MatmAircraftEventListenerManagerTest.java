package com.mosaicatm.fuser.client.api.impl.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class MatmAircraftEventListenerManagerTest 
{
	private GenericFuserProcessedEventManager<MatmAircraft> manager;
	
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
		
		MatmAircraft addAircraft = createAircraft ();
		MatmAircraft updateAircraft = createAircraft ();
		MatmAircraft removeAircraft = createAircraft ();
		
		// the FuserMatmFlightEventListenerManager also implements the event
		// listener interface
		
		manager.dataAdded(addAircraft);
		manager.dataUpdated(updateAircraft, updateAircraft);
		manager.dataRemoved(removeAircraft);
		
		MatmAircraft added = listener.getAdded();
		MatmAircraft updated = listener.getUpdated();
		MatmAircraft removed = listener.getRemoved();
		
		assertNotNull (added);
		assertNotNull (updated);
		assertNotNull (removed);
		
		assertEquals (addAircraft.getRegistration(), added.getRegistration());
		assertEquals (updateAircraft.getRegistration(), updated.getRegistration());
		assertEquals (removeAircraft.getRegistration(), removed.getRegistration());
	}
	
	@Test
	public void unregisterTest ()
	{
		MockEventListener listener = new MockEventListener ();
		manager.registerListener(listener);
		
		MatmAircraft addAircraft = createAircraft ();
		MatmAircraft updateAircraft = createAircraft ();
		MatmAircraft removeAircraft = createAircraft ();
		
		// the FuserMatmFlightEventListenerManager also implements the event
		// listener interface
		manager.dataAdded(addAircraft);
		manager.dataUpdated(updateAircraft, updateAircraft);
		manager.dataRemoved(removeAircraft);
		
		assertNotNull (listener.getAdded());
		assertNotNull (listener.getUpdated());
		assertNotNull (listener.getRemoved());
		
		listener.reset();
		
		assertNull (listener.getAdded());
		assertNull (listener.getUpdated());
		assertNull (listener.getRemoved());
		
		manager.unregisterListener(listener);
		
		addAircraft = createAircraft ();
		updateAircraft = createAircraft ();
		removeAircraft = createAircraft ();
		
		manager.dataAdded(addAircraft);
		manager.dataUpdated(updateAircraft, updateAircraft);
		manager.dataRemoved(removeAircraft);
		
		assertNull (listener.getAdded());
		assertNull (listener.getUpdated());
		assertNull (listener.getRemoved());		
	}
	
	private MatmAircraft createAircraft ()
	{
	    MatmAircraft aircraft = new MatmAircraft();
	    aircraft.setRegistration(UUID.randomUUID().toString());
		return aircraft;
	}
	
	private class MockEventListener
	implements FuserProcessedEventListener<MatmAircraft>
	{
		private MatmAircraft added;
		private MatmAircraft updated;
		private MatmAircraft removed;

		@Override
		public void dataAdded(MatmAircraft data)
		{
			added = data;
		}

		@Override
		public void dataUpdated(MatmAircraft data, MatmAircraft update)
		{
			updated = data;
		}

		@Override
		public void dataRemoved(MatmAircraft data) 
		{
			removed = data;
		}
		
		public MatmAircraft getAdded ()
		{
			return added;
		}
		
		public MatmAircraft getUpdated ()
		{
			return updated;
		}
		
		public MatmAircraft getRemoved ()
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
