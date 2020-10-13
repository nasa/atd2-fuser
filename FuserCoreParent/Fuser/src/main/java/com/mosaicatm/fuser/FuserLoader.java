package com.mosaicatm.fuser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.camel.CamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.pf4j.PluginManager;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.mosaicatm.fuser.aggregator.MetaDataManager;
import com.mosaicatm.fuser.heartbeat.HeartbeatManager;
import com.mosaicatm.fuser.playback.DailyFuserRepositoryCacheHandler;
import com.mosaicatm.fuser.rules.RuleFactory;
import com.mosaicatm.fuser.services.client.FuserAircraftSyncService;
import com.mosaicatm.fuser.services.client.FuserSectorAssignmentSyncService;
import com.mosaicatm.fuser.services.client.FuserSyncService;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.FuserStoreProxy;
import com.mosaicatm.fuser.store.SectorAssignmentDataStore;
import com.mosaicatm.fuser.store.SectorAssignmentStoreProxy;
import com.mosaicatm.fuser.store.StoreRemover;
import com.mosaicatm.fuser.store.event.FuserStoreListener;
import com.mosaicatm.lib.playback.Playback;
import com.mosaicatm.lib.spring.RuntimeConfigurer;
import com.mosaicatm.lib.util.concurrent.Handler;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class FuserLoader
{
    private final Log log = LogFactory.getLog(getClass());

    private boolean loadCoreRoutes;
    private boolean loadCoreJms;
    private boolean loadCoreLogging;
    private boolean loadCoreServices;
    private boolean loadCoreServiceClients;
    private boolean loadSecureSync;
    
    private List<String> pluginTypesToLoad;

    private boolean loadAircraftBeans;
    private boolean loadAircraftRoutes;
    private boolean loadAircraftServices;
    
    private boolean loadSectorBeans;
    private boolean loadSectorRoutes;
    private boolean loadSectorServices;

    private boolean loadTransforms;
    private boolean loadAggregator;
    private boolean loadFilters; 
    
    private boolean loadRedis;

    private boolean loadRules;
    
    private boolean loadHeartbeat;

    public boolean isLoadRedis() {
        return loadRedis;
    }

    public void setLoadRedis(boolean loadRedis) {
        this.loadRedis = loadRedis;
    }

    public void load ()
    {
        Set<String> contexts = new LinkedHashSet<String> ();

        contexts.addAll (getCoreContexts ());
        contexts.addAll (getAircraftContexts());
        contexts.addAll (getSectorContexts());
        contexts.addAll (getRedisContexts ());        
        contexts.addAll (getTransformContexts ());
        contexts.addAll (getRuleContexts ());
        contexts.addAll (getAggregatorContexts ());
        contexts.addAll (getFilterContexts ());
        contexts.addAll (getHeartbeatContexts());
        contexts.addAll (getDataReprocessContexts());
        
        ApplicationContext ctx = new ClassPathXmlApplicationContext (contexts.toArray(new String[]{}));
        RuntimeConfigurer configurer = (RuntimeConfigurer)ctx.getBean("fuser.propertyConfigurer");
        Properties props = configurer.getProperties();

        Playback fuserPlayback = getBean( ctx, "playback.FuserPlayback" );
        DailyFuserRepositoryCacheHandler cacheHandler = getBean( ctx, "playback.DailyRepositoryCacheHandler" );

        if( fuserPlayback != null && fuserPlayback.isActive() )
        {
            if( isLoadAircraftBeans() && cacheHandler != null )
            {
                FuserStoreProxy<MatmAircraft, MetaData> fuserStore = getBean(ctx, "aircraft.core.FuserStoreProxy");
                cacheHandler.setAircraftFuserStore( fuserStore );
            }
            if( isLoadSectorBeans() && cacheHandler != null )
            {
                FuserStoreProxy<MatmSectorAssignment, MetaData> sectorAssignmentDataStore = getBean(ctx, "sector.core.SectorAssignmentStoreProxy");
                cacheHandler.setSectorAssignmentDataStore( sectorAssignmentDataStore );
            }            
            
            fuserPlayback.publishStartTime();
        }


        FuserStoreProxy<MatmFlight, MetaData> fuserStore= getBean(ctx, "fuser.core.FuserStoreProxy");
        if (Boolean.parseBoolean(props.getProperty("fuser.redis.enabled")))
        {
            FuserStore<MatmFlight, MetaData> redisStore = getBean(ctx, "redis.RedisFuserStore");
            
            if (redisStore != null)
                fuserStore.setProxy(redisStore);
                        
            if (Boolean.parseBoolean(props.getProperty("fuser.redis.cleanstart")))
            {
                log.info ("Clearing Redis database and current flight cache");
                fuserStore.clear();
            }
            
        }
        
        if (Boolean.parseBoolean(props.getProperty("fuser.fromFuser.remove.publish.active")))
        {
            FuserStoreListener<MatmFlight> storeListener = getBean(ctx, "fuser.core.MatmFlightStoreListener");
            fuserStore.addFuserStoreListener(storeListener);
        }
        
        RuleFactory<MatmFlight> ruleFactory = getBean(ctx, "fuser-rules.RuleFactory");
        
        MetaDataManager<MatmFlight> manager = getBean(ctx, "fuser-aggregator.core.MetaDataManager");
        
        if (manager != null && ruleFactory != null)
            manager.setRuleFactory(ruleFactory);
        
        FuserInitializer initializer = getBean(ctx, "fuser.core.FuserInitializer");
        
        if( isLoadAircraftBeans() )
            loadAircraft(ctx, props, initializer);        
        
        if( isLoadSectorBeans() )
            loadSectorAssignments(ctx, props, initializer);
        
        if (initializer != null)
        {            
            // this references the FuserSyncService interface and not
            // the server implementation
            FuserSyncService syncClient = getBean(ctx, "fuser.services.client.FuserSyncService");
            PluginManager pluginManager = ctx.getBean( "fuser.transform.pluginManager", PluginManager.class );
            CamelContext camelContext = (CamelContext)ctx.getBean( "routes.fuser.CamelContext" );
            AutowireCapableBeanFactory beanFactory = ctx.getAutowireCapableBeanFactory();
            
            initializer.setSyncServiceClient(syncClient);
            initializer.setRuleFactory(ruleFactory);
            initializer.setPluginManager(pluginManager);
            initializer.setCamelContext(camelContext);
            initializer.setBeanFactory(beanFactory);
            initializer.setProps(props);
            initializer.setPluginTypesToLoad(this.pluginTypesToLoad);

            initializer.initialize();
        }
        
        HeartbeatManager heartbeatManager = getBean(ctx, "fuser.heartbeat.HeartbeatManager");
        
        if (heartbeatManager != null)
        {
            heartbeatManager.start();
        }
        
        if( fuserPlayback != null && fuserPlayback.isActive() )
        {
            try
            {
                fuserPlayback.startPlayback();
            }
            catch( Exception e )
            {
                log.error( "Unable to run playback", e );
            }
        }
    }
    
    private void loadAircraft(ApplicationContext ctx, Properties props, FuserInitializer initializer)
    {
        FuserStoreProxy<MatmAircraft, MetaData> fuserStore= getBean(ctx, "aircraft.core.FuserStoreProxy");
        if (Boolean.parseBoolean(props.getProperty("fuser.redis.enabled")))
        {
            FuserStore<MatmAircraft, MetaData> redisStore = getBean(ctx, "aircraft.redis.RedisFuserStore");
            
            if (redisStore != null)
                fuserStore.setProxy(redisStore);                
                        
            if (Boolean.parseBoolean(props.getProperty("fuser.redis.cleanstart")))
            {
                log.info ("Clearing Redis aircraft database and current flight cache");
                fuserStore.clear();
            }

        }            
        
        if (Boolean.parseBoolean(props.getProperty("fuser.fromFuser.aircraft.remove.publish.active")))
        {
            FuserStoreListener<MatmAircraft> storeListener = getBean(ctx, "aircraft.core.MatmAircraftStoreListener");
            fuserStore.addFuserStoreListener(storeListener);
        }
        
        com.mosaicatm.fuser.service.FuserSyncService serverService = getBean(ctx, "fuser.services.FuserSyncService");
        
        if (serverService != null)
        {

            FuserAircraftSyncService aircraftSyncService = getBean(ctx, "fuser.services.FuserAircraftSyncService");
            
            if (aircraftSyncService != null)
                serverService.setAircraftSyncService(aircraftSyncService);
        }
        
        RuleFactory<MatmAircraft> ruleFactory = getBean(ctx, "aircraft.fuser-rules.RuleFactory");
        MetaDataManager<MatmAircraft> manager = getBean(ctx, "aircraft.core.MetaDataManager");
        Handler<MatmAircraft> syncHandler = getBean(ctx, "aircraft.core.FuserReceiveForwardingHandler");
        StoreRemover<MatmAircraft> storeRemover = getBean(ctx, "aircraft.core.FuserStoreTimedRemover");
        
        if (manager != null && ruleFactory != null)
            manager.setRuleFactory(ruleFactory);
        
        if (initializer != null)
        {
            initializer.setAircraftFuserStore(fuserStore);
            initializer.setAircraftStoreRemover(storeRemover);
            initializer.setAircraftSyncHandler(syncHandler);
            
            initializer.setAircraftRuleFactory(ruleFactory);
        }
    }
    
    private void loadSectorAssignments(ApplicationContext ctx, Properties props, FuserInitializer initializer)
    {
        SectorAssignmentStoreProxy fuserStore = getBean(ctx, "sector.core.SectorAssignmentStoreProxy");
        
        if (Boolean.parseBoolean(props.getProperty("fuser.redis.enabled")))
        {
            SectorAssignmentDataStore redisStore = getBean(ctx, "sector.redis.RedisFuserStore");
            
            if (redisStore != null)
            {
                fuserStore.setProxy(redisStore);
            }
            else
            {
                log.error( "Unable to find bean : sector.redis.RedisFuserStore" );
            }
                
            if (Boolean.parseBoolean(props.getProperty("fuser.redis.cleanstart")))
            {
                log.info ("Clearing Redis sector assignment database and current flight cache");
                fuserStore.clear();
            }                   
        }
        
        if (Boolean.parseBoolean(props.getProperty("fuser.fromFuser.sectorAssignment.remove.publish.active")))
        {
            FuserStoreListener<MatmSectorAssignment> storeListener = getBean(ctx, "sector.core.MatmSectorAssignmentStoreListener");
            fuserStore.addFuserStoreListener(storeListener);
        }        
        
        com.mosaicatm.fuser.service.FuserSyncService serverService = getBean(ctx, "fuser.services.FuserSyncService");
        StoreRemover<MatmSectorAssignment> storeRemover = getBean(ctx, "sector.core.FuserStoreTimedRemover");
        
        if (serverService != null && isLoadSectorServices())
        {
            FuserSectorAssignmentSyncService sectorAssignmentSyncService = getBean(ctx, "fuser.services.FuserSectorAssignmentSyncService");
            
            if (sectorAssignmentSyncService != null )
            {
                serverService.setSectorAssignmentSyncService(sectorAssignmentSyncService);
            }
            else
            {
                log.error( "Unable to find bean : fuser.services.FuserSectorAssignmentSyncService" );
            }
        }
        
        if (initializer != null)
        {
            Handler<MatmSectorAssignment> syncHandler = getBean(ctx, "sector.core.FuserReceiveForwardingHandler");
            
            initializer.setSectorAssignmentFuserStore(fuserStore);
            initializer.setSectorAssignmentSyncHandler(syncHandler);
            initializer.setSectorAssignmentStoreRemover(storeRemover);
        }        
    }    

    private List<String> getRedisContexts() {
        List<String> contexts = new ArrayList<String> ();
        
        if (isLoadRedis())
            contexts.add("config/fuser/beans.redis.xml");
        
        return contexts;
    }

    @SuppressWarnings("unchecked")
    private <T> T getBean (ApplicationContext ctx, String beanName)
    {
        if (ctx.containsBean(beanName))
            return (T)ctx.getBean(beanName);
        return null;
    }

    private List<String> getAircraftContexts()
    {
        List<String> contexts = new ArrayList<String>();
        
        if (isLoadAircraftBeans())
        {
            log.info("Loading aircraft beans");
            contexts.add("config/fuser/beans.aircraft.core.xml");
            contexts.add("config/fuser/beans.aircraft.filter.xml");
            contexts.add("config/fuser/beans.aircraft.rules.xml");
            contexts.add("config/fuser/beans.aircraft.aggregate.xml");
            
            if (isLoadRedis())
            {
                contexts.add("config/fuser/beans.aircraft.redis.xml");
            }
            
            if (isLoadAircraftRoutes())
            {
                log.info("Loading aircraft routes");
                contexts.add("config/fuser/routes.aircraft.core.xml");
            }
            
            if (isLoadAircraftServices())
            {
                log.info("Loading aircraft services");
                contexts.add("config/fuser/beans.aircraft.services.xml");
            }
        }

        return contexts;
    }
    
    private List<String> getSectorContexts()
    {
        List<String> contexts = new ArrayList<String>();
        
        if (isLoadSectorBeans())
        {
            log.info("Loading sector beans");
            contexts.add("config/fuser/beans.sector.core.xml");

            if (isLoadRedis())
            {
                contexts.add("config/fuser/beans.sector.redis.xml");
            }
            
            if (isLoadSectorServices())
            {
                log.info("Loading sector services");
                contexts.add("config/fuser/beans.sector.services.xml");
            }            
                       
            if (isLoadSectorRoutes())
            {
                log.info("Loading sector routes");
                contexts.add("config/fuser/routes.sector.xml");
            }
        }

        return contexts;
    }    
    
    private List<String> getCoreContexts ()
    {
        List<String> contexts = new ArrayList<String> ();

        contexts.add("config/fuser/beans.properties.xml");
        contexts.add("config/fuser/beans.core.xml");

        if (isLoadCoreJms())
        {
            log.info ("Loading jms beans");
            contexts.add("config/fuser/beans.jms.xml");
        }

        if (isLoadCoreLogging())
        {
            log.info ("Loading core logging");
            contexts.add("config/fuser/beans.marshalling.xml");
        //    contexts.add("config/fuser/beans.logging.xml");
        }

        if (isLoadCoreRoutes())
        {
            log.info ("Loading core routes");
            contexts.add("config/fuser/routes.core.xml");
        }

        if (isLoadCoreServices() && !isLoadSecureSync())
        {
            log.info ("Loading core services");
            contexts.add("config/fuser/beans.services.server.xml");
        }
        else if (isLoadCoreServices() && isLoadSecureSync())
        {
            log.info( "Loading secure core services" );
            contexts.add("config/fuser/beans.services.server.secured.xml");
        }
        
        if (isLoadCoreServiceClients())
        {
            log.info("Loading core service clients");
            contexts.add("config/fuser/beans.services.client.xml");
        }

        return contexts;
    }

     private List<String> getTransformContexts ()
    {
        List<String> contexts = new ArrayList<String> ();

        if (isLoadTransforms ())
        {
            log.info("Loading transform beans");
            contexts.add("config/fuser/beans.transform.xml");
        }

        return contexts;
    }

    private List<String> getAggregatorContexts ()
    {
        List<String> contexts = new ArrayList<> ();

        if (isLoadAggregator())
        {
            log.info("Loading aggregator beans");
            contexts.add("config/fuser/beans.aggregate.xml");
        }

        return contexts;
    }
    
    private List<String> getFilterContexts ()
    {
        List<String> contexts = new ArrayList<> ();

        if (isLoadFilters())
        {
            log.info("Loading filter beans");
            contexts.add("config/fuser/beans.filter.xml");
        }

        return contexts;
    }

    public List<String> getRuleContexts ()
    {
        List<String> contexts = new ArrayList<String> ();
        if (isLoadRules())
        {
            log.info ("Loading rules");
            contexts.add("config/fuser/beans.rules.xml");
        }
        return contexts;
    }
    
    public List<String> getHeartbeatContexts()
    {
        List<String> contexts = new ArrayList<String> ();
        if (isLoadHeartbeat())
        {
            log.info ("Loading heartbeat");
            contexts.add("config/fuser/beans.jms.xml");
            contexts.add("config/fuser/beans.marshalling.xml");
            contexts.add("config/fuser/beans.heartbeat.xml");
            contexts.add("config/fuser/routes.heartbeat.xml");
        }
        return contexts;
    }
    
    public List<String> getDataReprocessContexts()
    {
        log.info( "Loading data reprocessing" );
        
        List<String> contexts = new ArrayList<String>();
        contexts.add( "config/fuser/beans.playback.xml" );
        contexts.add( "config/fuser/beans.capture.xml" );
        return contexts;
    }
    
    public void setLoadTransforms (boolean loadTransforms)
    {
        this.loadTransforms = loadTransforms;
    }

    public boolean isLoadTransforms ()
    {
        return loadTransforms;
    }

     public void setLoadCoreRoutes (boolean loadCoreRoutes)
    {
        this.loadCoreRoutes = loadCoreRoutes;
    }

    public boolean isLoadCoreRoutes ()
    {
        return loadCoreRoutes;
    }

    public void setLoadCoreJms (boolean loadCoreJms)
    {
        this.loadCoreJms = loadCoreJms;
    }

    public boolean isLoadCoreJms ()
    {
        return loadCoreJms;
    }

    public void setLoadCoreLogging (boolean loadCoreLogging)
    {
        this.loadCoreLogging = loadCoreLogging;
    }

    public boolean isLoadCoreLogging ()
    {
        return loadCoreLogging;
    }

    public void setLoadAggregator (boolean loadAggregator)
    {
        this.loadAggregator = loadAggregator;
    }

    public boolean isLoadAggregator ()
    {
        return loadAggregator;
    }
    
    public void setLoadFilters (boolean loadFilters)
    {
        this.loadFilters = loadFilters;
    }

    public boolean isLoadFilters ()
    {
        return loadFilters;
    }

    public boolean isLoadCoreServices() {
        return loadCoreServices;
    }

    public void setLoadCoreServices(boolean loadCoreServices) {
        this.loadCoreServices = loadCoreServices;
    }
    
    public boolean isLoadCoreServiceClients() {
        return loadCoreServiceClients;
    }
    
    public void setLoadCoreServiceClients(boolean loadCoreServiceClients) {
        this.loadCoreServiceClients = loadCoreServiceClients;
    }
    
    public boolean isLoadSecureSync()
    {
        return loadSecureSync;
    }

    public void setLoadSecureSync( boolean loadSecureSync )
    {
        this.loadSecureSync = loadSecureSync;
    }

    public void setPluginTypesToLoad( String pluginTypesToLoad )
    {
    	if( pluginTypesToLoad != null )
        {
            pluginTypesToLoad = pluginTypesToLoad.toLowerCase();
            this.pluginTypesToLoad = Arrays.asList( pluginTypesToLoad.split(",") );
        }
    }

    public boolean isLoadRules() {
        return loadRules;
    }

    public void setLoadRules(boolean loadRules) {
        this.loadRules = loadRules;
    }

    public boolean isLoadHeartbeat()
    {
        return loadHeartbeat;
    }

    public void setLoadHeartbeat(boolean loadHeartbeat)
    {
        this.loadHeartbeat = loadHeartbeat;
    }
    
    public void loadAircraftBeans(boolean loadAircraftBeans)
    {
        this.loadAircraftBeans = loadAircraftBeans;
    }
    
    public boolean isLoadAircraftBeans()
    {
        return loadAircraftBeans;
    }
    
    public void setLoadAircraftRoutes(boolean loadAircraftRoutes)
    {
        this.loadAircraftRoutes = loadAircraftRoutes;
    }
    
    public boolean isLoadAircraftRoutes()
    {
        return loadAircraftRoutes;
    }
    
    public void setLoadAircraftServices(boolean loadAircraftServices)
    {
        this.loadAircraftServices = loadAircraftServices;
    }
    
    public boolean isLoadAircraftServices()
    {
        return loadAircraftServices;
    }
    
    public void setLoadSectorBeans(boolean loadSectorBeans)
    {
        this.loadSectorBeans = loadSectorBeans;
    }    
    
    public boolean isLoadSectorBeans()
    {
        return loadSectorBeans;
    }  
    
    public void setLoadSectorRoutes(boolean loadSectorRoutes)
    {
        this.loadSectorRoutes = loadSectorRoutes;
    } 
    
    public boolean isLoadSectorRoutes()
    {
        return loadSectorRoutes;
    }     
    
    public void setLoadSectorServices(boolean loadSectorServices)
    {
        this.loadSectorServices = loadSectorServices;
    }
    
    public boolean isLoadSectorServices()
    {
        return loadSectorServices;
    }   
}
