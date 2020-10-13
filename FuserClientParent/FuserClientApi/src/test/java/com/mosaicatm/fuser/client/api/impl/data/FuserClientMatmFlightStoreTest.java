package com.mosaicatm.fuser.client.api.impl.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.impl.data.FuserClientMatmFlightStore;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class FuserClientMatmFlightStoreTest 
{
	private FuserClientStore<MatmFlight> store;
	
	@Before
	public void setup ()
	{
		store = new FuserClientMatmFlightStore ();
	}
	
	@Test
	public void testAdd ()
	{
		assertEquals (0, store.size());
		
		MatmFlight flight = createFlight (UUID.randomUUID().toString(), "TST123");
		store.add(flight);
		
		assertEquals (1, store.size());
	}
	
	@Test
	public void testUpdate ()
	{
		assertEquals (0, store.size());
		
		String key = UUID.randomUUID().toString();
		
		MatmFlight flight = createFlight (key, "TST123");
		store.add (flight);
		
		MatmFlight update = createFlight (key, "TST456");
		store.update(update);
		
		assertEquals (1, store.size());
		
		MatmFlight storeFlight = store.get(key);
		
		assertNotNull (update.getAcid());
		assertNotNull (storeFlight.getAcid());
		assertEquals (update.getAcid(), storeFlight.getAcid());
	}
	
	@Test
	public void testRemove ()
	{
		assertEquals (0, store.size());
		
		MatmFlight flight = createFlight (UUID.randomUUID().toString(), "TST789");
		store.add(flight);
		
		assertEquals (1, store.size());
		
		store.remove(flight);
		
		assertEquals (0, store.size());
	}
	
	@Test
	public void testRemoveByKey ()
	{
		assertEquals (0, store.size());
		
		MatmFlight flight = createFlight (UUID.randomUUID().toString(), "TST678");
		store.add(flight);
		
		assertEquals (1, store.size());
		
		store.removeByKey(flight.getGufi());
		
		assertEquals (0, store.size());
	}
	
	@Test
	public void testGet ()
	{
		assertEquals (0, store.size());
		
		MatmFlight flight = createFlight (UUID.randomUUID().toString(), "TST195");
		store.add(flight);
		
		assertEquals (1, store.size());
		
		MatmFlight storeFlight = store.get(flight.getGufi());
		
		assertEquals (flight.getGufi(), storeFlight.getGufi());
		assertEquals (flight.getAcid(), storeFlight.getAcid());
		
	}
	
	@Test
	public void testGetAll ()
	{
		assertEquals (0, store.size());
		
		MatmFlight flightA = createFlight (UUID.randomUUID().toString(), "TST111");
		MatmFlight flightB = createFlight (UUID.randomUUID().toString(), "TST222");
		MatmFlight flightC = createFlight (UUID.randomUUID().toString(), "TST333");
		
		store.add(flightA);
		store.add(flightB);
		store.add(flightC);
		
		assertEquals (3, store.size());
		
		List<MatmFlight> flightList = store.getAll();
		assertNotNull (flightList);
		
		assertEquals (store.size(), flightList.size());
	}
	
	@Test
	public void testSize ()
	{
		assertEquals (0, store.size());
		
		MatmFlight flightA = createFlight (UUID.randomUUID().toString(), "TST111");
		MatmFlight flightB = createFlight (UUID.randomUUID().toString(), "TST222");
		MatmFlight flightC = createFlight (UUID.randomUUID().toString(), "TST333");
		
		store.add(flightA);
		store.add(flightB);
		store.add(flightC);
		
		assertEquals (3, store.size());
	}
	
	@Test
	public void testClear ()
	{
		assertEquals (0, store.size());
		
		MatmFlight flightA = createFlight (UUID.randomUUID().toString(), "TST111");
		MatmFlight flightB = createFlight (UUID.randomUUID().toString(), "TST222");
		MatmFlight flightC = createFlight (UUID.randomUUID().toString(), "TST333");
		
		store.add(flightA);
		store.add(flightB);
		store.add(flightC);
		
		assertEquals (3, store.size());
		
		store.clear();
		
		assertEquals (0, store.size());
	}
	
	@Test
	public void testLocking ()
	{
		assertEquals (0, store.size());
		
		Thread addThread = new Thread (new AddRunnable("TST007"));
		
		// lock the store so the thread won't be able to obtain the lock
		store.lock();
		addThread.start();
		
		// give sufficient time for the thread to run
		sleep(2000);
		
		// this thread still has the lock, so the size should be 0
		assertEquals (0, store.size());
		
		// unlock the store so the thread can obtain the lock
		store.unlock();
		sleep(2000);
		
		// the thread should have completed running and added a flight
		assertEquals (1, store.size());
	}
	
	private void sleep (long millis)
	{
		try
		{
			// give sufficient time for the thread to run
			Thread.sleep(millis);
		}
		catch (InterruptedException ie)
		{
			ie.printStackTrace();
			fail (ie.getMessage());
		}
	}
	
	private MatmFlight createFlight (String gufi, String acid)
	{
		MatmFlight flat = new MatmFlight();
		flat.setGufi(gufi);
		flat.setAcid(acid);
		
		return flat;		
	}
	
	private class AddRunnable
	implements Runnable
	{
		private String acid;
		
		public AddRunnable (String acid)
		{
			this.acid = acid;
			
		}
		@Override
		public void run ()
		{
			MatmFlight flight = createFlight (UUID.randomUUID().toString(), acid);
			
			store.lock();
			store.add(flight);
			store.unlock();
		}
	}
}
