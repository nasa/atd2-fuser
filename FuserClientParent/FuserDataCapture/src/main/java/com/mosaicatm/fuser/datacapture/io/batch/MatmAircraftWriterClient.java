package com.mosaicatm.fuser.datacapture.io.batch;

import java.io.Writer;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.datacapture.CaptureType;
import com.mosaicatm.fuser.datacapture.DataWrapper;
import com.mosaicatm.fuser.datacapture.io.CaptureData;
import com.mosaicatm.fuser.datacapture.store.IdSet;
import com.mosaicatm.lib.database.bulk.impl.DBWriter;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmAircraftWriterClient
extends AbstractWriterClient<DataWrapper<MatmAircraft>>
{
    private final Log log = LogFactory.getLog(getClass());
    
    public static final String SEQUENCE_ID = "sequence_id";
    
    private String onlyIncludedColumnNames;
    
    @Override
    public DBWriter getWriter(Writer writer)
    {
        if(( getCaptureType() == CaptureType.MATM_AIRCRAFT ) || 
           ( getCaptureType() == CaptureType.MATM_AIRCRAFT_ALL ))
        {
            if( onlyIncludedColumnNames == null )
            {
                if( bulkDriver != null )
                {
                    List<String> columns = bulkDriver.getColumns( parentTable );
                    StringBuilder builder = new StringBuilder();
                    for (String column : columns)
                    {
                        // we want sequence to be auto generated
                        // exclude sequence from database copy
                        if( !SEQUENCE_ID.equals( column ))
                        {
                            if( builder.length() > 0 )
                            {
                                builder.append( "," );
                            }
                            builder.append( column );
                        }
                    }
                    onlyIncludedColumnNames = builder.toString();
                }
            }        

            return( new BulkWriter<>( 
                    getDelimitedFactory().getDelimitedObjectWriter( getCaptureType(), writer ), onlyIncludedColumnNames ));
        }
        else
        {
            return( new BulkWriter<>( 
                    getDelimitedFactory().getDelimitedObjectWriter( getCaptureType(), writer )));            
        }
    }

    /**
     * @param writer database writer object
     * @param wrapper wrapper containing the flight for writing
     * @param recordID not used and will be using the one from the wrapper to
     *  request a new one for each capture type
     */
    @Override
    public void writeMessage(DBWriter writer, DataWrapper<MatmAircraft> wrapper, String recordId)
    {
        if( !active || ( writer == null ) || 
            ( wrapper == null ) || wrapper.getData() == null ||
            ( !wrapper.contains(getCaptureType()) ) )
        {
            return;
        }
        
        MatmAircraft aircraft = wrapper.getData();
        IdSet idSet = wrapper.getIdSet();
        
        CaptureData<Object> captureData = new CaptureData<>();
        captureData.setGufi(aircraft.getRegistration());
        captureData.setRecordTimestamp(getRecordTimestamp( wrapper ));
        captureData.setTimestamp(timestampFor(wrapper));
        captureData.setType(getCaptureType());
        
        Object data = null;
        switch( getCaptureType() )
        {
            case MATM_AIRCRAFT:
            case MATM_AIRCRAFT_ALL:
            case MATM_AIRCRAFT_REMOVED:
                data = aircraft;
                if (CaptureType.MATM_AIRCRAFT_ALL.equals(getCaptureType()))
                {
                    recordId = idSet.getAircraftId();
                }
                break;
            default:
                log.error( "Cannot determine data. Unsupported capture type: " + getCaptureType() );
                break;
        }
        
        if (data != null)
        {
            captureData.setData(data);
            captureData.setRecordId(recordId);
            writeData(writer, captureData);
        }
        
    }
    
    @Override
    public Date getRecordTimestamp( DataWrapper<MatmAircraft> message )
    {       
        Date time = super.getRecordTimestamp(message);
        
        if (time == null && message != null && message.getData() != null)
        {
            time = message.getData().getTimestamp();
        }
        
        return time;
    }

    @Override
    public Date timestampFor(DataWrapper<MatmAircraft> message)
    {
        if (message != null && message.getData() != null)
        {
            return message.getData().getTimestamp();
        }
        
        return null;
    }
    
    @Override
    public String extraPartitionValueFor( DataWrapper<MatmAircraft> message )
    {
        return null;
    }
}
