package com.mosaicatm.fuser.client.api.impl.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class SurfaceAirportFilter
extends AbstractAirportFilter
{
    private final Log log = LogFactory.getLog(getClass());

    
    public SurfaceAirportFilter()
    {
    	super();
    }
    
    public SurfaceAirportFilter(List<String> airports)
    {
    	super(airports);
    }
    
    public SurfaceAirportFilter(String airports)
    {
    	super(airports);
    }
    
    @Override
    public boolean doFilter(MatmFlight flight)
    {
        if (!isActive())
            return true;
        
        if (flight == null)
            return false;
        
        if (getAirportsOfInterest() == null || getAirportsOfInterest().isEmpty())
        {
        	log.warn("You have an asdex airport filter active but no airports of interest defined,"
           	   		+ " this will result in all flights being filtered");
        	return false;
        }
        
       
        boolean isOfInterest = false;
        
        
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
            isOfInterest = isOfInterest || getAirportsOfInterest().contains(airport);
        
        return isOfInterest;
    }
    
}
