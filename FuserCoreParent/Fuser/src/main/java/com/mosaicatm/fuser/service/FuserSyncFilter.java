package com.mosaicatm.fuser.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class FuserSyncFilter
{
    private static final Logger logger = LoggerFactory.getLogger( FuserSyncFilter.class );
    
    private Boolean airportFilterActive = false;
    private Boolean surfaceFilterActive = false;
    private List<String> airports;

    
    /**
     * A filter to "filter in" data. As in, a return value of true means the MatmFlight passed the filters,
     * a return value of false means it did not.
     * @param matmFlight    The MatmFlight to test against the filters
     * @return  true if the matmFlight passed the filters or no filters active, false otherwise
     */
    public boolean doFilter( MatmFlight matmFlight )
    {
        boolean passAirportFilter = airportFilter( matmFlight );
        boolean passSurfaceFilter = surfaceFilter( matmFlight );        

        if( logger.isDebugEnabled() )
        {
            logger.debug( "Filtering results:" );
            logger.debug( "airportFilterActive = {}", airportFilterActive );
            logger.debug( "passed airport filter = {}", passAirportFilter );
            logger.debug( "surfaceFilterActive = {}", surfaceFilterActive );
            logger.debug( "Passed surface filter = {}", passSurfaceFilter );
        }
        
        boolean passedAirportFilters = 
                (!airportFilterActive && !surfaceFilterActive) ||   // both filters inactive
                (airportFilterActive && passAirportFilter) ||       // airport filter active and passed
                (surfaceFilterActive && passSurfaceFilter);         // surface filter active and passed
        
        return passedAirportFilters;
    }
    
    private boolean airportFilter( MatmFlight matmFlight )
    {
        boolean passAirportFilter = !airportFilterActive;
        if( airportFilterActive )
        {
            for( String airport : airports )
            {
                if( isAerodromeOfInterest( airport, matmFlight.getArrivalAerodrome() ) ||
                        isAerodromeOfInterest(airport, matmFlight.getDepartureAerodrome() ) )
                {
                    passAirportFilter = true;
                    break;
                }
            }
        }
        
        return passAirportFilter;
    }
    
    private boolean surfaceFilter( MatmFlight matmFlight )
    {
        boolean passSurfaceFilter = !surfaceFilterActive;
        if( surfaceFilterActive )
        {
            for( String airport : airports )
            {
                if( isAerodromeOfInterest( airport, matmFlight.getSurfaceAirport() ) )
                {
                    passSurfaceFilter = true;
                    break;
                }
            }
        }
        
        return passSurfaceFilter;
    }

    public Boolean getAirportFilterActive()
    {
        return airportFilterActive;
    }
    
    public void setAirportFilterActive( Boolean airportFilterActive )
    {
        this.airportFilterActive = airportFilterActive;
    }

    public Boolean getSurfaceFilterActive()
    {
        return surfaceFilterActive;
    }
    
    public void setSurfaceFilterActive( Boolean surfaceFilterActive )
    {
        this.surfaceFilterActive = surfaceFilterActive;
    }

    public List<String> getAirports()
    {
        return airports;
    }
    
    public void setAirports( List<String> airports )
    {
        this.airports = airports;
    }

    
    private boolean isAerodromeOfInterest (String airport, Aerodrome aerodrome)
    {
        boolean interest = false;
        
        if (airport != null && !airport.trim().isEmpty())
        {
            if (aerodrome != null )
            {
                if( aerodrome.getIataName() != null )
                {
                    interest = airport.equals( aerodrome.getIataName() );
                }
                if( !interest && aerodrome.getIcaoName() != null )
                {
                    interest = airport.equals( aerodrome.getIcaoName() );
                }
            }
        }
        
        return interest;
    }
}
