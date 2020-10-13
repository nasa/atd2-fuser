package com.mosaicatm.fuser.metrics.proxy;

import java.util.Map;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.mosaicatm.fuser.store.redis.RedisBulkTask;

public class MetricRedisBulkTaskProxy
implements RedisBulkTask
{
    private RedisBulkTask task;
    public MetricRedisBulkTaskProxy (MetricRegistry metricRegistry, final RedisBulkTask task, String id)
    {
        if (metricRegistry != null && task != null)
        {
            String key = task.getClass().getName();
            
            if (id != null)
                key = id + key;
            
            metricRegistry.register(key, new Gauge<Integer>()
            {
                @Override
                public Integer getValue()
                {
                    return ((task.updateSize() + task.removeSize()));
                }
            });
        }
        
        this.task = task;
    }
    
    @Override
    public void addUpdate(String key, String field, String body)
    {
        task.addUpdate(key, field, body);
    }

    @Override
    public void addUpdate(String key, Map<String, String> updates)
    {
        task.addUpdate(key, updates);
    }

    @Override
    public void addDelete(String key, String... fields)
    {
        task.addDelete(key, fields);
    }

    @Override
    public int updateSize()
    {
        return task.updateSize();
    }

    @Override
    public int removeSize()
    {
        return task.removeSize();
    }

}
