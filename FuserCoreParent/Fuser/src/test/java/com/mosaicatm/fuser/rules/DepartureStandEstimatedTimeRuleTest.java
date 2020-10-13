package com.mosaicatm.fuser.rules;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mosaicatm.fuser.transform.matm.airline.AirlineDataSource;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:config/fuser/beans.rules.test.xml")
public class DepartureStandEstimatedTimeRuleTest
{
    private static final String FIELD = "departureStandEstimatedTime";

    @Autowired
    @Qualifier("fuser-rules.DepartureStandEstimatedTimeRule")
    private Rule<MatmFlight> rule;
    
    private final Date currentDate = new Date();
    
    @Test
    public void testFlightStatsOverFlightStats()
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureStandEstimatedTime(currentDate);
        update.setLastUpdateSource(FuserSource.AIRLINE.name());
        update.setSystemId(AirlineDataSource.FLIGHTSTATS.name());
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.AIRLINE.name());
        history.setSystemType(AirlineDataSource.FLIGHTSTATS.name());
        
        rule.handleDifference(update, target, history, FIELD);
        
        assertEquals(currentDate, update.getDepartureStandEstimatedTime());
        
        assertTrue(rule.handleIdentical(update, target, history, FIELD));
    }
    
    @Test
    public void testIdenticalLowerPrioritySourceFilteredOut()
    {
        MatmFlight target = new MatmFlight();
        target.setDepartureStandEstimatedTime(currentDate);
        target.setLastUpdateSource(FuserSource.AIRLINE.name());
        target.setSystemId(AirlineDataSource.FLIGHTSTATS.name());
        
        MatmFlight update = new MatmFlight();
        update.setDepartureStandEstimatedTime(currentDate);
        update.setLastUpdateSource(FuserSource.TFM_TFDM.name());
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.AIRLINE.name());
        history.setSystemType(AirlineDataSource.FLIGHTSTATS.name());
        
        rule.handleIdentical(update, target, history, FIELD);
        
        assertNull(update.getDepartureStandEstimatedTime());
        assertFalse(rule.handleIdentical(update, target, history, FIELD));
    }    
    
    @Test
    public void testTmaBelowFlightStats()
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureStandEstimatedTime(currentDate);
        update.setLastUpdateSource(FuserSource.TMA.name());
        update.setSystemId("TMA.TEST.SYSTEM");
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.AIRLINE.name());
        history.setSystemType(AirlineDataSource.FLIGHTSTATS.name());
        
        rule.handleDifference(update, target, history, FIELD);
        
        assertNull(update.getDepartureStandEstimatedTime());
        
        assertFalse(rule.handleIdentical(update, target, history, FIELD));

    }
    
    @Test
    public void testNullHistory()
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureStandEstimatedTime(currentDate);
        update.setLastUpdateSource(FuserSource.TMA.name());
        update.setSystemId("TMA.TEST.SYSTEM");
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = null;
        
        rule.handleDifference(update, target, history, FIELD);
        
        assertEquals(currentDate, update.getDepartureStandEstimatedTime());
        
        assertTrue(rule.handleIdentical(update, target, history, FIELD));

    }

}
