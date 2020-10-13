package com.mosaicatm.fuser.store.matm;

import org.junit.Before;

import com.mosaicatm.fuser.common.matm.util.sector.MatmSectorAssignmentIdLookup;
import com.mosaicatm.fuser.store.SectorAssignmentDataStore;

public class SectorAssignmentDataStoreImplTest extends AbstractSectorAssignmentDataStoreImplTest
{
    private SectorAssignmentDataStore dataStore;
    
    @Before
    public void init()
    {
        dataStore = new MatmSectorAssignmentFuserStore( 1 );
        ((MatmSectorAssignmentFuserStore)dataStore).setIdLookup(new MatmSectorAssignmentIdLookup());  
    }
    
    @Override
    public SectorAssignmentDataStore getDataStore()
    {
        return( dataStore );
    }
}
