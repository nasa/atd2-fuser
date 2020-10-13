package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.matmdata.flight.extension.SfdpsExtension;

public class FlatSfdpsExtension
extends SfdpsExtension
implements GufiRecord
{
    private static final long serialVersionUID = -4501805800188154560L;

    private String recordIdentifier;
    private Date recordTimestamp;
    private Date timestamp;
    private String gufi;
    
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
    public void setSurfaceAirportName(String surfaceAirportName)
    {
    	// do nothing
    }
    
    @Override
    public String getSurfaceAirportName()
    {
    	return null;
    }
    
    public Double getLastSfdpsPositionAltitude ()
    {
        if (getLastSfdpsPosition() != null)
            return getLastSfdpsPosition().getAltitude();
        return null;
    }
    
    public Double getLastSfdpsPositionHeading ()
    {
        if (getLastSfdpsPosition() != null)
            return getLastSfdpsPosition().getHeading();
        return null;
    }
    
    public Double getLastSfdpsPositionLatitude ()
    {
        if (getLastSfdpsPosition() != null)
            return getLastSfdpsPosition().getLatitude();
        return null;
    }
    
    public Double getLastSfdpsPositionLongitude ()
    {
        if (getLastSfdpsPosition() != null)
            return getLastSfdpsPosition().getLongitude();
        return null;
    }
    
    public String getLastSfdpsPositionSource ()
    {
        if (getLastSfdpsPosition() != null)
            return getLastSfdpsPosition().getSource();
        return null;
    }
    
    public Double getLastSfdpsPositionSpeed ()
    {
        if (getLastSfdpsPosition() != null)
            return getLastSfdpsPosition().getSpeed();
        return null;
    }
    
    public Date getLastSfdpsPositionTimestamp ()
    {
        if (getLastSfdpsPosition() != null)
            return getLastSfdpsPosition().getTimestamp();
        return null;
    }    
}
