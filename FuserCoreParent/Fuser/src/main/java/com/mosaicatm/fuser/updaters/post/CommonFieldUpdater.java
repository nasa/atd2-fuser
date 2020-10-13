package com.mosaicatm.fuser.updaters.post;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * Make sure we have values for common fields if available
 * @author gorman
 *
 */
public class CommonFieldUpdater 
extends AbstractUpdater <MatmFlight, MatmFlight> 
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
            log.error("Cannot update common fields. Update is NULL!");
            return;
        }  
        
        if (log.isDebugEnabled()) {
            log.debug("Aircraft registration  for " + update.getGufi() + 
                " originally: " + update.getAircraftRegistration() + ". Update source:" + 
                    update.getLastUpdateSource() + "/" + update.getSystemId());
        }
        
        if(update.getAircraftRegistration() == null && target.getAircraftRegistration() != null)
        {
            update.setAircraftRegistration(target.getAircraftRegistration());

            if (log.isDebugEnabled()) {
                log.debug("Aircraft registration for " + update.getGufi() + 
                    " updated to: " + update.getAircraftRegistration() + ". Update source:" + 
                    update.getLastUpdateSource() + "/" + update.getSystemId());
            }
        }
        
        if(update.getAircraftAddress() == null && target.getAircraftAddress() != null)
        {
            update.setAircraftAddress(target.getAircraftAddress());
        }
            
        if(target.getDepartureAerodrome() != null)
        {
            if(update.getDepartureAerodrome() == null)
            {
                update.setDepartureAerodrome(target.getDepartureAerodrome());
            }
            else
            {
                if(update.getDepartureAerodrome().getIataName() == null)
                {
                    update.getDepartureAerodrome().setIataName( target.getDepartureAerodrome().getIataName() );
                }
                if(update.getDepartureAerodrome().getIcaoName() == null)
                {
                    update.getDepartureAerodrome().setIcaoName( target.getDepartureAerodrome().getIcaoName() );
                }                
            }
        }
        
        if(target.getArrivalAerodrome() != null)
        {
            if(update.getArrivalAerodrome() == null)
            {
                update.setArrivalAerodrome(target.getArrivalAerodrome());
            }
            else
            {
                if(update.getArrivalAerodrome().getIataName() == null)
                {
                    update.getArrivalAerodrome().setIataName( target.getArrivalAerodrome().getIataName() );
                }
                if(update.getArrivalAerodrome().getIcaoName() == null)
                {
                    update.getArrivalAerodrome().setIcaoName( target.getArrivalAerodrome().getIcaoName() );
                }                
            }            
        }
        
        if(update.getAcid() == null && target.getAcid() != null)
        {
            update.setAcid(target.getAcid());
        }

        if (update.getWakeTurbulenceCategory() == null && target.getWakeTurbulenceCategory() != null)
        {
            update.setWakeTurbulenceCategory(target.getWakeTurbulenceCategory());
        }

        if (update.getAircraftEngineClass() == null && target.getAircraftEngineClass() != null)
        {
            update.setAircraftEngineClass(target.getAircraftEngineClass());
        }
        
        if( update.getCarrier() == null && target.getCarrier() != null )
        {
            update.setCarrier( target.getCarrier() );
        }
        
        if( update.getMajorCarrier() == null && target.getMajorCarrier() != null )
        {
            update.setMajorCarrier( target.getMajorCarrier() );
        }

        if( update.getDepartureStandInitialTime() == null && target.getDepartureStandInitialTime() != null )
        {
            update.setDepartureStandInitialTime( target.getDepartureStandInitialTime() );
        }
    }

    
}
