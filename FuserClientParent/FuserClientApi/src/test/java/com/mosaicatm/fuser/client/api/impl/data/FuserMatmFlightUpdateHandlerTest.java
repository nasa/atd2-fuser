package com.mosaicatm.fuser.client.api.impl.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.client.api.FuserClientApi;
import com.mosaicatm.fuser.client.api.data.DataAdder;
import com.mosaicatm.fuser.client.api.data.DataRemover;
import com.mosaicatm.fuser.client.api.data.DataUpdater;
import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.data.FuserUpdateHandler;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventManager;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventManager;
import com.mosaicatm.fuser.client.api.impl.FuserMatmFlightClientApi;
import com.mosaicatm.fuser.client.api.impl.data.GenericDataAdder;
import com.mosaicatm.fuser.client.api.impl.data.MatmFlightDataRemover;
import com.mosaicatm.fuser.client.api.impl.data.GenericDataUpdater;
import com.mosaicatm.fuser.client.api.impl.event.GenericFuserProcessedEventManager;
import com.mosaicatm.fuser.client.api.impl.event.GenericFuserReceivedEventManager;
import com.mosaicatm.fuser.client.api.impl.data.FuserClientMatmFlightStore;
import com.mosaicatm.fuser.client.api.impl.data.FuserLockingProxyStore;
import com.mosaicatm.fuser.client.api.impl.data.GenericFuserUpdateHandler;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class FuserMatmFlightUpdateHandlerTest
{
	private FuserClientApi<MatmFlight> api;
	private FuserClientStore<MatmFlight> store;
	
	private FuserUpdateHandler<MatmFlight> handler;
		
	private MockOmniListener listener;
	
	@Before
	public void setup ()
	{
		store = new FuserLockingProxyStore<>(new FuserClientMatmFlightStore ());
		listener = new MockOmniListener ();
		
		FuserProcessedEventManager<MatmFlight> listenerManager = new GenericFuserProcessedEventManager();
		FuserProcessedEventListener<MatmFlight> eventListener = (FuserProcessedEventListener<MatmFlight>)listenerManager;
		FuserReceivedEventManager<MatmFlight> notifierManager = new GenericFuserReceivedEventManager();
		FuserReceivedEventListener<MatmFlight> eventNotifier = (FuserReceivedEventListener<MatmFlight>)notifierManager;
		
		api = new FuserMatmFlightClientApi ();
		((FuserMatmFlightClientApi)api).setStore(store);
		((FuserMatmFlightClientApi)api).setProcessedEventManager(listenerManager);
		((FuserMatmFlightClientApi)api).setReceivedEventManager(notifierManager);
		
		
		DataAdder<MatmFlight> adder = new GenericDataAdder (store);
		adder.setProcessedEventListener(eventListener);
		adder.setReceivedEventListener(eventNotifier);
		
		DataUpdater<MatmFlight> updater = new GenericDataUpdater (store);
		updater.setProcessedEventListener(eventListener);
		updater.setReceivedEventListener(eventNotifier);
		
		DataRemover<MatmFlight> remover = new MatmFlightDataRemover (store);
		remover.setProcessedEventListener(eventListener);
		remover.setReceivedEventListener(eventNotifier);
		
		((FuserMatmFlightClientApi)api).setDataAdder (adder);
		((FuserMatmFlightClientApi)api).setDataUpdater (updater);
		((FuserMatmFlightClientApi)api).setDataRemover (remover);
		
		api.registerProcessedEventListener(listener);
		api.registerReceivedEventListener(listener);
		
		handler = new GenericFuserUpdateHandler ();
		((GenericFuserUpdateHandler)handler).setFuserClientApi(api);
	}
	
	@Test
	public void testSingleAdd ()
	{
		assertEquals (0, store.size());
		
		MatmFlight flight = createFlight (UUID.randomUUID().toString(), "TST123", "IAD", "CLT");
		
		handler.handleUpdate(flight);
		
		assertEquals (1, store.size());
		
		MatmFlight storeFlight = store.get(flight.getGufi());
		assertNotNull (storeFlight);
		assertEquals (flight.getGufi(), storeFlight.getGufi());
		assertEquals (flight.getAcid(), storeFlight.getAcid());
		assertEquals (flight.getArrivalAerodrome().getIataName(), storeFlight.getArrivalAerodrome().getIataName());
		assertEquals (flight.getDepartureAerodrome().getIataName(), storeFlight.getDepartureAerodrome().getIataName());
		
		assertEquals (1, listener.getNotifyAdds().size());
		assertEquals (1, listener.getEventAdds().size());
		assertEquals (0, listener.getNotifyUpdates().size());
		assertEquals (0, listener.getEventUpdates().size());
	}
	
	@Test
	public void testSingleUpdate ()
	{
		assertEquals (0, store.size());
		
		String gufi = UUID.randomUUID().toString();
		MatmFlight target = createFlight (gufi, "TST123", null, null);
		MatmFlight update = createFlight (gufi, "TST123", "MSP", "STL");
		
		handler.handleUpdate(target);
		handler.handleUpdate(update);
		
		assertEquals (1, store.size());
		
		MatmFlight storeFlight = store.get(gufi);
		assertNotNull (storeFlight);
		assertEquals (gufi, storeFlight.getGufi());
		assertEquals (target.getAcid(), storeFlight.getAcid());
		assertEquals (update.getArrivalAerodrome().getIataName(), storeFlight.getArrivalAerodrome().getIataName());
		assertEquals (update.getDepartureAerodrome().getIataName(), storeFlight.getDepartureAerodrome().getIataName());
		
		assertEquals (1, listener.getNotifyAdds().size());
		assertEquals (1, listener.getEventAdds().size());
		assertEquals (1, listener.getNotifyUpdates().size());
		assertEquals (1, listener.getEventUpdates().size());
	}
	
	@Test
	public void testMultipleAdds ()
	{
		assertEquals (0, store.size());
		
		MatmFlight flightA = createFlight (UUID.randomUUID().toString(), "TST123", "IAD", "CLT");
		MatmFlight flightB = createFlight (UUID.randomUUID().toString(), "TST546", "MCO", "ORD");
		MatmFlight flightC = createFlight (UUID.randomUUID().toString(), "TST789", "DEN", "BOS");
		
		handler.handleUpdate(flightA);
		handler.handleUpdate(flightB);
		handler.handleUpdate(flightC);
		
		assertEquals (3, store.size());
		
		MatmFlight storeFlightA = store.get(flightA.getGufi());
		assertNotNull (storeFlightA);
		assertEquals (flightA.getGufi(), storeFlightA.getGufi());
		assertEquals (flightA.getAcid(), storeFlightA.getAcid());
		assertEquals (flightA.getArrivalAerodrome().getIataName(), storeFlightA.getArrivalAerodrome().getIataName());
		assertEquals (flightA.getDepartureAerodrome().getIataName(), storeFlightA.getDepartureAerodrome().getIataName());
		
		MatmFlight storeFlightB = store.get(flightB.getGufi());
		assertNotNull (storeFlightB);
		assertEquals (flightB.getGufi(), storeFlightB.getGufi());
		assertEquals (flightB.getAcid(), storeFlightB.getAcid());
		assertEquals (flightB.getArrivalAerodrome().getIataName(), storeFlightB.getArrivalAerodrome().getIataName());
		assertEquals (flightB.getDepartureAerodrome().getIataName(), storeFlightB.getDepartureAerodrome().getIataName());
		
		MatmFlight storeFlightC = store.get(flightC.getGufi());
		assertNotNull (storeFlightC);
		assertEquals (flightC.getGufi(), storeFlightC.getGufi());
		assertEquals (flightC.getAcid(), storeFlightC.getAcid());
		assertEquals (flightC.getArrivalAerodrome().getIataName(), storeFlightC.getArrivalAerodrome().getIataName());
		assertEquals (flightC.getDepartureAerodrome().getIataName(), storeFlightC.getDepartureAerodrome().getIataName());
		
		assertEquals (3, listener.getNotifyAdds().size());
		assertEquals (3, listener.getEventAdds().size());
		assertEquals (0, listener.getNotifyUpdates().size());
		assertEquals (0, listener.getEventUpdates().size());
	}
	
	@Test
	public void testMultipleUpdates ()
	{
		assertEquals (0, store.size());
		
		String gufi = UUID.randomUUID().toString();
		MatmFlight target = createFlight (gufi, "TST123", null, null);
		MatmFlight updateA = createFlight (gufi, "TST456", null, null);
		MatmFlight updateB = createFlight (gufi, null, "MSP", null);
		MatmFlight updateC = createFlight (gufi, null, null, "STL");
		
		handler.handleUpdate(target);
		handler.handleUpdate(updateA);
		handler.handleUpdate(updateB);
		handler.handleUpdate(updateC);
		
		assertEquals (1, store.size());
		
		MatmFlight storeFlight = store.get(gufi);
		assertNotNull (storeFlight);
		assertEquals (gufi, storeFlight.getGufi());
		assertEquals (updateA.getAcid(), storeFlight.getAcid());
		assertEquals (updateB.getArrivalAerodrome().getIataName(), storeFlight.getArrivalAerodrome().getIataName());
		assertEquals (updateC.getDepartureAerodrome().getIataName(), storeFlight.getDepartureAerodrome().getIataName());
		
		assertEquals (1, listener.getNotifyAdds().size());
		assertEquals (1, listener.getEventAdds().size());
		assertEquals (3, listener.getNotifyUpdates().size());
		assertEquals (3, listener.getEventUpdates().size());
	}
	
	private MatmFlight createFlight (String gufi, String acid, String apt, String dap)
	{
		MatmFlight flight = new MatmFlight();
		flight.setGufi(gufi);
		flight.setAcid(acid);
		Aerodrome arrivalAerodrome = new Aerodrome();
		arrivalAerodrome.setIataName( apt );
		flight.setArrivalAerodrome( arrivalAerodrome );
		Aerodrome departureAerodrome = new Aerodrome();
		departureAerodrome.setIataName( dap );
		flight.setDepartureAerodrome( departureAerodrome );
		
		return flight;
	}
	
	private class MockOmniListener
	implements FuserProcessedEventListener<MatmFlight>, FuserReceivedEventListener<MatmFlight>
	{
		private List<MatmFlight> notifyAdds = new ArrayList<> ();
		private List<MatmFlight> notifyUpdates = new ArrayList<> ();
		
		private List<MatmFlight> eventAdds = new ArrayList<> ();
		private List<MatmFlight> eventUpdates = new ArrayList<> ();

		@Override
		public void receivedAdd(MatmFlight data) 
		{
			notifyAdds.add(data);
		}

		@Override
		public void receivedUpdate(MatmFlight existingBeforeUpdating, MatmFlight data) 
		{
			notifyUpdates.add(data);
		}

		@Override
		public void receivedRemove(MatmFlight data) 
		{
			// do nothing
		}

		@Override
		public void dataAdded(MatmFlight data) 
		{
			eventAdds.add(data);
		}

		@Override
		public void dataUpdated(MatmFlight data, MatmFlight update)
		{
			eventUpdates.add(data);
		}

		@Override
		public void dataRemoved(MatmFlight data)
		{
			// do nothing
		}
		
		public List<MatmFlight> getNotifyAdds ()
		{
			return notifyAdds;
		}
		
		public List<MatmFlight> getNotifyUpdates ()
		{
			return notifyUpdates;
		}

		public List<MatmFlight> getEventAdds ()
		{
			return eventAdds;
		}
		
		public List<MatmFlight> getEventUpdates()
		{
			return eventUpdates;
		}
	}
}
