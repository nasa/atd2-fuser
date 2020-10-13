package com.mosaicatm.fuser.updaters.pre;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.matmdata.common.MatmObject;


public class CreationTimeUpdater <T extends MatmObject>
extends AbstractUpdater <T, T>
{
    private final Log log = LogFactory.getLog(getClass());

    private Clock clock;

    public CreationTimeUpdater(Clock clock){
        this.clock = clock;
    }

    @Override
    public void update(T update, T target)
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update creation time. Update is NULL!");
            return;
        }
        
        if( target == null )
        {
            target = update;
        }            

        if(clock != null){ 
            if(update.equals(target) && target.getCreationTime() == null){
                update.setCreationTime(new Date(clock.getTimeInMillis()));
            }
        }else{
            log.warn("Could not update creation time. Clock is not set.");
        }
    }

    public void setClock(Clock clock)
    {
        this.clock = clock;
    }

}
