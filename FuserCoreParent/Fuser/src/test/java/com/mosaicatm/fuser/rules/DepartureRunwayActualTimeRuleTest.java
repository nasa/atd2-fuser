package com.mosaicatm.fuser.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.fuser.common.matm.util.MatmIdLookupFactory;
import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightIdLookup;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;

public class DepartureRunwayActualTimeRuleTest
{
    private static final String FIELD_DEPARTURE_RUNWAY_ACTUAL_TIME = "departureRunwayActualTime.value";
    
    private MultiplePriorityMediationRule<MatmFlight> rule;
    private ObjectFactory objectFactory;
    
    @Before
    public void setup()
    {
        objectFactory = new ObjectFactory();
        
        Map<String, MatmIdLookup<?, String>> lookupMap = new HashMap<>();
        lookupMap.put(MatmFlight.class.getName(), new MatmFlightIdLookup());
        
        MatmIdLookupFactory lookupFactory = new MatmIdLookupFactory();
        lookupFactory.setLookups(lookupMap);
        
        List<String> includes = new ArrayList<>();
        includes.add(FIELD_DEPARTURE_RUNWAY_ACTUAL_TIME);
        
        rule = new MultiplePriorityMediationRule<MatmFlight>();
        rule.setActive(true);
        rule.setPriority(10);
        rule.setName("DepartureRunwayActualTimeRuleTest");
        rule.setIdLookup(lookupFactory);
        rule.setSourcePriorityListFromCsv("FMC,SMES,AIRLINE_FLIGHTHUB,TFM_TFDM,TFM_FLIGHT_MODIFY,TFM,TMA");
        rule.setIncludes(includes);
    }
    
    @Test
    public void testPriority()
    {
        Date updateTime = new Date(5);
        
        MatmFlight update = getUpdate(FuserSource.TFM.name(), "test", updateTime);
        testSourceOverride (FIELD_DEPARTURE_RUNWAY_ACTUAL_TIME, update, null, null, false);
        testUpdatedElements(update, false, updateTime);
        
        updateTime = new Date(10);
        update = getUpdate(FuserSource.SMES.name(), "test", updateTime);
        testSourceOverride (FIELD_DEPARTURE_RUNWAY_ACTUAL_TIME, update, FuserSource.TFM.name(), null, false);
        testUpdatedElements(update, false, updateTime);

        updateTime = new Date(15);
        update = getUpdate(FuserSource.TFM.name(), "test", updateTime);
        testSourceOverride (FIELD_DEPARTURE_RUNWAY_ACTUAL_TIME, update, FuserSource.SMES.name(), null, true);
        testUpdatedElements(update, true, null);
        
        updateTime = new Date(20);
        update = getUpdate(FuserSource.FMC.name(), "test", updateTime);
        testSourceOverride (FIELD_DEPARTURE_RUNWAY_ACTUAL_TIME, update, FuserSource.SMES.name(), null, false);
        testUpdatedElements(update, false, updateTime);
        
        updateTime = new Date(25);
        update = getUpdate(FuserSource.SMES.name(), "test", updateTime);
        testSourceOverride (FIELD_DEPARTURE_RUNWAY_ACTUAL_TIME, update, FuserSource.FMC.name(), null, true);
        testUpdatedElements(update, true, null);
        
        updateTime = new Date(30);
        update = getUpdate(FuserSource.FMC.name(), "test", updateTime);
        testSourceOverride (FIELD_DEPARTURE_RUNWAY_ACTUAL_TIME, update, FuserSource.FMC.name(), null, false);
        testUpdatedElements(update, false, updateTime);
        
        
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
    
    private void testUpdatedElements( MatmFlight update, boolean expectFilteredOut, Date expectedTime )
    {    
        if( expectFilteredOut )
        {
            assertNull(update.getDepartureRunwayActualTime());              
        }
        else
        {
            assertEquals(expectedTime, update.getDepartureRunwayActualTime().getValue());        
        }
    }
    
    private MatmFlight getUpdate(String source, String systemId, Date date)
    {
        MatmFlight update = new MatmFlight();
        update.setGufi("test_gufi");
        update.setLastUpdateSource(source);
        update.setSystemId(systemId);
        update.setDepartureRunwayActualTime(getJaxbDate(date));
        
        return update;
    }
    
    private JAXBElement<Date> getJaxbDate (Date date)
    {
        return objectFactory.createMatmFlightDepartureRunwayActualTime(date);
    }
}
