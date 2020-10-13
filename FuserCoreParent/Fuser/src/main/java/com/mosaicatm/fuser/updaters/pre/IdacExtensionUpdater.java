package com.mosaicatm.fuser.updaters.pre;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.IdacExtension;

/**
 * Fixes a bad IDAC global ID when one already exists.
 */
public class IdacExtensionUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{
    private final Log log = LogFactory.getLog(getClass());
    
    @Override
    public void update( MatmFlight update, MatmFlight target )
    {
        if (!isActive())
        {
            return;
        }
        
        if(( update == null ))
        {
            log.error("Cannot update IDAC extension. Update is NULL!");
            return;
        }

        if(( update.getExtensions() != null ) && ( update.getExtensions().getIdacExtension() != null ))
        {
            IdacExtension update_ext = update.getExtensions().getIdacExtension();
            
            updateGlobalId( update, update_ext );
        }
    }
    
    private void updateGlobalId( MatmFlight update, IdacExtension idacUpdate )
    {
        if(( idacUpdate != null ) && ( idacUpdate.getGlobalId() != null ))
        {
            //If update is not fused or the TMA ID is the same as the global ID, then this is not a real global ID
            if(( idacUpdate.isFused() == null ) || !idacUpdate.isFused() ||
                    ( idacUpdate.getUpstreamTmaId() != null ) && idacUpdate.getUpstreamTmaId().equals( idacUpdate.getGlobalId() ))
            {
                idacUpdate.setGlobalId( null );
                            
                if( log.isDebugEnabled() )
                {
                    log.debug( update.getAcid() + "/" + update.getGufi() + 
                              ": nulling out non-Fused IDAC global ID" );
                }
            }
        }
    }
}
