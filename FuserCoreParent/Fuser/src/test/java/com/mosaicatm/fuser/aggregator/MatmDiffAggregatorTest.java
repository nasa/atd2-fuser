package com.mosaicatm.fuser.aggregator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.AerodromeUtil;
import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightIdLookup;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.matm.MatmFuserStore;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.AsdexExtension;
import com.mosaicatm.matmdata.flight.extension.DerivedExtension;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;

public class MatmDiffAggregatorTest {

    @Test
    public void test() {

        int processingThreadCount = 1;
        FuserStore<MatmFlight, MetaData> store = new MatmFuserStore( processingThreadCount );
        ((MatmFuserStore)store).setIdLookup(new MatmFlightIdLookup());
        
        MetaDataManager<MatmFlight> manager = new MetaDataManager<>();
        manager.setFuserStore( store );
        MatmDiffAggregator aggregator = new MatmDiffAggregator();
        aggregator.setMetaDataManager(manager);
        aggregator.setFuserStore(store);
        
        MatmFlight flight = new MatmFlight();
        flight.setGufi("ABC123");
        flight.setDepartureRunwayActual("36R");
        flight.setDepartureRunwayAssigned("36R");
        flight.setArrivalAerodrome(AerodromeUtil.createFromIataName("ALT"));
        flight.setDepartureRunwayEstimatedTime(TimeFactory.getDate("2016-06-10 18:00:00"));
        flight.setLastUpdateSource("ASDEX");
        flight.setExtensions(new MatmFlightExtensions());
        flight.getExtensions().setAsdexExtension(new AsdexExtension());
        flight.getExtensions().getAsdexExtension().setTrackId(1);
        flight.setTimestamp(TimeFactory.getDate("2016-06-10 17:00:00"));
        Position position = new Position();
        position.setAltitude(100d);
        position.setSource("ASDEX");
        flight.setPosition(position);

        flight.getTrajectory().add(position);
        
        MatmFlight aggregated = aggregator.aggregate(flight);
        
        assertEquals("36R", aggregated.getDepartureRunwayActual());
        assertEquals("36R", aggregated.getDepartureRunwayAssigned());
        // assertEquals(200d, flight.getPosition().getAltitude(), 0);
        assertEquals(1, aggregated.getTrajectory().size());
        assertEquals("ASDEX", aggregated.getLastUpdateSource());
        
        assertEquals("2016-06-10 17:00:00", TimeFactory.getDateAsString(aggregated.getTimestamp()));
        assertEquals("2016-06-10 18:00:00", TimeFactory.getDateAsString(aggregated.getDepartureRunwayEstimatedTime()));
        

        MatmFlight flight2 = new MatmFlight();
        flight2.setGufi("ABC123");
        flight2.setDepartureAerodrome(AerodromeUtil.createFromIataName("CLT"));
        flight2.setArrivalAerodrome(AerodromeUtil.createFromIataName("ALT"));
        flight2.setDepartureRunwayEstimatedTime(TimeFactory.getDate("2016-06-10 18:00:00"));
        flight2.setLastUpdateSource("ASDEX");
        flight2.setTimestamp(TimeFactory.getDate("2016-06-10 17:00:01"));
        flight2.setDepartureRunwayActual("36L");
        flight2.setDepartureRunwayAssigned("36R");
        flight2.setExtensions(new MatmFlightExtensions());
        flight2.getExtensions().setAsdexExtension(new AsdexExtension());
        flight2.getExtensions().getAsdexExtension().setTrackId(1000);
        flight2.getExtensions().setDerivedExtension(new DerivedExtension());
        flight2.getExtensions().getDerivedExtension().setDepartureDetectedInMovementArea(true);
        Position position2 = new Position();
        position2.setAltitude(200d);
        position2.setSource("ASDEX");

        flight2.setPosition(position2);
        flight2.getTrajectory().add(position2);

        assertTrue(flight.getChanges().isEmpty());
        assertTrue(flight2.getChanges().isEmpty());
        
        aggregated = aggregator.aggregate(flight2);
        
        assertFalse(aggregated.getChanges().isEmpty());

        assertEquals("36L", aggregated.getDepartureRunwayActual());
        assertEquals(1, aggregated.getTrajectory().size());
        assertEquals("ASDEX", aggregated.getLastUpdateSource());
        assertNotNull(aggregated.getTimestamp());
        assertEquals("2016-06-10 17:00:01", TimeFactory.getDateAsString(aggregated.getTimestamp()));
        //assertNull(aggregated.getDepartureRunwayEstimatedTime());
        //assertEquals("2016-06-10 18:00:00", TimeFactory.getDateAsString(aggregated.getDepartureRunwayEstimatedTime()));
        Collection<MetaData> allMetaData = manager.getFuserStore().getAllMetaData( manager.getFuserStore().get( "ABC123" ));
        
        Map<String, MetaData> resultChange = new HashMap<>();
        for( MetaData meta: allMetaData )
        {
            resultChange.put( meta.getFieldName(), meta);
        }
        assertTrue(resultChange.containsKey("position"));
        assertTrue(resultChange.containsKey("trajectory"));
        assertTrue(resultChange.containsKey("departureRunwayActual"));
        assertTrue(resultChange.containsKey("timestamp"));
        
        assertTrue(resultChange.containsKey("extensions.asdexExtension.trackId"));
        assertTrue(resultChange.containsKey("extensions.derivedExtension.departureDetectedInMovementArea"));
        assertTrue(resultChange.containsKey("arrivalAerodrome.iataName"));
        
        // check the aggregated should carry those changes excepts those explicitly ignored
        assertTrue(aggregated.getChanges().contains("position"));
        assertTrue(aggregated.getChanges().contains("trajectory"));
        assertTrue(aggregated.getChanges().contains("departureRunwayActual"));
        assertTrue(aggregated.getChanges().contains("departureAerodrome.iataName"));
        
        assertTrue(aggregated.getChanges().contains("extensions.asdexExtension.trackId"));
        assertTrue(aggregated.getChanges().contains("extensions.derivedExtension.departureDetectedInMovementArea"));
        
        assertFalse(aggregated.getChanges().contains("arrivalAerodrome.iataName"));
        assertFalse(aggregated.getChanges().contains("departureRunwayAssigned"));
    }

}
