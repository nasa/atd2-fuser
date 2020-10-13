package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class RunwayOpNecUpdaterTest
{

    private RunwayOpNecUpdater updater;

    @Before
    public void setup ()
    {
        updater = new RunwayOpNecUpdater();
    }

    @Test
    public void testUpdateRunwayOpNec()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();

        target.setDepartureRunwayOperationalNecessity(true);

        target.setDepartureRunwayUser("23");
        update.setDepartureRunwayUser("23");

        updater.update(update, target);

        assertTrue(update.isDepartureRunwayOperationalNecessity());
    }

    @Test
    public void testResetRunwayOpNec()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();

        target.setDepartureRunwayOperationalNecessity(true);

        target.setDepartureRunwayUser("18C");
        update.setDepartureRunwayUser("23");

        updater.update(update, target);

        assertFalse(update.isDepartureRunwayOperationalNecessity());
    }

    @Test
    public void testUpdateIncludesOpNec()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();

        update.setDepartureRunwayOperationalNecessity(true);

        target.setDepartureRunwayUser("18C");
        update.setDepartureRunwayUser("23");

        updater.update(update, target);

        assertTrue(update.isDepartureRunwayOperationalNecessity());
    }

    @Test
    public void testNoChange()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();


        target.setDepartureRunwayUser("18C");
        update.setDepartureRunwayUser("23");

        updater.update(update, target);

        assertNull(update.isDepartureRunwayOperationalNecessity());
    }

}
