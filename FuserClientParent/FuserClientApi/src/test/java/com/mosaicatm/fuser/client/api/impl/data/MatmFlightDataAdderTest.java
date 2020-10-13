package com.mosaicatm.fuser.client.api.impl.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.client.api.data.DataAdder;
import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.impl.data.GenericDataAdder;
import com.mosaicatm.fuser.client.api.impl.data.FuserClientMatmFlightStore;
import com.mosaicatm.fuser.client.api.impl.data.FuserLockingProxyStore;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFlightDataAdderTest 
{
	private DataAdder<MatmFlight> adder;
	private FuserClientStore<MatmFlight> store;
	
	@Before
	public void setup ()
	{
		store = new FuserLockingProxyStore<>(new FuserClientMatmFlightStore ());
		adder = new GenericDataAdder<>(store);
	}
	
	@Test
	public void testAdd ()
	{
		assertEquals (0, store.size());
		
		MatmFlight flight = createFlight (UUID.randomUUID().toString(), "TST123");
		
		adder.add(flight);
		
		assertEquals (1, store.size());
		
		MatmFlight storeFlight = store.get(flight.getGufi());
		
		assertNotNull (storeFlight);
				
		assertEquals (flight.getGufi(), storeFlight.getGufi());
		assertEquals (flight.getAcid(), storeFlight.getAcid());
	}
	
	@Test
	public void testAddMultiple ()
	{
		assertEquals (0, store.size());
		
		MatmFlight flightA = createFlight (UUID.randomUUID().toString(), "TST111");
		MatmFlight flightB = createFlight (UUID.randomUUID().toString(), "TST222");
		MatmFlight flightC = createFlight (UUID.randomUUID().toString(), "TST333");
		
		adder.add(flightA);
		adder.add(flightB);
		adder.add(flightC);
		
		assertEquals (3, store.size());
		
		MatmFlight storeFlightA = store.get(flightA.getGufi());
		MatmFlight storeFlightB = store.get(flightB.getGufi());
		MatmFlight storeFlightC = store.get(flightC.getGufi());
		
		assertNotNull (storeFlightA);
		assertNotNull (storeFlightB);
		assertNotNull (storeFlightB);
		
		assertEquals (flightA.getGufi(), storeFlightA.getGufi());
		assertEquals (flightA.getAcid(), storeFlightA.getAcid());
		
		assertEquals (flightB.getGufi(), storeFlightB.getGufi());
		assertEquals (flightB.getAcid(), storeFlightB.getAcid());
		
		assertEquals (flightC.getGufi(), storeFlightC.getGufi());
		assertEquals (flightC.getAcid(), storeFlightC.getAcid());
	}
	
	private MatmFlight createFlight (String gufi, String acid)
	{
		MatmFlight flight = new MatmFlight();
		flight.setGufi(gufi);
		flight.setAcid(acid);
		
		return flight;
	}
}
