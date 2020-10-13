package com.mosaicatm.fuser.filter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.MeteredTime;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.common.WakeTurbulenceCategory;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.AsdexExtension;
import com.mosaicatm.matmdata.flight.extension.DerivedExtension;
import com.mosaicatm.matmdata.flight.extension.MatmAirlineMessageExtension;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;

public class AttributeFilterTest {
    AttributeFilter filter = new AttributeFilter();

    @Before
    public void init() {
        filter.setActive(true);
        Map<FuserSource, List<String>> includesArrival = new HashMap<FuserSource, List<String>> ();
        List<String> airlineList = new ArrayList<String> ();
        airlineList.add("extensions.matmAirlineMessageExtension.dataSource");
        airlineList.add("arrivalAerodrome.iataName");
        airlineList.add("wakeTurbulenceCategory");
        includesArrival.put(FuserSource.AIRLINE, airlineList);
        
        Map<FuserSource, List<String>> includesDeparture = new HashMap<FuserSource, List<String>> ();
        airlineList = new ArrayList<String> ();
        airlineList.add("departureFixActualTime");
        includesDeparture.put(FuserSource.AIRLINE, airlineList);    
        
        List<String> tfmList = new ArrayList<String> ();
        tfmList.add("extensions.tfmExtension.lastTfmPosition.altitude");
        tfmList.add("extensions.tfmExtension.lastTfmPosition.latitude");
        tfmList.add("extensions.tfmExtension.lastTfmPosition.longitude");
        tfmList.add("extensions.tfmExtension.lastTfmPosition.timestamp");
        tfmList.add("extensions.tfmExtension.diversion");
        includesArrival.put(FuserSource.TFM, tfmList);
        includesDeparture.put(FuserSource.TFM, tfmList);
        filter.setIncludesArrival(includesArrival);
        filter.setIncludesDeparture(includesDeparture);
        
        Map<FuserSource, List<String>> excludesArrival = new HashMap<FuserSource, List<String>> ();
        Map<FuserSource, List<String>> excludesDeparture = new HashMap<FuserSource, List<String>> ();
        List<String> asdexList = new ArrayList<String> ();
        asdexList.add("extensions.asdexExtension.lastAsdexPosition");
        excludesArrival.put(FuserSource.ASDEX, asdexList);
        excludesDeparture.put(FuserSource.ASDEX, asdexList);
        
        List<String> flighthubList = new ArrayList<String> ();
        flighthubList.add("arrivalFixActualTime");
        flighthubList.add("arrivalMovementAreaActualTime");
        flighthubList.add("arrivalRunwayActualTime");
        flighthubList.add("arrivalStandActualTime");
        flighthubList.add("extensions.asdexExtension");
        excludesArrival.put(FuserSource.FLIGHTHUB_POSITION, flighthubList);
        filter.setExcludesArrival(excludesArrival);
        
        flighthubList = new ArrayList<String> ();
        flighthubList.add("departureFixActualTime");
        flighthubList.add("departureMovementAreaActualTime");
        flighthubList.add("departureRunwayActualTime");
        flighthubList.add("departureStandActualTime");
        flighthubList.add("departureStartupRequestActualTime");
        flighthubList.add("departureStartupApprovalActualTime");
        flighthubList.add("departureStandReadyActualTime");
        flighthubList.add("departureQueueEntryActualTime");
        flighthubList.add("extensions.asdexExtension");
        excludesDeparture.put(FuserSource.FLIGHTHUB_POSITION, flighthubList);
        filter.setExcludesDeparture(excludesDeparture);
        
        includesArrival = new HashMap<FuserSource, List<String>> ();
        includesDeparture = new HashMap<FuserSource, List<String>> ();
        excludesArrival = new HashMap<FuserSource, List<String>> ();
        excludesDeparture = new HashMap<FuserSource, List<String>> ();
        filter.setExcludesDeparture(excludesDeparture, "DAL");
        filter.setExcludesArrival(excludesArrival, "DAL");
        filter.setIncludesArrival(includesArrival, "DAL");
        filter.setIncludesDeparture(includesDeparture, "DAL");

    }
    @Test
    public void includeTest1() throws DatatypeConfigurationException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            MatmFlight flight = new MatmFlight();
            flight.setLastUpdateSource(FuserSource.AIRLINE.toString());
            Date date = new Date();
            Date date1 = new Date();
            DatatypeFactory dataFactory = DatatypeFactory.newInstance();
            Duration duration = dataFactory.newDuration(1000);
            flight.setExtensions(new MatmFlightExtensions ());
            flight.getExtensions().setMatmAirlineMessageExtension(new MatmAirlineMessageExtension());
            flight.getExtensions().getMatmAirlineMessageExtension().setDataSource("data source");
            flight.getExtensions().getMatmAirlineMessageExtension().setAvailableRunways("runway");
            flight.getExtensions().setDerivedExtension(new DerivedExtension());
            flight.setDepartureFixActualTime(date);
            flight.setArrivalFixActualTime(date1);
            flight.setArrivalAerodrome(new Aerodrome());
            flight.getArrivalAerodrome().setIataName("KCLT");
            flight.setDepartureTaxiEstimatedDuration(duration);
            flight.setAcid("ABC123");
            flight.setWakeTurbulenceCategory(WakeTurbulenceCategory.A);
            filter.filter(flight);
            
