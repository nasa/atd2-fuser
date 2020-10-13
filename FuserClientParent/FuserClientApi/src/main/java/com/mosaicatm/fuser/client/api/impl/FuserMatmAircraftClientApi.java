package com.mosaicatm.fuser.client.api.impl;

import java.util.List;
import java.util.ListIterator;

import javax.activation.DataHandler;

import org.apache.camel.CamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.util.SyncPoint;
import com.mosaicatm.fuser.services.client.FuserSyncService;
import com.mosaicatm.lib.util.concurrent.Handler;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;

public class FuserMatmAircraftClientApi
extends AbstractFuserClientApi<MatmAircraft>
{
    private final Log log = LogFactory.getLog(getClass());

    private Handler<MatmAircraft> syncHandler;
    private FuserSyncService syncService;
    private SyncPoint syncPoint;
    private String syncServiceOverrideDataSource = null;

    //Putting this here so we can start the routes when the start method is called
    private CamelContext camelContext;

    private void startCamelContext(CamelContext context)
    {
        if(context != null)
        {
            log.info("Starting core camel context");

            try 
            {
                context.getRouteController().startAllRoutes();
                log.info("Routes sucessfully started");
            } 
            catch (Exception e) 
            {
                log.error("Error trying to start routes." ,e);
            }
        }
    }

    @Override
    public void start(boolean sync)
    {
        log.info("Start method called on the api, sync=" + sync);
        //The routes are set to autoStartup=false to avoid messages coming through
        //before the api is fully loaded and wired together

        startCamelContext(camelContext);

        if (sync && syncService != null && syncHandler != null)
        {
            long syncStart = System.currentTimeMillis();
            log.info("Syncing fuser aircraft data...");
            DataHandler dataHandler = syncService.getAircraftInAttachment();

            MatmTransferEnvelope matmTransferEnv = processDataHandler( dataHandler );

            if( matmTransferEnv != null )
            {
                List<MatmAircraft> aircraftList = matmTransferEnv.getAircraft();

                if (aircraftList != null)
                {
                    log.info("Received " + aircraftList.size() + " aircraft from the fuser sync service");
                    String syncSource = getSyncServiceOverrideDataSource();

                    // Iterate over the list backwards (to improve removal performance)
                    // Remove items from the list as we go, for memory savings
                    ListIterator<MatmAircraft> iter = aircraftList.listIterator( aircraftList.size() );

                    while( iter.hasPrevious() )
                    {
                        MatmAircraft aircraft = iter.previous();
                        iter.remove();

                        if( aircraft != null )
                        {
                            if( syncSource != null && !syncSource.trim().isEmpty() )
                            {
                                aircraft.setLastUpdateSource( syncSource );
                                aircraft.setSystemId( syncSource );
                            }

                            // the sync handler will force the aircraft through the same update
                            // process as if the message had been received from the fuser routing
                            syncHandler.handle(aircraft);
                        }
                    }
                }
            }

            log.info("...sync complete, total time: " + (System.currentTimeMillis() - syncStart) + " ms");
            log.info("Added " + this.getStore().size() + " aircraft to the store");
        }

        if(syncPoint != null)
        {
            log.info("Sync point, unlocked. Data should now be processed coming from routes");
            syncPoint.unlock();
        }
        
        unlockPublishers();
        
        fuserSyncCompleteEventManager.syncComplete();
    }

    @Override
    public MatmTransferEnvelope generateEnvelope(List<MatmAircraft> batch)
    {
        MatmTransferEnvelope envelope = null;

        if (batch != null && !batch.isEmpty())
        {
            envelope = new MatmTransferEnvelope();
            envelope.setAircraft(batch);
        }

        return envelope;
    }

    public FuserSyncService getSyncService() {
        return syncService;
    }

    public void setSyncService(FuserSyncService syncService) {
        this.syncService = syncService;
    }

    public SyncPoint getSyncPoint() {
        return syncPoint;
    }

    public void setSyncPoint(SyncPoint syncPoint) {
        this.syncPoint = syncPoint;
    }

    public String getSyncServiceOverrideDataSource()
    {
        return syncServiceOverrideDataSource;
    }

    public void setSyncServiceOverrideDataSource( String syncServiceOverrideDataSource )
    {
        if(( syncServiceOverrideDataSource != null ) && ( syncServiceOverrideDataSource.trim().isEmpty() ))
            syncServiceOverrideDataSource = null;

        this.syncServiceOverrideDataSource = syncServiceOverrideDataSource;
    }

    public void setCamelContext(CamelContext camelContext) 
    {
        this.camelContext = camelContext;
    }

    public void setSyncHandler(Handler<MatmAircraft> syncHandler) 
    {
        this.syncHandler = syncHandler;
    }

}
