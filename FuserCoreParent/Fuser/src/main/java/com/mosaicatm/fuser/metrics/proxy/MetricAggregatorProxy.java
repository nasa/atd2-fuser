package com.mosaicatm.fuser.metrics.proxy;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.mosaicatm.fuser.aggregator.Aggregator;

public class MetricAggregatorProxy<T> implements Aggregator<T>
{
    private Timer executionTimer;
    private Aggregator<T> aggregator;
    
    public MetricAggregatorProxy(MetricRegistry metricRegistry, Aggregator<T> aggregator)
    {
        if (metricRegistry != null && aggregator != null)
            this.executionTimer = metricRegistry.timer(aggregator.getClass().getName());
        
        this.aggregator = aggregator;
    }
    
    @Override
    public T aggregate(T update)
    {
        Timer.Context timeContext = null;
        
        try
        { 
            if (executionTimer != null)
                timeContext = executionTimer.time();
            
            return aggregator.aggregate(update);
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
