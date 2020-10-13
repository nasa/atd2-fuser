package com.mosaicatm.fuser.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlMimeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.services.client.FuserAircraftSyncService;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.lib.util.concurrent.SyncPoint;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.aircraftcomposite.MatmAircraftComposite;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;

public class FuserAircraftSyncServiceImpl
implements FuserAircraftSyncService
{
    private final Log log = LogFactory.getLog(getClass());

    private FuserStore<MatmAircraft, MetaData> store;
    private SyncPoint syncPoint;
    private GenericMarshaller matmEnvelopeMarshaller;

    @Override
    public List<MatmAircraft> getAircraft()
    {
        List<MatmAircraft> aircraftList = null;
        
        // This sync point is intended when running in the fuser jr mode, where
        // the service should be locked until the sync with fuser sr is complete
        log.info("Waiting for sync service to unlock...");
        syncPoint.sync(this);
        log.info("...Sync service unlocked");
        
        try
        {
            store.lockEntireStore();
            Collection<MatmAircraft> storeAircraftList = store.getAll();
            
            if (storeAircraftList != null)
            {
                aircraftList = new ArrayList<>();
                
                for (MatmAircraft storeAircraft : storeAircraftList)
                {
                    MatmAircraft copy = new MatmAircraft();
                    storeAircraft.copyTo(copy);
                    
                    copy.setLastUpdateSource(FuserSource.SYNC.name());
                    copy.setSystemId(FuserSource.SYNC.name());
                    
                    aircraftList.add(copy);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Error retrieving sync aircraft from store", e);
        }
        finally
        {
            store.unlockEntireStore();
        }
        
        return aircraftList;
    }

    @XmlMimeType("application/octet-stream")
    public DataHandler getAircraftInAttachment()
    {
        DataHandler dataHandler = null;
        List<MatmAircraft> aircraftList = getAircraft();

        MatmTransferEnvelope envelope = new MatmTransferEnvelope();
        envelope.setAircraft( aircraftList );

        FuserSyncServiceTransformUtil fuserSyncServiceTransformUtil = new FuserSyncServiceTransformUtil();
        dataHandler = fuserSyncServiceTransformUtil.matmTransferEnvelopeToCompressedByteData(
                envelope, matmEnvelopeMarshaller );

        return dataHandler;
    }

    @Override
    public List<MatmAircraftComposite> getCompositeAircraft()
    {
        return generateCompositeAircraft(getAircraft());
    }
    
    private List<MatmAircraftComposite> generateCompositeAircraft (Collection<MatmAircraft> aircraftList)
    {
        List<MatmAircraftComposite> composites = new ArrayList<>();
        
        if (aircraftList != null && !aircraftList.isEmpty())
        {
            try
            {
                store.lockEntireStore();
                
                MatmAircraftComposite composite = null;
                List<MetaData> metaData = null;
                MetaData copy = null;
                
                for (MatmAircraft aircraft : aircraftList)
                {
                    if (aircraft == null)
                        continue;
                    
                    composite = new MatmAircraftComposite();
                    composite.setAircraft(aircraft);
                    
                    Collection<MetaData> meta = store.getAllMetaData(aircraft);
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
                log.error("Error converting to composite matm aircraft", e);
            }
            finally
            {
                store.unlockEntireStore();
            }
        }
        
        return composites;
    }

    @Override
    public int getAircraftCount()
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
            log.error("Error retrieving aircraft store size", e);
        }
        finally
        {
            store.unlockEntireStore();
        }
        
        return count;
    }

    public void setStore (FuserStore<MatmAircraft, MetaData> store)
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
