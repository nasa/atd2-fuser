package com.mosaicatm.fuser.updaters.pre;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.common.SurfaceFlightState;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * Update the metered times. 
 * This class currently only clears out pre-departure metered arrival times.
 * However, we could do more complicated logic here, such as:
 * 1. Ignore TBFM SWIM STAs that are not frozen.
 * 2. Ignore TBFM SWIM STAs when metering is disabled.
 * 3. Calculate a weighted STA between TBFM SWIM and NASA TMA. 
 *    - TBFM SWIM value is more accurate outside the TRACON
 *    - NASA TMA value is more accurate inside the TRACON.
 */
public class MeteredTimesUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{
    private final Log log = LogFactory.getLog(getClass());

    @Override
    public void update(MatmFlight update, MatmFlight target)
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update metered times. Update is NULL!");
            return;
        }
        
        if( target == null )
        {
            target = update;
        }          

        updateArrivalMeteredTimes( update, target );
    }

    private void updateArrivalMeteredTimes( MatmFlight update, MatmFlight target )
    {
        if(( update.getArrivalRunwayMeteredTime() != null ) && 
                (( update.getArrivalRunwayMeteredTime().getValue() != null ) || 
                 ( update.getArrivalRunwayMeteredTime().isFrozen() != null )))
        {
            //Filter out the arrival metered data if the flight is pre-departure
            if( !isPostDeparture( update, target ))
            {
                update.getArrivalRunwayMeteredTime().setValue( null );
                update.getArrivalRunwayMeteredTime().setFrozen( null );

                if (log.isDebugEnabled())
                {
                    log.debug( "Filtering " + update.getGufi() + " arrival metered times. Update source:" +
                        update.getLastUpdateSource() + ", flight state is pre-departure." );
                }
            }

            //TODO: more complicated STA calculation to account for frozen, metering enabled,
            //      and possibly using TBFM SWIM STA data up until entering the TRACON.
            //NOTE: Any more complicated changes here would need to be coordinated with the 
            //      Existing mediation rules relating to TBFM SWIM vs. NASA TMA
        }
    }

    private static boolean isPostDeparture( MatmFlight update, MatmFlight target )
    {
        //Check if the update is a higher state
        if( isPostDeparture( update ))
        {
            return( true );
        }
        else
        {
            return( isPostDeparture( target ));
        }
    }

    private static boolean isPostDeparture( MatmFlight flight )
    {
        SurfaceFlightState arr_surface_state = flight
                        .getArrivalSurfaceFlightState();
        SurfaceFlightState dep_surface_state = flight
                        .getDepartureSurfaceFlightState();

        boolean isArrivalStateValid = isSurfaceFlightStateValid(arr_surface_state);
        boolean isDepartureStateValid = isSurfaceFlightStateValid(dep_surface_state);
        
        //Use the surface state if it exists
        if (isArrivalStateValid || isDepartureStateValid)
        {
            if ((isArrivalStateValid && (arr_surface_state.ordinal() >= SurfaceFlightState.OFF.ordinal())) ||
                            (isDepartureStateValid && (dep_surface_state.ordinal() >= SurfaceFlightState.OFF.ordinal())))
            {
                return( true );
            }
            else
            {
                return (false);
            }
        }

        //If no flight state, then use whatever else is in the data
        if (( flight.getDepartureRunwayActualTime() == null ) &&
               ( flight.getDepartureFixActualTime() == null ) &&
               ( flight.getArrivalFixActualTime() == null ) &&
               ( flight.getArrivalRunwayActualTime() == null ) &&
               ( flight.getArrivalMovementAreaActualTime() == null ) &&                
               ( flight.getArrivalStandActual() == null ) &&
               ( flight.getArrivalStandActualTime() == null ))
        {
            return( false );
        }
        else
        {
            return( true );
        }
    }
    
    private static boolean isSurfaceFlightStateValid(SurfaceFlightState surface_state) {
        return (surface_state != null)
                        && (surface_state != SurfaceFlightState.CANCELLED)
                        && (surface_state != SurfaceFlightState.SUSPENDED)
                        && (surface_state != SurfaceFlightState.UNKNOWN);
    }
}
