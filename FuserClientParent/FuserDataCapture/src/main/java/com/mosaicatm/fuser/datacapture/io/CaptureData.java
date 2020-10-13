package com.mosaicatm.fuser.datacapture.io;

import java.util.Date;

import com.mosaicatm.fuser.datacapture.CaptureType;

public class CaptureData<T>
{
    private CaptureType type;
    private String recordId;
    private Date recordTimestamp;
    private Date timestamp;
    private String gufi;
    private String surfaceAirportName;
    private T data;
    
    public CaptureData()
    {
        
    }
    
    public CaptureData(T data)
    {
        this.data = data;
    }
        
    public CaptureType getType()
    {
        return type;
    }
    
    public void setType(CaptureType type)
    {
        this.type = type;
    }
    
    public String getRecordId()
    {
        return recordId;
    }
    
    public void setRecordId(String recordId)
    {
        this.recordId = recordId;
    }
    
    public Date getRecordTimestamp()
    {
        return recordTimestamp;
    }
    
    public void setRecordTimestamp(Date recordTimestamp)
    {
        this.recordTimestamp = recordTimestamp;
    }
    
    public Date getTimestamp()
    {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }
    
    public String getGufi()
    {
        return gufi;
    }
    
    public void setGufi(String gufi)
    {
        this.gufi = gufi;
    }

    public String getSurfaceAirportName()
    {
        return surfaceAirportName;
    }

    public void setSurfaceAirportName( String surfaceAirportName )
    {
        this.surfaceAirportName = surfaceAirportName;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }
    
}
