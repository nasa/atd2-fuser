package com.mosaicatm.fuser.aggregator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mosaicatm.fuser.rules.RuleFactory;
import com.mosaicatm.fuser.transform.matm.airline.AirlineDataSource;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * Used to test the entire aggregate spring file. This can be used to test
 * changes to the Spring without having start-up the entire IADS assembly.
 * 
 * It can also be used to test the complete flow of aggregation
 * 
 * @author irobeson
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:config/fuser/beans.aggregator.test.xml")
public class MatmDiffAggregatorSpringTest
{
    private static final String TEST_ACID = "TEST1";
    private static final String TEST_ORIGIN = "CLT";
    private static final String TEST_DESTINATION = "IAD";
    private static final String TEST_GUFI = "TEST1.CLT.IAD.1234.FLIGHTHUB";
    private static final String TEST_TAIL = "N1234";
    private static final String TEST_NEW_TAIL = "N5678";
    
    @Autowired
    @Qualifier("fuser-aggregator.core.MatmAggregator")
    private MatmDiffAggregator aggregator;
    
    @Autowired
    @Qualifier("fuser-rules.RuleFactory")
    private RuleFactory<MatmFlight> ruleFactory;
    
    @Autowired
    @Qualifier("fuser-aggregator.core.MetaDataManager")
    private MetaDataManager<MatmFlight> manager;
    
    @Before
    public void setup()
    { 
        //Check all rules for any validation errors
        this.ruleFactory.initialize();
        
    	this.manager.setRuleFactory(ruleFactory);
    }

    /**
     * Test the process of updating tail numbers.
     */
    @Test
    public void testTailNumberMediation()
    {
        MatmFlight initFlight = new MatmFlight();
        initFlight.setGufi(TEST_GUFI);
        initFlight.setAcid(TEST_ACID);
        initFlight.setDepartureAerodrome(new Aerodrome());
        initFlight.getDepartureAerodrome().setIataName(TEST_ORIGIN);
        initFlight.setArrivalAerodrome(new Aerodrome());
        initFlight.getArrivalAerodrome().setIataName(TEST_DESTINATION);
        initFlight.setAircraftRegistration(TEST_TAIL);
        initFlight.setLastUpdateSource(FuserSource.AIRLINE.toString());
        initFlight.setSystemId(AirlineDataSource.FLIGHTHUB.toString());
        
        MatmFlight newFlight = aggregator.aggregate(initFlight);
        
        assertNotNull(newFlight);
        assertEquals(TEST_TAIL, newFlight.getAircraftRegistration());
        
        MatmFlight update1 = new MatmFlight();
        update1.setGufi(TEST_GUFI);
        update1.setAcid(TEST_ACID);
        update1.setDepartureAerodrome(new Aerodrome());
        update1.getDepartureAerodrome().setIataName(TEST_ORIGIN);
        update1.setArrivalAerodrome(new Aerodrome());
        update1.getArrivalAerodrome().setIataName(TEST_DESTINATION);
        update1.setLastUpdateSource(FuserSource.TFM_TFDM.toString());
        update1.setSystemId("AAL");
        
        MatmFlight diff1 = aggregator.aggregate(update1);
        
        assertNull(diff1);
        
        MatmFlight update2 = new MatmFlight();
        update2.setGufi(TEST_GUFI);
        update2.setAcid(TEST_ACID);
        update2.setDepartureAerodrome(new Aerodrome());
        update2.getDepartureAerodrome().setIataName(TEST_ORIGIN);
        update2.setArrivalAerodrome(new Aerodrome());
        update2.getArrivalAerodrome().setIataName(TEST_DESTINATION);
        // Change tail number
        update2.setAircraftRegistration(TEST_NEW_TAIL);
        update2.setLastUpdateSource(FuserSource.AIRLINE.toString());
        update2.setSystemId(AirlineDataSource.FLIGHTHUB.toString());
        
        MatmFlight diff2 = aggregator.aggregate(update2);
        
        assertNotNull(diff2);
        assertEquals(TEST_NEW_TAIL, diff2.getAircraftRegistration());
        
        MatmFlight update3 = new MatmFlight();
        update3.setGufi(TEST_GUFI);
        update3.setAcid(TEST_ACID);
        update3.setDepartureAerodrome(new Aerodrome());
        update3.getDepartureAerodrome().setIataName(TEST_ORIGIN);
        update3.setArrivalAerodrome(new Aerodrome());
        update3.getArrivalAerodrome().setIataName(TEST_DESTINATION);
        // Change tail number
        update3.setAircraftRegistration(TEST_NEW_TAIL);
        update3.setLastUpdateSource(FuserSource.TFM_TFDM.toString());
        update3.setSystemId("AAL");
        
        MatmFlight diff3 = aggregator.aggregate(update3);
        
        assertNull(diff3);
    }
    
