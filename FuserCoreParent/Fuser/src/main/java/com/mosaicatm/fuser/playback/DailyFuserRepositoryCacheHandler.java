package com.mosaicatm.fuser.playback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.rollingfile.impl.DatePathFolder;
import com.mosiacatm.ifile.IFile;
import com.mosiacatm.ifile.StandardFileSystem;

import com.mosaicatm.fuser.common.matm.xfer.FuserRepositoryTransferFileIO;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.matm.xfer.FuserStoreFileIO;
import com.mosaicatm.fuser.store.SectorAssignmentDataStore;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class DailyFuserRepositoryCacheHandler
{
    private static final String FILE_NAME_POSTFIX_FORMAT = "yyyyMMdd";
    
    private static final long MILLIS_IN_HOUR = 60L * 60L * 1000L;
    private static final long MILLIS_IN_DAY = 24L * MILLIS_IN_HOUR;
        
    private final Log log = LogFactory.getLog( getClass() );
    
    private final FuserStoreFileIO fuserStoreFileIO = new FuserStoreFileIO();
    
    private boolean active = false;
    
    private Clock clock;
    private FuserStore <MatmFlight, MetaData> fuserStore;
    private FuserStore <MatmAircraft, MetaData> aircraftFuserStore;
    private FuserStore <MatmSectorAssignment, MetaData> sectorAssignmentDataStore;
    
    private String archiveRootFolder;
    private String archiveDatePathString;
    private int processHoursBeforeWriteIfNoCache = 0;
    private boolean initializeFromRepositoryCacheFile = false;
    
    private long startTime = -1;
    private long lastTimeUpdated = -1;
    private long requiredProcessingTime = -1;
    private boolean requiredProcessingTimeMet = false;
    private boolean repositoryCacheFileLoaded = false;
    
    @Handler
    public void timeUpdated()
    {        
        if( !active )
        {
            return;
        }
        
        if( !checkInitialized() )
        {
            return;
        }
        
        long currentTime = clock.getTimeInMillis();
        if(( lastTimeUpdated > 0 ) && ( currentTime <= lastTimeUpdated ))
        {
            return;
        }      
        
        if( log.isDebugEnabled() )
        {
            if(( lastTimeUpdated / 600000 ) != ( currentTime / 600000 ))
            {
                log.debug( "Clock time: " + new Date( currentTime ));
            }
        }        
        
        // First clock update
        if( startTime <= 0 )
        {
            startTime = currentTime;            
                        
            if( initializeFromRepositoryCacheFile )
            {
                File file = FuserRepositoryTransferFileIO.getDefaultCacheFile( 
                        getCacheFolder( currentTime, false ), getFileNamePostfix( currentTime ));
                
                try
                {
                    fuserStore.lockEntireStore();
                    if( aircraftFuserStore != null )
                    {
                        aircraftFuserStore.lockEntireStore();
                    }
                    if( sectorAssignmentDataStore != null )
                    {
                        sectorAssignmentDataStore.lockEntireStore();
                    }                    
                
                    fuserStoreFileIO.loadFuserStoreFromXmlCache( fuserStore, aircraftFuserStore, sectorAssignmentDataStore, file );

                    if( !fuserStore.getAll().isEmpty() )
                    {
                        repositoryCacheFileLoaded = true;
                    }
                }
                finally
                {
                    if( sectorAssignmentDataStore != null )
                    {
                        sectorAssignmentDataStore.unlockEntireStore();
                    }                    
                    if( aircraftFuserStore != null )
                    {
                        aircraftFuserStore.unlockEntireStore();
                    }
                    fuserStore.unlockEntireStore();
                }
            }
            
            if( repositoryCacheFileLoaded )
            {
                requiredProcessingTime = -1;
                requiredProcessingTimeMet = true;
            }
            else
            {
                requiredProcessingTime = startTime + ( processHoursBeforeWriteIfNoCache * MILLIS_IN_HOUR );
            }
        }
        
        if( !requiredProcessingTimeMet )
        {
            requiredProcessingTimeMet = ( currentTime >= requiredProcessingTime );
        }
        
        if( requiredProcessingTimeMet && isNewDay( currentTime ))
        {            
            try 
            {
                fuserStore.lockEntireStore();
                
                if( aircraftFuserStore != null )
                {                
                    aircraftFuserStore.lockEntireStore();
                }
                if( sectorAssignmentDataStore != null )
                {
                    sectorAssignmentDataStore.lockEntireStore();
                }                  
                
                // Wait a bit, for in-transit processing to flush out
                Thread.sleep( 2000L );
                
                File file = FuserRepositoryTransferFileIO.getDefaultCacheFile( 
                        getCacheFolder( currentTime, true ), getFileNamePostfix( currentTime ));

                fuserStoreFileIO.writeFuserStoreToXmlCache( fuserStore, aircraftFuserStore, sectorAssignmentDataStore, file );                
            }
            catch( InterruptedException ex )
            {
            }
            finally
            {
                if( sectorAssignmentDataStore != null )
                {
                    sectorAssignmentDataStore.unlockEntireStore();
                }                  
                if( aircraftFuserStore != null )
                {
                    aircraftFuserStore.unlockEntireStore();
                }
                fuserStore.unlockEntireStore();                
            }
        }
        
        lastTimeUpdated = currentTime;
    }
    
    public boolean isRepositoryCacheFileLoaded()
    {
        return( repositoryCacheFileLoaded );
    }

    public boolean isRequiredProcessingTimeMet()
    {
        return( requiredProcessingTimeMet );
    }
    
    public void setActive( boolean active )
    {
        this.active = active;
    }

    public void setClock( Clock clock )
    {
        this.clock = clock;
    }

    public void setFuserStore( FuserStore <MatmFlight, MetaData> fuserStore )
    {
        this.fuserStore = fuserStore;
    }

    public void setAircraftFuserStore( FuserStore <MatmAircraft, MetaData> aircraftFuserStore )
    {
        this.aircraftFuserStore = aircraftFuserStore;
    }
    
    public void setSectorAssignmentDataStore( FuserStore <MatmSectorAssignment, MetaData> sectorAssignmentDataStore )
    {
        this.sectorAssignmentDataStore = sectorAssignmentDataStore;
    }    
    
    public void setArchiveRootFolder( String archiveRootFolder )
    {
        this.archiveRootFolder = archiveRootFolder;
    }

    public void setArchiveDatePathString( String archiveDatePathString )
    {
        this.archiveDatePathString = archiveDatePathString;
    }

    public void setProcessHoursBeforeWriteIfNoCache( int processHoursBeforeWriteIfNoCache )
    {
        this.processHoursBeforeWriteIfNoCache = processHoursBeforeWriteIfNoCache;
    }

    public void setInitializeFromRepositoryCacheFile( boolean initializeFromRepositoryCacheFile )
    {
        this.initializeFromRepositoryCacheFile = initializeFromRepositoryCacheFile;
    }
    
    private boolean checkInitialized()
    {
        if( clock == null )
        {
            log.error( "Cannot write daily repository file: clock is NULL!" );
            return( false );
        }
        
        if( fuserStore == null )
        {
            log.error( "Cannot write daily repository file: repository is NULL!" );
            return( false );
        }

        if( archiveRootFolder == null )
        {
            log.error( "Cannot write daily repository file: archiveRootFolder is NULL!" );
            return( false );
        }
        
        if( archiveDatePathString == null )
        {
            log.error( "Cannot write daily repository file: archiveDatePathString is NULL!" );
            return( false );
        }     
        
        return( true );
    }

    private String getCacheFolder( long timeInMillis, boolean createFolder )
    {
        DatePathFolder datePath = new DatePathFolder( archiveRootFolder, archiveDatePathString, new StandardFileSystem() );
        IFile folder = datePath.getFolder( new Date( timeInMillis ), createFolder );        
        
        return( folder.getAbsolutePath() );
    }    
    
    private static String getFileNamePostfix( long timeInMillis )
    {
        return( getFileNamePostfix( new Date( timeInMillis )));
    }
    
    private static String getFileNamePostfix( Date date )
    {
        SimpleDateFormat sdf = new SimpleDateFormat( FILE_NAME_POSTFIX_FORMAT );
        return( sdf.format( date ));
    }
    
    private boolean isNewDay( long time )
    {
        if( lastTimeUpdated <= 0 )
        {
            return( false );
        }
        
        return( getDay( lastTimeUpdated ) != getDay( time ));
    }
    
    private long getDay( long timeInMillis )
    {
        return( timeInMillis / MILLIS_IN_DAY );
    }    
}
