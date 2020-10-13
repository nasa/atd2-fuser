package com.mosaicatm.fuser.updaters.post;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import com.mosaicatm.fuser.util.FuserAirportCodeTranslator;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class InternationalUpdaterTest
{
    private InternationalUpdater updater;
    
    @Before
    public void setup ()
    {
        FuserAirportCodeTranslator translator = new FuserAirportCodeTranslator();
        translator.init();
        
        updater = new InternationalUpdater();
        updater.setAirportCodeTranslator(translator);
    }
    
    @Test
    public void testNoAerodromes()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        updater.update( update, target );
        
        assertEquals( null, update.isInternational() );

        update.setInternational( false );

        assertEquals( false, update.isInternational() );
    }
    
    @Test
    public void testDomestic()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        update.setDepartureAerodrome( createAerodrome( "CLT", null ));
        update.setArrivalAerodrome( null );       
        updater.update( update, target );
        
        assertEquals( null, update.isInternational() );
        
        update.setDepartureAerodrome( null );
        update.setArrivalAerodrome( createAerodrome( "CLT", null ));        
        updater.update( update, target );
        
        assertEquals( null, update.isInternational() );
        
        update.setDepartureAerodrome( createAerodrome( "123", null ));
        update.setArrivalAerodrome( createAerodrome( "CLT", null ));        
        updater.update( update, target );
        
        assertEquals( null, update.isInternational() );        

        // This one is tricky -- it's not a real airport
        update.setDepartureAerodrome( createAerodrome( null, "K123" ));
        update.setArrivalAerodrome( createAerodrome( "CLT", null ));        
        updater.update( update, target );
        
        assertEquals( null, update.isInternational() );    
        
        update.setDepartureAerodrome( createAerodrome( "CLT", null ));
        update.setArrivalAerodrome( createAerodrome( "CLT", null ));        
        updater.update( update, target );
        
        assertEquals( false, update.isInternational() );            
        
        update.setDepartureAerodrome( createAerodrome( null, "KCLT" ));
        update.setArrivalAerodrome( createAerodrome( "CLT", null ));        
        updater.update( update, target );
        
        assertEquals( false, update.isInternational() );      
    }    
    
    @Test
    public void testInternational()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();
        
        update.setDepartureAerodrome( createAerodrome( "MEX", null ));
        update.setArrivalAerodrome( null );       
        updater.update( update, target );
        
        assertEquals( true, update.isInternational() );
        
        update.setDepartureAerodrome( null );
        update.setArrivalAerodrome( createAerodrome( "MEX", null ));        
        updater.update( update, target );
        
        assertEquals( true, update.isInternational() );  
    }        
    
    private Aerodrome createAerodrome (String iata, String icao)
    {
        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setIataName(iata);
        aerodrome.setIcaoName(icao);
        
        return aerodrome;
    }
}
