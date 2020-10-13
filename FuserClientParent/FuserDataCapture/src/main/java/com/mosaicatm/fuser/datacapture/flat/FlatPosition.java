package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.matmdata.common.FavType;
import com.mosaicatm.matmdata.position.MatmPositionUpdate;

public class FlatPosition
extends MatmPositionUpdate
implements GufiRecord
{

	private static final long serialVersionUID = -8465195980695424810L;

    private String recordIdentifier;
    private Date recordTimestamp;
    private String surfaceAirportName;
    
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
    
	public Double getPositionAltitude ()
    {
        if (getPosition() != null)
            return getPosition().getAltitude();
        return null;
    }
    
    public Double getPositionLatitude ()
    {
        if (getPosition() != null)
            return getPosition().getLatitude();
        return null;
    }
    
    public Double getPositionLongitude ()
    {
        if (getPosition() != null)
            return getPosition().getLongitude();
        return null;
    }
    
    public String getPositionAtcSector ()
    {
        if ((getPosition() != null) && (getPosition().getAtcSector() != null))
            return getPosition().getAtcSector().getValue();
        return null;
    }      
    
    public String getPositionStaticSector ()
    {
        if ((getPosition() != null) && (getPosition().getStaticSector() != null))
            return getPosition().getStaticSector().getValue();
        return null;
    }   
    
    public String getPositionDynamicSector ()
    {
        if ((getPosition() != null) && (getPosition().getDynamicSector() != null))
            return getPosition().getDynamicSector().getValue();
        return null;
    }       

    public String getPositionFav ()
    {
        if ((getPosition() != null) && (getPosition().getFav() != null))
            return getPosition().getFav().getValue();
        return null;
    }   
    
    public FavType getPositionFavType ()
    {
        if ((getPosition() != null) && (getPosition().getFavType() != null))
            return getPosition().getFavType().getValue();
        return null;
    } 
    
    public String getPositionFavModule ()
    {
        if ((getPosition() != null) && (getPosition().getFavModule() != null))
            return getPosition().getFavModule().getValue();
        return null;
    }       
    
    public Double getPositionSpeed ()
    {
        if (getPosition() != null)
            return getPosition().getSpeed();
        return null;
    }

    public Double getPositionHeading ()
    {
        if (getPosition() != null)
            return getPosition().getHeading();
        return null;
    }    

    public Double getPositionDistanceTotal ()
    {
        if (getPosition() != null)
            return getPosition().getDistanceTotal();
        return null;
    }  
    
    public Date getPositionTimestamp ()
    {
    	if (getPosition() != null)
            return getPosition().getTimestamp();
        return null;
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
