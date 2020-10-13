package com.mosaicatm.fuser.datacapture.playback;

import java.io.File;
import java.util.Date;

import org.apache.camel.Handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;

import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.util.filter.Filter;
import com.mosaicatm.rollingfile.impl.DatePathFolder;
import com.mosiacatm.ifile.IFile;
import com.mosiacatm.ifile.StandardFileSystem;

import com.mosaicatm.fuser.client.api.FuserClientApi;
import com.mosaicatm.fuser.common.matm.xfer.FuserRepositoryTransfer;
import com.mosaicatm.fuser.common.matm.xfer.FuserRepositoryTransferFileIO;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class DailyFuserRepositoryCacheHandler
{
    private static final String FILE_NAME_POSTFIX_FORMAT = "yyyyMMdd";
    
    private final Log log = LogFactory.getLog( getClass() );
    
    private final FuserRepositoryTransferFileIO fuserStoreFileIO = new FuserRepositoryTransferFileIO();
    
    private boolean active = false;
    
    private Clock clock;
    
    private String archiveRootFolder;
    private String archiveDatePathString;
    private boolean initializeFromRepositoryCacheFile = false;
    
    private FuserClientApi<MatmFlight> fuserClientApi;
    private FuserClientApi<MatmAircraft> aircraftFuserClientApi;
    
    private long startTime = -1;
    private boolean repositoryCacheFileLoaded = false;
    
    @Handler
    public void timeUpdated()
    {          
        if( !active || !initializeFromRepositoryCacheFile )
        {
            return;
        }
        
        // First clock update
        if( startTime <= 0 )
        {
            startTime = clock.getTimeInMillis();
        
            if( log.isDebugEnabled() )
            {
                log.debug( "Clock time: " + new Date( startTime ));
            }                     
            
            if(( startTime <= 0 ) || !checkInitialized() )
            {
                return;
            }               
                        
            File file = FuserRepositoryTransferFileIO.getDefaultCacheFile( 
                    getCacheFolder( startTime, false ), getFileNamePostfix( startTime ));

            log.info( "Loading Fuser flight cache file: " + file + " ..." );
            
            FuserRepositoryTransfer cache = fuserStoreFileIO.loadXmlCache( file );

            if( cache != null )
            {
                try
                {
                    Filter<MatmFlight> filter = fuserClientApi.getFilter();
                    Filter<MatmAircraft> aircraftFilter = aircraftFuserClientApi.getFilter();
                    
                    fuserClientApi.getStore().lock();
                    aircraftFuserClientApi.getStore().lock();

                    repositoryCacheFileLoaded = true;

                    for( MatmFlight flight : cache.getFlightList() )
                    {
                        if( filter == null || filter.doFilter( flight ) )
                        {
                            fuserClientApi.getStore().add( flight );
                        }
                    }

                    for( MatmAircraft aircraft : cache.getAircraftList() )
                    {
                        if( aircraftFilter == null || aircraftFilter.doFilter( aircraft ) )
                        {
                            aircraftFuserClientApi.getStore().add( aircraft );
                        }
                    }

                    log.info( "... Loaded " + fuserClientApi.getStore().size() + " flights, "  + 
                            aircraftFuserClientApi.getStore().size() + " aircraft" );
                }
                finally
                {
                    aircraftFuserClientApi.getStore().unlock();
                    fuserClientApi.getStore().unlock();                                
                }
            }
        }        
    }     

    public boolean isRepositoryCacheFileLoaded()
    {
        return( repositoryCacheFileLoaded );
    }
    
    public void setActive( boolean active )
    {
        this.active = active;
    }

    public void setClock( Clock clock )
    {
        this.clock = clock;
    }
    
    public void setArchiveRootFolder( String archiveRootFolder )
    {
        this.archiveRootFolder = archiveRootFolder;
    }

    public void setArchiveDatePathString( String archiveDatePathString )
    {
        this.archiveDatePathString = archiveDatePathString;
    }

    public void setInitializeFromRepositoryCacheFile( boolean initializeFromRepositoryCacheFile )
    {
        this.initializeFromRepositoryCacheFile = initializeFromRepositoryCacheFile;
    }

    public void setFuserClientApi( FuserClientApi<MatmFlight> fuserClientApi )
    {
        this.fuserClientApi = fuserClientApi;
    }
    
    public void setAircraftFuserClientApi( FuserClientApi<MatmAircraft> aircraftFuserClientApi )
    {
        this.aircraftFuserClientApi = aircraftFuserClientApi;
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
    
    private boolean checkInitialized()
    {
        if( clock == null )
        {
            log.error( "Cannot read daily repository file: clock is NULL!" );
            return( false );
        }        

        if( archiveRootFolder == null )
        {
            log.error( "Cannot read daily repository file: archiveRootFolder is NULL!" );
            return( false );
        }
        
        if( archiveDatePathString == null )
        {
            log.error( "Cannot read daily repository file: archiveDatePathString is NULL!" );
            return( false );
        }              
        
        if( fuserClientApi == null )
        {
            log.error( "Cannot read daily repository file: fuserClientApi is NULL!" );
            return( false );
        }        
        
        if( aircraftFuserClientApi == null )
        {
            log.error( "Cannot read daily repository file: aircraftFuserClientApi is NULL!" );
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
}
