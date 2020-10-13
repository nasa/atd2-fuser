package com.mosaicatm.fuser.metrics;

import com.codahale.metrics.MetricRegistry;
import com.mosaicatm.fuser.aggregator.Aggregator;
import com.mosaicatm.fuser.aggregator.MetaDataManager;
import com.mosaicatm.fuser.filter.MatmFilter;
import com.mosaicatm.fuser.metrics.proxy.MetricAggregatorProxy;
import com.mosaicatm.fuser.metrics.proxy.MetricFuserStoreRegister;
import com.mosaicatm.fuser.metrics.proxy.MetricMatmFilterProxy;
import com.mosaicatm.fuser.metrics.proxy.MetricMetaDataManagerProxy;
import com.mosaicatm.fuser.metrics.proxy.MetricRedisBulkTaskProxy;
import com.mosaicatm.fuser.metrics.proxy.MetricSourceBatchTransformApiProxy;
import com.mosaicatm.fuser.metrics.proxy.MetricSourceMatmTransformApiProxy;
import com.mosaicatm.fuser.metrics.proxy.MetricSourceTransformApiProxy;
import com.mosaicatm.fuser.metrics.proxy.MetricUpdaterProxy;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.redis.RedisBulkTask;
import com.mosaicatm.fuser.transform.api.SourceTransformApi;
import com.mosaicatm.fuser.transform.batch.SourceBatchTransformApi;
import com.mosaicatm.fuser.transform.matm.SourceMatmTransformApi;
import com.mosaicatm.fuser.updaters.Updater;
import com.mosaicatm.lib.messaging.GenericReceiver;
import com.mosaicatm.lib.messaging.Receiver;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.performancemonitor.common.metric.MetricProxyFactoryImpl;
import com.mosaicatm.performancemonitor.common.metric.MetricReceiverProxy;

public class MetricProxyFactory<T> extends MetricProxyFactoryImpl<T>
{   
    public Updater<T, T> proxyUpdater(Updater<T, T> updater)
    {
        if(metricsActive && metricRegistry != null)
        {   
            MetricUpdaterProxy<T,T> proxy = new MetricUpdaterProxy<>(metricRegistry, updater);
            return proxy;
        }
        else
        {
            return updater;
        }
    }
    
    public Aggregator<T> proxyAggregator(Aggregator<T> aggregator)
    {
        if(metricsActive && metricRegistry != null)
        {
            
            MetricAggregatorProxy<T> proxy = new MetricAggregatorProxy<>(metricRegistry, aggregator);
            return proxy;
        }
        else
        {
            return aggregator;
        }
    }
    
    public <M extends MatmObject> MetaDataManager<M> proxyMetaDataManager(MetaDataManager<M> manager)
    {
        if(metricsActive && metricRegistry != null)
        {   
            MetricMetaDataManagerProxy<M> proxy = new MetricMetaDataManagerProxy<>(metricRegistry, manager);
            return proxy;
        }
        else
        {
            return manager;
        }
    }
    
    public <E,K> SourceMatmTransformApi<E, K> proxySourceMatmTransformApi(SourceMatmTransformApi<E,K> api)
    {
        if(metricsActive && metricRegistry != null)
        {
            MetricSourceMatmTransformApiProxy<E,K> proxy = new MetricSourceMatmTransformApiProxy<>(metricRegistry, api);
            return proxy;
        }
        else
        {
            return api;
        }
    }
    
    public <E,K> SourceMatmTransformApi<E, K> proxySourceMatmTransformApi(SourceMatmTransformApi<E,K> api, String appendName)
    {
        if(metricsActive && metricRegistry != null)
        {
            MetricSourceMatmTransformApiProxy<E,K> proxy = new MetricSourceMatmTransformApiProxy<>(metricRegistry, api, appendName);
            return proxy;
        }
        else
        {
            return api;
        }
    }
    
    public <E,K> SourceBatchTransformApi<E, K> proxySourceBatchTransformApi(SourceBatchTransformApi<E,K> api)
    {
        if(metricsActive && metricRegistry != null)
        {
            MetricSourceBatchTransformApiProxy<E,K> proxy = new MetricSourceBatchTransformApiProxy<>(metricRegistry, api);
            return proxy;
        }
        else
        {
            return api;
        }
    }
    
    public <E,F> SourceTransformApi<E,F> proxySourceTransformApi(SourceTransformApi<E,F> api)
    {
        if(metricsActive && metricRegistry != null)
        {
            MetricSourceTransformApiProxy<E,F> proxy = new MetricSourceTransformApiProxy<>(metricRegistry, api);
            return proxy;
        }
        else
        {
            return api;
        }
    }
    
    public <E,F> SourceTransformApi<E,F> proxySourceTransformApi(SourceTransformApi<E,F> api, String appendName)
    {
        if(metricsActive && metricRegistry != null)
        {
            MetricSourceTransformApiProxy<E,F> proxy = new MetricSourceTransformApiProxy<>(metricRegistry, api, appendName);
            return proxy;
        }
        else
        {
            return api;
        }
    }

    public MatmFilter<?> proxyFilter(MatmFilter<?> filter)
    {
        if(metricsActive && metricRegistry != null)
        {
            MetricMatmFilterProxy<?> proxy = new MetricMatmFilterProxy<>(metricRegistry, filter);
            return proxy;
        }
        else
        {
            return filter;
        }
    }
    
    public Receiver<?> proxyReceiver(GenericReceiver<?> receiver, String appendName)
    {
        if(metricsActive && metricRegistry != null)
        {
            MetricReceiverProxy<?> proxy = new MetricReceiverProxy<>(metricRegistry, receiver, appendName);
            return proxy;
        }
        else
        {
            return receiver;
        }
    }
    
    public RedisBulkTask proxyRedisBulkTask(RedisBulkTask task)
    {
        return proxyRedisBulkTask(task, null);
    }
    
    public RedisBulkTask proxyRedisBulkTask(RedisBulkTask task, String id)
    {
        if(metricsActive && metricRegistry != null)
        {
            MetricRedisBulkTaskProxy proxy = new MetricRedisBulkTaskProxy(metricRegistry, task, id);
            return proxy;
        }
        else
        {
            return task;
        }
    }
    
    public FuserStore<?, ?> registerFuserStore(FuserStore<?, ?> fuserStore)
    {
        return registerFuserStore(fuserStore, null);
    }
    
    public FuserStore<?, ?> registerFuserStore(FuserStore<?, ?> fuserStore, String name)
    {
        if (metricsActive && metricRegistry != null)
        {
            new MetricFuserStoreRegister(metricRegistry, fuserStore, name);
        }
        
        return fuserStore;
    }

    public MetricRegistry getMetricRegistry()
    {
        return metricRegistry;
    }

    public boolean isMetricsActive()
    {
        return metricsActive;
    }
}
