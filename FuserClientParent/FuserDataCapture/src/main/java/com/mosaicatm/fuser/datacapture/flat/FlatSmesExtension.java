package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.matmdata.flight.extension.SmesExtension;

public class FlatSmesExtension extends SmesExtension implements GufiRecord {

    private static final long serialVersionUID = 8718014100398523959L;

    private String recordIdentifier;
    private Date recordTimestamp;
    private Date timestamp;
    private String gufi;

    @Override
    public void setRecordIdentifier(String recordIdentifier) {
        this.recordIdentifier = recordIdentifier;
    }

    @Override
    public String getRecordIdentifier() {
        return recordIdentifier;
    }

    @Override
    public void setRecordTimestamp(Date recordTimestamp) {
        this.recordTimestamp = recordTimestamp;
    }

    @Override
    public Date getRecordTimestamp() {
        return recordTimestamp;
    }

    public Double getLastSmesPositionAltitude() {
        if (getLastSmesPosition() != null)
            return getLastSmesPosition().getAltitude();
        return null;
    }

    public Double getLastSmesPositionLatitude() {
        if (getLastSmesPosition() != null)
            return getLastSmesPosition().getLatitude();
        return null;
    }

    public Double getLastSmesPositionLongitude() {
        if (getLastSmesPosition() != null)
            return getLastSmesPosition().getLongitude();
        return null;
    }

    public String getLastSmesPositionSource() {
        if (getLastSmesPosition() != null)
            return getLastSmesPosition().getSource();
        return null;
    }

    public Date getLastSmesPositionTimestamp() {
        if (getLastSmesPosition() != null)
            return getLastSmesPosition().getTimestamp();
        return null;
    }

    @Override
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public void setGufi(String gufi) {
        this.gufi = gufi;
    }

    @Override
    public String getGufi() {
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
}
