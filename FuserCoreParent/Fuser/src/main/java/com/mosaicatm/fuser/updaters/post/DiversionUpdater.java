package com.mosaicatm.fuser.updaters.post;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Objects;

import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightStateUtil;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * This updater is responsible for updating the diversion indicator for clients.
 */
public class DiversionUpdater extends AbstractUpdater<MatmFlight, MatmFlight> {

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
            log.error("Cannot update diversion indicator, null input.");
            return;
        }

        // Simple check to ignore most messages and skip processing
        if (possibleUpdate(update, target))
        {
            updateDiversionIndicator(update, target);
        }
    }

    private boolean possibleUpdate(MatmFlight update, MatmFlight target)
    {
        return target == null
                || (update.getArrivalAerodrome() != null
                && aerodromeChanged(update.getArrivalAerodrome(), target.getArrivalAerodrome()));
    }

    private void updateDiversionIndicator(MatmFlight update, MatmFlight target)
    {
        boolean hasDeparted = MatmFlightStateUtil.isFlightDeparted(update) || MatmFlightStateUtil.isFlightDeparted(target);
        if (hasDeparted)
        {
            if (target != null && target.getPredepartureArrivalAerodrome() != null)
            {
                boolean hasChanged = aerodromeChanged(target.getPredepartureArrivalAerodrome(), update.getArrivalAerodrome());
                update.setActiveDiversion(hasChanged);
            }
        }
        else
        {
            update.setPredepartureArrivalAerodrome(update.getArrivalAerodrome());
        }
    }

    private boolean aerodromeChanged(Aerodrome update, Aerodrome target)
    {
        if (update != null && target != null && update.getIataName() != null && target.getIataName() != null)
        {
            return !update.getIataName().equals(target.getIataName());
        }
        if (target == null && update != null)
        {
            return true;
        }
        return !Objects.equals(update, target);
    }
}
