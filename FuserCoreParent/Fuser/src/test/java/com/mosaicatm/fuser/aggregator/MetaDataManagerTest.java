package com.mosaicatm.fuser.aggregator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;

public class MetaDataManagerTest
{
    private com.mosaicatm.matmdata.flight.extension.ObjectFactory extensionObjectFactory =
                    new com.mosaicatm.matmdata.flight.extension.ObjectFactory();
    
    private com.mosaicatm.matmdata.flight.ObjectFactory matmFlightObjectFactory =
                    new com.mosaicatm.matmdata.flight.ObjectFactory();
    @Test
    public void testApplyRules()
    {
        int processingThreadCount = 1;
        FuserStore<MatmFlight, MetaData> store = new MatmFuserStore( processingThreadCount );
        ((MatmFuserStore)store).setIdLookup(new MatmFlightIdLookup());
        MetaDataManager<MatmFlight> dataManager = new MetaDataManager<>();
        dataManager.setFuserStore( store );
        MatmFlight update = new MatmFlight();
        update.setGufi("ABC");
        update.setTimestamp(new Date());
        update.setSystemId("FLIGHTSTATS");
        update.setAircraftAddress("12345");
        update.setLastUpdateSource("AIRLINE");
        update.setExtensions(new MatmFlightExtensions());
        update.getExtensions().setAsdexExtension(new AsdexExtension());
        update.getExtensions().getAsdexExtension().setTrackId(1000);
        Set<String> changes = new TreeSet<String>();
        changes.add("aircraftAddress");
        changes.add("extensions.asdexExtension.trackId");
        update.setChanges(new ArrayList<String>( changes ));
        dataManager.applyRules(update, null, new AggregationResult(changes, null));
        
        assertNotNull(dataManager.getFuserStore().getAllMetaData( update ));
        MetaData metaData = dataManager.getFuserStore().getMetaData( update, "aircraftAddress");
        assertNotNull(metaData);
        assertEquals("field name", metaData.getFieldName(), "aircraftAddress");
        assertEquals("timestamp", metaData.getTimestamp(), update.getTimestamp());
        assertEquals("last update source", metaData.getSource(), update.getLastUpdateSource());
        assertEquals("system id", metaData.getSystemType(), update.getSystemId());
        
        metaData = dataManager.getFuserStore().getMetaData( update, "extensions.asdexExtension.trackId");
        assertNotNull(metaData);
        assertEquals("field name", metaData.getFieldName(), "extensions.asdexExtension.trackId");
        assertEquals("timestamp", metaData.getTimestamp(), update.getTimestamp());
        assertEquals("last update source", metaData.getSource(), update.getLastUpdateSource());
        assertEquals("system id", metaData.getSystemType(), update.getSystemId());

        update.setTimestamp(new Date());
        changes.add("registration");
        
        dataManager.applyRules(update, null, new AggregationResult(changes, null));
        metaData = dataManager.getFuserStore().getMetaData( update, "registration");
        assertNotNull(metaData);
        assertEquals("field name", metaData.getFieldName(), "registration");
        assertEquals("timestamp", metaData.getTimestamp(), update.getTimestamp());
        assertEquals("last update source", metaData.getSource(), update.getLastUpdateSource());
        assertEquals("system id", metaData.getSystemType(), update.getSystemId());
        
        //test remove
        dataManager.getFuserStore().remove(update);
        assertNull(dataManager.getFuserStore().getAllMetaData(update));
        
    }
    /**
     * This unit test case tests MetaDataManager createRecord method
     * as well as PropertyVisitor addChildren method
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     */
    @Test
    public void test1() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IllegalArgumentException
    {
        int processingThreadCount = 1;
        FuserStore<MatmFlight, MetaData> store = new MatmFuserStore( processingThreadCount );
        ((MatmFuserStore)store).setIdLookup(new MatmFlightIdLookup());
        MetaDataManager<MatmFlight> dataManager = new MetaDataManager<>();
        dataManager.setFuserStore( store );
        MatmFlight update = new MatmFlight();
        update.setGufi("ABC");
        update.setTimestamp(new Date());
        update.setSystemId("FLIGHTSTATS");
        update.setAircraftAddress("12345");
        update.setLastUpdateSource("AIRLINE");
        update.setExtensions(new MatmFlightExtensions());
        update.getExtensions().setAsdexExtension(new AsdexExtension());
        update.getExtensions().getAsdexExtension().setTrackId(1000);
        update.setArrivalAerodrome(new Aerodrome());
        update.getArrivalAerodrome().setIataName("iataName");
        update.getExtensions().setDerivedExtension(new DerivedExtension());
        
        // JAXBElement: DepartureStandControllerDerivedActualTime
        {
            update.getExtensions().getDerivedExtension().setDepartureStandControllerDerivedActualTime(
                extensionObjectFactory.createDerivedExtensionDepartureStandControllerDerivedActualTime(new Date(1000)));
        }
        
        // JAXBElement: FlightRestrictions
        {
            FlightRestrictions flightRestrictions = new FlightRestrictions();
            List<FlightRestrictionEntry> apreqRestrictionIds = new ArrayList<>();
            FlightRestrictionEntry entry = new FlightRestrictionEntry();
            entry.setRestrictionId("id");
            entry.setReferenceTime(new Date(11111));
            
            apreqRestrictionIds.add(entry);            
            flightRestrictions.setApreqRestrictionIds(apreqRestrictionIds);
            update.setFlightRestrictions(
                matmFlightObjectFactory.createMatmFlightFlightRestrictions(flightRestrictions));
        }
        dataManager.createRecord(update);        
        
        assertNotNull(store.getAllMetaData( update ));
        MetaData data = store.getMetaData( update, "timestamp");
        assertEquals(data.getFieldName(), "timestamp");
        data = store.getMetaData( update, "systemId");
        assertEquals(data.getFieldName(), "systemId");
        data = store.getMetaData( update, "aircraftAddress");
        assertEquals(data.getFieldName(), "aircraftAddress");
        data = store.getMetaData( update, "lastUpdateSource");
        assertEquals(data.getFieldName(), "lastUpdateSource");
        data = store.getMetaData( update, "extensions.asdexExtension.trackId");
        assertEquals(data.getFieldName(), "extensions.asdexExtension.trackId");
        data = store.getMetaData( update, "gufi");
        assertEquals(data.getFieldName(), "gufi");
        data = store.getMetaData( update, "arrivalAerodrome.iataName");
        assertEquals(data.getFieldName(), "arrivalAerodrome.iataName");
        data = store.getMetaData( update, "extensions.derivedExtension.departureStandControllerDerivedActualTime.value");
        assertEquals(data.getFieldName(), "extensions.derivedExtension.departureStandControllerDerivedActualTime.value");
        data = store.getMetaData( update, "flightRestrictions.value");
        assertEquals(data.getFieldName(), "flightRestrictions.value");

    }
}
