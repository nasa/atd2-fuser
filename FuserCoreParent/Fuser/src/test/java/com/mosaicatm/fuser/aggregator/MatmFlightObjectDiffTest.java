package com.mosaicatm.fuser.aggregator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightIdLookup;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.matm.MatmFuserStore;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.FlightRestrictionEntry;
import com.mosaicatm.matmdata.common.FlightRestrictions;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.AsdexExtension;
import com.mosaicatm.matmdata.flight.extension.DerivedExtension;
import com.mosaicatm.matmdata.flight.extension.MatmAirlineMessageExtension;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;

public class MatmFlightObjectDiffTest
{

    @Test
    public void testChanges() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        MatmFlight updateFlight = new MatmFlight();
        updateFlight.setGufi("updateFlight");
        updateFlight.setExtensions(new MatmFlightExtensions());
        updateFlight.getExtensions().setAsdexExtension(new AsdexExtension());
        updateFlight.getExtensions().getAsdexExtension().setTrackId(1000);
        updateFlight.setAcid("acid");
        Aerodrome CLT = new Aerodrome();
        CLT.setIataName("CLT");
        CLT.setIcaoName("ICAO");
        updateFlight.getExtensions().getAsdexExtension().setAsdexAirport(CLT);
        updateFlight.getExtensions().setMatmAirlineMessageExtension(new MatmAirlineMessageExtension());
        updateFlight.getExtensions().setDerivedExtension(new DerivedExtension());
        updateFlight.getExtensions().getDerivedExtension().setDepartureDetectedInMovementArea(false);
        updateFlight.setArrivalAerodrome(CLT);
        updateFlight.setDepartureAerodrome(CLT);
        updateFlight.setArrivalRunwayEstimatedTime(new Date());
        
        // Test JAXElement extensions.derivedExtension.departureStandControllerDerivedActualTime
        com.mosaicatm.matmdata.flight.extension.ObjectFactory extensionObjectFactory = new com.mosaicatm.matmdata.flight.extension.ObjectFactory();
        updateFlight.getExtensions().getDerivedExtension().setDepartureStandControllerDerivedActualTime(
            extensionObjectFactory.createDerivedExtensionDepartureStandControllerDerivedActualTime(new Date(100)));
        
        // Test JAXElement flightRestrictions
        com.mosaicatm.matmdata.flight.ObjectFactory mamtmFlightObjectFactory = new com.mosaicatm.matmdata.flight.ObjectFactory();
        FlightRestrictions flightRestrictions = new FlightRestrictions();
        
        List<FlightRestrictionEntry> flightRestrictionEntries = new ArrayList<>();
        FlightRestrictionEntry flightRestrictionEntry = new FlightRestrictionEntry();
        flightRestrictionEntry.setReferenceTime(new Date(1));
        flightRestrictionEntries.add(flightRestrictionEntry);
        
        flightRestrictions.setApreqRestrictionIds(flightRestrictionEntries);
        updateFlight.setFlightRestrictions(mamtmFlightObjectFactory.createMatmFlightFlightRestrictions(flightRestrictions));
        
        
        MatmFlight targetFlight = new MatmFlight();
        targetFlight.setGufi("targetFlight");
        targetFlight.setExtensions(new MatmFlightExtensions());
        targetFlight.getExtensions().setAsdexExtension(new AsdexExtension());
        targetFlight.getExtensions().setDerivedExtension(new DerivedExtension());
        
        
        // Expected diff results
        List<String> expectedChanges = new ArrayList<String> ();
        expectedChanges.add("gufi");
        expectedChanges.add("arrivalAerodrome.iataName");
        expectedChanges.add("arrivalAerodrome.icaoName");
        expectedChanges.add("departureAerodrome.iataName");
        expectedChanges.add("departureAerodrome.icaoName");
        expectedChanges.add("acid");
        expectedChanges.add("extensions.asdexExtension.trackId");
        expectedChanges.add("extensions.asdexExtension.asdexAirport.iataName");
        expectedChanges.add("extensions.asdexExtension.asdexAirport.icaoName");
        expectedChanges.add("extensions.derivedExtension.departureDetectedInMovementArea");
        expectedChanges.add("arrivalRunwayEstimatedTime");
        expectedChanges.add("extensions.derivedExtension.departureStandControllerDerivedActualTime.value");
        expectedChanges.add("flightRestrictions.value");
        
