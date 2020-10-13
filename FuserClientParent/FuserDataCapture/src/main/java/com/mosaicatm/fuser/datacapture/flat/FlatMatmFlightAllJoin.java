package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.fuser.datacapture.store.IdSet;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class FlatMatmFlightAllJoin
implements GufiRecord
{
    private final MatmFlight flight;
    private final IdSet idSet;
    private Date recordTimestamp;
    public FlatMatmFlightAllJoin(MatmFlight flight, IdSet idSet)
    {
        this.flight = flight;
        this.idSet = idSet;
    }
    
    @Override
    public void setRecordIdentifier(String recordIdentifier)
    {
        // Not in used
    }

    @Override
    public String getRecordIdentifier()
    {
        if (idSet != null)
            return idSet.getRecordId();
        
        return null;
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
        // Not in used
    }

    @Override
    public Date getTimestamp()
    {
        if (flight != null)
        {
            return flight.getTimestamp();
        }
        
        return null;
    }

    @Override
    public void setGufi(String gufi)
    {
        // Not in used
    }

    @Override
    public String getGufi()
    {
        if (flight != null)
        {
            return flight.getGufi();
        }
        
        return null;
    }
    
    public String getAcid()
    {
        if (flight != null)
        {
            return flight.getAcid();
        }
        
        return null;
    }

    public String getArrivalAerodromeIcaoName()
    {
        if (flight != null && flight.getArrivalAerodrome() != null)
        {
            return flight.getArrivalAerodrome().getIcaoName();
        }
        
        return null;
    }
    
    public String getDepartureAerodromeIcaoName()
    {
        if (flight != null && flight.getDepartureAerodrome() != null)
        {
            return flight.getDepartureAerodrome().getIcaoName();
        }
        
        return null;
    }
    
    public String getArrivalAerodromeIataName()
    {
        if (flight != null && flight.getArrivalAerodrome() != null)
        {
            return flight.getArrivalAerodrome().getIataName();
        }
        
        return null;
    }
    
    public String getDepartureAerodromeIataName()
    {
        if (flight != null && flight.getDepartureAerodrome() != null)
        {
            return flight.getDepartureAerodrome().getIataName();
        }
        
        return null;
    }
    
    public String getLastUpdateSource()
    {
        if (flight != null)
        {
            return flight.getLastUpdateSource();
        }
        
        return null;
    }
    
    public String getSystemId()
    {
        if (flight != null)
        {
            return flight.getSystemId();
        }
        
        return null;
    }
    
    public Date getTimestampFuserProcessed()
    {
    	if (flight != null)
        {
            return flight.getTimestampFuserProcessed();
        }
        
        return null;
    }
    
    public Date getTimestampFuserReceived()
    {
    	if (flight != null)
        {
            return flight.getTimestampFuserReceived();
        }
        
        return null;
    }
    
    public Date getTimestampSource()
    {
    	if (flight != null)
        {
            return flight.getTimestampSource();
        }
        
        return null;
    }
    
    public Date getTimestampSourceProcessed()
    {
    	if (flight != null)
        {
            return flight.getTimestampSourceProcessed();
        }
        
        return null;
    }
    
    public Date getTimestampSourceReceived()
    {
    	if (flight != null)
        {
            return flight.getTimestampSourceReceived();
        }
        
        return null;
    }
    
    public String getMatmFlightAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getMatmId();
        }
        
        return null;
    }
    
    public String getPositionRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getPositionId();
        }
        
        return null;
    }
    
    public String getAefsExtAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getAefsExtensionId();
        }
        
        return null;
    }
    
    public String getAirlineExtAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getAirlineExtensionId();
        }
        
        return null;
    }
    
    public String getAsdexExtAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getAsdexExtensionId();
        }
        
        return null;
    }
    
    public String getDerivedExtAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getDerivedExtensionId();
        }
        
        return null;
    }
    
    public String getIdacExtAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getIdacExtensionId();
        }
        
        return null;
    }
    
    public String getTbfmExtAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getTbfmExtensionId();
        }
        
        return null;
    }
    
    public String getTfmExtAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getTfmExtensionId();
        }
        
        return null;
    }
    
    public String getTfmTfdmExtAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getTfmTfdmExtensionId();
        }
        
        return null;
    }
    
    public String getSurfaceModelExtAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getSurfaceModelExtensionId();
        }
        
        return null;
    }
    
    public String getCat11ExtAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getCat11ExtensionId();
        }
        
        return null;
    }
    
    public String getCat62ExtAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getCat62ExtensionId();
        }
        
        return null;
    }
    
    public String getAdsbExtAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getAdsbExtensionId();
        }
        
        return null;
    }

    public String getSfdpsExtAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getSfdpsExtensionId();
        }

        return null;
    }
    
    public String getSmesExtAllRecordIdentifier()
    {
        if (idSet != null)
        {
            return idSet.getSmesExtensionId();
        }
        
        return null;
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
