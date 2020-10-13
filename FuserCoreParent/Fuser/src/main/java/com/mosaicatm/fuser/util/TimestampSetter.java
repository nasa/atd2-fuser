package com.mosaicatm.fuser.util;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.matmdata.common.MatmObject;

public class TimestampSetter
{
    private final Log log = LogFactory.getLog(getClass());
    
    private Clock clock;
    
    public void setTimestampFuserReceived(MatmObject flight, Long timestamp)
    {
        if (flight != null)
        {
            // only set the timestamp when received is null
            // IadsTestTool has the ability to override the received time
            if (flight.getTimestampFuserReceived() == null)
            {
                if (timestamp != null)
                {
                    flight.setTimestampFuserReceived(new Date(timestamp));
                }
                else
                {
                    log.warn( "Null timestampFuserReceived!!! Setting to current time." );
                    flight.setTimestampFuserReceived( getCurrentTime() );
                }
            }
            
            // Here we want to override the timestamp with timestampFuserReceived
            // As the timestamp has been proven as unreliable
            flight.setTimestamp(flight.getTimestampFuserReceived());
        }
    }
    
    private Date getCurrentTime()
    {
        if( getClock() != null )
        {
            return( new Date( getClock().getTimeInMillis() ));
        }
        else
        {
            return( new Date() );
        }
    }
    
    public MatmObject updateTimeFuserProcessed(MatmObject flight)
    {
        if (clock != null && flight != null)
            flight.setTimestampFuserProcessed(new Date(clock.getTimeInMillis()));

        return flight;
    }
    
    /**
     * calculate the time spent in message processing within Fuser 
     * @param flight
     * @return time in milliseconds
     */
    public static Long getFuserProcessTime(MatmObject flight)
    {
        if (flight != null && flight.getTimestampFuserProcessed() != null
                        && flight.getTimestampFuserReceived() != null)
        {
            return (flight.getTimestampFuserProcessed().getTime() -
                            flight.getTimestampFuserReceived().getTime());
        }
        
        return null;
    }
    
    public Clock getClock()
    {
        return clock;
    }

    public void setClock(Clock clock)
    {
        this.clock = clock;
    }
}
