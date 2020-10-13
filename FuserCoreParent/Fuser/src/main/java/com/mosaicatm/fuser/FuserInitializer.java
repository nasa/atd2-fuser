package com.mosaicatm.fuser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Collections;
import java.util.Comparator;

import org.apache.camel.CamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.mosaicatm.fuser.common.Initializer;
import com.mosaicatm.fuser.common.camel.FuserAmqConnectionFactory;
import com.mosaicatm.fuser.rules.RuleFactory;
import com.mosaicatm.fuser.services.client.FuserSyncService;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.StoreRemover;
import com.mosaicatm.fuser.transform.FuserTransformCamelRouteBuilder;
import com.mosaicatm.fuser.transform.api.FuserPluginProps;
import com.mosaicatm.fuser.transform.api.FuserTransformPluginApi;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.lib.util.concurrent.Handler;
import com.mosaicatm.lib.util.concurrent.SyncPoint;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.aircraftcomposite.MatmAircraftComposite;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flightcomposite.MatmFlightComposite;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class FuserInitializer
implements Initializer
{
    private final Log log = LogFactory.getLog(getClass());
    
    private long syncPointDurationMillis = 120000L;
    
    private Handler<MatmFlight> syncHandler;
    private Handler<MatmAircraft> aircraftSyncHandler;
    private Handler<MatmSectorAssignment> sectorAssignmentSyncHandler;
    private FuserStore<MatmFlight, MetaData> fuserStore;
    private FuserStore<MatmAircraft, MetaData> aircraftFuserStore;
    private FuserStore<MatmSectorAssignment, MetaData> sectorAssignmentFuserStore;
    private StoreRemover<MatmFlight> storeRemover;
    private StoreRemover<MatmAircraft> aircraftStoreRemover;
    private StoreRemover<MatmSectorAssignment> sectorAssignmentStoreRemover;
    private FuserSyncService syncServiceClient;
    private RuleFactory<MatmFlight> ruleFactory;
    private RuleFactory<MatmAircraft> aircraftRuleFactory;
    
    private SyncPoint clockSyncPoint;
    private SyncPoint syncPoint;
    
    private List<String> airportList;
    private List<String> pluginTypesToLoad;
    
    private AutowireCapableBeanFactory beanFactory;
    private PluginManager pluginManager;
    private CamelContext camelContext;
    private Properties props;
    
    @Override
    public void initialize() 
    {
        log.info("Initializing Fuser...");
        long startTime = System.currentTimeMillis();
        
        Timer syncPointTimer = new Timer("SyncPointTime");
        
        try
        {
            initializeRuleFactory(ruleFactory);
            initializeRuleFactory(aircraftRuleFactory);
            
            initializeStore(fuserStore);
            initializeStore(aircraftFuserStore);
            initializeStore(sectorAssignmentFuserStore);
            
            // need to wait for a valid clock sync so the removal
            // process runs correct
            clockSyncPoint.sync(this);
            
            cleanStore(storeRemover);
            cleanStore(aircraftStoreRemover);
            cleanStore(sectorAssignmentStoreRemover);
            
            syncPointTimer.schedule(new ForceUnlock(), syncPointDurationMillis);
            
            initializeFlights();
            initializeAircraft();
            initializeSectorAssignments();
            initializeFuserPlugins();
            
        }
        catch (Exception e)
        {
            log.error("Failed to initialize fuser", e);
            throw new IllegalStateException( "Failed to initialize fuser!" );
        }        
        
        syncPointTimer.cancel();
        log.info("Unlocking all sync points");
        syncPoint.unlock();
        
        startRemover(storeRemover);
        startRemover(aircraftStoreRemover);
        startRemover(sectorAssignmentStoreRemover);
        
        long endTime = System.currentTimeMillis() - startTime;
        log.info("...Fuser initialized in " + endTime + "ms");
    }
    
    private void startRemover(StoreRemover<?> remover)
    {
        log.info("Starting store remover...");
        
        if (remover != null)
            remover.start();
        
        log.info("...store remover started");
    }
    
    /**
     * Blocks waiting on a clock sync.  When the sync is unlocked
     * it will run the removal logic to clean any expired data from
     * the store.
     * @throws Exception
     */
    private void cleanStore (StoreRemover<?> remover)
    throws Exception
    {
        log.info("Cleaning fuser store...");
        
        if (remover != null)
            remover.remove();
        
        log.info("...fuser store cleaned");
    }
    
    /**
     * Initializes the fuser store and reconstitutes any save flight data
     * @throws Exception
     */
    private void initializeStore(FuserStore<?,?> store)
    throws Exception
    {
        log.info("Initializing fuser store...");
        
        if (store != null)
        {
            try
            {                
                store.lockEntireStore();
                store.initialize();
            }
            finally
            {
                store.unlockEntireStore();
            }
        }
        
        log.info("...fuser store initialized");
    }
    
    private void initializeRuleFactory (RuleFactory<?> factory)
    {
        log.info("Initializing rule factory...");
        
        if (factory != null)
            factory.initialize();
        else
            log.info("Rule factory is null, nothing to initialize");
        
        log.info("...Rule factory initialized");
    }
    
    /**
     * Requests the full set of flights from the fuser sync service and
     * merges the sync data with existing flights in the flight store.
     * @throws Exception
     */
    private void initializeFlights()
    throws Exception
    {
        log.info("Initializing flights...");
        long startTime = System.currentTimeMillis();
        
        List<MatmFlightComposite> flights = getFlightsFromSync();
        
        if (flights != null && !flights.isEmpty())
        {
            for (MatmFlightComposite flight : flights)
            {
                if (flight == null || flight.getFlight() == null)
                {
                    log.error("Failed initializing undefined composite flight");
                    continue;
                }
                
                syncHandler.handle(flight.getFlight());
                
                initializeMetaData(flight.getFlight(), flight.getMetaData(), fuserStore);
            }
        }
        
        long endTime = System.currentTimeMillis() - startTime;
        log.info("...flights initialized in " + endTime + "ms");
    }
    
    private void initializeAircraft()
    throws Exception
    {
        log.info("Initializing aircraft...");
        long startTime = System.currentTimeMillis();
        
        List<MatmAircraftComposite> aircraftList = getAircraftFromSync();
        
        if (aircraftList != null && !aircraftList.isEmpty())
        {
            for (MatmAircraftComposite aircraft : aircraftList)
            {
                if (aircraft == null || aircraft.getAircraft() == null)
                {
                    log.error("Failed initializing undefined composite aircraft");
                    continue;
                }
                
                aircraftSyncHandler.handle(aircraft.getAircraft());
                initializeMetaData(aircraft.getAircraft(), aircraft.getMetaData(), aircraftFuserStore);
            }
        }
        
        long endTime = System.currentTimeMillis() - startTime;
        log.info("...aircraft initialized in " + endTime + "ms");
    }
    
    private void initializeSectorAssignments()
    throws Exception
    {
        log.info("Initializing sector assignments...");
        long startTime = System.currentTimeMillis();
        
        List<MatmSectorAssignment> sectorList = getSectorAssignmentsFromSync();
        
        if (sectorList != null && !sectorList.isEmpty())
        {
            // Sort the results by timestamp to ensure they are handled in the correct order
            Comparator<MatmSectorAssignment> compareByTimestamp = 
                    (MatmSectorAssignment o1, MatmSectorAssignment o2) -> o1.getTimestamp().compareTo( o2.getTimestamp() );

            Collections.sort( sectorList, compareByTimestamp );               
            
            for (MatmSectorAssignment sector : sectorList)
            {
                if (sector == null)
                {
                    log.error("Failed initializing undefined sector assignment");
                    continue;
                }
                
                sectorAssignmentSyncHandler.handle(sector);
            }
        }
        
        long endTime = System.currentTimeMillis() - startTime;
        log.info("...sector assignmentsinitialized in " + endTime + "ms");
    }    
    
    private void initializeFuserPlugins()
    throws Exception
    {
        log.info("Initializing fuser plugins...");
        long startTime = System.currentTimeMillis();
        
        FuserAmqConnectionFactory connectionFactory = new FuserAmqConnectionFactory();
        
        if( pluginManager != null )
        {
            // Get the started plugins (plugins in "disabled.txt" will not be started)
            List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();

            for( PluginWrapper startedPlugin : startedPlugins )
            {
                List<FuserTransformPluginApi> fuserTransformPlugins = pluginManager.getExtensions( FuserTransformPluginApi.class, startedPlugin.getPluginId() );

                if( fuserTransformPlugins != null )
                {
                    for( FuserTransformPluginApi fuserTransformPlugin : fuserTransformPlugins )
                    {
                        if( pluginTypesToLoad == null || pluginTypesToLoad.isEmpty() ||
                                pluginTypesToLoad.contains( fuserTransformPlugin.getSourceType().toLowerCase() ) ||
                                pluginTypesToLoad.contains( startedPlugin.getPluginId().toLowerCase() ) )
                        {
                            FuserPluginProps pluginProps = new FuserPluginProps();
                            pluginProps.setProperties(props);
                            pluginProps.setConnectionFactory(connectionFactory);
                            
                            fuserTransformPlugin.initialize( pluginProps );
                            fuserTransformPlugin.addCamelComponent( camelContext );
                            // Create a RouteBuilder to create camel routes based on the objects from the transform plugin
                            FuserTransformCamelRouteBuilder camelRouteBuilder = new FuserTransformCamelRouteBuilder(
                                    fuserTransformPlugin,
                                    beanFactory,
                                    props);
                            try
                            {
                                // Add the routes to the Fuser's existing camel context
                                camelRouteBuilder.addRoutesToCamelContext( camelContext );
                            }
                            catch (Exception e)
                            {
                                log.warn( "Initialization of additional camel routes failed", e );
                            }
                        }
                        else
                        {
                            log.info( "plugin type " + fuserTransformPlugin.getSourceType() + " not included in pluginTypesToLoad value" );
                        }
                    }
                }
            }
        }
        
        long endTime = System.currentTimeMillis() - startTime;
        log.info("...fuser plugins initialized in " + endTime + "ms");
    }
    
    private <T extends MatmObject> void initializeMetaData(T flight, Collection<MetaData> metaData, FuserStore<T, MetaData> store)
    {        
        if (flight != null && metaData != null && !metaData.isEmpty() &&
            store != null)
        {
            try
            {
                store.lockStore( flight );
                store.updateMetaData(flight, metaData);
            }
            finally
            {
                store.unlockStore( flight );
            }
        }
    }
    
    private List<MatmFlightComposite> getFlightsFromSync()
    throws Exception
    {
        List<MatmFlightComposite> syncedFlights = new ArrayList<>();
        
        if (useSyncService())
        {
            log.info("Requesting flights from sync service...");
            long startTime = (System.currentTimeMillis());
            
            List<MatmFlightComposite> flights = null;
            
            for (String airport : airportList)
            {                    
                if (airport == null || airport.trim().isEmpty())
                    continue;
                
                flights = syncServiceClient.getCompositeFlightsByAirport(airport);
                
                if (flights != null)
                {
                    log.info("Synced " + flights.size() + " flights for " + airport);
                    syncedFlights.addAll(flights);
                }
            }
            
            long endTime = System.currentTimeMillis() - startTime;
            log.info("...requested flights from sync service in " + endTime + "ms");
        }
        
        return syncedFlights;
    }
    
    private List<MatmAircraftComposite> getAircraftFromSync()
    throws Exception
    {
        List<MatmAircraftComposite> syncedAircraft = new ArrayList<>();
        
        if (useSyncService())
        {
            log.info("Request aircraft from sync service...");
            long startTime = System.currentTimeMillis();
                
            List<MatmAircraftComposite> aircraftList = syncServiceClient.getCompositeAircraft();
            
            if (aircraftList != null)
            {
                log.info("Synced " + aircraftList.size() + " aircraft");
                syncedAircraft.addAll(aircraftList);
            }
            
            long endTime = System.currentTimeMillis() - startTime;
            log.info("...requested aircraft from sync service in " + endTime + "ms");
        }
        
        return syncedAircraft;
    }
    
    private List<MatmSectorAssignment> getSectorAssignmentsFromSync()
    throws Exception
    {
        List<MatmSectorAssignment> syncedSectorAssignments = new ArrayList<>();
        
        if (useSyncService())
        {
            log.info("Request sector assignments from sync service...");
            long startTime = System.currentTimeMillis();
                
            List<MatmSectorAssignment> sectorList = syncServiceClient.getSectorAssignments();
            
            if (sectorList != null)
            {
                log.info("Synced " + sectorList.size() + " sector assignments");
                syncedSectorAssignments.addAll(sectorList);
            }
            
            long endTime = System.currentTimeMillis() - startTime;
            log.info("...requested sector assignments from sync service in " + endTime + "ms");
        }
        
        return syncedSectorAssignments;
    }

    private boolean useSyncService ()
    {        
        if (syncServiceClient == null)
        {
            log.warn("Fuser sync service client is undefined");
            return false;
        }
            
        if (airportList == null || airportList.isEmpty())
        {
            log.warn("Fuser sync service client failed, no defined airports");
            return false;
        }
        
        return true;
    }
    
    public void setFuserStore (FuserStore<MatmFlight, MetaData> fuserStore)
    {
        this.fuserStore = fuserStore;
    }
    
    public void setAircraftFuserStore (FuserStore<MatmAircraft, MetaData> aircraftFuserStore)
    {
        this.aircraftFuserStore = aircraftFuserStore;
    }

    public void setSectorAssignmentFuserStore( FuserStore<MatmSectorAssignment, MetaData> sectorAssignmentFuserStore )
    {
        this.sectorAssignmentFuserStore = sectorAssignmentFuserStore;
    }
    
    public void setStoreRemover (StoreRemover<MatmFlight> storeRemover)
    {
        this.storeRemover = storeRemover;
    }
    
    public void setAircraftStoreRemover (StoreRemover<MatmAircraft> aircraftStoreRemover)
    {
        this.aircraftStoreRemover = aircraftStoreRemover;
    }

    public void setSectorAssignmentStoreRemover( StoreRemover<MatmSectorAssignment> sectorAssignmentStoreRemover )
    {
        this.sectorAssignmentStoreRemover = sectorAssignmentStoreRemover;
    }
    
    public void setSyncServiceClient (FuserSyncService syncServiceClient)
    {
        this.syncServiceClient = syncServiceClient;
    }
    
    public void setSyncHandler (Handler<MatmFlight> syncHandler)
    {
        this.syncHandler = syncHandler;
    }
    
    public void setAircraftSyncHandler (Handler<MatmAircraft> aircraftSyncHandler)
    {
        this.aircraftSyncHandler = aircraftSyncHandler;
    }    

    public void setSectorAssignmentSyncHandler( Handler<MatmSectorAssignment> sectorAssignmentSyncHandler )
    {
        this.sectorAssignmentSyncHandler = sectorAssignmentSyncHandler;
    }    
    
    public void setRuleFactory (RuleFactory<MatmFlight> ruleFactory)
    {
        this.ruleFactory = ruleFactory;
    }
    
    public void setAircraftRuleFactory (RuleFactory<MatmAircraft> aircraftRuleFactory)
    {
        this.aircraftRuleFactory = aircraftRuleFactory;
    }
    
    public void setAirportList (String airports)
    {
        if (airports != null && !airports.trim().isEmpty())
        {
            airportList = Arrays.asList(airports.split(",\\s?"));
        }
    }
    
    public void setClockSyncPoint (SyncPoint clockSyncPoint)
    {
        this.clockSyncPoint = clockSyncPoint;
    }
    
    public void setSyncPoint (SyncPoint syncPoint)
    {
        this.syncPoint = syncPoint;
    }
    
    public void setSyncPointDurationMinutes(int minutes)
    {
        if (minutes > 0)
            setSyncPointDurationMillis(minutes * TimeFactory.MINUTE_IN_MILLIS);
    }
    
    public void setSyncPointDurationSeconds(int seconds)
    {
        if (seconds > 0)
            setSyncPointDurationMillis(seconds * TimeFactory.SECOND_IN_MILLIS);
    }
    
    public void setSyncPointDurationMillis(long millis)
    {
        this.syncPointDurationMillis = millis;
    }
    
    public void setProps(Properties props) {
        this.props = props;
    }

    public void setPluginTypesToLoad(List<String> pluginTypesToLoad) {
        this.pluginTypesToLoad = pluginTypesToLoad;
    }
    
    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    private class ForceUnlock
    extends TimerTask
    {

        @Override
        public void run()
        {
            if (syncPoint.isLocked())
            {
                log.warn("Sync point forcefully unlocked");
                syncPoint.unlock();
            }
        }
    }
}
