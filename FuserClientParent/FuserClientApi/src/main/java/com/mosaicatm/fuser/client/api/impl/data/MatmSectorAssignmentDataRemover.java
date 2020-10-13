package com.mosaicatm.fuser.client.api.impl.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class MatmSectorAssignmentDataRemover
extends AbstractDataRemover<MatmSectorAssignment>
{
    private final static long MIN_PROCESSING_TIME_MILLIS = 300000L;
    
    private final Log log = LogFactory.getLog(getClass());
    
    private final Map<String, Long> artccProcessedTime = new HashMap<>();
    
    public MatmSectorAssignmentDataRemover ()
    {
        super (null, null);
    }
    
    public MatmSectorAssignmentDataRemover (FuserClientStore<MatmSectorAssignment> store)
    {
        super (store, null);
    }
    
    public MatmSectorAssignmentDataRemover (FuserClientStore<MatmSectorAssignment> store, Clock clock)
    {
        super (store, clock);
    }
    
    @Override
    public boolean isExpired(MatmSectorAssignment sectorAssignment)
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
            (( currTimeMillis - timestamp.getTime() ) > MIN_PROCESSING_TIME_MILLIS ) &&
            (( lastArtccProcessedTime - timestamp.getTime() ) > MIN_PROCESSING_TIME_MILLIS ))
        {
            return( true );
        }

        return false;
    }
}
