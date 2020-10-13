package com.mosaicatm.fuser.datacapture.io.batch;

import java.io.Writer;
import java.util.Date;

import com.mosaicatm.fuser.datacapture.io.CaptureData;
import com.mosaicatm.lib.database.bulk.impl.DBWriter;
import com.mosaicatm.matmdata.position.MatmPositionUpdate;

public class MatmPositionUpdateWriterClient
extends AbstractWriterClient<MatmPositionUpdate>
{

    @Override
    public DBWriter getWriter(Writer writer)
    {
        return( new BulkWriter<>( 
                getDelimitedFactory().getDelimitedObjectWriter( getCaptureType(), writer )));
    }

    @Override
    public void writeMessage(DBWriter writer, MatmPositionUpdate message, String recordId)
    {
        if( !active || ( writer == null ) || ( message == null ))
            return;
        
        CaptureData<MatmPositionUpdate> captureData = new CaptureData<>();
        captureData.setTimestamp(timestampFor(message));
        captureData.setRecordTimestamp(getRecordTimestamp( message ));
        captureData.setType(getCaptureType());
        captureData.setGufi(message.getGufi());
        captureData.setRecordId(recordId);
        captureData.setData(message);
        writeData(writer, captureData);
        
    }
    
    @Override
    public Date getRecordTimestamp( MatmPositionUpdate message )
    {       
        Date time = super.getRecordTimestamp(message);
        
        if (time == null)
        {
            time = message.getTimestamp();
        }
        
        return time;
    }

    @Override
    public Date timestampFor(MatmPositionUpdate message)
    {
        if (message != null)
        {
            return message.getTimestamp();
        }
        
        return null;
    }

    @Override
    public String extraPartitionValueFor( MatmPositionUpdate message )
    {
        return null;
    }

}
