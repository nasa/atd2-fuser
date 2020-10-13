package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.matmdata.flight.extension.TfmExtension;

public class FlatTfmExtension
extends TfmExtension
implements GufiRecord
{
    private static final long serialVersionUID = 8845566301080465579L;

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
    
    public Double getLastTfmPositionAltitude ()
    {
        if (getLastTfmPosition() != null)
            return getLastTfmPosition().getAltitude();
        return null;
    }
    
    public Double getLastTfmPositionHeading ()
    {
        if (getLastTfmPosition() != null)
            return getLastTfmPosition().getHeading();
        return null;
    }
    
    public Double getLastTfmPositionLatitude ()
    {
        if (getLastTfmPosition() != null)
            return getLastTfmPosition().getLatitude();
        return null;
    }
    
    public Double getLastTfmPositionLongitude ()
    {
        if (getLastTfmPosition() != null)
            return getLastTfmPosition().getLongitude();
        return null;
    }
    
    public String getLastTfmPositionSource ()
    {
        if (getLastTfmPosition() != null)
            return getLastTfmPosition().getSource();
        return null;
    }
    
    public Double getLastTfmPositionSpeed ()
    {
        if (getLastTfmPosition() != null)
            return getLastTfmPosition().getSpeed();
        return null;
    }
    
    public Date getLastTfmPositionTimestamp ()
    {
        if (getLastTfmPosition() != null)
            return getLastTfmPosition().getTimestamp();
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
