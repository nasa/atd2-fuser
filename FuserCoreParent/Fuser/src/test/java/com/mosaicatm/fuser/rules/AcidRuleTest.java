package com.mosaicatm.fuser.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
public class AcidRuleTest 
{
    private final String field = "acid";

    @Autowired
    @Qualifier("fuser-rules.AcidRule")
    private Rule<MatmFlight> rule;
    
    @Test
    public void testDifference() 
    {
        MetaData history = new MetaData();
        history.setSource("TFM");
        history.setSystemType( "FLIGHT_PLAN_AMENDMENT_INFORMATION" );

        MatmFlight update = new MatmFlight();
        String acid = "ACID";
        update.setLastUpdateSource("AIRLINE");
        update.setAcid(acid);
        
        assertFalse(rule.handleDifference(update, null, history, field));
        assertNull( update.getAcid() );
        
        history = new MetaData();
        history.setSource("TMA");
        update.setLastUpdateSource("AIRLINE");
        update.setAcid(acid);
        assertFalse(rule.handleDifference(update, null, history, field));
        assertNull(update.getAcid());         
        
        history = new MetaData();
        history.setSource("AIRLINE");
        update.setLastUpdateSource("ASDEX");
        update.setAcid(acid);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getAcid());        
        
        history = new MetaData();
        history.setSource("AIRLINE");
        update.setLastUpdateSource("AIRLINE");
        update.setAcid(acid);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getAcid());
        
        history = new MetaData();
        history.setSource("ASDEX");
        update.setLastUpdateSource("ASDEX");
        update.setAcid(acid);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getAcid());
        
        history = new MetaData();
        history.setSource("ASDEX");
        update.setLastUpdateSource("TFM");
        update.setSystemId("FLIGHT_PLAN_AMENDMENT_INFORMATION");
        update.setAcid(acid);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getAcid());      
        
        history = new MetaData();
        history.setSource("TMA");
        update.setLastUpdateSource("ASDEX");
        update.setSystemId(null);
        update.setAcid(acid);
        assertFalse(rule.handleDifference(update, null, history, field));
        assertNull(update.getAcid());
        
        // Test out the ties
        history = new MetaData();
        history.setSource("TFM");
        history.setSystemType( "FLIGHT_PLAN_AMENDMENT_INFORMATION" );
        update.setLastUpdateSource("TMA");
        update.setAcid(acid);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getAcid());  
        
        history = new MetaData();
        history.setSource("TMA");
        update.setLastUpdateSource("TFM");
        update.setSystemId("FLIGHT_PLAN_AMENDMENT_INFORMATION");
        update.setAcid(acid);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getAcid());        
    }
    
    @Test
    public void testIdenticals() 
    {
        String acid = "ACID";
        MetaData history = new MetaData();
        history.setSource("AIRLINE");

        MatmFlight update = new MatmFlight();
        update.setLastUpdateSource("TFM");
        update.setSystemId("FLIGHT_PLAN_AMENDMENT_INFORMATION");
        update.setAcid(acid);
        
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.getAcid()); 
        
        history = new MetaData();
        history.setSource("FLIGHT_HUB_POSITION");
        update.setLastUpdateSource("TMA");
        update.setSystemId(null);
        update.setAcid(acid);
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.getAcid()); 
        
        history = new MetaData();
        history.setSource("ASDEX");
        update.setLastUpdateSource("AIRLINE");
        update.setAcid(acid);
        assertFalse(rule.handleIdentical(update, null, history, field));
        assertNull(update.getAcid());
        
        history = new MetaData();
        history.setSource("TMA");
        update.setLastUpdateSource("AIRLINE");
        update.setAcid(acid);
        assertFalse(rule.handleIdentical(update, null, history, field));
        assertNull(update.getAcid());
        
        history = new MetaData();
        history.setSource("ASDEX");
        update.setLastUpdateSource("TFM");
        update.setSystemId("FLIGHT_PLAN_AMENDMENT_INFORMATION");
        update.setAcid(acid);
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.getAcid()); 
        
        // Test out the ties
        history = new MetaData();
        history.setSource("TFM");
        history.setSystemType( "FLIGHT_PLAN_AMENDMENT_INFORMATION" );
        update.setLastUpdateSource("TMA");
        update.setSystemId(null);        
        update.setAcid(acid);
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.getAcid()); 
        
        history = new MetaData();
        history.setSource("TMA");
        update.setLastUpdateSource("TFM");
        update.setSystemId("FLIGHT_PLAN_AMENDMENT_INFORMATION");
        update.setAcid(acid);
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.getAcid()); 
    }
}
