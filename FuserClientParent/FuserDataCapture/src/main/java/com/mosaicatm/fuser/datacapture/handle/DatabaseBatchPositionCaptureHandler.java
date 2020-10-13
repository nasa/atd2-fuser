package com.mosaicatm.fuser.datacapture.handle;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.database.bulk.BulkLoader;
import com.mosaicatm.lib.util.concurrent.Handler;
import com.mosaicatm.matmdata.position.MatmPositionUpdate;
import com.mosaicatm.matmdata.positionenvelope.MatmPositionUpdateEnvelope;

public class DatabaseBatchPositionCaptureHandler
implements Handler<MatmPositionUpdate>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private BulkLoader<MatmPositionUpdate> bulkLoader;

    @Override
    public void handle(MatmPositionUpdate data)
    {
        if (data != null)
        {
            if (bulkLoader != null && bulkLoader.isActive())
                bulkLoader.insert(data);
        }
    }
    
    public List<MatmPositionUpdate> getFlights(MatmPositionUpdateEnvelope envelope)
    {
        if (envelope != null && envelope.getFlights() != null)
        {
        	return envelope.getFlights();
        }
        
        return null;
    }

    @Override
    public void onShutdown()
    {
        log.info("Stopping database batch capture handler");
    }

    public void setBulkLoader(BulkLoader<MatmPositionUpdate> bulkLoader)
    {
        this.bulkLoader = bulkLoader;
    }
}
