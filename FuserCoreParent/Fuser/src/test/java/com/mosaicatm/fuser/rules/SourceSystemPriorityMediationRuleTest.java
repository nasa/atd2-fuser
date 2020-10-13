package com.mosaicatm.fuser.rules;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class SourceSystemPriorityMediationRuleTest
{
    @Test
    public void testExcludes()
    {
        String[] sources = { "IDAC", "TMA" };
        String exclude_field = "arrivalRunwayEstimatedTime";
        SourceSystemPriorityMediationRule<MatmFlight> rule = new SourceSystemPriorityMediationRule<>();
        
        rule.setUpdateSources( Arrays.asList( sources ));
        rule.setFavorSystemIdSuffix( "-SWIM" );
        rule.setExcludes( Arrays.asList( exclude_field ) );
        rule.setActive( true );

        //Start with testing TFM vs. TMA, where the data should not be filtered        
        MetaData history = new MetaData();
        history.setSource( "TFM" );

        String update_field = "departureRunwayEstimatedTime";
        MatmFlight update = new MatmFlight();
        Date date = new Date();
        update.setLastUpdateSource( "TMA" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-SWIM" );
        update.setDepartureRunwayEstimatedTime( date );
        //Test a TFM vs. TMA update
        assertTrue( rule.handleDifference( update, null, history, update_field ) );
        assertTrue( update.getDepartureRunwayEstimatedTime().getTime() == date.getTime() );

        //Now, test where sources are both TMA, varying the system ID
        history.setSource( "TMA" );
        history.setSystemType( "TMA.ZDC.FAA.GOV-SWIM" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-SWIM" );
        update_field = "departureRunwayEstimatedTime";
        //Test a TMA SWIM vs. TMA SWIM update
        assertTrue( rule.handleDifference( update, null, history, update_field ) );
        assertTrue( update.getDepartureRunwayEstimatedTime().getTime() == date.getTime() );

        history.setSystemType( "TMA.ZDC.FAA.GOV-NASA" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-NASA" );
        update_field = "departureRunwayEstimatedTime";
        //Test a TMA NASA vs. TMA NASA update
        assertTrue( rule.handleDifference( update, null, history, update_field ) );
        assertTrue( update.getDepartureRunwayEstimatedTime().getTime() == date.getTime() );

        history.setSystemType( "TMA.ZDC.FAA.GOV-NASA" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-SWIM" );
        update_field = "departureRunwayEstimatedTime";
        //Test a TMA NASA vs. TMA SWIM update
        assertTrue( rule.handleDifference( update, null, history, update_field ) );
        assertTrue( update.getDepartureRunwayEstimatedTime().getTime() == date.getTime() );

        //The basic test -- that the non-excluded field is filtered out
        history.setSystemType( "TMA.ZDC.FAA.GOV-SWIM" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-NASA" );
        update_field = "departureRunwayEstimatedTime";
        //Test a TMA SWIM vs. TMA NASA update, where the SWIM field overrides the update
        assertFalse( rule.handleDifference( update, null, history, update_field ) );
        assertNull( update.getDepartureRunwayEstimatedTime() );
        
        //Ensure we cannot filter out a protected field    
        history.setSystemType( "TMA.ZDC.FAA.GOV-SWIM" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-NASA" );
        update.setTimestamp( date );
        update_field = "timestamp";
        //Test a TMA SWIM vs. TMA NASA update, where the SWIM field does NOT override the update
        assertTrue( rule.handleDifference( update, null, history, update_field ) );
        assertTrue( update.getTimestamp().getTime() == date.getTime() );
        
        //Try an update with the excluded field
        history.setSystemType( "TMA.ZDC.FAA.GOV-SWIM" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-NASA" );
        update.setArrivalRunwayEstimatedTime( date );
        update_field = "arrivalRunwayEstimatedTime";
        //Test a TMA SWIM vs. TMA NASA update, where the SWIM field does NOT override the update
        assertTrue( rule.handleDifference( update, null, history, update_field ) );
        assertTrue( update.getArrivalRunwayEstimatedTime().getTime() == date.getTime() );
    }
    
    @Test
    public void testIncludes()
    {
        String[] sources = { "IDAC", "TMA" };
        String include_field = "departureRunwayEstimatedTime";
        SourceSystemPriorityMediationRule<MatmFlight> rule = new SourceSystemPriorityMediationRule<>();
        
        rule.setUpdateSources( Arrays.asList( sources ));
        rule.setFavorSystemIdSuffix( "-NASA" );
        rule.setIncludes(Arrays.asList(include_field ) );
        rule.setActive( true );

        //Start with testing TFM vs. TMA, where the data should not be filtered        
        MetaData history = new MetaData();
        history.setSource( "TFM" );

        String update_field = "departureRunwayEstimatedTime";
        MatmFlight update = new MatmFlight();
        Date date = new Date();
        update.setLastUpdateSource( "TMA" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-SWIM" );
        update.setDepartureRunwayEstimatedTime( date );
        //Test a TFM vs. TMA update
        assertTrue( rule.handleDifference( update, null, history, update_field ) );
        assertTrue( update.getDepartureRunwayEstimatedTime().getTime() == date.getTime() );
        
        //Now, test where sources are both TMA, varying the system ID
        history.setSource( "TMA" );
        history.setSystemType( "TMA.ZDC.FAA.GOV-SWIM" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-SWIM" );
        update_field = "departureRunwayEstimatedTime";
        //Test a TMA SWIM vs. TMA SWIM update
        assertTrue( rule.handleDifference( update, null, history, update_field ) );
        assertTrue( update.getDepartureRunwayEstimatedTime().getTime() == date.getTime() );

        history.setSystemType( "TMA.ZDC.FAA.GOV-NASA" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-NASA" );
        update_field = "departureRunwayEstimatedTime";
        //Test a TMA NASA vs. TMA NASA update
        assertTrue( rule.handleDifference( update, null, history, update_field ) );
        assertTrue( update.getDepartureRunwayEstimatedTime().getTime() == date.getTime() );
        
        history.setSystemType( "TMA.ZDC.FAA.GOV-SWIM" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-NASA" );
        update_field = "departureRunwayEstimatedTime";
        //Test a TMA SWIM vs. TMA NASA update
        assertTrue( rule.handleDifference( update, null, history, update_field ) );
        assertTrue( update.getDepartureRunwayEstimatedTime().getTime() == date.getTime() );        

        //The basic test -- that the included field is filtered out
        history.setSystemType( "TMA.ZDC.FAA.GOV-NASA" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-SWIM" );
        update_field = "departureRunwayEstimatedTime";
        //Test a TMA NASA vs. TMA SWIM update
        assertFalse( rule.handleDifference( update, null, history, update_field ) );
        assertNull( update.getDepartureRunwayEstimatedTime() );        
    }
    
    @Test
    public void testIdenticals()
    {
    	String[] sources = { "IDAC", "TMA" };
        String include_field = "departureRunwayEstimatedTime";
        SourceSystemPriorityMediationRule<MatmFlight> rule = new SourceSystemPriorityMediationRule<>();
        
        rule.setUpdateSources( Arrays.asList( sources ));
        rule.setFavorSystemIdSuffix( "-NASA" );
        rule.setIncludes(Arrays.asList(include_field ) );
        rule.setActive( true );

        MetaData history = new MetaData();
        history.setSource( "TFM" );

        String update_field = "departureRunwayEstimatedTime";
        MatmFlight update = new MatmFlight();
        Date date = new Date();
        update.setLastUpdateSource( "TMA" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-SWIM" );
        update.setDepartureRunwayEstimatedTime( date );
        //Test a TFM vs. TMA update
        assertTrue( rule.handleIdentical( update, null, history, update_field ) );
        assertNotNull(update.getDepartureRunwayEstimatedTime());
        
        
        history.setSystemType( "TMA.ZDC.FAA.GOV-SWIM" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-NASA" );
        update_field = "departureRunwayEstimatedTime";
        //Test a TMA SWIM vs. TMA NASA update
        assertTrue( rule.handleIdentical( update, null, history, update_field ) );
        assertNotNull(update.getDepartureRunwayEstimatedTime());

        //The basic test -- that the included field is filtered out
        history.setSystemType( "TMA.ZDC.FAA.GOV-NASA" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-SWIM" );
        update_field = "departureRunwayEstimatedTime";
        //Test a TMA NASA vs. TMA SWIM update
        assertFalse( rule.handleIdentical( update, null, history, update_field ) );
        assertNull(update.getDepartureRunwayEstimatedTime());
        
        update.setLastUpdateSource( "TFM" );
        update.setSystemId( "TMA.ZDC.FAA.GOV-SWIM" );
        update.setDepartureRunwayEstimatedTime( date );
        //Test a TFM vs. TMA update
        assertTrue( rule.handleIdentical( update, null, history, update_field ) );
        assertNotNull(update.getDepartureRunwayEstimatedTime());

    }
}
