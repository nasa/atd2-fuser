package com.mosaicatm.fuser.datacapture.metrics;

import com.codahale.metrics.Gauge;
import com.mosaicatm.lib.database.bulk.BulkLoader;
import com.mosaicatm.performancemonitor.common.metric.MetricProxyFactoryImpl;

public class FuserMetricProxyFactory<T>
extends MetricProxyFactoryImpl<T>
{
    public BulkLoader<T> monitorProcessTime(BulkLoader<T> bulkLoader, String metricName)
    {
        if (metricsActive && metricRegistry != null && bulkLoader != null && bulkLoader.isActive())
        {
           bulkLoader.setProcessJobHandler(
               new MetricBulkProcessJobHandler<T>(metricRegistry, metricName));
        }
        
        return bulkLoader;
    }
    
    public BulkLoader<T> monitorSize(final BulkLoader<T> loader, String appendName)
    {
        if(metricsActive && metricRegistry != null && loader != null && loader.isActive())
        {
            metricRegistry.register(
                loader.getClass().getName() + "." + appendName,
                new Gauge<Integer>()
                {
                    @Override
                    public Integer getValue()
                    {
                        return (loader.size());
                    }
                    
                });
        }
        
        return loader;
    }
}
