package com.mosaicatm.fuser.services.client;

import java.util.List;

import javax.activation.DataHandler;

import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flightcomposite.MatmFlightComposite;

public interface FuserFlightSyncService
{
    //maybe we should return a MatmTransferEnvelope
    public List<MatmFlight> getFlights ();
    public DataHandler getFlightsInAttachment ();
    public List<MatmFlight> getFlightsRequest( FuserSyncRequest fuserSyncRequest );
    public DataHandler getFlightsInAttachmentRequest( FuserSyncRequest fuserSyncRequest );
    public List<MatmFlight> getFlightsByAirport (String airport);
    
    public List<MatmFlightComposite> getCompositeFlights ();
    public List<MatmFlightComposite> getCompositeFlightsRequest( FuserSyncRequest fuserSyncRequest );
    public List<MatmFlightComposite> getCompositeFlightsByAirport (String airport);
    
    public int getFlightCount ();
    public int getFlightCountRequest( FuserSyncRequest fuserSyncRequest );
    public int getFlightCountByAirport (String airport);
}
