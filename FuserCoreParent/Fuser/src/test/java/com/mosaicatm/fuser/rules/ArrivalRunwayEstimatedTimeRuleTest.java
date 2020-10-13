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
public class ArrivalRunwayEstimatedTimeRuleTest 
{
    private final String field = "arrivalRunwayEstimatedTime";

    @Autowired
    @Qualifier("fuser-rules.ArrivalRunwayEstimatedTimeRule")
    private Rule<MatmFlight> rule;
    
    @Test
    public void testDifference() 
    {
        MetaData history = new MetaData();
        history.setSource("TMA");

        MatmFlight update = new MatmFlight();
        Date date = new Date();
        update.setLastUpdateSource("TFM");
        update.setArrivalRunwayEstimatedTime(date);
        
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getArrivalRunwayEstimatedTime());   
        
        history.setSource("TFM");
        update.setLastUpdateSource("TMA");
        assertFalse(rule.handleDifference(update, null, history, field));
        assertNull(update.getArrivalRunwayEstimatedTime());

        history.setSource("TMA");
        update.setLastUpdateSource("AIRLINE");
        update.setArrivalRunwayEstimatedTime(date);
        assertFalse(rule.handleDifference(update, null, history, field));
        assertNull(update.getArrivalRunwayEstimatedTime());
        
        history.setSource("AIRLINE");
        update.setLastUpdateSource("TMA");
        update.setArrivalRunwayEstimatedTime(date);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertTrue(update.getArrivalRunwayEstimatedTime().getTime() == date.getTime());
        
        history.setSource("AIRLINE");
        update.setLastUpdateSource("FMC");
        update.setArrivalRunwayEstimatedTime(date);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertTrue(update.getArrivalRunwayEstimatedTime().getTime() == date.getTime());
        
    }
    
    @Test
    public void testIdenticals() 
    {
        MetaData history = new MetaData();
        history.setSource("TMA");

        MatmFlight update = new MatmFlight();
        Date date = new Date();
        update.setLastUpdateSource("TFM");
        update.setArrivalRunwayEstimatedTime(date);
        
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.getArrivalRunwayEstimatedTime());
        
        history.setSource("TFM");
        update.setLastUpdateSource("TMA");
        assertFalse(rule.handleIdentical(update, null, history, field));
        assertNull(update.getArrivalRunwayEstimatedTime());
        
        history.setSource("TMA");
        update.setLastUpdateSource("AIRLINE");
        update.setArrivalRunwayEstimatedTime(date);
        assertFalse(rule.handleIdentical(update, null, history, field));
        assertNull(update.getArrivalRunwayEstimatedTime());
        
        history.setSource("AIRLINE");
        update.setLastUpdateSource("TMA");
        update.setArrivalRunwayEstimatedTime(date);
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.getArrivalRunwayEstimatedTime());
        
        history.setSource("AIRLINE");
        update.setLastUpdateSource("FMC");
        update.setArrivalRunwayEstimatedTime(date);
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.getArrivalRunwayEstimatedTime());
        
    }
}
