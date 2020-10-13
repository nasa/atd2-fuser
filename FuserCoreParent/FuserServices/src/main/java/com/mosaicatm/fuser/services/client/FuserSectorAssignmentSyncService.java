package com.mosaicatm.fuser.services.client;

import java.util.List;

import javax.activation.DataHandler;

import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public interface FuserSectorAssignmentSyncService
{
    public List<MatmSectorAssignment> getSectorAssignments ();
    public DataHandler getSectorAssignmentsInAttachment();
}
