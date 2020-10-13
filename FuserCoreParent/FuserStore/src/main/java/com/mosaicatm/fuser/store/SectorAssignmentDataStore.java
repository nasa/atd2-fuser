package com.mosaicatm.fuser.store;

import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public interface SectorAssignmentDataStore extends FuserStore<MatmSectorAssignment, MetaData>
{
    /**
     * Checks if dynamic sectorization assignments exists for an ARTCC.
     * @param artcc the ARTCC code
     * @return true if dynamic sectorization data exists
     */    
    public boolean dynamicSectorAssignmentsExist( String artcc );
            
    /**
     * Get the dynamic sector name for a given sector module name
     * @param moduleName the sector module name (e.g. ZOB4801M1)
     * @return the dynamic sector name
     */    
    public String getDynamicSectorForModule( String moduleName );

    /**
     * Update the current FAVs mapped to a dynamic sector name, setting the FAV list to
     * null or empty to deactivate a sector.
     * @param sectorAssignment the sector assignment
     */   
    public void updateDynamicSectorFixedAirspaceVolumes( MatmSectorAssignment sectorAssignment );
}
