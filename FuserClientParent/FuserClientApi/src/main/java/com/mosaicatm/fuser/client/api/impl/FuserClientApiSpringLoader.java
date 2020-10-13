package com.mosaicatm.fuser.client.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mosaicatm.fuser.client.api.FuserClientApi;
import com.mosaicatm.fuser.client.api.FuserClientApiConfiguration;
import com.mosaicatm.fuser.client.api.FuserClientApiLoader;
import com.mosaicatm.fuser.client.api.data.DataAdder;
import com.mosaicatm.fuser.client.api.data.DataRemover;
import com.mosaicatm.fuser.client.api.data.DataUpdater;
import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventManager;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventManager;
import com.mosaicatm.fuser.client.api.impl.data.GenericFuserUpdateHandler;
import com.mosaicatm.fuser.client.api.impl.event.GenericFuserSyncCompleteEventManager;
import com.mosaicatm.fuser.client.api.impl.filter.AirportFilter;
import com.mosaicatm.fuser.client.api.impl.filter.SurfaceAirportFilter;
import com.mosaicatm.fuser.client.api.util.SyncPoint;
import com.mosaicatm.fuser.services.client.FuserSyncRequest;
import com.mosaicatm.fuser.services.client.FuserSyncService;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.lib.messaging.MessageProducer;
import com.mosaicatm.lib.spring.RuntimeConfigurer;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.util.concurrent.Handler;
import com.mosaicatm.lib.util.filter.LogicalFilter;
import com.mosaicatm.lib.util.filter.LogicalOrFilter;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class FuserClientApiSpringLoader
implements FuserClientApiLoader
{
    private final Log log = LogFactory.getLog(getClass());

    private boolean loadJms = true;
    private boolean loadRoutes = true;
    private boolean loadServicesClients = true;
    private boolean loadAircraft = true;
    private boolean loadSectorAssignments = false;
    private boolean loadSecureSync = false;

    private FuserClientApi<MatmFlight> flightApi;
    private FuserClientApi<MatmAircraft> aircraftApi;
    private FuserClientApi<MatmSectorAssignment> sectorAssignmentApi;

    @Override
    public void load() 
    {	
        flightApi = new FuserMatmFlightClientApi();
        aircraftApi = new FuserMatmAircraftClientApi();
        sectorAssignmentApi = new FuserMatmSectorAssignmentClientApi();

        List<String> contexts = getContexts ();		
        ApplicationContext ctx = new ClassPathXmlApplicationContext(contexts.toArray(new String[]{}));
        RuntimeConfigurer configurer = getBean(ctx, "fuser-client-api.propertyConfigurer");
        configurer.getProperties().list(System.out);

        log.info("Loading Fuser client flight api....");
        initializeApi (flightApi, ctx);

        log.info("Loading Fuser client aircraft api...");
        initializeAircraftApi (aircraftApi, ctx);

        log.info("Loading Fuser client sector assignment api...");
        initializeSectorAssignmentApi (sectorAssignmentApi, ctx);        
        
        log.info("Fuser client apis loaded, waiting for the start method to be called");
    }
    
    @Override
    public void setApiConfiguration(FuserClientApiConfiguration configuration)
    {
        if(configuration.getJmsUrl() != null)
        {
            System.setProperty("fuser-client-api.jms.url", configuration.getJmsUrl());
        }
        if(configuration.getJmsUsername() != null)
        {
            System.setProperty("fuser-client-api.jms.username", configuration.getJmsUsername());
        }
        if(configuration.getJmsPassword() != null)
        {
            System.setProperty("fuser-client-api.jms.password", configuration.getJmsPassword());
        }
        if(configuration.getMatmTransferEnvelopeUri() != null)
        {
            System.setProperty("fuser-client-api.uri.matmTransferEnvelope.source", configuration.getMatmTransferEnvelopeUri());
        }
        if(configuration.getMatmFlightUri() != null)
        {
            System.setProperty("fuser-client-api.uri.matmFlight.source", configuration.getMatmFlightUri());
        }
        if(configuration.getMatmAircraftUri() != null)
        {
            System.setProperty("fuser-client-api.uri.matmAircraft.source",  configuration.getMatmAircraftUri());
        }
        if(configuration.getMatmAircraftRemoveUri() != null)
        {
            System.setProperty("fuser-client-api.uri.matmAircraft.remove.source", configuration.getMatmAircraftRemoveUri());
        }
        if(configuration.getMatmAircraftBatchUri() != null)
        {
            System.setProperty("fuser-client-api.uri.matmAircraft.batch.source",  configuration.getMatmAircraftBatchUri());
        }
        if(configuration.getMatmSectorAssignmentBatchUri() != null)
        {
            System.setProperty("fuser-client-api.uri.matmSectorAssignment.batch.source",  configuration.getMatmSectorAssignmentBatchUri());
        }        
        if(configuration.getSyncServiceUrl() != null)
        {
            System.setProperty("fuser-client-api.service.sync.url", configuration.getSyncServiceUrl());
        }
        if(configuration.getSyncServiceOverrideDataSource() != null)
        {
            System.setProperty("fuser-client-api.service.sync.override.datasource", String.valueOf( configuration.getSyncServiceOverrideDataSource() ));
        }        
        if(configuration.getTimedRemoverActive() != null)
        {
            System.setProperty("fuser-client-api.remover.timed.active", String.valueOf(configuration.getTimedRemoverActive()));
        }
        if(configuration.getSyncEnabled() != null)
        {
            System.setProperty("fuser-client-api.service.sync.enabled", String.valueOf(configuration.getSyncEnabled()));
        }
        if(configuration.getAircraftSyncEnabled() != null)
        {
            System.setProperty("fuser-client-api.service.sync.aircraft.enabled", String.valueOf(configuration.getAircraftSyncEnabled()));
        }
        if(configuration.getSectorAssignmentSyncEnabled() != null)
        {
            System.setProperty("fuser-client-api.service.sync.sectorAssignments.enabled", String.valueOf(configuration.getSectorAssignmentSyncEnabled()));
        }        
        if(configuration.getClockSyncEnabled() != null)
        {
            System.setProperty("fuser-client-api.clocksync.active", String.valueOf(configuration.getClockSyncEnabled()));
        }
        if(configuration.getClockSyncUri() != null)
        {
            System.setProperty("fuser-client-api.uri.clock.source", configuration.getClockSyncUri());
        }
        if(configuration.getRemoveAfterHours() != null)
        {
            System.setProperty("fuser-client-api.remover.window.hours", String.valueOf(configuration.getRemoveAfterHours()));
        }
        if(configuration.getProcessRawTrajectoriesEnabled() != null)
        {
            System.setProperty("fuser-client-api.process.trajectory.raw", String.valueOf(configuration.getProcessRawTrajectoriesEnabled()));
        }
        if(configuration.getAirportFilterActive() != null)
        {
            System.setProperty("fuser-client-api.filter.airport.active",String.valueOf(configuration.getAirportFilterActive()));
        }
        if(configuration.getSurfaceAirportFilterActive() != null)
        {
            System.setProperty("fuser-client-api.filter.asdex.airport.active",String.valueOf(configuration.getSurfaceAirportFilterActive()));
        }
        if(configuration.getAirportsOfInterest() != null)
        {
            System.setProperty("fuser-client-api.airportsOfInterest",configuration.getAirportsOfInterest());
        }
        if(configuration.getDisableCNCheck() != null)
        {
            System.setProperty("fuser-client-api.security.disableCNCheck", String.valueOf( configuration.getDisableCNCheck() ));
        }
        if(configuration.getKeystoreLocation() != null)
        {
            System.setProperty("fuser-client-api.keystore.location", configuration.getKeystoreLocation());
        }
        if(configuration.getKeystorePassword() != null)
        {
            System.setProperty("fuser-client-api.keystore.password", configuration.getKeystorePassword());
        }
        if(configuration.getKeystoreType() != null)
        {
            System.setProperty("fuser-client-api.keystore.type", configuration.getKeystoreType());
        }
        if(configuration.getTrustStoreLocation() != null)
        {
            System.setProperty("fuser-client-api.truststore.location", configuration.getTrustStoreLocation());
        }
        if(configuration.getTrustStorePassword() != null)
        {
            System.setProperty("fuser-client-api.truststore.password", configuration.getTrustStorePassword());
        }
        if(configuration.getTrustStoreType() != null)
        {
            System.setProperty("fuser-client-api.truststore.type", configuration.getTrustStoreType());
        }
        if(configuration.getReceiverQueueSize() != null)
        {
            System.setProperty("fuser-client-api.receiver.queue.size", String.valueOf(configuration.getReceiverQueueSize()));
        }
    }

    private void initializeAircraftApi (FuserClientApi<MatmAircraft> clientApi, ApplicationContext ctx)
    {
        if (!isLoadAircraft())
            return;

        if (ctx.containsBean("aircraft.routes.CamelContext"))
        {
            CamelContext context = getBean(ctx, "aircraft.routes.CamelContext");
            if (context != null)
                ((FuserMatmAircraftClientApi)clientApi).setCamelContext(context);
        }

        if (ctx.containsBean("aircraft.core.MatmAircraftForwardingHandler"))
        {
            Handler<MatmAircraft> syncHandler = getBean(ctx, "aircraft.core.MatmAircraftForwardingHandler");
            if (syncHandler != null)
                ((FuserMatmAircraftClientApi)clientApi).setSyncHandler(syncHandler);
        }

        if (ctx.containsBean("core.Clock"))
        {
            Clock clock = getBean (ctx, "core.Clock");
            if (clock != null)
                ((FuserMatmAircraftClientApi)clientApi).setClock(clock);
        }

        if (ctx.containsBean("aircraft.core.AircraftClientStore"))
        {
            FuserClientStore<MatmAircraft> store = getBean (ctx, "aircraft.core.AircraftClientStore");
            if (store != null)
                ((FuserMatmAircraftClientApi)clientApi).setStore(store);
        }

        if (ctx.containsBean("aircraft.updaters.DataAdder"))
        {
            DataAdder<MatmAircraft> adder = getBean (ctx, "aircraft.updaters.DataAdder");
            if (adder != null)
                ((FuserMatmAircraftClientApi)clientApi).setDataAdder(adder);
        }

        if (ctx.containsBean("aircraft.updaters.DataUpdater"))
        {
            DataUpdater<MatmAircraft> updater = getBean (ctx, "aircraft.updaters.DataUpdater");
            if (updater != null)
                ((FuserMatmAircraftClientApi)clientApi).setDataUpdater(updater);
        }

        if (ctx.containsBean("aircraft.updaters.DataRemover"))
        {
            DataRemover<MatmAircraft> remover = getBean (ctx, "aircraft.updaters.DataRemover");
            if (remover != null)
                ((FuserMatmAircraftClientApi)clientApi).setDataRemover(remover);
        }

        if (ctx.containsBean("aircraft.events.ProcessedEventManager"))
        {
            FuserProcessedEventManager<MatmAircraft> manager = getBean(ctx, "aircraft.events.ProcessedEventManager");
            if (manager != null)
                ((FuserMatmAircraftClientApi)clientApi).setProcessedEventManager(manager);
        }

        if (ctx.containsBean("aircraft.events.ReceivedEventManager"))
        {
            FuserReceivedEventManager<MatmAircraft> manager = getBean(ctx, "aircraft.events.ReceivedEventManager");
            if (manager != null)
                ((FuserMatmAircraftClientApi)clientApi).setReceivedEventManager(manager);
        }

        if( ctx.containsBean( "aircraft.events.FuserSyncCompleteEventManager" ) )
        {
            GenericFuserSyncCompleteEventManager manager = getBean(ctx, "aircraft.events.FuserSyncCompleteEventManager");
            if (manager != null)
            {
                ((FuserMatmAircraftClientApi)clientApi).setFuserSyncCompleteEventManager(manager);
            }
        }

        if (ctx.containsBean("aircraft.core.MatmAircraftUpdateHandler"))
        {
            GenericFuserUpdateHandler<MatmAircraft> handler = getBean(ctx, "aircraft.core.MatmAircraftUpdateHandler");
            if (handler != null)
                handler.setFuserClientApi(clientApi);
        }

        if (ctx.containsBean("services.FuserSyncService"))
        {
            FuserSyncService syncService = getBean(ctx, "services.FuserSyncService");
            if(syncService != null)
                ((FuserMatmAircraftClientApi)clientApi).setSyncService(syncService);
        }

        if (ctx.containsBean("aircraft.core.SyncPoint"))
        {
            SyncPoint syncPoint = getBean (ctx, "aircraft.core.SyncPoint");
            if (syncPoint != null)
                ((FuserMatmAircraftClientApi)clientApi).setSyncPoint(syncPoint);
        }

        if (ctx.containsBean("aircraft.routes.MessageProducer"))
        {
            MessageProducer messageProducer = getBean(ctx, "aircraft.routes.MessageProducer");
            if(messageProducer != null)
                ((FuserMatmAircraftClientApi)clientApi).setMessageProducer(messageProducer);
        }

        if( ctx.containsBean("marshalling.MatmTransferEnvelopeMarshaller" ) )
        {
            GenericMarshaller marshaller = getBean( ctx, "marshalling.MatmTransferEnvelopeMarshaller" );
            if( marshaller != null )
            {
                ((FuserMatmAircraftClientApi)clientApi).setMatmFlightEnvMarshaller( marshaller );
            }
        }

        RuntimeConfigurer configurer = getBean(ctx, "fuser-client-api.propertyConfigurer");
        if(configurer.getProperties().containsKey( "fuser-client-api.uri.matmAircraft.publish" ) )
        {
            ((FuserMatmAircraftClientApi)clientApi).setDataPublishEndpoint(
                configurer.getProperties().getProperty("fuser-client-api.uri.matmAircraft.publish"));
        }
        if(configurer.getProperties().containsKey( "fuser-client-api.uri.matmAircraft.batch.publish" ) )
        {
            ((FuserMatmAircraftClientApi)clientApi).setDataBatchPublishEndpoint(
                configurer.getProperties().getProperty("fuser-client-api.uri.matmAircraft.batch.publish"));
        }
        if(configurer.getProperties().containsKey( "fuser-client-api.uri.matmAircraft.remove.publish" ) )
        {
            ((FuserMatmAircraftClientApi)clientApi).setDataPublishRemoveEndpoint(
                configurer.getProperties().getProperty("fuser-client-api.uri.matmAircraft.remove.publish"));
        }

        if(configurer.getProperties().containsKey( "fuser-client-api.service.sync.override.datasource" ) )
        {
            ((FuserMatmAircraftClientApi)clientApi).setSyncServiceOverrideDataSource( 
                configurer.getProperties().getProperty( "fuser-client-api.service.sync.override.datasource"));
        }
        if(configurer.getProperties().containsKey( "fuser-client-api.publish.active" ) )
        {
            ((FuserMatmAircraftClientApi)clientApi).setPublishActive(
                Boolean.parseBoolean( configurer.getProperties().getProperty( "fuser-client-api.publish.active" ) ) );
        }
    }
    
    private void initializeSectorAssignmentApi (FuserClientApi<MatmSectorAssignment> clientApi, ApplicationContext ctx)
    {
        if (!isLoadSectorAssignments())
            return;

        if (ctx.containsBean("sector.routes.CamelContext"))
        {
            CamelContext context = getBean(ctx, "sector.routes.CamelContext");
            if (context != null)
                ((FuserMatmSectorAssignmentClientApi)clientApi).setCamelContext(context);
        }

        if (ctx.containsBean("sector.core.MatmSectorAssignmentForwardingHandler"))
        {
            Handler<MatmSectorAssignment> syncHandler = getBean(ctx, "sector.core.MatmSectorAssignmentForwardingHandler");
            if (syncHandler != null)
                ((FuserMatmSectorAssignmentClientApi)clientApi).setSyncHandler(syncHandler);
        }

        if (ctx.containsBean("core.Clock"))
        {
            Clock clock = getBean (ctx, "core.Clock");
            if (clock != null)
                ((FuserMatmSectorAssignmentClientApi)clientApi).setClock(clock);
        }

        if (ctx.containsBean("sector.core.SectorAssignmentClientStore"))
        {
            FuserClientStore<MatmSectorAssignment> store = getBean (ctx, "sector.core.SectorAssignmentClientStore");
            if (store != null)
                ((FuserMatmSectorAssignmentClientApi)clientApi).setStore(store);
        }

        if (ctx.containsBean("sector.updaters.DataAdder"))
        {
            DataAdder<MatmSectorAssignment> adder = getBean (ctx, "sector.updaters.DataAdder");
            if (adder != null)
                ((FuserMatmSectorAssignmentClientApi)clientApi).setDataAdder(adder);
        }

        if (ctx.containsBean("sector.updaters.DataUpdater"))
        {
            DataUpdater<MatmSectorAssignment> updater = getBean (ctx, "sector.updaters.DataUpdater");
            if (updater != null)
                ((FuserMatmSectorAssignmentClientApi)clientApi).setDataUpdater(updater);
        }
        
        if (ctx.containsBean("sector.updaters.DataRemover"))
        {
            DataRemover<MatmSectorAssignment> remover = getBean (ctx, "sector.updaters.DataRemover");
            if (remover != null)
                ((FuserMatmSectorAssignmentClientApi)clientApi).setDataRemover(remover);
        }        

        if (ctx.containsBean("sector.events.ProcessedEventManager"))
        {
            FuserProcessedEventManager<MatmSectorAssignment> manager = getBean(ctx, "sector.events.ProcessedEventManager");
            if (manager != null)
                ((FuserMatmSectorAssignmentClientApi)clientApi).setProcessedEventManager(manager);
        }

        if (ctx.containsBean("sector.events.ReceivedEventManager"))
        {
            FuserReceivedEventManager<MatmSectorAssignment> manager = getBean(ctx, "sector.events.ReceivedEventManager");
            if (manager != null)
                ((FuserMatmSectorAssignmentClientApi)clientApi).setReceivedEventManager(manager);
        }

        if( ctx.containsBean( "sector.events.FuserSyncCompleteEventManager" ) )
        {
            GenericFuserSyncCompleteEventManager manager = getBean(ctx, "sector.events.FuserSyncCompleteEventManager");
            if (manager != null)
            {
                ((FuserMatmSectorAssignmentClientApi)clientApi).setFuserSyncCompleteEventManager(manager);
            }
        }

        if (ctx.containsBean("sector.core.MatmSectorAssignmentUpdateHandler"))
        {
            GenericFuserUpdateHandler<MatmSectorAssignment> handler = getBean(ctx, "sector.core.MatmSectorAssignmentUpdateHandler");
            if (handler != null)
                handler.setFuserClientApi(clientApi);
        }

        if (ctx.containsBean("services.FuserSyncService"))
        {
            FuserSyncService syncService = getBean(ctx, "services.FuserSyncService");
            if(syncService != null)
                ((FuserMatmSectorAssignmentClientApi)clientApi).setSyncService(syncService);
        }

        if (ctx.containsBean("sector.core.SyncPoint"))
        {
            SyncPoint syncPoint = getBean (ctx, "sector.core.SyncPoint");
            if (syncPoint != null)
                ((FuserMatmSectorAssignmentClientApi)clientApi).setSyncPoint(syncPoint);
        }

        if (ctx.containsBean("sector.routes.MessageProducer"))
        {
            MessageProducer messageProducer = getBean(ctx, "sector.routes.MessageProducer");
            if(messageProducer != null)
                ((FuserMatmSectorAssignmentClientApi)clientApi).setMessageProducer(messageProducer);
        }

        if( ctx.containsBean("marshalling.MatmTransferEnvelopeMarshaller" ) )
        {
            GenericMarshaller marshaller = getBean( ctx, "marshalling.MatmTransferEnvelopeMarshaller" );
            if( marshaller != null )
            {
                ((FuserMatmSectorAssignmentClientApi)clientApi).setMatmFlightEnvMarshaller( marshaller );
            }
        }

        RuntimeConfigurer configurer = getBean(ctx, "fuser-client-api.propertyConfigurer");

        if(configurer.getProperties().containsKey( "fuser-client-api.service.sync.override.datasource" ) )
        {
            ((FuserMatmSectorAssignmentClientApi)clientApi).setSyncServiceOverrideDataSource( 
                configurer.getProperties().getProperty( "fuser-client-api.service.sync.override.datasource"));
        }
    }        

    private void initializeApi (FuserClientApi<MatmFlight> clientApi, ApplicationContext ctx)
    {
        if (ctx.containsBean("routes.core.CamelContext"))
        {
            CamelContext context = getBean(ctx, "routes.core.CamelContext");
            if (context != null)
                ((FuserMatmFlightClientApi)clientApi).setCamelContext(context);
        }

        if (ctx.containsBean("core.MatmFlightForwardingHandler"))
        {
            Handler<MatmFlight> syncHandler = getBean(ctx, "core.MatmFlightForwardingHandler");
            if (syncHandler != null)
                ((FuserMatmFlightClientApi)clientApi).setSyncHandler(syncHandler);
        }

        if (ctx.containsBean("core.Clock"))
        {
            Clock clock = getBean (ctx, "core.Clock");
            if (clock != null)
                ((FuserMatmFlightClientApi)clientApi).setClock(clock);
        }

        if (ctx.containsBean("core.FuserClientStore"))
        {
            FuserClientStore<MatmFlight> store = getBean (ctx, "core.FuserClientStore");
            if (store != null)
                ((FuserMatmFlightClientApi)clientApi).setStore(store);
        }

        if (ctx.containsBean("updaters.DataAdder"))
        {
            DataAdder<MatmFlight> adder = getBean (ctx, "updaters.DataAdder");
            if (adder != null)
                ((FuserMatmFlightClientApi)clientApi).setDataAdder(adder);
        }

        if (ctx.containsBean("updaters.DataUpdater"))
        {
            DataUpdater<MatmFlight> updater = getBean (ctx, "updaters.DataUpdater");
            if (updater != null)
                ((FuserMatmFlightClientApi)clientApi).setDataUpdater(updater);
        }

        if (ctx.containsBean("updaters.DataRemover"))
        {
            DataRemover<MatmFlight> remover = getBean (ctx, "updaters.DataRemover");
            if (remover != null)
                ((FuserMatmFlightClientApi)clientApi).setDataRemover(remover);
        }

        if (ctx.containsBean("events.ProcessedEventManager"))
        {
            FuserProcessedEventManager<MatmFlight> manager = getBean(ctx, "events.ProcessedEventManager");
            if (manager != null)
                ((FuserMatmFlightClientApi)clientApi).setProcessedEventManager(manager);
        }

        if (ctx.containsBean("events.ReceivedEventManager"))
        {
            FuserReceivedEventManager<MatmFlight> manager = getBean(ctx, "events.ReceivedEventManager");
            if (manager != null)
                ((FuserMatmFlightClientApi)clientApi).setReceivedEventManager(manager);
        }
        
        if( ctx.containsBean( "events.FuserSyncCompleteEventManager" ) )
        {
            GenericFuserSyncCompleteEventManager manager = getBean(ctx, "events.FuserSyncCompleteEventManager");
            if (manager != null)
            {
                ((FuserMatmFlightClientApi)clientApi).setFuserSyncCompleteEventManager(manager);
            }
        }

        if (ctx.containsBean("core.MatmFlightUpdateHandler"))
        {
            GenericFuserUpdateHandler<MatmFlight> handler = getBean(ctx, "core.MatmFlightUpdateHandler");
            if (handler != null)
                handler.setFuserClientApi(clientApi);
        }

        if (ctx.containsBean("services.FuserSyncService"))
        {
            FuserSyncService syncService = getBean(ctx, "services.FuserSyncService");
            if(syncService != null)
                ((FuserMatmFlightClientApi)clientApi).setSyncService(syncService);
        }

        if (ctx.containsBean("core.SyncPoint"))
        {
            SyncPoint syncPoint = getBean (ctx, "core.SyncPoint");
            if (syncPoint != null)
                ((FuserMatmFlightClientApi)clientApi).setSyncPoint(syncPoint);
        }

        if(ctx.containsBean("filter.FlightFilters"))
        {
            LogicalFilter<MatmFlight> filter = getBean(ctx, "filter.FlightFilters");
            if(filter != null)
                ((FuserMatmFlightClientApi)clientApi).setFilter(filter);

            //check the airport and asdex filter. If they are active we will put them in 
            //or filter so that the check passes if either is true
            LogicalOrFilter<MatmFlight> airportOrFilter = new LogicalOrFilter<MatmFlight>();

            if(ctx.containsBean("filter.AirportFilter"))
            {
                AirportFilter airportFilter = getBean(ctx,"filter.AirportFilter");
                if(airportFilter.isActive())
                    airportOrFilter.addFilter(airportFilter);
            }

            if(ctx.containsBean("filter.SurfaceAirportFilter"))
            {
                SurfaceAirportFilter surfaceAirportFilter = getBean(ctx,"filter.SurfaceAirportFilter");
                if(surfaceAirportFilter.isActive())
                    airportOrFilter.addFilter(surfaceAirportFilter);

            }
            if(!airportOrFilter.getFilters().isEmpty())
            {
                filter.addFilter(airportOrFilter);
            }
        }

        if (ctx.containsBean("routes.MessageProducer"))
        {
            MessageProducer messageProducer = getBean(ctx, "routes.MessageProducer");
            if(messageProducer != null)
                ((FuserMatmFlightClientApi)clientApi).setMessageProducer(messageProducer);
        }

        if( ctx.containsBean( "marshalling.MatmTransferEnvelopeMarshaller" ) )
        {
            GenericMarshaller marshaller = getBean( ctx, "marshalling.MatmTransferEnvelopeMarshaller" );
            if( marshaller != null )
            {
                ((FuserMatmFlightClientApi)clientApi).setMatmFlightEnvMarshaller( marshaller );
            }
        }
        
        if( ctx.containsBean( "core.FuserSyncRequest" ) )
        {
            FuserSyncRequest fuserSyncRequest = getBean( ctx, "core.FuserSyncRequest" );
            ((FuserMatmFlightClientApi)clientApi).setFuserSyncRequest( fuserSyncRequest );
        }

        RuntimeConfigurer configurer = getBean(ctx, "fuser-client-api.propertyConfigurer");
        if(configurer.getProperties().containsKey( "fuser-client-api.uri.matmFlight.process.publish" ) )
        {
            ((FuserMatmFlightClientApi)clientApi).setDataPublishEndpoint(
                configurer.getProperties().getProperty("fuser-client-api.uri.matmFlight.process.publish"));
        }
        if(configurer.getProperties().containsKey( "fuser-client-api.uri.matmFlight.process.publishBatch" ) )
        {
            ((FuserMatmFlightClientApi)clientApi).setDataBatchPublishEndpoint(
                configurer.getProperties().getProperty("fuser-client-api.uri.matmFlight.process.publishBatch"));
        }
        if(configurer.getProperties().containsKey( "fuser-client-api.uri.matmFlight.process.publishRemove" ) )
        {
            ((FuserMatmFlightClientApi)clientApi).setDataPublishRemoveEndpoint(
                configurer.getProperties().getProperty("fuser-client-api.uri.matmFlight.process.publishRemove"));
        }

        if(configurer.getProperties().containsKey( "fuser-client-api.service.sync.override.datasource" ) )
        {
            ((FuserMatmFlightClientApi)clientApi).setSyncServiceOverrideDataSource( 
                configurer.getProperties().getProperty( "fuser-client-api.service.sync.override.datasource"));
        }
        if(configurer.getProperties().containsKey( "fuser-client-api.publish.active" ) )
        {
            ((FuserMatmFlightClientApi)clientApi).setPublishActive(
                Boolean.parseBoolean( configurer.getProperties().getProperty( "fuser-client-api.publish.active" ) ) );
        }
    }

    @Override
    public void setPropertyOverrideFile (String file)
    {
        if (file != null)
            System.setProperty("fuser-client-api.override.config", file);
    }

    @Override
    public FuserClientApi<MatmFlight> getApi ()
    {
        return flightApi;
    }

    @Override
    public FuserClientApi<MatmAircraft> getAircraftApi()
    {
        return aircraftApi;
    }
    
    @Override
    public FuserClientApi<MatmSectorAssignment> getSectorAssignmentApi()
    {
        return sectorAssignmentApi;
    }    

    @SuppressWarnings("unchecked")
    private <T> T getBean (ApplicationContext ctx, String beanId)
    {
        if (ctx != null && beanId != null)
        {
            return (T)ctx.getBean(beanId);
        }

        return null;
    }

    private List<String> getContexts ()
    {
        List<String> contexts = new ArrayList<> ();

        contexts.addAll (getCoreContexts());
        contexts.addAll (getAircraftContexts());
        contexts.addAll (getSectorContexts());
        contexts.addAll (getProcessContexts());
        contexts.addAll (getRouteContexts());
        contexts.addAll (getServiceContexts());

        return contexts;
    }

    private List<String> getCoreContexts ()
    {
        List<String> contexts = new ArrayList<>();

        contexts.add("config/fuser-client-api/beans.properties.xml");
        contexts.add("config/fuser-client-api/beans.core.xml");
        contexts.add("config/fuser-client-api/beans.filter.xml");
        contexts.add("config/fuser-client-api/beans.events.xml");
        contexts.add("config/fuser-client-api/beans.updaters.xml");
        contexts.add("config/fuser-client-api/beans.marshalling.xml");

        return contexts;
    }

    private List<String> getAircraftContexts()
    {
        List<String> contexts = new ArrayList<>();

        if (isLoadAircraft())
        {
            log.info("Loading aircraft route contexts");
            contexts.add("config/fuser-client-api/beans.aircraft.core.xml");
            contexts.add("config/fuser-client-api/beans.aircraft.events.xml");
            contexts.add("config/fuser-client-api/beans.aircraft.updaters.xml");
        }

        return contexts;
    }

    private List<String> getSectorContexts()
    {
        List<String> contexts = new ArrayList<>();

        if (isLoadSectorAssignments())
        {
            log.info("Loading sector assignment route contexts");
            contexts.add("config/fuser-client-api/beans.sector.core.xml");
            contexts.add("config/fuser-client-api/beans.sector.events.xml");
            contexts.add("config/fuser-client-api/beans.sector.updaters.xml");
        }

        return contexts;
    }    
    
    private List<String> getProcessContexts ()
    {
        List<String> contexts = new ArrayList<> ();

        contexts.add("config/fuser-client-api/beans.process.xml");

        return contexts;
    }

    private List<String> getRouteContexts ()
    {
        List<String> contexts = new ArrayList<> ();

        if (isLoadJms ())
        {
            log.info("Loading JMS contexts");
            contexts.add("config/fuser-client-api/beans.jms.xml");
        }

        if (isLoadRoutes ())
        {
            log.info("Loading core route contexts");
            contexts.add("config/fuser-client-api/routes.core.xml");

            if (isLoadAircraft())
            {
                log.info("Loading aircraft route contexts");
                contexts.add("config/fuser-client-api/routes.aircraft.xml");
            }
            
            if (isLoadSectorAssignments())
            {
                log.info("Loading sector assignemnt route contexts");
                contexts.add("config/fuser-client-api/routes.sector.xml");
            }            
        }

        return contexts;
    }

    private List<String> getServiceContexts ()
    {
        List<String> contexts = new ArrayList<> ();

        if (isLoadServicesClients () && isLoadSecureSync())
        {
            log.info("Loading secure service client contexts");
            contexts.add("config/fuser-client-api/beans.services.client.secure.xml");
        }
        else if (isLoadServicesClients () && !isLoadSecureSync())
        {
            log.info("Loading service client contexts");
            contexts.add("config/fuser-client-api/beans.services.client.xml");
        }

        return contexts;
    }

    public boolean isLoadJms ()
    {
        return loadJms;
    }

    public void setLoadJms (boolean loadJms)
    {
        this.loadJms = loadJms;
    }

    public boolean isLoadRoutes ()
    {
        return loadRoutes;
    }

    public void setLoadRoutes (boolean loadRoutes)
    {
        this.loadRoutes = loadRoutes;
    }

    public boolean isLoadServicesClients ()
    {
        return loadServicesClients;
    }

    public void setLoadServicesClients (boolean loadServicesClients)
    {
        this.loadServicesClients = loadServicesClients;
    }

    public boolean isLoadAircraft()
    {
        return loadAircraft;
    }

    public void setLoadAircraft(boolean loadAircraft)
    {
        this.loadAircraft = loadAircraft;
    }
    
    public boolean isLoadSectorAssignments()
    {
        return loadSectorAssignments;
    }

    public void setLoadSectorAssignments(boolean loadSectorAssignments)
    {
        this.loadSectorAssignments = loadSectorAssignments;
    }        

    public boolean isLoadSecureSync()
    {
        return loadSecureSync;
    }

    @Override
    public void setLoadSecureSync( boolean loadSecureSync )
    {
        this.loadSecureSync = loadSecureSync;
    }
}
