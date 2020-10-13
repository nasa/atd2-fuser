package com.mosaicatm.fuser.sector;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.camel.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.store.SectorAssignmentDataStore;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class SectorAssignmentUpdater
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, Date> processedFacilities = new HashMap<>();
    
    private SectorAssignmentDataStore sectorAssignmentDataStore;

    @Handler
    public void process( MatmSectorAssignment sectorAssignmentMessage )
    {
        if( sectorAssignmentMessage == null )
        {
            log.error("Cannot process sector assignment message: message is NULL!");
            return;
        }

        if( sectorAssignmentDataStore == null )
        {
            log.error("Cannot process sector assignment message: sectorizationDataStore is NULL!");
            return;
        }

        //Typically, all assignments for a facility arrive at the same time
        if( !Objects.equals( processedFacilities.get( sectorAssignmentMessage.getSourceFacility() ), 
                sectorAssignmentMessage.getTimestamp() ))
        {
            log.info( "Processing sector assignments for {}", sectorAssignmentMessage.getSourceFacility() );
            processedFacilities.put( sectorAssignmentMessage.getSourceFacility(), sectorAssignmentMessage.getTimestamp() );
        }

        try
        {
            sectorAssignmentDataStore.lockStore( sectorAssignmentMessage );
            
            sectorAssignmentDataStore.updateDynamicSectorFixedAirspaceVolumes( sectorAssignmentMessage );
        }
        finally
        {
            sectorAssignmentDataStore.unlockStore( sectorAssignmentMessage );
        }
    }

    public void setSectorAssignmentDataStore( SectorAssignmentDataStore sectorAssignmentDataStore )
    {
        this.sectorAssignmentDataStore = sectorAssignmentDataStore;
    }
}
