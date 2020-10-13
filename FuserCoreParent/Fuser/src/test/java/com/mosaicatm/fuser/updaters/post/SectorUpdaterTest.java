package com.mosaicatm.fuser.updaters.post;

import com.mosaicatm.fuser.common.matm.util.sector.MatmSectorAssignmentIdLookup;
import java.util.Arrays;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;

import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.common.SurfaceFlightState;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.sector.geometry.dm.model.adaptation.AdaptationGeometryModel;
import com.mosaicatm.sector.geometry.model.ModelFactory;
import com.mosaicatm.sector.geometry.model.ModelFactoryInterface.ModelFactoryType;
import com.mosaicatm.sector.geometry.model.ModuleFinder;
import com.mosaicatm.sector.geometry.model.TreeModuleFinderFactory;
import com.mosaicatm.fuser.store.SectorAssignmentDataStore;
import com.mosaicatm.fuser.store.matm.MatmSectorAssignmentFuserStore;
import com.mosaicatm.matmdata.common.FavType;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class SectorUpdaterTest
{
    private static SectorUpdater updater;
    private static SectorAssignmentDataStore dataStore;

    @BeforeClass
    public static void init()
    {
        dataStore = new MatmSectorAssignmentFuserStore(1);
        ((MatmSectorAssignmentFuserStore)dataStore).setIdLookup(new MatmSectorAssignmentIdLookup());        
        
        ModelFactory modelFactory = new ModelFactory();
        modelFactory.setActive( true );
        modelFactory.setModelFactoryType(ModelFactoryType.ZIP_ARCHIVE);
        
        AdaptationGeometryModel adaptationModelFactory = modelFactory.createGeometryModel();
        
        TreeModuleFinderFactory treeModuleFinderFactory = new TreeModuleFinderFactory();
        treeModuleFinderFactory.setAdaptationGeometryModel( adaptationModelFactory );
        
        ModuleFinder moduleFinder = treeModuleFinderFactory.createInstance();
        
        updater = new SectorUpdater();
        updater.setSectorAssignmentDataStore( dataStore );
        updater.setModuleFinder( moduleFinder );        
    }
    
    @Before
    public void initTest()
    {
        dataStore.clear();
        updater.setActive( true );
    }    
    
    @Test
    public void testNullUpdates()
    {
        MatmFlight update = new MatmFlight();
             
        updater.update( update, null );

        assertNull( update.getPosition() );
        
        Position pos = new Position();
        pos.setAltitude( 0. );
        pos.setLatitude( 0. );
        pos.setLongitude( 0. );
        
        update.setPosition( pos );   
        
        updater.update( update, null );

        assertNull( update.getPosition().getFavModule() );
        assertNull( update.getPosition().getFav() );        
        assertNull( update.getPosition().getFavType() );
        assertNull( update.getPosition().getStaticSector() );
        assertNull( update.getPosition().getDynamicSector() );           
        
        update.setFuserFlightState( SurfaceFlightState.ENROUTE );
        
        updater.update( update, null );
                
        assertTrue( update.getPosition().getFavModule().isNil() );
        assertTrue( update.getPosition().getFav().isNil() );        
        assertTrue( update.getPosition().getFavType().isNil() );
        assertTrue( update.getPosition().getStaticSector().isNil() );
        assertTrue( update.getPosition().getDynamicSector().isNil() );        
    }   
    
    @Test
    public void testBasicUpdates()
    {
        MatmFlight update = new MatmFlight();
        update.setFuserFlightState( SurfaceFlightState.ENROUTE );
        
        Position pos = new Position();
        pos.setAltitude( 31000. );
        pos.setLatitude( 31.2236 );
        pos.setLongitude( -83.98 );
        
        update.setPosition( pos );   
        
        updater.setActive( false );
        updater.update( update, null );
        
        assertNull( update.getPosition().getFavModule() );
        assertNull( update.getPosition().getFav() );        
        assertNull( update.getPosition().getFavType() );   
        assertNull( update.getPosition().getStaticSector() );
        assertNull( update.getPosition().getDynamicSector() );           
        
        updater.setActive( true );
        updater.update( update, null );        
                
        assertEquals( "ZJX3300M1", update.getPosition().getFavModule().getValue() );
        assertEquals( "ZJX3300", update.getPosition().getFav().getValue() );        
        assertEquals( FavType.ENROUTE, update.getPosition().getFavType().getValue() );
        assertEquals( "ZJX33", update.getPosition().getStaticSector().getValue() );        
        assertNull( update.getPosition().getDynamicSector().getValue() );
        
        MatmSectorAssignment sectorAssignment = new MatmSectorAssignment();        
        sectorAssignment.setSourceFacility( "ZJX" );
        sectorAssignment.setSectorName( "ZJX99" );
        sectorAssignment.setFixedAirspaceVolumeList( Arrays.asList( "ZJX3301", "ZJX3300" )); 
        
        dataStore.updateDynamicSectorFixedAirspaceVolumes( sectorAssignment );
        
        updater.update( update, null );
                
        assertEquals( "ZJX3300M1", update.getPosition().getFavModule().getValue() );
        assertEquals( "ZJX3300", update.getPosition().getFav().getValue() );        
        assertEquals( FavType.ENROUTE, update.getPosition().getFavType().getValue() );
        assertEquals( "ZJX33", update.getPosition().getStaticSector().getValue() );
        assertEquals( "ZJX99", update.getPosition().getDynamicSector().getValue() );  
        
        pos.setAltitude( 9000. );
        pos.setLatitude( 30.498 );
        pos.setLongitude( -81.689 );        
        
        updater.update( update, null );
                
        assertEquals( "ZJX0024M1", update.getPosition().getFavModule().getValue() );
        assertEquals( "ZJX0024", update.getPosition().getFav().getValue() );        
        assertEquals( FavType.APPROACH, update.getPosition().getFavType().getValue() );
        assertEquals( "ZJXJAX", update.getPosition().getStaticSector().getValue() );
        assertEquals( "ZJXJAX", update.getPosition().getDynamicSector().getValue() );         
    }    
}
