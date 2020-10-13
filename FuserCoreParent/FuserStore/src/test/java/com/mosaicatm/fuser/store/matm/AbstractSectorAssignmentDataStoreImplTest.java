package com.mosaicatm.fuser.store.matm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;

import com.mosaicatm.matmdata.sector.MatmSectorAssignment;
import com.mosaicatm.sector.geometry.utils.SectorUtils;
import com.mosaicatm.fuser.store.SectorAssignmentDataStore;
import org.junit.Ignore;

public abstract class AbstractSectorAssignmentDataStoreImplTest
{
    abstract public SectorAssignmentDataStore getDataStore();

    @Test
    public void test()
    {
        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZAB04", 
        		Arrays.asList("ZAB0301", "ZAB0302", "ZAB0401", "ZAB0402", "ZAB0403")));
        
        assertTrue( getDataStore().dynamicSectorAssignmentsExist( "ZAB" ));
        assertFalse( getDataStore().dynamicSectorAssignmentsExist( "ZDV" ));

        assertEquals("ZAB04", getDataStore().getDynamicSectorForModule("ZAB00301M1"));
        assertEquals("ZAB04", getDataStore().getDynamicSectorForModule("ZAB0301M1"));
        
        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZAB05", 
        		Arrays.asList("ZAB0301", "ZAB0302", "ZAB0401", "ZAB0402", "ZAB0403")));    
        
        assertNotNull(getDataStore().get( "ZAB04" ));
        assertNotNull(getDataStore().get( "ZAB05" ));
        assertEquals("ZAB05", getDataStore().getDynamicSectorForModule("ZAB0301M1"));
        assertEquals("ZAB05", getDataStore().getDynamicSectorForModule("ZAB0403M1"));
        
        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZAB04", null));
        
        assertNull(getDataStore().get( "ZAB04" ));
        assertNotNull(getDataStore().get( "ZAB05" ));        
        assertEquals("ZAB05", getDataStore().getDynamicSectorForModule("ZAB0301M1"));
        assertEquals("ZAB05", getDataStore().getDynamicSectorForModule("ZAB0403M1"));      
        
        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZAB05", 
        		Arrays.asList("ZAB0301")));          
        
        assertNull(getDataStore().getDynamicSectorForModule("ZAB0403M1")); 
    }
    
    @Test
    public void sectorZTL4201M1Test()
    {
        assertNull(getDataStore().getDynamicSectorForModule("ZTL4201M1")); 
        
        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZTL46", 
        		Arrays.asList("ZTL4601", "ZTL4201")));         
        
        assertEquals("ZTL46", getDataStore().getDynamicSectorForModule("ZTL4201M1"));
        
        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZTL42", null));
        
        assertEquals("ZTL46", getDataStore().getDynamicSectorForModule("ZTL4201M1"));
        
        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZTL43", 
        		Arrays.asList("ZTL4301")));          
        
        assertEquals("ZTL46", getDataStore().getDynamicSectorForModule("ZTL4201M1"));      
        
        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZTL46", 
        		Arrays.asList("ZTL4601", "ZTL4201")));         
        
        assertEquals("ZTL46", getDataStore().getDynamicSectorForModule("ZTL4201M1"));        
        
        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZTL42", Collections.emptyList()));
        
        assertEquals("ZTL46", getDataStore().getDynamicSectorForModule("ZTL4201M1"));   

        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZTL43", 
        		Arrays.asList("ZTL4201", "ZTL4301")));          
        
        assertEquals("ZTL43", getDataStore().getDynamicSectorForModule("ZTL4201M1"));      
         
        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZTL46", 
        		Arrays.asList("ZTL4601")));          
        
        assertEquals("ZTL43", getDataStore().getDynamicSectorForModule("ZTL4201M1"));                 
    }    
    
    @Ignore
    @Test
    // The stale sector closing logic was removed in favor of the timed remover
    public void staleFavRemoveTest()
    {
        assertNull( getDataStore().get( "ZME10" )); 
        assertNull( getDataStore().get( "ZME20" ));
        assertNull( getDataStore().get( "ZME30" ));
        
        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZME10", 
        		Arrays.asList("ZME0001", "ZME0002")));         
        
        assertEquals("ZME10", getDataStore().getDynamicSectorForModule("ZME0001M1"));
        assertEquals("ZME10", getDataStore().getDynamicSectorForModule("ZME0002M1"));
        
        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZME20", 
        		Arrays.asList("ZME0001")));   
        
        assertEquals("ZME20", getDataStore().getDynamicSectorForModule("ZME0001M1"));
        assertEquals("ZME10", getDataStore().getDynamicSectorForModule("ZME0002M1"));
        
        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZME30", 
        		Arrays.asList("ZME0002")));   
        
        assertNull( getDataStore().get( "ZME10" )); 
        assertEquals("ZME20", getDataStore().getDynamicSectorForModule("ZME0001M1"));
        assertEquals("ZME30", getDataStore().getDynamicSectorForModule("ZME0002M1"));
    }
    
    @Test
    public void removeTest()
    {
        assertNull( getDataStore().get( "ZME10" )); 
        assertNull( getDataStore().get( "ZME10" ));
        
        getDataStore().updateDynamicSectorFixedAirspaceVolumes(toMatmSectorAssignment("ZME10", 
        		Arrays.asList("ZME0001", "ZME0002")));
        
        getDataStore().removeByKey("ZME50" );
        
        assertNotNull( getDataStore().get( "ZME10" ));
        assertEquals("ZME10", getDataStore().getDynamicSectorForModule("ZME0001M1"));
        assertEquals("ZME10", getDataStore().getDynamicSectorForModule("ZME0002M1"));

        getDataStore().removeByKey( "ZME10" );
        
        assertNull( getDataStore().get( "ZME10" )); 
        assertNull( getDataStore().getDynamicSectorForModule("ZME0001M1"));
        assertNull( getDataStore().getDynamicSectorForModule("ZME0001M2"));
        
        for( MatmSectorAssignment mas : getDataStore().getAll() )
        {
            assertNotEquals( "ZME10", mas.getSectorName() );
            assertFalse( mas.getFixedAirspaceVolumeList().contains( "ZME0001M1" ));
            assertFalse( mas.getFixedAirspaceVolumeList().contains( "ZME0001M2" ));
        }
    }    
    
    protected MatmSectorAssignment toMatmSectorAssignment( String sectorName, List<String> favs )
    {
        MatmSectorAssignment sectorAssignment = new MatmSectorAssignment();
        
        sectorAssignment.setTimestamp( new Date() );
        sectorAssignment.setSourceFacility( SectorUtils.getArtcc( sectorName ));
        sectorAssignment.setSectorName( sectorName );
        
        if( favs != null )
        {
            sectorAssignment.getFixedAirspaceVolumeList().addAll( favs );
        }
        
        return( sectorAssignment );
    }
}
