package com.mosaicatm.fuser.client.api.impl.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.client.api.data.DataUpdater;
import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.impl.data.GenericDataUpdater;
import com.mosaicatm.fuser.client.api.impl.data.FuserClientMatmFlightStore;
import com.mosaicatm.fuser.client.api.impl.data.FuserLockingProxyStore;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFlightDataUpdaterTest 
{
	private DataUpdater<MatmFlight> updater;
	private FuserClientStore<MatmFlight> store;
	
	@Before
	public void setup ()
	{
		store = new FuserLockingProxyStore<>(new FuserClientMatmFlightStore ());
		
		updater = new GenericDataUpdater<> (store);
	}
	
	@Test
	public void testMatmFlightUpdatingMatmFlight ()
	{
		String gufi = UUID.randomUUID().toString();
		
		MatmFlight targetFlight = createFlight (gufi, "AAL123", "IAD", null);
		MatmFlight updateFlight = createFlight (gufi, null, "DCA", "ATL");
		
		updater.update(updateFlight, targetFlight);
		
		MatmFlight mergeFlight = store.get(gufi);
		
		assertNotNull (mergeFlight);
				
		assertEquals (gufi, mergeFlight.getGufi());
		assertEquals (targetFlight.getAcid(), mergeFlight.getAcid());
		assertEquals (updateFlight.getArrivalAerodrome().getIataName(), mergeFlight.getArrivalAerodrome().getIataName());
		assertEquals (updateFlight.getDepartureAerodrome().getIataName(), mergeFlight.getDepartureAerodrome().getIataName());
	}
	
	private MatmFlight createFlight (String gufi, String acid, String apt, String dap)
	{
		MatmFlight flight = new MatmFlight();
		flight.setGufi(gufi);
		flight.setAcid(acid);
		
		Aerodrome arrivalAerodrome = new Aerodrome();
		arrivalAerodrome.setIataName(apt);
		flight.setArrivalAerodrome( arrivalAerodrome );

		Aerodrome departureAerodrome = new Aerodrome();
		departureAerodrome.setIataName(dap);		
		flight.setDepartureAerodrome( departureAerodrome );
		
		return flight;
	}
}
