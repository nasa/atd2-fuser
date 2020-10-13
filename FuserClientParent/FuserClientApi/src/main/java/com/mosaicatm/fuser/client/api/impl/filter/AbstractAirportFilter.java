package com.mosaicatm.fuser.client.api.impl.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mosaicatm.lib.util.filter.Filter;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.flight.MatmFlight;


public abstract class AbstractAirportFilter
implements Filter<MatmFlight>
{
    private boolean active;
    
    private List<String> airportsOfInterest;
    
    public AbstractAirportFilter()
    {
    	
    }
    
    public AbstractAirportFilter(List<String> airports)
    {
    	setAirportsOfInterest(airports);
    }
    
    public AbstractAirportFilter(String airports)
    {
    	setAirportsOfInterestFromString(airports);
    }
    
    //@Override
    public abstract boolean doFilter(MatmFlight flight);
    
    
    protected String getAerodromeIata (Aerodrome aerodrome)
    {
        if (aerodrome != null)
            return aerodrome.getIataName();
        return null;
    }
    
    protected String getAerodromeIcao (Aerodrome aerodrome)
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
    
    public void setAirportsOfInterest(List<String> airports)
    {
    	this.airportsOfInterest = airports;
    }
    
    public void setAirportsOfInterestFromString(String airports)
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
