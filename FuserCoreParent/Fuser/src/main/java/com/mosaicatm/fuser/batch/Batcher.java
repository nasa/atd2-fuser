package com.mosaicatm.fuser.batch;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class Batcher
extends AbstractBatcher<MatmFlight, MatmTransferEnvelope>
{
    private final Log log = LogFactory.getLog(getClass());

    public Batcher()
    {
        super();
    }

    @Override
    public MatmTransferEnvelope createBatchMessage(List<MatmFlight> batch)
    {
        MatmTransferEnvelope envelope = null;
        
        if(!batch.isEmpty())
        {
            //do something with the sendBatch
            envelope = new MatmTransferEnvelope();
            envelope.setFlights(batch);
        }
        
        return envelope;
    }
}
