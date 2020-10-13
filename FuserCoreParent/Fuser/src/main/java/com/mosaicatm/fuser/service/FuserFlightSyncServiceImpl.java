package com.mosaicatm.fuser.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlMimeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.mosaicatm.fuser.services.client.FuserFlightSyncService;
import com.mosaicatm.fuser.services.client.FuserSyncRequest;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.lib.util.concurrent.SyncPoint;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flightcomposite.MatmFlightComposite;

public class FuserFlightSyncServiceImpl
implements FuserFlightSyncService
{
    private final Log log = LogFactory.getLog(getClass());

    private FuserStore<MatmFlight, MetaData> store;
    private SyncPoint syncPoint;
    private GenericMarshaller matmEnvelopeMarshaller;

    
    public FuserFlightSyncServiceImpl ()
    {
        super ();
    }
    
    public FuserFlightSyncServiceImpl (FuserStore<MatmFlight, MetaData> store)
    {
        this.store = store;
    }

    @Override
    public List<MatmFlight> getFlights()
    {
        return getFlightsRequest( null );
    }

    @XmlMimeType("application/octet-stream")
    @Override
    public DataHandler getFlightsInAttachment()
    {
        return getFlightsInAttachmentRequest( null );
    }

    @Override
    public List<MatmFlight> getFlightsRequest( FuserSyncRequest fuserSyncRequest )
    {
        List<MatmFlight> flights = null;
        FuserSyncFilter fuserSyncFilter = null;
        if( fuserSyncRequest != null )
        {
            fuserSyncFilter = new FuserSyncFilter();
            fuserSyncFilter.setAirportFilterActive( fuserSyncRequest.getAirportFilterActive() );
            fuserSyncFilter.setSurfaceFilterActive( fuserSyncRequest.getSurfaceFilterActive() );
            fuserSyncFilter.setAirports( fuserSyncRequest.getAirports() );
        }

        // This sync point is intended when running in the fuser jr mode, where
        // the service should be locked until the sync with fuser sr is complete
        log.info("Waiting for sync service to unlock...");
        syncPoint.sync(this);
        log.info("...Sync service unlocked");

        long startStoreLock = 0;
        try
        {
            store.lockEntireStore();

            startStoreLock = System.currentTimeMillis();

            Collection<MatmFlight> storeFlights = store.getAll();

            if (storeFlights != null)
            {
                flights = new ArrayList<>();

                for (MatmFlight storeFlight : storeFlights)
                {
                    if( fuserSyncFilter == null ||
                            fuserSyncFilter.doFilter( storeFlight ) )
                    {
                        MatmFlight copy = new MatmFlight();
                        storeFlight.copyTo(copy);

                        copy.setLastUpdateSource(FuserSource.SYNC.name());
                        copy.setSystemId(FuserSource.SYNC.name());

                        flights.add(copy);
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("Error retrieving sync flights from store", e);
        }
        finally
        {
            store.unlockEntireStore();
            if( log.isDebugEnabled() )
            {
                long storeLockDuration = System.currentTimeMillis() - startStoreLock;
                log.debug( "getFlights: store locked for: " + storeLockDuration + " ms" );
            }
        }

        return flights;
    }

    @XmlMimeType("application/octet-stream")
    @Override
    public DataHandler getFlightsInAttachmentRequest( FuserSyncRequest fuserSyncRequest )
    {
        DataHandler dataHandler = null;
        List<MatmFlight> flights = getFlightsRequest( fuserSyncRequest );

        MatmTransferEnvelope envelope = new MatmTransferEnvelope();
        envelope.setFlights( flights );

        FuserSyncServiceTransformUtil fuserSyncServiceTransformUtil = new FuserSyncServiceTransformUtil();
        dataHandler = fuserSyncServiceTransformUtil.matmTransferEnvelopeToCompressedByteData(
                envelope, matmEnvelopeMarshaller );

        return dataHandler;
    }
    
    @Override
    public List<MatmFlight> getFlightsByAirport(String airport)
    {
        FuserSyncRequest fuserSyncRequest = new FuserSyncRequest();
        if( StringUtils.hasText( airport ) )
        {
            fuserSyncRequest.setAirports( Arrays.asList( airport.trim() ) );
            fuserSyncRequest.setAirportFilterActive( true );
            fuserSyncRequest.setSurfaceFilterActive( true );
        }
        
        return getFlightsRequest( fuserSyncRequest );
    }

    @Override
    public List<MatmFlightComposite> getCompositeFlights()
    {       
        return generateCompositeFlights(getFlightsRequest( null ));
    }
    
    @Override
    public List<MatmFlightComposite> getCompositeFlightsRequest( FuserSyncRequest fuserSyncRequest )
    {
        return generateCompositeFlights( getFlightsRequest( fuserSyncRequest ) );
    }
    
    @Override
    public List<MatmFlightComposite> getCompositeFlightsByAirport(String airport)
    {       
        return generateCompositeFlights(getFlightsByAirport(airport));
    }
    
    private List<MatmFlightComposite> generateCompositeFlights (Collection<MatmFlight> flights)
    {
        List<MatmFlightComposite> composites = new ArrayList<>();
        
        if (flights != null && !flights.isEmpty())
        {
            try
            {
                store.lockEntireStore();
                
                MatmFlightComposite composite = null;
                List<MetaData> metaData = null;
                MetaData copy = null;
                
                for (MatmFlight flight : flights)
                {
                    if (flight == null)
                        continue;
                    
                    composite = new MatmFlightComposite();
                    composite.setFlight(flight);
                    
                    Collection<MetaData> meta = store.getAllMetaData(flight);
                    if (meta != null)
                    {
                        metaData = new ArrayList<>();
                        
                        for (MetaData m : meta)
                        {
                            copy = new MetaData();
                            m.copyTo(copy);
                            
                            metaData.add(copy);
                        }
                        
                        composite.setMetaData(metaData);
                    }
                    
                    composites.add(composite);
                }
            }
            catch (Exception e)
            {
                log.error("Error converting to composite matm flight", e);
            }
            finally
            {
                store.unlockEntireStore();
            }
        }
        
        return composites;
    }
    
    @Override
    public int getFlightCount ()
    {
        int count = 0;
        
        try
        {
            syncPoint.sync(this);
            store.lockEntireStore();
            count = store.size();
        }
        catch (Exception e)
        {
            log.error("Error retrieving store size", e);
        }
        finally
        {
            store.unlockEntireStore();
        }
        
        return count;
    }
    
    @Override
    public int getFlightCountRequest( FuserSyncRequest fuserSyncRequest )
    {
        List<MatmFlight> flights = getFlightsRequest( fuserSyncRequest );
        
        return flights == null ? 0 : flights.size();
    }
    
    @Override
    public int getFlightCountByAirport (String airport)
    {
        int count = 0;
        
        syncPoint.sync(this);
        
        List<MatmFlight> flights = getFlightsByAirport (airport);
        
        if (flights != null)
            count = flights.size();
        
        return count;
    }
    
    public void setStore (FuserStore<MatmFlight, MetaData> store)
    {
        this.store = store;
    }

    public void setSyncPoint (SyncPoint syncPoint)
    {
        this.syncPoint = syncPoint;
    }

    public void setMatmEnvelopeMarshaller( GenericMarshaller matmEnvelopeMarshaller )
    {
        this.matmEnvelopeMarshaller = matmEnvelopeMarshaller;
    }
}
