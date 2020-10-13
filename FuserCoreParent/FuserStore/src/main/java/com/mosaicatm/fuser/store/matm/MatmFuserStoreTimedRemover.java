package com.mosaicatm.fuser.store.matm;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFuserStoreTimedRemover
extends AbstractFuserStoreTimedRemover<MatmFlight>
{
    private static final String FMC_SOURCE = FuserSource.FMC.toString();
    
    private static final String[] SURFACE_SURVEILLANCE_SOURCES_ARRAY = 
    { 
        FuserSource.ASDEX.toString() , 
        FuserSource.FLIGHTHUB_POSITION.toString() 
    };
    
    private static final HashSet<String> SURFACE_SURVEILLANCE_SOURCES = new HashSet<>( 
        Arrays.asList( SURFACE_SURVEILLANCE_SOURCES_ARRAY ));
    
    private final Log log = LogFactory.getLog(getClass());
    
    // These tolerances need to be bigger than the GufiService or bad things happen
    private long removeThresholdMillis = 36 * TimeFactory.HOUR_IN_MILLIS;
    private long removeArrivedFlightThresholdMillis = 10 * TimeFactory.HOUR_IN_MILLIS;
    private long removeActiveFlightThresholdMillis = 20 * TimeFactory.HOUR_IN_MILLIS;
    private long removeNonDepartedFlightThresholdMillis = 30 * TimeFactory.HOUR_IN_MILLIS;
    private long removeAsdexOnlyFlightThresholdMillis = 1 * TimeFactory.HOUR_IN_MILLIS;
    
    public MatmFuserStoreTimedRemover (FuserStore<MatmFlight, MetaData> store)
    {
        super("MatmFlight", store);
    }    

    @Override
    public boolean isExpired (MatmFlight flight)
    {
        long curr_time_in_millis = getClock().getTimeInMillis();
        
        // Check if flight is expected to operate in the future
        long future_time = getLatestTimeMillis( 
                    flight.getArrivalRunwayScheduledTime(), 
                    flight.getArrivalRunwayEstimatedTime(),
                    flight.getArrivalRunwayUndelayedTime(), 
                    flight.getArrivalRunwayTargetedTime(),
                    flight.getArrivalRunwayActualTime(), 
                    flight.getArrivalStandScheduledTime(),
                    flight.getArrivalStandEstimatedTime(),
                    flight.getArrivalStandAirlineTime(), 
                    flight.getArrivalStandUndelayedTime(),                    
                    flight.getArrivalStandTargetedTime(),
                    flight.getArrivalStandActualTime() );       
        
        // If we have an arrival time, we can just check that.
        if( future_time > 0 )
        {
            if( future_time >= curr_time_in_millis ) 
            {
                return( false );
            }
        }
        // If no arrival time, check the departure time
        else
        {
            future_time = getLatestTimeMillis( 
                    flight.getDepartureRunwayScheduledTime(), 
                    flight.getDepartureRunwayEstimatedTime(),
                    flight.getDepartureRunwayUndelayedTime(), 
                    ( flight.getDepartureRunwayTargetedTime() != null ) ? flight.getDepartureRunwayTargetedTime().getValue() : null,
                    ( flight.getDepartureRunwayActualTime() != null ) ? flight.getDepartureRunwayActualTime().getValue() : null,
                    flight.getDepartureStandScheduledTime(),
                    flight.getDepartureStandEstimatedTime(),
                    flight.getDepartureStandAirlineTime(), 
                    flight.getDepartureStandUndelayedTime(),                    
                    flight.getDepartureStandTargetedTime(),
                    ( flight.getDepartureStandActualTime() != null ) ? flight.getDepartureStandActualTime().getValue() : null );  
            
            if( future_time >= curr_time_in_millis ) 
            {
                return( false );
            }            
        }        
        
        //Get the latest external timestamp (ignoring FMC updates)
        Date last_external_update_time = getLatestExternalTimestamp( flight );
        
        if (( last_external_update_time != null ) && ( last_external_update_time.getTime() > 0 ))
        {
            //Check for the basic timeout by timestamp difference
            double stale_millis = curr_time_in_millis - last_external_update_time.getTime();

            //Check for an arrived flight
            if(( flight.getArrivalRunwayActualTime() != null ) || ( flight.getArrivalStandActualTime() != null ))
            {
                if (stale_millis > removeArrivedFlightThresholdMillis)
                {
                    if (log.isDebugEnabled())
                        log.debug( String.format("Flight %1s expired: actual arrival runway or " +
                                "actual arrival stand time over %2$.1f hours in the past.",
                                flight.getGufi(), stale_millis / 3600000 ));

                    return( true );
                }
            }
            //If not arrived, no position data was ever received, and flight has not departed
            else if(( flight.getDepartureRunwayActualTime() == null ) && ( flight.getDepartureStandActualTime() == null ) && 
                    ( flight.getPosition() == null ))
            {
                if (stale_millis > removeNonDepartedFlightThresholdMillis)
                {
                    if (log.isDebugEnabled())
                        log.debug( String.format("Flight %1s expired: expected departure time " +
                                "for non-departed flight over %2$.1f hours in the past, and no more recent updates.",
                                flight.getGufi(), stale_millis / 3600000 ));                  

                    return( true );
                }                
            }    
            //If not arrived and position data exists, handle this case
            else if(( flight.getPosition() != null ) && ( flight.getPosition().getTimestamp() != null ))
            {
                long threshold_millis = 0;
                String reason = "";

                //ASDEX only can be detected if airports are not set and last message type is ASDEX
                if(( flight.getLastUpdateSource() != null ) && 
                        SURFACE_SURVEILLANCE_SOURCES.contains( flight.getLastUpdateSource() ) &&
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

                if (stale_millis > threshold_millis)
                {
                    if (log.isDebugEnabled())
                        log.debug( String.format("Flight %1s expired: latest update " +
                                "for %2s flight over %3$.1f hours in the past.",
                                flight.getGufi(), reason, stale_millis / 3600000 ));                  

                    return( true );
                }
            }            
            
            if (stale_millis > removeThresholdMillis)
            {
                if (log.isDebugEnabled())
                    log.debug( String.format("Flight %1s expired: latest update " +
                            "over %2$.1f hours in the past.",
                            flight.getGufi(), stale_millis / 3600000 ));                  
                
                return( true ); 
            }
        }        
        
        return( false );
    }

    private long getLatestTimeMillis( Date ... times )
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
     
    private Date getLatestExternalTimestamp( MatmFlight flight )
    {
        Date latest_time = null;
        
        if(( flight.getUpdateSources() != null ) && !flight.getUpdateSources().isEmpty() )
        {
            for( MetaData meta : flight.getUpdateSources() )
            {
                if (meta == null)
                {
                    log.error(flight.getGufi() + " contains null meta data");
                    continue;
                }
                
                if (meta.getTimestamp() == null)
                {
                    log.warn(flight.getGufi() + " has null timestamp for " + 
                             meta.getSource() + "/" + meta.getSystemType());
                    continue;
                }
                
                if( !FMC_SOURCE.equals( meta.getSource() ) && 
                        (( latest_time == null ) || ( latest_time.before( meta.getTimestamp() ))))
                {
                    latest_time = meta.getTimestamp();
                }
            }
        }
        
        if( latest_time != null )
            return( latest_time );
        else
            return( flight.getTimestamp() );
    }    
    
    public void setRemoveThresholdHours (int hours)
    {
        setRemoveThresholdMillis (hours * TimeFactory.HOUR_IN_MILLIS);
    }
    
    public void setRemoveThresholdMinutes (int minutes)
    {
        setRemoveThresholdMillis (minutes * TimeFactory.MINUTE_IN_MILLIS);
    }
    
    public void setRemoveThresholdMillis (long removeThresholdMillis)
    {
        this.removeThresholdMillis = removeThresholdMillis;
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
}
