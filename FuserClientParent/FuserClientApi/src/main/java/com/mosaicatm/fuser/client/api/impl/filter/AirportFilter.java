package com.mosaicatm.fuser.client.api.impl.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.flight.MatmFlight;


public class AirportFilter
extends AbstractAirportFilter
{
    private final Log log = LogFactory.getLog(getClass());
    
    public AirportFilter()
    {
    	super();
    }
    
    public AirportFilter(List<String> airports)
    {
    	super(airports);
    }
    
    public AirportFilter(String airports)
    {
    	super(airports);
    }
    
    @Override
    public boolean doFilter(MatmFlight flight)
    {
        // filter is disabled, everything passes
        if (!isActive())
            return true;
        
        if (flight == null)
            return false;
        
       if (getAirportsOfInterest() == null || getAirportsOfInterest().isEmpty())
       {
    	   log.warn("You have an airport filter active but no airports of interest defined,"
    	   		+ " this will result in all flights being filtered");
    	   return false;
       }
        
              
        boolean isOfInterest = false;
        
        List<String> messageAirports = new ArrayList<>();
        
        if (flight != null)
        {
            String iata = getAerodromeIata(flight.getArrivalAerodrome());
            if (iata != null && !iata.trim().isEmpty())
                messageAirports.add(iata);
            
            String icao = getAerodromeIcao(flight.getArrivalAerodrome());
            if (icao != null && !icao.trim().isEmpty())
                messageAirports.add(icao);
            
        }
        
        if (flight.getDepartureAerodrome() != null)
        {
            String iata = getAerodromeIata(flight.getDepartureAerodrome());
            if (iata != null && !iata.trim().isEmpty())
                messageAirports.add(iata);
            
            String icao = getAerodromeIcao(flight.getDepartureAerodrome());
            if (icao != null && !icao.trim().isEmpty())
                messageAirports.add(icao);
        }
        
        for (String airport : messageAirports)
            isOfInterest = isOfInterest || getAirportsOfInterest().contains(airport);
        
        return isOfInterest;
    }
    
}