    @Test
    public void testRouteTextMediation() throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        MatmFlight initFlight = new MatmFlight();
        initFlight.setTimestamp( sdf.parse( "2018-09-23 17:40:53" ));
        initFlight.setGufi(TEST_GUFI);
        initFlight.setAcid(TEST_ACID);
        initFlight.setLastUpdateSource("TFM");
        initFlight.setSystemId("TFM-FLIGHT_SCHEDULE_ACTIVATE");        
        initFlight.setRouteText( "KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR" );  
        
        MatmFlight newFlight = aggregator.aggregate(initFlight);
        assertNotNull(newFlight); 
        
        assertEquals("KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR", newFlight.getRouteText());
        
        MatmFlight update1 = new MatmFlight();
        update1.setGufi(TEST_GUFI);
        update1.setAcid(TEST_ACID);  
        update1.setTimestamp( sdf.parse( "2018-09-24 16:24:34" ));
        update1.setLastUpdateSource("TFM");
        update1.setSystemId("TFM-FLIGHT_PLAN_INFORMATION");        
        update1.setRouteText( "KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR/0117" );   
        update1.setDepartureFixSourceData("KILNS");
        
        MatmFlight diff1 = aggregator.aggregate(update1);
        assertNotNull(diff1); 
        assertNotNull(diff1.getDepartureFixSourceData()); 
        assertEquals("KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR/0117", diff1.getRouteText());         
        
        
        MatmFlight update1a = new MatmFlight();
        update1a.setGufi(TEST_GUFI);
        update1a.setAcid(TEST_ACID);  
        update1a.setTimestamp( sdf.parse( "2018-09-24 16:24:37" ));
        update1a.setLastUpdateSource("TMA");
        update1a.setSystemId("TMA.ZTL.FAA.GOV-NASA");    
        update1a.setRouteText( "KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR/0117" );        
        update1a.setArrivalRunwayScratchpad( "18L" );
        
        MatmFlight diff1a = aggregator.aggregate(update1a);
        assertNotNull(diff1a); 
        assertNull(diff1a.getRouteText());           
                
        MatmFlight update2 = new MatmFlight();
        update2.setGufi(TEST_GUFI);
        update2.setAcid(TEST_ACID);  
        update2.setTimestamp( sdf.parse( "2018-09-24 17:03:06" ));
        update2.setLastUpdateSource("TFM");
        update2.setSystemId("TFM-FLIGHT_PLAN_AMENDMENT_INFORMATION");        
        update2.setRouteText( "KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR/0117" );    
        update2.setDepartureFixSourceData( "KILNS" );
        update2.setAltitudeFiled( 44.);
        
        MatmFlight diff2 = aggregator.aggregate(update2);
        assertNotNull(diff2); 
        assertNotNull(diff2.getDepartureFixSourceData()); 
        assertEquals("KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR/0117", diff2.getRouteText()); 

