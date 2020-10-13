package com.mosaicatm.fuser.rules;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:config/fuser/beans.rules.test.xml")
public class AirlineParkingGateRuleTest 
{
    private final String targetField = "departureStandAirline";
    
    @Autowired
    @Qualifier("fuser-rules.AirlineParkingGateRule")
    private Rule<MatmFlight> gateRule;
    
    @Test
    public void testDifference() 
    {
        MatmFlight update = new MatmFlight();
        update.setLastUpdateSource("AIRLINE");
        update.setSystemId("FLIGHTSTATS");
        update.setDepartureStandAirline("departure stand airline");
        
        MetaData history = new MetaData();
        history.setFieldName(targetField);
        history.setSource("AIRLINE");
        history.setSystemType("FLIGHTHUB");
        
        assertFalse(gateRule.handleDifference(update, null, history, history.getFieldName()));
        assertNull(update.getDepartureStandAirline());
        
        history.setSystemType("FLIGHTSTATS");
        update.setSystemId("FLIGHTHUB");
        update.setDepartureStandAirline("result");
        assertTrue(gateRule.handleDifference(update, null, history, history.getFieldName()));
        assertEquals("result", update.getDepartureStandAirline());
        
        history.setSystemType("FLIGHTSTATS");
        update.setSystemId(null);
        update.setSystemId("No system");
        update.setDepartureStandAirline("result");
        assertFalse(gateRule.handleDifference(update, null, history, history.getFieldName()));
        assertNull(update.getDepartureStandAirline());
        
        history.setSource("AIRLINE");        
        history.setSystemType("FLIGHTHUB");
        update.setLastUpdateSource("TFM_TFDM");
        update.setSystemId(null);
        update.setDepartureStandAirline("result2"); 
        assertFalse(gateRule.handleDifference(update, null, history, history.getFieldName()));
        assertNull("result2", update.getDepartureStandAirline());     
        
        history.setSource("AIRLINE");        
        history.setSystemType("FLIGHTSTATS");
        update.setLastUpdateSource("TFM_TFDM");
        update.setSystemId(null);
        update.setDepartureStandAirline("result2"); 
        assertTrue(gateRule.handleDifference(update, null, history, history.getFieldName()));
        assertEquals("result2", update.getDepartureStandAirline());    
    }
    
    @Test
    public void testIdenticals() 
    {
        MatmFlight update = new MatmFlight();
        update.setLastUpdateSource("AIRLINE");
        update.setSystemId("FLIGHTSTATS");
        update.setDepartureStandAirline("departure stand airline");
        
        MetaData history = new MetaData();
        history.setFieldName(targetField);
        history.setSource("AIRLINE");
        history.setSystemType("FLIGHTHUB");
        
        assertFalse(gateRule.handleIdentical(update, null, history, history.getFieldName()));
        assertNull(update.getDepartureStandAirline());   
        
        history.setSystemType("FLIGHTSTATS");
        update.setSystemId("FLIGHTHUB");
        update.setDepartureStandAirline("result");
        assertTrue(gateRule.handleIdentical(update, null, history, history.getFieldName()));
        assertNotNull(update.getDepartureStandAirline());   
        
        history.setSystemType("FLIGHTSTATS");
        update.setSystemId(null);
        update.setSystemId("No system");
        update.setDepartureStandAirline("result");
        assertFalse(gateRule.handleIdentical(update, null, history, history.getFieldName()));
        assertNull(update.getDepartureStandAirline());   
        
        history.setSource("TFM_TFDM");        
        history.setSystemType(null);
        update.setLastUpdateSource("TFM_TFDM");
        update.setSystemId(null);
        update.setDepartureStandAirline("result2");
        assertTrue(gateRule.handleIdentical(update, null, history, history.getFieldName()));
        assertNotNull(update.getDepartureStandAirline());   
    }

}
