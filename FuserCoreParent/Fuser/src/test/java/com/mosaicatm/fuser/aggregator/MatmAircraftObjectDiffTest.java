package com.mosaicatm.fuser.aggregator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.aircraft.MatmAircraftIdLookup;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.matm.MatmAircraftFuserStore;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.aircraft.ObjectFactory;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.common.SurfaceFlightState;

public class MatmAircraftObjectDiffTest
{
    ObjectFactory objectFactory = new ObjectFactory();

    @Test
    public void testChanges() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        MatmAircraft targetAircraft = new MatmAircraft();
        targetAircraft.setRegistration("tail");
        targetAircraft.setType("A123");
        targetAircraft.setAddress("12345");
        targetAircraft.setAtGate(objectFactory.createMatmAircraftAtGate(false));
        targetAircraft.setAtOperatingAirport(objectFactory.createMatmAircraftAtOperatingAirport(true));
        targetAircraft.setEquipmentQualifier("J");
        targetAircraft.setFaaEngineClass("D");
        targetAircraft.setLastKnownGate(objectFactory.createMatmAircraftLastKnownGate("A1"));
        targetAircraft.setLastKnownGufi(objectFactory.createMatmAircraftLastKnownGufi("gufi123"));
        Position position = new Position();
        position.setAltitude(0.0);
        position.setHeading(0.0);
        position.setLatitude(35.0);
        position.setLongitude(-81.0);
        position.setSpeed(0.0);
        targetAircraft.setLastKnownPosition(position);
        targetAircraft.setLastKnownSurfaceFlightState(
            objectFactory.createMatmAircraftLastKnownSurfaceFlightState(SurfaceFlightState.SCHEDULED));
        targetAircraft.setRecatEngineClass("D");
        targetAircraft.setRepositionDestination(null);
        targetAircraft.setRepositioned(objectFactory.createMatmAircraftRepositioned(false));

        MatmAircraft update = new MatmAircraft();
        update.setRegistration("tail");
        update.setType("A123");
        update.setAddress("12345");
        update.setAtOperatingAirport(objectFactory.createMatmAircraftAtOperatingAirport(true));
        update.setEquipmentQualifier("J");
        update.setFaaEngineClass("D");
        update.setRecatEngineClass("D");
        update.setRepositionDestination(null);
        update.setRepositioned(objectFactory.createMatmAircraftRepositioned(false));
        //diffs
        update.setAtGate(objectFactory.createMatmAircraftAtGate(true));
        update.setLastKnownGate(objectFactory.createMatmAircraftLastKnownGate("A3"));
        update.setLastKnownGufi(objectFactory.createMatmAircraftLastKnownGufi("gufi456"));
        Position positionTarget = new Position();
        positionTarget.setAltitude(0.0);
        positionTarget.setHeading(0.0);
        positionTarget.setLatitude(35.1);
        positionTarget.setLongitude(-81.1);
        positionTarget.setSpeed(0.0);
        update.setLastKnownPosition(positionTarget);
        update.setLastKnownSurfaceFlightState(
            objectFactory.createMatmAircraftLastKnownSurfaceFlightState(SurfaceFlightState.IN_GATE));

        // Expected diff results
        List<String> expectedChanges = new ArrayList<String> ();
        expectedChanges.add("atGate.value");
        expectedChanges.add("lastKnownGate.value");
        expectedChanges.add("lastKnownGufi.value");
        expectedChanges.add("lastKnownPosition");
        expectedChanges.add("lastKnownSurfaceFlightState.value");

        // Expected identical results
        List<String> expectedIdenticals = new ArrayList<String> ();
        expectedIdenticals.add("registration");
        expectedIdenticals.add("address");
        expectedIdenticals.add("faaEngineClass");
        expectedIdenticals.add("equipmentQualifier");
        expectedIdenticals.add("recatEngineClass");
        expectedIdenticals.add("type");
        expectedIdenticals.add("repositioned.value");
        expectedIdenticals.add("atOperatingAirport.value");

        int processingThreadCount = 1;
        MatmAircraftDiffAggregator aggregator = new MatmAircraftDiffAggregator();
        aggregator.setMetaDataManager(new MetaDataManager<MatmAircraft>());
        FuserStore<MatmAircraft, MetaData> store = new MatmAircraftFuserStore( processingThreadCount );
        ((MatmAircraftFuserStore)store).setIdLookup(new MatmAircraftIdLookup());
        aggregator.setFuserStore(store);
        aggregator.getMetaDataManager().setFuserStore(store);
        MatmObjectDiff diff = new MatmObjectDiff();

        AggregationResult result = diff.compareObjects(update,targetAircraft);
        assertEquals(result.getChanges().size(), expectedChanges.size());

        for (String change : expectedChanges)
        {
            assertTrue(result.getChanges().contains(change));
        }

        if(result.getIdenticals().contains("changes"))
            result.getIdenticals().remove("changes");

        assertEquals(result.getIdenticals().size(), expectedIdenticals.size());

        for (String identical : expectedIdenticals)
        {
            assertTrue(result.getIdenticals().contains(identical));
        }

    }

}
