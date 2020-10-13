package com.mosaicatm.fuser.updaters.post;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;

/**
 * Make sure we have values for common fields if available
 */
public class CommonAircraftFieldUpdater 
extends AbstractUpdater <MatmAircraft, MatmAircraft> 
{
    private final Log log = LogFactory.getLog(getClass());
    
    @Override
    public void update(MatmAircraft update, MatmAircraft target)
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update common aircraft fields. Update is NULL!");
            return;
        }        
        
        if( target == null )
        {
            target = update;
        }
        
        if(update.getAddress() == null && target.getAddress() != null)
        {
            update.setAddress(target.getAddress());
        }
        
        if (update.getFaaEngineClass() == null && target.getFaaEngineClass() != null)
        {
            update.setFaaEngineClass(target.getFaaEngineClass());
        }
        
        if (update.getRecatEngineClass() == null && target.getRecatEngineClass() != null)
        {
            update.setRecatEngineClass(target.getRecatEngineClass());
        }
        
        if (update.getEquipmentQualifier() == null && target.getEquipmentQualifier() != null)
        {
            update.setEquipmentQualifier(target.getEquipmentQualifier());
        }
        
        if (update.getType() == null && target.getType() != null)
        {
            update.setType(target.getType());
        }
    }

    
}
