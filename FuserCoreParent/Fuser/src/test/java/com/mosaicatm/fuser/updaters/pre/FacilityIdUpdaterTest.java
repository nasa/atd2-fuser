package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class FacilityIdUpdaterTest
{
    private FacilityIdUpdater updater;
    
    @Before
    public void setup ()
    {
        updater = new FacilityIdUpdater();
    }
    
    @Test
    public void testNullIllegalFacilities()
    {       
        MatmFlight update = getFacilityIdUpdate( "TFMS", "1111", "999" );           
        MatmFlight target = new MatmFlight();
        
        assertNotNull( update.getSourceFacility() );
        assertNotNull( update.getBeaconCode() );
        assertNotNull( update.getComputerId() );
        
        updater.update(update, null);
        assertNull( update.getSourceFacility() );
        assertNull( update.getBeaconCode() );
        assertNull( update.getComputerId() );            
        
        update = getFacilityIdUpdate( "TFMS", "1111", "999" ); 
        updater.update(update, target);
        assertNull( update.getSourceFacility() );
        assertNull( update.getBeaconCode() );
        assertNull( update.getComputerId() );              
        
        update = getFacilityIdUpdate( "AAL", "1111", "999" );             
        updater.update(update, null);
        assertNull( update.getSourceFacility() );
        assertNull( update.getBeaconCode() );
        assertNull( update.getComputerId() );           
        
        update = getFacilityIdUpdate( "AAL", "1111", "999" ); 
        updater.update(update, target);
        assertNull( update.getSourceFacility() );
        assertNull( update.getBeaconCode() );
        assertNull( update.getComputerId() );             
        
        update = getFacilityIdUpdate( "KDFW", "1111", "999" );             
        updater.update(update, null);
        assertNull( update.getSourceFacility() );
        assertNull( update.getBeaconCode() );
        assertNull( update.getComputerId() );     

        update = getFacilityIdUpdate( "KDFW", "1111", "999" );      
        updater.update(update, target);
        assertNull( update.getSourceFacility() );
        assertNull( update.getBeaconCode() );
        assertNull( update.getComputerId() );           
        
        update = getFacilityIdUpdate( "UAL", "1111", "999" );                     
        updater.update(update, null);
        assertNull( update.getSourceFacility() );
        assertNull( update.getBeaconCode() );
        assertNull( update.getComputerId() );     

        update = getFacilityIdUpdate( "UAL", "1111", "999" );          
        updater.update(update, target);
        assertNull( update.getSourceFacility() );
        assertNull( update.getBeaconCode() );
        assertNull( update.getComputerId() );                      
    }
    
    @Test
    public void testAllowFacilities()
    {
        MatmFlight update = new MatmFlight();
        
        update = getFacilityIdUpdate( "ZSA", "1111", "999" );                 
        updater.update(update, null);
        assertEquals("ZSA", update.getSourceFacility());     
        assertEquals( "1111", update.getBeaconCode() );
        assertEquals( "999", update.getComputerId() );          
          
        update = getFacilityIdUpdate( "KZOB", "1111", "999" );  
        updater.update(update, null);
        assertEquals("KZOB", update.getSourceFacility());  
        assertEquals( "1111", update.getBeaconCode() );
        assertEquals( "999", update.getComputerId() );             
           
        update = getFacilityIdUpdate( "KONY", "1111", "999" );  
        updater.update(update, null);
        assertEquals("KONY", update.getSourceFacility());  
        assertEquals( "1111", update.getBeaconCode() );
        assertEquals( "999", update.getComputerId() );             

        update = getFacilityIdUpdate( "KOOA", "1111", "999" );               
        updater.update(update, null);
        assertEquals("KOOA", update.getSourceFacility());     
        assertEquals( "1111", update.getBeaconCode() );
        assertEquals( "999", update.getComputerId() );             
    }    
    
    @Test
    public void testFixSourceFacilities()
    {
        MatmFlight update = new MatmFlight();
        
        update.setSourceFacility( "ZDV" );               
        updater.update(update, null);
        assertEquals("KZDV", update.getSourceFacility());
        
        update.setSourceFacility( "ZOB" );               
        updater.update(update, null);
        assertEquals("KZOB", update.getSourceFacility());  
        
        update.setSourceFacility( "KZAP" );               
        updater.update(update, null);
        assertEquals("PZAN", update.getSourceFacility());     
    }   
    
    @Test
    public void testFixBeaconCode()
    {
        MatmFlight update = getFacilityIdUpdate( "KZOB", "1111", "999" );                 
        
        updater.update(update, null);
        assertEquals("KZOB", update.getSourceFacility());     
        assertEquals( "1111", update.getBeaconCode() );
        assertEquals( "999", update.getComputerId() );            
        
        update = getFacilityIdUpdate( "KZOB", "1", "999" );  
        updater.update(update, null);
        assertEquals("KZOB", update.getSourceFacility());     
        assertEquals( "0001", update.getBeaconCode() );
        assertEquals( "999", update.getComputerId() ); 

        update = getFacilityIdUpdate( "KZOB", "01", "999" );  
        updater.update(update, null);
        assertEquals("KZOB", update.getSourceFacility());     
        assertEquals( "0001", update.getBeaconCode() );
        assertEquals( "999", update.getComputerId() );         
        
        update = getFacilityIdUpdate( "KZOB", "001", "999" );  
        updater.update(update, null);
        assertEquals("KZOB", update.getSourceFacility());     
        assertEquals( "0001", update.getBeaconCode() );
        assertEquals( "999", update.getComputerId() );     
        
        update = getFacilityIdUpdate( "KZOB", "00100", "999" );  
        updater.update(update, null);
        assertEquals("KZOB", update.getSourceFacility());     
        assertNull( update.getBeaconCode() );
        assertEquals( "999", update.getComputerId() );           
    }
    
    @Test
    public void testFixComputerId()
    {
        MatmFlight update = getFacilityIdUpdate( "KZOB", "1111", "999" );                 
        
        updater.update(update, null);
        assertEquals("KZOB", update.getSourceFacility());     
        assertEquals( "1111", update.getBeaconCode() );
        assertEquals( "999", update.getComputerId() );            
        
        update = getFacilityIdUpdate( "KZOB", "1111", "9" );  
        updater.update(update, null);
        assertEquals("KZOB", update.getSourceFacility());     
        assertEquals( "1111", update.getBeaconCode() );
        assertEquals( "009", update.getComputerId() ); 

        update = getFacilityIdUpdate( "KZOB", "1111", "09" );  
        updater.update(update, null);
        assertEquals("KZOB", update.getSourceFacility());     
        assertEquals( "1111", update.getBeaconCode() );
        assertEquals( "009", update.getComputerId() );         
        
        update = getFacilityIdUpdate( "KZOB", "1111", "9999" );  
        updater.update(update, null);
        assertEquals("KZOB", update.getSourceFacility());     
        assertEquals( "1111", update.getBeaconCode() );
        assertNull( update.getComputerId() );          
    }    
    
    private MatmFlight getFacilityIdUpdate( String facility, String beacon, String cid )
    {
        MatmFlight matm = new MatmFlight();
        matm.setSourceFacility( facility );
        matm.setBeaconCode( beacon );
        matm.setComputerId( cid );
        
        return( matm );
    }
}
