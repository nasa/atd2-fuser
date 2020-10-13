package com.mosaicatm.fuser.store.listener;

import com.mosaicatm.fuser.store.FuserStore;
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
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class MatmSectorAssignmentStoreListener
implements FuserStoreListener<MatmSectorAssignment>, MessageProducerOwner
{
    private final Log logger = LogFactory.getLog(getClass());
    
    private String removeTopicUrl;
    
    private Timer timer;
    
    private long checkInterval = TimeFactory.MINUTE_IN_MILLIS;
    
    private boolean active;
    
    private Batch<MatmSectorAssignment> removeBatch;
    
    private MessageProducer messageProducer;
    
    private Clock clock;
    
    private FuserStore<MatmSectorAssignment, MetaData> store;
    
    public MatmSectorAssignmentStoreListener()
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
        
        removeBatch = new ThreadSafeBatch<MatmSectorAssignment>();
    }
    
    private void stop()
    {
         if (timer != null)
             timer.cancel();
    }

    @Override
    public void handleFuserStoreEvent(FuserStoreEvent<MatmSectorAssignment> event)
    {
        if (!active || removeBatch == null || event == null)
            return;
        
        FuserStoreEventType type = event.getEventType();
        MatmSectorAssignment sectorAssignment = event.getContent();
        if (type == null || sectorAssignment == null)
            return;
        
        switch (type)
        {
            case REMOVE:
                removeBatch.addItem(sectorAssignment);
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
            List<MatmSectorAssignment> sectorAssignmentList = removeBatch.drainBatch();
            
            if (sectorAssignmentList != null && sectorAssignmentList.size() > 0)
            {
                for (MatmSectorAssignment sectorAssignment : sectorAssignmentList){
                    
                    // Sectors close and open quickly. Prevent sending a remove if the
                    // sector was re-instated.
                    if(( store != null ) && 
                            ( store.get( store.getKey( sectorAssignment )) != null ))
                    {                    
                        continue;
                    }
                    
                    if(clock != null)
                        sectorAssignment.setTimestamp(new Date(clock.getTimeInMillis()));

                    publishTo(removeTopicUrl, sectorAssignment);
                }
            }
        }
    }

    private void publishTo(String url, MatmSectorAssignment sectorAssignment)
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
                logger.debug("removing sectorAssignment " + sectorAssignment.getSectorName());

            messageProducer.publish(url, sectorAssignment);
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

    public void setStore( FuserStore<MatmSectorAssignment, MetaData> store )
    {
        this.store = store;
    }
}
