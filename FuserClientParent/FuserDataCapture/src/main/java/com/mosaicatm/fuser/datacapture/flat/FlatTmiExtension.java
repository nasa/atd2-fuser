package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.fuser.datacapture.util.CaptureUtils;
import com.mosaicatm.matmdata.flight.extension.TmiExtension;

public class FlatTmiExtension
    extends TmiExtension
implements GufiRecord
{
    private static final long serialVersionUID = 1652281842997486454L;

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

    public String getApreqs()
    {
        if (getApreqList() == null || getApreqList().isEmpty())
            return null;

        return CaptureUtils.toBulkCopyJson(getApreqList());
    }

    public String getResourceClosures()
    {
        if (getResourceClosureList() == null || getResourceClosureList().isEmpty())
            return null;

        return CaptureUtils.toBulkCopyJson(getResourceClosureList());
    }

    public String getGroundDelayPrograms()
    {
        if (getGroundDelayProgramList() == null
            || getGroundDelayProgramList().isEmpty())
            return null;

        return CaptureUtils.toBulkCopyJson(getGroundDelayProgramList());
    }

    public String getGroundStops()
    {
        if (getGroundStopList() == null || getGroundStopList().isEmpty())
            return null;
        return CaptureUtils.toBulkCopyJson(getGroundStopList());
    }

    public String getMilesInTrails()
    {
        if (getMilesInTrailList() == null || getMilesInTrailList().isEmpty())
            return null;

        return CaptureUtils.toBulkCopyJson(getMilesInTrailList());
    }

    public String getReroutes()
    {
        if (getRerouteList() == null || getRerouteList().isEmpty())
            return null;

        return CaptureUtils.toBulkCopyJson(getRerouteList());
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
