package com.mosaicatm.fuser.rules;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mosaicatm.fuser.transform.matm.airline.AirlineDataSource;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.TfmMessageTypeType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:config/fuser/beans.rules.test.xml")
public class AerodromeRuleTest
{
    private static final String FIELD1 = "departureAerodrome.iataName";
    private static final String FIELD2 = "departureAerodrome.icaoName";
    
    private static final String IATA_FIELD = "IATA";
    private static final String ICAO_FIELD = "ICAO";
    
    @Autowired
    @Qualifier("fuser-rules.AerodromeRule")
    private Rule<MatmFlight> rule;
    
    @Test
    public void test()
    {
        MatmFlight update = getUpdate( FuserSource.TFM.name(), "TFM-" + TfmMessageTypeType.FLIGHT_MODIFY.name() );

        // Don't want to filter out new source
        testSourceOverride( FIELD1, update, null, null, false );
        testSourceOverride( FIELD2, update, null, null, false );
        testUpdatedElements( update, false );
        
        // Don't want to filter out same source
        update = getUpdate( FuserSource.TFM.name(), "TFM-" + TfmMessageTypeType.FLIGHT_MODIFY.name());
        testSourceOverride( FIELD1, update, FuserSource.TFM.name(), "TFM-" + TfmMessageTypeType.FLIGHT_MODIFY.name(), false );
        testSourceOverride( FIELD2, update, FuserSource.TFM.name(), "TFM-" + TfmMessageTypeType.FLIGHT_MODIFY.name(), false );
        testUpdatedElements( update, false );

        // TFM FM message > FlightStats source
        update = getUpdate( FuserSource.AIRLINE.name(),AirlineDataSource.FLIGHTSTATS.name() );
        testSourceOverride( FIELD1, update, FuserSource.TFM.name(), "TFM-" + TfmMessageTypeType.FLIGHT_MODIFY.name(), true );
        testSourceOverride( FIELD2, update, FuserSource.TFM.name(), "TFM-" + TfmMessageTypeType.FLIGHT_MODIFY.name(), true );
        testUpdatedElements( update, true );
       
        // TFM FM message < TFM FZ message
        update = getUpdate( FuserSource.TFM.name(),"TFM-" + TfmMessageTypeType.FLIGHT_PLAN_INFORMATION.name() );
        testSourceOverride( FIELD1, update, FuserSource.TFM.name(), "TFM-" + TfmMessageTypeType.FLIGHT_MODIFY.name(), false );
        testSourceOverride( FIELD2, update, FuserSource.TFM.name(), "TFM-" + TfmMessageTypeType.FLIGHT_MODIFY.name(), false );
        testUpdatedElements( update, false );
        
        // TFM FM message > FMC source
        update = getUpdate( FuserSource.FMC.name(), null );
        testSourceOverride( FIELD1, update, FuserSource.TFM.name(), "TFM-" + TfmMessageTypeType.FLIGHT_MODIFY.name(), true );
        testSourceOverride( FIELD2, update, FuserSource.TFM.name(), "TFM-" + TfmMessageTypeType.FLIGHT_MODIFY.name(), true );
        testUpdatedElements( update, true );
        
        // TFM > FMC source
        update = getUpdate( FuserSource.FMC.name(), null );
        testSourceOverride( FIELD1, update, FuserSource.TFM.name(), "TFM-" + TfmMessageTypeType.FLIGHT_MODIFY.name(), true );
        testSourceOverride( FIELD2, update, FuserSource.TFM.name(), "TFM-" + TfmMessageTypeType.FLIGHT_MODIFY.name(), true );
        testUpdatedElements( update, true );
    }
    
    private MatmFlight getUpdate( String source, String system )
    {
        MatmFlight update = new MatmFlight();
        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setIataName( IATA_FIELD );
        aerodrome.setIcaoName( ICAO_FIELD );
        update.setDepartureAerodrome( aerodrome );
        update.setLastUpdateSource( source );
        update.setSystemId( system );

        return( update );
    }
    
    private void testSourceOverride( String field, MatmFlight update, 
            String historySource, String historySystem, boolean expectFilteredOut )
    {
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        
        if( historySource != null )
        {
            history.setSource(historySource);
            history.setSystemType(historySystem);
            history.setFieldName(field);
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
    
    private void testUpdatedElements( MatmFlight update, boolean expectFilteredOut )
    {    
        if( expectFilteredOut )
        {
            assertNull(update.getDepartureAerodrome().getIataName());
            assertNull(update.getDepartureAerodrome().getIcaoName());              
        }
        else
        {
            assertEquals(IATA_FIELD, update.getDepartureAerodrome().getIataName());
            assertEquals(ICAO_FIELD, update.getDepartureAerodrome().getIcaoName());
        }
    }    
}
