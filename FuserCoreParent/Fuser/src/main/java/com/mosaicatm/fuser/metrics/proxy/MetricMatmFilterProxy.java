package com.mosaicatm.fuser.metrics.proxy;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.mosaicatm.fuser.filter.MatmFilter;

public class MetricMatmFilterProxy<T>
implements MatmFilter<T>
{

    private Timer executionTimer;
    private MatmFilter<T> filter;
    
    public MetricMatmFilterProxy (MetricRegistry metricRegistry, MatmFilter<T> filter)
    {
        if (metricRegistry != null && filter != null)
            executionTimer = metricRegistry.timer(filter.getClass().getName());
        this.filter = filter;
    }
    @Override
    public T filter(T flight)
    {
        Timer.Context timerContext = null;
        try
        {
            if (executionTimer != null)
                timerContext = executionTimer.time();
            return filter.filter(flight);
        }
        finally
        {
            if (timerContext != null)
                timerContext.close();
        }
    }

    @Override
    public boolean isActive()
    {
        return filter.isActive();
    }

}
