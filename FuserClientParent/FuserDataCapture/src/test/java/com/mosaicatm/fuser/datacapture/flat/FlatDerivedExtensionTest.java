package com.mosaicatm.fuser.datacapture.flat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.junit.Test;

import com.mosaicatm.fuser.datacapture.flat.FlatDerivedExtension;
import com.mosaicatm.matmdata.flight.extension.DerivedExtension;

public class FlatDerivedExtensionTest
{
    @Test
    public void testDepartureRunwayCrossedDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setDepartureRunwayCrossedDuration(getDuration(1000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(1000), flat.getDepartureRunwayCrossedDurationValue());
    }
    
    @Test
    public void testDepartureRampAreaDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setDepartureRampAreaDuration(getDuration(2000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(2000), flat.getDepartureRampAreaDurationValue());
    }
    
    @Test
    public void testDepartureRampAreaStopDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setDepartureRampAreaStopDuration(getDuration(3000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(3000), flat.getDepartureRampAreaStopDurationValue());
    }
    
    @Test
    public void testDepartureMovementAreaDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setDepartureMovementAreaDuration(getDuration(4000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(4000), flat.getDepartureMovementAreaDurationValue());
    }
    
    @Test
    public void testDepartureMovementAreaStopDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setDepartureMovementAreaStopDuration(getDuration(5000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(5000), flat.getDepartureMovementAreaStopDurationValue());
    }
    
    @Test
    public void testDepartureRunwayCrossingsDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setDepartureRunwayCrossingsDuration(getDuration(6000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(6000), flat.getDepartureRunwayCrossingsDurationValue());
    }
    
    @Test
    public void testDepartureTaxiDerivedDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setDepartureTaxiDerivedDuration(getDuration(7000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(7000), flat.getDepartureTaxiDerivedDurationValue());
    }
    
    @Test
    public void testDepartureRunwayOccupancyDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setDepartureRunwayOccupancyDuration(getDuration(8000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(8000), flat.getDepartureRunwayOccupancyDurationValue());
    }
    
    @Test
    public void testDepartureRunwaySeparationDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setDepartureRunwaySeparationDuration(getDuration(9000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(9000), flat.getDepartureRunwaySeparationDurationValue());
    }
    
    @Test
    public void testArrivalRunwaySeparationDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setArrivalRunwaySeparationDuration(getDuration(10000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(10000), flat.getArrivalRunwaySeparationDurationValue());
    }
    
    @Test
    public void testArrivalRunwayOccupancyDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setArrivalRunwayOccupancyDuration(getDuration(11000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(11000), flat.getArrivalRunwayOccupancyDurationValue());
    }
    
    @Test
    public void testArrivalMovementAreaStopDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setArrivalMovementAreaStopDuration(getDuration(12000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(12000), flat.getArrivalMovementAreaStopDurationValue());
    }
    
    @Test
    public void testArrivalRunwayCrossingsDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setArrivalRunwayCrossingsDuration(getDuration(13000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(13000), flat.getArrivalRunwayCrossingsDurationValue());
    }
    
    @Test
    public void testArrivalRampAreaDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setArrivalRampAreaDuration(getDuration(14000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(14000), flat.getArrivalRampAreaDurationValue());
    }
    
    @Test
    public void testArrivalRampAreaStopDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setArrivalRampAreaStopDuration(getDuration(15000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(15000), flat.getArrivalRampAreaStopDurationValue());
    }
    
    @Test
    public void testArrivalTaxiUndelayedDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setArrivalTaxiUndelayedDuration(getDuration(16000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(16000), flat.getArrivalTaxiUndelayedDurationValue());
    }
    
    @Test
    public void testArrivalTaxiEstimatedDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setArrivalTaxiEstimatedDuration(getDuration(17000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(17000), flat.getArrivalTaxiEstimatedDurationValue());
    }
    
    @Test
    public void testArrivalTaxiActualDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setArrivalTaxiActualDuration(getDuration(18000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(18000), flat.getArrivalTaxiActualDurationValue());
    }
    
    @Test
    public void testArrivalTaxiDerivedDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setArrivalTaxiDerivedDuration(getDuration(19000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(19000), flat.getArrivalTaxiDerivedDurationValue());
    }
    
    @Test
    public void testArrivalMovementAreaDurationValue ()
    {
        DerivedExtension ext = new DerivedExtension ();
        ext.setArrivalMovementAreaDuration(getDuration(20000L));
        
        FlatDerivedExtension flat = new FlatDerivedExtension();
        ext.copyTo(flat);
        
        assertEquals(new Long(20000), flat.getArrivalMovementAreaDurationValue());
    }
    
    private Duration getDuration (long millis)
    {
        Duration duration = null;
        
        try
        {
            duration = DatatypeFactory.newInstance().newDuration(millis);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
            e.printStackTrace();
        }
        
        return duration;
    }
}
