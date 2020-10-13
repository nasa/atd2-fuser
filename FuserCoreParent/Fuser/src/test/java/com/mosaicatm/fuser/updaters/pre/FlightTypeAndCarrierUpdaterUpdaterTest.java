package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightCarrierUtil;
import com.mosaicatm.matmdata.common.FlightType;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class FlightTypeAndCarrierUpdaterUpdaterTest
{
    private FlightTypeAndCarrierUpdater updater;
    
    @Before
    public void setup ()
    {
        MatmFlightCarrierUtil matmFlightCarrierUtil = new MatmFlightCarrierUtil();
        
        updater = new FlightTypeAndCarrierUpdater();
        updater.setMatmFlightCarrierUtil( matmFlightCarrierUtil );
    }
    
    @Test
    public void testNullAcid()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        assertNull(update.getCarrier());
        assertNull(target.getCarrier());
        assertNull(update.getFlightType());
        assertNull(target.getFlightType());
        
        updater.update(update, target);
        
        assertNull(update.getCarrier());
        assertNull(target.getCarrier());
        assertNull(update.getFlightType());
        assertNull(target.getFlightType());        
    }
    
    @Test
    public void testGaFromUpdateAcids()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        update.setAcid( "N1443" );
        
        assertNull(update.getCarrier());
        assertNull(target.getCarrier());
        assertNull(update.getFlightType());
        assertNull(target.getFlightType());
        
        updater.update(update, target);
        
        assertNull(target.getCarrier());
        assertEquals(update.getCarrier(), MatmFlightCarrierUtil.GA_CARRIER );
        assertEquals(update.getFlightType(), FlightType.GENERAL_AVIATION );
        
        update.setAcid( "N501RR" );
        update.setCarrier( null );
        updater.update(update, target);
        assertEquals(update.getCarrier(), MatmFlightCarrierUtil.GA_CARRIER );
        assertEquals(update.getFlightType(), FlightType.GENERAL_AVIATION );
        
        update.setAcid( "G-ACSS" );
        update.setCarrier( null );
        updater.update(update, target);
        assertEquals(update.getCarrier(), MatmFlightCarrierUtil.GA_CARRIER );     
        assertEquals(update.getFlightType(), FlightType.GENERAL_AVIATION );
        
        update.setAcid( "N10A" );
        update.setCarrier( null );
        updater.update(update, target);
        assertEquals(update.getCarrier(), MatmFlightCarrierUtil.GA_CARRIER );  
        assertEquals(update.getFlightType(), FlightType.GENERAL_AVIATION );        
        
        update.setAcid( "N1" );
        update.setCarrier( null );
        updater.update(update, target);
        assertEquals(update.getCarrier(), MatmFlightCarrierUtil.GA_CARRIER );
        assertEquals(update.getFlightType(), FlightType.GENERAL_AVIATION );
        
        update.setAcid( "LN1" );
        update.setCarrier( null );
        updater.update(update, target);
        assertEquals(update.getCarrier(), MatmFlightCarrierUtil.GA_CARRIER );    
        assertEquals(update.getFlightType(), FlightType.GENERAL_AVIATION );
    }    
    
    @Test
    public void testGaFromTargetAcids()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        target.setAcid( "N1443" );
        update.setAcid( "N1443" );
        
        assertNull(update.getCarrier());
        assertNull(target.getCarrier());
        assertNull(update.getFlightType());
        assertNull(target.getFlightType());
        
        updater.update(update, target);
        
        assertNull(target.getCarrier());
        assertNull(target.getFlightType());
        assertEquals(update.getCarrier(), MatmFlightCarrierUtil.GA_CARRIER );
        assertEquals(update.getFlightType(), FlightType.GENERAL_AVIATION );
        
        target.setAcid( "N1443Z" );
        target.setCarrier( null );
        update.setCarrier( null );
        updater.update(update, target);
        assertEquals(update.getCarrier(), MatmFlightCarrierUtil.GA_CARRIER );
        assertEquals(update.getFlightType(), FlightType.GENERAL_AVIATION );
        
        target.setAcid( "G-ACSS" );
        target.setCarrier( null );
        update.setCarrier( null );
        updater.update(update, target);
        assertEquals(update.getCarrier(), MatmFlightCarrierUtil.GA_CARRIER );       
        assertEquals(update.getFlightType(), FlightType.GENERAL_AVIATION );
        
        target.setAcid( "N10A" );
        target.setCarrier( null );
        update.setCarrier( null );
        updater.update(update, target);
        assertEquals(update.getCarrier(), MatmFlightCarrierUtil.GA_CARRIER );   
        assertEquals(update.getFlightType(), FlightType.GENERAL_AVIATION );        
    }        
    
    @Test
    public void testCarrierFromUpdateAcids()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        update.setAcid( "AAL222" );
        
        assertNull(update.getCarrier());
        assertNull(target.getCarrier());
        assertNull(update.getFlightType());
        assertNull(target.getFlightType());
        
        updater.update(update, target);
        
        assertNull(target.getCarrier());
        assertEquals(update.getCarrier(), "AAL" );
        assertNull(update.getFlightType());  
        
        update.setAcid( "AAL0222" );
        update.setCarrier( null );
        updater.update(update, target);
        assertEquals(update.getCarrier(), "AAL" );
        assertNull(update.getFlightType());          
        
        update.setAcid( "AAL022A" );
        update.setCarrier( null );
        updater.update(update, target);
        assertEquals(update.getCarrier(), "AAL" );        
        assertNull(update.getFlightType());          
        
        update.setAcid( "JIA5235" );
        update.setCarrier( null );
        updater.update(update, target);
        assertEquals(update.getCarrier(), "JIA" );    
        assertNull(update.getFlightType());          
        
        update.setAcid( "AWI33A" );
        update.setCarrier( null );
        updater.update(update, target);
        assertEquals(update.getCarrier(), "AWI" );        
        assertNull(update.getFlightType());          
        
        update.setAcid( "WOW3KN" );
        update.setCarrier( null );
        updater.update(update, target);
        assertEquals(update.getCarrier(), "WOW" );          
        assertNull(update.getFlightType());  

        update.setScheduledFlight( true );
        updater.update(update, target);
        assertEquals(update.getFlightType(), FlightType.SCHEDULED_AIR_TRANSPORT );
    }       
    
    @Test
    public void testRemainsFromUpdate()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        update.setAcid( "AAL222" );
        update.setCarrier( "CAR" );
        update.setFlightType( FlightType.MILITARY );
        
        assertEquals(update.getCarrier(), "CAR");
        assertNull(target.getCarrier());
        assertEquals(update.getFlightType(), FlightType.MILITARY);
        assertNull(target.getFlightType());
        
        updater.update(update, target);
        
        assertNull(target.getCarrier());
        assertEquals(update.getCarrier(), "CAR" );
        assertNull(target.getFlightType());
        assertEquals(update.getFlightType(), FlightType.MILITARY );        
    }         
    
    @Test
    public void testCarrierRemainsFromTarget()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        update.setAcid( "AAL222" );
        target.setAcid( "AAL222" );
        target.setCarrier( "CAR" );
        target.setFlightType( FlightType.OTHER );
        
        assertEquals(target.getCarrier(), "CAR" );
        assertNull(update.getCarrier());
        assertEquals(target.getFlightType(), FlightType.OTHER );
        assertNull(update.getFlightType());
        
        updater.update(update, target);
        
        assertNull(update.getCarrier());
        assertEquals(target.getCarrier(), "CAR" );
        assertNull(update.getFlightType());
        assertEquals(target.getFlightType(), FlightType.OTHER );        
    }       
    
    @Test
    public void testCarrierChange()
    {
        MatmFlight target = new MatmFlight();
        MatmFlight update = new MatmFlight();
        
        target.setAcid( "N1500" );
        target.setCarrier( "GA" );
        target.setFlightType( FlightType.GENERAL_AVIATION );
        update.setAcid( "AAL222" );

        updater.update(update, target);
        
        assertEquals(update.getCarrier(), "AAL" );   
        assertNull(update.getFlightType());   
        
        target.setAcid( "N1500" );
        target.setCarrier( "GA" );
        
        update.setAcid( "EJA222" );
        update.setCarrier( null );
        update.setFlightType( null );

        updater.update(update, target);

        assertEquals(update.getCarrier(), "EJA" ); 
        assertEquals(update.getFlightType(), FlightType.GENERAL_AVIATION ); 

        update.setScheduledFlight( true );
        update.setCarrier( null );
        update.setFlightType( null );
        
        updater.update(update, target);

        assertEquals( update.getCarrier(), "EJA" );           
        assertEquals(update.getFlightType(), FlightType.SCHEDULED_AIR_TRANSPORT ); 
    }        
}
