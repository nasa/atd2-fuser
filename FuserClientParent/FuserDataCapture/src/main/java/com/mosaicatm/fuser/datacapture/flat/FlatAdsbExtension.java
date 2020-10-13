package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.matmdata.flight.extension.AdsbExtension;

public class FlatAdsbExtension
extends AdsbExtension
implements GufiRecord
{
    private static final long serialVersionUID = -4501805800188154560L;

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
    
    public Double getLastAdsbPositionAltitude ()
    {
        if (getLastAdsbPosition() != null)
            return getLastAdsbPosition().getAltitude();
        return null;
    }
    
    public Double getLastAdsbPositionHeading ()
    {
        if (getLastAdsbPosition() != null)
            return getLastAdsbPosition().getHeading();
        return null;
    }
    
    public Double getLastAdsbPositionLatitude ()
    {
        if (getLastAdsbPosition() != null)
            return getLastAdsbPosition().getLatitude();
        return null;
    }
    
    public Double getLastAdsbPositionLongitude ()
    {
        if (getLastAdsbPosition() != null)
            return getLastAdsbPosition().getLongitude();
        return null;
    }
    
    public String getLastAdsbPositionSource ()
    {
        if (getLastAdsbPosition() != null)
            return getLastAdsbPosition().getSource();
        return null;
    }
    
    public Double getLastAdsbPositionSpeed ()
    {
        if (getLastAdsbPosition() != null)
            return getLastAdsbPosition().getSpeed();
        return null;
    }
    
    public Date getLastAdsbPositionTimestamp ()
    {
        if (getLastAdsbPosition() != null)
            return getLastAdsbPosition().getTimestamp();
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
