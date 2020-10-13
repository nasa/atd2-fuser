package com.mosaicatm.fuser.store.listener;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.store.event.FuserStoreEvent;
import com.mosaicatm.fuser.store.event.FuserStoreEvent.FuserStoreEventType;
import com.mosaicatm.fuser.store.event.FuserStoreListener;
import com.mosaicatm.lib.messaging.MessageProducer;
import com.mosaicatm.lib.messaging.MessageProducerOwner;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.util.Batch;
import com.mosaicatm.lib.util.ThreadSafeBatch;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class MatmAircraftStoreListener
implements FuserStoreListener<MatmAircraft>, MessageProducerOwner
{
    private final Log logger = LogFactory.getLog(getClass());
    
    private String removeTopicUrl;
    
    private Timer timer;
    
    private long checkInterval = TimeFactory.MINUTE_IN_MILLIS;
    
    private boolean active;
    
    private Batch<MatmAircraft> removeBatch;
    
    private MessageProducer messageProducer;
    
    private Clock clock;
    
    public MatmAircraftStoreListener()
    {
        
    }
    
    public void start()
    {
        if (!active)
            return;
        
        if (timer != null)
            stop ();
        
        timer = new Timer ();
        timer.scheduleAtFixedRate(new RemoveTask(), checkInterval, checkInterval);
        
        removeBatch = new ThreadSafeBatch<MatmAircraft>();
    }
    
    private void stop()
    {
         if (timer != null)
             timer.cancel();
    }

    @Override
    public void handleFuserStoreEvent(FuserStoreEvent<MatmAircraft> event)
    {
        if (!active || removeBatch == null || event == null)
            return;
        
        FuserStoreEventType type = event.getEventType();
        MatmAircraft aircraft = event.getContent();
        if (type == null || aircraft == null)
            return;
        
        switch (type)
        {
            case REMOVE:
                removeBatch.addItem(aircraft);
                break;
            case ADD:
            case UPDATE:
            case NOT_SET:
            default:
                break;
        }
    }
    
    private class RemoveTask
    extends TimerTask
    {
        @Override
        public void run() 
        {
            List<MatmAircraft> aircraftList = removeBatch.drainBatch();
            
            if (aircraftList != null && aircraftList.size() > 0)
            {
                for (MatmAircraft aircraft : aircraftList){
                    if(clock != null)
                        aircraft.setTimestamp(new Date(clock.getTimeInMillis()));
                    publishTo(removeTopicUrl, aircraft);
                }
            }
        }
    }

    private void publishTo(String url, MatmAircraft aircraft)
    {
        if (!active)
            return;
        
        if (url == null)
        {
            logger.warn("Null url to publish message to ");
        }
        else if (messageProducer == null)
        {
            logger.warn("Message producer is not initialized");
        }
        else
        {
            if (logger.isDebugEnabled())
                logger.debug("removing aircraft " + aircraft.getRegistration());

            messageProducer.publish(url, aircraft);
        }
    }

    public void setCheckInterval(long checkInterval)
    {
        this.checkInterval = checkInterval;
    }

    @Override
    public void setMessageProducer(MessageProducer messageProducer)
    {
        this.messageProducer = messageProducer;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public void setRemoveTopicUrl(String removeTopicUrl)
    {
        this.removeTopicUrl = removeTopicUrl;
    }

    public void setClock(Clock clock)
    {
        this.clock = clock;
    }

}
