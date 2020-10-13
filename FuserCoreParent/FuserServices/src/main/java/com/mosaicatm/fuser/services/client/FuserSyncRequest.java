package com.mosaicatm.fuser.services.client;

import java.util.Arrays;
import java.util.List;


public class FuserSyncRequest
{
    private Boolean airportFilterActive = false;
    private Boolean surfaceFilterActive = false;
    private List<String> airports;

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
    
    public void setAirportsFromString( String airports )
    {
        this.airports = commaSeparatedStringToList( airports );
    }
    
    private List<String> commaSeparatedStringToList( String commaSeparatedString )
    {
        if( commaSeparatedString == null || commaSeparatedString.trim().isEmpty() )
        {
            return null;
        }

        String[] pieces = commaSeparatedString.split(",");
        for( int x = 0; x < pieces.length; x++ )
        {
            pieces[x] = pieces[x].trim();
        }
        return Arrays.asList(pieces);
    }
}
