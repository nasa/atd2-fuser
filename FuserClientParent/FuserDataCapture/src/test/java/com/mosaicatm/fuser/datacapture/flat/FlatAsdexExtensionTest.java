package com.mosaicatm.fuser.datacapture.flat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.mosaicatm.fuser.datacapture.flat.FlatAsdexExtension;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.extension.AsdexExtension;

public class FlatAsdexExtensionTest
{
    @Test
    public void testAerodromeIcao ()
    {
        Aerodrome aerodrome = new Aerodrome ();
        aerodrome.setIcaoName("icao");
        
        AsdexExtension ext = new AsdexExtension ();
        ext.setAsdexAirport(aerodrome);

        FlatAsdexExtension flat = new FlatAsdexExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getAsdexAirportIcao());
        assertEquals (ext.getAsdexAirport().getIcaoName(), flat.getAsdexAirportIcao());
    }
    
    @Test
    public void testAerodromeIata ()
    {
        Aerodrome aerodrome = new Aerodrome ();
        aerodrome.setIataName("iata");
        
        AsdexExtension ext = new AsdexExtension ();
        ext.setAsdexAirport(aerodrome);

        FlatAsdexExtension flat = new FlatAsdexExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getAsdexAirportIata());
        assertEquals (ext.getAsdexAirport().getIataName(), flat.getAsdexAirportIata());
    }
    
    @Test
    public void testPositionAltitude ()
    {
        Position position = new Position ();
        position.setAltitude(123d);
        
        AsdexExtension ext = new AsdexExtension ();
        ext.setLastAsdexPosition(position);

        FlatAsdexExtension flat = new FlatAsdexExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastAsdexPositionAltitude());
        assertEquals (ext.getLastAsdexPosition().getAltitude(), flat.getLastAsdexPositionAltitude());
    }
    
    @Test
    public void testPositionHeading ()
    {
        Position position = new Position ();
        position.setHeading(67.4d);
        
        AsdexExtension ext = new AsdexExtension ();
        ext.setLastAsdexPosition(position);

        FlatAsdexExtension flat = new FlatAsdexExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastAsdexPositionHeading());
        assertEquals (ext.getLastAsdexPosition().getHeading(), flat.getLastAsdexPositionHeading());
    }
    
    @Test
    public void testPositionLatitude ()
    {
        Position position = new Position ();
        position.setLatitude(82.456d);
        
        AsdexExtension ext = new AsdexExtension ();
        ext.setLastAsdexPosition(position);

        FlatAsdexExtension flat = new FlatAsdexExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastAsdexPositionLatitude());
        assertEquals (ext.getLastAsdexPosition().getLatitude(), flat.getLastAsdexPositionLatitude());
    }
    
    @Test
    public void testPositionLongitude ()
    {
        Position position = new Position ();
        position.setLongitude(34.98d);
        
        AsdexExtension ext = new AsdexExtension ();
        ext.setLastAsdexPosition(position);

        FlatAsdexExtension flat = new FlatAsdexExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastAsdexPositionLongitude());
        assertEquals (ext.getLastAsdexPosition().getLongitude(), flat.getLastAsdexPositionLongitude());
    }
    
    @Test
    public void testPositionSource ()
    {
        Position position = new Position ();
        position.setSource("test source");
        
        AsdexExtension ext = new AsdexExtension ();
        ext.setLastAsdexPosition(position);

        FlatAsdexExtension flat = new FlatAsdexExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastAsdexPositionSource());
        assertEquals (ext.getLastAsdexPosition().getSource(), flat.getLastAsdexPositionSource());
    }
    
    @Test
    public void testPositionSpeed ()
    {
        Position position = new Position ();
        position.setSpeed(347d);
        
        AsdexExtension ext = new AsdexExtension ();
        ext.setLastAsdexPosition(position);

        FlatAsdexExtension flat = new FlatAsdexExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastAsdexPositionSpeed());
        assertEquals (ext.getLastAsdexPosition().getSpeed(), flat.getLastAsdexPositionSpeed());
    }
    
    @Test
    public void testPositionTimestamp ()
    {
        Position position = new Position ();
        position.setTimestamp(new Date());
        
        AsdexExtension ext = new AsdexExtension ();
        ext.setLastAsdexPosition(position);

        FlatAsdexExtension flat = new FlatAsdexExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastAsdexPositionTimestamp());
        assertEquals (ext.getLastAsdexPosition().getTimestamp(), flat.getLastAsdexPositionTimestamp());
    }
}
