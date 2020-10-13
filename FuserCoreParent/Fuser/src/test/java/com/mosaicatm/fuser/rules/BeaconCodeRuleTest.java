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
import com.mosaicatm.matmdata.flight.extension.TfmMessageTypeType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:config/fuser/beans.rules.test.xml")
public class BeaconCodeRuleTest
{
    private static final String BEACON_CODE_FIELD = "beaconCode";
    
    private static final String BEACON_CODE = "5489";
    
    @Autowired
    @Qualifier("fuser-rules.BeaconCodeRule")
    private Rule<MatmFlight> beaconCodeRule;
    
    @Test
    public void testTfmOverIdac()
    {
        MatmFlight update = new MatmFlight();
        update.setBeaconCode(BEACON_CODE);
        update.setLastUpdateSource(FuserSource.TFM.name());
        update.setSystemId(TfmMessageTypeType.BEACON_CODE_INFORMATION.name());
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.IDAC.name());
        history.setSystemType(null);
        
        assertTrue(beaconCodeRule.handleDifference(update, target, history, BEACON_CODE_FIELD));
        
        assertEquals(BEACON_CODE, update.getBeaconCode());
        
        MatmFlight secondUpdate = new MatmFlight();
        secondUpdate.setBeaconCode(BEACON_CODE);
        secondUpdate.setLastUpdateSource(FuserSource.AIRLINE.name());
        secondUpdate.setSystemId(AirlineDataSource.FLIGHTSTATS.name());
        
        history.setSource(FuserSource.TFM.name());
        history.setSystemType(TfmMessageTypeType.FLIGHT_PLAN_AMENDMENT_INFORMATION.name());
        
        assertFalse(beaconCodeRule.handleDifference(secondUpdate, target, history, BEACON_CODE_FIELD));
        
        assertNull(secondUpdate.getBeaconCode());
    }
    
    @Test
    public void testAsdexOverTfm()
    {
        MatmFlight update = new MatmFlight();
        update.setBeaconCode(BEACON_CODE);
        update.setLastUpdateSource(FuserSource.ASDEX.name());
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.TFM.name());
        history.setSystemType(TfmMessageTypeType.BEACON_CODE_INFORMATION.name());
        
        assertTrue(beaconCodeRule.handleDifference(update, target, history, BEACON_CODE_FIELD));
        
        assertEquals(BEACON_CODE, update.getBeaconCode());
        
        MatmFlight secondUpdate = new MatmFlight();
        secondUpdate.setBeaconCode("N7777");
        secondUpdate.setLastUpdateSource(FuserSource.IDAC.name());
        secondUpdate.setLastUpdateSource(null);        
        
        history.setSource(FuserSource.ASDEX.name());
        
        assertFalse(beaconCodeRule.handleDifference(secondUpdate, target, history, BEACON_CODE_FIELD));
        
        assertNull(secondUpdate.getBeaconCode());
        assertNull(secondUpdate.getAircraftAddress());
    }    
    
    @Test
    public void testIdentical1()
    {
        // update contains no favor source
        // and store contains no favor source
        // do update
        MatmFlight update = new MatmFlight();
        update.setBeaconCode(BEACON_CODE);
        update.setLastUpdateSource(FuserSource.ASDEX.name());
        
        MatmFlight target = new MatmFlight();
        
        MetaData history = new MetaData();
        history.setSource(FuserSource.TFM.name());
        history.setSystemType(TfmMessageTypeType.BEACON_CODE_INFORMATION.name());
        
        assertTrue(beaconCodeRule.handleIdentical(update, target, history, BEACON_CODE_FIELD));
        
        assertEquals(BEACON_CODE, update.getBeaconCode());
    }   
}
