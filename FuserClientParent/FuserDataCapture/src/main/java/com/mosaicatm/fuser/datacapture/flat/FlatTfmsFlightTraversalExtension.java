package com.mosaicatm.fuser.datacapture.flat;

import com.mosaicatm.fuser.datacapture.util.CaptureUtils;
import com.mosaicatm.matmdata.flight.extension.TfmsFlightTraversalExtension;

import java.util.Date;

public class FlatTfmsFlightTraversalExtension 
extends TfmsFlightTraversalExtension
implements GufiRecord
{
	private static final long serialVersionUID = 8659201844421669389L;

	private String gufi;
	private String recordIdentifier;
	private Date recordTimestamp;
	private Date timestamp;
	private String surfaceAirportName;

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
	public void setRecordIdentifier(String recordIdentifier)
	{
		this.recordIdentifier = recordIdentifier;
	}

	@Override
	public String getRecordIdentifier()
	{
		return recordIdentifier;
	}

	@Override
	public void setRecordTimestamp(Date recordTimestamp)
	{
		this.recordTimestamp = recordTimestamp;
	}

	@Override
	public Date getRecordTimestamp()
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

	@Override
	public void setSurfaceAirportName(String surfaceAirportName)
	{
		this.surfaceAirportName = surfaceAirportName;
	}

	@Override
	public String getSurfaceAirportName()
	{
		return surfaceAirportName;
	}

	public String getCenterTraversal()
	{
		return CaptureUtils.toBulkCopyJson(getCenters());
	}

	public String getFixTraversal()
	{
		return CaptureUtils.toBulkCopyJson(getFixes());
	}

	public String getSectorTraversal()
	{
		return CaptureUtils.toBulkCopyJson(getSectors());
	}

	public String getWaypointTraversal()
	{
		return CaptureUtils.toBulkCopyJson(getWaypoints());
	}
}