package com.mosaicatm.fuser.store;

import java.util.List;

import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class SectorAssignmentStoreProxy extends FuserStoreProxy<MatmSectorAssignment, MetaData>
implements SectorAssignmentDataStore
{
    private SectorAssignmentDataStore proxy;
    
    public SectorAssignmentStoreProxy ()
    {
        super();
    }
    
    public SectorAssignmentStoreProxy (SectorAssignmentDataStore proxy)
    {
        super( proxy );
        
        this.proxy = proxy;
    }
    
    public void setProxy (SectorAssignmentDataStore proxy)
    {
        super.setProxy( proxy );
        
        this.proxy = proxy;
    }    

    /**
     * Checks if dynamic sectorization assignments exists for an ARTCC.
     * @param artcc the ARTCC code
     * @return true if dynamic sectorization data exists
     */    
    @Override
    public boolean dynamicSectorAssignmentsExist( String artcc )
    {
        if (proxy != null)
            return( proxy.dynamicSectorAssignmentsExist( artcc ));
        else
            return( false );        
    }
    
    /**
     * Get the dynamic sector name for a given sector module name
     * @param moduleName the sector module name (e.g. ZOB4801M1)
     * @return the dynamic sector name
     */    
    @Override
    public String getDynamicSectorForModule( String moduleName )
    {
        if (proxy != null)
            return( proxy.getDynamicSectorForModule( moduleName ));
        else
            return( null );          
    }

    /**
     * Update the current FAVs mapped to a dynamic sector name, setting the FAV list to
     * null or empty to deactivate a sector.
     * @param sectorAssignment the sector assignment
     */   
    @Override
    public void updateDynamicSectorFixedAirspaceVolumes( MatmSectorAssignment sectorAssignment )
    {
        if (proxy != null)
            proxy.updateDynamicSectorFixedAirspaceVolumes( sectorAssignment );       
    }
}
