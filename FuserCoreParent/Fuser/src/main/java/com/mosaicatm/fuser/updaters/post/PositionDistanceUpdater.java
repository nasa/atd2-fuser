package com.mosaicatm.fuser.updaters.post;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.matm.util.DistanceUtil;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * This updater is responsible for updating the total track distance.
 */
public class PositionDistanceUpdater extends AbstractUpdater<MatmFlight, MatmFlight> {

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public void update(MatmFlight update, MatmFlight target)
    {
        if (!isActive())
        {
            return;
        }

        if (update == null)
        {
            log.error("Cannot update position distance, null input.");
            return;
        }

        // Simple check to ignore most messages and skip processing
        if (possibleUpdate(update, target))
        {
            updatePositionDistance(update, target);
        }
    }

    private boolean possibleUpdate(MatmFlight update, MatmFlight target)
    {
        if(( update.getPosition() == null) ||
               ( update.getPosition().getLatitude() == null ) ||
               ( update.getPosition().getLongitude() == null ))
        {
            return( false );
        }
        
        // If target data is null, we can still set distance to zero
        if(( target == null ) || 
                ( target.getPosition() == null ) || 
                ( target.getPosition().getDistanceTotal() == null ))
        {
            return( true );
        }

        // No update if the data is the same
        if( Objects.equals( update.getPosition().getLatitude(), target.getPosition().getLatitude() ) &&
            Objects.equals( update.getPosition().getLongitude(), target.getPosition().getLongitude() ))
        {
            return( false );
        }
        
        return( true );
    }

    private void updatePositionDistance(MatmFlight update, MatmFlight target)
    {
        Double distanceTotal = 0.;
        
        if(( target != null ) &&
               ( target.getPosition() != null ) &&
               ( target.getPosition().getLatitude() != null ) &&
               ( target.getPosition().getLongitude() != null ))
        {
            if( target.getPosition().getDistanceTotal() != null )
            {
                distanceTotal = target.getPosition().getDistanceTotal();
            }
            
            distanceTotal = distanceTotal + DistanceUtil.calculateDistance( update.getPosition(), target.getPosition() );
        }        
        
        update.getPosition().setDistanceTotal(distanceTotal );
    }
}
