package com.mosaicatm.fuser.datacapture.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.util.filter.Filter;
import com.mosaicatm.matmdata.position.MatmPositionUpdate;

public class AirportFilter
implements Filter
{
    private final Log log = LogFactory.getLog(getClass());

    private boolean active;
    
    private List<String> airportsOfInterest;
    
    @Override
    public boolean doFilter(Object data)
    {
        // filter is disabled, everything passes
        if (!isActive())
            return true;
        
        if (data == null)
            return false;
        
        if (airportsOfInterest == null)
            return false;
        
        if (!(data instanceof MatmPositionUpdate))
        	return false;
        
        boolean isOfInterest = false;
        
        MatmPositionUpdate position = (MatmPositionUpdate)data;
        
        List<String> messageAirports = new ArrayList<>();
        
        String arrivalIata = position.getArrivalAerodromeIataName();
        if (arrivalIata != null && !arrivalIata.trim().isEmpty())
        {
        	messageAirports.add(arrivalIata);   
        }
        
        String departureIata = position.getDepartureAerodromeIataName();
        if (departureIata != null && !departureIata.trim().isEmpty())
        {
        	messageAirports.add(departureIata);
        }
        
        for (String airport : messageAirports)
            isOfInterest = isOfInterest || airportsOfInterest.contains(airport);
        
        return isOfInterest;
    }

    public List<String> getAirportsOfInterest()
    {
        if (airportsOfInterest == null)
            airportsOfInterest = new ArrayList<>();
        return airportsOfInterest;
    }
    
    public void setAirportsOfInterest(String airports)
    {
        String[] apts = airports.split(",");
        getAirportsOfInterest().addAll(Arrays.asList(apts));
    }
    
    public boolean isActive()
    {
        return active;
    }
    
    public void setActive(boolean active)
    {
        this.active = active;
    }
}
