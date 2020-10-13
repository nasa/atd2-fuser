package com.mosaicatm.fuser.store.matm.xfer;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.common.matm.xfer.FuserRepositoryTransfer;
import com.mosaicatm.fuser.common.matm.xfer.FuserRepositoryTransferFileIO;
import com.mosaicatm.fuser.common.matm.xfer.GufiMetaData;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class FuserStoreFileIO
{
    private final static Log LOG = LogFactory.getLog( FuserStoreFileIO.class );

    public void loadFuserStoreFromXmlCache( FuserStore <MatmFlight, MetaData> matmFuserStore, 
            FuserStore <MatmAircraft, MetaData> matmAircraftFuserStore, 
            FuserStore <MatmSectorAssignment, MetaData> sectorAssignmentDataStore, File file )
    {
        if( matmFuserStore == null )
        {
            LOG.error( "Cannot load cache file: matmFuserStore is NULL!" );
            return;
        }           
        
        if( matmAircraftFuserStore == null )
        {
            LOG.warn( "Not loading aircraft-fuser-store" );
        }       
        
        if( sectorAssignmentDataStore == null )
        {
            LOG.warn( "Not loading sectorAssignmentDataStore" );
        }         
            
        LOG.info( "Loading Fuser cache: " + file + " ..." );
        
        FuserRepositoryTransferFileIO fuserRepositoryTransferFileIO = new FuserRepositoryTransferFileIO();        
        FuserRepositoryTransfer cache = fuserRepositoryTransferFileIO.loadXmlCache( file );

        if( cache != null )
        {
            matmFuserStore.clear();
            if( matmAircraftFuserStore != null )
            {
                matmAircraftFuserStore.clear();
            }
            if( sectorAssignmentDataStore != null )
            {
                sectorAssignmentDataStore.clear();
            }                        

            for( MatmFlight flight : cache.getFlightList() )
            {
                matmFuserStore.add( flight );
            }

            for( GufiMetaData gufiMetaData : cache.getMetaDataList() )
            {
                matmFuserStore.updateMetaData( matmFuserStore.get( gufiMetaData.getGufi() ), gufiMetaData.getMetaData() );
            }

            if( matmAircraftFuserStore != null )
            {
                for( MatmAircraft aircraft : cache.getAircraftList() )
                {
                    matmAircraftFuserStore.add( aircraft );
                }
            }
            
            if( sectorAssignmentDataStore != null )
            {
                List<MatmSectorAssignment> sectorAssignmentsList = cache.getSectorAssignmentList();

                if(( sectorAssignmentsList != null ) && !sectorAssignmentsList.isEmpty() )
                {
                    // Sort the results by timestamp to ensure they are handled in the correct order
                    Comparator<MatmSectorAssignment> compareByTimestamp = 
                            (MatmSectorAssignment o1, MatmSectorAssignment o2) -> o1.getTimestamp().compareTo( o2.getTimestamp() );

                    Collections.sort( sectorAssignmentsList, compareByTimestamp );                   

                    for( MatmSectorAssignment sectorAssignment : cache.getSectorAssignmentList() )
                    {
                        sectorAssignmentDataStore.add( sectorAssignment );
                    }
                }
            }            

            LOG.info( "-- Flights: " + cache.getFlightList().size() );
            LOG.info( "-- MetaData: " + cache.getMetaDataList().size() );
            LOG.info( "-- Aircraft: " +  cache.getAircraftList().size() );
            LOG.info( "-- SectorAssignments: " +  cache.getSectorAssignmentList().size() );
            LOG.info( "-- FuserStore size: " + matmFuserStore.size() );
            if( matmAircraftFuserStore != null )
            {
                LOG.info( "-- AircraftStore size: " + matmAircraftFuserStore.size() );
            }
            if( sectorAssignmentDataStore != null )
            {
                LOG.info( "-- SectorAssignmentStore size: " + sectorAssignmentDataStore.size() );
            }            
            LOG.info( "... Cache File Initialization Complete!" );                
        }
    }
    
    public void writeFuserStoreToXmlCache( FuserStore <MatmFlight, MetaData> matmFuserStore, 
            FuserStore <MatmAircraft, MetaData> matmAircraftFuserStore, 
            FuserStore <MatmSectorAssignment, MetaData> sectorAssignmentDataStore, File file )
    {
        if( matmFuserStore == null )
        {
            LOG.error( "Cannot write cache file: matmFuserStore is NULL!" );
            return;
        }                
        
        if( matmAircraftFuserStore == null )
        {
            LOG.warn( "Not writing aircraft to cache file: matmAircraftFuserStore is NULL!" );
        }         
        
        if( sectorAssignmentDataStore == null )
        {
            LOG.warn( "Not writing aircraft to cache file: sectorAssignmentDataStore is NULL!" );
        }        

        LOG.info( "Writing Fuser cache: " + file + " ..." );

        FuserRepositoryTransfer cache = new FuserRepositoryTransfer();
        
        try
        {
            matmFuserStore.lockEntireStore();
            
            for( MatmFlight flight : matmFuserStore.getAll() )
            {
                cache.getFlightList().add( flight );

                for( MetaData metaData : matmFuserStore.getAllMetaData( flight ))
                {
                    cache.getMetaDataList().add( new GufiMetaData( flight.getGufi(), metaData ));
                }            
            }
        }
        finally
        {
            matmFuserStore.unlockEntireStore();
        }

        if( matmAircraftFuserStore != null )
        {            
            try
            {
                matmAircraftFuserStore.lockEntireStore();
                
                for( MatmAircraft aircraft : matmAircraftFuserStore.getAll() )
                {
                    cache.getAircraftList().add( aircraft );
                }
            }
            finally
            {
                matmAircraftFuserStore.unlockEntireStore();
            }
        }
        
        if( sectorAssignmentDataStore != null )
        {            
            try
            {            
                sectorAssignmentDataStore.lockEntireStore();
                
                for( MatmSectorAssignment sectorAssignment : sectorAssignmentDataStore.getAll() )
                {
                    cache.getSectorAssignmentList().add( sectorAssignment );
                }
            }
            finally
            {
                sectorAssignmentDataStore.unlockEntireStore();
            }
        }        

        FuserRepositoryTransferFileIO fuserRepositoryTransferFileIO = new FuserRepositoryTransferFileIO();
        fuserRepositoryTransferFileIO.writeXmlCache( cache, file );

        LOG.info( "... Fuser cache write file complete" );
    }               
}
