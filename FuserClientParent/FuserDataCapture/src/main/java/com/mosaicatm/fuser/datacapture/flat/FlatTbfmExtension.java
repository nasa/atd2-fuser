package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.fuser.datacapture.util.CaptureUtils;

import com.mosaicatm.matmdata.flight.extension.TbfmExtension;

public class FlatTbfmExtension
extends TbfmExtension
implements GufiRecord
{
    private static final long serialVersionUID = -3394373221225513650L;

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
    
    public Double getLastTbfmPositionAltitude ()
    {
        if (getLastTbfmPosition() != null)
            return getLastTbfmPosition().getAltitude();
        return null;
    }
    
    public Double getLastTbfmPositionHeading ()
    {
        if (getLastTbfmPosition() != null)
            return getLastTbfmPosition().getHeading();
        return null;
    }
    
    public Double getLastTbfmPositionLatitude ()
    {
        if (getLastTbfmPosition() != null)
            return getLastTbfmPosition().getLatitude();
        return null;
    }
    
    public Double getLastTbfmPositionLongitude ()
    {
        if (getLastTbfmPosition() != null)
            return getLastTbfmPosition().getLongitude();
        return null;
    }
    
    public String getLastTbfmPositionSource ()
    {
        if (getLastTbfmPosition() != null)
            return getLastTbfmPosition().getSource();
        return null;
    }
    
    public Double getLastTbfmPositionSpeed ()
    {
        if (getLastTbfmPosition() != null)
            return getLastTbfmPosition().getSpeed();
        return null;
    }
    
    public Date getLastTbfmPositionTimestamp ()
    {
        if (getLastTbfmPosition() != null)
            return getLastTbfmPosition().getTimestamp();
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

                        
    public String getMeterReferencePoints()
    {
        String json = null;

        if(( getMeterReferencePointList() != null ) && !getMeterReferencePointList().isEmpty() )
        {
            json = CaptureUtils.toBulkCopyJson( getMeterReferencePointList() );
        }

        return json;        
    }
                        
    public int getMeterReferencePointCount()
    {
        if( getMeterReferencePointList() == null )
        {
            return( 0 );
        }
        
        return( getMeterReferencePointList().size() );
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
