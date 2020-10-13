package com.mosaicatm.fuser.metrics.proxy;

import java.lang.reflect.InvocationTargetException;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.mosaicatm.fuser.aggregator.AggregationResult;
import com.mosaicatm.fuser.aggregator.MetaDataManager;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MetricMetaDataManagerProxy<T extends MatmObject>
extends MetaDataManager<T>
{
    private Timer executionTimer;
    private MetaDataManager<T> metaDataManager;
    
    public MetricMetaDataManagerProxy(MetricRegistry metricRegistry, MetaDataManager<T> metaDataManager)
    {
        if (metricRegistry != null && metaDataManager != null)
            this.executionTimer = metricRegistry.timer(metaDataManager.getClass().getName());
        this.metaDataManager = metaDataManager;
    }
    
    @Override
    public void createRecord(T update) throws IllegalAccessException, InvocationTargetException, 
        NoSuchMethodException, IllegalArgumentException
    {
        metaDataManager.createRecord(update);
    }
 
    @Override
    public int applyRules(T update, T target, AggregationResult result)
    {
        Timer.Context timeContext = null;
        int count;
        try
        {
            if (executionTimer != null)
                timeContext = executionTimer.time();
            count = metaDataManager.applyRules(update, target, result);
        }
        finally
        {
            //this stops the execution timing
            if(timeContext != null)
            {
                timeContext.stop();
            }
        }
        
        return count;
    }

}
