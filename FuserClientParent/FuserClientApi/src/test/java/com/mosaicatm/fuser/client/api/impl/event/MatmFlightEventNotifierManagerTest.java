package com.mosaicatm.fuser.client.api.impl.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventManager;
import com.mosaicatm.fuser.client.api.impl.event.GenericFuserReceivedEventManager;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFlightEventNotifierManagerTest 
{
	private GenericFuserReceivedEventManager<MatmFlight> manager;

	@Before
	public void setup() {
		manager = new GenericFuserReceivedEventManager<>();
	}

	@Test
	public void registerTest() 
	{
		MockEventNotifier notifier = new MockEventNotifier();
		manager.registerListener(notifier);

		MatmFlight addFlight = createFlight();
		MatmFlight updateFlight = createFlight();
		MatmFlight removeFlight = createFlight();

		// the FuserMatmFlightEventNotifierManager also implements the event
		// notifier interface
		manager.receivedAdd(addFlight);
		manager.receivedUpdate(addFlight,updateFlight);
		manager.receivedRemove(removeFlight);

		MatmFlight added = notifier.getAdded();
		MatmFlight updated = notifier.getUpdated();
		MatmFlight removed = notifier.getRemoved();

		assertNotNull(added);
		assertNotNull(updated);
		assertNotNull(removed);

		assertEquals(addFlight.getGufi(), added.getGufi());
		assertEquals(updateFlight.getGufi(), updated.getGufi());
		assertEquals(removeFlight.getGufi(), removed.getGufi());
	}

	@Test
	public void unregisterTest() 
	{
		MockEventNotifier listener = new MockEventNotifier();
		manager.registerListener(listener);

		MatmFlight addFlight = createFlight();
		MatmFlight updateFlight = createFlight();
		MatmFlight removeFlight = createFlight();

		// the FuserMatmFlightEventNotifierManager also implements the event
		// notifier interface
		manager.receivedAdd(addFlight);
		manager.receivedUpdate(addFlight,updateFlight);
		manager.receivedRemove(removeFlight);

		assertNotNull(listener.getAdded());
		assertNotNull(listener.getUpdated());
		assertNotNull(listener.getRemoved());

		listener.reset();

		assertNull(listener.getAdded());
		assertNull(listener.getUpdated());
		assertNull(listener.getRemoved());

		manager.unregisterListener(listener);

		addFlight = createFlight();
		updateFlight = createFlight();
		removeFlight = createFlight();

		manager.receivedAdd(addFlight);
		manager.receivedUpdate(addFlight,updateFlight);
		manager.receivedRemove(removeFlight);

		assertNull(listener.getAdded());
		assertNull(listener.getUpdated());
		assertNull(listener.getRemoved());
	}

	private MatmFlight createFlight()
	{
		MatmFlight flight = new MatmFlight();
		flight.setGufi(UUID.randomUUID().toString());
		return flight;
	}

	private class MockEventNotifier 
	implements FuserReceivedEventListener<MatmFlight> 
	{
		private MatmFlight added;
		private MatmFlight updated;
		private MatmFlight removed;

		@Override
		public void receivedAdd(MatmFlight data) 
		{
			added = data;
		}

		@Override
		public void receivedUpdate(MatmFlight existingBeforeUpdating, MatmFlight data) 
		{
			updated = data;
		}

		@Override
		public void receivedRemove(MatmFlight data) 
		{
			removed = data;
		}

		public MatmFlight getAdded() 
		{
			return added;
		}

		public MatmFlight getUpdated() 
		{
			return updated;
		}

		public MatmFlight getRemoved() 
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
