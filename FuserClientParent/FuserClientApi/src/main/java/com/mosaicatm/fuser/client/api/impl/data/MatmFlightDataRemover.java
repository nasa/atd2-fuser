package com.mosaicatm.fuser.client.api.impl.data;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFlightDataRemover
extends AbstractDataRemover<MatmFlight>
{
    private final Log log = LogFactory.getLog(getClass());

    private long removeWindow = 36 * TimeFactory.HOUR_IN_MILLIS; 
    private long removeArrivedFlightThresholdMillis = 2 * TimeFactory.HOUR_IN_MILLIS;
    private long removeActiveFlightThresholdMillis = 20 * TimeFactory.HOUR_IN_MILLIS;
    private long removeNonDepartedFlightThresholdMillis = 8 * TimeFactory.HOUR_IN_MILLIS;
    private long removeAsdexOnlyFlightThresholdMillis = 2 * TimeFactory.HOUR_IN_MILLIS;
    
    public MatmFlightDataRemover ()
    {
        super (null, null);
    }
    
    public MatmFlightDataRemover (FuserClientStore<MatmFlight> store)
    {
        super (store, null);
    }
    
    public MatmFlightDataRemover (FuserClientStore<MatmFlight> store, Clock clock)
    {
        super(store, clock);
    }
    
    @Override
    public boolean isExpired (MatmFlight flight)
    {
        Date timestamp = flight.getTimestamp();
        long curr_time_in_millis = getClock().getTimeInMillis();
        
        //Check for an arrived flight
        if(( flight.getArrivalRunwayActualTime() != null ) || ( flight.getArrivalStandActualTime() != null ))
        {
            double diff = curr_time_in_millis - getLatestTimeMillis( 
                    flight.getArrivalStandActualTime(), 
                    flight.getArrivalRunwayActualTime(),
                    timestamp );                   

            if (diff >= removeArrivedFlightThresholdMillis)
            {
                if (log.isDebugEnabled())
                    log.debug( String.format("Flight %1s expired: actual arrival runway or " +
                            "actual arrival stand time over %2$.1f hours in the past.",
                            flight.getGufi(), diff / 3600000f ));
                
                return( true );
            }
            
        }
        //If not arrived, no position data was ever received, and flight has not departed, look at the departure time data
        else if(( flight.getDepartureRunwayActualTime() == null ) && ( flight.getDepartureStandActualTime() == null ) && 
                ( flight.getPosition() == null ))
        {
            long latest_time = getLatestTimeMillis( 
                    flight.getDepartureRunwayEstimatedTime(), 
                    ( flight.getEstimatedDepartureClearanceTime() != null ) ? flight.getEstimatedDepartureClearanceTime().getValue() : null,
                    flight.getDepartureRunwayProposedTime(),
                    flight.getDepartureRunwayScheduledTime(),
                    ( flight.getDepartureRunwayTargetedTime() != null ) ? flight.getDepartureRunwayTargetedTime().getValue() : null,
                    ( flight.getDepartureRunwayMeteredTime() != null && !flight.getDepartureRunwayMeteredTime().isNil() ) ? flight.getDepartureRunwayMeteredTime().getValue().getValue() : null );
            
            if( latest_time > 0 )
            {
                //make sure no later messages were received
                if( timestamp != null )
                    latest_time = Math.max( timestamp.getTime(), latest_time );
                
                double diff = curr_time_in_millis - latest_time;
                if (diff >= removeNonDepartedFlightThresholdMillis)
                {
                    if (log.isDebugEnabled())
                        log.debug( String.format("Flight %1s expired: expected departure time " +
                                "for non-departed flight over %2$.1f hours in the past, and no more recent updates.",
                                flight.getGufi(), diff / 3600000f ));                  

                    return( true );
                }                
            }      
        }
      //If not arrived and position data exists, handle this case
        else if(( flight.getPosition() != null ) && ( flight.getPosition().getTimestamp() != null ))
        {
            long threshold_millis = 0;
            String reason = "";
            
            //ASDEX only can be detected if airports are not set and last message type is ASDEX
            if(( flight.getLastUpdateSource() != null ) && flight.getLastUpdateSource().equals( "ASDEX" ) &&
                    (( flight.getDepartureAerodrome() == null ) || 
                        (( flight.getDepartureAerodrome().getIataName() == null ) && 
                         ( flight.getDepartureAerodrome().getIcaoName() == null ))) &&
                    (( flight.getArrivalAerodrome() == null ) || 
                        (( flight.getArrivalAerodrome().getIataName() == null ) && 
                         ( flight.getArrivalAerodrome().getIcaoName() == null ))))
            {
                threshold_millis = removeAsdexOnlyFlightThresholdMillis;
                reason = "ASDEX only";
            }
            else
            {
                threshold_millis = removeActiveFlightThresholdMillis;
                reason = "active state";
            }
            
            double diff = curr_time_in_millis - getLatestTimeMillis( 
                    flight.getPosition().getTimestamp(), 
                    timestamp );

            if (diff >= threshold_millis)
            {
                if (log.isDebugEnabled())
                    log.debug( String.format("Flight %1s expired: latest update " +
                            "for %2s flight over %3$.1f hours in the past.",
                            flight.getGufi(), reason, diff / 3600000f ));                  
                
                return( true );
            }
        }
        
        //default timeout check
        if (timestamp != null)
        {
            long diff = curr_time_in_millis - timestamp.getTime();
            
            if (diff >= removeWindow) 
            {
                if (log.isDebugEnabled()) {
                    log.debug( String.format("Flight %1s expired: latest update " +
                            "over %2$.1f hours in the past.",
                            flight.getGufi(), diff / 3600000f ));
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    private static long getLatestTimeMillis( Date ... times )
    {
        long latest_time = 0;

        if( times != null )
        {
            for( Date time : times )
            {
                if( time != null )
                    latest_time = Math.max( latest_time, time.getTime() );        
            }
        }
        
        return( latest_time );
    }
    
    public void setRemoveWindowHours (int hours)
    {
        setRemoveWindowMillis (hours * TimeFactory.HOUR_IN_MILLIS);
    }
    
    public void setRemoveWindowMinutes (int minutes)
    {
        setRemoveWindowMillis (minutes * TimeFactory.MINUTE_IN_MILLIS);
    }
    
    public void setRemoveWindowMillis (long removeWindow)
    {
        this.removeWindow = removeWindow;
    }

    public void setRemoveActiveFlightThresholdHours (int hours)
    {
        setRemoveActiveFlightThresholdMillis (hours * TimeFactory.HOUR_IN_MILLIS);
    }
    
    public void setRemoveActiveFlightThresholdMinutes (int minutes)
    {
        setRemoveActiveFlightThresholdMillis (minutes * TimeFactory.MINUTE_IN_MILLIS);
    }
    
    public void setRemoveActiveFlightThresholdMillis( long removeActiveFlightThresholdMillis )
    {
        this.removeActiveFlightThresholdMillis = removeActiveFlightThresholdMillis;
    }
    
    public void setRemoveArrivedFlightThresholdHours (int hours)
    {
        setRemoveArrivedFlightThresholdMillis (hours * TimeFactory.HOUR_IN_MILLIS);
    }
    
    public void setRemoveArrivedFlightThresholdMinutes (int minutes)
    {
        setRemoveArrivedFlightThresholdMillis (minutes * TimeFactory.MINUTE_IN_MILLIS);
    }     
    
    public void setRemoveArrivedFlightThresholdMillis( long removeArrivedFlightThresholdMillis )
    {
        this.removeArrivedFlightThresholdMillis = removeArrivedFlightThresholdMillis;
    }
    
    public void setRemoveNonDepartedFlightThresholdHours (int hours)
    {
        setRemoveNonDepartedFlightThresholdMillis (hours * TimeFactory.HOUR_IN_MILLIS);
    }
    
    public void setRemoveNonDepartedFlightThresholdMinutes (int minutes)
    {
        setRemoveNonDepartedFlightThresholdMillis (minutes * TimeFactory.MINUTE_IN_MILLIS);
    }       
    
    public void setRemoveNonDepartedFlightThresholdMillis( long removeNonDepartedFlightThresholdMillis )
    {
        this.removeNonDepartedFlightThresholdMillis = removeNonDepartedFlightThresholdMillis;
    }
    
    public void setRemoveAsdexOnlyFlightThresholdHours (int hours)
    {
        setRemoveAsdexOnlyFlightThresholdMillis (hours * TimeFactory.HOUR_IN_MILLIS);
    }
    
    public void setRemoveAsdexOnlyFlightThresholdMinutes (int minutes)
    {
        setRemoveAsdexOnlyFlightThresholdMillis (minutes * TimeFactory.MINUTE_IN_MILLIS);
    }       
    
    public void setRemoveAsdexOnlyFlightThresholdMillis( long removeAsdexOnlyFlightThresholdMillis )
    {
        this.removeAsdexOnlyFlightThresholdMillis = removeAsdexOnlyFlightThresholdMillis;
    } 
    
    public long getRemoveArrivedFlightThresholdMillis() {
        return removeArrivedFlightThresholdMillis;
    }

    public long getRemoveActiveFlightThresholdMillis() {
        return removeActiveFlightThresholdMillis;
    }

    public long getRemoveNonDepartedFlightThresholdMillis() {
        return removeNonDepartedFlightThresholdMillis;
    }

    public long getRemoveAsdexOnlyFlightThresholdMillis() {
        return removeAsdexOnlyFlightThresholdMillis;
    }

}
