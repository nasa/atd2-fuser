package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.fuser.datacapture.util.CaptureUtils;
import com.mosaicatm.matmdata.flight.extension.TfmTfdmExtension;

public class FlatTfmTfdmExtension
extends TfmTfdmExtension
implements GufiRecord
{
    private static final long serialVersionUID = 4998446634309307240L;
    
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
    
    public String getAccDepRwys()
    {
        return( CaptureUtils.listAsString( getAccDepRwyList(), " " ));
    }
    
    public String getUnaccDepRwys()
    {
        return( CaptureUtils.listAsString( getUnaccDepRwyList(), " " ));
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
