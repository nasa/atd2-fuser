package com.mosaicatm.fuser.batch;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class SectorAssignmentBatcher
extends AbstractBatcher<MatmSectorAssignment, MatmTransferEnvelope>
{
    private final Log log = LogFactory.getLog(getClass());

    public SectorAssignmentBatcher()
    {
        super();
    }

    @Override
    public MatmTransferEnvelope createBatchMessage(List<MatmSectorAssignment> batch)
    {
        MatmTransferEnvelope envelope = null;
        
        if(!batch.isEmpty())
        {
            envelope = new MatmTransferEnvelope();
            envelope.setSectorAssignments(batch);
        }
        
        return envelope;
    }
}
