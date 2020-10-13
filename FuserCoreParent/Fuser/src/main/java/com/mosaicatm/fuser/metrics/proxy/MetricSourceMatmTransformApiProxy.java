package com.mosaicatm.fuser.metrics.proxy;

import java.util.List;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.mosaicatm.fuser.transform.matm.SourceMatmTransformApi;

public class MetricSourceMatmTransformApiProxy<E,K>
implements SourceMatmTransformApi<E,K>
{

    private Timer fromXmlTimer;
    private Timer toMatmTimer;
    private Timer toXmlTimer;
    private SourceMatmTransformApi<E,K> transformApi;
    
    public MetricSourceMatmTransformApiProxy(MetricRegistry metricRegistry, SourceMatmTransformApi<E,K> api)
    {
        if (metricRegistry != null && api != null)
        {
            this.fromXmlTimer = metricRegistry.timer(api.getClass().getName()
                                + ".fromXml");
            
            this.toMatmTimer = metricRegistry.timer(api.getClass().getName()
                                + ".toMatm");
            
            this.toXmlTimer = metricRegistry.timer(api.getClass().getName()
                                + ".toXml");
        }
        this.transformApi = api;
    }
    
    public MetricSourceMatmTransformApiProxy(MetricRegistry metricRegistry, SourceMatmTransformApi<E,K> api, String appendName)
    {
        if (metricRegistry != null)
        {
            this.fromXmlTimer = metricRegistry.timer(api.getClass().getName()
                                    + "." + appendName + ".fromXml");
            
            this.toMatmTimer = metricRegistry.timer(api.getClass().getName()
                                    + "." + appendName + ".toMatm");
        }
        this.transformApi = api;
    }
    
    @Override
    public E fromMatm(K matm)
    {
        return transformApi.fromMatm(matm);
    }

    @Override
    public K toMatm(E flight)
    {
        Timer.Context timerContext = null;
        try
        {
            if (toMatmTimer != null)
                timerContext = toMatmTimer.time();
            return transformApi.toMatm(flight);
        }
        finally
        {
            if (timerContext != null)
                timerContext.close();
        }
    }

    @Override
    public E fromMatm(K matm, String airport)
    {
        return transformApi.fromMatm(matm, airport);
    }
    
    @Override
    public E fromMatm(K matm, List<String> airports)
    {
        return transformApi.fromMatm(matm, airports);
    }

    @Override
    public K toMatm(E flight, String airport)
    {
        return transformApi.toMatm(flight, airport);
    }

    @Override
    public E fromMatm(K oldMatm, K matm, String airport)
    {
        return transformApi.fromMatm(oldMatm, matm, airport);
    }
    
    @Override
    public E fromMatm(K oldMatm, K matm, List<String> airports)
    {
        return transformApi.fromMatm(oldMatm, matm, airports);
    }

    @Override
    public E fromXml(String xml)
    {
        Timer.Context timerContext = null;
        try
        {
            if (fromXmlTimer != null)
                timerContext = fromXmlTimer.time();
            return transformApi.fromXml(xml);
        }
        finally
        {
            if (timerContext != null)
                timerContext.close();
        }
    }

    @Override
    public String toXml(E flight)
    {
        Timer.Context timerContext = null;
        try
        {
            if (toXmlTimer != null)
                timerContext = toXmlTimer.time();
            return transformApi.toXml(flight);
        }
        finally
        {
            if (timerContext != null)
                timerContext.close();
        }
    }

}
