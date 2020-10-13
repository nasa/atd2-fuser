package com.mosaicatm.fuser.datacapture.flat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.junit.Test;

import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.common.SurfaceFlightState;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class FlatMatmFlightTest
{
    @Test
    public void testPositionAltitude ()
    {
        Position position = new Position ();
        position.setAltitude(123d);
        
        MatmFlight ext = new MatmFlight ();
        ext.setPosition(position);

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getPositionAltitude());
        assertEquals (ext.getPosition().getAltitude(), flat.getPositionAltitude());
    }
    
    @Test
    public void testPositionHeading ()
    {
        Position position = new Position ();
        position.setHeading(67.4d);
        
        MatmFlight ext = new MatmFlight ();
        ext.setPosition(position);

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getPositionHeading());
        assertEquals (ext.getPosition().getHeading(), flat.getPositionHeading());
    }
    
    @Test
    public void testPositionLatitude ()
    {
        Position position = new Position ();
        position.setLatitude(82.456d);
        
        MatmFlight ext = new MatmFlight ();
        ext.setPosition(position);

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getPositionLatitude());
        assertEquals (ext.getPosition().getLatitude(), flat.getPositionLatitude());
    }
    
    @Test
    public void testPositionLongitude ()
    {
        Position position = new Position ();
        position.setLongitude(34.98d);
        
        MatmFlight ext = new MatmFlight ();
        ext.setPosition(position);

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getPositionLongitude());
        assertEquals (ext.getPosition().getLongitude(), flat.getPositionLongitude());
    }
    
    @Test
    public void testPositionSource ()
    {
        Position position = new Position ();
        position.setSource("test source");
        
        MatmFlight ext = new MatmFlight ();
        ext.setPosition(position);

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getPositionSource());
        assertEquals (ext.getPosition().getSource(), flat.getPositionSource());
    }
    
    @Test
    public void testPositionSpeed ()
    {
        Position position = new Position ();
        position.setSpeed(347d);
        
        MatmFlight ext = new MatmFlight ();
        ext.setPosition(position);

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getPositionSpeed());
        assertEquals (ext.getPosition().getSpeed(), flat.getPositionSpeed());
    }
    
    @Test
    public void testPositionTimestamp ()
    {
        Position position = new Position ();
        position.setTimestamp(new Date());
        
        MatmFlight ext = new MatmFlight ();
        ext.setPosition(position);

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getPositionTimestamp());
        assertEquals (ext.getPosition().getTimestamp(), flat.getPositionTimestamp());
    }
    
    @Test
    public void testArrivalAerodromeIataName ()
    {
        Aerodrome aerodrome = new Aerodrome ();
        aerodrome.setIataName("iata");
        
        MatmFlight ext = new MatmFlight ();
        ext.setArrivalAerodrome(aerodrome);

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getArrivalAerodromeIataName());
        assertEquals (ext.getArrivalAerodrome().getIataName(), flat.getArrivalAerodromeIataName());
    }
    
    @Test
    public void testArrivalAerodromeIcaoName ()
    {
        Aerodrome aerodrome = new Aerodrome ();
        aerodrome.setIcaoName("icao");
        
        MatmFlight ext = new MatmFlight ();
        ext.setArrivalAerodrome(aerodrome);

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getArrivalAerodromeIcaoName());
        assertEquals (ext.getArrivalAerodrome().getIcaoName(), flat.getArrivalAerodromeIcaoName());
    }
    
    @Test
    public void testDepartureAerodromeIataName ()
    {
        Aerodrome aerodrome = new Aerodrome ();
        aerodrome.setIataName("iata");
        
        MatmFlight ext = new MatmFlight ();
        ext.setDepartureAerodrome(aerodrome);

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getDepartureAerodromeIataName());
        assertEquals (ext.getDepartureAerodrome().getIataName(), flat.getDepartureAerodromeIataName());
    }
    
    @Test
    public void testDepartureAerodromeIcaoName ()
    {
        Aerodrome aerodrome = new Aerodrome ();
        aerodrome.setIcaoName("icao");
        
        MatmFlight ext = new MatmFlight ();
        ext.setDepartureAerodrome(aerodrome);

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getDepartureAerodromeIcaoName());
        assertEquals (ext.getDepartureAerodrome().getIcaoName(), flat.getDepartureAerodromeIcaoName());
    }
    
    @Test
    public void testFixListValues ()
    {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        
        MatmFlight ext = new MatmFlight ();
        ext.setFixList(list);
        
        FlatMatmFlight flat = new FlatMatmFlight();
        ext.copyTo(flat);
        
        assertEquals("a b c", flat.getFixListValues());
    }
    
    @Test
    public void testSectorListValues ()
    {
        List<String> list = new ArrayList<>();
        list.add("d");
        list.add("e");
        list.add("f");
        
        MatmFlight ext = new MatmFlight ();
        ext.setSectorList(list);
        
        FlatMatmFlight flat = new FlatMatmFlight();
        ext.copyTo(flat);
        
        assertEquals("d e f", flat.getSectorListValues());
    }
    
    @Test
    public void testDepartureQueueDurationValue ()
    {
        MatmFlight ext = new MatmFlight ();
        ext.setDepartureQueueDuration(getDuration(1000L));

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertEquals(new Long(1000), flat.getDepartureQueueDurationValue());
    }
    
    @Test
    public void testDepartureTaxiUndelayedDurationValue ()
    {
        MatmFlight ext = new MatmFlight ();
        ext.setDepartureTaxiUndelayedDuration(getDuration(2000L));

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertEquals(new Long(2000), flat.getDepartureTaxiUndelayedDurationValue());
    }
    
    @Test
    public void testDepartureTaxiEstimatedDurationValue ()
    {
        MatmFlight ext = new MatmFlight ();
        ext.setDepartureTaxiEstimatedDuration(getDuration(3000L));

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertEquals(new Long(3000), flat.getDepartureTaxiEstimatedDurationValue());
    }
    
    @Test
    public void testDepartureTaxiActualDurationValue ()
    {
        MatmFlight ext = new MatmFlight ();
        ext.setDepartureTaxiActualDuration(getDuration(4000L));

        FlatMatmFlight flat = new FlatMatmFlight ();
        ext.copyTo(flat);
        
        assertEquals(new Long(4000), flat.getDepartureTaxiActualDurationValue());
    }
    
    @Test
    public void testSurfaceFlightState ()
    {
        MatmFlight flight = new MatmFlight ();
        flight.setDepartureSurfaceFlightState(SurfaceFlightState.IN_QUEUE);
        flight.setArrivalSurfaceFlightState(SurfaceFlightState.ENROUTE);

        FlatMatmFlight flat = new FlatMatmFlight ();
        flight.copyTo(flat);
        
        assertEquals(SurfaceFlightState.IN_QUEUE.toString(), flat.getSurfaceFlightState());
        
        flight.setDepartureSurfaceFlightState(SurfaceFlightState.OFF);
        flight.setArrivalSurfaceFlightState(SurfaceFlightState.TERMINAL_AIRSPACE);

        flat = new FlatMatmFlight ();
        flight.copyTo(flat);
        
        assertEquals(SurfaceFlightState.TERMINAL_AIRSPACE.toString(), flat.getSurfaceFlightState());
        
        flight.setDepartureSurfaceFlightState(SurfaceFlightState.OFF);
        flight.setArrivalSurfaceFlightState(SurfaceFlightState.UNKNOWN);

        flat = new FlatMatmFlight ();
        flight.copyTo(flat);
        
        assertEquals(SurfaceFlightState.OFF.toString(), flat.getSurfaceFlightState());
        
        flight.setDepartureSurfaceFlightState(SurfaceFlightState.UNKNOWN);
        flight.setArrivalSurfaceFlightState(SurfaceFlightState.ENROUTE);

        flat = new FlatMatmFlight ();
        flight.copyTo(flat);
        
        assertEquals(SurfaceFlightState.ENROUTE.toString(), flat.getSurfaceFlightState());
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
