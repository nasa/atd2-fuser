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
import com.mosaicatm.matmdata.flight.ObjectFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:config/fuser/beans.rules.test.xml")
public class TfmTmiRuleTest 
{
    private final String field = "estimatedDepartureClearanceTime.value";
    
    private final ObjectFactory objectFactory = new ObjectFactory();

    @Autowired
    @Qualifier("fuser-rules.TfmTmiRule")
    private Rule<MatmFlight> rule;
    
    @Test
    public void testDifference() 
    {
        MetaData history = new MetaData();
        history.setSource("TFM");

        MatmFlight update = new MatmFlight();
        Date date = new Date();
        update.setLastUpdateSource("TMA");
        update.setEstimatedDepartureClearanceTime(objectFactory.createMatmFlightEstimatedDepartureClearanceTime( date ));
        
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull( update.getEstimatedDepartureClearanceTime() );
        
        update.setLastUpdateSource("IDAC");
        update.setEstimatedDepartureClearanceTime(objectFactory.createMatmFlightEstimatedDepartureClearanceTime( date ));        

        assertFalse(rule.handleDifference(update, null, history, field));
        assertNull( update.getEstimatedDepartureClearanceTime() );
        
        history.setSource("AIRLINE");
        update.setEstimatedDepartureClearanceTime(objectFactory.createMatmFlightEstimatedDepartureClearanceTime( date ));
        
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull( update.getEstimatedDepartureClearanceTime() );
        assertEquals( date, update.getEstimatedDepartureClearanceTime().getValue() );
        
        history.setSource("TMA");
        update.setLastUpdateSource("TFM");
        update.setEstimatedDepartureClearanceTime(objectFactory.createMatmFlightEstimatedDepartureClearanceTime( date ));
        
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull( update.getEstimatedDepartureClearanceTime() );   
        assertEquals( date, update.getEstimatedDepartureClearanceTime().getValue() );
    }
    
    @Test
    public void testDifferenceNill() 
    {
        MetaData history = new MetaData();
        history.setSource("TFM");

        MatmFlight update = new MatmFlight();
        update.setLastUpdateSource("TMA");
        update.setEstimatedDepartureClearanceTime(objectFactory.createMatmFlightEstimatedDepartureClearanceTime( null ));
        update.getEstimatedDepartureClearanceTime().setNil( true );
        
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull( update.getEstimatedDepartureClearanceTime() );
        
        update.setLastUpdateSource("IDAC");
        update.setEstimatedDepartureClearanceTime(objectFactory.createMatmFlightEstimatedDepartureClearanceTime( null ));        

        assertFalse(rule.handleDifference(update, null, history, field));
        assertNull( update.getEstimatedDepartureClearanceTime() );        
        
        history.setSource("AIRLINE");
        update.setEstimatedDepartureClearanceTime(objectFactory.createMatmFlightEstimatedDepartureClearanceTime( null ));
        update.getEstimatedDepartureClearanceTime().setNil( true );
        
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull( update.getEstimatedDepartureClearanceTime() );
        assertNull( update.getEstimatedDepartureClearanceTime().getValue() );
        assertTrue( update.getEstimatedDepartureClearanceTime().isNil() );
        
        history.setSource("TMA");
        update.setLastUpdateSource("TFM");
        update.setEstimatedDepartureClearanceTime(objectFactory.createMatmFlightEstimatedDepartureClearanceTime( null ));
        update.getEstimatedDepartureClearanceTime().setNil( true );
        
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull( update.getEstimatedDepartureClearanceTime() );   
        assertNull( update.getEstimatedDepartureClearanceTime().getValue() );
        assertTrue( update.getEstimatedDepartureClearanceTime().isNil() );
    }    
    
    @Test
    public void testIdenticals() 
    {
        MetaData history = new MetaData();
        history.setSource("TFM");

        MatmFlight update = new MatmFlight();
        Date date = new Date();
        update.setLastUpdateSource("TMA");
        update.setEstimatedDepartureClearanceTime(objectFactory.createMatmFlightEstimatedDepartureClearanceTime( date ));
        
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull( update.getEstimatedDepartureClearanceTime().getValue() );
        
        update.setLastUpdateSource("IDAC");
        update.setEstimatedDepartureClearanceTime(objectFactory.createMatmFlightEstimatedDepartureClearanceTime( date ));
        
        assertFalse(rule.handleIdentical(update, null, history, field));
        assertNull( update.getEstimatedDepartureClearanceTime());
        
        history.setSource("TMA");
        update.setLastUpdateSource("TFM");
        update.setEstimatedDepartureClearanceTime(objectFactory.createMatmFlightEstimatedDepartureClearanceTime( date ));
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull( update.getEstimatedDepartureClearanceTime().getValue() );
        
        history.setSource("TFM");
        update.setLastUpdateSource("TFM");
        update.setEstimatedDepartureClearanceTime(objectFactory.createMatmFlightEstimatedDepartureClearanceTime( date ));
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull( update.getEstimatedDepartureClearanceTime().getValue() );
        
        history.setSource("AIRLINE");
        update.setLastUpdateSource("TFM");
        update.setEstimatedDepartureClearanceTime(objectFactory.createMatmFlightEstimatedDepartureClearanceTime( date ));
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull( update.getEstimatedDepartureClearanceTime().getValue() );
    }
}
