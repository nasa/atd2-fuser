package com.mosaicatm.fuser.batch;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;

public class AircraftBatcher
extends AbstractBatcher<MatmAircraft, MatmTransferEnvelope>
{
    private final Log log = LogFactory.getLog(getClass());

    public AircraftBatcher()
    {
        super();
    }

    @Override
    public MatmTransferEnvelope createBatchMessage(List<MatmAircraft> batch)
    {
        MatmTransferEnvelope envelope = null;
        
        if(!batch.isEmpty())
        {
            //do something with the sendBatch
            envelope = new MatmTransferEnvelope();
            envelope.setAircraft(batch);
        }
        
        return envelope;
    }
}
