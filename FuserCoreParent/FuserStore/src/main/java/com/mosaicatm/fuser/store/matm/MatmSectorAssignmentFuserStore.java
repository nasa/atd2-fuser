package com.mosaicatm.fuser.store.matm;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.store.GenericFuserStore;
import com.mosaicatm.fuser.store.SectorAssignmentDataStore;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;
import com.mosaicatm.sector.geometry.utils.SectorUtils;

public class MatmSectorAssignmentFuserStore
extends GenericFuserStore <MatmSectorAssignment> implements SectorAssignmentDataStore
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );
    
    private final Map<String, String> favToDynamicSector = new ConcurrentHashMap<>();  
    private final Set<String> dynamicSectorArtccs = new HashSet<>();      
    
    public MatmSectorAssignmentFuserStore (int numberOfLocks)
    {
        super ("Fuser Sector Assignment Store", numberOfLocks);
    }
    
    @Override
    public void clear()
    {
        super.clear();

        favToDynamicSector.clear();
        dynamicSectorArtccs.clear();
    }    
    
    @Override
    public void add( MatmSectorAssignment sectorAssignment )
    {
        updateDynamicSectorFixedAirspaceVolumes( sectorAssignment );
    }

    @Override
    public void update( MatmSectorAssignment sectorAssignment )
    {
        updateDynamicSectorFixedAirspaceVolumes( sectorAssignment );
    }
    
    @Override
    public void updateMetaData(MatmSectorAssignment data, MetaData metaData)            
    {
    }
    
    @Override
    public void updateMetaData(MatmSectorAssignment data, Collection<MetaData> metaDataCollection)
    {
    }
    
    @Override
    public void removeByKey( String key )
    {
        MatmSectorAssignment oldAssignment = get( key );
        
        if( oldAssignment != null )
        {
            cleanFavToSectorMappings( oldAssignment.getSectorName() );
            
            super.removeByKey( getKey( oldAssignment ));
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
        return(( artcc != null ) && dynamicSectorArtccs.contains( artcc ));
    }   

    /**
     * Get the dynamic sector name for a given sector module name
     * @param moduleName the sector module name (e.g. ZOB4801M1)
     * @return the dynamic sector name
     */     
    @Override
    public String getDynamicSectorForModule( String moduleName )
    {
        String dynamicSector = null;
        
        if( moduleName != null )
        {
            String favName = SectorUtils.getFavName( moduleName );

            if( favName == null )
            {
                logger.warn( "Unable to determine FAV name for module name: {}", moduleName );
                return null;
            }            

            dynamicSector = favToDynamicSector.get( favName );
        }
        
        return dynamicSector;
    }
    
    @Override
    public void initialize ()
    {
        // in memory map, nothing to initialize
    }

    /**
     * Update the current FAVs mapped to a dynamic sector name, setting the FAV list to
     * null or empty to deactivate a sector.
     * @param sectorAssignment the sector assignment
     */   
    @Override
    public void updateDynamicSectorFixedAirspaceVolumes( MatmSectorAssignment sectorAssignment )
    {           
        if(( sectorAssignment == null ) || ( sectorAssignment.getSectorName() == null ))
        {
            return;
        }        
        
        if( sectorAssignment.getSectorName().length() < 5 )
        {
            logger.warn( "Cannot update dynamic sector FAVs: illegal sector name {}", sectorAssignment.getSectorName() );
            return;
        }        
               
        String artcc = sectorAssignment.getSourceFacility();
        if( artcc == null ) 
        {
            artcc = SectorUtils.getArtcc( sectorAssignment.getSectorName() );
            
            if( artcc == null )
            {
                logger.warn( "Cannot update dynamic sector FAVs: cannot determine ARTCC for {}", sectorAssignment.getSectorName() );
                return;
            }               
        }        
        
        List<String> fixedAirspaceVolumeList = sectorAssignment.getFixedAirspaceVolumeList();

        boolean deactivate = ( fixedAirspaceVolumeList == null ) || fixedAirspaceVolumeList.isEmpty();
        boolean newSector = ( get( sectorAssignment.getSectorName() ) == null );

        // Clean up old mappings to this sector
        cleanFavToSectorMappings( sectorAssignment.getSectorName() );

        if( !deactivate )
        {
            dynamicSectorArtccs.add( artcc );

            for( String favName : fixedAirspaceVolumeList )
            {
                if(( favName == null ) || !favName.startsWith( artcc ))
                {
                    logger.warn( "Bad FAV name found for sector={}, FAV={}", sectorAssignment.getSectorName(), favName );
                    continue;
                }

                // This logic updated the stored FAV list without waiting for a 
                // deactivated message, but the timed remover is safer for removing old assignments
                //String closedSector = cleanOldAssignmentFavList( favName, sectorAssignment );

                favToDynamicSector.put( favName, sectorAssignment.getSectorName() );                
            }  
        }

        if( deactivate )
        {
            super.remove( sectorAssignment );
        }
        else if( newSector )
        {
            super.add( sectorAssignment );
        }
        else
        {
            super.update( sectorAssignment );
        }                   
    }
    
    private void cleanFavToSectorMappings( String sectorName )
    {
        MatmSectorAssignment oldAssignment = get( sectorName );
        
        if(( oldAssignment != null ) && 
                ( oldAssignment.getFixedAirspaceVolumeList() != null ) && 
                !oldAssignment.getFixedAirspaceVolumeList().isEmpty() )
        {
            for( String favName : oldAssignment.getFixedAirspaceVolumeList() )
            {                
                // Remove mapping if it hasn't already been updated to the new sector
                String currentSectorMapping = favToDynamicSector.get( favName );
                if( sectorName.equals( currentSectorMapping ))
                {
                    favToDynamicSector.remove( favName );
                }   
            }
        }          
    }    
    
    private String cleanOldAssignmentFavList( String favName, MatmSectorAssignment newSectorAssignment )
    {
        // Clean up any different old sector mapped to this FAV
        String oldSector = favToDynamicSector.get( favName );
        
        if(( oldSector != null ) && !oldSector.equals( newSectorAssignment.getSectorName() ))
        {
            MatmSectorAssignment oldAssignment = get( oldSector );
            if(( oldAssignment != null ) && ( oldAssignment.getFixedAirspaceVolumeList() != null ))
            {
                if(( oldAssignment.getTimestamp() != null ) && 
                        ( newSectorAssignment.getTimestamp() != null ) &&
                          oldAssignment.getTimestamp().after( newSectorAssignment.getTimestamp() ))
                {
                    logger.error( "Found newer sector assignment than update to FAV {}: update={} newer={}", 
                            favName, newSectorAssignment.getSectorName(), oldSector );
                    return( null );
                }

                logger.debug( "Removing old FAV {} mapping: from {}, replaced with {}", 
                        favName, oldSector, newSectorAssignment.getSectorName() );

                oldAssignment.getFixedAirspaceVolumeList().remove( favName );

                if( oldAssignment.getFixedAirspaceVolumeList().isEmpty() )
                {
                    logger.debug( "Detected closed sector {}", oldSector );                               
                    return( oldSector );
                }
            }
        }   

        return( null );
    }            
}
