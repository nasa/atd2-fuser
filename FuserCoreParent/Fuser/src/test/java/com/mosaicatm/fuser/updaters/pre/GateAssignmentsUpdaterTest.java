package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.FmcDataSources;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class GateAssignmentsUpdaterTest
{
    private GateAssignmentsUpdater updater;
    
    @Before
    public void setup ()
    {
        updater = new GateAssignmentsUpdater();
    }
    
    @Test
    public void testUpdateArrivalGateSNS()
    {
        MatmFlight update = new MatmFlight();
        update.setArrivalStandActual("A001091");
        update.setArrivalStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getArrivalStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getArrivalStandActual());
        
        assertEquals("A1091", update.getArrivalStandActual());
    }
    
    @Test
    public void testUpdateArrivalGateWithSNS()
    {
        MatmFlight update = new MatmFlight();
        update.setArrivalStandActual("A001091BB");
        update.setArrivalStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getArrivalStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getArrivalStandActual());
        
        assertEquals("A1091BB", update.getArrivalStandActual());
    }
    
    @Test
    public void testUpdateArrivalGateWithNS()
    {
        MatmFlight update = new MatmFlight();
        update.setArrivalStandActual("001091BB");
        update.setArrivalStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getArrivalStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getArrivalStandActual());
        
        assertEquals("1091BB", update.getArrivalStandActual());
    }
    
    @Test
    public void testUpdateArrivalGateWithSNSNS()
    {
        MatmFlight update = new MatmFlight();
        update.setArrivalStandActual("A001091BB0205CCC");
        update.setArrivalStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getArrivalStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getArrivalStandActual());
        
        assertEquals("A1091BB205CCC", update.getArrivalStandActual());
    }
    
    @Test
    public void testUpdateArrivalGateWithNumber()
    {
        MatmFlight update = new MatmFlight();
        update.setArrivalStandAirline("015");
        update.setArrivalStandSource(FmcDataSources.AIRLINE);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getArrivalStandAirline());
        
        updater.update(update, target);
        
        assertNotNull(update.getArrivalStandAirline());
        
        assertEquals("15", update.getArrivalStandAirline());
    }
    
    @Test
    public void testUpdateDepartureGate()
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureStandActual("A001991");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        
        assertEquals("A1991", update.getDepartureStandActual());
    }
    
    @Test
    public void testUpdateDepartureGateNull()
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureStandActual(null);
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNull(update.getDepartureStandActual());
    }
    
    @Test
    public void testUpdateDepartureGateWithInvalidCharAtEnd()
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureStandActual("A001991--");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        
        assertEquals("A1991", update.getDepartureStandActual());
    }
    
    @Test
    public void testUpdateDepartureGateWithOnlyInvalidChar()
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureStandActual("--");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNull(update.getDepartureStandActual());
    }

    @Test
    public void testUpdateDepartureGateWithOnlyInvalidCharInTheMiddle()
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureStandActual("AAl--8*aal&*(&(&*(6  _6__alA(");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("AAl-8aal6_6_alA", update.getDepartureStandActual());
    }
    
    @Test
    public void testUpdateDepartureGateWithHyphen()
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureStandActual("R2-A");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("R2-A", update.getDepartureStandActual());
    }

    @Test
    public void testUpdateDepartureGateWithInvalidHyphen()
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureStandActual("R2-2-");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("R2-2", update.getDepartureStandActual());
    }
    
    @Test
    public void testUpdateDepartureGateWithUnderscore()
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureStandActual("DAL_A");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("DAL_A", update.getDepartureStandActual());
    }
    
    @Test
    public void testUpdateDepartureGateWithInvalidUnderscore()
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureStandActual("DAL_2_");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("DAL_2", update.getDepartureStandActual());
    }
    
    @Test
    public void testUpdateDepartureGateWithIncorrectGate()
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureStandActual("-_--_DAL_2_-_---_-");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("DAL_2", update.getDepartureStandActual());
    }
    
    @Test
    public void testValidateGate() {
        String s = "DAL_2";
        assertEquals("DAL_2", updater.validateGate(s));
        s = "DAL_";
        assertEquals("DAL", updater.validateGate(s));
        s = "DAL";
        assertEquals("DAL", updater.validateGate(s));
        s = "_DAL_";
        assertEquals("DAL", updater.validateGate(s));
        s = "DAL_2_";
        assertEquals("DAL_2", updater.validateGate(s));
        
        s = "R2-2";
        assertEquals("R2-2", updater.validateGate(s));
        s = "R2-";
        assertEquals("R2", updater.validateGate(s));
        s = "-R2-";
        assertEquals("R2", updater.validateGate(s));
        s = "R2-2-";
        assertEquals("R2-2", updater.validateGate(s));
        
        s = "-_GGG-2-12--_";
        assertEquals("GGG-2-12", updater.validateGate(s));
        s = "-_GGG--2-12--_";
        assertEquals("GGG-2-12", updater.validateGate(s));
        s = "-_GGG___2_12--_";
        assertEquals("GGG_2_12", updater.validateGate(s));

        s = "-";
        assertNull(updater.validateGate(s));
        s = "_";
        assertNull(updater.validateGate(s));
        s = "-_";
        assertNull(updater.validateGate(s));
        s = "-_---_--";
        assertNull(updater.validateGate(s));
        s = "";
        assertNull(updater.validateGate(s));
        assertNull(updater.validateGate(null));
        
        s = null;
        assertNull(updater.validateGate(s));
        
        s = "INTL";
        assertEquals("", updater.validateGate(s));
        
        s = "InTl";
        assertEquals("", updater.validateGate(s));
        
        s = "Intl";
        assertEquals("", updater.validateGate(s));
        
        s = "1";
        assertEquals("1", updater.validateGate(s));
        
        s = "2";
        assertEquals("2", updater.validateGate(s));
        
        s = "1B132";
        assertEquals("B132", updater.validateGate(s));
        
        s = "INTLG11";
        assertEquals("G11", updater.validateGate(s));
        
        s = "INTL11";
        assertEquals("11", updater.validateGate(s));
        
        s = "INDTL11";
        assertEquals("INDTL11", updater.validateGate(s));
        
        s = "2B11";
        assertEquals("B11", updater.validateGate(s));

        s = "1A13";
        assertEquals("A13", updater.validateGate(s));

        s = "1091BB";
        assertEquals("1091BB", updater.validateGate(s));

        s = "INTL90";
        assertEquals("90", updater.validateGate(s));

        s = "INTL2B10";
        assertEquals("B10", updater.validateGate(s));
    }
    
    @Test
    // Test INTL and DIGIT prefix patterns
    public void testUpdateForPrefixPatterns()
    {
        MatmFlight update = new MatmFlight();
        update.setDepartureStandActual("INTL92");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        MatmFlight target = new MatmFlight();
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("92", update.getDepartureStandActual());
        
        update.setDepartureStandActual("INTL102");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("102", update.getDepartureStandActual());
        
        update.setDepartureStandActual("INTL");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("", update.getDepartureStandActual());
        
        update.setDepartureStandActual(null);
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        assertNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        assertNull(update.getDepartureStandActual());

        
        update.setDepartureStandActual("2B11");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("B11", update.getDepartureStandActual());
        

        update.setDepartureStandActual("1A13");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("A13", update.getDepartureStandActual());
        
        update.setDepartureStandActual("INTLG11");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("G11", update.getDepartureStandActual());
        
        update.setDepartureStandActual("INTLa15");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("a15", update.getDepartureStandActual());
        
        update.setDepartureStandActual("INTL2B10");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("B10", update.getDepartureStandActual());
        
        
        update.setDepartureStandActual("INTL2B6");
        update.setDepartureStandSource(FmcDataSources.ACTUAL);
        
        assertNotNull(update.getDepartureStandActual());
        
        updater.update(update, target);
        
        assertNotNull(update.getDepartureStandActual());
        assertEquals("B6", update.getDepartureStandActual());
    }
    
}
