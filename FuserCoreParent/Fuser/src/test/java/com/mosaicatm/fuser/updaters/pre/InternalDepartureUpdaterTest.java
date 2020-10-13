package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.SurfaceFlightState;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class InternalDepartureUpdaterTest
{
    private InternalDepartureUpdater updater;
    
    @Before
    public void setup(){
        updater = new InternalDepartureUpdater();
        updater.setActive(true);
        updater.setAirports("DFW,DAL");
    }
    
    @Test
    public void testSetInternalDepartureFlag()
    {
        MatmFlight flight = new MatmFlight();
        Aerodrome arrAerodrome = new Aerodrome();
        arrAerodrome.setIataName("DFW");
        Aerodrome depAerodrome = new Aerodrome();
        depAerodrome.setIataName("DAL");
        flight.setArrivalAerodrome(arrAerodrome);
        flight.setDepartureAerodrome(depAerodrome);
        
        updater.update(flight, null);
        assertTrue(flight.isInternalDeparture());
    }

    @Test
    public void testSetInternalDepartureFlag_SingleAirport()
    {
        MatmFlight flight = new MatmFlight();
        Aerodrome arrAerodrome = new Aerodrome();
        arrAerodrome.setIataName("DFW");
        Aerodrome depAerodrome = new Aerodrome();
        depAerodrome.setIataName("DFW");
        flight.setArrivalAerodrome(arrAerodrome);
        flight.setDepartureAerodrome(depAerodrome);

        updater.update(flight, null);
        assertFalse(flight.isInternalDeparture());
    }

    @Test
    public void testSetInternalDepartureFlag_NotInternalDeparture()
    {
        MatmFlight update = new MatmFlight();
        Aerodrome arrAerodrome = new Aerodrome();
        arrAerodrome.setIataName("DFW");
        Aerodrome depAerodrome = new Aerodrome();
        depAerodrome.setIataName("CLT");
        update.setArrivalAerodrome(arrAerodrome);
        update.setDepartureAerodrome(depAerodrome);

        updater.update(update, null);
        assertFalse(update.isInternalDeparture());
    }

    @Test
    public void testSetInternalDepartureFlag_IncompleteUpdate()
    {
        MatmFlight flight = new MatmFlight();
        Aerodrome arrAerodrome = new Aerodrome();
        arrAerodrome.setIataName("DFW");
        flight.setArrivalAerodrome(arrAerodrome);

        MatmFlight target = new MatmFlight();
        Aerodrome arrAerodrome2 = new Aerodrome();
        arrAerodrome2.setIataName("CLT");
        Aerodrome depAerodrome = new Aerodrome();
        depAerodrome.setIataName("DAL");
        target.setArrivalAerodrome(arrAerodrome2);
        target.setDepartureAerodrome(depAerodrome);

        updater.update(flight, target);
        assertTrue(flight.isInternalDeparture());
    }

    @Test
    public void testSetInternalDepartureFlag_ReturnToGate()
    {
        MatmFlight flight = new MatmFlight();
        Aerodrome arrAerodrome = new Aerodrome();
        arrAerodrome.setIataName("DFW");
        Aerodrome depAerodrome = new Aerodrome();
        depAerodrome.setIataName("DAL");
        flight.setArrivalAerodrome(arrAerodrome);
        flight.setDepartureAerodrome(depAerodrome);
        flight.setDepartureSurfaceFlightState(
            SurfaceFlightState.RETURN_TO_GATE);

        updater.update(flight, null);
        assertFalse(flight.isInternalDeparture());
    }


}
