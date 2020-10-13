package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.matmdata.flight.extension.SurfaceModelExtension;

public class FlatSurfaceModelExtension
extends SurfaceModelExtension
implements GufiRecord
{
    private static final long serialVersionUID = 808597019648724527L;
    
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
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }
    
    public String getTacticalGroup(){
        return tacticalGroup;
    }
    
    public Boolean isRepositioned(){
        Boolean rtn = null;
        if(operationalStatus != null)
            rtn = operationalStatus.isRepositioned();
        return rtn;
    }

    public Boolean isSuspendedFromScheduling(){
        Boolean rtn = null;
        if(operationalStatus != null)
            rtn = operationalStatus.isSuspendedFromScheduling();
        return rtn;
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
