package com.mosaicatm.fuser.datacapture.io.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.datacapture.CaptureType;
import com.mosaicatm.fuser.datacapture.flat.FlatFactory;
import com.mosaicatm.fuser.datacapture.io.DelimitedObjectFactory;
import com.mosaicatm.lib.database.bulk.BulkDriver;
import com.mosaicatm.lib.database.bulk.BulkLoaderClient;
import com.mosaicatm.lib.time.Clock;

public class WriterClientFactory
{
    private final Log log = LogFactory.getLog(getClass());
    
    private Clock clock;
    
    private DelimitedObjectFactory delimitedFactory;
    
    private FlatFactory flatFactory;
    
    private String parentTable;    
    
    private BulkDriver bulkDriver;

    public BulkDriver getBulkDriver()
    {
        return bulkDriver;
    }

    public void setBulkDriver( BulkDriver bulkDriver )
    {
        this.bulkDriver = bulkDriver;
    }

    public Clock getClock()
    {
        return clock;
    }

    public void setClock( Clock clock )
    {
        this.clock = clock;
    }

    public DelimitedObjectFactory getDelimitedFactory()
    {
        return delimitedFactory;
    }

    public void setDelimitedFactory( DelimitedObjectFactory delimitedFactory )
    {
        this.delimitedFactory = delimitedFactory;
    }

    public FlatFactory getFlatFactory()
    {
        return flatFactory;
    }

    public void setFlatFactory( FlatFactory flatFactory )
    {
        this.flatFactory = flatFactory;
    }

    public String getParentTable()
    {
        return parentTable;
    }

    public void setParentTable( String parentTable )
    {
        this.parentTable = parentTable;
    }
    
    public BulkLoaderClient<?> getClientInstance( String captureType, String parentTable )
    {
        return( getClientInstance( captureType, parentTable, true ));
    }
    
    public BulkLoaderClient<?> getPositionClientInstance( String captureType, String parentTable )
    {
        return( getPositionClientInstance( captureType, parentTable, true ));
    }
    
    public BulkLoaderClient<?> getAircraftClientInstance( String captureType, String parentTable )
    {
        return( getAircraftClientInstance( captureType, parentTable, true));
    }
    
    public BulkLoaderClient<?> getClientInstance( String captureType, String parentTable, boolean active )
    {
        try
        {
            CaptureType captureTypeEnum = CaptureType.valueOf( captureType );
            return( getClientInstance( captureTypeEnum, parentTable, active ));
        }
        catch( Exception ex )
        {        
            log.error( "Error getting client instance for capture type: " + captureType, ex);
            return( null );
        }
    }
    
    public BulkLoaderClient<?> getPositionClientInstance( String captureType, String parentTable, boolean active )
    {
        try
        {
            CaptureType captureTypeEnum = CaptureType.valueOf( captureType );
            return( getPositionClientInstance( captureTypeEnum, parentTable, active ));
        }
        catch( Exception ex )
        {        
            log.error( "Error getting client instance for capture type: " + captureType, ex);
            return( null );
        }
    }
    
    public BulkLoaderClient<?> getAircraftClientInstance( String captureType, String parentTable, boolean active )
    {
        try
        {
            CaptureType captureTypeEnum = CaptureType.valueOf( captureType );
            return( getAircraftClientInstance( captureTypeEnum, parentTable, active ));
        }
        catch( Exception ex )
        {        
            log.error( "Error getting client instance for capture type: " + captureType, ex);
            return( null );
        }
    }

    public BulkLoaderClient<?> getClientInstance( CaptureType captureType, String parentTable )
    {
        return( getClientInstance( captureType, parentTable, true ));
    }
    
    public BulkLoaderClient<?> getPositionClientInstance( CaptureType captureType, String parentTable )
    {
        return( getPositionClientInstance( captureType, parentTable, true ));
    }
    
    public BulkLoaderClient<?> getAircraftClientInstance( CaptureType captureType, String parentTable )
    {
        return( getAircraftClientInstance( captureType, parentTable, true ));
    }

    public BulkLoaderClient<?> getClientInstance( final CaptureType captureType, String parentTable, boolean active )
    {
        AbstractWriterClient<?> client = new MatmFlightWriterClient();
        
        initializeClientInstance( client, captureType, parentTable, active );
        
        return( client );
    }
    
    public BulkLoaderClient<?> getPositionClientInstance( final CaptureType captureType, String parentTable, boolean active )
    {
        AbstractWriterClient<?> client = new MatmPositionUpdateWriterClient();

        initializeClientInstance( client, captureType, parentTable, active );
        
        return( client );
    }
    
    public BulkLoaderClient<?> getAircraftClientInstance( final CaptureType captureType, String parentTable, boolean active)
    {
        AbstractWriterClient<?> client = new MatmAircraftWriterClient();
        
        initializeClientInstance( client, captureType, parentTable, active);
        
        return ( client );
    }
    
    private BulkLoaderClient<?> initializeClientInstance( AbstractWriterClient<?> client,
        final CaptureType captureType, String parentTable, boolean active )
    {
        if (client != null)
        {
            client.setCaptureType(captureType);
            client.setClock( clock );
            client.setDelimitedFactory( delimitedFactory );
            client.setFlatFactory( flatFactory );
            client.setBulkDriver( bulkDriver );
            
            client.setActive( active );
            client.setParentTable( parentTable );
        }
        
        return( client );
    }
    
}
