 package com.mosaicatm.fuser.updaters.pre;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.matm.util.flight.CategoryUtil;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.SurfaceFlightState;
import com.mosaicatm.matmdata.flight.MatmFlight;


public class InternalDepartureUpdater
extends AbstractUpdater <MatmFlight, MatmFlight>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private List<String> airports;

    @Override
    public void update(MatmFlight update, MatmFlight target)
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot set internal departure flag. Null update!" );
            return;
        }
        
        if (airports == null || airports.isEmpty())
        {
            log.error("Cannot set internal departure flag. No airport list!");
            return;
        }

        if( target == null )
        {
            target = update;
        }
        
        update.setInternalDeparture(isInternalDeparture(update, target));
    }

    private boolean isInternalDeparture(MatmFlight update, MatmFlight target)
    {
        boolean isInternalDeparture = false;

        Aerodrome arrivalAerodrome;
        if (isValidAerodrome(update.getArrivalAerodrome()))
            arrivalAerodrome = update.getArrivalAerodrome();
        else
            arrivalAerodrome = target.getArrivalAerodrome();

        Aerodrome departureAerodrome;
        if (isValidAerodrome(update.getDepartureAerodrome()))
            departureAerodrome = update.getDepartureAerodrome();
        else
            departureAerodrome = target.getDepartureAerodrome();

        boolean isArrival = CategoryUtil.isAirportIncluded(arrivalAerodrome, airports);
        boolean isDeparture = CategoryUtil.isAirportIncluded(departureAerodrome, airports);
        
        if (isDeparture && isArrival)
        {
            // If flight is departing/arriving at the same airport or returning
            // to gate, do not mark as internal departure
            if (!isSingleAirportFlight(arrivalAerodrome, departureAerodrome)
                && !isReturnToGate(update, target))
            {
                isInternalDeparture = true;
            }
        }

        return isInternalDeparture;
    }

    private boolean isSingleAirportFlight(Aerodrome arrivalAerodrome, Aerodrome departureAerodrome)
    {
        boolean isSingleAirport = false;
        if (arrivalAerodrome != null && departureAerodrome != null)
        {
            isSingleAirport = matches(arrivalAerodrome.getFaaLid(), departureAerodrome.getFaaLid()) ||
                            matches(arrivalAerodrome.getIataName(), departureAerodrome.getIataName()) ||
                            matches(arrivalAerodrome.getIcaoName(), departureAerodrome.getIcaoName());       
        }

        return isSingleAirport;
    }

    private boolean matches(String arrival, String departure)
    {
        boolean isEquals = false;
        if (arrival != null && !arrival.trim().isEmpty() && departure != null
            && !departure.trim().isEmpty())
        {
            isEquals = arrival.equals(departure);
        }

        return isEquals;
    }

    private boolean isReturnToGate(MatmFlight update, MatmFlight target)
    {
        boolean returnToGate = false;

        if(update.getDepartureSurfaceFlightState() != null)
        {
            returnToGate = SurfaceFlightState.RETURN_TO_GATE.equals(update.getDepartureSurfaceFlightState());
        }
        else
        {
            returnToGate = SurfaceFlightState.RETURN_TO_GATE.equals(target.getDepartureSurfaceFlightState());
        }

        return returnToGate;
    }

    private boolean isValidAerodrome(Aerodrome aerodrome)
    {
        return (aerodrome != null && 
                        (isValidAerodromeString(aerodrome.getIataName()) ||
                         isValidAerodromeString(aerodrome.getIcaoName()) ||
                         isValidAerodromeString(aerodrome.getFaaLid())));
    }

    private boolean isValidAerodromeString(String aerodrome)
    {
        return (aerodrome != null && !aerodrome.trim().isEmpty());
    }

    public void setAirports(String airportList)
    {
        this.airports = Arrays.asList(airportList.split(","));
    }

}
