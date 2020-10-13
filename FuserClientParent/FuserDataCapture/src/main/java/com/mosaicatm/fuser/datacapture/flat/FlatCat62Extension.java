package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.matmdata.flight.extension.Cat62Extension;

public class FlatCat62Extension
extends Cat62Extension
implements GufiRecord
{
    private static final long serialVersionUID = 2235822848959350281L;

    private String recordIdentifier;
    private Date recordTimestamp;
    private Date timestamp;
	private String gufi;
	private String surfaceAirportName;
    
    @Override
    public void setRecordIdentifier (String recordIdentifier)
    {
        this.recordIdentifier = recordIdentifier;
    }
    
    @Override
    public String getRecordIdentifier ()
    {
        return recordIdentifier;
    }
    
    @Override
    public void setRecordTimestamp (Date recordTimestamp)
    {
        this.recordTimestamp = recordTimestamp;
    }
    
    @Override
    public Date getRecordTimestamp ()
    {
        return recordTimestamp;
    }
    
    public Double getLastPositionAltitude ()
    {
        if (getLastPosition() != null)
            return getLastPosition().getAltitude();
        return null;
    }
    
    public Double getLastPositionHeading ()
    {
        if (getLastPosition() != null)
            return getLastPosition().getHeading();
        return null;
    }
    
    public Double getLastPositionLatitude ()
    {
        if (getLastPosition() != null)
            return getLastPosition().getLatitude();
        return null;
    }
    
    public Double getLastPositionLongitude ()
    {
        if (getLastPosition() != null)
            return getLastPosition().getLongitude();
        return null;
    }
    
    public String getLastPositionSource ()
    {
        if (getLastPosition() != null)
            return getLastPosition().getSource();
        return null;
    }
    
    public Double getLastPositionSpeed ()
    {
        if (getLastPosition() != null)
            return getLastPosition().getSpeed();
        return null;
    }
    
    public Date getLastPositionTimestamp ()
    {
        if (getLastPosition() != null)
            return getLastPosition().getTimestamp();
        return null;
    }

	@Override
	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
		
	}

	@Override
	public Date getTimestamp()
	{
		return timestamp;
	}

	    @Override
    public void setGufi(String gufi)
    {
        this.gufi = gufi;
    }

    @Override
    public String getGufi()
    {
        return gufi;
    }

    @Override
    public void setSurfaceAirportName( String surfaceAirportName )
    {
        this.surfaceAirportName = surfaceAirportName;
    }

    @Override
    public String getSurfaceAirportName()
    {
        return surfaceAirportName;
    }
}
