package com.mosaicatm.fuser.sector;

import java.util.List;
import java.util.Arrays;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;

import com.mosaicatm.fuser.common.matm.util.sector.MatmSectorAssignmentIdLookup;
import com.mosaicatm.fuser.store.SectorAssignmentDataStore;
import com.mosaicatm.fuser.store.matm.MatmSectorAssignmentFuserStore;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class SectorAssignmentUpdaterTest
{
    private static SectorAssignmentUpdater updater;
    private static SectorAssignmentDataStore dataStore;

    @BeforeClass
    public static void init()
    {
        dataStore = new MatmSectorAssignmentFuserStore(1);
        ((MatmSectorAssignmentFuserStore)dataStore).setIdLookup(new MatmSectorAssignmentIdLookup());

        updater = new SectorAssignmentUpdater();
        updater.setSectorAssignmentDataStore( dataStore );      
    }
    
    @Before
    public void initTest()
    {
        dataStore.clear();
    }    

    @Test
    public void test()
    {
        List<String> favs = Arrays.asList( "ZJX3301", "ZJX3300", "ZJX4300" );
        
        MatmSectorAssignment update = new MatmSectorAssignment();
        update.setSectorName("ZJX99");
        update.setSourceFacility("ZJX");
        update.setFixedAirspaceVolumeList( favs );
        
        assertFalse( dataStore.dynamicSectorAssignmentsExist( "ZJX" ));
        assertNull( dataStore.get( "ZJX33" ));
        assertNull( dataStore.get( "ZJX99" ));
        
        updater.process( update );
        
        assertTrue( dataStore.dynamicSectorAssignmentsExist( "ZJX" ));
        assertNull( dataStore.get( "ZJX33" ));
        assertNotNull( dataStore.get( "ZJX99" ));                
        assertEquals( favs, dataStore.get( "ZJX99" ).getFixedAirspaceVolumeList() );
    }    
}
