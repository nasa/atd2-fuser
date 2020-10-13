package com.mosaicatm.fuser.datacapture.io.batch;

import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.datacapture.CaptureType;
import com.mosaicatm.fuser.datacapture.DataWrapper;
import com.mosaicatm.fuser.datacapture.flat.FlatMatmFlightAllJoin;
import com.mosaicatm.fuser.datacapture.io.CaptureData;
import com.mosaicatm.fuser.datacapture.store.IdSet;
import com.mosaicatm.lib.database.bulk.impl.DBWriter;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.position.MatmPositionUpdate;

public class MatmFlightWriterClient
extends AbstractWriterClient<DataWrapper<MatmFlight>>
{
    private final Log log = LogFactory.getLog(getClass());
    
    public static final String SEQUENCE_ID = "sequence_id";
    
    private String onlyIncludedColumnNames;
    
    @Override
    public DBWriter getWriter(Writer writer)
    {
        if(( getCaptureType() == CaptureType.MATM_FLIGHT ) || 
           ( getCaptureType() == CaptureType.MATM_FLIGHT_ALL )|| 
           ( getCaptureType() == CaptureType.MATM_FLIGHT_ALL_JOIN ))
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
    public void writeMessage(DBWriter writer, DataWrapper<MatmFlight> wrapper, String recordId)
    {
        if( !active || ( writer == null ) || 
            ( wrapper == null ) || wrapper.getData() == null ||
            ( !wrapper.contains(getCaptureType()) ) )
        {
            return;
        }
        
        MatmFlight flight = wrapper.getData();
        IdSet idSet = wrapper.getIdSet();

        CaptureData<Object> captureData = new CaptureData<>();
        captureData.setGufi(flight.getGufi());
        captureData.setRecordTimestamp(getRecordTimestamp( wrapper ));
        captureData.setTimestamp(timestampFor(wrapper));
        captureData.setType(getCaptureType());
        String surfaceAirportName = flight.getSurfaceAirport() == null ? null : flight.getSurfaceAirport().getIataName();
        captureData.setSurfaceAirportName( surfaceAirportName );

        Object data = null;
        switch( getCaptureType() )
        {
            case MATM_FLIGHT:
            case MATM_FLIGHT_ALL:
            case MATM_FLIGHT_REMOVED:
                data = flight; 
                recordId = getRecordId(idSet, (s) -> s.getMatmId()); 
                break;
                
            case EXT_ADSB:
            case EXT_ADSB_ALL:
                if( flight.getExtensions() != null &&
                        flight.getExtensions().getAdsbExtension() != null )
                {
                    data = flight.getExtensions().getAdsbExtension();
                    recordId = getRecordId(idSet, (s) -> s.getAdsbExtensionId());
                    
                }
                break;

            case EXT_ASDEX:
            case EXT_ASDEX_ALL:   
                if(( flight.getExtensions() != null ) &&
                        ( flight.getExtensions().getAsdexExtension() != null ))
                {
                    data = flight.getExtensions().getAsdexExtension();
                    recordId = getRecordId(idSet, (s) -> s.getAsdexExtensionId());
                    
                }
                break;
                
            case EXT_CAT11:
            case EXT_CAT11_ALL:
                if( flight.getExtensions() != null &&
                        flight.getExtensions().getCat11Extension() != null )
                {
                    data = flight.getExtensions().getCat11Extension();
                    recordId = getRecordId(idSet, (s) -> s.getCat11ExtensionId());
                    
                }
                break;

            case EXT_CAT62:
            case EXT_CAT62_ALL:
                if( flight.getExtensions() != null &&
                        flight.getExtensions().getCat62Extension() != null )
                {
                    data = flight.getExtensions().getCat62Extension();
                    recordId = getRecordId(idSet, (s) -> s.getCat62ExtensionId());
                }
                break;

            case EXT_DERIVED:
            case EXT_DERIVED_ALL:
                if(( flight.getExtensions() != null ) &&
                        ( flight.getExtensions().getDerivedExtension() != null ))                               
                {
                    data = flight.getExtensions().getDerivedExtension();
                    recordId = getRecordId(idSet, (s) -> s.getDerivedExtensionId());
                }
                break;

            case EXT_TBFM:
            case EXT_TBFM_ALL:
                if(( flight.getExtensions() != null ) &&
                        ( flight.getExtensions().getTbfmExtension() != null ))
                {
                    data = flight.getExtensions().getTbfmExtension();
                    recordId = getRecordId(idSet, (s) -> s.getTbfmExtensionId());
                }
                break;

            case EXT_TFM:
            case EXT_TFM_ALL:
                if(( flight.getExtensions() != null ) &&
                        ( flight.getExtensions().getTfmExtension() != null ))
                {
                    data = flight.getExtensions().getTfmExtension();
                    recordId = getRecordId(idSet, (s) -> s.getTfmExtensionId());
                }
                break;
                
            case EXT_TFM_TFDM:
            case EXT_TFM_TFDM_ALL:
                if(( flight.getExtensions() != null ) &&
                        ( flight.getExtensions().getTfmTfdmExtension() != null ))
                {
                    data = flight.getExtensions().getTfmTfdmExtension();
                    recordId = getRecordId(idSet, (s) -> s.getTfmTfdmExtensionId());
                }
                break;


            case EXT_TFM_TRAVERSAL:
            case EXT_TFM_TRAVERSAL_ALL:
                if(( flight.getExtensions() != null ) &&
                        ( flight.getExtensions().getTfmsFlightTraversalExtension() != null ))
                {
                    data = flight.getExtensions().getTfmsFlightTraversalExtension();
                    recordId = getRecordId(idSet, (s) -> s.getTfmTraversalExtensionId());
                }
                break;
                
            case EXT_MATM_AIRLINE_MESSAGE:
            case EXT_MATM_AIRLINE_MESSAGE_ALL:
                if(( flight.getExtensions() != null ) &&
                        ( flight.getExtensions().getMatmAirlineMessageExtension() != null ))
                {
                    data = flight.getExtensions().getMatmAirlineMessageExtension();
                    recordId = getRecordId(idSet, (s) -> s.getAirlineExtensionId());
                }
                break;
                
            case EXT_IDAC:
            case EXT_IDAC_ALL:
                if(( flight.getExtensions() != null ) &&
                        ( flight.getExtensions().getIdacExtension() != null ))
                {
                    data = flight.getExtensions().getIdacExtension();
                    recordId = getRecordId(idSet, (s) -> s.getIdacExtensionId());
                }
                break;
                
            case EXT_SURFACE_MODEL:
            case EXT_SURFACE_MODEL_ALL:
                if(( flight.getExtensions() != null ) &&
                        ( flight.getExtensions().getSurfaceModelExtension() != null ))
                {
                    data = flight.getExtensions().getSurfaceModelExtension();
                    recordId = getRecordId(idSet, (s) -> s.getSurfaceModelExtensionId());
                }
                break;
            case EXT_AEFS:
            case EXT_AEFS_ALL:
                if (( flight.getExtensions() != null) &&
                          ( flight.getExtensions().getAefsExtension() != null ))
                {
                    data = flight.getExtensions().getAefsExtension();
                    recordId = getRecordId(idSet, (s) -> s.getAefsExtensionId());
                }
                break;
            case POSITION:
            case MATM_POSITION_ALL:
                if( flight.getPosition() != null )
                {
                    MatmPositionUpdate positionUpdate = convertToPosition(flight);
                    if( positionUpdate != null )
                    {
                        recordId = getRecordId(idSet, (s) -> s.getPositionId());
                        data = positionUpdate;
                    }
                }
                break;
            case MATM_FLIGHT_ALL_JOIN:
                data = new FlatMatmFlightAllJoin(flight, idSet);
                break;
            case EXT_SFDPS:
            case EXT_SFDPS_ALL:
                if( flight.getExtensions() != null &&
                        flight.getExtensions().getSfdpsExtension() != null )
                {
                    data = flight.getExtensions().getSfdpsExtension();
                    recordId = getRecordId(idSet, (s) -> s.getSfdpsExtensionId());
                }
                break;

            case EXT_SMES:
            case EXT_SMES_ALL:
                if(( flight.getExtensions() != null ) &&
                        ( flight.getExtensions().getSmesExtension() != null ))
                {
                    data = flight.getExtensions().getSmesExtension();
                    recordId = getRecordId(idSet, (s) -> s.getSmesExtensionId());

                }
                break;

            case EXT_TMI:
            case EXT_TMI_ALL:
                if(( flight.getExtensions() != null ) &&
                        ( flight.getExtensions().getTmiExtension() != null ))
                {
                    data = flight.getExtensions().getTmiExtension();
                    recordId = getRecordId(idSet, (s) -> s.getTmiExtensionId());

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
    
    public String getRecordId(IdSet idSet, Function<IdSet, String> func)
    {
        String recordId = null;
        
        if (idSet != null && func != null)
        {
            recordId = func.apply(idSet);
        }
        
        return recordId;
    }
    
    @Override
    public Date getRecordTimestamp( DataWrapper<MatmFlight> message )
    {       
        Date time = super.getRecordTimestamp(message);
        
        if (time == null && message != null && message.getData() != null)
        {
            time = message.getData().getTimestamp();
        }
        
        return time;
    }

    @Override
    public Date timestampFor(DataWrapper<MatmFlight> message)
    {
        if (message != null && message.getData() != null)
        {
            return message.getData().getTimestamp();
        }
        
        return null;
    }
    
    private MatmPositionUpdate convertToPosition( MatmFlight flight )
    {
        Position position = flight.getPosition();
        MatmPositionUpdate positionUpdate = null;
        
        // Not every source has altitude in the position
        if (flight.getGufi() != null && flight.getAcid() != null && position != null && position.getTimestamp() != null &&
            position.getLongitude() != null && position.getLatitude() != null &&
            flight.getArrivalAerodrome() != null && flight.getArrivalAerodrome().getIataName() != null &&
            flight.getDepartureAerodrome() != null && flight.getDepartureAerodrome().getIataName() != null)
        {
            positionUpdate = new MatmPositionUpdate();
                
            positionUpdate.setGufi(flight.getGufi());
            positionUpdate.setAcid(flight.getAcid());
            positionUpdate.setArrivalAerodromeIataName(flight.getArrivalAerodrome().getIataName());
            positionUpdate.setDepartureAerodromeIataName(flight.getDepartureAerodrome().getIataName());
            positionUpdate.setTimestamp(flight.getTimestamp());
            positionUpdate.setPosition(position);
            positionUpdate.setLastUpdateSource(flight.getLastUpdateSource());
            positionUpdate.setSystemId(flight.getSystemId());
        }
        
        return positionUpdate;
    }

    @Override
    public String extraPartitionValueFor( DataWrapper<MatmFlight> message )
    {
        String airport = message.getData().getSurfaceAirport() == null ? null : message.getData().getSurfaceAirport().getIataName();
        return airport;
    }
}
