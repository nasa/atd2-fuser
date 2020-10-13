package com.mosaicatm.fuser.metrics.proxy;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.mosaicatm.fuser.store.FuserStore;

public class MetricFuserStoreRegister
{
    private FuserStore<?, ?> store;
    
    public MetricFuserStoreRegister(MetricRegistry metricRegistry,
        FuserStore<?, ?> fuserStore, String name)
    {
        this.store = fuserStore;
        if (metricRegistry != null && store != null)
        {
            String registryName = "fuserStore.size";
            
            if(name != null && !name.trim().isEmpty())
                registryName = name + "." + registryName;
            
            metricRegistry.register(registryName,
                new Gauge<Integer>()
            {
                    @Override
                    public Integer getValue()
                    {
                        return store.size();
                    }
                
            });
        }
    }
}
