package com.mosaicatm.fuser.updaters.pre;

import com.mosaicatm.fuser.transform.matm.MatmTransformConstants;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.util.FuserAirportCodeTranslator;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.SfdpsExtension;
import com.mosaicatm.matmdata.flight.extension.SfdpsMessageTypeType;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;
import com.mosaicatm.matmdata.flight.extension.TfmMessageTypeType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AerodromeUpdaterTest
{
    private AerodromeUpdater updater;
    
    @Before
    public void setup ()
    {
        FuserAirportCodeTranslator translator = new FuserAirportCodeTranslator();
        translator.init();
        
        updater = new AerodromeUpdater ();
        updater.setAirportCodeTranslator(translator);
    }
    
    @Test
    public void testIataToIataArrivalConversion ()
    {
        MatmFlight flight = new MatmFlight();
        MatmFlight update = new MatmFlight();
        update.setArrivalAerodrome(createAerodrome("ATL", null));
        
        updater.update(update, flight);
        
        assertEquals ("ATL", update.getArrivalAerodrome().getIataName());
        assertEquals ("KATL", update.getArrivalAerodrome().getIcaoName());
    }
    
    @Test
    public void testIcaoToIataArrivalConversion ()
    {
        MatmFlight flight = new MatmFlight();
        MatmFlight update = new MatmFlight();
        update.setArrivalAerodrome(createAerodrome(null, "EGQL"));
        
        updater.update(update, flight);
        
        assertEquals ("ADX", update.getArrivalAerodrome().getIataName());
        assertEquals ("EGQL", update.getArrivalAerodrome().getIcaoName());
    }
    
    @Test
    public void testIcaoToIataArrivalEmptyConversion ()
    {
        MatmFlight flight = new MatmFlight();
        MatmFlight update = new MatmFlight();
        update.setArrivalAerodrome(createAerodrome("", "EGQL"));
        
        updater.update(update, flight);
        
        assertEquals ("ADX", update.getArrivalAerodrome().getIataName());
        assertEquals ("EGQL", update.getArrivalAerodrome().getIcaoName());
    }
    
    @Test
    public void testIcaoToIcaoArrivalConversion ()
    {
        MatmFlight flight = new MatmFlight();
        MatmFlight update = new MatmFlight();
        update.setArrivalAerodrome(createAerodrome(null, "EGQL"));
        
        updater.update(update, flight);
        
        assertEquals ("ADX", update.getArrivalAerodrome().getIataName());
        assertEquals ("EGQL", update.getArrivalAerodrome().getIcaoName());
    }
    
    @Test
    public void testIataToIataDepartureConversion ()
    {
        MatmFlight flight = new MatmFlight();
        MatmFlight update = new MatmFlight();
        update.setDepartureAerodrome(createAerodrome("ACT", null));
        
        updater.update(update, flight);
        
        assertEquals ("ACT", update.getDepartureAerodrome().getIataName());
        assertEquals ("KACT", update.getDepartureAerodrome().getIcaoName());
    }
    
    @Test
    public void testIcaoToIataDepartureConversion ()
    {
        MatmFlight flight = new MatmFlight();
        MatmFlight update = new MatmFlight();
        update.setDepartureAerodrome(createAerodrome(null, "ENAL"));
        
        updater.update(update, flight);
        
        assertEquals ("AES", update.getDepartureAerodrome().getIataName());
        assertEquals ("ENAL", update.getDepartureAerodrome().getIcaoName());
    }

    @Test
    public void testIcaoToIataDepartureEmptyConversion ()
    {
        MatmFlight flight = new MatmFlight();
        MatmFlight update = new MatmFlight();
        update.setDepartureAerodrome(createAerodrome("", "ENAL"));
        
        updater.update(update, flight);
        
        assertEquals ("AES", update.getDepartureAerodrome().getIataName());
        assertEquals ("ENAL", update.getDepartureAerodrome().getIcaoName());
    }
    
    @Test
    public void testIcaoToIcaoDepartureConversion ()
    {
        MatmFlight flight = new MatmFlight();
        MatmFlight update = new MatmFlight();
        update.setDepartureAerodrome(createAerodrome(null, "ENAL"));
        
        updater.update(update, flight);
        
        assertEquals ("AES", update.getDepartureAerodrome().getIataName());
        assertEquals ("ENAL", update.getDepartureAerodrome().getIcaoName());
    }
    
    @Test
    public void testUnknownIataAirportConversion ()
    {
        MatmFlight flight = new MatmFlight();
        MatmFlight update = new MatmFlight();

        flight.setDepartureAerodrome(createAerodrome("IAD", "KIAD"));
        update.setDepartureAerodrome(createAerodrome("Y99", null));
        
        updater.update(update, flight);
        
        //If we can't figure out the airport, then leave it alone for IATA
        assertEquals ("Y99", update.getDepartureAerodrome().getIataName());
        //But the ICAO should be XXX
        assertEquals (MatmTransformConstants.FUSER_UNKNOWN_AIRPORT_OVERWRITE_CODE, 
                update.getDepartureAerodrome().getIcaoName());
    }    
    
    @Test
    public void testUnknownIcaoAirportConversion ()
    {
        MatmFlight flight = new MatmFlight();
        MatmFlight update = new MatmFlight();

        flight.setDepartureAerodrome(createAerodrome("IAD", "KIAD"));
        update.setDepartureAerodrome(createAerodrome(null, "NANA"));
        
        updater.update(update, flight);
        
        //If we can't figure out the airport, then leave it alone for IATA
        assertEquals ("NANA", update.getDepartureAerodrome().getIcaoName());
        //But the ICAO should be XXX
        assertEquals (MatmTransformConstants.FUSER_UNKNOWN_AIRPORT_OVERWRITE_CODE, 
                update.getDepartureAerodrome().getIataName());
    }     
    
    @Test
    public void testFilterOutTfmDepartureAirportChange ()
    {
        MatmFlight flight = new MatmFlight();
        flight.setDepartureAerodrome(createAerodrome(null, "KCLT"));
        
        MatmFlight update = new MatmFlight();
        update.setDepartureAerodrome(createAerodrome(null, "KIAD"));
        
        // Non-TFM message type should change the airport
        updater.update(update, flight);
        assertEquals ("KIAD", update.getDepartureAerodrome().getIcaoName());
        assertNotEquals (flight.getDepartureAerodrome(), update.getDepartureAerodrome());
        
        update.setLastUpdateSource( "TFM" );
        update.setExtensions( new MatmFlightExtensions() );
        update.getExtensions().setTfmExtension( new TfmExtension() );
        update.getExtensions().getTfmExtension().setMessageType( TfmMessageTypeType.FLIGHT_PLAN_INFORMATION );
        
        // TFM flight plan message type should change the airport
        update.setDepartureAerodrome(createAerodrome(null, "KIAD"));
        updater.update(update, flight);
        assertEquals ("KIAD", update.getDepartureAerodrome().getIcaoName());
        assertNotEquals (flight.getDepartureAerodrome(), update.getDepartureAerodrome());     
        
        // TFM TRACK message type should NOT change the airport
        update.getExtensions().getTfmExtension().setMessageType( TfmMessageTypeType.TRACK_INFORMATION );
        update.setDepartureAerodrome(createAerodrome(null, "KIAD"));
        updater.update(update, flight);
        assertEquals ("KCLT", update.getDepartureAerodrome().getIcaoName());
        assertEquals (flight.getDepartureAerodrome(), update.getDepartureAerodrome());       
        
        // SFDPS TRACK message type should NOT change the airport
        update.setLastUpdateSource( "SFDPS" );
        update.getExtensions().setTfmExtension(null);
        update.getExtensions().setSfdpsExtension( new SfdpsExtension() );        
        update.getExtensions().getSfdpsExtension().setMessageType( SfdpsMessageTypeType.TH );
        update.setDepartureAerodrome(createAerodrome(null, "KIAD"));
        updater.update(update, flight);
        assertEquals ("KCLT", update.getDepartureAerodrome().getIcaoName());
        assertEquals (flight.getDepartureAerodrome(), update.getDepartureAerodrome());             
    }     
    
    @Test
    public void testFilterOutTfmArrivalAirportChange ()
    {
        MatmFlight flight = new MatmFlight();
        flight.setArrivalAerodrome(createAerodrome(null, "KCLT"));
        
        MatmFlight update = new MatmFlight();
        update.setArrivalAerodrome(createAerodrome(null, "KIAD"));
        
        // Non-TFM message type should change the airport
        updater.update(update, flight);
        assertEquals ("KIAD", update.getArrivalAerodrome().getIcaoName());
        assertNotEquals (flight.getArrivalAerodrome(), update.getArrivalAerodrome());
        
        update.setLastUpdateSource( "TFM" );
        update.setExtensions( new MatmFlightExtensions() );
        update.getExtensions().setTfmExtension( new TfmExtension() );
        update.getExtensions().getTfmExtension().setMessageType( TfmMessageTypeType.FLIGHT_PLAN_INFORMATION );
        
        // TFM flight plan message type should change the airport
        update.setArrivalAerodrome(createAerodrome(null, "KIAD"));
        updater.update(update, flight);
        assertEquals ("KIAD", update.getArrivalAerodrome().getIcaoName());
        assertNotEquals (flight.getArrivalAerodrome(), update.getArrivalAerodrome());     
        
        // TFM TRACK message type should NOT change the airport
        update.getExtensions().getTfmExtension().setMessageType( TfmMessageTypeType.TRACK_INFORMATION );
        update.setArrivalAerodrome(createAerodrome(null, "KIAD"));
        updater.update(update, flight);
        assertEquals ("KCLT", update.getArrivalAerodrome().getIcaoName());
        assertEquals (flight.getArrivalAerodrome(), update.getArrivalAerodrome());         
    }     
    
    @Test
    public void testFilterOutBadAirportChange ()
    {
        MatmFlight flight = new MatmFlight();
        flight.setArrivalAerodrome(createAerodrome("IAD", "KIAD"));
        
        MatmFlight update = new MatmFlight();
        update.setArrivalAerodrome(createAerodrome(null, "BADAIRPORT"));
        
        updater.update(update, flight); 
        
        assertEquals ("IAD", update.getArrivalAerodrome().getIataName());
        assertEquals ("KIAD", update.getArrivalAerodrome().getIcaoName());
        
        update.setArrivalAerodrome(createAerodrome("BADDY", null));
        
        updater.update(update, flight); 
        
        assertEquals ("IAD", update.getArrivalAerodrome().getIataName());
        assertEquals ("KIAD", update.getArrivalAerodrome().getIcaoName());        
    }
    
    private Aerodrome createAerodrome (String iata, String icao)
    {
        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setIataName(iata);
        aerodrome.setIcaoName(icao);
        
        return aerodrome;
    }
}
