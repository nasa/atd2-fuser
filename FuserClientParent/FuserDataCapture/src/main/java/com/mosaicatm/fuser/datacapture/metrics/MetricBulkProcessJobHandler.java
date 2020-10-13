package com.mosaicatm.fuser.datacapture.metrics;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.mosaicatm.lib.database.bulk.BulkProcessJobHandler;

public class MetricBulkProcessJobHandler<T>
implements BulkProcessJobHandler<T>
{
    private Timer executionTimer;
    private long startTime;
    
    public MetricBulkProcessJobHandler(MetricRegistry metricRegistry, String metricName)
    {
        if (metricRegistry != null)
        {
            executionTimer = metricRegistry.timer(metricName);
        }
    }
    
    @Override
    public void beforeProcess(List<T> messages)
    {
        if (messages != null )
        {
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void afterProcess(List<T> messages, long rows)
    {
        if (messages == null || messages.isEmpty())
            return;
        
        long processedTime = System.currentTimeMillis() - startTime;
        // Here we don't use rows because MatmFlight has extensions which will increase the size a lot
        // We only want to record the size of the MatmFlight messages
        long size = messages.size();
        long averageProcessTime = processedTime / size;
        
        if (executionTimer == null)
            return;
        
        for (int i = 0; i < size; i++)
        {
            executionTimer.update(averageProcessTime, TimeUnit.MILLISECONDS);
        }
        
    }
    
}