            assertEquals(flight.getArrivalAerodrome().getIataName(), "KCLT");
            assertEquals(flight.getDepartureFixActualTime(), date);
            assertTrue(flight.getExtensions().getDerivedExtension() == null);
            assertTrue(flight.getAcid() == null);
            assertTrue(flight.getDepartureTaxiEstimatedDuration() == null);
            assertEquals(flight.getExtensions().getMatmAirlineMessageExtension().getDataSource(), "data source");
            assertTrue(flight.getExtensions().getMatmAirlineMessageExtension().getAvailableRunways() == null);
            assertTrue(flight.getWakeTurbulenceCategory() == WakeTurbulenceCategory.A);
            
    }
    
    @Test
    public void includeTest2() throws DatatypeConfigurationException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            MatmFlight flight = new MatmFlight();
            flight.setLastUpdateSource(FuserSource.TFM.toString());
            Date date = new Date();
            DatatypeFactory dataFactory = DatatypeFactory.newInstance();
            Duration duration = dataFactory.newDuration(1000);
            flight.setExtensions(new MatmFlightExtensions ());
            flight.getExtensions().setMatmAirlineMessageExtension(new MatmAirlineMessageExtension());
            flight.getExtensions().getMatmAirlineMessageExtension().setDataSource("data source");
            flight.getExtensions().getMatmAirlineMessageExtension().setAvailableRunways("runway");
            flight.getExtensions().setDerivedExtension(new DerivedExtension());
            flight.setDepartureFixActualTime(date);
            flight.setArrivalAerodrome(new Aerodrome());
            flight.getArrivalAerodrome().setIataName("CLT");
            flight.setDepartureTaxiEstimatedDuration(duration);
            flight.setAcid("ABC123");

            flight.getExtensions().setTfmExtension(new TfmExtension());
            flight.getExtensions().getTfmExtension().setDiversion(true);
            Position position = new Position();
            position.setAltitude(10.0);
            position.setHeading(11.0);
            position.setLatitude(12.0);
            position.setLongitude(13.0);
            position.setTimestamp(date);
            flight.getExtensions().getTfmExtension().setLastTfmPosition(position);
            filter.filter(flight);
            assertTrue(flight.getArrivalAerodrome() == null);
            assertTrue(flight.getDepartureFixActualTime() == null);
            
            assertTrue(flight.getExtensions().getDerivedExtension() == null);
            assertTrue(flight.getAcid() == null);
            assertTrue(flight.getDepartureTaxiEstimatedDuration() == null);
            assertTrue(flight.getExtensions().getMatmAirlineMessageExtension() == null);
            assertTrue(flight.getExtensions().getTfmExtension().isDiversion().booleanValue() == true);
            assertTrue(flight.getExtensions().getTfmExtension().getLastTfmPosition().getAltitude() == 10.0);
            assertTrue(flight.getExtensions().getTfmExtension().getLastTfmPosition().getHeading() == null);
            assertTrue(flight.getExtensions().getTfmExtension().getLastTfmPosition().getLatitude() == 12.0);
            assertTrue(flight.getExtensions().getTfmExtension().getLastTfmPosition().getLongitude() == 13.0);
            assertTrue(flight.getExtensions().getTfmExtension().getLastTfmPosition().getTimestamp() == date);
        //  PropertyUtils.getProperty(flight, "extensions.tfmExtension.diversion");
    }
    @Test
    public void includeTest3() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MatmFlight flight = new MatmFlight();
        flight.setLastUpdateSource(FuserSource.FIXM.toString());
        flight.setExtensions(new MatmFlightExtensions ());
        flight.getExtensions().setMatmAirlineMessageExtension(new MatmAirlineMessageExtension());
        flight.getExtensions().getMatmAirlineMessageExtension().setDataSource("data source");
        flight.getExtensions().getMatmAirlineMessageExtension().setAvailableRunways("runway");
        flight.getExtensions().setAsdexExtension(new AsdexExtension());
        AsdexExtension asdex = flight.getExtensions().getAsdexExtension();
        asdex.setAsdexAirport(new Aerodrome());
        asdex.getAsdexAirport().setIataName("KDFW");
        asdex.getAsdexAirport().setIcaoName("DFW");
        asdex.setTrackId(1000);
        filter.filter(flight);
        assertTrue(flight.getExtensions().getDerivedExtension() == null);
        assertTrue(flight.getAcid() == null);
        assertTrue(flight.getDepartureTaxiEstimatedDuration() == null);
        assertEquals(flight.getExtensions().getMatmAirlineMessageExtension().getDataSource(), "data source");
        assertEquals(flight.getExtensions().getMatmAirlineMessageExtension().getAvailableRunways(), "runway");
        
    }
    
    @Test
    public void excludeTest1() throws DatatypeConfigurationException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        MatmFlight flight = new MatmFlight();
        flight.setLastUpdateSource(FuserSource.ASDEX.toString());
        flight.setExtensions(new MatmFlightExtensions ());
        flight.getExtensions().setMatmAirlineMessageExtension(new MatmAirlineMessageExtension());
        flight.getExtensions().getMatmAirlineMessageExtension().setDataSource("data source");
        flight.getExtensions().getMatmAirlineMessageExtension().setAvailableRunways("runway");
        flight.getExtensions().setAsdexExtension(new AsdexExtension());
        AsdexExtension asdex = flight.getExtensions().getAsdexExtension();
        asdex.setAsdexAirport(new Aerodrome());
        asdex.getAsdexAirport().setIataName("KDFW");
        asdex.getAsdexAirport().setIcaoName("DFW");
        asdex.setTrackId(1000);
        filter.filter(flight);
        
        assertEquals(flight.getExtensions().getAsdexExtension().getAsdexAirport().getIataName(), "KDFW");
        assertEquals(flight.getExtensions().getAsdexExtension().getAsdexAirport().getIcaoName(), "DFW");
        assertEquals(flight.getExtensions().getAsdexExtension().getTrackId(), new Integer(1000));
        assertTrue(flight.getExtensions().getAsdexExtension().getLastAsdexPosition() == null);
        assertEquals(flight.getExtensions().getMatmAirlineMessageExtension().getDataSource(), "data source");
        assertEquals(flight.getExtensions().getMatmAirlineMessageExtension().getAvailableRunways(), "runway");
    }
    
    @Test
    public void excludeTest2() {
        MatmFlight flight = new MatmFlight();
        flight.setLastUpdateSource(FuserSource.FLIGHTHUB_POSITION.toString());
        flight.setExtensions(new MatmFlightExtensions ());
        flight.setArrivalFixActualTime(new Date());
        flight.setArrivalMovementAreaActualTime(new Date());
        flight.setArrivalRunwayActualTime(new Date());
        flight.setGufi("gufi");
        Aerodrome arrival = new Aerodrome();
        arrival.setIataName("DFW");
        flight.setArrivalAerodrome(arrival);
        MatmFlightExtensions extensions = new MatmFlightExtensions ();
        flight.setExtensions(extensions);
        flight.setArrivalFixActual("FIX");
        flight.getExtensions().setDerivedExtension(new DerivedExtension());
        flight.getExtensions().getDerivedExtension().setArrivalMissedApproach(false);
        filter.filter(flight);
        assertTrue(flight.getArrivalFixActualTime() == null);
        assertTrue(flight.getArrivalMovementAreaActualTime() == null);
        assertTrue(flight.getArrivalRunwayActualTime() == null);
        assertTrue(flight.getArrivalStandActualTime() == null);
        assertTrue(flight.getDepartureFixActualTime() == null);
        assertTrue(flight.getDepartureMovementAreaActualTime() == null);
        assertTrue(flight.getDepartureRunwayActualTime() == null);
        assertTrue(flight.getDepartureStandActualTime() == null);
        assertTrue(flight.getDepartureStartupApprovalActualTime() == null);
        assertTrue(flight.getDepartureStartupRequestActualTime() == null);
        assertTrue(flight.getDepartureStandReadyActualTime() == null);
        assertTrue(flight.getDepartureQueueEntryActualTime() == null);
        assertTrue(flight.getExtensions() != null);
        assertTrue(flight.getExtensions().getDerivedExtension() != null);
        assertTrue(flight.getExtensions().getDerivedExtension().isArrivalMissedApproach() != null);
        assertTrue(flight.getExtensions().getDerivedExtension().isArrivalMissedApproach().booleanValue() == false);
        assertEquals(flight.getGufi(), "gufi");
        assertEquals(flight.getExtensions(), extensions);
        assertEquals(flight.getArrivalFixActual(), "FIX");
        
    }
    
    @Test
    public void excludeTest3() {
        MatmFlight flight = new MatmFlight();
        flight.setLastUpdateSource(FuserSource.FLIGHTHUB_POSITION.toString());
        flight.setExtensions(new MatmFlightExtensions ());
        flight.setArrivalFixActualTime(new Date());
        flight.setArrivalMovementAreaActualTime(new Date());
        flight.setArrivalRunwayActualTime(new Date());
        flight.setGufi("gufi");
        Aerodrome arrival = new Aerodrome();
        arrival.setIataName("DAL");
        flight.setArrivalAerodrome(arrival);
        MatmFlightExtensions extensions = new MatmFlightExtensions ();
        flight.setExtensions(extensions);
        flight.setArrivalFixActual("FIX");
        flight.getExtensions().setDerivedExtension(new DerivedExtension());
        flight.getExtensions().getDerivedExtension().setArrivalMissedApproach(false);
        filter.filter(flight);
        assertTrue(flight.getArrivalFixActualTime() != null);
        assertTrue(flight.getArrivalMovementAreaActualTime() != null);
        assertTrue(flight.getArrivalRunwayActualTime() != null);
        
    }
    
    @Test
    public void airportsXmlLocationTest() {
        AttributeFilter filterManager = new AttributeFilter();
        filterManager.setActive(true);
        filterManager.setDefaultLocation("src/test/resources/AttributeFilterTest.xml");
        filterManager.setAttributeMappings("src/test/resources/metroplex_attribute_properties_test.json");
        filterManager.init();
        Map<String, AirportAttributeLists> map = filterManager.getAirportArrivalAttributrFilters();
        assertTrue(map.keySet().contains("Default"));
        assertTrue(map.keySet().contains("A"));
        assertTrue(map.keySet().contains("B"));

        map = filterManager.getAirportDepartureAttributrFilters();
        assertTrue(map.keySet().contains("Default"));
        assertTrue(map.keySet().contains("A"));
        assertTrue(map.keySet().contains("B"));
        
    }
}
