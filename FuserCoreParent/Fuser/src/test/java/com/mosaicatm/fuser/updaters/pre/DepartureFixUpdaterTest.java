package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class DepartureFixUpdaterTest
{

    private DepartureFixUpdater updater;

    @Before
    public void setup ()
    {
        updater = new DepartureFixUpdater();
    }

    @Test
    public void testDepartureFixInRoute()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();

        update.setDepartureFixSourceData("FIX");
        target.setRouteText("KCLT.FIX...KDFW");
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureFixSourceData());
    }
    
    @Test
    public void testDepartureFixInRouteWithNumber()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();

        update.setDepartureFixSourceData("FIX");
        target.setRouteText("KCLT.FIX2...KDFW");
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureFixSourceData());
    }
    
    @Test
    public void testDepartureFixInRouteWithExcessNumbers()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();

        update.setDepartureFixSourceData("FIX");
        target.setRouteText("KCLT.FIX42...KDFW");
        
        updater.update(update, target);
        
        assertNull(update.getDepartureFixSourceData());
    }
    
    @Test
    public void testDepartureFixInRouteSubstring()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();

        update.setDepartureFixSourceData("FIX");
        target.setRouteText("KCLT.DFIX...KDFW");        
        updater.update(update, target);
        
        assertNull(update.getDepartureFixSourceData());
        
        update.setDepartureFixSourceData("FIX");
        target.setRouteText("KCLT.FIXED...KDFW");        
        updater.update(update, target);
        
        assertNull(update.getDepartureFixSourceData());
        
        update.setDepartureFixSourceData("FIX");
        target.setRouteText("KCLT.FIX/....KDFW");        
        updater.update(update, target);
        
        assertNull(update.getDepartureFixSourceData());
    }
    
    
    
    @Test
    public void testDepartureFixNotInRoute()
    {
        MatmFlight update = new MatmFlight();
        MatmFlight target = new MatmFlight();

        update.setDepartureFixSourceData("NEWFIX");
        target.setRouteText("KCLT.FIX...KDFW");
        
        updater.update(update, target);
        
        assertNull(update.getDepartureFixSourceData());
    }

}
