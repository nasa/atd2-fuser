package com.mosaicatm.fuser.datacapture.flat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.mosaicatm.fuser.datacapture.flat.FlatTbfmExtension;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.extension.TbfmExtension;

public class FlatTbfmExtensionTest
{
    @Test
    public void testPositionAltitude ()
    {
        Position position = new Position ();
        position.setAltitude(123d);
        
        TbfmExtension ext = new TbfmExtension ();
        ext.setLastTbfmPosition(position);

        FlatTbfmExtension flat = new FlatTbfmExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastTbfmPositionAltitude());
        assertEquals (ext.getLastTbfmPosition().getAltitude(), flat.getLastTbfmPositionAltitude());
    }
    
    @Test
    public void testPositionHeading ()
    {
        Position position = new Position ();
        position.setHeading(67.4d);
        
        TbfmExtension ext = new TbfmExtension ();
        ext.setLastTbfmPosition(position);

        FlatTbfmExtension flat = new FlatTbfmExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastTbfmPositionHeading());
        assertEquals (ext.getLastTbfmPosition().getHeading(), flat.getLastTbfmPositionHeading());
    }
    
    @Test
    public void testPositionLatitude ()
    {
        Position position = new Position ();
        position.setLatitude(82.456d);
        
        TbfmExtension ext = new TbfmExtension ();
        ext.setLastTbfmPosition(position);

        FlatTbfmExtension flat = new FlatTbfmExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastTbfmPositionLatitude());
        assertEquals (ext.getLastTbfmPosition().getLatitude(), flat.getLastTbfmPositionLatitude());
    }
    
    @Test
    public void testPositionLongitude ()
    {
        Position position = new Position ();
        position.setLongitude(34.98d);
        
        TbfmExtension ext = new TbfmExtension ();
        ext.setLastTbfmPosition(position);

        FlatTbfmExtension flat = new FlatTbfmExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastTbfmPositionLongitude());
        assertEquals (ext.getLastTbfmPosition().getLongitude(), flat.getLastTbfmPositionLongitude());
    }
    
    @Test
    public void testPositionSource ()
    {
        Position position = new Position ();
        position.setSource("test source");
        
        TbfmExtension ext = new TbfmExtension ();
        ext.setLastTbfmPosition(position);

        FlatTbfmExtension flat = new FlatTbfmExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastTbfmPositionSource());
        assertEquals (ext.getLastTbfmPosition().getSource(), flat.getLastTbfmPositionSource());
    }
    
    @Test
    public void testPositionSpeed ()
    {
        Position position = new Position ();
        position.setSpeed(347d);
        
        TbfmExtension ext = new TbfmExtension ();
        ext.setLastTbfmPosition(position);

        FlatTbfmExtension flat = new FlatTbfmExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastTbfmPositionSpeed());
        assertEquals (ext.getLastTbfmPosition().getSpeed(), flat.getLastTbfmPositionSpeed());
    }
    
    @Test
    public void testPositionTimestamp ()
    {
        Position position = new Position ();
        position.setTimestamp(new Date());
        
        TbfmExtension ext = new TbfmExtension ();
        ext.setLastTbfmPosition(position);

        FlatTbfmExtension flat = new FlatTbfmExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastTbfmPositionTimestamp());
        assertEquals (ext.getLastTbfmPosition().getTimestamp(), flat.getLastTbfmPositionTimestamp());
    }
}
