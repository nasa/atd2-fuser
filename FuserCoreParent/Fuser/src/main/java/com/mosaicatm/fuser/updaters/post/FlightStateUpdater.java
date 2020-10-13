package com.mosaicatm.fuser.updaters.post;

import com.mosaicatm.fuser.common.matm.util.flight.DefaultMatmFlightTimeChooser;
import java.util.Date;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightStateUtil;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.fuser.util.MatmFlightInternalUpdateUtil;
import com.mosaicatm.matmdata.common.CancelledType;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.common.SurfaceFlightState;

/**
 * Sets the fuser flight state for a flight.
 */
public class FlightStateUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{   
    public final static String FLIGHT_STATE_DROP_TRACK_SYSTEM_ID = "FLIGHT_STATE_DROPPED_TRACK";
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Override
    public void update(MatmFlight update, MatmFlight target)
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update fuser flight state. Update is NULL!");
            return;
        }          
        
        SurfaceFlightState surfaceFlightState = getBestSurfaceFlightState( update, target );
        SurfaceFlightState defaultFuserFlightState = getFuserFlightState( update, target );
        
        String updateSource;

        if(( surfaceFlightState != null ) && 
                ( surfaceFlightState.ordinal() > SurfaceFlightState.SCHEDULED.ordinal() ) &&
                !Objects.equals( SurfaceFlightState.CANCELLED, surfaceFlightState ) &&
                ( surfaceFlightState.ordinal() > defaultFuserFlightState.ordinal() ))
        {
            updateSource = "Surface";
            update.setFuserFlightState( surfaceFlightState );
        }
        else
        {            
            updateSource = "Fuser";
            update.setFuserFlightState( defaultFuserFlightState );                
        }
        
        if(( target == null ) || 
            !Objects.equals( update.getFuserFlightState(), target.getFuserFlightState() ))
        {
            log.debug( "Derived Fuser flight state from {} state: {}", updateSource, update.getFuserFlightState() );
        }
        else
        {
            update.setFuserFlightState( null );
        }
    }
        
    @Override
    public void sweeperUpdate( Date currentTime, MatmFlight currentState ) 
    {
        if (!isActive())
        {
            return;
        }        
        
        if( currentState == null || currentTime == null )
        {
            log.error("Cannot update fuser flight state. matmFlight or currentTime is NULL!");
            return;
        }         
        
        handleDroppedAirborneTrack( currentTime, currentState );
    }        
    
    private SurfaceFlightState getBestSurfaceFlightState(MatmFlight update, MatmFlight target)
    {
        SurfaceFlightState depSurfaceFlightState = update.getDepartureSurfaceFlightState();
        if(( depSurfaceFlightState == null ) && ( target != null ))
        {
            depSurfaceFlightState = target.getDepartureSurfaceFlightState();
        }
        
        SurfaceFlightState arrSurfaceFlightState = update.getArrivalSurfaceFlightState();
        if(( arrSurfaceFlightState == null ) && ( target != null ))
        {        
            arrSurfaceFlightState = target.getArrivalSurfaceFlightState();
        }
                
        if( depSurfaceFlightState == null )
        {
            return( arrSurfaceFlightState );
        }
        else if( arrSurfaceFlightState == null )
        {
            return( depSurfaceFlightState );
        }
        else if( arrSurfaceFlightState.ordinal() > depSurfaceFlightState.ordinal() )
        {
            return( arrSurfaceFlightState );
        }
        else
        {
            return( depSurfaceFlightState );
        }
    }    
    
    private void handleDroppedAirborneTrack( Date currentTime, MatmFlight currentState ) 
    {                
        if( currentState.getFuserFlightState() == null )
        {
            return;
        }
        
        if( currentState.getFuserFlightState().ordinal() < SurfaceFlightState.ENROUTE.ordinal() ||
                currentState.getFuserFlightState().ordinal() > SurfaceFlightState.ON_FINAL.ordinal() )
        {
            return;
        }
        
        if( MatmFlightStateUtil.isDroppedAirborneTrackState( currentTime, currentState ))
        {
            MatmFlight matm = MatmFlightInternalUpdateUtil.getFuserInternalUpdate( 
                    currentTime, currentState, FLIGHT_STATE_DROP_TRACK_SYSTEM_ID );
            matm.setFuserFlightState( SurfaceFlightState.AIRBORNE_DROPPED_TRACK );

            log.debug( "Injecting Fuser flight state message: {}, {}, {}, {}", 
                    matm.getGufi(), matm.getLastUpdateSource(), matm.getSystemId(),
                    matm.getFuserFlightState()  );
            
            injectFuserMessage( matm );
        }         
    }
    
    private SurfaceFlightState getFuserFlightState( MatmFlight update, MatmFlight target )
    {
        //UNKNOWN,
        //SCHEDULED,
        //FILED,
        //PUSHBACK,
        //RAMP_TAXI_OUT,
        //TAXI_OUT,
        //RETURN_TO_GATE,
        //IN_QUEUE,
        //OFF,
        //ENROUTE,
        //TERMINAL_AIRSPACE,
        //ON_FINAL,
        //AIRBORNE_DROPPED_TRACK,        
        //TAXI_IN,
        //RAMP_TAXI_IN,
        //IN_GATE,
        //SUSPENDED,
        //CANCELLED;        
        
        if( target == null )
        {
            target = update;
        }
        
        /* 
            Handling for the arrival states
        */           
        if(( update.getArrivalStandActualTime() != null ) || 
                Objects.equals( SurfaceFlightState.IN_GATE, target.getFuserFlightState() ) ||
                ( target.getArrivalStandActualTime() != null ))
        {
            return( SurfaceFlightState.IN_GATE );
        }
        
        if(( update.getArrivalMovementAreaActualTime() != null ) || 
                Objects.equals( SurfaceFlightState.RAMP_TAXI_IN, target.getFuserFlightState() ) ||
                ( target.getArrivalMovementAreaActualTime() != null ))
        {
            return( SurfaceFlightState.RAMP_TAXI_IN );
        } 
        
        if(( update.getArrivalRunwayActualTime() != null ) || 
                Objects.equals( SurfaceFlightState.TAXI_IN, target.getFuserFlightState() ) ||
               ( target.getArrivalRunwayActualTime() != null ))
        {
            return( SurfaceFlightState.TAXI_IN );
        }        
        
        /* 
            Handling for the airborne states
        */  

        boolean updateHasAirborneTrack = MatmFlightStateUtil.hasAirborneDataSourceTrack( update );
        // This would normally update in a sweeper
        if( !updateHasAirborneTrack && 
                MatmFlightStateUtil.isDroppedAirborneTrackState( update.getTimestamp(), target ))
        {
            return( SurfaceFlightState.AIRBORNE_DROPPED_TRACK );
        }
        
        if( updateHasAirborneTrack ||
                Objects.equals( SurfaceFlightState.ENROUTE, target.getFuserFlightState() ) ||
                MatmFlightStateUtil.hasAirborneDataSourceTrack( target ))
        {
            return( SurfaceFlightState.ENROUTE );
        }

        if((( update.getDepartureRunwayActualTime() != null ) && !update.getDepartureRunwayActualTime().isNil() ) ||
                (( update.getDepartureRunwayActualTime() == null ) && 
                    ( Objects.equals( SurfaceFlightState.OFF, target.getFuserFlightState() ) ||
                    ( target.getDepartureRunwayActualTime() != null ) && !target.getDepartureRunwayActualTime().isNil() )))
        {
            return( SurfaceFlightState.OFF );
        }

        /* 
            Handling for the pre-airborne states
        */        
        
        if( Objects.equals( update.getCancelled(), CancelledType.CANCELLED ) || 
                ( !Objects.equals( update.getCancelled(), CancelledType.UNCANCELLED ) && 
                  !Objects.equals( update.getCancelled(), CancelledType.NOT_CANCELLED ) &&
                  Objects.equals( SurfaceFlightState.CANCELLED, target.getFuserFlightState() )))
        {
            return( SurfaceFlightState.CANCELLED );
        }

        if((( update.getDepartureMovementAreaActualTime() != null ) && !update.getDepartureMovementAreaActualTime().isNil() ) ||
                (( update.getDepartureMovementAreaActualTime() == null ) && 
                    ( Objects.equals( SurfaceFlightState.TAXI_OUT, target.getFuserFlightState() ) ||
                    ( target.getDepartureMovementAreaActualTime() != null ) && !target.getDepartureMovementAreaActualTime().isNil() )))
        {
            return( SurfaceFlightState.TAXI_OUT );
        }  

        if((( update.getDepartureStandActualTime() != null ) && !update.getDepartureStandActualTime().isNil() ) ||
                (( update.getDepartureStandActualTime() == null ) && 
                    ( Objects.equals( SurfaceFlightState.PUSHBACK, target.getFuserFlightState() ) ||
                    ( target.getDepartureStandActualTime() != null ) && !target.getDepartureStandActualTime().isNil() )))
        {
            return( SurfaceFlightState.PUSHBACK );
        } 
        
        if( Objects.equals( Boolean.TRUE, update.isFiledFlight() ) || 
                Objects.equals( SurfaceFlightState.FILED, target.getFuserFlightState() ) ||
                Objects.equals( Boolean.TRUE, target.isFiledFlight() ))
        {
            return( SurfaceFlightState.FILED );
        }
        
        if( Objects.equals( Boolean.TRUE, update.isScheduledFlight() ) ||
                Objects.equals( SurfaceFlightState.SCHEDULED, target.getFuserFlightState() ) ||
                Objects.equals( Boolean.TRUE, target.isScheduledFlight() ))
        {
            return( SurfaceFlightState.SCHEDULED );
        }        
        
        // STBO convention is pretty much anything else with times is "scheduled"
        if(( DefaultMatmFlightTimeChooser.getBestAvailableOutTime( update ) != null ) || 
                ( DefaultMatmFlightTimeChooser.getBestAvailableOffTime( update ) != null ) || 
                ( DefaultMatmFlightTimeChooser.getBestAvailableOnTime( update ) != null ) || 
                ( DefaultMatmFlightTimeChooser.getBestAvailableInTime( update ) != null ) || 
                ( DefaultMatmFlightTimeChooser.getBestAvailableOutTime( target ) != null ) || 
                ( DefaultMatmFlightTimeChooser.getBestAvailableOffTime( target ) != null ) || 
                ( DefaultMatmFlightTimeChooser.getBestAvailableOnTime( target ) != null ) || 
                ( DefaultMatmFlightTimeChooser.getBestAvailableInTime( target ) != null ))
        {
            return( SurfaceFlightState.SCHEDULED );
        }
                        
        return( SurfaceFlightState.UNKNOWN );
    }
}
