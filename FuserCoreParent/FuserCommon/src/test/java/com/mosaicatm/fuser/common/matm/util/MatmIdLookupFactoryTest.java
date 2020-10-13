package com.mosaicatm.fuser.common.matm.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.aircraft.MatmAircraftIdLookup;
import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightIdLookup;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmIdLookupFactoryTest
{
    private MatmIdLookupFactory factory;

    @Before
    public void setup()
    {
        Map<String, MatmIdLookup<? extends Object, String>> lookups = new HashMap<>();
        lookups.put(MatmFlight.class.getName(), new MatmFlightIdLookup());
        lookups.put(MatmAircraft.class.getName(), new MatmAircraftIdLookup());

        factory = new MatmIdLookupFactory();
        factory.setLookups(lookups);
    }

    @Test
    public void testMatmFlightIdLookup()
    {
        MatmFlight flight = new MatmFlight();
        flight.setGufi(UUID.randomUUID().toString());

        String key = factory.getIdentifier(flight);

        assertNotNull(key);
        assertEquals(flight.getGufi(), key);
    }

    @Test
    public void testMatmAircraftLookup()
    {
        MatmAircraft aircraft = new MatmAircraft();
        aircraft.setRegistration(UUID.randomUUID().toString());

        String key = factory.getIdentifier(aircraft);

        assertNotNull(key);
        assertEquals(aircraft.getRegistration(), key);
    }
}