        int processingThreadCount = 1;
        MatmDiffAggregator aggregator = new MatmDiffAggregator();
        aggregator.setMetaDataManager(new MetaDataManager<MatmFlight>());
        FuserStore<MatmFlight, MetaData> store = new MatmFuserStore( processingThreadCount );
        ((MatmFuserStore)store).setIdLookup(new MatmFlightIdLookup());
        aggregator.setFuserStore(store);
        aggregator.getMetaDataManager().setFuserStore(store);
        MatmObjectDiff diff = new MatmObjectDiff();
        
        AggregationResult result = diff.compareObjects(updateFlight,targetFlight);
        assertEquals(result.getChanges().size(), expectedChanges.size());
        
        for (String change : expectedChanges)
        {
            assertTrue(result.getChanges().contains(change));
        }    
        
        // This test is same as above but with null derivedExtension
        // This makes sure all nested fields inside the derivedExtension
        // will also be compared. Expected to be the same result as above
        // and correctly generated
        MatmFlight targetFlight2 = new MatmFlight();
        targetFlight2.setGufi("targetFlight2");
        targetFlight2.setExtensions(new MatmFlightExtensions());
        targetFlight2.getExtensions().setAsdexExtension(new AsdexExtension());
        result = diff.compareObjects(updateFlight,targetFlight);
        assertEquals(result.getChanges().size(), expectedChanges.size());
        
