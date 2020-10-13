package com.mosaicatm.fuser.common.matm.util.flight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.common.FlightType;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFlightCarrierUtil
{
    private static final String GA_CARRIERS_RESOURCE = "/config/FuserCommon/flightType/GeneralAviationCarriers.txt";
    
    public static final String GA_CARRIER = "GA";
    public static final String MILITARY_CARRIER = "M";
    
    private final Log log = LogFactory.getLog( getClass() );
    
    private final Set<String> gaCarriersPrefixSet = new HashSet<>();

    public MatmFlightCarrierUtil()
    {
        //Load the default carriers. NULL means use the classpath file.
        initGeneralAviationCarrierLookup( null );
    }
    
    public void setGeneralAviationPrefixesFile( String file )
    {
        initGeneralAviationCarrierLookup( file );
    }    
    
    public String interpolateCarrier( MatmFlight flight )
    {
        // ICAO carrier identifiers are almost always 3 letters, followed by a number.
        //    However, it is also possible to have an ACID with a letter on the end for a commercial carrier.
        // In the U.S. the registration number starts with N, followed by a number and possible letters on the end.
        //    It is also possible for a US GA flight to have an ACID with a letter preceding the N
        // International registration values start with different letters, may have 2 letters, and possibly no number.
        
        String acid = flight.getAcid(); 
        String carrier = null;
        
        // An ACID must be at least 2 characters
        if(( acid != null ) && ( acid.length() > 1 ))
        {
            // Commerical carrier needs at least 4 characters, the first 3 must be letters, 
            // and the fourth must be a number.
            if(( acid.length() >= 4 ) &&
                    Character.isLetter( acid.charAt( 0 )) &&
                    Character.isLetter( acid.charAt( 1 )) &&
                    Character.isLetter( acid.charAt( 2 )) &&
                    Character.isDigit( acid.charAt( 3 )))
            {
                carrier = acid.substring( 0, 3 );
            }
            else if( isMilitaryFlight( flight ))
            {
                carrier = MILITARY_CARRIER;
            }
            else
            {
                carrier = GA_CARRIER;
            }
        }
        
        return( carrier );
    }    
    
    public FlightType interpolateFlightType( MatmFlight flight )
    {
        if( isScheduledFlight( flight ))
        {
            return( FlightType.SCHEDULED_AIR_TRANSPORT );
        }        
        else if( isGeneralAviation( flight ))
        {
            return( FlightType.GENERAL_AVIATION );
        }
        else if( isMilitaryFlight( flight ))
        {
            return( FlightType.MILITARY );
        }        
        else
        {
            return( null );
        }
    }
    
    public boolean isGeneralAviation( MatmFlight flight )
    {
        if( FlightType.GENERAL_AVIATION == flight.getFlightType() )
        {
            return( true );
        }        
        else if( isMilitaryFlight( flight ) || isScheduledFlight( flight ))
        {
            return( false );
        }
        else if( flight.getFlightType() == null )
        {
            if( Objects.equals( flight.getCarrier(), MatmFlightCarrierUtil.GA_CARRIER ))
            {
                return( true );
            }
            else 
            {
                String carrier = interpolateCarrier( flight );

                // If the carrier is not a registration number, we need to check
                // the code is in our lookup            
                if(( carrier != null ) && 
                        ( Objects.equals( carrier, MatmFlightCarrierUtil.GA_CARRIER ) || 
                        gaCarriersPrefixSet.contains( carrier )))
                {
                    return( true );
                }            
            }
        }
        
        return( false );
    } 
    
    public boolean isScheduledFlight( MatmFlight flight )
    {
        if( FlightType.SCHEDULED_AIR_TRANSPORT == flight.getFlightType() )
        {
            return( true );
        }        
        else if( Objects.equals( Boolean.TRUE, flight.isScheduledFlight() ))
        {
            return( true );
        }
        
        return( false );
    }    
    
    public boolean isMilitaryFlight( MatmFlight flight )
    {
        if( FlightType.MILITARY == flight.getFlightType() )
        {
            return( true );
        }        
        
        return( false );
    }     
    
    /**
     * Initializes the GA carrier lookup, using either a classpath 
     * resource or a file resource.
     * 
     * @param file The file to load. The file can either be on the file system
     * or the classpath. If null, the default classpath resource is loaded.
     */    
    private void initGeneralAviationCarrierLookup( String file )
    {
        gaCarriersPrefixSet.clear();
        
        String inputSource = null;
        File inputFile = null;
        
        if(( file != null ) && ( !file.trim().isEmpty() ))
        {
            inputFile = new File( file );
        }
        
        // use default classpath resource if null
        if(( inputFile == null ) || ( !inputFile.isFile() ))
        {
            String resource = GA_CARRIERS_RESOURCE;
            if( inputFile != null )
            {
                resource = file;
            }
            
            inputSource = "classpath:" + resource;
                    
            try( InputStream is = getClass().getResourceAsStream( resource ); 
                 Reader reader = new InputStreamReader( is ))
            {      
                for( String line : IOUtils.readLines( reader ))
                {
                    line = line.trim();
                    if( !line.isEmpty() )
                    {
                        gaCarriersPrefixSet.add( line );
                    }                    
                }
            }
            catch( Exception ex )
            {
                log.error( "Error loading carriers resource!", ex );
            }
        }
        // Or, load a file if provided
        else
        {
            inputSource = "file:" + inputFile.getAbsolutePath();
            
            try( BufferedReader br = new BufferedReader( new FileReader( inputFile )))
            {      
                for( String line; ( line = br.readLine() ) != null; )
                {
                    line = line.trim();
                    if( !line.isEmpty() )
                    {
                        gaCarriersPrefixSet.add( line );
                    }
                }
            }
            catch( Exception ex )
            {
                log.error( "Error loading carriers file!", ex );
            }                
        }
        
        log.info( "Loaded " + gaCarriersPrefixSet.size() + " GA carrier prefixes from " + inputSource );
    }        
}
