package com.mosaicatm.fuser.datacapture.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.util.filter.Filter;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class SurfaceAirportFilter
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
        
        if (!(data instanceof MatmFlight))
            return false;
        
        boolean isOfInterest = false;
        
        MatmFlight flight = (MatmFlight)data;
        
        List<String> messageAirports = new ArrayList<>();
        
        if (flight.getSurfaceAirport() != null)
        {
            String iata = getAerodromeIata(flight.getSurfaceAirport());
            if (iata != null && !iata.trim().isEmpty())
                messageAirports.add(iata);
            
            String icao = getAerodromeIcao(flight.getSurfaceAirport());
            if (icao != null && !icao.trim().isEmpty())
                messageAirports.add(icao);
        }
        
        for (String airport : messageAirports)
            isOfInterest = isOfInterest || airportsOfInterest.contains(airport);
        
        return isOfInterest;
    }
    
    private String getAerodromeIata (Aerodrome aerodrome)
    {
        if (aerodrome != null)
            return aerodrome.getIataName();
        return null;
    }
    
    private String getAerodromeIcao (Aerodrome aerodrome)
    {
        if (aerodrome != null)
            return aerodrome.getIcaoName();
        return null;
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
