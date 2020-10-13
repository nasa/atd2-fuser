package com.mosaicatm.fuser.service;

import java.util.List;

import javax.activation.DataHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.services.client.FuserAircraftSyncService;
import com.mosaicatm.fuser.services.client.FuserFlightSyncService;
import com.mosaicatm.fuser.services.client.FuserSectorAssignmentSyncService;
import com.mosaicatm.fuser.services.client.FuserSyncRequest;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.aircraftcomposite.MatmAircraftComposite;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flightcomposite.MatmFlightComposite;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class FuserSyncService
implements com.mosaicatm.fuser.services.client.FuserSyncService
{
	private final Log log = LogFactory.getLog(getClass());
	
	private FuserAircraftSyncService aircraftService;
	private FuserFlightSyncService flightService;
    private FuserSectorAssignmentSyncService sectorAssignmentService;
	
	public FuserSyncService ()
	{
		super ();
	}

	@Override
	public List<MatmFlight> getFlights()
	{
	    if (flightService != null)
	        return flightService.getFlights();
	    return null;
	}

	@Override
	public DataHandler getFlightsInAttachment()
	{
	    if (flightService != null)
	        return flightService.getFlightsInAttachment();
	    return null;
	}

    @Override
    public List<MatmFlight> getFlightsRequest( FuserSyncRequest fuserSyncRequest )
    {
        if( flightService != null )
        {
            return flightService.getFlightsRequest( fuserSyncRequest );
        }
        return null;
    }

    @Override
    public DataHandler getFlightsInAttachmentRequest( FuserSyncRequest fuserSyncRequest )
    {
        if( flightService != null )
        {
            return flightService.getFlightsInAttachmentRequest( fuserSyncRequest );
        }
        return null;
    }
	
	@Override
	public List<MatmFlight> getFlightsByAirport(String airport)
	{
		if (flightService != null)
		    return flightService.getFlightsByAirport(airport);
		return null;
	}

	@Override
	public List<MatmFlightComposite> getCompositeFlights()
	{	    
	    if (flightService != null)
	        return flightService.getCompositeFlights();
	    return null;
	}
	
    @Override
    public List<MatmFlightComposite> getCompositeFlightsRequest( FuserSyncRequest fuserSyncRequest )
    {
        if( flightService != null )
        {
            return flightService.getCompositeFlightsRequest( fuserSyncRequest );
        }
        return null;
    }
	
	@Override
	public List<MatmFlightComposite> getCompositeFlightsByAirport(String airport)
	{	    
	    if (flightService != null)
	        return flightService.getCompositeFlightsByAirport(airport);
	    return null;
	}

	@Override
	public int getFlightCount ()
	{
		if (flightService != null)
		    return flightService.getFlightCount();
		return 0;
	}
	
    @Override
    public int getFlightCountRequest( FuserSyncRequest fuserSyncRequest )
    {
        if( flightService != null )
        {
            return flightService.getFlightCountRequest( fuserSyncRequest );
        }
        return 0;
    }
	
	@Override
	public int getFlightCountByAirport (String airport)
	{
		if (flightService != null)
		    return flightService.getFlightCountByAirport(airport);
		return 0;
	}

    @Override
    public List<MatmAircraft> getAircraft()
    {
        if (aircraftService != null)
            return aircraftService.getAircraft();
        return null;
    }

    @Override
    public DataHandler getAircraftInAttachment()
    {
        if (aircraftService != null)
            return aircraftService.getAircraftInAttachment();
        return null;
    }

    @Override
    public List<MatmAircraftComposite> getCompositeAircraft()
    {
        if (aircraftService != null)
            return aircraftService.getCompositeAircraft();
        return null;
    }

    @Override
    public int getAircraftCount()
    {
        if (aircraftService != null)
            return aircraftService.getAircraftCount();
        return 0;
    }
    

    @Override
    public List<MatmSectorAssignment> getSectorAssignments()
    {
        if (sectorAssignmentService != null)
            return sectorAssignmentService.getSectorAssignments();
        return null;
    }

    @Override
    public DataHandler getSectorAssignmentsInAttachment()
    {
        if (sectorAssignmentService != null)
            return sectorAssignmentService.getSectorAssignmentsInAttachment();
        return null;
    }    

    public void setAircraftSyncService(FuserAircraftSyncService aircraftService)
    {
        this.aircraftService = aircraftService;
    }
    
    public void setFlightSyncService(FuserFlightSyncService flightService)
    {
        this.flightService = flightService;
    }
    
    public void setSectorAssignmentSyncService(FuserSectorAssignmentSyncService sectorAssignmentService)
    {
        this.sectorAssignmentService = sectorAssignmentService;
    }    
}
