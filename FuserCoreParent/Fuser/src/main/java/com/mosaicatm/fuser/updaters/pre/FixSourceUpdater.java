package com.mosaicatm.fuser.updaters.pre;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.matm.util.FmcDataSources;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class FixSourceUpdater
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
            log.error("Cannot update fix source. Update is NULL!");
            return;
        }        
        
        if( target == null )
        {
            target = update;
        }      
        
        if(target != null &&  update != null)
        {
            if( dataWithNoSource(target.getArrivalFixSource(), update.getArrivalFixSource(), update.getArrivalFixSourceData()) )
                update.setArrivalFixSource(FmcDataSources.SOURCE_DATA);
            
            if( dataWithNoSource(target.getDepartureFixSource(), update.getDepartureFixSource(), update.getDepartureFixSourceData()) )
                update.setDepartureFixSource(FmcDataSources.SOURCE_DATA);
        }
        
    }
    
    @SuppressWarnings("null")
    private boolean dataWithNoSource(String targetFixSource, String updateFixSource, String updateFixSourceData)
    {
        return (targetFixSource == null || targetFixSource.trim().isEmpty() )
                && (updateFixSource == null || updateFixSource.trim().isEmpty() )
                && (updateFixSourceData != null && !updateFixSourceData.trim().isEmpty() );
    }

}
