package com.mosaicatm.fuser.updaters.pre;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightCarrierUtil;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * Sets the carrier and flight type using logic that extracts from the ACID if it is not already set.
 */
public class FlightTypeAndCarrierUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private MatmFlightCarrierUtil matmFlightCarrierUtil = null;
    
    @Override
    public void update(MatmFlight update, MatmFlight target)
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update carrier or flight type. Update is NULL!");
            return;
        }        
        
        updateCarrier(update, target);
        updateFlightType(update, target);
    }
    
    public void setMatmFlightCarrierUtil( MatmFlightCarrierUtil matmFlightCarrierUtil )
    {
        this.matmFlightCarrierUtil = matmFlightCarrierUtil;
    }
    
    private void updateCarrier (MatmFlight update, MatmFlight target)
    {
        // Only update the carrier if not already set or 
        // "XXX" which means not set.
        if(( update.getAcid() != null ) && 
                (( update.getCarrier() == null ) || 
                   Objects.equals( "XXX", update.getCarrier() )))
        {
            // If ACID hasn't changed, and the flightType hasn't changed, 
            // and the target has the right carrier, there's nothing to do...
            if(( target == null ) ||
                    ( target.getCarrier() == null ) || 
                    !Objects.equals( update.getAcid(), target.getAcid() ) ||
                    (( update.getFlightType() != null ) && 
                        !Objects.equals( update.getFlightType(), target.getFlightType() )))
            {
                if( matmFlightCarrierUtil == null )
                {
                    log.error( "NULL matmFlightCarrierUtil, cannot interpolate carrier!" );
                    return;
                }                
                
                update.setCarrier( matmFlightCarrierUtil.interpolateCarrier( update ));

                if (log.isDebugEnabled())
                {
                    log.debug(update.getAcid() + "/" + update.getGufi() + 
                              ": setting unknown carrier to " +
                              update.getCarrier());
                }                
            }
        }
    }
    
    private void updateFlightType (MatmFlight update, MatmFlight target)
    {
        if( update.getFlightType() == null )
        {
            // If the target was set and the carrier didn't change, the flight type won't either, there's nothing to do...
            if(( target == null ) ||
                    ( target.getFlightType() == null ) || 
                    !Objects.equals( update.getCarrier(), target.getCarrier() ))
            {
                if( matmFlightCarrierUtil == null )
                {
                    log.error( "NULL matmFlightCarrierUtil, cannot interpolate flight type!" );
                    return;
                }                  
                
                update.setFlightType( matmFlightCarrierUtil.interpolateFlightType( update ));

                if (log.isDebugEnabled())
                {
                    log.debug(update.getAcid() + "/" + update.getGufi() + 
                              ": setting unknown flight type to " +
                              update.getFlightType());
                }
            }            
        }
    }    
}
