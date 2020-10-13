package com.mosaicatm.fuser.services.client;

import java.util.List;

import javax.activation.DataHandler;
import javax.jws.WebService;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.aircraftcomposite.MatmAircraftComposite;

@WebService
public interface FuserAircraftSyncService
{
    public List<MatmAircraft> getAircraft ();
    public DataHandler getAircraftInAttachment();
    public List<MatmAircraftComposite> getCompositeAircraft();
    
    public int getAircraftCount();
}
