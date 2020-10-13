package com.mosaicatm.fuser.filter;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.MeteredTime;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class FuserFilterTest {

    @Test
    public void testLocationFilter(){
        FuserFilter<MatmFlight> filter = new FuserFilter<>();
        LocationFilter locationFilter = new LocationFilter();
        locationFilter.setAirports("CLT,DFW");
        locationFilter.setActive(true);
        filter.addFilter(locationFilter);
        filter.setActive(true);
        
        MatmFlight cltFlight = new MatmFlight();
        Aerodrome arrival = new Aerodrome();
        arrival.setIataName("CLT");
        cltFlight.setArrivalAerodrome(arrival);
        
        MatmFlight dfwFlight = new MatmFlight();
        Aerodrome departureDfw = new Aerodrome();
        departureDfw.setIataName("DFW");
        dfwFlight.setArrivalAerodrome(departureDfw);
        
        MatmFlight iadFlight = new MatmFlight();
        Aerodrome departureIad = new Aerodrome();
        departureIad.setIataName("IAD");
        iadFlight.setArrivalAerodrome(departureIad);
        
        MatmFlight asdexFlight = new MatmFlight();
        Aerodrome surfaceCltFlight = new Aerodrome();
        surfaceCltFlight.setIataName("CLT");
        asdexFlight.setSurfaceAirport(surfaceCltFlight);
        
        MatmFlight nullFlight = new MatmFlight();
        
        MatmFlight filteredCltFlight = filter.filter(cltFlight);
        MatmFlight filteredDfwFlight = filter.filter(dfwFlight);
        MatmFlight filteredIadFlight = filter.filter(iadFlight);
        MatmFlight filteredNullFlight = filter.filter(nullFlight);
        MatmFlight filteredSurfaceFlight = filter.filter(asdexFlight);
        
        assertNotNull(filteredCltFlight);
        assertNotNull(filteredDfwFlight);
        assertNotNull(filteredSurfaceFlight);
        assertNull(filteredIadFlight);
        assertNull(filteredNullFlight);
        
    }
    
    @Test
    public void testGufiFilter(){
        FuserFilter<MatmFlight> filter = new FuserFilter<>();
        GufiFilter gufiFilter = new GufiFilter();
        gufiFilter.setActive(true);
        filter.addFilter(gufiFilter);
        filter.setActive(true);
        
        MatmFlight flight = new MatmFlight();
        flight.setGufi("gufi");
        
        MatmFlight flightGufiIgnore = new MatmFlight();
        flightGufiIgnore.setGufi("IGNORE");
        
        MatmFlight flightGufiNull = new MatmFlight();
        
        MatmFlight filteredFlight = filter.filter(flight);
        MatmFlight filteredFlightGufiIgnore = filter.filter(flightGufiIgnore);
        MatmFlight filteredFlightGufiNull = filter.filter(flightGufiNull);
        
        assertNotNull(filteredFlight);
        assertNull(filteredFlightGufiNull);
        assertNull(filteredFlightGufiIgnore);
    }
    
    @Test
    public void testNasaTmaArrivalPredictionsFilter(){
        FuserFilter<MatmFlight> filter = new FuserFilter<>();
        filter.setActive(true);
        
        NasaTmaArrivalPredictionsFilter runwayFilter = new NasaTmaArrivalPredictionsFilter();
        runwayFilter.setActive(true);
        filter.addFilter(runwayFilter);
        
        // First test with a NASA TMA flgiht, arrival predictions should be removed
        MatmFlight flight = new MatmFlight();
        flight.setLastUpdateSource( "TMA" );
        flight.setSystemId( "ZDC.TMA.GOV-NASA" );
        flight.setArrivalRunwayAssigned("36L");
        flight.setArrivalRunwayModel("18R");
        flight.setArrivalRunwayEstimatedTime( new Date() );
        flight.setDepartureRunwayEstimatedTime( new Date() );
        
        MeteredTime mt = new MeteredTime();
        mt.setFrozen( Boolean.FALSE );
        mt.setValue( new Date() );
        flight.setArrivalRunwayMeteredTime( mt );
        
        MatmFlight filteredFlight = filter.filter(flight);
        
        assertNotNull(filteredFlight);
        assertNull(filteredFlight.getArrivalRunwayAssigned());
        assertNull(filteredFlight.getArrivalRunwayEstimatedTime());
        assertNull(filteredFlight.getArrivalRunwayMeteredTime());
        assertNotNull(filteredFlight.getArrivalRunwayModel());
        assertNotNull(filteredFlight.getDepartureRunwayEstimatedTime());
        
        // First test with a SWIM TMA flgiht, arrival predictions should NOT be removed
        flight = new MatmFlight();
        flight.setLastUpdateSource( "TMA" );
        flight.setSystemId( "ZDC.TMA.GOV-SWIM" );
        flight.setArrivalRunwayAssigned("36L");
        flight.setArrivalRunwayModel("18R");
        flight.setArrivalRunwayEstimatedTime( new Date() );
        flight.setDepartureRunwayEstimatedTime( new Date() );
        
        mt = new MeteredTime();
        mt.setFrozen( Boolean.FALSE );
        mt.setValue( new Date() );
        flight.setArrivalRunwayMeteredTime( mt );
        
        filteredFlight = filter.filter(flight);
        
        assertNotNull(filteredFlight);
        assertNotNull(filteredFlight.getArrivalRunwayAssigned());
        assertNotNull(filteredFlight.getArrivalRunwayEstimatedTime());
        assertNotNull(filteredFlight.getArrivalRunwayMeteredTime());
        assertNotNull(filteredFlight.getArrivalRunwayModel());
        assertNotNull(filteredFlight.getDepartureRunwayEstimatedTime());        
    }
}
