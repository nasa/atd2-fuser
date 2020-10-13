package com.mosaicatm.fuser.metrics.proxy;

import java.util.Date;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.mosaicatm.fuser.updaters.Updater;

public class MetricUpdaterProxy<U,T> implements Updater <U, T>
{
    private Timer executionTimer;
    private Updater<U,T> updater;
    
    public MetricUpdaterProxy(MetricRegistry metricRegistry, Updater <U, T> updater)
    {
        if (metricRegistry != null && updater != null)
            this.executionTimer = metricRegistry.timer(updater.getClass().getName());
        this.updater = updater;
    }

    @Override
    public void update(U update, T target)
    {
        Timer.Context timeContext = null;
        
        try
        {
            if (executionTimer != null)
                timeContext = executionTimer.time();
            updater.update(update, target);
        }
        finally
        {
            //this stops the execution timing
            if(timeContext != null)
            {
                timeContext.stop();
            }
        }
    }

    @Override
    public void sweeperUpdate (Date currentTime, U currentState)
    {
        Timer.Context timeContext = null;
        
        try
        {
            if (executionTimer != null)
                timeContext = executionTimer.time();
            updater.sweeperUpdate(currentTime, currentState);
        }
        finally
        {
            //this stops the execution timing
            if(timeContext != null)
            {
                timeContext.stop();
            }
        }
    }    
}
