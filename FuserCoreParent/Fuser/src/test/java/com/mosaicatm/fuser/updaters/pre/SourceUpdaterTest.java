package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.fuser.common.matm.util.MatmIdLookupFactory;
import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightIdLookup;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class SourceUpdaterTest {

    private SourceUpdater<MatmFlight> sourceUpdater = new SourceUpdater<>();

    @Before
    public void setup()
    {
        Map<String, MatmIdLookup<? extends Object, String>> lookupMap = new HashMap<>();
        lookupMap.put(MatmFlight.class.getName(), new MatmFlightIdLookup());

        MatmIdLookupFactory factory = new MatmIdLookupFactory();
        factory.setLookups(lookupMap);

        sourceUpdater = new SourceUpdater<>();
        sourceUpdater.setIdLookup(factory);
    }

    @Test
    public void test(){
        String gufi = "gufi";
        MatmFlight flight = new MatmFlight();
        flight.setGufi(gufi);

        sourceUpdater.update(flight, null);

        assertTrue(flight.getUpdateSources().size() == 0);

        MatmFlight updateFlight = new MatmFlight();
        updateFlight.setLastUpdateSource("TFM");
        updateFlight.setGufi(gufi);

        sourceUpdater.update(updateFlight, flight);

        assertTrue(updateFlight.getUpdateSources().size() == 1);

        MatmFlight updateFlight1 = new MatmFlight();
        updateFlight1.setGufi(gufi);
        updateFlight1.setLastUpdateSource("AIRLINE");
        sourceUpdater.update(updateFlight1, updateFlight);

        assertTrue(updateFlight1.getUpdateSources().size() == 2);

        //flight with unknown source is not allowed
        MatmFlight updateFlight2 = new MatmFlight();
        updateFlight2.setGufi(gufi);
        updateFlight2.setLastUpdateSource(null);
        sourceUpdater.update(updateFlight2, updateFlight1);
        assertTrue(updateFlight2.getUpdateSources().size() == 2);

        MatmFlight updateFlight3 = new MatmFlight();
        updateFlight3.setGufi(gufi);
        updateFlight3.setLastUpdateSource("AIRLINE");
        updateFlight3.setSystemId("FLIGHTHUB");
        sourceUpdater.update(updateFlight3, updateFlight2);
        assertTrue(updateFlight3.getUpdateSources().size() == 3);

        //same source and system is not allowed
        MatmFlight updateFlight4 = new MatmFlight();
        updateFlight4.setGufi(gufi);
        updateFlight4.setLastUpdateSource("AIRLINE");
        updateFlight4.setSystemId("FLIGHTHUB");
        sourceUpdater.update(updateFlight4, updateFlight3);
        assertTrue(updateFlight4.getUpdateSources().size() == 3);

        //update with same source but different system should be added
        MatmFlight updateFlight5 = new MatmFlight();
        updateFlight5.setGufi(gufi);
        updateFlight5.setLastUpdateSource("AIRLINE");
        updateFlight5.setSystemId("FLIGHTSTATS");
        sourceUpdater.update(updateFlight5, updateFlight4);
        assertTrue(updateFlight5.getUpdateSources().size() == 4);		
    }

    @Test
    public void testUpdateSourceTimestamp()
    {
        String gufi = "gufi";
        String updateSource = "TMA";
        String systemId = "TMA-SWIM";
        Date timestamp = TimeFactory.getDate("2017-09-21 17:00:00");

        MatmFlight flight = new MatmFlight();
        flight.setGufi(gufi);
        flight.setLastUpdateSource(updateSource);
        flight.setSystemId(systemId);
        flight.setTimestamp(timestamp);
        flight.setTimestampFuserReceived(null);

        MatmFlight target = new MatmFlight();
        sourceUpdater.update(flight, target);

        List<MetaData> updateSources = flight.getUpdateSources();
        assertEquals (1, updateSources.size());

        MetaData source = updateSources.get(0);
        assertNotNull(source);
        assertEquals (updateSource, source.getSource());
        assertEquals (systemId, source.getSystemType());
        assertEquals (timestamp, source.getTimestamp());
    }

    @Test
    public void testUpdateSourceFuserReceivedTimestamp()
    {
        String gufi = "gufi";
        String updateSource = "TMA";
        String systemId = "TMA-SWIM";
        Date timestamp = TimeFactory.getDate("2017-09-21 17:00:00");

        MatmFlight flight = new MatmFlight();
        flight.setGufi(gufi);
        flight.setLastUpdateSource(updateSource);
        flight.setSystemId(systemId);
        flight.setTimestampFuserReceived(timestamp);
        flight.setTimestamp(null);

        MatmFlight target = new MatmFlight();
        sourceUpdater.update(flight, target);

        List<MetaData> updateSources = flight.getUpdateSources();
        assertEquals (1, updateSources.size());

        MetaData source = updateSources.get(0);
        assertNotNull(source);
        assertEquals (updateSource, source.getSource());
        assertEquals (systemId, source.getSystemType());
        assertEquals (timestamp, source.getTimestamp());
    }
}
