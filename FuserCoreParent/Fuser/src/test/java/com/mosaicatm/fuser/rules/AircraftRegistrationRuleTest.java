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
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:config/fuser/beans.rules.test.xml")
public class AircraftRegistrationRuleTest
{
    private static final String TAIL_NUMBER_FIELD = "aircraftRegistration";
    private static final String MODE_S_FIELD = "aircraftAddress";
    
    private static final String TAIL_NUMBER = "N1234";
    private static final String MODE_S = "11558888";
    
    @Autowired
    @Qualifier("fuser-rules.AircraftRegistrationRule")
    private Rule<MatmFlight> aircraftRegistrationRule;
    
    @Test
    public void testFlightHubOverFlightStats()
    {
        MatmFlight update = new MatmFlight();
        update.setAircraftRegistration(TAIL_NUMBER);
        update.setAircraftAddress(MODE_S);
        update.setLastUpdateSource(FuserSource.AIRLINE.name());
        update.setSystemId(AirlineDataSource.FLIGHTHUB.name());
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.AIRLINE.name());
        history.setSystemType(AirlineDataSource.FLIGHTSTATS.name());
        
        assertTrue(aircraftRegistrationRule.handleDifference(update, target, history, TAIL_NUMBER_FIELD));
        assertTrue(aircraftRegistrationRule.handleDifference(update, target, history, MODE_S_FIELD));
        
        assertEquals(TAIL_NUMBER, update.getAircraftRegistration());
        assertEquals(MODE_S, update.getAircraftAddress());
        
        MatmFlight secondUpdate = new MatmFlight();
        secondUpdate.setAircraftRegistration(TAIL_NUMBER);
        secondUpdate.setAircraftAddress(MODE_S);
        secondUpdate.setLastUpdateSource(FuserSource.AIRLINE.name());
        secondUpdate.setSystemId(AirlineDataSource.FLIGHTSTATS.name());
        
        history.setSource(FuserSource.AIRLINE.name());
        history.setSystemType(AirlineDataSource.FLIGHTHUB.name());
        
        assertFalse(aircraftRegistrationRule.handleDifference(secondUpdate, target, history, TAIL_NUMBER_FIELD));
        assertFalse(aircraftRegistrationRule.handleDifference(secondUpdate, target, history, MODE_S_FIELD));
        
