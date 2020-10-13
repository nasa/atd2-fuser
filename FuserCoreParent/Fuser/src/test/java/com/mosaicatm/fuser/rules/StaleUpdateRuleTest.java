package com.mosaicatm.fuser.rules;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:config/fuser/beans.rules.test.xml")
public class StaleUpdateRuleTest
{
    private static final String FIELD1 = "departureRunwayEstimatedTime";
    
    private static final Date CORRECT_FIELD1_TIME = new Date( 50 );

    @Autowired
    @Qualifier("fuser-rules.StaleUpdateRule")
    private Rule<MatmObject> rule;
    
    @Test
    public void test()
    {
        MatmFlight update = getUpdate( FuserSource.TFM_TFDM.name(), null, 0 );

        // Don't want to filter out new source
        testSourceOverrideDifferent( FIELD1, update, null, null, 0, false );
        testUpdatedElements( update, false );
        
        // Don't want to filter out same source if newer
        update = getUpdate( FuserSource.TFM_TFDM.name(), null, 1000 );
        testSourceOverrideDifferent( FIELD1, update, FuserSource.TFM_TFDM.name(), null, 0, false );
        testUpdatedElements( update, false );
        
        // Don't want to filter out different source if newer
        update = getUpdate( FuserSource.TFM_TFDM.name(), null, 1000 );
        testSourceOverrideDifferent( FIELD1, update, FuserSource.TFM.name(), null, 0, false );
        testUpdatedElements( update, false );        
        
        // Don't want to filter out same source if slightly behind
        update = getUpdate( FuserSource.TFM_TFDM.name(), null, 1000 );
        testSourceOverrideDifferent( FIELD1, update, FuserSource.TFM_TFDM.name(), null, 1200, false );
        testUpdatedElements( update, false );
        
        // Don't want to filter out different source if more behind
        update = getUpdate( FuserSource.TFM_TFDM.name(), null, 1000 );
        testSourceOverrideDifferent( FIELD1, update, FuserSource.TFM.name(), null, 5000, false );
        testUpdatedElements( update, false );   
        
        // Do want to filter out same source if much behind
        update = getUpdate( FuserSource.TFM_TFDM.name(), null, 1000 );
        testSourceOverrideDifferent( FIELD1, update, FuserSource.TFM_TFDM.name(), null, 1700, true );
        testUpdatedElements( update, true );            
        
        // Do want to filter out different source if much behind
        update = getUpdate( FuserSource.TFM_TFDM.name(), null, 1000 );
        testSourceOverrideDifferent( FIELD1, update, FuserSource.TFM.name(), null, 70000, true );
        testUpdatedElements( update, true );
        
        //Lastly, let's test with identicals too, logic should be the same
        update = getUpdate( FuserSource.TFM.name(), null, 2000 );
        testSourceOverrideIdentical( FIELD1, update, FuserSource.TFM.name(), null, 1000, false );
        testUpdatedElements( update, false );        
        
        update = getUpdate( FuserSource.TFM.name(), null, 0 );
        testSourceOverrideIdentical( FIELD1, update, FuserSource.TFM.name(), null, 1000, true );
        testUpdatedElements( update, true );             
    }
    
    private MatmFlight getUpdate( String source, String system, long updateTime )
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureRunwayEstimatedTime( CORRECT_FIELD1_TIME );
        update.setLastUpdateSource( source );
        update.setSystemId( system );
        update.setTimestamp( new Date( updateTime ));

        return( update );
    }
    
    private void testSourceOverrideDifferent( String field, MatmFlight update, 
            String historySource, String historySystem, long historyTimestamp, boolean expectFilteredOut )
    {
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        
        if( historySource != null )
        {
            history.setSource(historySource);
            history.setSystemType(historySystem);
            history.setTimestamp(new Date( historyTimestamp ));
        }
        
        if( expectFilteredOut )
        {
            assertFalse(rule.handleDifference(update, target, history, field));
        }
        else
        {
            assertTrue(rule.handleDifference(update, target, history, field));
        }             
    }
    
    private void testSourceOverrideIdentical( String field, MatmFlight update, 
            String historySource, String historySystem, long historyTimestamp, boolean expectFilteredOut )
    {
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        
        if( historySource != null )
        {
            history.setSource(historySource);
            history.setSystemType(historySystem);
            history.setTimestamp(new Date( historyTimestamp ));
        }
        
        if( expectFilteredOut )
        {
            assertFalse(rule.handleIdentical(update, target, history, field));
        }
        else
        {
            assertTrue(rule.handleIdentical(update, target, history, field));
        }             
    }    
    
    private void testUpdatedElements( MatmFlight update, boolean expectFilteredOut )
    {    
        if( expectFilteredOut )
        {
            assertNull(update.getDepartureRunwayEstimatedTime());
        }
        else
        {
            assertEquals(CORRECT_FIELD1_TIME, update.getDepartureRunwayEstimatedTime());
        }
    }    
}
