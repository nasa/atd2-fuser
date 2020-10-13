package com.mosaicatm.fuser.client.api.impl;

import java.util.List;
import java.util.ListIterator;

import javax.activation.DataHandler;

import org.apache.camel.CamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.util.SyncPoint;
import com.mosaicatm.fuser.services.client.FuserSyncRequest;
import com.mosaicatm.fuser.services.client.FuserSyncService;
import com.mosaicatm.lib.util.concurrent.Handler;
import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class FuserMatmFlightClientApi
extends AbstractFuserClientApi<MatmFlight>
{
	private final Log log = LogFactory.getLog(getClass());

	private Handler<MatmFlight> syncHandler;
	private FuserSyncService syncService;
	private SyncPoint syncPoint;
	private String syncServiceOverrideDataSource = null;
	private FuserSyncRequest fuserSyncRequest;

	//Putting this here so we can start the routes when the start method is called
	private CamelContext camelContext;

	@Override
	public void start(boolean sync)
	{
		log.info("Start method called on the api, sync=" + sync);
		//The routes are set to autoStartup=false to avoid messages coming through
		//before the api is fully loaded and wired together
		if(camelContext != null)
		{
			try {
                camelContext.getRouteController().startAllRoutes();
				log.info("Routes sucessfully started");
			} catch (Exception e) {
				log.error("Error trying to start routes." ,e);
			}
		}

		if (sync && syncService != null && syncHandler != null)
		{
		    long syncStart = System.currentTimeMillis();
			log.info("Syncing fuser flight data...");
			DataHandler dataHandler = syncService.getFlightsInAttachmentRequest( fuserSyncRequest );

			MatmTransferEnvelope matmTransferEnv = processDataHandler( dataHandler );

			if( matmTransferEnv != null )
			{
			    List<MatmFlight> flights = matmTransferEnv.getFlights();

			    if (flights != null)
			    {
			        log.info("Received " + flights.size() + " flights from the fuser sync service");
	                String syncSource = getSyncServiceOverrideDataSource();

			        // Iterate over the list backwards (to improve removal performance)
			        // Remove items from the list as we go, for memory savings
			        ListIterator<MatmFlight> iter = flights.listIterator( flights.size() );

			        while( iter.hasPrevious() )
			        {
			            MatmFlight flight = iter.previous();
			            iter.remove();

			            if( flight != null )
			            {
			                if( syncSource != null && !syncSource.trim().isEmpty() )
			                {
			                    flight.setLastUpdateSource( syncSource );
			                    flight.setSystemId( syncSource );
			                }

			                // the sync handler will force the flight through the same update
			                // process as if the message had been received from the fuser routing
			                syncHandler.handle(flight);
			            }
			        }
			    }
			}
    
			log.info("...sync complete, total time: " + (System.currentTimeMillis() - syncStart) + " ms");
			log.info("Added " + this.getStore().size() + " flights to the store");
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
	public MatmTransferEnvelope generateEnvelope(List<MatmFlight> batch)
	{
	    MatmTransferEnvelope envelope = null;
	    
	    if (batch != null && !batch.isEmpty())
	    {
	        envelope = new MatmTransferEnvelope();
	        envelope.setFlights(batch);
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

	public void setCamelContext(CamelContext camelContext) {
		this.camelContext = camelContext;
	}
	
	public void setSyncHandler(Handler<MatmFlight> syncHandler) {
	    this.syncHandler = syncHandler;
	}

    public void setFuserSyncRequest( FuserSyncRequest fuserSyncRequest )
    {
        this.fuserSyncRequest = fuserSyncRequest;
    }

}
