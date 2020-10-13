package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.fuser.datacapture.util.CaptureUtils;
import com.mosaicatm.matmdata.flight.extension.IdacExtension;

public class FlatIdacExtension
extends IdacExtension
implements GufiRecord
{
	private static final long serialVersionUID = 6248909714175220738L;
	
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
    
    public String getArtccSchedulingListValues()
    {
        if (getArtccSchedulingList() != null)
		{
                    if (getArtccSchedulingList().getArtccSchedulingData() != null)
                        return CaptureUtils.artccSchedulingListAsString(getArtccSchedulingList().getArtccSchedulingData(), " ");
		}
        
        return null;
    }
    
    
    public String getFlowAssignmentListValues()
    {

        if (getFlowAssignmentList() != null)
        {
            if (getFlowAssignmentList().getFlowAssignmentData() != null)
                return CaptureUtils.flowAssignmentListAsString(getFlowAssignmentList().getFlowAssignmentData(), " ");
        } 
        return null;
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
