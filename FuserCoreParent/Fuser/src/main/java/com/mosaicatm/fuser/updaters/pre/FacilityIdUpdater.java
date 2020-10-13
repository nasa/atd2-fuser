package com.mosaicatm.fuser.updaters.pre;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * Corrects data related to en route facilities. E.g. sourceFacility, computerId, beaconCode
 */
public class FacilityIdUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{
    private static final String[] US_4_LETTER_FACILITY_CODES = 
    {
        // CONUS ARTCCs
        "KZAB", "KZAU", "KZBW", "KZDC", "KZDV", "KZFW", "KZHU", "KZID", "KZJX", "KZKC",
        "KZLA", "KZLC", "KZMA", "KZME", "KZMP", "KZNY", "KZOA", "KZOB", "KZSE", "KZTL",
        
        // Oceanic ARTCCs
        "KONY", "KOOA"
    };

    private static final String[] IGNORE_4_LETTER_FACILITY_CODES = 
    {
        "TFMS"
    };
    
    private static final String[] ALLOWABLE_3_LETTER_FACILITY_CODES = 
    {
        "ZPA", // Pacific / Australia
        "ZSA" // South America
    };    
    
    private static final Set<String> US_4_LETTER_FACILITY_CODE_SET = 
            new HashSet<>( Arrays.asList( US_4_LETTER_FACILITY_CODES ));

    private static final Set<String> IGNORE_4_LETTER_FACILITY_CODE_SET = 
            new HashSet<>( Arrays.asList( IGNORE_4_LETTER_FACILITY_CODES ));

    private static final Set<String> ALLOWABLE_3_LETTER_FACILITY_CODES_SET = 
            new HashSet<>( Arrays.asList( ALLOWABLE_3_LETTER_FACILITY_CODES ));
    
    private static final Map<String,String> REPLACE_FACILITY_CODES_SET;    
    
    static
    {
        REPLACE_FACILITY_CODES_SET = new HashMap<>();
        REPLACE_FACILITY_CODES_SET.put( "KZAP", "PZAN" ); // KZAP is sometimes in TFMS for Anchorage
    }
    
    private final Log log = LogFactory.getLog(getClass());    
    
    @Override
    public void update( MatmFlight update, MatmFlight target )
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update facility ID data. Update is NULL!");
            return;
        }
        
        updateSourceFacility( update, target );        
        updateComputerId( update, target );
        updateBeacondCode( update, target );
    }
    
    public void updateSourceFacility( MatmFlight update, MatmFlight target )
    {
        String facility = update.getSourceFacility();

        if(( facility == null ) || ( target != null ) && 
                Objects.equals( facility, target.getSourceFacility() ))
        {
            return;            
        }         
        
        // Fix 3-letter codes (e.g. ZOB) 
        if(( facility.length() == 3 ) && 
                US_4_LETTER_FACILITY_CODE_SET.contains( "K" + facility ))
        {
            facility = "K" + facility;
            update.setSourceFacility( facility );
            
            if( log.isDebugEnabled() )
            {
                log.debug( "Correcting 3-letter facility code : " + update.getGufi() + " / " + update.getSourceFacility() ); 
            }              
        }        
        
        if( REPLACE_FACILITY_CODES_SET.containsKey( facility ))
        {
            facility = REPLACE_FACILITY_CODES_SET.get( facility );
            update.setSourceFacility( facility );
            
            if( log.isDebugEnabled() )
            {
                log.debug( "Correcting replacement facility code : " + update.getGufi() + " / " + update.getSourceFacility() ); 
            }             
        }
 
        // Null out source facility and any related en route facility data if
        // 1. Facility is not 4 characters (e.g. AAL)
        // 2. Facility starts with K, but is not from a CONUS ERAM facility (e.g. KDFW)
        // 3. Facility is in the set of ignored facilities (e.g. TFMS)
        if((( facility.length() != 4 ) && !ALLOWABLE_3_LETTER_FACILITY_CODES_SET.contains( facility )) || 
                ( facility.startsWith( "K" ) && !US_4_LETTER_FACILITY_CODE_SET.contains( facility )) ||
                IGNORE_4_LETTER_FACILITY_CODE_SET.contains( facility ))
        {
            if( log.isDebugEnabled() )
            {
                log.debug( "Handling illegal facility code : " + update.getGufi() + " / " + update.getSourceFacility() ); 
            }               
            
            update.setSourceFacility( null );
            
            // Other elements associated with the sourceFacility
            update.setBeaconCode( null );            
            update.setComputerId( null );
            update.setEramGufi( null );
        }
    }
    
    public void updateComputerId( MatmFlight update, MatmFlight target )
    {
        if(( update.getComputerId() == null ) || ( target != null ) && 
                Objects.equals( update.getComputerId(), target.getComputerId() ))
        {
            return;            
        }           
        
        String cid = update.getComputerId();
        
        if( cid.isEmpty() || ( cid.length() > 3 ))
        {
            log.warn( "Handling illegal computerId: " + update.getGufi() + " / " + cid ); 
            update.setComputerId( null );
        }
        
        // Need to pad with zeros
        if( cid.length() < 3 )
        {
            if( log.isDebugEnabled() )
            {
                log.debug( "Correcting less than 3 character computerId : " + update.getGufi() + " / " + cid ); 
            }    

            update.setComputerId( padZeros( cid, 3 )); 
        }
    }
    
    public void updateBeacondCode( MatmFlight update, MatmFlight target )
    {
        if(( update.getBeaconCode() == null ) || ( target != null ) && 
                Objects.equals( update.getBeaconCode(), target.getBeaconCode() ))
        {
            return;            
        }      
        
        String beaconCode = update.getBeaconCode();
        
        if( beaconCode.isEmpty() || ( beaconCode.length() > 4 ))
        {
            log.warn( "Handling illegal beaconCode: " + update.getGufi() + " / " + beaconCode ); 
            update.setBeaconCode( null );
        }
        
        // Need to pad with zeros
        if( beaconCode.length() < 4 )
        {
            if( log.isDebugEnabled() )
            {
                log.debug( "Correcting less than 4 character beaconCode : " + update.getGufi() + " / " + beaconCode ); 
            }    

            update.setBeaconCode( padZeros( beaconCode, 4 )); 
        }                
    }    
    
    private static String padZeros( String string, int length )
    {
        int addZeros = length - string.length();
        
        for( int i = 0; i < addZeros; i++ )
        {
            string = "0" + string;
        }        
        
        return( string );
    }    
}
