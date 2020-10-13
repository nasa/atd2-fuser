package com.mosaicatm.fuser.store.matm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.FuserStoreLoader;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.common.MetaData;

/**
 * Utility class for loading a store.  The intent is to encapsulate all the
 * generic nuances of creating store (e.g. adding listeners, setting remove properties, etc)
 * so downstream projects don't need to worry about it
 */
public class MatmAircraftFuserStoreLoader
implements FuserStoreLoader <MatmAircraft, MetaData>
{
    private final Log log = LogFactory.getLog(getClass());

    private MatmIdLookup<MatmAircraft, String> idLookup;
    private int numberOfThreads = 4;

    @Override
    public FuserStore<MatmAircraft, MetaData> loadStore ()
    {
        MatmAircraftFuserStore store = new MatmAircraftFuserStore( numberOfThreads );
        store.setIdLookup(idLookup);

        return store;
    }

    public void setIdLookup(MatmIdLookup<MatmAircraft, String> idLookup)
    {
        this.idLookup = idLookup;
    }

    public void setNumberOfThreads( int numberOfThreads )
    {
        this.numberOfThreads = numberOfThreads;
    }
}