        assertNull(secondUpdate.getAircraftRegistration());
        assertNull(secondUpdate.getAircraftAddress());
    }
    
    @Test
    public void testTfmTfdmOverFlightHub()
    {
        MatmFlight update = new MatmFlight();
        update.setAircraftRegistration(TAIL_NUMBER);
        update.setAircraftAddress(MODE_S);
        update.setLastUpdateSource(FuserSource.TFM_TFDM.name());
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.AIRLINE.name());
        history.setSystemType(AirlineDataSource.FLIGHTHUB.name());
        
        assertTrue(aircraftRegistrationRule.handleDifference(update, target, history, TAIL_NUMBER_FIELD));
        assertTrue(aircraftRegistrationRule.handleDifference(update, target, history, MODE_S_FIELD));
        
        assertEquals(TAIL_NUMBER, update.getAircraftRegistration());
        assertEquals(MODE_S, update.getAircraftAddress());
        
        MatmFlight secondUpdate = new MatmFlight();
        secondUpdate.setAircraftRegistration("N7777");
        secondUpdate.setAircraftAddress("BOOOO");
        secondUpdate.setLastUpdateSource(FuserSource.AIRLINE.name());
        secondUpdate.setLastUpdateSource(AirlineDataSource.FLIGHTHUB.name());        
        
        history.setSource(FuserSource.TFM_TFDM.name());
        
        assertFalse(aircraftRegistrationRule.handleDifference(secondUpdate, target, history, TAIL_NUMBER_FIELD));
        assertFalse(aircraftRegistrationRule.handleDifference(secondUpdate, target, history, MODE_S_FIELD));
        
        assertNull(secondUpdate.getAircraftRegistration());
        assertNull(secondUpdate.getAircraftAddress());
    }    
    
    @Test
    public void testFlightHubPositionOverOther()
    {
        MatmFlight update = new MatmFlight();
        update.setAircraftRegistration(TAIL_NUMBER);
        update.setAircraftAddress(MODE_S);
        update.setLastUpdateSource(FuserSource.FLIGHTHUB_POSITION.name());
        update.setSystemId(FuserSource.FLIGHTHUB_POSITION.name());
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.TFM.name());
        history.setSystemType(FuserSource.TFM.name());
        
        assertTrue(aircraftRegistrationRule.handleDifference(update, target, history, TAIL_NUMBER_FIELD));
        assertTrue(aircraftRegistrationRule.handleDifference(update, target, history, MODE_S_FIELD));
        
        assertEquals(TAIL_NUMBER, update.getAircraftRegistration());
        assertEquals(MODE_S, update.getAircraftAddress());
        
        MatmFlight secondUpdate = new MatmFlight();
        secondUpdate.setAircraftRegistration(TAIL_NUMBER);
        secondUpdate.setAircraftAddress(MODE_S);
        secondUpdate.setLastUpdateSource(FuserSource.TMA.name());
        secondUpdate.setSystemId("TMA.ZNY.FAA.GOV-NASA");

        history.setSource(FuserSource.FLIGHTHUB_POSITION.name());
        history.setSystemType(FuserSource.FLIGHTHUB_POSITION.name());
        
        assertFalse(aircraftRegistrationRule.handleDifference(secondUpdate, target, history, TAIL_NUMBER_FIELD));
        assertFalse(aircraftRegistrationRule.handleDifference(secondUpdate, target, history, MODE_S_FIELD));
        
        assertNull(secondUpdate.getAircraftRegistration());
        assertNull(secondUpdate.getAircraftAddress());
    }
    
    @Test
    public void testDoesNotApplyToOtherField()
    {
        MatmFlight update = new MatmFlight();
        update.setAircraftRegistration(TAIL_NUMBER);
        update.setAircraftAddress(MODE_S);
        update.setLastUpdateSource(FuserSource.FLIGHTHUB_POSITION.name());
        update.setSystemId(FuserSource.FLIGHTHUB_POSITION.name());
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.AIRLINE.name());
        history.setSystemType(AirlineDataSource.FLIGHTHUB.name());
        
        assertTrue(aircraftRegistrationRule.handleDifference(update, target, history, "otherRandomField"));
        
        assertEquals(TAIL_NUMBER, update.getAircraftRegistration());
        assertEquals(MODE_S, update.getAircraftAddress());
    }
    
    @Test
    public void testIdentical1()
    {
        // update contains no favor source
        // and store contains no favor source
        // do update
        MatmFlight update = new MatmFlight();
        update.setAircraftRegistration(TAIL_NUMBER);
        update.setAircraftAddress(MODE_S);
        update.setLastUpdateSource(FuserSource.IDAC.name());
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.TFM.name());
        
        assertTrue(aircraftRegistrationRule.handleIdentical(update, target, history, TAIL_NUMBER_FIELD));
        assertTrue(aircraftRegistrationRule.handleIdentical(update, target, history, MODE_S_FIELD));
        
        assertEquals(TAIL_NUMBER, update.getAircraftRegistration());
        assertEquals(MODE_S, update.getAircraftAddress());
    }
    
    @Test
    public void testIdentical2()
    {
        // update contains favor source
        // but store contains no favor source
        // do update
        MatmFlight update = new MatmFlight();
        update.setAircraftRegistration(TAIL_NUMBER);
        update.setAircraftAddress(MODE_S);
        update.setLastUpdateSource(FuserSource.FLIGHTHUB_POSITION.name());
        update.setSystemId(FuserSource.FLIGHTHUB_POSITION.name());
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.TFM.name());
        
        assertTrue(aircraftRegistrationRule.handleIdentical(update, target, history, TAIL_NUMBER_FIELD));
        assertTrue(aircraftRegistrationRule.handleIdentical(update, target, history, MODE_S_FIELD));

        assertEquals(TAIL_NUMBER, update.getAircraftRegistration());
        assertEquals(MODE_S, update.getAircraftAddress());
    }
    
    @Test
    public void testIdentical3()
    {
        // update contains no favor source
        // but store contains favor source
        // do not update
        MatmFlight update = new MatmFlight();
        update.setAircraftRegistration(TAIL_NUMBER);
        update.setAircraftAddress(MODE_S);
        update.setLastUpdateSource(FuserSource.TFM.name());
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.FLIGHTHUB_POSITION.name());
        
        assertFalse(aircraftRegistrationRule.handleIdentical(update, target, history, TAIL_NUMBER_FIELD));
        assertFalse(aircraftRegistrationRule.handleIdentical(update, target, history, MODE_S_FIELD));
        
        assertNull(update.getAircraftRegistration());
        assertNull(update.getAircraftAddress());

    }
    
    @Test
    public void testIdentical4()
    {
        // update contains highest favor source
        // but store second highest favor source
        // do update
        MatmFlight update = new MatmFlight();
        update.setAircraftRegistration(TAIL_NUMBER);
        update.setAircraftAddress(MODE_S);
        update.setLastUpdateSource(FuserSource.AIRLINE.name());
        update.setSystemId(AirlineDataSource.FLIGHTHUB.name());
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.AIRLINE.name());
        history.setSystemType(AirlineDataSource.FLIGHTSTATS.name());
        
        assertTrue(aircraftRegistrationRule.handleIdentical(update, target, history, TAIL_NUMBER_FIELD));
        assertTrue(aircraftRegistrationRule.handleIdentical(update, target, history, MODE_S_FIELD));
        
        assertEquals(TAIL_NUMBER, update.getAircraftRegistration());
        assertEquals(MODE_S, update.getAircraftAddress());
    }
    
    @Test
    public void testIdentical5()
    {
        // update contains second highest favor source
        // but store contains highest favor source
        // do update
        MatmFlight update = new MatmFlight();
        update.setAircraftRegistration(TAIL_NUMBER);
        update.setAircraftAddress(MODE_S);
        update.setLastUpdateSource(FuserSource.AIRLINE.name());
        update.setSystemId(AirlineDataSource.FLIGHTSTATS.name());
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.AIRLINE.name());
        history.setSystemType(AirlineDataSource.FLIGHTHUB.name());
        
        assertFalse(aircraftRegistrationRule.handleIdentical(update, target, history, TAIL_NUMBER_FIELD));
        assertFalse(aircraftRegistrationRule.handleIdentical(update, target, history, MODE_S_FIELD));
        
        assertNull(update.getAircraftRegistration());
        assertNull(update.getAircraftAddress());
    }
}
