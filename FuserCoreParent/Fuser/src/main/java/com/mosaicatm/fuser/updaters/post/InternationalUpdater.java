package com.mosaicatm.fuser.updaters.post;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.aptcode.data.AirportCodeEntry;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.fuser.util.FuserAirportCodeTranslator;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * Sets the international flag for a flight.
 */
public class InternationalUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{
    // The country for CONUS in airportCodeTranslator is "USA". 
    // For non-CONUS it is something like this: "USA (Alaska)"
    private static final String UNITED_STATES_CONUS_NAME = "USA";
    
    private final Log log = LogFactory.getLog(getClass());
    
    private FuserAirportCodeTranslator airportCodeTranslator;
    
    @Override
    public void update(MatmFlight update, MatmFlight target)
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update international status. Update is NULL!");
            return;
        }
        
        if( airportCodeTranslator == null )
        {
            log.error("Cannot update international status. airportCodeTranslator is NULL!");
            return;
        }        
        
        updateInternational( update );
    }
    
    public void setAirportCodeTranslator(FuserAirportCodeTranslator airportCodeTranslator)
    {
        this.airportCodeTranslator = airportCodeTranslator;
    }      

    /**
     * Set international if either airport is international, and
     * set NOT international if both airports are NOT international, 
     * otherwise NULL   
     * @param update the update
     */    
    private void updateInternational( MatmFlight update )
    {
        // Try to set international
        Boolean deptIntl = null;
        if( update.getDepartureAerodrome() != null )
        {
            AirportCodeEntry code = airportCodeTranslator.getBestMatchAirport( update.getDepartureAerodrome() );
            if( code != null )
            {
                deptIntl = isInternational( code );
            }
        }

        if( Objects.equals( Boolean.TRUE, deptIntl ))
        {
            update.setInternational( true );
        }
        else
        {
            Boolean arrIntl = null;
            if( update.getArrivalAerodrome() != null )
            {
                AirportCodeEntry code = airportCodeTranslator.getBestMatchAirport( update.getArrivalAerodrome() );
                if( code != null )
                {
                    arrIntl = isInternational( code );
                }
            }

            if( Objects.equals( Boolean.TRUE, arrIntl ))
            {
                update.setInternational( true );
            }
            else if( Objects.equals( Boolean.FALSE, deptIntl ) && Objects.equals( Boolean.FALSE, arrIntl ))
            {
                update.setInternational( false );
            }
        }

        if (log.isDebugEnabled())
        {
            log.debug( update.getAcid() + "/" + update.getGufi() + 
                       ": setting international to " +
                       update.isInternational() );
        }            
    }    
    
    /**
     * Checks if airport code is international.
     * 
     * @param code The airport code. Null if nationality cannot be determined.
     */    
    private Boolean isInternational( AirportCodeEntry code )
    {
        String country = null;
        if( code.getCountry() != null )
        {
            country = code.getCountry().trim();
        }
        
        if(( country != null ) && ( !country.isEmpty() ))
        {
            return( !country.equals( UNITED_STATES_CONUS_NAME ));
        }
        else if( code.getIcao() != null )
        {
            return( !code.getIcao().startsWith( "K" ));
        }
        
        return( null );
    }     
}
