package com.mosaicatm.fuser.match;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.transform.matm.airline.AirlineDataSource;
import com.mosaicatm.gufiservice.data.GlobalGufiFlight;
import com.mosaicatm.gufiservice.interfaces.GufiActionType;
import com.mosaicatm.gufiservice.interfaces.GufiFlightState;
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.CancelledType;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;
import com.mosaicatm.matmdata.flight.extension.TfmTfdmExtension;

public class MatmToGufiFlightTransform
implements Transformer<GlobalGufiFlight, MatmFlight>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private final String FUSER_CLIENT_SYNC = "FUSER_CLIENT_SYNC";
    
    @Override
    public GlobalGufiFlight transform(MatmFlight matm)
    {
        GlobalGufiFlight gufiFlight = new GlobalGufiFlight();
        gufiFlight.setGufi(matm.getGufi()); //should always be null at this point
        transformMessageType( matm, gufiFlight );
        
        gufiFlight.setLastMessageTime(matm.getTimestamp());
        
        gufiFlight.setAcid(matm.getAcid());
        
        if(( matm.getLastUpdateSource() == null ) || !matm.getLastUpdateSource().equals( "AIRLINE" ))
            gufiFlight.setAircraftType( matm.getAircraftType() );
        
        gufiFlight.setDepartureAirport(airportCode(matm.getDepartureAerodrome()));
        gufiFlight.setDestinationAirport(airportCode(matm.getArrivalAerodrome()));
        
        if(( matm.getSurfaceAirport() != null ) && ( matm.getSurfaceAirport().getIataName() != null ))
            gufiFlight.setAsdexAirport( matm.getSurfaceAirport().getIataName() );
        
        if(matm.getAircraftAddress() != null)
            gufiFlight.setModeSAddress(Long.parseLong(matm.getAircraftAddress()));    // OCtal???
        if(matm.getAircraftRegistration() != null)
            gufiFlight.setAircraftRegistration(matm.getAircraftRegistration());
        
        if(matm.getPosition() != null)
            transform(matm.getPosition(), gufiFlight);
        
        if(matm.getCancelled() == CancelledType.CANCELLED)
            gufiFlight.setLatestState( GufiFlightState.Cancelled );
        
        if(matm.getExtensions() != null)
            transformUniqueId(matm.getExtensions(), gufiFlight);
        
        TimeAndAccuracy dept_time = latestDepartureTime(matm);
        Boolean accurate_dept_time = null;
        if( dept_time != null )
        {
            gufiFlight.setLatestDepartureTime( dept_time.getTime() );
            accurate_dept_time = dept_time.isAccurate();
        }

        TimeAndAccuracy arr_time = latestArrivalTime(matm);
        Boolean accurate_arr_time = null;
        if( arr_time != null )
        {
            gufiFlight.setLatestArrivalTime( arr_time.getTime() );
            accurate_arr_time = arr_time.isAccurate();
        }
        
        gufiFlight.setAction( GufiActionType.REQUEST_GUFI );
        if(( accurate_dept_time != null ) && ( accurate_arr_time != null ) &&
                !accurate_dept_time && !accurate_arr_time )
        {
            gufiFlight.setAction( GufiActionType.REQUEST_GUFI_INACCURATE_DEPARTURE_ARRIVAL_TIMES );
        }
        else if(( accurate_dept_time != null ) && !accurate_dept_time )
        {
            gufiFlight.setAction( GufiActionType.REQUEST_GUFI_INACCURATE_DEPARTURE_TIME );
        }   
        else if(( accurate_arr_time != null ) && !accurate_arr_time )
        {
            gufiFlight.setAction( GufiActionType.REQUEST_GUFI_INACCURATE_ARRIVAL_TIME );
        }          
        
        return gufiFlight;
    }
    
    private void transform(Position position, GlobalGufiFlight gufiFlight)
    {
        gufiFlight.setLatestLatitude(position.getLatitude());
        gufiFlight.setLatestLongitude(position.getLongitude());
        gufiFlight.setLatestAltitude(position.getAltitude());
        gufiFlight.setLatestPositionUpdateTime(position.getTimestamp());
    }
    
    private void transformUniqueId(MatmFlightExtensions matm, GlobalGufiFlight gufiFlight)
    {
        if(matm.getAsdexExtension() != null && matm.getAsdexExtension().getTrackId() != null)
        {
            gufiFlight.setUniqueIdAsdex(matm.getAsdexExtension().getTrackId().longValue());
        }
        else if(matm.getCat11Extension() != null && matm.getCat11Extension().getTrackId() != null)    //need to add a new asterix id
        {
            gufiFlight.setUniqueIdAsdex(matm.getCat11Extension().getTrackId().longValue());
        }
        else if(matm.getCat62Extension() != null && matm.getCat62Extension().getTrackId() != null)    //need to add a new asterix id
        {
            gufiFlight.setUniqueIdAsdex(matm.getCat62Extension().getTrackId().longValue());
        }
        else if(( matm.getMatmAirlineMessageExtension() != null ) && 
                ( matm.getMatmAirlineMessageExtension().getDataSource() != null ) &&
                ( matm.getMatmAirlineMessageExtension().getDataSource().equals( AirlineDataSource.FLIGHTHUB.name() ) ||
                  matm.getMatmAirlineMessageExtension().getDataSource().equals( AirlineDataSource.FLIGHTHUB_EOBT.name() )))
        {
            gufiFlight.setUniqueIdFlightHub( matm.getMatmAirlineMessageExtension().getSourceId() );
        }
    }
    
    private TimeAndAccuracy latestDepartureTime(MatmFlight matm)
    {
        Date result = null;
        
        // Actual runway time
        if(matm.getDepartureRunwayActualTime() != null && !matm.getDepartureRunwayActualTime().isNil())
            result = matm.getDepartureRunwayActualTime().getValue();
        
        if(result != null)
            return new TimeAndAccuracy( result, true );     
        
        // Estimated runway time
        result = matm.getDepartureRunwayEstimatedTime();
        if(result != null)
            return new TimeAndAccuracy( result, true );        

        // EDCT
        if(( matm.getEstimatedDepartureClearanceTime() != null ) && !matm.getEstimatedDepartureClearanceTime().isNil() )
        {
            result = matm.getEstimatedDepartureClearanceTime().getValue();
            if(result != null)
                return new TimeAndAccuracy( result, true );          
        }
        
        // Actual gate time
        result = matm.getDepartureStandActualTime() == null ? null : matm.getDepartureStandActualTime().getValue();
        if(result != null)
            return new TimeAndAccuracy( result, true );  
        
        // Estimated gate time
        result = matm.getDepartureStandEstimatedTime();
        if(result != null)
            return new TimeAndAccuracy( result, true );
        
        // Proposed times
        result = matm.getDepartureRunwayProposedTime();
        if(result != null)
            return new TimeAndAccuracy( result, true );

        result = matm.getDepartureStandProposedTime();
        if(result != null)
            return new TimeAndAccuracy( result, true );

        // Scheduled times
        result = matm.getDepartureRunwayScheduledTime();
        if(result != null)
            return new TimeAndAccuracy( result, false );
        
        result = matm.getDepartureStandScheduledTime();
        if(result != null)
            return new TimeAndAccuracy( result, false );
        
        return null;
    }
    
    private TimeAndAccuracy latestArrivalTime(MatmFlight matm)
    {
        Date result = null;

        // Actual runway time
        result = matm.getArrivalRunwayActualTime();
        if(result != null)
            return new TimeAndAccuracy( result, true );      
        
        // Estimated runway time
        result = matm.getArrivalRunwayEstimatedTime();
        if(result != null)
            return new TimeAndAccuracy( result, true );

        // Controlled runway time
        if(( matm.getArrivalRunwayControlledTime() != null ) && !matm.getArrivalRunwayControlledTime().isNil() )
        {        
            result = matm.getArrivalRunwayControlledTime().getValue();
            if(result != null)
                return new TimeAndAccuracy( result, true );
        }
        
        // Actual gate time
        result = matm.getArrivalStandActualTime();
        if(result != null)
            return new TimeAndAccuracy( result, true );           
        
        // Estimated gate time
        result = matm.getArrivalStandEstimatedTime();
        if(result != null)
            return new TimeAndAccuracy( result, true );
        
        // Controlled gate time
        if(( matm.getArrivalStandControlledTime() != null ) && !matm.getArrivalStandControlledTime().isNil() )
        {           
            result = matm.getArrivalStandControlledTime().getValue();
            if(result != null)
                return new TimeAndAccuracy( result, true );        
        }
        
        // Proposed times
        result = matm.getArrivalRunwayProposedTime();
        if(result != null)
            return new TimeAndAccuracy( result, true );        

        result = matm.getArrivalStandProposedTime();
        if(result != null)
            return new TimeAndAccuracy( result, true );      
        
        // Scheduled times
        result = matm.getArrivalRunwayScheduledTime();
        if(result != null)
            return new TimeAndAccuracy( result, false );
        
        result = matm.getArrivalStandScheduledTime();
        if(result != null)
            return new TimeAndAccuracy( result, false );
        
        return null;
    }
    
    private void transformMessageType(MatmFlight matm, GlobalGufiFlight gufiFlight)
    {
        FuserSource source = null;
        if (FUSER_CLIENT_SYNC.equals(matm.getLastUpdateSource()))
        {
            source = FuserSource.SYNC;
        }
        else
        {
            source = FuserSource.fromValue( matm.getLastUpdateSource() );
        }
                
        switch( source )
        {
            case ASDEX:
                gufiFlight.setLastMessageType( matm.getLastUpdateSource() );
                break;
                
            case AIRLINE:
                gufiFlight.setLastMessageType( matm.getLastUpdateSource() );
                gufiFlight.setLastMessageSubType( matm.getSystemId() );
                break;
                
            case FLIGHTHUB_POSITION:
                gufiFlight.setLastMessageType( matm.getLastUpdateSource() );
                break;
                
            case IDAC:
                gufiFlight.setLastMessageType( "TMA" );
                gufiFlight.setLastMessageSubType( "FlightPlanInformation" );
                gufiFlight.setLastMessageSubTypeSource( "IDAC" );
                break;
                
            case SFDPS:
                gufiFlight.setLastMessageType( matm.getLastUpdateSource() );
                gufiFlight.setLastMessageSubType( matm.getSystemId() );
                break;
                
            case TFM:
                TfmExtension tfm = matm.getExtensions().getTfmExtension();
                gufiFlight.setLastMessageType( matm.getLastUpdateSource() );
                gufiFlight.setLastMessageSubType( tfm.getMessageType().value() );
                gufiFlight.setLastMessageSubTypeSource( tfm.getMessageTrigger().value() );
                break;
                
            case TFM_TFDM:
                TfmTfdmExtension tfdm = matm.getExtensions().getTfmTfdmExtension();
                gufiFlight.setLastMessageType( "TFM" );
                gufiFlight.setLastMessageSubType( tfdm.getMessageType().value() );
                gufiFlight.setLastMessageSubTypeSource( "TFM_TFDM_MSG" );
                break;
                
            case TMA:
                gufiFlight.setLastMessageType( matm.getLastUpdateSource() );
                gufiFlight.setLastMessageSubTypeSource( matm.getSystemId() );
                break;
                
            case AEFS:
                gufiFlight.setLastMessageType( matm.getLastUpdateSource() );
                gufiFlight.setLastMessageSubType( matm.getSystemId() );                
                break;
                
            case FMC:
            case SYNC:
            case AMS:
                gufiFlight.setLastMessageType("MATM");
                gufiFlight.setLastMessageSubType(source.value());
                break;                
                
            default:
                log.error( "Unsupported message type: " + source );
        }
    }
    
    private static String airportCode(Aerodrome aerodrome)
    {
        if(aerodrome == null)
            return null;
        if(aerodrome.getIataName() != null)
            return aerodrome.getIataName();
        else return aerodrome.getIcaoName();
    }
    
    private class TimeAndAccuracy
    {
        private boolean isAccurate = false;
        private Date time = null;
        
        TimeAndAccuracy( Date time, boolean isAccurate )
        {
            this.time = time;
            this.isAccurate = isAccurate;
        }

        public Date getTime()
        {
            return time;
        }

        public boolean isAccurate()
        {
            return isAccurate;
        }
    }
}
