package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.IdacExtension;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class IdacExtensionUpdaterTest
{
    private IdacExtensionUpdater updater;
    
    @Before
    public void setup ()
    {
        updater = new IdacExtensionUpdater();
    }
    
    @Test
    public void testNullIdacExtension()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        assertTrue( update.getExtensions() == null || update.getExtensions().getIdacExtension() == null );
        assertTrue( target.getExtensions() == null || target.getExtensions().getIdacExtension() == null );
        
        updater.update(update, target);
        
        assertTrue( update.getExtensions() == null || update.getExtensions().getIdacExtension() == null );
        assertTrue( target.getExtensions() == null || target.getExtensions().getIdacExtension() == null );
    }
    
    @Test
    public void testIdacGlobalIdStaysTheSame()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        IdacExtension idac_update = new IdacExtension();
        IdacExtension idac_target = new IdacExtension();
        
        update.setExtensions( new MatmFlightExtensions() );
        update.getExtensions().setIdacExtension( idac_update );
        target.setExtensions( new MatmFlightExtensions() );
        target.getExtensions().setIdacExtension( idac_target );
        
        // test when neither are fused
        idac_target.setFused( Boolean.FALSE );
        idac_target.setGlobalId( "GlobalId1" );
        
        idac_update.setFused( Boolean.FALSE );
        idac_update.setGlobalId( "GlobalId2" );        
        
        updater.update(update, target);
        
        assertEquals( idac_target.getGlobalId(), "GlobalId1" );
        assertNull( idac_update.getGlobalId() );
        
        // test when update is fused
        idac_target.setFused( Boolean.FALSE );
        idac_target.setGlobalId( "GlobalId1" );
        
        idac_update.setFused( Boolean.TRUE );
        idac_update.setGlobalId( "GlobalId2" );        
        
        updater.update(update, target);
        
        assertEquals( idac_target.getGlobalId(), "GlobalId1" );
        assertEquals( idac_update.getGlobalId(), "GlobalId2" );        
        
        
        // Test when both are fused
        idac_target.setFused( true );
        idac_target.setGlobalId( "GlobalId1" );
        
        idac_update.setFused( true );
        idac_update.setGlobalId( "GlobalId2" );        
        
        updater.update(update, target);
        
        assertEquals( idac_target.getGlobalId(), "GlobalId1" );
        assertEquals( idac_update.getGlobalId(), "GlobalId2" );          
        
        // test when isfused is null
        idac_target.setFused( null );
        idac_target.setGlobalId( "GlobalId1" );
        
        idac_update.setFused( null );
        idac_update.setGlobalId( "GlobalId2" );        
        
        updater.update(update, target);
        
        assertEquals( idac_target.getGlobalId(), "GlobalId1" );
        assertNull( idac_update.getGlobalId() );
        
        // test when isfused is null for target only
        idac_target.setFused( null );
        idac_target.setGlobalId( "GlobalId1" );
        
        idac_update.setFused( false );
        idac_update.setGlobalId( "GlobalId2" );        
        
        updater.update(update, target);
        
        assertEquals( idac_target.getGlobalId(), "GlobalId1" );
        assertNull( idac_update.getGlobalId() );      
    }    
    
    @Test
    public void testIdacGlobalNulledOut()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        IdacExtension idac_update = new IdacExtension();
        IdacExtension idac_target = new IdacExtension();
        
        update.setExtensions( new MatmFlightExtensions() );
        update.getExtensions().setIdacExtension( idac_update );
        target.setExtensions( new MatmFlightExtensions() );
        target.getExtensions().setIdacExtension( idac_target );
        
        // test when update is not fused
        idac_target.setFused( true );
        idac_target.setGlobalId( "GlobalId1" );
        
        idac_update.setFused( false );
        idac_update.setGlobalId( "GlobalId2" );        
        
        updater.update(update, target);
        
        assertEquals( idac_target.getGlobalId(), "GlobalId1" );
        assertNull( idac_update.getGlobalId() );
        
        // test when update isFused is null
        idac_target.setFused( true );
        idac_target.setGlobalId( "GlobalId1" );
        
        idac_update.setFused( null );
        idac_update.setGlobalId( "GlobalId2" );        
        
        updater.update(update, target);
        
        assertEquals( idac_target.getGlobalId(), "GlobalId1" );
        assertNull( idac_update.getGlobalId() );            
    }        
}
