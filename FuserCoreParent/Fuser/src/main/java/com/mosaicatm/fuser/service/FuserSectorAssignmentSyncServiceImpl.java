package com.mosaicatm.fuser.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlMimeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.services.client.FuserSectorAssignmentSyncService;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.lib.util.concurrent.SyncPoint;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class FuserSectorAssignmentSyncServiceImpl
implements FuserSectorAssignmentSyncService
{
    private final Log log = LogFactory.getLog(getClass());

    private FuserStore<MatmSectorAssignment, MetaData> store;
    private SyncPoint syncPoint;
    private GenericMarshaller matmEnvelopeMarshaller;

    @Override
    public List<MatmSectorAssignment> getSectorAssignments()
    {
        List<MatmSectorAssignment> sectorList = null;
        
        // This sync point is intended when running in the fuser jr mode, where
        // the service should be locked until the sync with fuser sr is complete
        log.info("Waiting for sync service to unlock...");
        syncPoint.sync(this);
        log.info("...Sync service unlocked");
        
        try
        {
            store.lockEntireStore();
            Collection<MatmSectorAssignment> storeSectorList = store.getAll();
            
            if (storeSectorList != null)
            {
                sectorList = new ArrayList<>();
                
                for (MatmSectorAssignment storeSector : storeSectorList)
                {
                    MatmSectorAssignment copy = new MatmSectorAssignment();
                    storeSector.copyTo(copy);
                    
                    copy.setLastUpdateSource(FuserSource.SYNC.name());
                    copy.setSystemId(FuserSource.SYNC.name());
                    
                    sectorList.add(copy);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Error retrieving sync sector assignments from store", e);
        }
        finally
        {
            store.unlockEntireStore();
        }
        
        return sectorList;
    }

    @XmlMimeType("application/octet-stream")
    @Override
    public DataHandler getSectorAssignmentsInAttachment()
    {
        DataHandler dataHandler = null;
        List<MatmSectorAssignment> sectorList = getSectorAssignments();

        MatmTransferEnvelope envelope = new MatmTransferEnvelope();
        envelope.setSectorAssignments(sectorList );

        FuserSyncServiceTransformUtil fuserSyncServiceTransformUtil = new FuserSyncServiceTransformUtil();
        dataHandler = fuserSyncServiceTransformUtil.matmTransferEnvelopeToCompressedByteData(
                envelope, matmEnvelopeMarshaller );

        return dataHandler;
    }

    public void setStore (FuserStore<MatmSectorAssignment, MetaData> store)
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
