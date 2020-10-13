package com.mosaicatm.fuser.client.api.impl.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.client.api.data.DataRemover;
import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.impl.data.MatmFlightDataRemover;
import com.mosaicatm.fuser.client.api.impl.data.FuserClientMatmFlightStore;
import com.mosaicatm.fuser.client.api.impl.data.FuserLockingProxyStore;
import com.mosaicatm.fuser.client.api.impl.data.TimedProxyDataRemover;
import com.mosaicatm.lib.time.SystemClock;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class TimedProxyDataRemoverTest 
{
	private FuserClientStore<MatmFlight> store;
	private DataRemover<MatmFlight> remover;
	
	@Before
	public void setup ()
	{
		store = new FuserLockingProxyStore<>(new FuserClientMatmFlightStore ());
		remover = new TimedProxyDataRemover<>(new MatmFlightDataRemover (store, new SystemClock()));
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
	public void testExpirationTimed ()
	{		
		assertEquals (0, store.size());
		
		store.add(createFlight(UUID.randomUUID().toString(), "TST111", getNegativeOffsetTime(0)));
		store.add(createFlight(UUID.randomUUID().toString(), "TST222", getNegativeOffsetTime(12)));
		store.add(createFlight(UUID.randomUUID().toString(), "TST333", getNegativeOffsetTime(24)));
		store.add(createFlight(UUID.randomUUID().toString(), "TST444", getNegativeOffsetTime(36)));
		store.add(createFlight(UUID.randomUUID().toString(), "TST555", getNegativeOffsetTime(48)));
		
		assertEquals (5, store.size());
		
		((TimedProxyDataRemover)remover).setRunIntervalMillis(1000L);
		((TimedProxyDataRemover)remover).setActive(true);
		((TimedProxyDataRemover)remover).start();
		
		try
		{
			// give enough time for the remover to run
			Thread.sleep(5000L);
		}
		catch (InterruptedException ie)
		{
			ie.printStackTrace ();
			fail (ie.getMessage());
		}
		
		assertEquals (3, store.size());
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
	
}
