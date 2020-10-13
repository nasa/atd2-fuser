package com.mosaicatm.fuser.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mosaicatm.matmdata.common.Aerodrome;

public class FuserAirportCodeTranslatorTest
{
    @Test
    public void testIataConversion ()
    {
        FuserAirportCodeTranslator translator = new FuserAirportCodeTranslator ();
        translator.init();
        
        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setIataName( "ATL" );
        assertEquals ("ATL", translator.getBestMatchAirport( aerodrome ).getIata() );
        
        aerodrome = new Aerodrome();
        aerodrome.setIcaoName( "KATL" );        
        assertEquals ("ATL", translator.getBestMatchAirport( aerodrome ).getIata() );
        
        aerodrome = new Aerodrome();
        aerodrome.setIcaoName( "BIAR" );         
        assertEquals ("AEY", translator.getBestMatchAirport( aerodrome ).getIata() );
    }
}
