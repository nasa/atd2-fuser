package com.mosaicatm.fuser.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.messaging.MessageProducer;
import com.mosaicatm.lib.messaging.MessageProducerOwner;

public abstract class AbstractBatcher<T, B>
implements MessageProducerOwner
{
    private final Log log = LogFactory.getLog(getClass());

    private final List<T> currentBatch;

    private MessageProducer producer;

    private String destination;
    
    private long loggingInterval = 60000;
    private long lastLoggingTime;
    private long currentBatchCount = 0;
    private long currentBatchSize = 0;
    
    // Trigger a sendBatch once the currentBatch reaches this size
    private long maxBatchSize = 1_000;


    protected AbstractBatcher()
    {
        currentBatch =  new ArrayList<>();
    }

    public abstract B createBatchMessage(List<T> batch);
    
    public void init()
    {
        TimerTask sendBatchTask = new BatcherTimerTask();

        Timer timer = new Timer("Batch Send Timer");
        timer.schedule(sendBatchTask, 200L, 200L);
    }

    public void batch(T data)
    {
        if(data == null)
        {
            if(log.isTraceEnabled())
            {
                log.trace("Null update received in the batcher. This could just simply mean we got"
                        + " an update with no diffs");
            }

            return;
        }

        List<T> sendBatch = null;
        synchronized(currentBatch)
        {
            currentBatch.add(data);

            // Don't let the batch get too large
            if( currentBatch.size() > maxBatchSize )
            {
                sendBatch = emptyBatch();
            }
        }
        if( sendBatch != null )
        {
            doSend( sendBatch );
        }
    }

    public void sendBatch()
    {
        if(!currentBatch.isEmpty())
        {
            final List<T> sendBatch = emptyBatch();
            
            doSend( sendBatch );
        }
    }
    
    public List<T> emptyBatch()
    {
        final List<T> sendBatch;
        synchronized(currentBatch)
        {
            sendBatch = new ArrayList<>(currentBatch);
            currentBatch.clear();
            
            currentBatchCount++;
            currentBatchSize += sendBatch.size();
        }
        
        return sendBatch;
    }
    
    public void doSend( List<T> sendBatch )
    {
        //do something with the sendBatch
        B batchMessage = createBatchMessage(sendBatch);

        if (batchMessage != null)
        {
            if(destination == null)
            {
                log.error("Cannot publish MatmTransferEnvelope the destination endpoint is null");
                return;
            }

            if(producer == null)
            {
                log.error("Cannot publish MatmTransferEnvelope the producer is null");
                return;
            }

            if(log.isDebugEnabled())
                log.debug("Ready to send batch " + sendBatch.size());

            producer.publish(destination, batchMessage);

            if(log.isDebugEnabled())
                log.debug("Batch sent " + sendBatch.size());
        }

        long currentTime = System.currentTimeMillis();
        if((lastLoggingTime + loggingInterval) <=  currentTime)
        {
            log.info("Processed " + currentBatchCount + " batches with a total " + 
                    currentBatchSize + " messages. Average batch size: " + (currentBatchSize/currentBatchCount));
            
            lastLoggingTime = currentTime;
            currentBatchCount = 0;
            currentBatchSize = 0;
        }
    }

    public class BatcherTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            sendBatch();
        }                

    }

    public String getDestination()
    {
        return destination;
    }

    public void setDestination(String destination)
    {
        this.destination = destination;
    }

    public void setLoggingInterval(long loggingInterval)
    {
        this.loggingInterval = loggingInterval;
    }

    @Override
    public void setMessageProducer(MessageProducer producer)
    {
        this.producer = producer;
    }

    public void setMaxBatchSize( int maxBatchSize )
    {
        this.maxBatchSize = maxBatchSize;
    }
}
