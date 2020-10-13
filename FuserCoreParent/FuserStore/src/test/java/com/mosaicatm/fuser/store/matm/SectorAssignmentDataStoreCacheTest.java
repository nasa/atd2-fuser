package com.mosaicatm.fuser.store.matm;

import java.util.Arrays;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.sector.MatmSectorAssignmentIdLookup;
import com.mosaicatm.fuser.store.SectorAssignmentDataStore;

public class SectorAssignmentDataStoreCacheTest extends AbstractSectorAssignmentDataStoreImplTest
{
    private SectorAssignmentDataStore dataStore;
    private SectorAssignmentDataStore memoryStore;
    private SectorAssignmentDataStore testRedisStore;
    
    @Before
    public void init()
    {
        MatmSectorAssignmentFuserStore sectorAssignmentStore = new MatmSectorAssignmentFuserStore( 1 );
        sectorAssignmentStore.setIdLookup(new MatmSectorAssignmentIdLookup());        
                
        memoryStore = new MatmSectorAssignmentFuserStore( 1 );
        ((MatmSectorAssignmentFuserStore)memoryStore).setIdLookup(new MatmSectorAssignmentIdLookup());  
        
        testRedisStore = new MatmSectorAssignmentFuserStore( 1 );
        ((MatmSectorAssignmentFuserStore)testRedisStore).setIdLookup(new MatmSectorAssignmentIdLookup());          
        
        testRedisStore.updateDynamicSectorFixedAirspaceVolumes(
                toMatmSectorAssignment( "ZMA99", Arrays.asList( "ZMA5432", "ZMA5544" )));
                
        dataStore = new MatmSectorAssignmentFuserStoreCache(
                (MatmSectorAssignmentFuserStore) testRedisStore,(MatmSectorAssignmentFuserStore) memoryStore );
        ((MatmSectorAssignmentFuserStoreCache)dataStore).initialize();
    }
    
    @Test
    public void initializeTest()
    {
        assertEquals( 1, dataStore.getAll().size() );
        assertEquals( dataStore.getAll().toString(), memoryStore.getAll().toString() );
        assertEquals( dataStore.getAll().toString(), testRedisStore.getAll().toString() );
    }
    
    @Override
    public SectorAssignmentDataStore getDataStore()
    {
        return( dataStore );
    }
}
