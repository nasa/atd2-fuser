package com.mosaicatm.fuser.store.matm;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.store.SectorAssignmentDataStore;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

/**
 * The <code>MatmSectorAssignmentFuserStoreCache</code> provides an in-memory cache in front
 * of a FuserStore delegate (acts like a proxy).  The goal is to reduce remote
 * calls to get information.
 */
public class MatmSectorAssignmentFuserStoreCache 
extends AbstractFuserStoreTimedBackup <MatmSectorAssignment> implements SectorAssignmentDataStore
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public MatmSectorAssignmentFuserStoreCache(SectorAssignmentDataStore delegate,
                                       SectorAssignmentDataStore cache)
    {        
        super(delegate, cache);
        ((MatmSectorAssignmentFuserStore)getCache()).setSendEvents(true);
    }

    @Override
    public void initialize()
    {
        try
        {
            logger.info( "Initializing MatmSectorAssignmentStoreCache..." );
            
            lockEntireStore();
            
            getCache().clear();
            
            List<MetaData> invalid = null;
            
            Collection<MatmSectorAssignment> sectorAssignments = getDelegate().getAll();

            if( sectorAssignments != null )
            {
                List<MatmSectorAssignment> sectorAssignmentsList = new ArrayList<>( sectorAssignments );

                // Sort the results by timestamp to ensure they are handled in the correct order
                Comparator<MatmSectorAssignment> compareByTimestamp = 
                        (MatmSectorAssignment o1, MatmSectorAssignment o2) -> o1.getTimestamp().compareTo( o2.getTimestamp() );

                Collections.sort( sectorAssignmentsList, compareByTimestamp );    

                for( MatmSectorAssignment sectorAssignment : sectorAssignmentsList )
                {
                    // clears out
                    if (sectorAssignment.getUpdateSources() != null && !sectorAssignment.getUpdateSources().isEmpty())
                    {
                        invalid = new ArrayList<>();
                        for (MetaData updateSource : sectorAssignment.getUpdateSources())
                        {
                            if (updateSource == null ||
                                (updateSource.getFieldName() == null &&
                                 updateSource.getSource() == null &&
                                 updateSource.getSystemType() == null &&
                                 updateSource.getTimestamp() == null))
                            {
                                logger.warn(sectorAssignment.getSectorName() + " tried restoring a nill update source, " + 
                                            "removing nil from list");
                                invalid.add(updateSource);
                            }
                        }
                        sectorAssignment.getUpdateSources().removeAll(invalid);
                    }

                    getCache().add( sectorAssignment );
                }
            }                                    
            
            logger.info( "  Sector Assignments: " +  size() );
        }
        finally
        {
            unlockEntireStore();
            logger.info( "... MatmSectorAssignmentFuserStoreCache Initialization Complete!" );
        }          
    }    
    
    /**
     * Checks if dynamic sectorization assignments exists for an ARTCC.
     * @param artcc the ARTCC code
     * @return true if dynamic sectorization data exists
     */    
    @Override
    public boolean dynamicSectorAssignmentsExist( String artcc )
    {
        return(((SectorAssignmentDataStore)getCache()).dynamicSectorAssignmentsExist( artcc ));
    }            
    
    /**
     * Get the dynamic sector name for a given sector module name
     * @param moduleName the sector module name (e.g. ZOB4801M1)
     * @return the dynamic sector name
     */    
    @Override
    public String getDynamicSectorForModule( String moduleName )
    {
        return(((SectorAssignmentDataStore)getCache()).getDynamicSectorForModule( moduleName ));
    }

    /**
     * Update the current FAVs mapped to a dynamic sector name, setting the FAV list to
     * null or empty to deactivate a sector.
     * @param sectorAssignment the sector assignment
     */   
    @Override
    public void updateDynamicSectorFixedAirspaceVolumes( MatmSectorAssignment sectorAssignment )
    {
        ((SectorAssignmentDataStore)getCache()).updateDynamicSectorFixedAirspaceVolumes( sectorAssignment );
        ((SectorAssignmentDataStore)getDelegate()).updateDynamicSectorFixedAirspaceVolumes( sectorAssignment );
    }    

    @Override
    public String getKey(MatmSectorAssignment sectorAssignment)
    {
        if (getDelegate() != null)
            return getDelegate().getKey(sectorAssignment);
        return null;
    }
}
