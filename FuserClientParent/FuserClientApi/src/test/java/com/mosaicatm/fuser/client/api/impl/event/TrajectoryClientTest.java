package com.mosaicatm.fuser.client.api.impl.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.client.api.FuserClientApi;
import com.mosaicatm.fuser.client.api.FuserClientApiConfiguration;
import com.mosaicatm.fuser.client.api.impl.FuserClientApiSpringLoader;
import com.mosaicatm.fuser.client.api.impl.data.GenericFuserUpdateHandler;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class TrajectoryClientTest
{
    private FuserClientApi<MatmFlight> api;
    private GenericFuserUpdateHandler<MatmFlight> updateHandler;

    @Before
    public void setup ()
    {
        FuserClientApiConfiguration configs = new FuserClientApiConfiguration ();
        configs.setProcessRawTrajectoriesEnabled(true);

        FuserClientApiSpringLoader loader = new FuserClientApiSpringLoader ();
        loader.setApiConfiguration(configs);
        loader.setLoadJms(false);
        loader.setLoadRoutes(false);
        loader.setLoadServicesClients(false);
        loader.load();

        api = loader.getApi();

        updateHandler = new GenericFuserUpdateHandler<> ();
        updateHandler.setFuserClientApi(api);
    }

    @Test
    public void testTrajectoryCollection ()
    {
        assertNotNull (api);

        String gufi = UUID.randomUUID().toString();

        MatmFlight flightA = createFlight (gufi, 11.111d, 22.222d, 333d, new Date());
        assertNotNull (flightA);
        System.out.println(flightA.getPosition().getTimestamp().getTime());

        updateHandler.handleUpdate(copyFlight(flightA));

        MatmFlight storeFlightA = api.getStore().get(gufi);
        assertNotNull (storeFlightA);

        List<MatmFlight> expectedTrajectoriesA = new ArrayList<>();
        expectedTrajectoriesA.add(flightA);
        containsValidTrajectories (storeFlightA, expectedTrajectoriesA);

        MatmFlight flightB = createFlight (gufi, 33.333d, 44.444d, 555d, new Date(System.currentTimeMillis() + (1000 * 60 * 1)));
        assertNotNull (flightB);

        updateHandler.handleUpdate(copyFlight(flightB));

        MatmFlight storeFlightB = api.getStore().get(gufi);
        assertNotNull(storeFlightB);

        List<MatmFlight> expectedTrajectoriesB = new ArrayList<>();
        expectedTrajectoriesB.add(flightA);
        expectedTrajectoriesB.add(flightB);
        containsValidTrajectories (storeFlightB, expectedTrajectoriesB);
    }

    private void containsValidTrajectories (MatmFlight updated, List<MatmFlight> flights)
    {
        List<Position> trajectories = updated.getTrajectory();
        assertNotNull (trajectories);
        assertEquals (flights.size(), trajectories.size());

        for (int i = 0; i < flights.size(); ++i)
        {
            Position flightPoint =  flights.get(i).getPosition();
            assertNotNull (flightPoint);

            Position trajPoint = trajectories.get(i);
            assertNotNull (trajPoint);

            assertEquals (flightPoint.getTimestamp().getTime(), trajPoint.getTimestamp().getTime());
            assertEquals (flightPoint.getAltitude(), trajPoint.getAltitude());
            assertEquals(flightPoint.getLatitude(), trajPoint.getLatitude());
            assertEquals(flightPoint.getLongitude(), trajPoint.getLongitude());
        }
    }


    private MatmFlight createFlight (String gufi, Double lat, Double lon, Double alt, Date time)
    {   
        MatmFlight flight = new MatmFlight();

        flight.setGufi(gufi);
        Position pos = new Position();
        pos.setLatitude(lat);
        pos.setLongitude(lon);
        pos.setTimestamp(time);
        pos.setAltitude(alt);

        flight.setPosition(pos);

        return flight;
    }

    private MatmFlight copyFlight (MatmFlight flight)
    {
        MatmFlight copy = null;

        try
        {
            GenericMarshaller gm = new GenericMarshaller (MatmFlight.class);
            String xml = gm.marshall(flight);
            copy = (MatmFlight)gm.unmarshall(xml);
            System.out.println(copy.getPosition().getTimestamp().getTime());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return copy;

    }
}
