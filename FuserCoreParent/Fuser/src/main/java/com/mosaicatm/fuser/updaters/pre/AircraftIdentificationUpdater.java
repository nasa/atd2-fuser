package com.mosaicatm.fuser.updaters.pre;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.fuser.dependency.util.AircraftUpdaterUtil;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * If the aircraft registration or aircraft address are part of the update
 * message, the updater will attempt to derive aircraft registration and
 * the aircraft address from the available fields.
 */
public class AircraftIdentificationUpdater
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
            log.error("Cannot update aircraft identification. Update is NULL!");
            return;
        }

        String updateTail = deriveAircraftRegistration(update);
        String updateModeS = deriveAircraftAddress(update);
        
        // attempt to derive the tail and address from the update
        if (update.getAircraftRegistration() == null && updateTail != null &&
            !updateTail.trim().isEmpty())
        {
            update.setAircraftRegistration(updateTail);

            if (log.isDebugEnabled())
            {
                log.debug("Aircraft registration for " + update.getGufi() + 
                    " derived as: " + update.getAircraftRegistration() + ".");
            }
        }
        
        if (update.getAircraftAddress() == null && updateModeS != null &&
            !updateModeS.trim().isEmpty())
            update.setAircraftAddress(updateModeS);
    }
    
    private String deriveAircraftRegistration(MatmFlight flight)
    {
        if (flight == null)
            return null;
        
        String tail = null;
        
        if (isValidString(flight.getAircraftRegistration()))
        {
            tail = flight.getAircraftRegistration();
        }
        else if (!isValidString(flight.getAircraftRegistration()) && 
                 isValidString(flight.getAircraftAddress()))
        {
            if (AircraftUpdaterUtil.isValidModeSCode(flight.getAircraftAddress()))
            {            
                try
                {
                    tail = AircraftUpdaterUtil.convertToTail(flight.getAircraftAddress());
                }
                catch (Exception e)
                {
                    log.error("Error converting modeS to tail for " + flight.getAcid() + ", " + flight.getAircraftAddress(), e);
                }
            }
            else
            {
            	if (log.isDebugEnabled())
            		log.debug("Failed to derive tail from Invalid modeS address for " + flight.getGufi() + ", " + flight.getAircraftAddress());
            }
        }
        
        return tail;
    }
    
    private String deriveAircraftAddress(MatmFlight flight)
    {
        if (flight == null)
            return null;
        
        String modeS = null;
        
        if (isValidString(flight.getAircraftAddress()))
        {
            modeS = flight.getAircraftAddress();
        }
        else if (!isValidString(flight.getAircraftAddress()) &&
                 isValidString(flight.getAircraftRegistration()))
        {
            if (AircraftUpdaterUtil.isValidTail(flight.getAircraftRegistration()))
            {
                try
                {
                    modeS = AircraftUpdaterUtil.convertToDecimal(flight.getAircraftRegistration());
                }
                catch (Exception e)
                {
                    log.error("Error converting tail to modeS: " + flight.getAircraftRegistration(), e);
                }
            }
            else
            {
            	if (log.isDebugEnabled())
            		log.debug("Failed to derive modeS from invalid tail for " + flight.getAcid() + ", " + flight.getAircraftRegistration());
            }
        }
        
        return modeS;
    }
    
    private boolean isValidString(String text)
    {
        return text != null && !text.trim().isEmpty();
    }
}
