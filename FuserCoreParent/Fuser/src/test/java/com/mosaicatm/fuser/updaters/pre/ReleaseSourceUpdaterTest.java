package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.common.MeteredTime;
import com.mosaicatm.matmdata.common.ReleaseMode;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import com.mosaicatm.matmdata.flight.extension.IdacExtension;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.TbfmExtension;

public class ReleaseSourceUpdaterTest 
{
    private final String amsSource = "AMS";
    private final String idacSource = "IDAC";
    private final String tmaSource = "TMA";
    
    private final String amsSystemId = "AMS-SWIM";
    private final String tmaZTLSystemId = "TMA.ZTL.FAA.GOV-SWIM";
    
    private final ObjectFactory objectFactory = new ObjectFactory();
    private final com.mosaicatm.matmdata.flight.extension.ObjectFactory tbfmObjectFactory = 
        new com.mosaicatm.matmdata.flight.extension.ObjectFactory();
    
    private ReleaseSourceUpdater updater;
    
    @Before
    public void setup ()
    {
        updater = new ReleaseSourceUpdater();
        updater.setEnableAutoReleaseModeFilter(false);
    }    
    
    @Test
    public void testAmsUpdate ()
    {
        Date releaseTime = TimeFactory.getDate("2017-09-06 12:00:00");
        Date targetTime = TimeFactory.getDate("2017-09-06 13:00:00");
        
        MatmFlight update = createAmsFlight(releaseTime, null, amsSystemId);
        MatmFlight target = createSwimFlight(targetTime, tmaZTLSystemId);
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureRunwayMeteredTime());
        assertNotNull(update.getDepartureRunwayMeteredTime().getValue());
        assertNotNull(update.getReleaseUpdateSource());
        assertEquals(releaseTime, update.getDepartureRunwayMeteredTime().getValue().getValue());
        assertEquals(amsSystemId, update.getReleaseUpdateSource());
    }
    
    @Test
    public void testScheduledSwimUpdate()
    {
        Date releaseTime = TimeFactory.getDate("2017-08-24 12:00:00");
        
        MatmFlight swimUpdate = createSwimFlight(releaseTime, tmaZTLSystemId);
        
        MatmFlight target = new MatmFlight();
        updater.update(swimUpdate, target);
        
        assertNotNull(swimUpdate.getDepartureRunwayMeteredTime().getValue());
        assertNotNull(swimUpdate.getDepartureRunwayMeteredTime().getValue().getValue());
        assertEquals(releaseTime, swimUpdate.getDepartureRunwayMeteredTime().getValue().getValue());
        assertEquals (tmaZTLSystemId, swimUpdate.getReleaseUpdateSource());
    }
    
    @Test
    public void testScheduledIdacUpdate()
    {
        Date releaseTime = TimeFactory.getDate("2017-08-24 13:00:00");
        
        MatmFlight idacUpdate = createIdacFlight(releaseTime, "SCHED", false, ReleaseMode.SEMI);
        
        MatmFlight target = new MatmFlight();
        updater.update(idacUpdate, target);
        
        assertNotNull(idacUpdate.getDepartureRunwayMeteredTime().getValue());
        assertNotNull(idacUpdate.getDepartureRunwayMeteredTime().getValue().getValue());
        assertEquals(releaseTime, idacUpdate.getDepartureRunwayMeteredTime().getValue().getValue());
        assertEquals (idacSource, idacUpdate.getReleaseUpdateSource());
        assertTrue(idacUpdate.getExtensions().getIdacExtension().isNegotiatingRelease());
    }
    
    @Test
    public void testManualIdacUpdateWithModeFilter()
    {
        updater.setEnableAutoReleaseModeFilter(true);
        
        Date releaseTime = TimeFactory.getDate("2017-08-24 13:00:00");
        
        MatmFlight idacUpdate = createIdacFlight(releaseTime, "SCHED", false, ReleaseMode.MANUAL);
        
        updater.update(idacUpdate, null);

        assertNull (idacUpdate.getReleaseUpdateSource());
        
        updater.setEnableAutoReleaseModeFilter(false);        
        
        MatmFlight target = new MatmFlight();
        updater.update(idacUpdate, target);
        
        assertNotNull(idacUpdate.getDepartureRunwayMeteredTime().getValue());
        assertNotNull(idacUpdate.getDepartureRunwayMeteredTime().getValue().getValue());
        assertEquals(releaseTime, idacUpdate.getDepartureRunwayMeteredTime().getValue().getValue());
        assertEquals (idacSource, idacUpdate.getReleaseUpdateSource());
        assertTrue(idacUpdate.getExtensions().getIdacExtension().isNegotiatingRelease());
    }
    
    @Test
    public void testUnscheduledIdacTargetUnscheduledSwimUpdate()
    {        
        MatmFlight idacTarget = createIdacFlight(null, "UNSCHED", false, ReleaseMode.SEMI);
        MatmFlight swimUpdate = createSwimFlight(null, tmaZTLSystemId);
        
        updater.update(swimUpdate, idacTarget);
        
        assertNull(swimUpdate.getDepartureRunwayMeteredTime());
        assertNull (swimUpdate.getReleaseUpdateSource());
    }
    
    @Test
    public void testUnscheduledIdacUpdateUnscheduledSwimTarget()
    {
        MatmFlight idacUpdate = createIdacFlight(null, "UNSCHED", false, ReleaseMode.SEMI);
        MatmFlight swimTarget = createSwimFlight(null, tmaZTLSystemId);
        
        updater.update(idacUpdate, swimTarget);
        
        assertNull(idacUpdate.getDepartureRunwayMeteredTime());
        assertEquals(idacSource, idacUpdate.getReleaseUpdateSource());
        assertFalse(idacUpdate.getExtensions().getIdacExtension().isNegotiatingRelease());
    }
    
    @Test
    public void testUnscheduledIdacTargetScheduledSwimUpdate()
    {
        Date swimReleaseTime = TimeFactory.getDate("2017-08-24 14:30:00");
        
        MatmFlight idacTarget = createIdacFlight(null, "UNSCHED", false, ReleaseMode.SEMI);
        MatmFlight swimUpdate = createSwimFlight(swimReleaseTime, tmaZTLSystemId);
        
        updater.update(swimUpdate, idacTarget);
        
        assertNotNull(swimUpdate.getDepartureRunwayMeteredTime());
        assertNotNull(swimUpdate.getDepartureRunwayMeteredTime().getValue());
        assertEquals(swimReleaseTime, swimUpdate.getDepartureRunwayMeteredTime().getValue().getValue());
        assertEquals(tmaZTLSystemId, swimUpdate.getReleaseUpdateSource());
    }
    
    @Test
    public void testCanceledStdSwimUpdate()
    {
        MatmFlight swimTarget = createSwimFlight(new Date(), tmaZTLSystemId);
        MatmFlight swimUpdate = createCancelStdSwimFlight(tmaZTLSystemId);
        
        updater.update(swimUpdate, swimTarget);
        
        assertNotNull(swimUpdate.getDepartureRunwayMeteredTime());
        assertTrue(swimUpdate.getDepartureRunwayMeteredTime().isNil());
        assertNull(swimUpdate.getDepartureRunwayMeteredTime().getValue());
        assertEquals(tmaZTLSystemId, swimUpdate.getReleaseUpdateSource());
    }    
    
    @Test
    public void testScheduledIdacTargetUnscheduledSwimUpdate()
    {
        Date idacReleaseTime = TimeFactory.getDate("2017-08-24 15:00:00");
        Date swimReleaseTime = TimeFactory.getDate("2017-08-24 15:30:00");
        
        MatmFlight idacTarget = createIdacFlight(idacReleaseTime, "SCHED", true, ReleaseMode.SEMI);
        MatmFlight swimUpdate = createSwimFlight(swimReleaseTime, tmaZTLSystemId);
        
        updater.update(swimUpdate, idacTarget);
        
        assertNull(swimUpdate.getDepartureRunwayMeteredTime());
        assertNull(swimUpdate.getReleaseUpdateSource());
    }
    
    @Test
    public void testScheduledSwimTargetScheduledIdacUpdate()
    {
        Date swimReleaseTime = TimeFactory.getDate("2017-08-24 16:00:00");
        Date idacReleaseTime = TimeFactory.getDate("2017-08-04 16:30:00");
        
        MatmFlight swimTarget = createSwimFlight(swimReleaseTime, tmaZTLSystemId);
        MatmFlight idacUpdate = createIdacFlight(idacReleaseTime, "SCHED", false, ReleaseMode.SEMI);
        
        updater.update(idacUpdate, swimTarget);
        
        assertNotNull(idacUpdate.getDepartureRunwayMeteredTime());
        assertNotNull(idacUpdate.getDepartureRunwayMeteredTime().getValue());
        assertEquals(idacReleaseTime, idacUpdate.getDepartureRunwayMeteredTime().getValue().getValue());
        assertEquals(idacSource, idacUpdate.getReleaseUpdateSource());
        assertTrue(idacUpdate.getExtensions().getIdacExtension().isNegotiatingRelease());
    }
    
    @Test
    public void testScheduledSwimUpdateScheduledIdacTarget()
    {
        Date swimReleaseTime = TimeFactory.getDate("2017-08-24 16:00:00");
        Date idacReleaseTime = TimeFactory.getDate("2017-08-04 16:30:00");
        
        MatmFlight swimUpdate = createSwimFlight(swimReleaseTime, tmaZTLSystemId);
        MatmFlight idacTarget = createIdacFlight(idacReleaseTime, "SCHED", true, ReleaseMode.SEMI);
        
        updater.update(swimUpdate, idacTarget);
        
        assertNull(swimUpdate.getDepartureRunwayMeteredTime());
        assertNull(swimUpdate.getReleaseUpdateSource());
    }
    
    private MatmFlight createCancelStdSwimFlight (String systemId)
    {
        MatmFlight flight = new MatmFlight();
        flight.setLastUpdateSource(tmaSource);
        flight.setSystemId(systemId);
        
        flight.setDepartureRunwayMeteredTime(objectFactory.createMatmFlightDepartureRunwayMeteredTime(null));
        
        flight.setExtensions( new MatmFlightExtensions() );
        flight.getExtensions().setTbfmExtension( new TbfmExtension() );
        flight.getExtensions().getTbfmExtension().setStd( tbfmObjectFactory.createTbfmExtensionStd( null ));
        
        return flight;
    }
    
    private MatmFlight createSwimFlight (Date releaseTime, String systemId)
    {
        MatmFlight flight = new MatmFlight();
        flight.setLastUpdateSource(tmaSource);
        flight.setSystemId(systemId);
        
        MeteredTime meteredTime = null;
        
        if (releaseTime != null)
        {
            meteredTime = new MeteredTime();
            meteredTime.setValue(releaseTime);
            flight.setDepartureRunwayMeteredTime(objectFactory.createMatmFlightDepartureRunwayMeteredTime(meteredTime));
        }
        
        return flight;
    }    
    
    private MatmFlight createIdacFlight (Date releaseTime, String statusCode, boolean negotiating, ReleaseMode mode)
    {
        MatmFlight flight = new MatmFlight();
        flight.setLastUpdateSource(idacSource);
        flight.setReleaseMode(mode);
        
        MeteredTime meteredTime = null;
        
        if (releaseTime != null)
        {
            meteredTime = new MeteredTime();
            meteredTime.setValue(releaseTime);
            flight.setDepartureRunwayMeteredTime(objectFactory.createMatmFlightDepartureRunwayMeteredTime(meteredTime));
        }
        
        IdacExtension idac = new IdacExtension();
        idac.setScheduledStatusCode(statusCode);
        idac.setNegotiatingRelease(negotiating);
        
        MatmFlightExtensions ext = new MatmFlightExtensions();
        ext.setIdacExtension(idac);
        flight.setExtensions(ext);
        
        return flight;
    }
    
    private MatmFlight createAmsFlight (Date releaseTime, Date cancelTime, String systemId)
    {
        MatmFlight flight = new MatmFlight();
        flight.setLastUpdateSource(amsSource);
        flight.setSystemId(systemId);
        
        MeteredTime meteredTime = null;
        if (releaseTime != null)
        {
            meteredTime = new MeteredTime();
            meteredTime.setValue(releaseTime);
            flight.setDepartureRunwayMeteredTime(objectFactory.createMatmFlightDepartureRunwayMeteredTime(meteredTime));
        }
        
        MatmFlightExtensions ext = new MatmFlightExtensions();
        TbfmExtension tbfm = new TbfmExtension();
        ext.setTbfmExtension(tbfm);
        
        if (cancelTime != null)
        {
            tbfm.setCanceledSwimReleaseTime(cancelTime);
        }
        
        return flight;
    }
}
