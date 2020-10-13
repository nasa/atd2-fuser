package com.mosaicatm.fuser.rules;

import static org.junit.Assert.*;

import java.util.Date;

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
public class DepartureStandEarliestTimeRuleTest 
{
    private final String field = "departureStandEarliestTime";

    @Autowired
    @Qualifier("fuser-rules.DepartureStandEarliestTimeRule")
    private Rule<MatmFlight> rule;
    
    @Test
    public void testDifference() 
    {
        MetaData history = new MetaData();
        history.setSource("TFM_TFDM");
        history.setSystemType("TFM_TFDM");

        MatmFlight update = new MatmFlight();
        Date date = new Date();
        update.setLastUpdateSource("AIRLINE");
        update.setDepartureStandEarliestTime(date);
        
        assertFalse(rule.handleDifference(update, null, history, field));
        assertNull( update.getDepartureStandEarliestTime() );
        
        history.setSource("AIRLINE");
        history.setSystemType("AIRLINE");
        update.setLastUpdateSource("TFM_TFDM");
        update.setSystemId("TFM_TFDM");
        update.setDepartureStandEarliestTime(date);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getDepartureStandEarliestTime());
        
        history.setSource("AIRLINE");
        history.setSystemType( "AIRLINE" );
        update.setLastUpdateSource("AIRLINE");
        update.setSystemId( "AIRLINE" ); 
        update.setDepartureStandEarliestTime(date);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getDepartureStandEarliestTime());
        
        history.setSource("AIRLINE");
        history.setSystemType( null );
        update.setLastUpdateSource("AIRLINE");
        update.setSystemId( "AIRLINE" );    
        update.setDepartureStandEarliestTime(date);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getDepartureStandEarliestTime());
        
        history.setSource("TFM_TFDM");
        history.setSystemType("TFM_TFDM");
        update.setLastUpdateSource("TFM_TFDM");
        update.setSystemId( "TFM_TFDM" );    
        update.setDepartureStandEarliestTime(date);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getDepartureStandEarliestTime());
    }
    
    @Test
    public void testIdenticals() 
    {
        MetaData history = new MetaData();
        history.setSource("TFM_TFDM");
        history.setSystemType( "TFM_TFDM" );

        MatmFlight update = new MatmFlight();
        Date date = new Date();
        update.setLastUpdateSource("AIRLINE");
        update.setDepartureStandEarliestTime(date);
        
        assertFalse(rule.handleIdentical(update, null, history, field));
        assertNull(update.getDepartureStandEarliestTime());
        
        history.setSource("AIRLINE");
        history.setSystemType( "AIRLINE" );
        update.setLastUpdateSource("TFM_TFDM");
        update.setSystemId( "TFM_TFDM" );   
        update.setDepartureStandEarliestTime(date);
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.getDepartureStandEarliestTime());
        
        history.setSource("TFM_TFDM");
        history.setSystemType( "TFM_TFDM" );        
        update.setLastUpdateSource("AIRLINE");
        update.setLastUpdateSource("AIRLINE");
        update.setDepartureStandEarliestTime(date);
        assertFalse(rule.handleIdentical(update, null, history, field));
        assertNull(update.getDepartureStandEarliestTime());
        
        history.setSource("TFM_TFDM");
        history.setSystemType( null );    
        update.setLastUpdateSource("FMC");
        update.setDepartureStandEarliestTime(date);
        assertFalse(rule.handleIdentical(update, null, history, field));
        assertNull(update.getDepartureStandEarliestTime());
        
    }
}
