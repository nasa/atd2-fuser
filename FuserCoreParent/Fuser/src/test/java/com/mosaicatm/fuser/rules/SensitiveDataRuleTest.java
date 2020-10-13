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
public class SensitiveDataRuleTest 
{
    private final String field = "sensitiveDataExternal";

    @Autowired
    @Qualifier("fuser-rules.SensitiveDataRule")
    private Rule<MatmFlight> rule;
    
    @Test
    public void testDifference() 
    {
        MetaData history = new MetaData();
        history.setSource("TFM");
        history.setSystemType( "TFM-FLIGHT_PLAN_INFORMATION" );

        MatmFlight update = new MatmFlight();
        Boolean sensitive = true;
        update.setLastUpdateSource("TMA");
        update.setSystemId( null );
        update.setSensitiveDataExternal(sensitive);
        
        assertFalse(rule.handleDifference(update, null, history, field));
        assertNull( update.isSensitiveDataExternal() );  
        
        history.setSource("TMA");
        history.setSystemType(null);
        update.setLastUpdateSource("ASDEX");
        update.setSystemId( null );
        update.setSensitiveDataExternal(sensitive);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.isSensitiveDataExternal());        
        
        history.setSource("TMA");
        history.setSystemType(null);
        update.setLastUpdateSource("TMA");
        update.setSystemId( null );
        update.setSensitiveDataExternal(sensitive);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.isSensitiveDataExternal());
        
        history.setSource("ASDEX");
        history.setSystemType(null);
        update.setLastUpdateSource("ASDEX");
        update.setSystemId( null );
        update.setSensitiveDataExternal(sensitive);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.isSensitiveDataExternal());
        
        history.setSource("ASDEX");
        history.setSystemType(null);
        update.setLastUpdateSource("TFM");
        update.setSystemId( "TFM-FLIGHT_PLAN_INFORMATION" );
        update.setSensitiveDataExternal(sensitive);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.isSensitiveDataExternal());      
        
        history.setSource("TMA");
        history.setSystemType(null);
        update.setLastUpdateSource("ASDEX");
        update.setSystemId( null );
        update.setSensitiveDataExternal(sensitive);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.isSensitiveDataExternal());  
        
        history.setSource("TFM");
        history.setSystemType("TFM-FLIGHT_PLAN_INFORMATION");
        update.setLastUpdateSource("TFM");
        update.setSystemId( "TFM-TRACK_INFORMATION" );
        update.setSensitiveDataExternal(sensitive);
        assertFalse(rule.handleDifference(update, null, history, field));
        assertNull(update.isSensitiveDataExternal());          
        
        history.setSource("TFM");
        history.setSystemType("TFM-FLIGHT_PLAN_INFORMATION");
        update.setLastUpdateSource("ASDEX");
        update.setSystemId( null );
        update.setSensitiveDataExternal(sensitive);
        assertFalse(rule.handleDifference(update, null, history, field));
        assertNull(update.isSensitiveDataExternal());          
        
        // Test out the ties
        history.setSource("TFM");
        history.setSystemType("TFM-FLIGHT_PLAN_INFORMATION");
        update.setLastUpdateSource("TFM_TFDM");
        update.setSystemId( null );
        update.setSystemId( field );
        update.setSensitiveDataExternal(sensitive);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.isSensitiveDataExternal());
    }
    
    @Test
    public void testIdenticals() 
    {
        Boolean sensitive = true;
        MetaData history = new MetaData();
        history.setSource("TMA");
        history.setSystemType(null);

        MatmFlight update = new MatmFlight();
        update.setLastUpdateSource("TFM");
        update.setSystemId( "TFM-FLIGHT_PLAN_INFORMATION" );
        update.setSensitiveDataExternal(sensitive);
        
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.isSensitiveDataExternal());
        
        history.setSource("FLIGHT_HUB_POSITION");
        history.setSystemType(null);
        update.setLastUpdateSource("TMA");
        update.setSystemId( null );
        update.setSensitiveDataExternal(sensitive);
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.isSensitiveDataExternal());
        
        history.setSource("TFM");
        history.setSystemType("TFM-FLIGHT_PLAN_INFORMATION");
        update.setLastUpdateSource("TMA");
        update.setSystemId( null );
        update.setSensitiveDataExternal(sensitive);
        assertFalse(rule.handleIdentical(update, null, history, field));
        assertNull(update.isSensitiveDataExternal());
        
        history.setSource("ASDEX");
        history.setSystemType(null);
        update.setLastUpdateSource("TFM");
        update.setSystemId( "TFM-FLIGHT_PLAN_INFORMATION" );
        update.setSensitiveDataExternal(sensitive);
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.isSensitiveDataExternal());

        history.setSource("TFM");
        history.setSystemType("TFM-TRACK_INFORMATION");
        update.setLastUpdateSource("TFM");
        update.setSystemId( "TFM-FLIGHT_PLAN_INFORMATION" );
        update.setSensitiveDataExternal(sensitive);
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.isSensitiveDataExternal());
        
        history.setSource("TFM");
        history.setSystemType("TFM-FLIGHT_PLAN_INFORMATION");
        update.setLastUpdateSource("TFM");
        update.setSystemId( "TFM-TRACK_INFORMATION" );
        update.setSensitiveDataExternal(sensitive);
        assertFalse(rule.handleIdentical(update, null, history, field));
        assertNull(update.isSensitiveDataExternal());
        
        // Test out the ties
        history.setSource("TFM");
        history.setSystemType("TFM-FLIGHT_PLAN_INFORMATION");
        update.setLastUpdateSource("TFM");
        update.setSystemId( "TFM-FLIGHT_PLAN_INFORMATION" );
        update.setSensitiveDataExternal(sensitive);
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.isSensitiveDataExternal());
    }
}
