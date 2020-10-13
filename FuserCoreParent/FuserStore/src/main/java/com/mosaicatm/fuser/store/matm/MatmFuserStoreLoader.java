package com.mosaicatm.fuser.store.matm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.FuserStoreLoader;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * Utility class for loading a store.  The intent is to encapsulate all the
 * generic nuances of creating store (e.g. adding listeners, setting remove properties, etc)
 * so downstream projects don't need to worry about it
 */
public class MatmFuserStoreLoader
implements FuserStoreLoader <MatmFlight, MetaData>
{
    private final Log log = LogFactory.getLog(getClass());

    private MatmIdLookup<MatmFlight, String> idLookup;
    private int numberOfThreads = 4;


    @Override
    public FuserStore<MatmFlight, MetaData> loadStore ()
    {
        MatmFuserStore store = new MatmFuserStore( numberOfThreads );
        store.setIdLookup(idLookup);

        return store;
    }

    public void setIdLookup(MatmIdLookup<MatmFlight, String> idLookup)
    {
        this.idLookup = idLookup;
    }

    public void setNumberOfThreads( int numberOfThreads )
    {
        this.numberOfThreads = numberOfThreads;
    }
}
