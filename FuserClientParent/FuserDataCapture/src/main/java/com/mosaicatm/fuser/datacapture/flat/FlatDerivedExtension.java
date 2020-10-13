package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.fuser.datacapture.util.CaptureUtils;
import com.mosaicatm.matmdata.flight.extension.DerivedExtension;

public class FlatDerivedExtension
extends DerivedExtension
implements GufiRecord
{
    private static final long serialVersionUID = 6163487060154680852L;

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
    
    public Long getDepartureRunwayCrossedDurationValue ()
    {
        if (getDepartureRunwayCrossedDuration() != null)
            return CaptureUtils.durationToMillis(getDepartureRunwayCrossedDuration());
        return null;
    }
    
    public Long getDepartureRampAreaDurationValue ()
    {
        if (getDepartureRampAreaDuration() != null)
            return CaptureUtils.durationToMillis(getDepartureRampAreaDuration());
        return null;
    }
    
    public Long getDepartureRampAreaStopDurationValue ()
    {
        if (getDepartureRampAreaStopDuration() != null)
            return CaptureUtils.durationToMillis(getDepartureRampAreaStopDuration());
        return null;
    }
    
    public Long getDepartureMovementAreaDurationValue ()
    {
        if (getDepartureMovementAreaDuration() != null)
            return CaptureUtils.durationToMillis(getDepartureMovementAreaDuration());
        return null;
    }
    
    public Long getDepartureMovementAreaStopDurationValue ()
    {
        if (getDepartureMovementAreaStopDuration() != null)
            return CaptureUtils.durationToMillis(getDepartureMovementAreaStopDuration());
        return null;
    }
    
    public Long getDepartureRunwayCrossingsDurationValue ()
    {
        if (getDepartureRunwayCrossingsDuration() != null)
            return CaptureUtils.durationToMillis(getDepartureRunwayCrossingsDuration());
        return null;
    }
    
    public Long getDepartureTaxiDerivedDurationValue ()
    {
        if (getDepartureTaxiDerivedDuration() != null)
            return CaptureUtils.durationToMillis(getDepartureTaxiDerivedDuration());
        return null;
    }
    
    public Long getDepartureRunwayOccupancyDurationValue ()
    {
        if (getDepartureRunwayOccupancyDuration() != null)
            return CaptureUtils.durationToMillis(getDepartureRunwayOccupancyDuration());
        return null;
    }
    
    public Long getDepartureRunwaySeparationDurationValue ()
    {
        if (getDepartureRunwaySeparationDuration() != null)
            return CaptureUtils.durationToMillis(getDepartureRunwaySeparationDuration());
        return null;
    }
    
    public Long getArrivalRunwaySeparationDurationValue ()
    {
        if (getArrivalRunwaySeparationDuration() != null)
            return CaptureUtils.durationToMillis(getArrivalRunwaySeparationDuration());
        return null;
    }
    
    public Long getArrivalRunwayOccupancyDurationValue ()
    {
        if (getArrivalRunwayOccupancyDuration() != null)
            return CaptureUtils.durationToMillis(getArrivalRunwayOccupancyDuration());
        return null;
    }
    
    public Long getArrivalMovementAreaStopDurationValue ()
    {
        if (getArrivalMovementAreaStopDuration() != null)
            return CaptureUtils.durationToMillis(getArrivalMovementAreaStopDuration());
        return null;
    }
    
    public Long getArrivalRunwayCrossingsDurationValue ()
    {
        if (getArrivalRunwayCrossingsDuration() != null)
            return CaptureUtils.durationToMillis(getArrivalRunwayCrossingsDuration());
        return null;
    }
    
    public Long getArrivalRampAreaDurationValue ()
    {
        if (getArrivalRampAreaDuration() != null)
            return CaptureUtils.durationToMillis(getArrivalRampAreaDuration());
        return null;
    }
    
    public Long getArrivalRampAreaStopDurationValue ()
    {
        if (getArrivalRampAreaStopDuration() != null)
            return CaptureUtils.durationToMillis(getArrivalRampAreaStopDuration());
        return null;
    }
    
    public Long getArrivalTaxiUndelayedDurationValue ()
    {
        if (getArrivalTaxiUndelayedDuration() != null)
            return CaptureUtils.durationToMillis(getArrivalTaxiUndelayedDuration());
        return null;
    }
    
    public Long getArrivalTaxiEstimatedDurationValue ()
    {
        if (getArrivalTaxiEstimatedDuration() != null)
            return CaptureUtils.durationToMillis(getArrivalTaxiEstimatedDuration());
        return null;
    }
    
    public Long getArrivalTaxiActualDurationValue ()
    {
        if (getArrivalTaxiActualDuration() != null)
            return CaptureUtils.durationToMillis(getArrivalTaxiActualDuration());
        return null;
    }
    
    public Long getArrivalTaxiDerivedDurationValue ()
    {
        if (getArrivalTaxiDerivedDuration() != null)
            return CaptureUtils.durationToMillis(getArrivalTaxiDerivedDuration());
        return null;
    }
    
    public Long getArrivalMovementAreaDurationValue ()
    {
        if (getArrivalMovementAreaDuration() != null)
            return CaptureUtils.durationToMillis(getArrivalMovementAreaDuration());
        return null;
    }
    
    public Date getDepartureStandControllerDerivedActualTimeValue()
    {
        if (getDepartureStandControllerDerivedActualTime() != null)
        {
            return getDepartureStandControllerDerivedActualTime().getValue();
        }
        return null;
    }
    
    public Date getDepartureStandPositionDerivedActualTimeValue()
    {
        if (getDepartureStandPositionDerivedActualTime() != null)
        {
            return getDepartureStandPositionDerivedActualTime().getValue();
        }
        return null;
    }
    
    public Long getArrivalMovementAreaEstimatedDurationValue()
    {
        if( getArrivalMovementAreaEstimatedDuration() != null )
        {
            return CaptureUtils.durationToMillis( getArrivalMovementAreaEstimatedDuration() );
        }
        return null;
    }
    
    public Long getArrivalMovementAreaUndelayedDurationValue()
    {
        if( getArrivalMovementAreaUndelayedDuration() != null )
        {
            return CaptureUtils.durationToMillis( getArrivalMovementAreaUndelayedDuration() );
        }
        return null;
    }
    
    public Long getArrivalRampAreaEstimatedDurationValue()
    {
        if( getArrivalRampAreaEstimatedDuration() != null )
        {
            return CaptureUtils.durationToMillis( getArrivalRampAreaEstimatedDuration() );
        }
        return null;
    }
    
    public Long getArrivalRampAreaUndelayedDurationValue()
    {
        if( getArrivalRampAreaUndelayedDuration() != null )
        {
            return CaptureUtils.durationToMillis( getArrivalRampAreaUndelayedDuration() );
        }
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
