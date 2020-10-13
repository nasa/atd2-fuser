package com.mosaicatm.fuser.common.matm.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.MatmCloner;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmClonerTest
{
    @Test
    public void testCloneIsDeepCopy()
    {
        MatmCloner cloner = new MatmCloner();
        
        MatmFlight flight = new MatmFlight();
        List<MetaData> update_sources = new ArrayList<>();
        flight.setUpdateSources( update_sources );
        
        MetaData meta = new MetaData();
        meta.setFieldName( "field" );
        meta.setTimestamp( new Date() );
        meta.setSource( "source" );
        meta.setSystemType( "system_type" );
        
        flight.getUpdateSources().add( meta );
        
        MatmFlight clone = cloner.cloneFlight( flight );
        MetaData clone_meta = clone.getUpdateSources().get( 0 );
        
        assertFalse( clone_meta == meta );
        assertFalse( clone_meta.getTimestamp() == meta.getTimestamp() );
        assertEquals( meta, clone_meta );
    }
    
    @Test
    public void testAircraftCloneIsDeepCopy()
    {
        MatmCloner cloner = new MatmCloner();
        
        MatmAircraft aircraft = new MatmAircraft();
        List<MetaData> update_sources = new ArrayList<>();
        aircraft.setUpdateSources( update_sources );
        
        MetaData meta = new MetaData();
        meta.setFieldName( "field" );
        meta.setTimestamp( new Date() );
        meta.setSource( "source" );
        meta.setSystemType( "system_type" );
        
        aircraft.getUpdateSources().add( meta );
        
        MatmAircraft clone = cloner.cloneAircraft( aircraft );
        MetaData clone_meta = clone.getUpdateSources().get( 0 );
        
        assertFalse( clone_meta == meta );
        assertFalse( clone_meta.getTimestamp() == meta.getTimestamp() );
        assertEquals( meta, clone_meta );
    }   
}
