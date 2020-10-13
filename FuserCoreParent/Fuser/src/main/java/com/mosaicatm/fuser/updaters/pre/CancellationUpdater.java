package com.mosaicatm.fuser.updaters.pre;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightCarrierUtil;
import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightStateUtil;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.common.CancelledType;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.SurfaceFlightState;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * Sets the cancelled state and surface flight state when cancelled or uncancelled.
 */
public class CancellationUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{     
    private final Log log = LogFactory.getLog(getClass());
    
    private MatmFlightCarrierUtil matmFlightCarrierUtil = null;
    
    @Override
    public void update(MatmFlight update, MatmFlight target)
    {  
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update cancellation. Null update!" );
            return;
        }
        
        if( target == null )
        {
            target = update;
        }           
        
        filterUnchangedNotCancelled( update, target );
        filterIgnoreCancelled( update, target );
        
        handleUpdateMessage(update, target);
        
        checkUncancelByStatus( update, target );
        checkUncancelledToNotCancelledStateChange( update, target );
    }

    public void setMatmFlightCarrierUtil( MatmFlightCarrierUtil matmFlightCarrierUtil )
    {
        this.matmFlightCarrierUtil = matmFlightCarrierUtil;
    }
    
    private void filterUnchangedNotCancelled( MatmFlight update, MatmFlight target )
    {     
        // We can filter out NOT_CANCELLED updates that don't change the target
        if(( update.getCancelled() == CancelledType.NOT_CANCELLED ) &&
           (( target.getCancelled() == null ) ||
                ( target.getCancelled() == CancelledType.NOT_CANCELLED )))
        {
            update.setCancelled( null );
            update.setCancelledTime( null );

            if( log.isTraceEnabled() )
            {
                log.trace( update.getAcid() + "/" + update.getGufi() + 
                    ": ignore unchanged false cancelled flag." );
            }
        }        
    }
    
    private void filterIgnoreCancelled( MatmFlight update, MatmFlight target )
    {
        if( matmFlightCarrierUtil == null )
        {
            log.error( "NULL matmFlightCarrierUtil, cannot interpolate flight type!" );
            return;
        }         
        
        // Ignore cancellation messages for airborne flights or 
        // flights on the ground at the destination airport
        // Do not cancel GA flights; instead, let the current suspend logic take care of them        
        if(( update.getCancelled() == CancelledType.CANCELLED ) &&
                ( matmFlightCarrierUtil.isGeneralAviation( update ) ||
                  matmFlightCarrierUtil.isGeneralAviation( target ) ||
                  MatmFlightStateUtil.isFlightDeparted( update ) ||
                  MatmFlightStateUtil.isFlightDeparted( target )))
        {
            update.setCancelled( null );
            update.setCancelledTime( null );

            if( log.isDebugEnabled() )
            {                
                log.debug( update.getAcid() + "/" + update.getGufi() + 
                    ": ignoring cancellation update. Flight is either a GA or has already departed." );
            }                      
        }
    }
            
    private void handleUpdateMessage( MatmFlight update, MatmFlight target )
    {
        if( update.getCancelled() == CancelledType.CANCELLED )
        {
            cancelFlight(update, target);
        }
        // Check if we need to trigger an UNCANCEL. This occurs when:
        //   The target is CANCELLED and the update isn't.
        //   And the update is not from FMC -- we don't want to trigger the uncancellation logic when FMC changes the status
        else if(( update.getCancelled() == CancelledType.NOT_CANCELLED ) ||
                ( update.getCancelled() == CancelledType.UNCANCELLED ))
        {
            if(( target.getCancelled() == CancelledType.CANCELLED ) &&
                 !FuserSource.FMC.value().equals( update.getLastUpdateSource() ))
            {
                uncancelFlight( update, target );
            }
        }
    } 

    private void checkUncancelByStatus( MatmFlight update, MatmFlight target )
    {
        //Will the flight be cancelled?
        if(( CancelledType.CANCELLED == update.getCancelled() ) ||
                (( update.getCancelled() == null ) &&
                 ( CancelledType.CANCELLED == target.getCancelled() )))
        {
            //Uncancel if:
            //  - We receive track data for the flight after the cancellation
            //  - We receive an actual out time for the flight AFTER it was cancelled
            //  -- But only if the actual out time is different from the value we currently have for actual out time
            
            Date cancelled_time = update.getCancelledTime();
            if( cancelled_time == null )
            {
                cancelled_time = target.getCancelledTime();
            }
            
            if( cancelled_time != null )
            {
                // Check for airborne track data later than the cancellation time
                if(( MatmFlightStateUtil.isFlightDeparted( update ) || 
                            MatmFlightStateUtil.isFlightDeparted( target )) && 
                        ( update.getTimestamp().after( cancelled_time )))
                {
                    if( log.isDebugEnabled() )
                    {                
                        log.debug( update.getAcid() + "/" + update.getGufi() + 
                            ": uncancelling. Flight has already departed." );
                    }                      
                    
                    uncancelFlight( update, target );
                }
                // Check for the out time after cancelled
                else if(( update.getDepartureStandActualTime() != null && update.getDepartureStandActualTime().getValue() != null &&
                             target.getDepartureStandActualTime() != null && target.getDepartureStandActualTime().getValue() != null)
                        && !Objects.equals( update.getDepartureStandActualTime().getValue(), target.getDepartureStandActualTime().getValue() )
                        && ( update.getDepartureStandActualTime().getValue().after( cancelled_time )))
                {
                    if( log.isDebugEnabled() )
                    {                
                        log.debug( update.getAcid() + "/" + update.getGufi() + 
                            ": uncancelling. Flight has actual gate out time." );
                    }                      
                    
                    uncancelFlight( update, target );
                }
            }            
        }
    }    
    
    private void checkUncancelledToNotCancelledStateChange( MatmFlight update, MatmFlight target )
    {
        // This is meant to run only if the update is null or UNCANCELLED
        if(( update.getCancelled() == null ) ||
                ( update.getCancelled() == CancelledType.UNCANCELLED ))
        {
            // We may need to set UNCANCELLED back to NOT_CANCELLED
            if( target.getCancelled() == CancelledType.UNCANCELLED )
            {
                update.setCancelled( CancelledType.NOT_CANCELLED );
                
                if( log.isDebugEnabled() )
                {                
                    log.debug( update.getAcid() + "/" + update.getGufi() + 
                        ": changing state from uncancelled to not cancelled." );
                }            
            } 
        }
    }

    private void cancelFlight( MatmFlight update, MatmFlight target )
    {    
        update.setCancelled( CancelledType.CANCELLED );
       
        update.setDepartureSurfaceFlightState(SurfaceFlightState.CANCELLED);
        update.setArrivalSurfaceFlightState(SurfaceFlightState.CANCELLED);

        // We don't want to change the cancelled time -- the transforms just sets it to the current timestamp
        if(( target.getCancelled() == CancelledType.CANCELLED ) &&
                ( target.getCancelledTime() != null ))
        {
            update.setCancelledTime( null );
        }

        if( log.isDebugEnabled() )
        {                
            log.debug( update.getAcid() + "/" + update.getGufi() + 
                ": cancelled." );
        } 
    }
    
    private void uncancelFlight( MatmFlight update, MatmFlight target )
    {    
        if( target.getCancelled() == CancelledType.CANCELLED )
        {
            update.setCancelled( CancelledType.UNCANCELLED );
            
            if( SurfaceFlightState.CANCELLED == target.getDepartureSurfaceFlightState() ||
                    SurfaceFlightState.CANCELLED == update.getDepartureSurfaceFlightState())
            {
                update.setDepartureSurfaceFlightState(SurfaceFlightState.UNKNOWN);
            }
            
            if( SurfaceFlightState.CANCELLED == target.getArrivalSurfaceFlightState() ||
                    SurfaceFlightState.CANCELLED == update.getArrivalSurfaceFlightState())
            {
                update.setArrivalSurfaceFlightState(SurfaceFlightState.UNKNOWN);
            }

            update.setCancelledTime( null );

            if( log.isDebugEnabled() )
            {                
                log.debug( update.getAcid() + "/" + update.getGufi() + 
                    ": uncancelling flight." );                
            }
        }
        else
        {
            update.setCancelled( CancelledType.NOT_CANCELLED );
            update.setCancelledTime( null );
            
            if( log.isDebugEnabled() )
            {                
                log.debug( update.getAcid() + "/" + update.getGufi() + 
                    ": setting update to NOT_CANCELLED." );
            }            
        }
    }
}
