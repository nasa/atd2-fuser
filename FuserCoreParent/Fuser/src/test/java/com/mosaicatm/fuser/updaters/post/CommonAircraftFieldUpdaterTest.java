package com.mosaicatm.fuser.updaters.post;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class CommonAircraftFieldUpdaterTest
{
    private CommonAircraftFieldUpdater updater;
    
    @Before
    public void setup()
    {
        updater = new CommonAircraftFieldUpdater();
    }
    
    @Test
    public void testAddress()
    {
        MatmAircraft update = new MatmAircraft();
        MatmAircraft target = new MatmAircraft();
        target.setAddress("test_address");
        
        assertNull(update.getAddress());
        assertNotNull(target.getAddress());
        
        updater.update(update, target);
        
        assertNotNull(update.getAddress());
        assertEquals(target.getAddress(), update.getAddress());
    }
    
    @Test
    public void testEngineClass()
    {
        MatmAircraft update = new MatmAircraft();
        MatmAircraft target = new MatmAircraft();
        target.setFaaEngineClass("faa_class");
        target.setRecatEngineClass("recat_class");
        
        assertNull(update.getFaaEngineClass());
        assertNull(update.getRecatEngineClass());
        assertNotNull(target.getFaaEngineClass());
        assertNotNull(target.getRecatEngineClass());
        
        updater.update(update, target);
        
        assertNotNull(update.getFaaEngineClass());
        assertNotNull(update.getRecatEngineClass());
        assertEquals(target.getFaaEngineClass(), update.getFaaEngineClass());
        assertEquals(target.getRecatEngineClass(), update.getRecatEngineClass());
    }
    
    @Test
    public void testEquipmentQualifier()
    {
        MatmAircraft update = new MatmAircraft();
        MatmAircraft target = new MatmAircraft();
        target.setEquipmentQualifier("test_equipment");
        
        assertNull(update.getEquipmentQualifier());
        assertNotNull(target.getEquipmentQualifier());
        
        updater.update(update, target);
        
        assertNotNull(update.getEquipmentQualifier());
        assertEquals(target.getEquipmentQualifier(), update.getEquipmentQualifier());
    }
    
    @Test
    public void testType()
    {
        MatmAircraft update = new MatmAircraft();
        MatmAircraft target = new MatmAircraft();
        target.setType("test_type");
        
        assertNull(update.getType());
        assertNotNull(target.getType());
        
        updater.update(update, target);
        
        assertNotNull(update.getType());
        assertEquals(target.getType(), update.getType());
    }
}
