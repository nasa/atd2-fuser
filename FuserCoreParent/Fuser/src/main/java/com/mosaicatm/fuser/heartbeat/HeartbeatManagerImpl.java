package com.mosaicatm.fuser.heartbeat;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.messaging.MessageProducerSender;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.matmdata.heartbeat.Heartbeat;

public class HeartbeatManagerImpl
implements HeartbeatManager
{
    private final Log log = LogFactory.getLog(getClass());
    private final String source = "fuser";
    private long heartbeatInterval = 10000L;
    private boolean active;
    
    private MessageProducerSender<Heartbeat> sender;
    
    private Timer timer;
    
    private Clock clock;
    
    public HeartbeatManagerImpl()
    {
        
    }
    
    @Override
    public void start()
    {
        if (!active)
        {
            return;
        }
        
        log.info("Start reporting hearbeat at a rate of " + heartbeatInterval);
        
        timer = new Timer("HeartbeatTask");
        
        timer.scheduleAtFixedRate(new HeartbeatTask(), heartbeatInterval, heartbeatInterval);
    }
    
    @Override
    public void stop()
    {
        if (!active)
        {
            return;
        }
        
        if (timer != null)
        {
            log.info("Stop reporting heartbeat.");
            timer.cancel();
        }
    }
    
    class HeartbeatTask extends TimerTask
    {
        @Override
        public void run()
        {
            send();
        }
    }
    
    private void send()
    {
        if (active && sender != null && clock != null)
        {
            Heartbeat heartbeat = new Heartbeat();
            heartbeat.setLastUpdateTime(new Date(clock.getTimeInMillis()));
            heartbeat.setSource(source);
            sender.send(heartbeat);
        }
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public void setSender(MessageProducerSender<Heartbeat> sender)
    {
        this.sender = sender;
    }

    public void setClock(Clock clock)
    {
        this.clock = clock;
    }

    public void setHeartbeatInterval(long heartbeatInterval)
    {
        this.heartbeatInterval = heartbeatInterval;
    }
}
