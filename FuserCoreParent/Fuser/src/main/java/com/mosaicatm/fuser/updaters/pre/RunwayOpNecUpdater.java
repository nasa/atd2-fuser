package com.mosaicatm.fuser.updaters.pre;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * If the update and the target have matching user runway values
 * and the update does not include an Operational Necessity value,
 * set the update's Operational Necessity value to match the target
 */
public class RunwayOpNecUpdater
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
            log.error("Cannot update runway OpNec values. Update is NULL!");
            return;
        }
        
        if( target == null )
        {
            target = update;
        }         

        // Only run this updater for user set runways if the update does not include an OpNec value but the target does
        updateDeparture(update, target);
        updateArrival(update, target);
    }
    
    private void updateDeparture(MatmFlight update, MatmFlight target){
        if(target.getDepartureRunwayUser() != null && target.isDepartureRunwayOperationalNecessity() != null && 
                        update.getDepartureRunwayUser() != null && update.isDepartureRunwayOperationalNecessity() == null)
        {
            if(target.getDepartureRunwayUser().equals(update.getDepartureRunwayUser())){
                update.setDepartureRunwayOperationalNecessity(target.isDepartureRunwayOperationalNecessity());

                if(log.isDebugEnabled()){
                    log.debug("Setting updated departure OpNec value to match target value " + target.isDepartureRunwayOperationalNecessity() +
                        " for flight: " + update.getGufi());
                }

            }else{
                update.setDepartureRunwayOperationalNecessity(false);

                if(log.isDebugEnabled()){
                    log.debug("Updated departure runway value does not match target. Departure OpNec value will be reset for flight: " + update.getGufi());
                }
            }
        }
    }
    
    private void updateArrival(MatmFlight update, MatmFlight target){
        if(target.getArrivalRunwayUser() != null && target.isArrivalRunwayOperationalNecessity() != null && 
                        update.getArrivalRunwayUser() != null && update.isArrivalRunwayOperationalNecessity() == null)
        {
            if(target.getArrivalRunwayUser().equals(update.getArrivalRunwayUser())){
                update.setArrivalRunwayOperationalNecessity(target.isArrivalRunwayOperationalNecessity());

                if(log.isDebugEnabled()){
                    log.debug("Setting updated arrival OpNec value to match target value " + target.isArrivalRunwayOperationalNecessity() +
                        " for flight: " + update.getGufi());
                }

            }else{
                update.setArrivalRunwayOperationalNecessity(false);

                if(log.isDebugEnabled()){
                    log.debug("Updated arrival runway value does not match target. Arrival OpNec value will be reset for flight: " + update.getGufi());
                }
            }
        }
    }
}
