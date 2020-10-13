package com.mosaicatm.fuser.client.api.impl.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class FuserClientMatmSectorAssignmentStore 
extends AbstractFuserClientStore <MatmSectorAssignment>
{
	private final Log log = LogFactory.getLog(getClass());
	
	public FuserClientMatmSectorAssignmentStore ()
	{
		super();
	}
	
	@Override
	public String getKey (MatmSectorAssignment sectorAssignment)
	{
		if (sectorAssignment != null)
			return sectorAssignment.getSectorName();
		return null;
	}
}
