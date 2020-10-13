package com.mosaicatm.fuser.datacapture.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.datacapture.filter.SurfaceAirportFilter;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.AsdexExtension;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;

public class SurfaceAirportFilterTest
{
    private SurfaceAirportFilter filter;
    
    @Before
    public void setup ()
    {
        filter = new SurfaceAirportFilter();
        filter.setActive(true);
        filter.setAirportsOfInterest("CLT");
    }
    
    @Test
    public void testValidAirport ()
    {
        Aerodrome airport = new Aerodrome();
        airport.setIcaoName("CLT");
        
        MatmFlight matmFlight = new MatmFlight();
        matmFlight.setSurfaceAirport(airport);
        
        assertTrue(filter.doFilter(matmFlight));
    }
    
    @Test
    public void testInvalidAirport ()
    {
        Aerodrome airport = new Aerodrome();
        airport.setIcaoName("LGA");
        
        MatmFlightExtensions extension = new MatmFlightExtensions ();
        
        AsdexExtension asdex = new AsdexExtension();
        asdex.setAsdexAirport(airport);
        
        extension.setAsdexExtension(asdex);
        
        MatmFlight matmFlight = new MatmFlight();
        matmFlight.setLastUpdateSource("ASDEX");
        matmFlight.setExtensions(extension);
        
        assertFalse(filter.doFilter(matmFlight));
    }
    
    @Test
    public void testAirportsOfInterestUnset ()
    {
        filter = new SurfaceAirportFilter();
        filter.setActive(true);
        
        Aerodrome airport = new Aerodrome();
        airport.setIcaoName("CLT");
        
        MatmFlightExtensions extension = new MatmFlightExtensions ();
        
        AsdexExtension asdex = new AsdexExtension();
        asdex.setAsdexAirport(airport);
        
        extension.setAsdexExtension(asdex);
        
        MatmFlight matmFlight = new MatmFlight();
        matmFlight.setLastUpdateSource("ASDEX");
        matmFlight.setExtensions(extension);
        
        assertFalse(filter.doFilter(matmFlight));
    }
    
    @Test
    public void testDisabledFilter ()
    {
        filter.setActive(false);
        
        Aerodrome airport = new Aerodrome();
        airport.setIcaoName("LGA");
        
        MatmFlightExtensions extension = new MatmFlightExtensions ();
        
        AsdexExtension asdex = new AsdexExtension();
        asdex.setAsdexAirport(airport);
        
        extension.setAsdexExtension(asdex);
        
        MatmFlight matmFlight = new MatmFlight();
        matmFlight.setLastUpdateSource("ASDEX");
        matmFlight.setExtensions(extension);
        
        assertTrue(filter.doFilter(matmFlight));
    }
}
