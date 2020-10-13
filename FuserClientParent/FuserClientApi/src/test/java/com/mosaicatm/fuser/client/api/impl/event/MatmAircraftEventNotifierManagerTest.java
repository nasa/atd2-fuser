package com.mosaicatm.fuser.client.api.impl.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class MatmAircraftEventNotifierManagerTest 
{
	private GenericFuserReceivedEventManager<MatmAircraft> manager;

	@Before
	public void setup() {
		manager = new GenericFuserReceivedEventManager<>();
	}

	@Test
	public void registerTest() 
	{
		MockEventNotifier notifier = new MockEventNotifier();
		manager.registerListener(notifier);

		MatmAircraft addAircraft = createAircraft();
		MatmAircraft updateAircraft = createAircraft();
		MatmAircraft removeAircraft = createAircraft();

		// the FuserMatmAircraftEventNotifierManager also implements the event
		// notifier interface
		manager.receivedAdd(addAircraft);
		manager.receivedUpdate(addAircraft,updateAircraft);
		manager.receivedRemove(removeAircraft);

		MatmAircraft added = notifier.getAdded();
		MatmAircraft updated = notifier.getUpdated();
		MatmAircraft removed = notifier.getRemoved();

		assertNotNull(added);
		assertNotNull(updated);
		assertNotNull(removed);

		assertEquals(addAircraft.getRegistration(), added.getRegistration());
		assertEquals(updateAircraft.getRegistration(), updated.getRegistration());
		assertEquals(removeAircraft.getRegistration(), removed.getRegistration());
	}

	@Test
	public void unregisterTest() 
	{
		MockEventNotifier listener = new MockEventNotifier();
		manager.registerListener(listener);

		MatmAircraft addAircraft = createAircraft();
		MatmAircraft updateAircraft = createAircraft();
		MatmAircraft removeAircraft = createAircraft();

		// the FuserMatmAircraftEventNotifierManager also implements the event
		// notifier interface
		manager.receivedAdd(addAircraft);
		manager.receivedUpdate(addAircraft,updateAircraft);
		manager.receivedRemove(removeAircraft);

		assertNotNull(listener.getAdded());
		assertNotNull(listener.getUpdated());
		assertNotNull(listener.getRemoved());

		listener.reset();

		assertNull(listener.getAdded());
		assertNull(listener.getUpdated());
		assertNull(listener.getRemoved());

		manager.unregisterListener(listener);

		addAircraft = createAircraft();
		updateAircraft = createAircraft();
		removeAircraft = createAircraft();

		manager.receivedAdd(addAircraft);
		manager.receivedUpdate(addAircraft,updateAircraft);
		manager.receivedRemove(removeAircraft);

		assertNull(listener.getAdded());
		assertNull(listener.getUpdated());
		assertNull(listener.getRemoved());
	}

	private MatmAircraft createAircraft()
	{
		MatmAircraft aircraft = new MatmAircraft();
		aircraft.setRegistration(UUID.randomUUID().toString());
		return aircraft;
	}

	private class MockEventNotifier 
	implements FuserReceivedEventListener<MatmAircraft> 
	{
		private MatmAircraft added;
		private MatmAircraft updated;
		private MatmAircraft removed;

		@Override
		public void receivedAdd(MatmAircraft data) 
		{
			added = data;
		}

		@Override
		public void receivedUpdate(MatmAircraft existingBeforeUpdating, MatmAircraft data) 
		{
			updated = data;
		}

		@Override
		public void receivedRemove(MatmAircraft data) 
		{
			removed = data;
		}

		public MatmAircraft getAdded() 
		{
			return added;
		}

		public MatmAircraft getUpdated() 
		{
			return updated;
		}

		public MatmAircraft getRemoved() 
		{
			return removed;
		}

		public void reset() 
		{
			added = null;
			updated = null;
			removed = null;
		}
	}
}