        MatmFlight update2aa = new MatmFlight();
        update2aa.setGufi(TEST_GUFI);
        update2aa.setAcid(TEST_ACID);  
        update2aa.setTimestamp( sdf.parse( "2018-09-24 17:08:58" ));
        update2aa.setLastUpdateSource("TFM");
        update2aa.setSystemId("TFM-FLIGHT_TIMES");    
        update2aa.setDepartureRunwayEstimatedTime( new Date() );
        
        MatmFlight diff2aa = aggregator.aggregate(update2aa);
        assertNotNull(diff2aa); 
        assertNotNull(diff2aa.getDepartureRunwayEstimatedTime());
        assertNull(diff2aa.getRouteText());          
        
        MatmFlight update2a = new MatmFlight();
        update2a.setGufi(TEST_GUFI);
        update2a.setAcid(TEST_ACID);  
        update2a.setTimestamp( sdf.parse( "2018-09-24 17:13:45" ));
        update2a.setLastUpdateSource("TMA");
        update2a.setSystemId("TMA.ZTL.FAA.GOV-SWIM");    
        update2a.setRouteText( "KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR/0117" );        
        update2a.setArrivalRunwayScratchpad( "19L" );
        update2a.setDepartureAerodrome(new Aerodrome());
        update2a.getDepartureAerodrome().setIataName(TEST_ORIGIN);
        
        MatmFlight diff2a = aggregator.aggregate(update2a);
        assertNotNull(diff2a); 
        assertNull(diff2a.getArrivalRunwayScratchpad());
        assertNotNull(diff2a.getDepartureAerodrome());
        assertNull(diff2a.getRouteText());           
        

        MatmFlight update2b = new MatmFlight();
        update2b.setGufi(TEST_GUFI);
        update2b.setAcid(TEST_ACID);  
        update2b.setTimestamp( sdf.parse( "2018-09-24 17:18:12" ));
        update2b.setLastUpdateSource("TMA");
        update2b.setSystemId("TMA.ZNY.FAA.GOV-SWIM");    
        update2b.setRouteText( "KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR/0117" );        
        update2b.setArrivalRunwayScratchpad( "19R" );
        update2b.setAircraftType( "B777");
        
        MatmFlight diff2b = aggregator.aggregate(update2b);
        assertNotNull(diff2b); 
        assertNull(diff2b.getArrivalRunwayScratchpad());
        assertNotNull(diff2b.getAircraftType());
        assertNull(diff2b.getRouteText());            
        
        MatmFlight update2c = new MatmFlight();
        update2c.setGufi(TEST_GUFI);
        update2c.setAcid(TEST_ACID);  
        update2c.setTimestamp( sdf.parse( "2018-09-24 17:19:12" ));
        update2c.setLastUpdateSource("IDAC");
        update2c.setSystemId("IDAC");    
        update2c.setRouteText( "KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR/0117" );        
        update2c.setAltitudeFiled( 555.);
        
        MatmFlight diff2c = aggregator.aggregate(update2c);
        assertNotNull(diff2c); 
        assertNotNull(diff2c.getAltitudeFiled());
        assertNull(diff2c.getRouteText()); 
        
        MatmFlight update3 = new MatmFlight();
        update3.setGufi(TEST_GUFI);
        update3.setAcid(TEST_ACID);  
        update3.setTimestamp( sdf.parse( "2018-09-24 17:28:11" ));
        update3.setLastUpdateSource("TMA");
        update3.setSystemId("TMA.ZDC.FAA.GOV-SWIM");        
        update3.setRouteText( "KCLT.KILNS3.AUDII..FAK.PHLBO3.KEWR/0117" ); 
        update3.setArrivalRunwayScratchpad( "18L" );
        update3.setAircraftType( "B757");
        
        MatmFlight diff3 = aggregator.aggregate(update3);
        assertNotNull(diff3); 
        assertNull(diff3.getArrivalRunwayScratchpad());
        assertNotNull(diff3.getAircraftType());
        assertNull(diff3.getRouteText());
    }    
}
