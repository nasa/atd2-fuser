package com.mosaicatm.fuser.client.api;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public interface FuserClientApiLoader
{
	public void load ();
		
	public void setApiConfiguration(FuserClientApiConfiguration config);
	
	public void setPropertyOverrideFile (String file);
	
	public FuserClientApi<MatmFlight> getApi ();
	public FuserClientApi<MatmAircraft> getAircraftApi();
    public FuserClientApi<MatmSectorAssignment> getSectorAssignmentApi();
	
	public void setLoadSecureSync( boolean loadSecureSync );
}
