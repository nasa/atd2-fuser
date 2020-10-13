package com.mosaicatm.fuser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.matm.actypelib.manager.AircraftTypeManager;
import com.mosaicatm.fuser.common.FuserSourceSystemType;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class AircraftTypeRuleTest 
{
    private String targetField;
    private AircraftTypeRule aircraftTypeRule;
    
    @Before
    public void setup()
    {
        targetField = "aircraftType";
        
        Set<FuserSourceSystemType> highPriority = new HashSet<>();
        highPriority.add(FuserSourceSystemType.ASDEX);
        highPriority.add(FuserSourceSystemType.IDAC);
        highPriority.add(FuserSourceSystemType.SFDPS);
        highPriority.add(FuserSourceSystemType.TFM);
        highPriority.add(FuserSourceSystemType.TFM_TFDM);
        highPriority.add(FuserSourceSystemType.TMA);
        
        Set<FuserSourceSystemType> lowPriority = new HashSet<>();
        lowPriority.add(FuserSourceSystemType.AIRLINE_FLIGHTHUB);
        lowPriority.add(FuserSourceSystemType.AIRLINE_FLIGHTSTATS);
        
        List<Set<FuserSourceSystemType>> sourcePrioritySetList = new ArrayList<>();
        sourcePrioritySetList.add(highPriority);
        sourcePrioritySetList.add(lowPriority);
        
        aircraftTypeRule = new AircraftTypeRule();
        aircraftTypeRule.setSourcePriorityList(sourcePrioritySetList);
        aircraftTypeRule.setIncludes(Arrays.asList(targetField));
        aircraftTypeRule.setActive(true);
    }
    
	@Test
	public void testUnknownAircraftType()
	{	    	    		
		MatmFlight update = new MatmFlight();
		update.setAircraftType(AircraftTypeManager.DEFAULT_AIRCRAFT_TYPE);
		update.setLastUpdateSource("AIRLINE");
		update.setSystemId("FLIGHTHUB");
		
		MetaData history = new MetaData();
		
		assertFalse(aircraftTypeRule.handleDifference(update, null, history, targetField));
		assertNull(update.getAircraftType());
	}
	
	@Test
	public void testKnownAircraftType() 
	{
		String aircraftType = "B738";
		MatmFlight update = new MatmFlight();
		update.setAircraftType(aircraftType);
		
		MetaData history = new MetaData();
		
		assertTrue(aircraftTypeRule.handleDifference(update, null, history, targetField));
		assertNotNull(update.getAircraftType());
		assertEquals(aircraftType, update.getAircraftType());
	}
	
	@Test
	public void testAircraftPriorities()
	{
        MatmFlight update = new MatmFlight();
        MetaData history = new MetaData();
     
        // AIRLINE should be able to overwrite AIRLINE
        String aircraftType = "AAA";
        history.setFieldName(targetField);
        history.setSource(FuserSourceSystemType.AIRLINE_FLIGHTSTATS.getSource().name());
        history.setSystemType(FuserSourceSystemType.AIRLINE_FLIGHTSTATS.getSystem());
        
        update.setAircraftType(aircraftType);
        update.setLastUpdateSource(FuserSourceSystemType.AIRLINE_FLIGHTHUB.getSource().name());
        update.setSystemId(FuserSourceSystemType.AIRLINE_FLIGHTHUB.getSystem());
        
        assertTrue(aircraftTypeRule.handleDifference(update, null, history, targetField));
        assertNotNull(update.getAircraftType());
        assertEquals(aircraftType, update.getAircraftType());
        
        // TFM should be able to overwrite AIRLINE
        aircraftType = "BBB";        
        history.setFieldName(targetField);
        history.setSource(FuserSourceSystemType.AIRLINE_FLIGHTHUB.getSource().name());
        history.setSystemType(FuserSourceSystemType.AIRLINE_FLIGHTHUB.getSystem());
        
        update.setAircraftType(aircraftType);
        update.setLastUpdateSource(FuserSourceSystemType.TFM_FLIGHT_PLAN_AMENDMENT_INFORMATION.getSource().name());
        update.setSystemId(FuserSourceSystemType.TFM_FLIGHT_PLAN_AMENDMENT_INFORMATION.getSystem());
        
        assertTrue(aircraftTypeRule.handleDifference(update, null, history, targetField));
        assertNotNull(update.getAircraftType());
        assertEquals(aircraftType, update.getAircraftType());
        
        // TFM should be able to overwrite TFM
        aircraftType = "CCC";        
        history.setFieldName(targetField);
        history.setSource(FuserSourceSystemType.TFM_FLIGHT_PLAN_AMENDMENT_INFORMATION.getSource().name());
        history.setSystemType(FuserSourceSystemType.TFM_FLIGHT_PLAN_AMENDMENT_INFORMATION.getSystem());
        
        update.setAircraftType(aircraftType);
        update.setLastUpdateSource(FuserSourceSystemType.TFM_FLIGHT_MODIFY.getSource().name());
        update.setSystemId(FuserSourceSystemType.TFM_FLIGHT_MODIFY.getSystem());
        
        assertTrue(aircraftTypeRule.handleDifference(update, null, history, targetField));
        assertNotNull(update.getAircraftType());
        assertEquals(aircraftType, update.getAircraftType());
        
        // AIRLINE should NOT be able to overwrite TFM
        aircraftType = "CCC";        
        history.setFieldName(targetField);
        history.setSource(FuserSourceSystemType.TFM_FLIGHT_MODIFY.getSource().name());
        history.setSystemType(FuserSourceSystemType.TFM_FLIGHT_MODIFY.getSystem());
        
        update.setAircraftType(aircraftType);
        update.setLastUpdateSource(FuserSourceSystemType.AIRLINE_FLIGHTSTATS.getSource().name());
        update.setSystemId(FuserSourceSystemType.AIRLINE_FLIGHTSTATS.getSystem());
        
        assertFalse(aircraftTypeRule.handleDifference(update, null, history, targetField));
        assertNull(update.getAircraftType());
	}
	
	@Test
	public void testAircraftTypeRuleIdentical()
	{		
		String aircraftType = "B738";
		
		MetaData history = new MetaData();
        history.setFieldName(targetField);
        history.setSource(FuserSourceSystemType.AIRLINE_FLIGHTSTATS.getSource().name());
        history.setSystemType(FuserSourceSystemType.AIRLINE_FLIGHTSTATS.getSystem());
		
		MatmFlight update = new MatmFlight();
		update.setAircraftType(aircraftType);
        update.setLastUpdateSource(FuserSourceSystemType.TFM_FLIGHT_MODIFY.getSource().name());
        update.setSystemId(FuserSourceSystemType.TFM_FLIGHT_MODIFY.getSystem());
		
		assertTrue(aircraftTypeRule.handleIdentical(update, null, history, targetField));
		assertNotNull(update.getAircraftType());
		assertEquals(aircraftType, update.getAircraftType());
	}

}
