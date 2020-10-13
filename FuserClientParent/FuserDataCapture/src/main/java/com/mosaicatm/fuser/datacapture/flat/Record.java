package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

public interface Record
{
    public void setRecordIdentifier(String recordIdentifier);
    
    public String getRecordIdentifier();
    
    public void setRecordTimestamp(Date recordTimestamp);
    
    public Date getRecordTimestamp();
    
    public void setTimestamp(Date timestamp);
    
    public Date getTimestamp();

    public void setSurfaceAirportName(String surfaceAirportName);
    
    public String getSurfaceAirportName();
}
