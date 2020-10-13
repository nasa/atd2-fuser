package com.mosaicatm.fuser.updaters.post;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mosaicatm.fuser.updaters.Updater;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.EngineClass;
import com.mosaicatm.matmdata.common.WakeTurbulenceCategory;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class CommonFieldUpdaterTest
{
    Updater<MatmFlight, MatmFlight> updater = new CommonFieldUpdater();


    @Test
    public void testTargetWakeTurbulence()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        target.setWakeTurbulenceCategory(WakeTurbulenceCategory.SMALL);
        
        assertNull(update.getAircraftType());
        assertNotNull(target.getWakeTurbulenceCategory());
        
        updater.update(update, target);
        
        assertNotNull(update.getWakeTurbulenceCategory());        
        assertNotNull(target.getWakeTurbulenceCategory());
        
        assertEquals(target.getWakeTurbulenceCategory(), update.getWakeTurbulenceCategory());
    }
    
    @Test
    public void testUpdateWakeTurbulence()
    {
        MatmFlight update = new MatmFlight();
        update.setWakeTurbulenceCategory(WakeTurbulenceCategory.SMALL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getWakeTurbulenceCategory());
        assertNull(target.getWakeTurbulenceCategory());
        
        updater.update(update, target);
        
        assertNotNull(update.getWakeTurbulenceCategory());        
        assertNull(target.getWakeTurbulenceCategory());
    }
    
    @Test
    public void testTargetEngineClass()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        target.setAircraftEngineClass(EngineClass.PISTON);
        
        assertNull(update.getAircraftEngineClass());
        assertNotNull(target.getAircraftEngineClass());
        
        updater.update(update, target);
        
        assertNotNull(update.getAircraftEngineClass());        
        assertNotNull(target.getAircraftEngineClass());
        
        assertEquals(target.getAircraftEngineClass(), update.getAircraftEngineClass());
    }
    
    @Test
    public void testUpdateEngineClass()
    {
        MatmFlight update = new MatmFlight();
        update.setAircraftEngineClass(EngineClass.TURBO);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getAircraftEngineClass());
        assertNull(target.getAircraftEngineClass());
        
        updater.update(update, target);
        
        assertNotNull(update.getAircraftEngineClass());        
        assertNull(target.getAircraftEngineClass());
    }

    @Test
    public void testAerodromes()
    {
        MatmFlight target = new MatmFlight();
        target.setDepartureAerodrome( new Aerodrome() );
        target.setArrivalAerodrome( new Aerodrome() );
        target.getDepartureAerodrome().setIataName( "DEP_IATA" );
        target.getDepartureAerodrome().setIataName( "DEP_ICAO" );
        target.getArrivalAerodrome().setIataName( "ARR_IATA" );
        target.getArrivalAerodrome().setIataName( "ARR_ICAO" );
        
        MatmFlight update = new MatmFlight();
        
        updater.update(update, target);
        
        assertEquals( target.getDepartureAerodrome().getIataName(), update.getDepartureAerodrome().getIataName());        
        assertEquals( target.getDepartureAerodrome().getIcaoName(), update.getDepartureAerodrome().getIcaoName());        
        assertEquals( target.getArrivalAerodrome().getIataName(), update.getArrivalAerodrome().getIataName());        
        assertEquals( target.getArrivalAerodrome().getIcaoName(), update.getArrivalAerodrome().getIcaoName());    
        
        // Try updating one field.
        update = new MatmFlight();
        update.setDepartureAerodrome( new Aerodrome() );
        update.getDepartureAerodrome().setIataName( "UPDATE_DEP_IATA" );   
        
        updater.update(update, target);
        
        assertNotEquals( target.getDepartureAerodrome().getIataName(), update.getDepartureAerodrome().getIataName());        
        assertEquals( target.getDepartureAerodrome().getIcaoName(), update.getDepartureAerodrome().getIcaoName());        
        assertEquals( target.getArrivalAerodrome().getIataName(), update.getArrivalAerodrome().getIataName());        
        assertEquals( target.getArrivalAerodrome().getIcaoName(), update.getArrivalAerodrome().getIcaoName()); 
        
        
        // This was broken for a while
        update = new MatmFlight();
        update.setDepartureAerodrome( new Aerodrome() );
        update.setArrivalAerodrome( new Aerodrome() );        
        
        updater.update(update, target);
        
        assertEquals( target.getDepartureAerodrome().getIataName(), update.getDepartureAerodrome().getIataName());        
        assertEquals( target.getDepartureAerodrome().getIcaoName(), update.getDepartureAerodrome().getIcaoName());        
        assertEquals( target.getArrivalAerodrome().getIataName(), update.getArrivalAerodrome().getIataName());        
        assertEquals( target.getArrivalAerodrome().getIcaoName(), update.getArrivalAerodrome().getIcaoName());    
    }    
}
