package com.mosaicatm.fuser.store.matm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.fuser.store.FuserStoreLoader;
import com.mosaicatm.fuser.store.SectorAssignmentDataStore;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

/**
 * Utility class for loading a store.  The intent is to encapsulate all the
 * generic nuances of creating store (e.g. adding listeners, setting remove properties, etc)
 * so downstream projects don't need to worry about it
 */
public class MatmSectorAssignmentFuserStoreLoader
implements FuserStoreLoader <MatmSectorAssignment, MetaData>
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private MatmIdLookup<MatmSectorAssignment, String> idLookup;
    private int numberOfThreads = 4;

    @Override
    public SectorAssignmentDataStore loadStore ()
    {
        MatmSectorAssignmentFuserStore store = new MatmSectorAssignmentFuserStore( numberOfThreads );
        store.setIdLookup(idLookup);

        return store;
    }

    public void setIdLookup(MatmIdLookup<MatmSectorAssignment, String> idLookup)
    {
        this.idLookup = idLookup;
    }

    public void setNumberOfThreads( int numberOfThreads )
    {
        this.numberOfThreads = numberOfThreads;
    }
}
