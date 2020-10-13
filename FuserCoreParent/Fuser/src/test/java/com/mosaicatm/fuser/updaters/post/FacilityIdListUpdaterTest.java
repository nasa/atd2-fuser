package com.mosaicatm.fuser.updaters.post;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.common.FacilityIdType;

public class FacilityIdListUpdaterTest
{
    private FacilityIdListUpdater updater;
    
    @Before
    public void setup ()
    {
        updater = new FacilityIdListUpdater();
    }
    
    @Test
    public void testFacilityIdUpdates()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        updater.update( update, target );
        
        assertNull( update.getFacilityIdList() );

        // Facility ID is required
        update = getFacilityIdUpdate( null, "BCN", "CID", "EramGufi" );
        
        updater.update( update, target );

        assertNull( update.getFacilityIdList() );        
        
        // At least one element is required
        update = getFacilityIdUpdate( "ZFW", null, null, null );
        
        updater.update( update, target );

        assertNull( update.getFacilityIdList() );          
        
        update = getFacilityIdUpdate( "ZFW", "BCN", null, null );
        
        updater.update( update, target );

        assertNotNull( update.getFacilityIdList() );
        assertNotNull( update.getFacilityIdList().getFacilityIds() );
        
        List<FacilityIdType> facIdList = update.getFacilityIdList().getFacilityIds();
        assertEquals( 1, facIdList.size() ); 
        assertNotNull( facIdList.get(0).getSystemId() );
        assertNotNull( facIdList.get(0).getTimestamp() );
        assertEquals( "BCN", facIdList.get(0).getBeaconCode() );
        assertNull( facIdList.get(0).getComputerId() );
        assertNull( facIdList.get(0).getEramGufi() );
        
        target = (MatmFlight) update.clone();
        update = getFacilityIdUpdate( "ZFW", null, "CID", null );
        
        updater.update( update, target );

        facIdList = update.getFacilityIdList().getFacilityIds();
        assertEquals( 1, facIdList.size() ); 
        assertNotNull( facIdList.get(0).getSystemId() );
        assertNotNull( facIdList.get(0).getTimestamp() );
        assertEquals( "ZFW", facIdList.get(0).getSourceFacility() );
        assertEquals( "BCN", facIdList.get(0).getBeaconCode() );
        assertEquals( "CID", facIdList.get(0).getComputerId() );
        assertNull( facIdList.get(0).getEramGufi() );
        
        target = (MatmFlight) update.clone();
        update = getFacilityIdUpdate( "ZFW", null, null, "EramGufi" );
        
        updater.update( update, target );

        facIdList = update.getFacilityIdList().getFacilityIds();
        assertEquals( 1, facIdList.size() ); 
        assertNotNull( facIdList.get(0).getSystemId() );
        assertNotNull( facIdList.get(0).getTimestamp() );
        assertEquals( "ZFW", facIdList.get(0).getSourceFacility() );
        assertEquals( "BCN", facIdList.get(0).getBeaconCode() );
        assertEquals( "CID", facIdList.get(0).getComputerId() );        
        assertEquals( "EramGufi", facIdList.get(0).getEramGufi() ); 
        
        // No change
        target = (MatmFlight) update.clone();
        update = getFacilityIdUpdate( "ZFW", "BCN", "CID", "EramGufi" );
        
        updater.update( update, target );

        assertNull( update.getFacilityIdList() );      
        
        update = getFacilityIdUpdate( "ZFW", "BCN2", null, "EramGufi" );
        
        updater.update( update, target );

        facIdList = update.getFacilityIdList().getFacilityIds();
        assertEquals( 1, facIdList.size() ); 
        assertNotNull( facIdList.get(0).getSystemId() );
        assertNotNull( facIdList.get(0).getTimestamp() );
        assertEquals( "ZFW", facIdList.get(0).getSourceFacility() );
        assertEquals( "BCN2", facIdList.get(0).getBeaconCode() );
        assertEquals( "CID", facIdList.get(0).getComputerId() );        
        assertEquals( "EramGufi", facIdList.get(0).getEramGufi() );     
        
        target = (MatmFlight) update.clone();
        update = getFacilityIdUpdate( "ZFW", null, "CID2", "EramGufi2" );
        
        updater.update( update, target );

        facIdList = update.getFacilityIdList().getFacilityIds();
        assertEquals( 1, facIdList.size() ); 
        assertNotNull( facIdList.get(0).getSystemId() );
        assertNotNull( facIdList.get(0).getTimestamp() );
        assertEquals( "ZFW", facIdList.get(0).getSourceFacility() );
        assertEquals( "BCN2", facIdList.get(0).getBeaconCode() );
        assertEquals( "CID2", facIdList.get(0).getComputerId() );        
        assertEquals( "EramGufi2", facIdList.get(0).getEramGufi() );           
    }
    
    @Test
    public void testMultiFacilityIdUpdates()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        update = getFacilityIdUpdate( "FAC1", "BCN1", "CID1", "ERAM1" );
        
        updater.update( update, target );
        
        List<FacilityIdType> facIdList = update.getFacilityIdList().getFacilityIds();
        assertEquals( 1, facIdList.size() ); 
        assertEquals( "FAC1", facIdList.get(0).getSourceFacility() );
        assertEquals( "BCN1", facIdList.get(0).getBeaconCode() );
        assertEquals( "CID1", facIdList.get(0).getComputerId() );        
        assertEquals( "ERAM1", facIdList.get(0).getEramGufi() );   
        
        target = (MatmFlight) update.clone();
        update = getFacilityIdUpdate( "FAC2", "BCN2", "CID2", "ERAM2" );
        
        updater.update( update, target );
        
        facIdList = update.getFacilityIdList().getFacilityIds();
        assertEquals( 2, facIdList.size() ); 
        assertEquals( "FAC1", facIdList.get(0).getSourceFacility() );
        assertEquals( "BCN1", facIdList.get(0).getBeaconCode() );
        assertEquals( "CID1", facIdList.get(0).getComputerId() );        
        assertEquals( "ERAM1", facIdList.get(0).getEramGufi() ); 
        
        assertEquals( "FAC2", facIdList.get(1).getSourceFacility() );
        assertEquals( "BCN2", facIdList.get(1).getBeaconCode() );
        assertEquals( "CID2", facIdList.get(1).getComputerId() );        
        assertEquals( "ERAM2", facIdList.get(1).getEramGufi() );          
    }
    
    private MatmFlight getFacilityIdUpdate( String facility, String beacon, String cid, String eramGufi )
    {
        MatmFlight matm = new MatmFlight();
        matm.setTimestamp( new Date() );        
        matm.setSystemId( "sysid" );
        
        matm.setEramGufi( eramGufi );
        matm.setSourceFacility( facility );
        matm.setBeaconCode( beacon );
        matm.setComputerId( cid );
        
        return( matm );
    }
}