        for (String change : expectedChanges)
        {
            assertTrue(result.getChanges().contains(change));
        }   
        
    }
    
    @Test
    public void testIdenticalsAndChanges() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        Aerodrome CLT = new Aerodrome();
        CLT.setIataName("CLT");
        CLT.setIcaoName("ICAO");
        
        // Update flight
        MatmFlight updateFlight = new MatmFlight();
        updateFlight.setExtensions(new MatmFlightExtensions());
        // diffs
        updateFlight.setGufi("a");
        updateFlight.setAcid("acid");
        updateFlight.getExtensions().setAsdexExtension(new AsdexExtension());
        updateFlight.getExtensions().getAsdexExtension().setAsdexAirport(CLT);
        updateFlight.getExtensions().getAsdexExtension().setTrackId(1000);
        updateFlight.setArrivalRunwayEstimatedTime(new Date());
        
        // identicals
        updateFlight.getExtensions().setDerivedExtension(new DerivedExtension());
        updateFlight.getExtensions().getDerivedExtension().setDepartureDetectedInMovementArea(true);
        updateFlight.setArrivalAerodrome(CLT);
        updateFlight.setDepartureAerodrome(CLT);
        updateFlight.setAircraftAddress("address");
        
        com.mosaicatm.matmdata.flight.extension.ObjectFactory extensionObjectFactory = new com.mosaicatm.matmdata.flight.extension.ObjectFactory();
        updateFlight.getExtensions().getDerivedExtension().setDepartureStandControllerDerivedActualTime(
            extensionObjectFactory.createDerivedExtensionDepartureStandControllerDerivedActualTime(new Date(100)));
        
        com.mosaicatm.matmdata.flight.ObjectFactory mamtmFlightObjectFactory = new com.mosaicatm.matmdata.flight.ObjectFactory();
        FlightRestrictions flightRestrictions = new FlightRestrictions();
        
        List<FlightRestrictionEntry> flightRestrictionEntries = new ArrayList<>();
        FlightRestrictionEntry flightRestrictionEntry = new FlightRestrictionEntry();
        flightRestrictionEntry.setReferenceTime(new Date(1));
        flightRestrictionEntries.add(flightRestrictionEntry);
        
        flightRestrictions.setApreqRestrictionIds(flightRestrictionEntries);
        updateFlight.setFlightRestrictions(mamtmFlightObjectFactory.createMatmFlightFlightRestrictions(flightRestrictions));
        
        
        // Target flight
        MatmFlight targetFlight = new MatmFlight();
        targetFlight.setTrajectory(null);
        targetFlight.setFixList(null);
        targetFlight.setSectorList(null);
        targetFlight.setChanges(null);
        targetFlight.setExtensions(new MatmFlightExtensions());
        // diffs
        targetFlight.setGufi("targetFlight");
        targetFlight.setAcid("");
        targetFlight.getExtensions().setAsdexExtension(new AsdexExtension());
        targetFlight.getExtensions().getAsdexExtension().setTrackId(2000);
        targetFlight.setArrivalRunwayEstimatedTime(new Date());
        
        // identicals
        targetFlight.getExtensions().setDerivedExtension(new DerivedExtension());
        targetFlight.getExtensions().getDerivedExtension().setDepartureDetectedInMovementArea(true);
        targetFlight.setArrivalAerodrome(CLT);
        targetFlight.setDepartureAerodrome(CLT);
        targetFlight.setAircraftAddress("address");
        targetFlight.getExtensions().getDerivedExtension().setDepartureStandControllerDerivedActualTime(
            extensionObjectFactory.createDerivedExtensionDepartureStandControllerDerivedActualTime(new Date(100)));

        FlightRestrictions flightRestrictions2 = new FlightRestrictions();
        
        List<FlightRestrictionEntry> flightRestrictionEntries2 = new ArrayList<>();
        FlightRestrictionEntry flightRestrictionEntry2 = new FlightRestrictionEntry();
        flightRestrictionEntry2.setReferenceTime(new Date(1));
        flightRestrictionEntries2.add(flightRestrictionEntry2);
        
        flightRestrictions2.setApreqRestrictionIds(flightRestrictionEntries2);
        targetFlight.setFlightRestrictions(mamtmFlightObjectFactory.createMatmFlightFlightRestrictions(flightRestrictions2));
        
        int processingThreadCount = 1;
        MatmDiffAggregator aggregator = new MatmDiffAggregator();
        aggregator.setMetaDataManager(new MetaDataManager<MatmFlight>());
        FuserStore<MatmFlight, MetaData> store = new MatmFuserStore( processingThreadCount );
        ((MatmFuserStore)store).setIdLookup(new MatmFlightIdLookup());
        aggregator.setFuserStore(store);
        aggregator.getMetaDataManager().setFuserStore(store);
        MatmObjectDiff diff = new MatmObjectDiff();
        List<String> expectedIdenticals = new ArrayList<String> ();
        expectedIdenticals.add("arrivalAerodrome.iataName");
        expectedIdenticals.add("arrivalAerodrome.icaoName");
        expectedIdenticals.add("departureAerodrome.iataName");
        expectedIdenticals.add("departureAerodrome.icaoName");
        expectedIdenticals.add("extensions.derivedExtension.departureDetectedInMovementArea");
        expectedIdenticals.add("aircraftAddress");
        expectedIdenticals.add("extensions.derivedExtension.departureStandControllerDerivedActualTime.value");
        expectedIdenticals.add("flightRestrictions.value");
        AggregationResult result = diff.compareObjects(updateFlight, targetFlight);
        
        // MatmFlight schema is generated using jaxb which automatically
        // initializes attribute that is a List type.
        // Whenever calls to the get method will return a empty list if null
        // so the result will include those list fields if 
        // they are both empty, for instance: trajectory, sectorList.
        assertTrue(result.getIdenticals().size() >= expectedIdenticals.size() );
        for (String identical : expectedIdenticals)
        {
            // since the result may contain extra identical fields: trajectory, sectorList
            // We don't want to have them included in this test case
            // as development goes further down
            System.out.println(identical);
            assertTrue(result.getIdenticals().contains(identical));
        }
        
        
    }

    @Test
    public void testChangesJaxBNill() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        MatmFlight updateFlight = new MatmFlight();
        updateFlight.setGufi("gufi");
        com.mosaicatm.matmdata.flight.ObjectFactory mamtmFlightObjectFactory = new com.mosaicatm.matmdata.flight.ObjectFactory();
        updateFlight.setEstimatedDepartureClearanceTime( mamtmFlightObjectFactory.createMatmFlightEstimatedDepartureClearanceTime( new Date(0) ));
        
        MatmFlight targetFlight = new MatmFlight();
        targetFlight.setGufi("gufi");
        targetFlight.setEstimatedDepartureClearanceTime( mamtmFlightObjectFactory.createMatmFlightEstimatedDepartureClearanceTime( new Date(1) ));
        
        int processingThreadCount = 1;
        MatmDiffAggregator aggregator = new MatmDiffAggregator();
        aggregator.setMetaDataManager(new MetaDataManager<MatmFlight>());
        FuserStore<MatmFlight, MetaData> store = new MatmFuserStore( processingThreadCount );
        ((MatmFuserStore)store).setIdLookup(new MatmFlightIdLookup());
        aggregator.setFuserStore(store);
        aggregator.getMetaDataManager().setFuserStore(store);
        MatmObjectDiff diff = new MatmObjectDiff();
        
        AggregationResult result = diff.compareObjects(updateFlight,targetFlight);
        assertTrue(result.getChanges().contains("estimatedDepartureClearanceTime.value"));
        
        updateFlight.setEstimatedDepartureClearanceTime( mamtmFlightObjectFactory.createMatmFlightEstimatedDepartureClearanceTime( null ));
        result = diff.compareObjects(updateFlight,targetFlight);
        assertTrue(result.getChanges().contains("estimatedDepartureClearanceTime.value"));
    }    
}
