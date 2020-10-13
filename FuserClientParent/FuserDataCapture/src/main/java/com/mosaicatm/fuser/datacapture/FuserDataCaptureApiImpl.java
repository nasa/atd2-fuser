package com.mosaicatm.fuser.datacapture;

import org.apache.camel.CamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.mosaicatm.fuser.client.api.FuserClientApi;
import com.mosaicatm.fuser.client.api.FuserClientApiConfiguration;
import com.mosaicatm.lib.messaging.MessageProducer;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.util.concurrent.SyncPoint;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.position.MatmPositionUpdate;
import com.mosaicatm.performancemonitor.common.MetricsReporterFactory;

public class FuserDataCaptureApiImpl implements FuserDataCaptureApi<MatmPositionUpdate>
{
    private final Log log = LogFactory.getLog(getClass());
    private MessageProducer producer;
    private ApplicationContext context;
    private FuserClientApi<MatmFlight> fuserClientApi;
    private FuserClientApi<MatmAircraft> aircraftFuserClientApi;
    private String matmPositionUpdateUrl;
    private Clock clock;

    @Override
    public void publishToProcess(MatmPositionUpdate data)
    {
        if (producer != null)
            producer.publish(matmPositionUpdateUrl, data);

    }

    @Override
    public void start() {

        if(context.containsBean("routes.core.CamelContext"))
        {
            CamelContext camel = (CamelContext)context.getBean("routes.core.CamelContext");
            try
            {
                camel.getRouteController().startAllRoutes();
                log.info("Routes sucessfully started");
            } catch (Exception e) {
                log.error("Error trying to start routes." ,e);
            }
        }
        
        if(context.containsBean("core.MetricsReporter"))
        {
            MetricsReporterFactory metricReporter = (MetricsReporterFactory)context.getBean("core.MetricsReporter");
            metricReporter.start();
        }

        Boolean sync = null;
        Boolean aircraftSync = null;
        if (context.containsBean("api.FuserClientApiConfiguration"))
        {

            FuserClientApiConfiguration fuserConfig = context.getBean("api.FuserClientApiConfiguration", FuserClientApiConfiguration.class);
            sync = fuserConfig.getSyncEnabled();
            aircraftSync = fuserConfig.getAircraftSyncEnabled();

        }

        if(fuserClientApi != null){
            if (sync != null)
            {
                fuserClientApi.start( sync.booleanValue() );
            }
            else
            {
                fuserClientApi.start( true );
            }
        }

        if(aircraftFuserClientApi != null){
            if (aircraftSync != null)
            {
                aircraftFuserClientApi.start(aircraftSync.booleanValue());
            }
            else
            {
                aircraftFuserClientApi.start(true);
            }
        }

        SyncPoint lock = context.getBean("core.SyncPoint", SyncPoint.class);
        lock.unlock();

        log.info("Api started...");

    }

    public ApplicationContext getContext()
    {
        return context;
    }

    public void setContext(ApplicationContext context)
    {
        this.context = context;
    }

    public FuserClientApi<MatmFlight> getFuserClientApi()
    {
        return fuserClientApi;
    }

    public void setFuserClientApi(FuserClientApi<MatmFlight> fuserClientApi)
    {
        this.fuserClientApi = fuserClientApi;
    }

    public FuserClientApi<MatmAircraft> getAircraftFuserClientApi()
    {
        return aircraftFuserClientApi;
    }

    public void setAircraftFuserClientApi(FuserClientApi<MatmAircraft> aircraftFuserClientApi)
    {
        this.aircraftFuserClientApi = aircraftFuserClientApi;
    }

    public MessageProducer getProducer()
    {
        return producer;
    }

    public void setProducer(MessageProducer producer)
    {
        this.producer = producer;
    }

    public String getMatmPositionUpdateUrl()
    {
        return matmPositionUpdateUrl;
    }

    public void setMatmPositionUpdateUrl(String matmPositionUpdateUrl)
    {
        this.matmPositionUpdateUrl = matmPositionUpdateUrl;
    }

    public Clock getClock()
    {
        return clock;
    }

    public void setClock(Clock clock)
    {
        this.clock = clock;
    }

}
