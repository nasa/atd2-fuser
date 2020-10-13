package com.mosaicatm.fuser.services.client;

import javax.jws.WebService;

@WebService
public interface FuserSyncService
extends FuserFlightSyncService, FuserAircraftSyncService, FuserSectorAssignmentSyncService
{
	
}
