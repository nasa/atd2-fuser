package com.mosaicatm.fuser.common.matm.util.flight;

import java.util.Date;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class DefaultMatmFlightTimeChooser
{
    
    public static Date getBestAvailableInTime(MatmFlight flight)
    {
        // Order: actual, airline, estimated,
        // proposed, scheduled
        if (flight == null)
            return null;
        
        Date time = null;
        
        if (flight.getArrivalStandActualTime() != null)
            time = flight.getArrivalStandActualTime();
        
        else if (flight.getArrivalStandAirlineTime() != null)
            time = flight.getArrivalStandAirlineTime();
        
        else if (flight.getArrivalStandEstimatedTime() != null)
            time = flight.getArrivalStandEstimatedTime();
        
        else if (flight.getArrivalStandProposedTime() != null)
            time = flight.getArrivalStandProposedTime();

        else if (flight.getArrivalStandScheduledTime() != null)
            time = flight.getArrivalStandScheduledTime();
        
        return time;
    }
    
    public static Date getBestAvailableOnTime(MatmFlight flight)
    {
        // Order: actual, metered, estimated
        if (flight == null)
            return null;
        
        Date time = null;
        
        if (flight.getArrivalRunwayActualTime() != null)
            time = flight.getArrivalRunwayActualTime();
        
        else if (flight.getArrivalRunwayMeteredTime() != null &&
                       flight.getArrivalRunwayMeteredTime().getValue() != null)
            time = flight.getArrivalRunwayMeteredTime().getValue();

        else if (flight.getArrivalRunwayEstimatedTime() != null)
            time = flight.getArrivalRunwayEstimatedTime();
        
        else if (flight.getArrivalRunwayScheduledTime() != null)
            time = flight.getArrivalRunwayScheduledTime();
            
        return time;
    }
    
    public static Date getBestAvailableOutTime(MatmFlight flight)
    {
        // Order: actual, intended, earliest, airline
        // estimated, proposed, scheduled, initial
        if (flight == null)
            return null;
        
        Date time = null;
        
        if (flight.getDepartureStandActualTime() != null &&
                       flight.getDepartureStandActualTime().getValue() != null)
            time = flight.getDepartureStandActualTime().getValue();
        
        else if (flight.getDepartureStandIntendedTime() != null)
            time = flight.getDepartureStandIntendedTime();
        
        else if (flight.getDepartureStandEarliestTime() != null)
            time = flight.getDepartureStandEarliestTime();
        
        else if (flight.getDepartureStandAirlineTime() != null)
            time = flight.getDepartureStandAirlineTime();
        
        else if (flight.getDepartureStandEstimatedTime() != null)
            time = flight.getDepartureStandEstimatedTime();
        
        else if (flight.getDepartureStandProposedTime() != null)
            time = flight.getDepartureStandProposedTime();
        
        else if (flight.getDepartureStandScheduledTime() != null)
            time = flight.getDepartureStandScheduledTime();
        
        else if (flight.getDepartureStandInitialTime() != null)
            time = flight.getDepartureStandInitialTime();
        
        return time;
    }
    
    public static Date getBestAvailableOffTime(MatmFlight flight)
    {
        // Order: actual, estimated, scheduled
        if (flight == null)
            return null;
        
        Date time = null;
        
        if (flight.getDepartureRunwayActualTime() != null && 
                        !flight.getDepartureRunwayActualTime().isNil())
            time = flight.getDepartureRunwayActualTime().getValue();
        
        else if (flight.getDepartureRunwayEstimatedTime() != null)
            time = flight.getDepartureRunwayEstimatedTime();
        
        else if (flight.getDepartureRunwayScheduledTime() != null)
            time = flight.getDepartureRunwayScheduledTime();
        
        return time;
    }
    
    public static Date findMinimumDate(Date date1, Date date2)
    {
        
        if (date1 == null)
            return date2;
        
        if (date2 == null)
            return date1;
        
        if (date1.compareTo(date2) < 0)
        {
            return date1;
        }
        else
        {
            return date2;
        }
        
    }
    
}
