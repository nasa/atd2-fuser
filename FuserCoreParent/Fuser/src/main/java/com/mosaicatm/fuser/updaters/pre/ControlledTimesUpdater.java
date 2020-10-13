package com.mosaicatm.fuser.updaters.pre;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * Make sure that only certain sources can un-purge a controlled time.
 */
public class ControlledTimesUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{
    private final static String[] ALLOW_PURGE_OVERRIDE_SOURCES = { FuserSource.TFM.value() };
    
    private final static Set<String> ALLOW_PURGE_OVERRIDE_SOURCES_SET = 
            new HashSet<>( Arrays.asList( ALLOW_PURGE_OVERRIDE_SOURCES ));    
    
    private final Log log = LogFactory.getLog(getClass());
    
    @Override
    public void update( MatmFlight update, MatmFlight target )
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update controlled times. Update is NULL!");
            return;
        }
        
        if( target == null )
        {
            target = update;
        }          
        
        updateControlledTimes( update, target );
    }
    
    private void updateControlledTimes( MatmFlight update, MatmFlight target )
    {        
        if( !allowUpdate( 
                update.getLastUpdateSource(),
                update.getEstimatedDepartureClearanceTime(), 
                target.getEstimatedDepartureClearanceTime() ))
        {
            update.setEstimatedDepartureClearanceTime( null );
            
            if (log.isDebugEnabled())
            {
                log.debug(update.getAcid() + "/" + update.getGufi() + 
                          ": ignoring estimatedDepartureClearanceTime from " +
                          update.getLastUpdateSource());
            }
        }        
        
        if( !allowUpdate( 
                update.getLastUpdateSource(),
                update.getArrivalRunwayControlledTime(), 
                target.getArrivalRunwayControlledTime() ))
        {
            update.setArrivalRunwayControlledTime( null );
            
            if (log.isDebugEnabled())
            {
                log.debug(update.getAcid() + "/" + update.getGufi() + 
                          ": ignoring arrivalRunwayControlledTime from " +
                          update.getLastUpdateSource());
            }
        }
    }
    
    /**
     * Check if an update source is allowed to update a controlled time.
     * 
     * @param updateSource update source
     * @param updateControlledTime update controlled time
     * @param targetControlledTime target controlled time
     * @return true if update is allowed to modify a controlled time
     */   
    private boolean allowUpdate( String updateSource, JAXBElement<Date> updateControlledTime, 
            JAXBElement<Date> targetControlledTime )
    {
        boolean allow = true;
        
        if( updateControlledTime == null )
        {
            allow = false;
        }
        // Only allow some sources to update a purged (nil) controlled time.
        // Same sources are only ones that can set the target to nil.
        else if(( updateControlledTime.isNil() ) || 
                (( targetControlledTime != null ) &&
                 ( targetControlledTime.isNil() )))
        {
            allow = ALLOW_PURGE_OVERRIDE_SOURCES_SET.contains( updateSource );
        }
        
        return( allow );
    }     
}
