package com.mosaicatm.fuser.updaters.post;

import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import junit.framework.TestCase;
import org.junit.Test;

import javax.xml.bind.JAXBElement;
import java.time.Instant;
import java.util.Date;

public class DiversionUpdaterTest extends TestCase {


    @Test
    public void testFlow()
    {
        DiversionUpdater updater = new DiversionUpdater();

        Aerodrome aero1 = create("MCO");
        Aerodrome aero2 = create("MIA");
        Aerodrome aero3 = create("FLL");

        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();

        update.setArrivalAerodrome(aero1);
        target.setArrivalAerodrome(aero1);

        // before takeoff, no changes
        updater.update(update, target);
        assertNull(target.getPredepartureArrivalAerodrome());
        assertNull(target.isActiveDiversion());
        assertNull(update.getPredepartureArrivalAerodrome());
        assertNull(update.isActiveDiversion());

        // before takeoff, arrival aerodrome changed
        target.setArrivalAerodrome(aero2);
        updater.update(update, target);
        assertEquals(aero1, update.getPredepartureArrivalAerodrome());
        assertNull(update.isActiveDiversion());

        // simulate store updated
        target.setPredepartureArrivalAerodrome(aero1);

        // takeoff, no changes
        target.setDepartureRunwayActualTime(getJaxbDate());
        updater.update(update, target);
        assertEquals(aero1, update.getPredepartureArrivalAerodrome());
        assertFalse(update.isActiveDiversion());

        // diverted after takeoff
        target.setArrivalAerodrome(aero3);
        target.setPredepartureArrivalAerodrome(aero3);
        updater.update(update, target);
        assertTrue(update.isActiveDiversion());
    }

    @Test
    public void testNullTarget()
    {
        DiversionUpdater updater = new DiversionUpdater();
        Aerodrome aero1 = create("MCO");

        MatmFlight update = new MatmFlight();
        MatmFlight target = null;

        // before departed message
        update.setArrivalAerodrome(aero1);
        updater.update(update, target);
        assertEquals(aero1, update.getPredepartureArrivalAerodrome());

        // after departed message
        update.setDepartureRunwayActualTime(getJaxbDate());
        updater.update(update, target);
        assertNull(update.isActiveDiversion());
    }

    @Test
    public void testNullAerodrome()
    {
        DiversionUpdater updater = new DiversionUpdater();
        Aerodrome aero1 = create("MCO");

        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();

        // before departed message
        updater.update(update, target);
        assertNull(update.getPredepartureArrivalAerodrome());

        // after departed message
        update.setDepartureRunwayActualTime(getJaxbDate());
        updater.update(update, target);
        assertNull(update.isActiveDiversion());
    }

    private JAXBElement<Date> getJaxbDate()
    {
        return new ObjectFactory().createMatmFlightDepartureRunwayActualTime(Date.from(Instant.now()));
    }

    private Aerodrome create(String iata)
    {
        Aerodrome aero = new Aerodrome();
        aero.setIataName(iata);
        aero.setIcaoName("K" + iata);
        aero.setFaaLid(iata);
        return aero;
    }
}