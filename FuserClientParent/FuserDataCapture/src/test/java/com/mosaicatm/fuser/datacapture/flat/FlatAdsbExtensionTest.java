package com.mosaicatm.fuser.datacapture.flat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.mosaicatm.fuser.datacapture.flat.FlatAdsbExtension;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.extension.AdsbExtension;

public class FlatAdsbExtensionTest
{
    @Test
    public void testPositionAltitude ()
    {
        Position position = new Position ();
        position.setAltitude(123d);
        
        AdsbExtension ext = new AdsbExtension ();
        ext.setLastAdsbPosition(position);

        FlatAdsbExtension flat = new FlatAdsbExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastAdsbPositionAltitude());
        assertEquals (ext.getLastAdsbPosition().getAltitude(), flat.getLastAdsbPositionAltitude());
    }
    
    @Test
    public void testPositionHeading ()
    {
        Position position = new Position ();
        position.setHeading(67.4d);
        
        AdsbExtension ext = new AdsbExtension ();
        ext.setLastAdsbPosition(position);

        FlatAdsbExtension flat = new FlatAdsbExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastAdsbPositionHeading());
        assertEquals (ext.getLastAdsbPosition().getHeading(), flat.getLastAdsbPositionHeading());
    }
    
    @Test
    public void testPositionLatitude ()
    {
        Position position = new Position ();
        position.setLatitude(82.456d);
        
        AdsbExtension ext = new AdsbExtension ();
        ext.setLastAdsbPosition(position);

        FlatAdsbExtension flat = new FlatAdsbExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastAdsbPositionLatitude());
        assertEquals (ext.getLastAdsbPosition().getLatitude(), flat.getLastAdsbPositionLatitude());
    }
    
    @Test
    public void testPositionLongitude ()
    {
        Position position = new Position ();
        position.setLongitude(34.98d);
        
        AdsbExtension ext = new AdsbExtension ();
        ext.setLastAdsbPosition(position);

        FlatAdsbExtension flat = new FlatAdsbExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastAdsbPositionLongitude());
        assertEquals (ext.getLastAdsbPosition().getLongitude(), flat.getLastAdsbPositionLongitude());
    }
    
    @Test
    public void testPositionSource ()
    {
        Position position = new Position ();
        position.setSource("test source");
        
        AdsbExtension ext = new AdsbExtension ();
        ext.setLastAdsbPosition(position);

        FlatAdsbExtension flat = new FlatAdsbExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastAdsbPositionSource());
        assertEquals (ext.getLastAdsbPosition().getSource(), flat.getLastAdsbPositionSource());
    }
    
    @Test
    public void testPositionSpeed ()
    {
        Position position = new Position ();
        position.setSpeed(347d);
        
        AdsbExtension ext = new AdsbExtension ();
        ext.setLastAdsbPosition(position);

        FlatAdsbExtension flat = new FlatAdsbExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastAdsbPositionSpeed());
        assertEquals (ext.getLastAdsbPosition().getSpeed(), flat.getLastAdsbPositionSpeed());
    }
    
    @Test
    public void testPositionTimestamp ()
    {
        Position position = new Position ();
        position.setTimestamp(new Date());
        
        AdsbExtension ext = new AdsbExtension ();
        ext.setLastAdsbPosition(position);

        FlatAdsbExtension flat = new FlatAdsbExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastAdsbPositionTimestamp());
        assertEquals (ext.getLastAdsbPosition().getTimestamp(), flat.getLastAdsbPositionTimestamp());
    }
}
