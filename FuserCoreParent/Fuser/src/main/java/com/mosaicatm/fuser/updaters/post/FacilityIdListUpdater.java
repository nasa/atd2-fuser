package com.mosaicatm.fuser.updaters.post;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.common.FacilityIdListType;
import com.mosaicatm.matmdata.common.FacilityIdType;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 *    This updater is responsible for maintaining a list of FacilityIdType for each unique sourceFacility.
 */
public class FacilityIdListUpdater
extends AbstractUpdater <MatmFlight, MatmFlight>
{
    private final Log log = LogFactory.getLog(getClass());
            
    @Override
    public void update( MatmFlight update, MatmFlight target )
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error( "Cannot update facility ID list, null input." );
            return;
        }
        
        // Simple check to ignore most messages and skip processing
        if( possibleUpdate( update, target ))
        {
            updateFacilityIdList( update, target );
        }
    }
    
    private void updateFacilityIdList( MatmFlight update, MatmFlight target )
    {
        FacilityIdListType updateFacilityIdListType;
        List<FacilityIdType> updateFacilityIds;

        // Either get the current target facilityList or make a new empty one
        if(( target == null ) ||
                ( target.getFacilityIdList() == null ) || 
                ( target.getFacilityIdList().getFacilityIds() == null ) || 
                target.getFacilityIdList().getFacilityIds().isEmpty() )
        {
            updateFacilityIdListType = new FacilityIdListType();
            updateFacilityIds = new ArrayList<>();
            updateFacilityIdListType.setFacilityIds( updateFacilityIds );          
        }
        else
        {
            updateFacilityIdListType = (FacilityIdListType) target.getFacilityIdList().clone(); 
            updateFacilityIds = updateFacilityIdListType.getFacilityIds();
        }
        
        String currentFacility = update.getSourceFacility();      
        if( currentFacility == null )
        {
            return;
        }

        FacilityIdType existingFacilityId = null;
        if( !updateFacilityIds.isEmpty() )
        {
            for( FacilityIdType facilityId : updateFacilityIds )
            {
                if( facilityId.getSourceFacility().equals( currentFacility ))
                {
                    existingFacilityId = facilityId; 
                    break;
                }
            }
        }
            
        // Only update with data in this update
        FacilityIdType newFacilityId = getFacilityId( update );

        // Only set the MatmFlight update facilityIdList if something changed
        if( dataChanged( newFacilityId, existingFacilityId ))
        {
            if( existingFacilityId == null )
            {
                updateFacilityIds.add( newFacilityId );
            }
            else
            {
                getMergedFacilityId( newFacilityId, existingFacilityId ).copyTo( existingFacilityId );
            }
            
            if( log.isDebugEnabled() )
            {
                log.debug( "Updating FacilityIdList : " + updateFacilityIds );
            }
            
            update.setFacilityIdList( updateFacilityIdListType );
        }        
    }
    
    private boolean possibleUpdate( MatmFlight update, MatmFlight target )
    {
        // We really need a source facilityId included in the update message to update to list
        if( update.getSourceFacility() == null )
        {
            return( false );
        }   

        // Need the update to affect at least one data element being tracked
        if(( update.getComputerId() == null ) && 
                ( update.getBeaconCode() == null ) && 
                ( update.getBeaconCode() == null ) && 
                ( update.getEramGufi() == null ))
        {
            return( false );
        }
        
        if( target == null )
        {
            return( true );
        }
                
        if( !Objects.equals( update.getSourceFacility(), target.getSourceFacility() ))
        {
            return( true );
        }
        
        if(( update.getComputerId() != null ) && 
                ( !Objects.equals( update.getComputerId(), target.getComputerId() )))
        {
            return( true );
        }
        
        if(( update.getBeaconCode() != null ) && 
                ( !Objects.equals( update.getBeaconCode(), target.getBeaconCode() )))
        {
            return( true );
        }        
        
        if(( update.getEramGufi() != null ) && 
                ( !Objects.equals( update.getEramGufi(), target.getEramGufi() )))
        {
            return( true );
        }        
        
        return( false );
    }
    
    private boolean dataChanged( FacilityIdType update, FacilityIdType target )
    {
        // Need the update to affect at least one data element being tracked
        if(( update.getSourceFacility() != null ) && (( target == null ) ||
                ( !Objects.equals( update.getSourceFacility(), target.getSourceFacility() ))))
        {
            return( true );
        }
        
        if(( update.getComputerId() != null ) && (( target == null ) ||
                ( !Objects.equals( update.getComputerId(), target.getComputerId() ))))
        {
            return( true );
        }
        
        if(( update.getBeaconCode() != null ) && (( target == null ) ||
                ( !Objects.equals( update.getBeaconCode(), target.getBeaconCode() ))))
        {
            return( true );
        }  
        
        if(( update.getEramGufi() != null ) && (( target == null ) ||
                ( !Objects.equals( update.getEramGufi(), target.getEramGufi() ))))
        {
            return( true );
        }         
        
        return( false );
    }    
    
    private FacilityIdType getMergedFacilityId( FacilityIdType update, FacilityIdType target )
    {
        FacilityIdType facilityId = new FacilityIdType();
        
        facilityId.mergeFrom( update, target );  
        
        return( facilityId );
    }    
    
    private FacilityIdType getFacilityId( MatmFlight flight )
    {
        FacilityIdType facilityId = new FacilityIdType();
        
        facilityId.setBeaconCode( flight.getBeaconCode() );
        facilityId.setComputerId( flight.getComputerId() );        
        facilityId.setEramGufi( flight.getEramGufi() );
        facilityId.setSourceFacility( flight.getSourceFacility() );
        facilityId.setSystemId( flight.getSystemId() );
        facilityId.setTimestamp( flight.getTimestamp() );
        
        return( facilityId );
    }
}
