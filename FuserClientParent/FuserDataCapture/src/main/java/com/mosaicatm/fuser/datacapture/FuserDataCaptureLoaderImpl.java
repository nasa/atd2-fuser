package com.mosaicatm.fuser.datacapture;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mosaicatm.fuser.client.api.FuserClientApi;
import com.mosaicatm.fuser.datacapture.db.ViewInitializer;
import com.mosaicatm.fuser.datacapture.db.dao.MatmAircraftSummaryMapperWrapper;
import com.mosaicatm.fuser.datacapture.db.dao.MatmFlightSummaryMapperWrapper;
import com.mosaicatm.fuser.datacapture.handle.CaptureHandler;
import com.mosaicatm.fuser.datacapture.store.IdStore;
import com.mosaicatm.lib.database.liquibase.LiquibaseInitializer;
import com.mosaicatm.lib.messaging.MessageProducer;
import com.mosaicatm.lib.playback.Playback;
import com.mosaicatm.lib.spring.RuntimeConfigurer;
import com.mosaicatm.lib.spring.RuntimeProperties;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.util.concurrent.Handler;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.position.MatmPositionUpdate;

public class FuserDataCaptureLoaderImpl
implements FuserDataCaptureLoader<MatmPositionUpdate>
{
    private final Log log = LogFactory.getLog(getClass());

    private FuserDataCaptureApi<MatmPositionUpdate> api;
    private FuserClientApi<MatmFlight> fuserClientApi;
    private FuserClientApi<MatmAircraft> aircraftFuserClientApi;
    private Playback playback;

    @Override
    public void load()
    {        
        ApplicationContext ctx = new ClassPathXmlApplicationContext("config/fuser-data-capture/main-context.xml");

        fuserClientApi = getBean("api.FuserClientApi", ctx);
        aircraftFuserClientApi = getBean("api.AircraftFuserClientApi", ctx);

        api = new FuserDataCaptureApiImpl();
        ((FuserDataCaptureApiImpl)api).setContext(ctx);
        ((FuserDataCaptureApiImpl)api).setFuserClientApi(fuserClientApi);
        ((FuserDataCaptureApiImpl)api).setAircraftFuserClientApi(aircraftFuserClientApi);

        playback = getBean( "playback.FuserDataPlayback", ctx );

        RuntimeConfigurer configurer = getBean("fuser-data-capture.propertyConfigurer", ctx);
        Properties props = configurer.getProperties();
        
        if(props != null)
            props.list(System.out);

        CaptureHandler<MatmFlight> captureHandler = getBean("api.CaptureHandler", ctx);

        if (captureHandler != null)
        {
            if (Boolean.valueOf(props.getProperty("fuser-capture.capture.database.enabled")))
            {
                Handler<DataWrapper<MatmFlight>> handler = getBean("database.MatmFlightDatabaseBatchCaptureHandler", ctx);

                if (handler != null)
                    captureHandler.addHandler(handler);
            }

            if (Boolean.valueOf(props.getProperty("fuser-capture.capture.xml.enabled")))
            {
                Handler<DataWrapper<MatmFlight>> handler = getBean("xml.XmlCaptureHandler", ctx);

                if (handler != null)
                    captureHandler.addHandler(handler);
            }

            if (Boolean.valueOf(props.getProperty("fuser-capture.capture.csv.enabled")))
            {
                Handler<DataWrapper<MatmFlight>> handler = getBean("csv.CsvCaptureHandler", ctx);

                if (handler != null)
                    captureHandler.addHandler(handler);
            }
        }

        CaptureHandler<MatmAircraft> aircraftCaptureHandler = getBean("api.AircraftCaptureHandler", ctx);

        if (aircraftCaptureHandler != null)
        {
            if (Boolean.valueOf(props.getProperty("fuser-capture.capture.database.enabled")))
            {
                Handler<DataWrapper<MatmAircraft>> handler = getBean("database.MatmAircraftDatabaseBatchCaptureHandler", ctx);

                if (handler != null)
                    aircraftCaptureHandler.addHandler(handler);
            }

            if (Boolean.valueOf(props.getProperty("fuser-capture.capture.xml.enabled")))
            {
                Handler<DataWrapper<MatmAircraft>> handler = getBean("xml.XmlAircraftCaptureHandler", ctx);

                if (handler != null)
                    aircraftCaptureHandler.addHandler(handler);
            }

            /*if (Boolean.valueOf(props.getProperty("fuser-capture.capture.csv.enabled")))
            {
                Handler<DataWrapper<MatmAircraft>> handler = getBean("csv.CsvCaptureHandler", ctx);

                if (handler != null)
                    aircraftCaptureHandler.addHandler(handler);
            }*/
        }

        CaptureHandler<MatmFlight> captureAllHandler = getBean("api.CaptureAllHandler", ctx);

        if (captureAllHandler != null)
        {
            if (Boolean.valueOf(props.getProperty("fuser-capture.capture.database.all.enabled")))
            {
                Handler<DataWrapper<MatmFlight>> handler = getBean("database.MatmFlightAllDatabaseBatchCaptureHandler", ctx);

                if (handler != null)
                    captureAllHandler.addHandler(handler);
            }

            if (Boolean.valueOf(props.getProperty("fuser-capture.capture.xml.all.enabled")))
            {
                Handler<DataWrapper<MatmFlight>> handler = getBean("xml.XmlCaptureAllHandler", ctx);

                if (handler != null)
                    captureAllHandler.addHandler(handler);
            }

            if (Boolean.valueOf(props.getProperty("fuser-capture.capture.csv.all.enabled")))
            {
                Handler<DataWrapper<MatmFlight>> handler = getBean("csv.CsvCaptureAllHandler", ctx);

                if (handler != null)
                    captureAllHandler.addHandler(handler);
            }
        }

        CaptureHandler<MatmAircraft> aircraftCaptureAllHandler = getBean("api.AircraftCaptureAllHandler", ctx);

        if (aircraftCaptureAllHandler != null)
        {
            if (Boolean.valueOf(props.getProperty("fuser-capture.capture.database.all.enabled")))
            {
                Handler<DataWrapper<MatmAircraft>> handler = getBean("database.MatmAircraftAllDatabaseBatchCaptureHandler", ctx);

                if (handler != null)
                    aircraftCaptureAllHandler.addHandler(handler);
            }

            if (Boolean.valueOf(props.getProperty("fuser-capture.capture.xml.all.enabled")))
            {
                Handler<DataWrapper<MatmAircraft>> handler = getBean("xml.XmlAircraftCaptureAllHandler", ctx);

                if (handler != null)
                    aircraftCaptureAllHandler.addHandler(handler);
            }

            /*if (Boolean.valueOf(props.getProperty("fuser-capture.capture.csv.all.enabled")))
            {
                Handler<DataWrapper<MatmAircraft>> handler = getBean("csv.CsvCaptureAllHandler", ctx);

                if (handler != null)
                    aircraftCaptureAllHandler.addHandler(handler);
            }*/
        }

        CaptureHandler<MatmFlight> removedHandler = getBean( "api.RemovedHandler", ctx );

        if( removedHandler != null )
        {
            if( Boolean.valueOf( props.getProperty( "fuser-capture.capture.database.removed.enabled" ) ) )
            {
                Handler<DataWrapper<MatmFlight>> handler = getBean( "database.MatmRemovedDatabaseBatchCaptureHandler", ctx );

                if( handler != null )
                {
                    removedHandler.addHandler( handler );
                }
            }
        }

        CaptureHandler<MatmAircraft> aircraftRemovedHandler = getBean("api.AircraftRemovedHandler", ctx);

        if( aircraftRemovedHandler != null )
        {
            if( Boolean.valueOf( props.getProperty( "fuser-capture.capture.database.removed.enabled" ) ) )
            {
                Handler<DataWrapper<MatmAircraft>> handler = getBean( "database.MatmAircraftRemovedDatabaseBatchCaptureHandler", ctx );

                if( handler != null )
                {
                    aircraftRemovedHandler.addHandler( handler );
                }
            }
        }

        if (ctx.containsBean("routes.MessageProducer"))
        {
            MessageProducer producer = ctx.getBean("routes.MessageProducer", MessageProducer.class);
            ((FuserDataCaptureApiImpl)api).setProducer(producer);

            if (props.containsKey("fuser-capture.position.uri.matmPositionUpdate.process"))
            {
                String matmPositionUpdateUrl = String.valueOf(props.get("fuser-capture.position.uri.matmPositionUpdate.process"));
                ((FuserDataCaptureApiImpl)api).setMatmPositionUpdateUrl(matmPositionUpdateUrl);
            }
        }

        if (ctx.containsBean("api.FuserDataReceivingListener"))
        {
            FuserDataReceivingListener<MatmFlight> listener = getBean("api.FuserDataReceivingListener", ctx);

            if (ctx.containsBean("core.IdStore"))
            {
                IdStore idStore = getBean("core.IdStore", ctx);
                listener.setIdStore(idStore);
            }
        }

        if (ctx.containsBean("api.AircraftFuserDataReceivingListener"))
        {
            FuserDataReceivingListener<MatmAircraft> listener = getBean("api.AircraftFuserDataReceivingListener", ctx);

            if (ctx.containsBean("core.AircraftIdStore"))
            {
                IdStore idStore = getBean("core.AircraftIdStore", ctx);
                listener.setIdStore(idStore);
            }
        }

        if (ctx.containsBean("core.Clock"))
        {
            Clock clock = ctx.getBean("core.Clock", Clock.class);
            ((FuserDataCaptureApiImpl)api).setClock(clock);
        }

        if (ctx.containsBean("db.LiquibaseInitializer"))
        {
            LiquibaseInitializer liquibaseInitializer = ctx.getBean("db.LiquibaseInitializer", LiquibaseInitializer.class);

            liquibaseInitializer.start();
        }

        if (ctx.containsBean("db.matm-all.viewInitializer"))
        {
            ViewInitializer viewInitializer = ctx.getBean("db.matm-all.viewInitializer", ViewInitializer.class);
            viewInitializer.initViews();
        }

        if (ctx.containsBean("db.viewInitializer"))
        {
            ViewInitializer viewInitializer = ctx.getBean("db.viewInitializer", ViewInitializer.class);
            viewInitializer.initViews();
        }

        if (ctx.containsBean("db.matm-all.MatmFlightSummaryMapper"))
        {
            MatmFlightSummaryMapperWrapper summaryMapper =
                            getBean("db.matm-all.MatmFlightSummaryMapper", ctx);
            summaryMapper.initViews();
        }

        if (ctx.containsBean("db.matm-all.MatmAircraftSummaryMapper"))
        {
            MatmAircraftSummaryMapperWrapper summaryMapper =
                            getBean("db.matm-all.MatmAircraftSummaryMapper", ctx);
            summaryMapper.initViews();
        }

        log.info("Finished contexts loading and ready to start.");
    }

    @Override
    public void loadRuntimeProperties (String[] args)
    {
        try
        {
            if (args.length > 0)
            {
                File file = null;
                RuntimeProperties properties = null;
                RuntimeProperties allProperties = null;
                for (String runtimeProps : args)
                {
                    file = new File(runtimeProps);
                    if (file.isFile())
                    {
                        log.info("propertiesLocation = " + runtimeProps);
                        properties = RuntimeProperties.findProperties(runtimeProps);
                    }
                    else
                    {
                        log.info("propertiesLocation = " + runtimeProps);
                        properties = RuntimeProperties.findProperties(runtimeProps, "env/fuser-data-capture/");
                    }

                    if (allProperties == null)
                    {
                        allProperties = properties;
                    }
                    else if (properties != null)
                    {
                        allProperties.putAll(properties);
                    }

                }

                if (allProperties != null)
                {
                    RuntimeConfigurer.setRuntimeProperties(allProperties);
                    RuntimeConfigurer.getRuntimeProperties().setTransitive(true);
                    allProperties.list(System.out);
                }
            }
        }
        catch (IOException ioe)
        {
            log.error("Error loading runtime properties", ioe);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getBean (String beanId, ApplicationContext context)
    {
        T bean = null;

        if (beanId != null && !beanId.trim().isEmpty())
        {
            if (context.containsBean(beanId))
                bean = (T)context.getBean(beanId);
        }

        return bean;
    }

    @Override
    public FuserDataCaptureApi<MatmPositionUpdate> getFuserDataCaptureApi()
    {
        return api;
    }

    @Override
    public FuserClientApi<MatmFlight> getFuserClientApi()
    {
        return fuserClientApi;
    }

    public void setFuserClientApi(FuserClientApi<MatmFlight> fuserClientApi)
    {
        this.fuserClientApi = fuserClientApi;
    }

    @Override
    public FuserClientApi<MatmAircraft> getAircraftFuserClientApi()
    {
        return aircraftFuserClientApi;
    }

    public void setAircraftFuserClientApi(FuserClientApi<MatmAircraft> aircraftFuserClientApi)
    {
        this.aircraftFuserClientApi = aircraftFuserClientApi;
    }
    
    @Override
    public Playback getPlayback()
    {
        return playback;
    }

    @Override
    public void setFuserDataCaptureApiConfiguration(FuserDataCaptureConfiguration config)
    {
        if (config.getMatmPositionJmsUrl() != null)
        {
            System.setProperty("fuser-capture.position.jms.url", config.getMatmPositionJmsUrl());
        }
        if (config.getMatmPositionUpdateUrl() != null)
        {
            System.setProperty("fuser-capture.position.uri.matmPositionUpdate.source", config.getMatmPositionUpdateUrl());
        }
        if (config.getMatmPositionUpdateEnvelopeUrl() != null)
        {
            System.setProperty("fuser-capture.position.uri.matmPositionUpdateEnvelope.source", config.getMatmPositionUpdateEnvelopeUrl());
        }
        if (config.getClockSyncEnabled() != null)
        {
            System.setProperty("fuser-client-api.clocksync.active", String.valueOf(config.getClockSyncEnabled()));
            System.setProperty("fuser-capture.position.clocksync.enabled", String.valueOf(config.getClockSyncEnabled()));
        }
        if (config.getPositionEnable() != null)
        {
            System.setProperty("fuser-capture.capture.database.position.enabled", String.valueOf(config.getPositionEnable()));
        }
        if (config.getMatmPositionEnable() != null)
        {
            System.setProperty("fuser-capture.capture.database.matm.position.enabled", String.valueOf(config.getMatmPositionEnable()));
        }
        if (config.getMatmFlightAirportsOfInterest() != null)
        {
            System.setProperty("fuser-client-api.airportsOfInterest", config.getMatmFlightAirportsOfInterest());
        }
        if (config.getMatmPositionUpdateAirportsOfInterest() != null)
        {
            System.setProperty("fuser-capture.position.filter.airportsOfInterest", config.getMatmPositionUpdateAirportsOfInterest());
        }
        if (config.getMatmFlightAirportFilterActive() != null)
        {
            System.setProperty("fuser-client-api.filter.airport.active", String.valueOf(config.getMatmFlightAirportFilterActive()));
        }
        if (config.getMatmPositionUpdateAirportFilterActive() != null)
        {
            System.setProperty("fuser-capture.position.filter.airport.active", String.valueOf(config.getMatmPositionUpdateAirportFilterActive()));
        }
        if (config.getSurfaceAirportFilterActive() != null)
        {
            System.setProperty("fuser-client-api.filter.asdex.airport.active", String.valueOf(config.getSurfaceAirportFilterActive()));
        }
        if (config.getDbUrl() != null)
        {
            System.setProperty("fuser-capture.database.url", config.getDbUrl());
        }
        if (config.getDbUsername() != null)
        {
            System.setProperty("fuser-capture.database.username", config.getDbUsername());
        }
        if (config.getDbPassword() != null)
        {
            System.setProperty("fuser-capture.database.password", config.getDbPassword());
        }
        if (config.getDropTables() != null)
        {
            System.setProperty("fuser-capture.database.dropTables", String.valueOf(config.getDropTables()));
        }
        if (config.getCaptureMatmFlightCsv() != null)
        {
            System.setProperty("fuser-capture.capture.csv.enabled", String.valueOf(config.getCaptureMatmFlightCsv()));
        }
        if (config.getCaptureMatmFlightAllCsv() != null)
        {
            System.setProperty("fuser-capture.capture.csv.all.enabled", String.valueOf(config.getCaptureMatmFlightAllCsv()));
        }
        if (config.getCaptureMatmFlightXml() != null)
        {
            System.setProperty("fuser-capture.capture.xml.enabled", String.valueOf(config.getCaptureMatmFlightXml()));
        }
        if (config.getCaptureMatmFlightAllXml() != null)
        {
            System.setProperty("fuser-capture.capture.xml.all.enabled", String.valueOf(config.getCaptureMatmFlightAllXml()));
        }
        if (config.getSyncMatmFlight() != null)
        {
            System.setProperty("fuser-client-api.service.sync.enabled", String.valueOf(config.getSyncMatmFlight()));
        }
        if (config.getSyncMatmAircraft() != null)
        {
            System.setProperty("fuser-client-api.service.sync.aircraft.enabled", String.valueOf(config.getSyncMatmAircraft()));
        }

    }
}
