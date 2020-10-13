package com.mosaicatm.fuser.rules;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
public class RouteOfFlightRuleTest 
{
    private final String field = "routeText";

    @Autowired
    @Qualifier("fuser-rules.RouteOfFlightRule")
    private Rule<MatmFlight> rule;
    
    @Test
    public void testDifference() 
    {
        MetaData history = new MetaData();
        history.setSource("TFM");

        MatmFlight update = new MatmFlight();
        String route = "CLT..DFW";
        update.setLastUpdateSource("TMA");
        update.setRouteText(route);
        
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull( update.getRouteText() );  
        
        history.setSystemType( "TFM-FLIGHT_PLAN_INFORMATION" );
        update.setRouteText(route);
        assertFalse(rule.handleDifference(update, null, history, field));
        assertNull(update.getRouteText());           
        
        history.setSource("TMA");
        history.setSystemType(null);
        update.setLastUpdateSource("ASDEX");
        update.setRouteText(route);
        assertFalse(rule.handleDifference(update, null, history, field));
        assertNull(update.getRouteText());        
        
        history.setSource("TMA");
        update.setLastUpdateSource("TMA");
        update.setRouteText(route);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getRouteText());
        
        history.setSource("ASDEX");
        update.setLastUpdateSource("ASDEX");
        update.setRouteText(route);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getRouteText());
        
        history.setSource("ASDEX");
        update.setLastUpdateSource("TFM");
        update.setRouteText(route);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getRouteText());      
        
        history.setSource("TMA");
        update.setLastUpdateSource("ASDEX");
        update.setRouteText(route);
        assertFalse(rule.handleDifference(update, null, history, field));
        assertNull(update.getRouteText());    
        
        // Test out the ties
        history.setSource("TFM");
        update.setLastUpdateSource("TFM");
        update.setRouteText(route);
        assertTrue(rule.handleDifference(update, null, history, field));
        assertNotNull(update.getRouteText());
    }
    
    @Test
    public void testIdenticals() 
    {
        String route = "CLT..DFW";
        MetaData history = new MetaData();
        history.setSource("TMA");

        MatmFlight update = new MatmFlight();
        update.setLastUpdateSource("TFM");
        update.setSystemId( "TFM-FLIGHT_PLAN_INFORMATION" );
        update.setRouteText(route);
        
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.getRouteText());
        
        history.setSource("FLIGHT_HUB_POSITION");
        update.setLastUpdateSource("TMA");
        update.setRouteText(route);
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.getRouteText());
        
        history.setSource("TFM");
        history.setSystemType( "TFM-FLIGHT_PLAN_INFORMATION" );
        update.setLastUpdateSource("TMA");
        update.setRouteText(route);
        assertFalse(rule.handleIdentical(update, null, history, field));
        assertNull(update.getRouteText());
        
        history.setSource("ASDEX");
        update.setLastUpdateSource("TFM");
        update.setRouteText(route);
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.getRouteText());
        
        // Test out the ties
        history.setSource("TFM");
        update.setLastUpdateSource("TFM");
        update.setRouteText(route);
        assertTrue(rule.handleIdentical(update, null, history, field));
        assertNotNull(update.getRouteText());
    }
    
    @Test
    public void testIdenticalsBeingAllowed() throws ParseException 
    {
        // This happened in in the database, and it should not have
        // Unfortunately this unit test passes.
        //2018-09-23 17:40:53   TFM-FLIGHT_SCHEDULE_ACTIVATE              KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR
        //2018-09-24 16:24:34   TFM-FLIGHT_PLAN_INFORMATION               KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR/0117
        //2018-09-24 17:03:06   TFM-FLIGHT_PLAN_AMENDMENT_INFORMATION     KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR/0117
        //2018-09-24 17:28:11   TMA.ZDC.FAA.GOV-SWIM                      KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR/0117        
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        MetaData history = new MetaData();
        history.setSource("TFM");
        history.setSystemType( "TFM-FLIGHT_PLAN_AMENDMENT_INFORMATION");
        history.setFieldName( "routeText" );
        history.setTimestamp( sdf.parse( "2018-09-24 17:03:06" ));

        MatmFlight target = new MatmFlight();
        target.setLastUpdateSource("TFM");
        target.setSystemId("TFM-FLIGHT_PLAN_AMENDMENT_INFORMATION");        
        target.setRouteText( "KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR/0117" );
        target.setTimestamp( sdf.parse( "2018-09-24 17:03:06" ));        
        
        MatmFlight update = new MatmFlight();
        update.setTimestamp( sdf.parse( "2018-09-24 17:28:11" ));
        update.setLastUpdateSource("TMA");
        update.setSystemId("TMA.ZDC.FAA.GOV-SWIM");        
        update.setRouteText( "KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR/0117" );
        update.setSensitiveData( false );

        rule.handleIdentical(update, target, history, field);
        
        assertNull( update.getRouteText() );
    }
}
