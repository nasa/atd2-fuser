package com.mosaicatm.fuser.metrics.proxy;

import java.util.List;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.mosaicatm.fuser.transform.batch.SourceBatchTransformApi;

public class MetricSourceBatchTransformApiProxy<E, K>
implements SourceBatchTransformApi<E, K>
{
    private Timer executionTimer;
    private SourceBatchTransformApi<E, K> api;
    
    public MetricSourceBatchTransformApiProxy(MetricRegistry metricRegistry, SourceBatchTransformApi<E, K> api)
    {
        if (metricRegistry != null && api != null)
            this.executionTimer = metricRegistry.timer(api.getClass().getName());
        this.api = api;
    }
    
    @Override
    public E toBatch(List<K> batch)
    {
        return api.toBatch(batch);
    }

    @Override
    public List<K> fromBatch(E batch)
    {
        Timer.Context context = null;
        try
        {
            if (executionTimer != null)
                context = executionTimer.time();
            return api.fromBatch(batch);
        }
        finally
        {
            if (context != null)
                context.close();
        }
    }

}
