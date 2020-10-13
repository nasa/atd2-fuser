package com.mosaicatm.fuser.datacapture.flat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.mosaicatm.fuser.datacapture.flat.FlatTfmExtension;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;

public class FlatTfmExtensionTest
{
    @Test
    public void testPositionAltitude ()
    {
        Position position = new Position ();
        position.setAltitude(123d);
        
        TfmExtension ext = new TfmExtension ();
        ext.setLastTfmPosition(position);

        FlatTfmExtension flat = new FlatTfmExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastTfmPositionAltitude());
        assertEquals (ext.getLastTfmPosition().getAltitude(), flat.getLastTfmPositionAltitude());
    }
    
    @Test
    public void testPositionHeading ()
    {
        Position position = new Position ();
        position.setHeading(67.4d);
        
        TfmExtension ext = new TfmExtension ();
        ext.setLastTfmPosition(position);

        FlatTfmExtension flat = new FlatTfmExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastTfmPositionHeading());
        assertEquals (ext.getLastTfmPosition().getHeading(), flat.getLastTfmPositionHeading());
    }
    
    @Test
    public void testPositionLatitude ()
    {
        Position position = new Position ();
        position.setLatitude(82.456d);
        
        TfmExtension ext = new TfmExtension ();
        ext.setLastTfmPosition(position);

        FlatTfmExtension flat = new FlatTfmExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastTfmPositionLatitude());
        assertEquals (ext.getLastTfmPosition().getLatitude(), flat.getLastTfmPositionLatitude());
    }
    
    @Test
    public void testPositionLongitude ()
    {
        Position position = new Position ();
        position.setLongitude(34.98d);
        
        TfmExtension ext = new TfmExtension ();
        ext.setLastTfmPosition(position);

        FlatTfmExtension flat = new FlatTfmExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastTfmPositionLongitude());
        assertEquals (ext.getLastTfmPosition().getLongitude(), flat.getLastTfmPositionLongitude());
    }
    
    @Test
    public void testPositionSource ()
    {
        Position position = new Position ();
        position.setSource("test source");
        
        TfmExtension ext = new TfmExtension ();
        ext.setLastTfmPosition(position);

        FlatTfmExtension flat = new FlatTfmExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastTfmPositionSource());
        assertEquals (ext.getLastTfmPosition().getSource(), flat.getLastTfmPositionSource());
    }
    
    @Test
    public void testPositionSpeed ()
    {
        Position position = new Position ();
        position.setSpeed(347d);
        
        TfmExtension ext = new TfmExtension ();
        ext.setLastTfmPosition(position);

        FlatTfmExtension flat = new FlatTfmExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastTfmPositionSpeed());
        assertEquals (ext.getLastTfmPosition().getSpeed(), flat.getLastTfmPositionSpeed());
    }
    
    @Test
    public void testPositionTimestamp ()
    {
        Position position = new Position ();
        position.setTimestamp(new Date());
        
        TfmExtension ext = new TfmExtension ();
        ext.setLastTfmPosition(position);

        FlatTfmExtension flat = new FlatTfmExtension ();
        ext.copyTo(flat);
        
        assertNotNull (flat.getLastTfmPositionTimestamp());
        assertEquals (ext.getLastTfmPosition().getTimestamp(), flat.getLastTfmPositionTimestamp());
    }
}
