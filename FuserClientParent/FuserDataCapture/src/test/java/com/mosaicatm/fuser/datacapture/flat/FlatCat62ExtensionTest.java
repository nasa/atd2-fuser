package com.mosaicatm.fuser.datacapture.flat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.mosaicatm.fuser.datacapture.flat.FlatCat62Extension;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.extension.Cat62Extension;

public class FlatCat62ExtensionTest
{
    @Test
    public void testPositionAltitude ()
    {
        Position position = new Position ();
        position.setAltitude(123d);
        
        Cat62Extension ext = new Cat62Extension ();
        ext.setLastPosition(position);

        FlatCat62Extension flat = new FlatCat62Extension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastPositionAltitude());
        assertEquals (ext.getLastPosition().getAltitude(), flat.getLastPositionAltitude());
    }
    
    @Test
    public void testPositionHeading ()
    {
        Position position = new Position ();
        position.setHeading(67.4d);
        
        Cat62Extension ext = new Cat62Extension ();
        ext.setLastPosition(position);

        FlatCat62Extension flat = new FlatCat62Extension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastPositionHeading());
        assertEquals (ext.getLastPosition().getHeading(), flat.getLastPositionHeading());
    }
    
    @Test
    public void testPositionLatitude ()
    {
        Position position = new Position ();
        position.setLatitude(82.456d);
        
        Cat62Extension ext = new Cat62Extension ();
        ext.setLastPosition(position);

        FlatCat62Extension flat = new FlatCat62Extension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastPositionLatitude());
        assertEquals (ext.getLastPosition().getLatitude(), flat.getLastPositionLatitude());
    }
    
    @Test
    public void testPositionLongitude ()
    {
        Position position = new Position ();
        position.setLongitude(34.98d);
        
        Cat62Extension ext = new Cat62Extension ();
        ext.setLastPosition(position);

        FlatCat62Extension flat = new FlatCat62Extension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastPositionLongitude());
        assertEquals (ext.getLastPosition().getLongitude(), flat.getLastPositionLongitude());
    }
    
    @Test
    public void testPositionSource ()
    {
        Position position = new Position ();
        position.setSource("test source");
        
        Cat62Extension ext = new Cat62Extension ();
        ext.setLastPosition(position);

        FlatCat62Extension flat = new FlatCat62Extension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastPositionSource());
        assertEquals (ext.getLastPosition().getSource(), flat.getLastPositionSource());
    }
    
    @Test
    public void testPositionSpeed ()
    {
        Position position = new Position ();
        position.setSpeed(347d);
        
        Cat62Extension ext = new Cat62Extension ();
        ext.setLastPosition(position);

        FlatCat62Extension flat = new FlatCat62Extension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastPositionSpeed());
        assertEquals (ext.getLastPosition().getSpeed(), flat.getLastPositionSpeed());
    }
    
    @Test
    public void testPositionTimestamp ()
    {
        Position position = new Position ();
        position.setTimestamp(new Date());
        
        Cat62Extension ext = new Cat62Extension ();
        ext.setLastPosition(position);

        FlatCat62Extension flat = new FlatCat62Extension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastPositionTimestamp());
        assertEquals (ext.getLastPosition().getTimestamp(), flat.getLastPositionTimestamp());
    }
}
