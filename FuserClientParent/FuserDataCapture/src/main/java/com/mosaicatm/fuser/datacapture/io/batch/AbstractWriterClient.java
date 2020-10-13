package com.mosaicatm.fuser.datacapture.io.batch;

import java.util.Date;

import com.mosaicatm.fuser.datacapture.CaptureType;
import com.mosaicatm.fuser.datacapture.flat.FlatFactory;
import com.mosaicatm.fuser.datacapture.io.CaptureData;
import com.mosaicatm.fuser.datacapture.io.DelimitedObjectFactory;
import com.mosaicatm.lib.database.bulk.BulkDriver;
import com.mosaicatm.lib.database.bulk.BulkLoaderClient;
import com.mosaicatm.lib.database.bulk.impl.DBWriter;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.matmdata.flight.MatmFlight;

public abstract class AbstractWriterClient<T> implements BulkLoaderClient<T>
{
    protected Clock clock;
    
    protected DelimitedObjectFactory delimitedFactory;
    
    protected FlatFactory flatFactory;
    
    protected String parentTable;
    
    protected BulkDriver bulkDriver;
    
    protected CaptureType captureType;
    
    protected boolean active = true;
    
    public BulkDriver getBulkDriver()
    {
        return bulkDriver;
    }

    public void setBulkDriver( BulkDriver bulkDriver )
    {
        this.bulkDriver = bulkDriver;
    }

    @Override
    public boolean isActive()
    {
        return active;
    }

    @Override
    public void setActive( boolean active )
    {
        this.active = active;
    }
    
    public void setCaptureType( CaptureType type )
    {
        this.captureType = type;
    }
    
    public CaptureType getCaptureType()
    {
        return this.captureType;
    }
    
    public abstract void writeMessage(DBWriter writer, T message, String recordId);
    public abstract String extraPartitionValueFor( T message );
    
    @SuppressWarnings("unchecked")
    protected <E, F> void writeData(DBWriter writer, CaptureData<E> data)
    {
        F flat = getFlatFactory().flatten(data);
        
        if (flat != null)
            ((BulkWriter<F>)writer).writeLine(flat);
    }
    
    protected Date getRecordTimestamp( T message )
    {       
        if( clock != null )
        {
            return( new Date( clock.getTimeInMillis() ));
        }
        
        return null;
    }

    @Override
    public abstract Date timestampFor(T message);

    @Override
    public String getParentTable()
    {
        return parentTable;
    }

    @Override
    public void setParentTable(String parentTable)
    {
        this.parentTable = parentTable;
    }

    public Clock getClock()
    {
        return clock;
    }

    public void setClock(Clock clock)
    {
        this.clock = clock;
    }
    
    public DelimitedObjectFactory getDelimitedFactory()
    {
        return delimitedFactory;
    }
    
    public void setDelimitedFactory(DelimitedObjectFactory delimitedFactory)
    {
        this.delimitedFactory = delimitedFactory;
    }
    
    public FlatFactory getFlatFactory()
    {
        return flatFactory;
    }
    
    public void setFlatFactory(FlatFactory flatFactory)
    {
        this.flatFactory = flatFactory;
    }
}
