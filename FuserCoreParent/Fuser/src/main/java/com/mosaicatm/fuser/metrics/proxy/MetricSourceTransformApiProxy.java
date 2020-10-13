package com.mosaicatm.fuser.metrics.proxy;

import java.util.List;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.mosaicatm.fuser.transform.api.SourceTransformApi;

public class MetricSourceTransformApiProxy<E,F>
		implements SourceTransformApi<E,F>
{

    private Timer fromXmlTimer;
    private Timer toMatmTimer;
    private SourceTransformApi<E,F> transformApi;
    
    public MetricSourceTransformApiProxy(MetricRegistry metricRegistry, SourceTransformApi<E,F> api)
    {
        this( metricRegistry, api, "" );
    }
    
    public MetricSourceTransformApiProxy(MetricRegistry metricRegistry, SourceTransformApi<E,F> api, String appendName)
    {
        if (metricRegistry != null && api != null)
        {
            this.fromXmlTimer = metricRegistry.timer(api.getClass().getName()
                                    + "." + appendName + ".fromXml");
            
            this.toMatmTimer = metricRegistry.timer(api.getClass().getName()
                                    + "." + appendName + ".toMatm");
        }
        this.transformApi = api;
    }

    @Override
    public List<F> toMatm(E flight)
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

}
