package com.mosaicatm.fuser.client.api.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import javax.activation.DataHandler;

import org.apache.camel.CamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.util.SyncPoint;
import com.mosaicatm.fuser.services.client.FuserSyncService;
import com.mosaicatm.lib.util.concurrent.Handler;
import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class FuserMatmSectorAssignmentClientApi
extends AbstractFuserClientApi<MatmSectorAssignment>
{
    private final Log log = LogFactory.getLog(getClass());

    private Handler<MatmSectorAssignment> syncHandler;
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
            log.info("Syncing fuser sector assignment data...");
            DataHandler dataHandler = syncService.getSectorAssignmentsInAttachment();

            MatmTransferEnvelope matmTransferEnv = processDataHandler( dataHandler );

            if( matmTransferEnv != null )
            {
                List<MatmSectorAssignment> sectorAssignmentList = matmTransferEnv.getSectorAssignments();

                if (sectorAssignmentList != null)
                {
                    log.info("Received " + sectorAssignmentList.size() + " sector assignments from the fuser sync service");
                    String syncSource = getSyncServiceOverrideDataSource();
                    
                    // Sort the results by timestamp to ensure they are handled in the correct order
                    Comparator<MatmSectorAssignment> compareByTimestamp = 
                            (MatmSectorAssignment o1, MatmSectorAssignment o2) -> o1.getTimestamp().compareTo( o2.getTimestamp() );

                    // Using reverse sort order (see next comment)
                    Collections.sort( sectorAssignmentList, compareByTimestamp.reversed() );                       
                    
                    // Iterate over the list backwards (to improve removal performance)
                    // Remove items from the list as we go, for memory savings
                    ListIterator<MatmSectorAssignment> iter = sectorAssignmentList.listIterator( sectorAssignmentList.size() );

                    while( iter.hasPrevious() )
                    {
                        MatmSectorAssignment sector = iter.previous();
                        iter.remove();

                        if( sector != null )
                        {
                            if( syncSource != null && !syncSource.trim().isEmpty() )
                            {
                                sector.setLastUpdateSource( syncSource );
                                sector.setSystemId( syncSource );
                            }

                            // the sync handler will force the sector assignments through the same update
                            // process as if the message had been received from the fuser routing
                            syncHandler.handle(sector);
                        }
                    }
                }
            }

            log.info("...sync complete, total time: " + (System.currentTimeMillis() - syncStart) + " ms");
            log.info("Added " + this.getStore().size() + " sector assignments to the store");
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
    public MatmTransferEnvelope generateEnvelope(List<MatmSectorAssignment> batch)
    {
        MatmTransferEnvelope envelope = null;

        if (batch != null && !batch.isEmpty())
        {
            envelope = new MatmTransferEnvelope();
            envelope.setSectorAssignments(batch);
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

    public void setSyncHandler(Handler<MatmSectorAssignment> syncHandler) 
    {
        this.syncHandler = syncHandler;
    }

}
