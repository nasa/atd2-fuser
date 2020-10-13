package com.mosaicatm.fuser.datacapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.lib.messaging.MessageProducerSender;
import com.mosaicatm.lib.util.Batch;
import com.mosaicatm.lib.util.ThreadSafeBatch;
import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class FuserDataReceivingRerouteListener
implements FuserProcessedEventListener<MatmFlight>
{
    private Log log = LogFactory.getLog(getClass());
    
    private boolean active = false;
    
    private MessageProducerSender<MatmTransferEnvelope> sender;
    
    private Batch<MatmFlight> batch = new ThreadSafeBatch<>();
    
    private Timer timer;
    
    private long batchSize = 10;
    
    private long interval = 10000L;
    
    public FuserDataReceivingRerouteListener()
    {
        
    }
    
    public void start()
    {
        if (!active)
        {
            return;
        }
        
        timer = new Timer();
        
        timer.schedule(new SendTask(), interval, interval);
    }
    
    @Override
    public void dataAdded(MatmFlight data)
    {
        if (!active)
        {
            return;
        }
        
        process(data);
    }

    @Override
    public void dataUpdated(MatmFlight afterUpdating, MatmFlight update)
    {
        if (!active)
        {
            return;
        }

        process(update);
    }

    @Override
    public void dataRemoved(MatmFlight data)
    {
        // don't care about capturing removes
    }
    
    private void process(MatmFlight data)
    {
        if (!active || sender == null || data == null)
        {
            return;
        }
        
        MatmFlight clone = (MatmFlight) data.clone();
        batch.addItem(clone);
    }
    
    private void send()
    {
        if (batch != null)
        {
            List<MatmFlight> flights = batch.drainBatch();
            
            if (flights == null || flights.isEmpty())
            {
                return;
            }
            
            MatmTransferEnvelope envelope = new MatmTransferEnvelope();
            envelope.setFlights(new ArrayList<>());
            
            int size = 0;
            for (MatmFlight flight : flights)
            {
                size++;

                envelope.getFlights().add(flight);
                
                if (size >= batchSize)
                {
                    size = 0;
                    send(envelope);
                    envelope = new MatmTransferEnvelope();
                    envelope.setFlights(new ArrayList<>());
                }
            }
            
            send(envelope);
            log.info("Rerouted "+ flights.size() + " messages.");
        }
    }
    
    private void send(MatmTransferEnvelope envelope)
    {
        if (sender != null && envelope != null &&
            envelope.getFlights() != null &&
            !envelope.getFlights().isEmpty())
        {
            log.info("Rerouting " + envelope.getFlights().size() +
                " flight messages.");
            
            sender.send(envelope);
        }
    }

    class SendTask
    extends TimerTask
    {

        @Override
        public void run()
        {
            send();
        }
        
    }

    public void setSender(MessageProducerSender<MatmTransferEnvelope> sender)
    {
        this.sender = sender;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public void setInterval(long interval)
    {
        this.interval = interval;
    }

    public void setBatchSize(long batchSize)
    {
        this.batchSize = batchSize;
    }

}
  
   