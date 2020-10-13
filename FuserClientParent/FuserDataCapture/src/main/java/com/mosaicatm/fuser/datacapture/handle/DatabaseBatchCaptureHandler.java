package com.mosaicatm.fuser.datacapture.handle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.datacapture.DataWrapper;
import com.mosaicatm.lib.database.bulk.BulkLoader;
import com.mosaicatm.lib.util.concurrent.Handler;

public class DatabaseBatchCaptureHandler<T>
implements Handler<DataWrapper<T>>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private BulkLoader<DataWrapper<T>> bulkLoader;

    @Override
    public void handle(DataWrapper<T> wrapper)
    {
        if (wrapper != null)
        {
            if (bulkLoader != null && bulkLoader.isActive())
                bulkLoader.insert(wrapper);
        }
    }

    @Override
    public void onShutdown()
    {
        log.info("Stopping database batch capture handler");
    }

    public void setBulkLoader(BulkLoader<DataWrapper<T>> bulkLoader)
    {
        this.bulkLoader = bulkLoader;
    }
}
