 package com.mosaicatm.fuser.updaters.pre;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.aptcode.data.AirportCodeEntry;
import com.mosaicatm.fuser.transform.matm.MatmTransformConstants;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.fuser.util.FuserAirportCodeTranslator;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.SfdpsExtension;
import com.mosaicatm.matmdata.flight.extension.SfdpsMessageTypeType;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;
import com.mosaicatm.matmdata.flight.extension.TfmMessageTypeType;


public class AerodromeUpdater
extends AbstractUpdater <MatmFlight, MatmFlight>
{
    private static final TfmMessageTypeType[] IGNORE_TFM_AIRPORT_CHANGE_MESSAGE_TYPES = 
    {
        TfmMessageTypeType.FLIGHT_PLAN_CANCELLATION,
        TfmMessageTypeType.ARRIVAL_INFORMATION,
        TfmMessageTypeType.DEPARTURE_INFORMATION,
        TfmMessageTypeType.FLIGHT_TIMES,
        TfmMessageTypeType.TRACK_INFORMATION,
        TfmMessageTypeType.OCEANIC_REPORT,
        TfmMessageTypeType.BOUNDARY_CROSSING_UPDATE,
        TfmMessageTypeType.FLIGHT_SECTORS,
    };
    
    private static final SfdpsMessageTypeType[] ALLOW_SFDPS_AIRPORT_CHANGE_MESSAGE_TYPES = 
    {
        SfdpsMessageTypeType.FH,
        SfdpsMessageTypeType.AH
    };    
        
    private final Log log = LogFactory.getLog(getClass());
    
    private final Set<TfmMessageTypeType> ignoreTfmAirportChangeMessageTypes;
    private final Set<SfdpsMessageTypeType> allowSfdpsAirportChangeMessageTypes;
    
    private FuserAirportCodeTranslator airportCodeTranslator;
    
    public AerodromeUpdater()
    {
        ignoreTfmAirportChangeMessageTypes = new HashSet<>();
        ignoreTfmAirportChangeMessageTypes.addAll( 
                Arrays.asList( IGNORE_TFM_AIRPORT_CHANGE_MESSAGE_TYPES ));
        
        allowSfdpsAirportChangeMessageTypes = new HashSet<>();
        allowSfdpsAirportChangeMessageTypes.addAll( 
                Arrays.asList( ALLOW_SFDPS_AIRPORT_CHANGE_MESSAGE_TYPES ));        
    }
    
    @Override
    public void update(MatmFlight update, MatmFlight target)
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update aerodrome. Null update!" );
            return;
        }
        
        if( target == null )
        {
            target = update;
        }

        fixAerodromeCodes( update.getDepartureAerodrome(), target.getDepartureAerodrome() );
        fixAerodromeCodes( update.getArrivalAerodrome(), target.getArrivalAerodrome() );
        fixAerodromeCodes( update.getSurfaceAirport(), target.getSurfaceAirport() );
        
        fixChangingAerodrome( update, target, true );
        fixChangingAerodrome( update, target, false );
    }
    
    public void setAirportCodeTranslator(FuserAirportCodeTranslator airportCodeTranslator)
    {
        this.airportCodeTranslator = airportCodeTranslator;
    }    
    
    private void fixAerodromeCodes( Aerodrome updateAerodrome, Aerodrome targetAerodrome )
    {
        if( updateAerodrome != null )
        {
            if( airportCodeTranslator != null )
            {
                // if the IATA name is accidentaly in ICAO format this will force
                // the conversion from ICAO to IATA.  If the airport is already 
                // in a valid IATA format no conversion is performed
                AirportCodeEntry code = airportCodeTranslator.getBestMatchAirport( updateAerodrome );
                if( code != null )
                {
                    updateAerodrome.setIcaoName( code.getIcao() );
                    updateAerodrome.setFaaLid( code.getFaaLid() );

                    // We currently default to stuffing either the IATA or FAA LID into the IATA element
                    if( code.getIata() != null )
                    {
                        updateAerodrome.setIataName( code.getIata() );
                    }
                    else if( code.getFaaLid() != null )
                    {
                        updateAerodrome.setIataName( code.getFaaLid() );
                    }
                }
                
                // Don't overwrite a real airport with a bad one
                if(( targetAerodrome != null ) && 
                        airportCodeTranslator.isInvalidAerodrome( updateAerodrome ) && 
                        !airportCodeTranslator.isInvalidAerodrome( targetAerodrome ))
                {
                    updateAerodrome.setIcaoName( targetAerodrome.getIcaoName() );
                    updateAerodrome.setIataName( targetAerodrome.getIataName() );                    
                    updateAerodrome.setFaaLid( targetAerodrome.getFaaLid() );
                }

                // Now make sure we don't have a problem where a changing airport code could fail to 
                // update all of the codes for the target. This check must happen when:
                // 1. Any of the update codes is not null
                // 2. The matching target's code is not null
                if (  (( updateAerodrome.getIataName() != null ) || ( updateAerodrome.getIcaoName() != null )) && 
                       ( targetAerodrome != null ))
                {
                    if(( updateAerodrome.getIataName() == null ) && ( targetAerodrome.getIataName() != null ))
                    {
                        updateAerodrome.setIataName( 
                                MatmTransformConstants.FUSER_UNKNOWN_AIRPORT_OVERWRITE_CODE );
                    }
                    if(( updateAerodrome.getIcaoName() == null ) && ( targetAerodrome.getIcaoName() != null ))
                    {
                        updateAerodrome.setIcaoName( 
                                MatmTransformConstants.FUSER_UNKNOWN_AIRPORT_OVERWRITE_CODE );
                    }     
                    if(( updateAerodrome.getFaaLid() == null ) && ( targetAerodrome.getFaaLid() != null ))
                    {
                        updateAerodrome.setFaaLid( 
                                MatmTransformConstants.FUSER_UNKNOWN_AIRPORT_OVERWRITE_CODE );
                    }                     
                }
            }
            else
            {
                log.warn("Unable to determine IATA/ICAO airports, airport code translator not initialized!!!");
            }        
        }
    }

    private void fixChangingAerodrome(MatmFlight update, MatmFlight target, boolean isDeparture)
    {
        Aerodrome update_aerodrome = null;
        Aerodrome target_aerodrome = null;
        
        if( isDeparture )
        {
            update_aerodrome = update.getDepartureAerodrome();
            target_aerodrome = target.getDepartureAerodrome();
        }
        else
        {
            update_aerodrome = update.getArrivalAerodrome();
            target_aerodrome = target.getArrivalAerodrome();            
        }
        
        // Was there an aerodrome change?
        if(( update_aerodrome != null ) && 
                ( target_aerodrome != null ) &&
                  !update_aerodrome.equals( target_aerodrome ))
        {   
            TfmMessageTypeType tfm_message_type = null;
            SfdpsMessageTypeType sfdps_message_type = null;
            boolean aerodrome_change_override = false;            
            
            if( FuserSource.TFM.value().equals( update.getLastUpdateSource() ) && 
                    ( update.getExtensions() != null ))
            {
                TfmExtension tfm_extension = update.getExtensions().getTfmExtension();
                if( tfm_extension != null )
                {
                    tfm_message_type = tfm_extension.getMessageType();
                    if(( tfm_message_type != null ) && ignoreTfmAirportChangeMessageTypes.contains(tfm_message_type ))
                    {
                        aerodrome_change_override = true;
                    }                    
                }
            }
            else if( FuserSource.SFDPS.value().equals( update.getLastUpdateSource() ) && 
                    ( update.getExtensions() != null ))
            {
                SfdpsExtension sfdps_extension = update.getExtensions().getSfdpsExtension();
                if( sfdps_extension != null )
                {
                    sfdps_message_type = sfdps_extension.getMessageType();
                    if(( sfdps_message_type != null ) && !allowSfdpsAirportChangeMessageTypes.contains( sfdps_message_type ))
                    {
                        aerodrome_change_override = true;
                    }                    
                }
            }            
            else if( FuserSource.FMC.value().equals( update.getLastUpdateSource() ))
            {
                aerodrome_change_override = true;
            }

            // Now, build a log message if necessary
            if( !aerodrome_change_override || log.isDebugEnabled() )
            {
                StringBuilder log_message = new StringBuilder();
                if( aerodrome_change_override )
                {
                    log_message.append( "Ignoring " );
                }

                if( isDeparture )
                {
                    log_message.append( "Departure" );
                }
                else
                {
                    log_message.append( "Arrival" );
                }

                log_message.append( " Aerodrome change. Update source: " );
                log_message.append( update.getLastUpdateSource() );
                if( tfm_message_type != null )
                {
                    log_message.append( ", tfmType = " );
                    log_message.append( tfm_message_type );
                }
                else if( sfdps_message_type != null )
                {
                    log_message.append( ", sfdpsType = " );
                    log_message.append( sfdps_message_type );
                }                
                else if( update.getSystemId() != null )
                {
                    log_message.append( ", systemType = " );   
                    log_message.append( update.getSystemId() );
                }

                log_message.append( ", GUFI = " );   
                log_message.append( update.getGufi() );        

                log_message.append( ", OLD = " );   
                log_message.append( aerodromeToString( target_aerodrome )); 

                log_message.append( ", NEW = " );   
                log_message.append( aerodromeToString( update_aerodrome ));        

                if(( target_aerodrome.getIataName() != null ) && !aerodrome_change_override )
                {
                    log.info( log_message );
                }            
                else 
                {
                    log.debug( log_message );
                }   
            }
            
            //Finally, filter out the update's aerodrome if necessary
            if( aerodrome_change_override )
            {
                if( isDeparture )
                {
                    update.setDepartureAerodrome( target_aerodrome );
                }
                else
                {
                    update.setArrivalAerodrome( target_aerodrome );
                }                
            }            
        }
    }    
    
    private static String aerodromeToString( Aerodrome aerodrome )
    {
        String string = null;
        
        if( aerodrome != null )
        {
            string = "[" + aerodrome.getIcaoName() + " / " + 
                    aerodrome.getIataName() +  " / " + 
                    aerodrome.getFaaLid() + "]";
        }
        
        return( string );
    }
}
