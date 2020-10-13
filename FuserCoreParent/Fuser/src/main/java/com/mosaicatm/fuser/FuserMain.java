package com.mosaicatm.fuser;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mosaicatm.lib.spring.RuntimeConfigurer;
import com.mosaicatm.lib.spring.RuntimeProperties;

public class FuserMain
{
    private final Log log = LogFactory.getLog(getClass());

    public FuserMain ()
    {
        super ();
    }

    public void load (Properties props)
    {        
        FuserLoader loader = new FuserLoader ();
        
        // Loading CORE
        loader.setLoadCoreJms(Boolean.parseBoolean(props.getProperty("fuser.core.loadJms")));
        loader.setLoadCoreRoutes(Boolean.parseBoolean(props.getProperty("fuser.core.loadRoutes")));
        loader.setLoadCoreLogging(Boolean.parseBoolean(props.getProperty("fuser.core.loadLogging")));
        loader.setLoadCoreServices(Boolean.parseBoolean(props.getProperty("fuser.core.loadServices")));
        loader.setLoadCoreServiceClients(Boolean.parseBoolean(props.getProperty("fuser.core.loadServiceClients")));
        loader.setLoadSecureSync(Boolean.parseBoolean(props.getProperty("fuser.core.loadSecureSync")));
        
        loader.setLoadTransforms(Boolean.parseBoolean(props.getProperty("fuser.loadTransforms")));
        loader.setLoadAggregator(Boolean.parseBoolean(props.getProperty("fuser.loadAggregator")));
        loader.setLoadFilters(Boolean.parseBoolean(props.getProperty("fuser.loadFilters")));
        loader.setLoadRedis(Boolean.parseBoolean(props.getProperty("fuser.redis.enabled")));
        loader.setLoadRules(Boolean.parseBoolean(props.getProperty("fuser.core.loadRules")));
        
        // Loading Heartbeat
        loader.setLoadHeartbeat(Boolean.parseBoolean(props.getProperty("fuser.heartbeat.active")));
        
        // Loading Aircraft
        loader.loadAircraftBeans(Boolean.parseBoolean(props.getProperty("fuser.aircraft.loadBeans")));
        loader.setLoadAircraftRoutes(Boolean.parseBoolean(props.getProperty("fuser.aircraft.loadRoutes")));
        loader.setLoadAircraftServices(Boolean.parseBoolean(props.getProperty("fuser.aircraft.loadServices")));

        // Loading Sector
        loader.setLoadSectorBeans(Boolean.parseBoolean(props.getProperty("fuser.sector.loadBeans")));
        loader.setLoadSectorRoutes(Boolean.parseBoolean(props.getProperty("fuser.sector.loadRoutes")));
        loader.setLoadSectorServices(Boolean.parseBoolean(props.getProperty("fuser.sector.loadServices")));
        
        loader.setPluginTypesToLoad( props.getProperty( "fuser.pluginTypesToLoad" ) );
        
        loader.load();
    }

    public Properties getProperties (String[] environements)
    throws IOException
    {
        Properties props = null;

        if (environements != null)
        {
            if (environements.length > 0)
            {
                File file = null;
                RuntimeProperties properties = null;
                RuntimeProperties allProperties = null;
                for (String runtimeProps : environements)
                {
                    file = new File(runtimeProps);
                    
                    if (file.isFile())
                    {
                        log.info ("Runtime properties location: " + runtimeProps);
                        properties = RuntimeProperties.findProperties(runtimeProps);
                    }
                    else
                    {
                        log.info ("Runtime properties location: " + runtimeProps);
                        properties = RuntimeProperties.findProperties(runtimeProps, "env/fuser/");
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
                }
            }
        }

        ApplicationContext ctx = new ClassPathXmlApplicationContext("config/fuser/beans.properties.xml");
        RuntimeConfigurer configurer = (RuntimeConfigurer)ctx.getBean("fuser.propertyConfigurer");
        props = configurer.getProperties();

        props.list(System.out);

        return props;
    }

    public static void main (String[] args)
    {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

        FuserMain frm = new FuserMain ();

        try
        {
            Properties props = frm.getProperties(args);
            frm.load(props);

            System.out.println("loaded!");
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

}
