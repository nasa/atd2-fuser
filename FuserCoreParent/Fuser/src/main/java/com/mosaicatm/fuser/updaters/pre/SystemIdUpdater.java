package com.mosaicatm.fuser.updaters.pre;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.common.MatmObject;

public class SystemIdUpdater<T extends MatmObject> 
extends AbstractUpdater<T, T>
{
    private final Log log = LogFactory.getLog(getClass());
    
    @Override
    public void update(T update, T target)
    {
        if (!isActive())
        {
            return;
        }
        
        if(( update == null ))
        {
            log.error("Cannot update system ID. Update is NULL!");
            return;
        }

        if(update.getSystemId() == null){
            update.setSystemId(update.getLastUpdateSource());
        }
    }

}
