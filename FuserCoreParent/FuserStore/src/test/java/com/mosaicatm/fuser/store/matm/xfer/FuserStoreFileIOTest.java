package com.mosaicatm.fuser.store.matm.xfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.Test;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

import com.mosaicatm.fuser.common.matm.util.aircraft.MatmAircraftIdLookup;
import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightIdLookup;
import com.mosaicatm.fuser.common.matm.util.sector.MatmSectorAssignmentIdLookup;
import com.mosaicatm.fuser.common.matm.xfer.FuserRepositoryTransferFileIO;
import com.mosaicatm.fuser.store.matm.MatmAircraftFuserStore;
import com.mosaicatm.fuser.store.matm.MatmFuserStore;
import com.mosaicatm.fuser.store.matm.MatmSectorAssignmentFuserStore;
import com.mosaicatm.fuser.store.SectorAssignmentDataStore;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class FuserStoreFileIOTest 
{
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();    
    
	@Test
	public void testCacheFiles() throws Exception
	{        
        FuserStoreFileIO fileIo = new FuserStoreFileIO();
        
        MatmFuserStore flightStore = new MatmFuserStore( 1 );
        flightStore.setIdLookup( new MatmFlightIdLookup() );
        MatmAircraftFuserStore aircraftStore = new MatmAircraftFuserStore( 1 );
        aircraftStore.setIdLookup(new MatmAircraftIdLookup());        
        MatmSectorAssignmentFuserStore sectorAssignmentStore = new MatmSectorAssignmentFuserStore( 1 );
        sectorAssignmentStore.setIdLookup(new MatmSectorAssignmentIdLookup());
        
        flightStore.add( createFlight( "gufi1", "acid1" ));
        flightStore.updateMetaData( flightStore.get( "gufi1" ), createMetaData( "field1", "source1", "system1", new Date( 1000 )));
        
        flightStore.add( createFlight( "gufi2", "acid2" ));
        flightStore.updateMetaData( flightStore.get( "gufi2" ), createMetaData( "field2", "source2", "system2", new Date( 2000 )));
        
        aircraftStore.add( createAircraft(UUID.randomUUID().toString(), "A737"));
        aircraftStore.add( createAircraft(UUID.randomUUID().toString(), "A777"));
        
        MatmSectorAssignment sectorAssignment = new MatmSectorAssignment();        
        sectorAssignment.setSourceFacility( "ZOB" );
        sectorAssignment.setSectorName( "ZOB48" );
        sectorAssignment.setFixedAirspaceVolumeList( Arrays.asList( "ZOB4801", "ZOB4901" ));                        
        sectorAssignmentStore.updateDynamicSectorFixedAirspaceVolumes( sectorAssignment );
        
        String filePostfix = "20190105_0000";
        String tempFolder = testFolder.newFolder( "cacheFiles" ).getAbsolutePath();        
        File file = FuserRepositoryTransferFileIO.getDefaultCacheFile( tempFolder, filePostfix );
        
        fileIo.writeFuserStoreToXmlCache( flightStore, aircraftStore, sectorAssignmentStore, file );
        
        MatmFuserStore flightStore2 = new MatmFuserStore( 5 );
        flightStore2.setIdLookup( new MatmFlightIdLookup() );
        MatmAircraftFuserStore aircraftStore2 = new MatmAircraftFuserStore( 1 );
        aircraftStore2.setIdLookup(new MatmAircraftIdLookup());        
        MatmSectorAssignmentFuserStore sectorAssignmentStore2 = new MatmSectorAssignmentFuserStore( 1 );
        sectorAssignmentStore2.setIdLookup(new MatmSectorAssignmentIdLookup());        
        
        fileIo.loadFuserStoreFromXmlCache( flightStore2, aircraftStore2, sectorAssignmentStore2, file );
        
        assertFalse( flightStore.getAll().isEmpty() );
        assertFalse( flightStore2.getAll().isEmpty() );
        assertFalse( aircraftStore.getAll().isEmpty() );
        assertFalse( aircraftStore2.getAll().isEmpty() );        
        assertFalse( aircraftStore2.getAll().isEmpty() );  
        assertFalse( sectorAssignmentStore.getAll().isEmpty() );  
        assertFalse( sectorAssignmentStore2.getAll().isEmpty() );  
        
        assertEquals( flightStore.getAll(), flightStore2.getAll() );
        assertEquals( aircraftStore.getAll(), aircraftStore2.getAll() );
        assertEquals( sectorAssignmentStore.getAll(), sectorAssignmentStore2.getAll() );  
        
        for( MatmFlight flight : flightStore.getAll() )
        {       
            ArrayList<MetaData> list1 = new ArrayList<>( flightStore.getAllMetaData( flight ));
            ArrayList<MetaData> list2 = new ArrayList<>( flightStore2.getAllMetaData( flight ));
            
            assertFalse( list1.isEmpty() );  
            assertFalse( list2.isEmpty() );  
            
            for( int i = 0; i < list1.size(); i++ )
            {
                assertEquals( list1.get( i ).getFieldName(), list2.get( i ).getFieldName() );
                assertEquals( list1.get( i ).getSource(), list2.get( i ).getSource() );
                assertEquals( list1.get( i ).getSystemType(), list2.get( i ).getSystemType() );
                assertEquals( list1.get( i ).getTimestamp(), list2.get( i ).getTimestamp() );
            }
        }
        
        for( MatmSectorAssignment sector : sectorAssignmentStore.getAll() )
        {  
            assertEquals( sectorAssignmentStore.get( sector.getSectorName()), 
                    sectorAssignmentStore2.get( sector.getSectorName()) );  
        }        
    }    
	
    private MetaData createMetaData (String field, String source, String system, Date date )
    {
        MetaData meta = new MetaData ();

        meta.setFieldName( field );
        meta.setSource( source );
        meta.setSystemType( system );
        meta.setTimestamp( date );

        return meta;
    }     
    
    private MatmFlight createFlight (String gufi, String acid)
    {
        MatmFlight flight = new MatmFlight ();

        flight.setGufi(gufi);
        flight.setAcid(acid);

        return flight;
    }        
    
    private MatmAircraft createAircraft (String tail, String acType)
    {
        MatmAircraft aircraft = new MatmAircraft();
        aircraft.setRegistration(tail);
        aircraft.setType(acType);

        return aircraft;
    }    
}
