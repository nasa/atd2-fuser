package com.mosaicatm.fuser.updaters.pre;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.matm.actypelib.AircraftType;
import com.matm.actypelib.manager.AircraftTypeManager;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * Sets default aircraft values if the values are not present in either the
 * update or target flight.  If the update values are null and the target has
 * existing values those values will be assigned to the update
 */
public class AircraftTypeUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private AircraftTypeManager aircraftTypeManager;
    
    public AircraftTypeUpdater()
    {
        
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
            log.error("Cannot update aircraft type. Update is NULL!");
            return;
        }

        String updateAircraftType = update.getAircraftType();
        
        if (updateAircraftType != null && !updateAircraftType.isEmpty())
        {
            // If the map contains the input aircraft type, convert the 
            // input to the corresponding adapted aircraft types
            if (aircraftTypeManager.hasType(updateAircraftType))
            {
                AircraftType newAircraftType = aircraftTypeManager.getType(updateAircraftType);
                String newAircraftTypeName = newAircraftType.getName();
                update.setAircraftType(newAircraftTypeName);
                
                if (log.isDebugEnabled() && !newAircraftTypeName.equals(updateAircraftType)) 
                {
                    log.debug(update.getGufi() +
                              ": converting input aircraft type " + updateAircraftType + 
                              " to adapted aircraft type" + newAircraftTypeName + ". Update received from " + 
                              update.getLastUpdateSource() + "/" + update.getSystemId());
                }
            }
            // If unable to map the input aircraft type, set the aircraft type to UNKN
            else
            {
                update.setAircraftType(AircraftTypeManager.DEFAULT_AIRCRAFT_TYPE);
                
                log.warn(update.getGufi() +
                        ": unknown aircraft type " + updateAircraftType + 
                        " received from " + update.getLastUpdateSource() + "/" + 
                        update.getSystemId() +". Defaulting aircraft type to " +
                        update.getAircraftType());
            }
        }
        else 
        {            
            update.setAircraftType(AircraftTypeManager.DEFAULT_AIRCRAFT_TYPE);
            
            if (log.isDebugEnabled())
            {
                log.debug(update.getGufi() + 
                          ": null or empty aircraft type, setting default value to " +
                          update.getAircraftType());
            }
        }
    }

    public void setAircraftTypeManager(AircraftTypeManager aircraftTypeManager)
    {
        this.aircraftTypeManager = aircraftTypeManager;
    }
}
