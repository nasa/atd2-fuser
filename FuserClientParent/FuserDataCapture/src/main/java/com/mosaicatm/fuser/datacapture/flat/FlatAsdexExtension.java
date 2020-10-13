package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.matmdata.flight.extension.AsdexExtension;

public class FlatAsdexExtension
extends AsdexExtension
implements GufiRecord
{
    private static final long serialVersionUID = -6262554768522758994L;

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
    
    public String getAsdexAirportIcao ()
    {
        if (getAsdexAirport() != null)
            return getAsdexAirport().getIcaoName();
        return null;
    }
    
    public String getAsdexAirportIata ()
    {
        if (getAsdexAirport() != null)
            return getAsdexAirport().getIataName();
        return null;
    }
    
    public Double getLastAsdexPositionAltitude ()
    {
        if (getLastAsdexPosition() != null)
            return getLastAsdexPosition().getAltitude();
        return null;
    }
    
    public Double getLastAsdexPositionHeading ()
    {
        if (getLastAsdexPosition() != null)
            return getLastAsdexPosition().getHeading();
        return null;
    }
    
    public Double getLastAsdexPositionLatitude ()
    {
        if (getLastAsdexPosition() != null)
            return getLastAsdexPosition().getLatitude();
        return null;
    }
    
    public Double getLastAsdexPositionLongitude ()
    {
        if (getLastAsdexPosition() != null)
            return getLastAsdexPosition().getLongitude();
        return null;
    }
    
    public String getLastAsdexPositionSource ()
    {
        if (getLastAsdexPosition() != null)
            return getLastAsdexPosition().getSource();
        return null;
    }
    
    public Double getLastAsdexPositionSpeed ()
    {
        if (getLastAsdexPosition() != null)
            return getLastAsdexPosition().getSpeed();
        return null;
    }
    
    public Date getLastAsdexPositionTimestamp ()
    {
        if (getLastAsdexPosition() != null)
            return getLastAsdexPosition().getTimestamp();
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
