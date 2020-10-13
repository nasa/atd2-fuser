package com.mosaicatm.fuser.store.matm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class MatmSectorAssignmentFuserStoreTimedRemover
extends AbstractFuserStoreTimedRemover<MatmSectorAssignment>
{    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final Map<String, Long> artccProcessedTime = new HashMap<>();
    
    private long expirationTime = TimeFactory.MINUTE_IN_MILLIS * 5;
    
    public MatmSectorAssignmentFuserStoreTimedRemover (FuserStore<MatmSectorAssignment, MetaData> store)
    {
        super("MatmSectorAssignment", store);
    }    

    @Override
    public boolean isExpired (MatmSectorAssignment sectorAssignment)
    {
        if( sectorAssignment == null )
        {
            return( false );
        }
        
        Date timestamp = sectorAssignment.getTimestamp();
        long currTimeMillis = getClock().getTimeInMillis();
        
        if( timestamp == null )
        {
            log.error( "Cannot compute expired: Sector Assignment timestamp is NULL!" );
            return( false );
        }
        
        if( sectorAssignment.getSourceFacility() == null )
        {
            log.error( "Cannot compute expired: Sector Assignment source facility is NULL!" );
            return( false );
        }                
        
        Long lastArtccProcessedTime = artccProcessedTime.get( sectorAssignment.getSourceFacility() );
        
        if(( lastArtccProcessedTime == null ) || 
                ( lastArtccProcessedTime < timestamp.getTime() ))
        {
            lastArtccProcessedTime = timestamp.getTime();
            artccProcessedTime.put( sectorAssignment.getSourceFacility(), lastArtccProcessedTime );
        }
        
        // All sector assignments for an ARTCC arrive at roughly the same time, an assignment
        // would be stale if it's older than the most recent timestamp for the same ARTCC
        if( // Allow some time for all the assigments in a batch to trickle in
            (( currTimeMillis - timestamp.getTime() ) > expirationTime ) &&
            (( lastArtccProcessedTime - timestamp.getTime() ) > expirationTime ))
        {
            return( true );
        }

        return false;
    }

    public void setExpirationTimeMillis (long expirationTime)
    {
        this.expirationTime = expirationTime;
    }
}
