package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class FlatMatmAircraft
extends MatmAircraft
implements Record
{
    private static final long serialVersionUID = -1337785100877115125L;

    private String recordIdentifier;
    private Date recordTimestamp;

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
    public void setSurfaceAirportName(String surfaceAirportName)
    {
    	// do nothing
    }
    
    @Override
    public String getSurfaceAirportName()
    {
    	return null;
    }
}
