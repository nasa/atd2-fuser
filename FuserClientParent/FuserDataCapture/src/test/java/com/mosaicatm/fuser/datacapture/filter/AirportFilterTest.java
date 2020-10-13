package com.mosaicatm.fuser.datacapture.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.matmdata.position.MatmPositionUpdate;

public class AirportFilterTest
{
    private AirportFilter filter;
    
    @Before
    public void setup ()
    {
        filter = new AirportFilter();
        filter.setActive(true);
        filter.setAirportsOfInterest("CLT,DFW");
    }
    
    @Test
    public void testValidArrivalAirportFilter()
    {
    	MatmPositionUpdate positionUpdate = new MatmPositionUpdate();
    	
    	positionUpdate.setArrivalAerodromeIataName("CLT");
        
    	positionUpdate.setDepartureAerodromeIataName("LGA");
                
        assertTrue(filter.doFilter(positionUpdate));
    }
    
    @Test
    public void testInvalidArrivalAirportFilter()
    {
    	MatmPositionUpdate positionUpdate = new MatmPositionUpdate();
    	
    	positionUpdate.setArrivalAerodromeIataName("JFK");
        
    	positionUpdate.setDepartureAerodromeIataName("LGA");  
    	
        assertFalse(filter.doFilter(positionUpdate));
    }
    
    @Test
    public void testValidDepartureAirportFilter()
    {
    	MatmPositionUpdate positionUpdate = new MatmPositionUpdate();
    	
    	positionUpdate.setArrivalAerodromeIataName("JFK");
        
    	positionUpdate.setDepartureAerodromeIataName("DFW");
        
        assertTrue(filter.doFilter(positionUpdate));
    }
    
    @Test
    public void testInvalidDepartureAirportFilter()
    {
    	MatmPositionUpdate positionUpdate = new MatmPositionUpdate();
    	
    	positionUpdate.setArrivalAerodromeIataName("LGA");
        
    	positionUpdate.setDepartureAerodromeIataName("JFK");
        
        assertFalse(filter.doFilter(positionUpdate));
    }
    
    @Test
    public void testAirportsOfInterestUnset ()
    {
        filter = new AirportFilter();
        filter.setActive(true);
        
        MatmPositionUpdate positionUpdate = new MatmPositionUpdate();
    	
    	positionUpdate.setArrivalAerodromeIataName("CLT");
        
    	positionUpdate.setDepartureAerodromeIataName("DFW");
        
        assertFalse(filter.doFilter(positionUpdate));
    }
    
    @Test
    public void testDisabledFilter ()
    {
        filter.setActive(false);
        
        MatmPositionUpdate positionUpdate = new MatmPositionUpdate();
    	
    	positionUpdate.setArrivalAerodromeIataName("JFK");
        
    	positionUpdate.setDepartureAerodromeIataName("LGA");
        
        assertTrue(filter.doFilter(positionUpdate));
    }
}
