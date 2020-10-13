package com.mosaicatm.fuser.common.matm.util.flight;

import java.util.Date;
import java.util.Objects;

import com.mosaicatm.matmdata.common.SurfaceFlightState;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFlightStateUtil
{
    private static final int ACTIVE_ALTITUDE_FEET = 7000;
    private static final long STALE_POSITION_MILLIS = 5 * 60 * 1000;        
    
    public static boolean isFlightLanded( MatmFlight flight )
    {
        if( flight == null )
        {
            return( false );
        }          
        
        if( flight.getFuserFlightState() != null )
        {
            return( flight.getFuserFlightState().ordinal() >= SurfaceFlightState.TAXI_IN.ordinal() &&
                    flight.getFuserFlightState().ordinal() <= SurfaceFlightState.IN_GATE.ordinal() );
        }      
        
        if(( flight.getArrivalRunwayActualTime() != null ) || 
                ( flight.getArrivalStandActualTime() != null ))
        {
            return( true );
        }
        
        return( false );
    }
    
    public static boolean isFlightDeparted( MatmFlight flight )
    {
        if( flight == null )
        {
            return( false );
        }        
        
        if( flight.getFuserFlightState() != null )
        {
            return( flight.getFuserFlightState().ordinal() >= SurfaceFlightState.OFF.ordinal() &&
                    flight.getFuserFlightState().ordinal() <= SurfaceFlightState.IN_GATE.ordinal() );
        }        
        
        if((( flight.getDepartureRunwayActualTime() != null ) && !flight.getDepartureRunwayActualTime().isNil() ) || 
                ( flight.getDepartureFixActualTime() != null ) ||
                ( flight.getArrivalRunwayActualTime() != null ) ||
                ( flight.getArrivalFixActualTime() != null ))
        {
            return( true );
        }
        
        if( hasAirborneDataSourceTrack( flight ))
        {
            return( true );
        }          

        return( false );
    }

    public static Position getLatestAirborneDataSourceTrackPosition( MatmFlight matmFlight )
    {
        Position latestPosition = null;
        
        // Check extensions for track
        if(( matmFlight != null ) && ( matmFlight.getExtensions() != null ))
        {
            if(( matmFlight.getExtensions().getTfmExtension() != null ) && 
                    ( matmFlight.getExtensions().getTfmExtension().getLastTfmPosition() != null ))
            {
                Position extPosition = matmFlight.getExtensions().getTfmExtension().getLastTfmPosition();
                if(( latestPosition == null ) || latestPosition.getTimestamp().before( extPosition.getTimestamp() ))
                {
                    latestPosition = extPosition;
                }
            }
            
            if(( matmFlight.getExtensions().getTbfmExtension() != null ) && 
                    ( matmFlight.getExtensions().getTbfmExtension().getLastTbfmPosition() != null ))
            {
                Position extPosition = matmFlight.getExtensions().getTbfmExtension().getLastTbfmPosition();
                if(( latestPosition == null ) || latestPosition.getTimestamp().before( extPosition.getTimestamp() ))
                {
                    latestPosition = extPosition;
                }                
            }     
            
            if(( matmFlight.getExtensions().getSfdpsExtension() != null ) && 
                    ( matmFlight.getExtensions().getSfdpsExtension().getLastSfdpsPosition() != null ))
            {
                Position extPosition = matmFlight.getExtensions().getSfdpsExtension().getLastSfdpsPosition();
                if(( latestPosition == null ) || latestPosition.getTimestamp().before( extPosition.getTimestamp() ))
                {
                    latestPosition = extPosition;
                }   
            }             
        }
        
        return( latestPosition );
    }
    
    public static boolean hasAirborneDataSourceTrack( MatmFlight matmFlight )
    {
        if( matmFlight == null )
        {
            return( false );
        }
        
        // Check extensions for track
        if( matmFlight.getExtensions() != null )
        {
            if(( matmFlight.getExtensions().getTfmExtension() != null ) && 
                    ( matmFlight.getExtensions().getTfmExtension().getLastTfmPosition() != null ))
            {
                return( true );
            }
            
            if(( matmFlight.getExtensions().getTbfmExtension() != null ) && 
                    ( matmFlight.getExtensions().getTbfmExtension().getLastTbfmPosition() != null ))
            {
                return( true );
            }     
            
            if(( matmFlight.getExtensions().getSfdpsExtension() != null ) && 
                    ( matmFlight.getExtensions().getSfdpsExtension().getLastSfdpsPosition() != null ))
            {
                return( true );
            }             
        }        
        
        return( false );
    }        

    public static boolean isDroppedAirborneTrackState( Date currentTime, MatmFlight matmFlight )
    {
        if( matmFlight == null )
        {
            return( false );
        }
        
        if( Objects.equals( SurfaceFlightState.AIRBORNE_DROPPED_TRACK, matmFlight.getFuserFlightState() ))
        {
            return( true );
        }          
        
        if( isFlightLanded( matmFlight ))
        {
            return( false );
        }
        
        if( !hasAirborneDataSourceTrack( matmFlight ))
        {
            return( false );
        }        
        
        return( MatmFlightStateUtil.hasStaleAirborneDataSourceTrack( currentTime, matmFlight ));        
    } 
    
    public static boolean hasStaleAirborneDataSourceTrack( MatmFlight matmFlight )
    {
        if( matmFlight == null )
        {
            return( false );
        }
        
        return( MatmFlightStateUtil.hasStaleAirborneDataSourceTrack( matmFlight.getTimestamp(), matmFlight ));        
    }    
    
    public static boolean hasStaleAirborneDataSourceTrack( Date currentTime, MatmFlight matmFlight )
    {
        if(( matmFlight == null ) || ( currentTime == null ))
        {            
            return( false );        
        }
        
        return( isStaleAirborneDataSourceTrack( currentTime, 
                    getLatestAirborneDataSourceTrackPosition( matmFlight )));
    }

    public static boolean isStaleAirborneDataSourceTrack( Date currentTime, Position position )
    {
        if(( position != null ) &&
                ( position.getTimestamp() != null ) &&
                ( currentTime != null ))
        {
            long staleMillis = currentTime.getTime() - position.getTimestamp().getTime();
            
            return( staleMillis > STALE_POSITION_MILLIS );
        }
        
        return( false );        
    }    
    
    public static boolean isFlightAirborne( MatmFlight matmFlight )
    {
        if( matmFlight == null )
        {
            return( false );
        }
        
        if( matmFlight.getFuserFlightState() != null )
        {
            return( matmFlight.getFuserFlightState() != SurfaceFlightState.AIRBORNE_DROPPED_TRACK &&
                    matmFlight.getFuserFlightState().ordinal() >= SurfaceFlightState.OFF.ordinal() &&
                    matmFlight.getFuserFlightState().ordinal() <= SurfaceFlightState.ON_FINAL.ordinal() );            
        }
        
        if( isFlightLanded( matmFlight ))
        {
            return( false );
        }
        
        if( hasAirborneDataSourceTrack( matmFlight ) && !hasStaleAirborneDataSourceTrack( matmFlight ))
        {
            return( true );
        }
        
        if(( matmFlight.getPosition() != null ) && 
                ( matmFlight.getPosition().getAltitude() != null ) &&
                ( matmFlight.getPosition().getAltitude() > ACTIVE_ALTITUDE_FEET ) &&
                !isStaleAirborneDataSourceTrack( matmFlight.getTimestamp(), matmFlight.getPosition() ))
        {
            return( true );
        }        
        
        return( false );
    }    
}
