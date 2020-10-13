package com.mosaicatm.tfmplugin.matm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.CancelledType;
import com.mosaicatm.matmdata.common.EngineClass;
import com.mosaicatm.matmdata.common.FlightType;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.MeteredTime;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import com.mosaicatm.matmdata.flight.extension.LatLonExtension;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.TfmControlIndicatorType;
import com.mosaicatm.matmdata.flight.extension.TfmDiversionIndicatorType;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;
import com.mosaicatm.matmdata.flight.extension.TfmMessageTriggerType;
import com.mosaicatm.matmdata.flight.extension.TfmMessageTypeType;
import com.mosaicatm.matmdata.flight.extension.TfmsFlightTraversalExtension;
import com.mosaicatm.matmdata.flight.extension.TraversalExtensionElement;
import com.mosaicatm.matmdata.flight.extension.WaypointTraversalExtensionElement;
import com.mosaicatm.matmdata.flight.extension.tfmcomm.TfmSensitivityReasonType;
import com.mosaicatm.matmdata.flight.extension.tfmcomm.TfmSensitivityType;
import com.mosaicatm.tfm.thick.flight.mtfms.data.TfmsFlightTransfer;
import com.mosaicatm.tfm.thick.flight.mtfms.data.TfmsFlightTraversalData;
import com.mosaicatm.tfm.thick.flight.mtfms.data.TraversalElement;
import com.mosaicatm.tfm.thick.flight.mtfms.data.WaypointTraversalElement;
import com.mosaicatm.tfmplugin.TfmsSensitivityUtil;

public class TfmsFlightTransferToMatmTransform
implements Transformer <MatmFlight, TfmsFlightTransfer>
{
    private final Log log = LogFactory.getLog( getClass() );
    
    private final ObjectFactory objectFactory = new ObjectFactory();
    private final com.mosaicatm.matmdata.flight.extension.ObjectFactory extObjectFactory = 
            new com.mosaicatm.matmdata.flight.extension.ObjectFactory();
    
    private boolean includeFixTraversalData = false;
    private boolean includeWaypointTraversalData = false;
    private boolean includeCenterTraversalData = false;
    private boolean includeSectorTraversalData = false;

    @Override
    public MatmFlight transform(TfmsFlightTransfer transfer) 
    {
        if (transfer == null)
            return null;
        
        MatmFlight flight = new MatmFlight();    
        flight.setLastUpdateSource(FuserSource.TFM.value());
        flight.setSystemId( FuserSource.TFM.value() );
        if( transfer.getMessageType() != null )
            flight.setSystemId( FuserSource.TFM.value() + "-" + transfer.getMessageType());
        else
            flight.setSystemId( FuserSource.TFM.value() );
        
        flight.setTimestampSource(transfer.getTimeStamp());
        flight.setTimestampSourceReceived(transfer.getTimestampSourceReceived());
        flight.setTimestampSourceProcessed(transfer.getTimestampSourceProcessed());
        MatmFlightExtensions extensions = new MatmFlightExtensions();
        TfmExtension tfm = new TfmExtension();
        extensions.setTfmExtension(tfm);
        flight.setExtensions(extensions);
        
        transformFlightIdentifiers (transfer, flight);
        transformIdentificationInfo (transfer, flight);
        transformFlightPlanInfo (transfer, flight);
        transformPositionData (transfer, flight);
        transformFlightTimes (transfer, flight);    
        transformMisc (transfer,flight);
        transformWakeTurbulenceCategory(transfer,flight);
        
        
        transformExtension (transfer, tfm);
        
        //These requires the extension data to be set first
        transformCancelledFromExtension(transfer, tfm, flight);
        transformControlCancelledFromExtension(tfm, flight);        
        transformSensitivityFromExtension(tfm, flight); 
        transformFiledScheduledFromExtension(tfm, flight);
        
        if(isIncludeTraversalData() && transfer.getFlightTraversalData() != null) {
            TfmsFlightTraversalExtension traversalExtension = new TfmsFlightTraversalExtension();
            traversalExtension.setSystemId(flight.getSystemId());
            transformFlightTraversalExtension (transfer, traversalExtension);
            extensions.setTfmsFlightTraversalExtension(traversalExtension);
        }
        
        return flight;
    }

    public boolean isIncludeTraversalData()
    {
        return includeFixTraversalData || 
                includeWaypointTraversalData || 
                includeCenterTraversalData || 
                includeSectorTraversalData;
    }
    
    public boolean isIncludeFixTraversalData()
    {
        return includeFixTraversalData;
    }

    public void setIncludeFixTraversalData( boolean includeFixTraversalData )
    {
        log.debug( "Set includeFixTraversalData=" + includeFixTraversalData );
        
        this.includeFixTraversalData = includeFixTraversalData;
    }
    
    public boolean isIncludeWaypointTraversalData()
    {
        return includeWaypointTraversalData;
    }

    public void setIncludeWaypointTraversalData( boolean includeWaypointTraversalData )
    {
        log.debug( "Set includeWaypointTraversalData=" + includeWaypointTraversalData );
        
        this.includeWaypointTraversalData = includeWaypointTraversalData;
    }

    public boolean isIncludeCenterTraversalData()
    {
        return includeCenterTraversalData;
    }

    public void setIncludeCenterTraversalData( boolean includeCenterTraversalData )
    {
        log.debug( "Set includeCenterTraversalData=" + includeCenterTraversalData );
                
        this.includeCenterTraversalData = includeCenterTraversalData;
    }

    public boolean isIncludeSectorTraversalData()
    {        
        return includeSectorTraversalData;
    }

    public void setIncludeSectorTraversalData( boolean includeSectorTraversalData )
    {
        log.debug( "Set includeSectorTraversalData=" + includeSectorTraversalData );
        
        this.includeSectorTraversalData = includeSectorTraversalData;
    }
    
    private void transformFlightTraversalExtension(TfmsFlightTransfer transfer,
        TfmsFlightTraversalExtension traversalExtension) {

        TfmsFlightTraversalData traversalData = transfer.getFlightTraversalData();

        if( includeCenterTraversalData && traversalData.getCenters() != null) {
            traversalExtension.setCenters(transformTraversalCollection(traversalData.getCenters()));
        }

        if( includeFixTraversalData && traversalData.getFixes() != null) {
            traversalExtension.setFixes(transformTraversalCollection(traversalData.getFixes()));
        }
        
        if( includeSectorTraversalData && traversalData.getSectors() != null) {
            traversalExtension.setSectors(transformTraversalCollection(traversalData.getSectors()));
        }

        if( includeWaypointTraversalData && traversalData.getWaypoints() != null) {
            traversalExtension.setWaypoints(transformWaypointTraversalCollection(traversalData.getWaypoints()));
        }
        
    }
    
    private List<TraversalExtensionElement> transformTraversalCollection(List<TraversalElement> traversalElements) {
        
        List<TraversalExtensionElement> extElements = new ArrayList<TraversalExtensionElement>();
        TraversalExtensionElement extElement = null;
        for(TraversalElement transferElement : traversalElements) {
            extElement = new TraversalExtensionElement();
            extElement.setName(transferElement.getName());
            extElement.setElapsedSeconds(transferElement.getElapsedSeconds());
            extElement.setSequenceNumber(transferElement.getSequenceNumber());
            extElement.setTraversalTime(transferElement.getTraversalTime());
            extElements.add(extElement);
        }
        return extElements;
    }

    private List<WaypointTraversalExtensionElement> transformWaypointTraversalCollection(List<WaypointTraversalElement> traversalElements) {
        
        List<WaypointTraversalExtensionElement> extElements = new ArrayList<WaypointTraversalExtensionElement>();
        WaypointTraversalExtensionElement extElement = null;
        for(WaypointTraversalElement transferElement : traversalElements) {
            
            extElement = new WaypointTraversalExtensionElement();
            extElement.setName(transferElement.getName());
            extElement.setElapsedSeconds(transferElement.getElapsedSeconds());
            extElement.setSequenceNumber(transferElement.getSequenceNumber());
            extElement.setTraversalTime(transferElement.getTraversalTime());
            
            if(transferElement.getPosition() != null) {
                LatLonExtension extLatLon = new LatLonExtension(); 
                extLatLon.setLatDeg(transferElement.getPosition().getLatDeg());
                extLatLon.setLonDeg(transferElement.getPosition().getLonDeg());
                extElement.setPosition(extLatLon);
            }
            
            extElements.add(extElement);
        }
        return extElements;
    }
    

    private void transformFlightIdentifiers (TfmsFlightTransfer transfer, MatmFlight matm)
    {
        if (transfer.getAircraftType() != null)
            matm.setAircraftType(transfer.getAircraftType());
        
        if (transfer.getPhysicalClass() != null)
            matm.setAircraftEngineClass( EngineClass.fromValue( transfer.getPhysicalClass() ));

        if (transfer.getAcEqpSuffix() != null)
            matm.setAircraftEquipmentQualifier(transfer.getAcEqpSuffix());        
        
        if (transfer.getAcid() != null)
            matm.setAcid(transfer.getAcid());
        
        if (transfer.getBeaconCode() != null)
            matm.setBeaconCode(String.valueOf(transfer.getBeaconCode()));
        
        if (transfer.getGufi() != null)
            matm.setGufi(transfer.getGufi());
    }
    
    private void transformIdentificationInfo (TfmsFlightTransfer transfer, MatmFlight matm)
    {      
        if (transfer.getEramGufi() != null)
            matm.setEramGufi(transfer.getEramGufi());
        
        if (transfer.getComputerId() != null)
            matm.setComputerId(transfer.getComputerId());
        
        if (transfer.getComputerFacility() != null)
            matm.setSourceFacility(transfer.getComputerFacility());
    }
    
    private void transformFlightPlanInfo (TfmsFlightTransfer transfer, MatmFlight matm)
    {
        if (transfer.getRouteString() != null)
            matm.setRouteText(transfer.getRouteString());
        
                
        if (transfer.getDepAirport() != null || transfer.getIcaoDepAirport() != null)
        {
            Aerodrome deptAerodrome = new Aerodrome();
            
            if (transfer.getDepAirport() != null)
                    deptAerodrome.setIataName(transfer.getDepAirport());
            
            if(transfer.getIcaoDepAirport() != null)
                deptAerodrome.setIcaoName(transfer.getIcaoDepAirport());
            
            matm.setDepartureAerodrome(deptAerodrome);
        }

        
        
        if (transfer.getArrAirport() != null || transfer.getIcaoArrAirport() != null)
        {
            Aerodrome arrAerodrome = new Aerodrome();
            
            if(transfer.getArrAirport() != null)
                arrAerodrome.setIataName(transfer.getArrAirport());

            if(transfer.getIcaoArrAirport() != null)
                arrAerodrome.setIcaoName(transfer.getIcaoArrAirport());

            matm.setArrivalAerodrome(arrAerodrome);
        }
        
        if (transfer.getArrFix() != null)
            matm.setArrivalFixSourceData(transfer.getArrFix());
        
        if (transfer.getDepFix() != null)
            matm.setDepartureFixSourceData(transfer.getDepFix());
        
        if( transfer.getAltitudeAssigned() != null )
            matm.setAltitudeAssigned( transfer.getAltitudeAssigned().doubleValue() * 100 );
            
        if( transfer.getAltitudeRequested() != null )
            matm.setAltitudeRequested( transfer.getAltitudeRequested().doubleValue() * 100 );
            
        if( transfer.getAltitudeFiled() != null )
            matm.setAltitudeFiled( transfer.getAltitudeFiled().doubleValue() * 100 );
                    
        if( transfer.getSpeedFiled() != null )            
            matm.setSpeedFiled( transfer.getSpeedFiled().doubleValue() );
    }
    
    private void transformWakeTurbulenceCategory(TfmsFlightTransfer transfer, MatmFlight matm)
    {
        if (transfer.getWeightClass() != null)
        {
        //    WakeTurbulenceCategory category = WakeTurbulenceCategory.fromCode(transfer.getWeightClass());
        //    if(category != null)
            //{
        //        matm.setWakeTurbulenceCategory(category);
        //    }
        //    else
        //    {
                //logger.warn("Unknown weight class found in " + transfer.getWeightClass());
        //    }
        }
            
        
    }
    
    private void transformPositionData (TfmsFlightTransfer transfer, MatmFlight matm)
    {
        Position position = null;
        if (transfer.getCurrPos() != null)
        {
            position = new Position();
            if (transfer.getCurrPos().getLatDeg() != null)
                position.setLatitude(transfer.getCurrPos().getLatDeg());
            
            if (transfer.getCurrPos().getLonDeg() != null)
                position.setLongitude(transfer.getCurrPos().getLonDeg());
            
            if (transfer.getTimeStamp() != null)
                position.setTimestamp(transfer.getTimeStamp());
            
            matm.setPosition(position);
        
        
            // TFMS sends altitude in 100ft increments, so need to multiple by 100
            // to get the true altitude
            if (transfer.getAltitude() != null)
            {
                position.setAltitude(transfer.getAltitude().doubleValue() * 100);
            }        
            
            if (transfer.getSpeed() != null)
            {
                position.setSpeed(transfer.getSpeed().doubleValue());
            }
            
            position.setSource("TFM");
            matm.setPosition(position);
            matm.getExtensions().getTfmExtension().setLastTfmPosition(position);
        }
        
    }
    
    private void transformFlightTimes (TfmsFlightTransfer transfer, MatmFlight matm)
    {
        if (transfer.getTimeStamp() != null)
            matm.setTimestamp(transfer.getTimeStamp());
        
        transformFixTimes (transfer, matm);
        transformArrivalStandTimes (transfer, matm);
        transformDepartureRunwayTimes (transfer, matm);
        transformArrivalRunwayTimes (transfer, matm);
        transformDepartureStandTimes (transfer, matm);
    }
    
    private void transformFixTimes (TfmsFlightTransfer transfer, MatmFlight matm)
    {
        if (transfer.getArrFixEstimatedTime() != null)
            matm.setArrivalFixEstimatedTime(transfer.getArrFixEstimatedTime());    
        
        if (transfer.getDepFixEstimatedTime() != null)
            matm.setDepartureFixEstimatedTime(transfer.getDepFixEstimatedTime());
    }
    
    private void transformDepartureStandTimes (TfmsFlightTransfer transfer, MatmFlight matm)
    {
        if (transfer.getDepartureStandActualTime() != null)
            matm.getExtensions().getTfmExtension().setDepartureStandActualTime(transfer.getDepartureStandActualTime());
        
        if (transfer.getDepartureStandAirlineTime() != null)
            matm.setDepartureStandAirlineTime(transfer.getDepartureStandAirlineTime());
        
        if (transfer.getDepartureStandInitialTime() != null)
            matm.setDepartureStandInitialTime(transfer.getDepartureStandInitialTime());
        
        if (transfer.getDepartureStandProposedTime() != null)
            matm.setDepartureStandProposedTime(transfer.getDepartureStandProposedTime());
    }

    private void transformArrivalRunwayTimes (TfmsFlightTransfer transfer, MatmFlight matm)
    {
        if (transfer.getArrivalRunwayScheduledTime() != null)
            matm.setArrivalRunwayScheduledTime(transfer.getArrivalRunwayScheduledTime());
        
        if (transfer.getArrivalRunwayProposedTime() != null)
            matm.setArrivalRunwayProposedTime(transfer.getArrivalRunwayProposedTime());
        
        if (transfer.getArrivalRunwayEstimatedTime() != null)
            matm.setArrivalRunwayEstimatedTime(transfer.getArrivalRunwayEstimatedTime());
        
        if (transfer.getArrivalRunwayActualTime() != null)
            matm.setArrivalRunwayActualTime(transfer.getArrivalRunwayActualTime());
        
        if (transfer.getArrivalRunwayControlledTime() != null)
            matm.setArrivalRunwayControlledTime( objectFactory.createMatmFlightArrivalRunwayControlledTime( transfer.getArrivalRunwayControlledTime() ));
    }
    
    private void transformDepartureRunwayTimes (TfmsFlightTransfer transfer, MatmFlight matm)
    {
        if (transfer.getDepartureRunwayScheduledTime() != null)
            matm.setDepartureRunwayScheduledTime(transfer.getDepartureRunwayScheduledTime());
        
        if (transfer.getDepartureRunwayProposedTime() != null)
            matm.setDepartureRunwayProposedTime(transfer.getDepartureRunwayProposedTime());
        
        if (transfer.getDepartureRunwayMeteredTime() != null)
        {
            if(( matm.getDepartureRunwayMeteredTime() == null ) || (( matm.getDepartureRunwayMeteredTime().isNil() )))
            {
                matm.setDepartureRunwayMeteredTime(objectFactory.createMatmFlightDepartureRunwayMeteredTime( new MeteredTime() ));
            }
            
            MeteredTime metered_time = matm.getDepartureRunwayMeteredTime().getValue();
            metered_time.setValue( transfer.getDepartureRunwayMeteredTime());        
        }
        
        if (transfer.getDepartureRunwayEstimatedTime() != null)
            matm.setDepartureRunwayEstimatedTime(transfer.getDepartureRunwayEstimatedTime());
        
        if (transfer.getDepartureRunwayActualTime() != null){
            matm.setDepartureRunwayActualTime(objectFactory.createMatmFlightDepartureRunwayActualTime(
                transfer.getDepartureRunwayActualTime()));
        }
        
        if (transfer.getDepartureRunwayControlledTime() != null) 
        {
            matm.setEstimatedDepartureClearanceTime( objectFactory.createMatmFlightEstimatedDepartureClearanceTime( 
                    transfer.getDepartureRunwayControlledTime() )); 
        }
    }
    
    private void transformArrivalStandTimes (TfmsFlightTransfer transfer, MatmFlight matm)
    {
        if (transfer.getArrivalStandActualTime() != null)
            matm.setArrivalStandActualTime(transfer.getArrivalStandActualTime());
        
        if (transfer.getArrivalStandAirlineTime() != null)
            matm.setArrivalStandAirlineTime(transfer.getArrivalStandAirlineTime());
        
        if (transfer.getArrivalStandProposedTime() != null)
            matm.setArrivalStandProposedTime(transfer.getArrivalStandProposedTime());
    }
    
    private void transformMisc(TfmsFlightTransfer transfer, MatmFlight flight)
    {
        if (transfer.getListFixes() != null && !transfer.getListFixes().isEmpty()){
            List<String> fixes = new ArrayList<>();
            fixes.addAll(transfer.getListFixes());
            flight.setFixList(fixes);
        }
        
        if (transfer.getListSectors() != null && !transfer.getListSectors().isEmpty()){
            List<String> sectors = new ArrayList<>();
            sectors.addAll(transfer.getListSectors());
            flight.setSectorList(sectors);
        }
        
        if (transfer.getListCenters() != null && !transfer.getListCenters().isEmpty()){
            List<String> centers = new ArrayList<>();
            centers.addAll(transfer.getListCenters());
            flight.setCenterList(centers);
        }
    }
    
    private void transformExtension (TfmsFlightTransfer transfer, TfmExtension tfm)
    {
        if (transfer.getFlightIndex() != null)
            tfm.setFlightRef(transfer.getFlightIndex());
        
        if (transfer.getMessageType() != null)
        {
            try
            {
                tfm.setMessageType(TfmMessageTypeType.valueOf(transfer.getMessageType()));
            }
            catch( IllegalArgumentException ex )
            {
                log.error( "Unhandled message type enum value: " + transfer.getMessageType() );
            }
        }
        
        if (transfer.getFlightCreationTime() != null)
            tfm.setFlightCreationTime(transfer.getFlightCreationTime());        
        
        if (transfer.getMessageTrigger() != null)
        {
            try
            {
                tfm.setMessageTrigger(TfmMessageTriggerType.valueOf(transfer.getMessageTrigger()));
            }
            catch( IllegalArgumentException ex )
            {
                log.error( "Unhandled message trigger enum value: " + transfer.getMessageTrigger() );
            }            
        }   
        
        if (transfer.getSensitivity() != null)
        {
            try
            {
                tfm.setSensitivity(TfmSensitivityType.valueOf(transfer.getSensitivity()));
            }
            catch( IllegalArgumentException ex )
            {
                log.error( "Unhandled message sensitivity enum value: " + transfer.getSensitivity() );
            }            
        }         

        if(transfer.getSensitivityReason() != null)
        {
            try
            {
                tfm.setSensitivityReason(TfmSensitivityReasonType.valueOf(transfer.getSensitivityReason()));
            }
            catch( IllegalArgumentException ex )
            {
                log.error( "Unhandled message sensitivity reason enum value: " + transfer.getSensitivityReason() );
            }             
        }
        
        if(transfer.getCanceled() != null)
            tfm.setCanceled(transfer.getCanceled());
        
        if (transfer.getCdmParticipant() != null)
            tfm.setCdmParticipant( transfer.getCdmParticipant() );
        
        if (transfer.getDepartureStandAirlineTime() != null)
            tfm.setDepartureStandAirlineTime( transfer.getDepartureStandAirlineTime() );

        if (transfer.getDepartureRunwayAirlineTime() != null)
            tfm.setDepartureRunwayAirlineTime( transfer.getDepartureRunwayAirlineTime() );
        
        if (transfer.getArrivalRunwayAirlineTime() != null)
            tfm.setArrivalRunwayAirlineTime( transfer.getArrivalRunwayAirlineTime() );

        if (transfer.getArrivalStandAirlineTime() != null)
            tfm.setArrivalStandAirlineTime( transfer.getArrivalStandAirlineTime() );        

        if (transfer.getDepartureRunwayOriginalControlledTime() != null)
            tfm.setDepartureRunwayOriginalControlledTime( transfer.getDepartureRunwayOriginalControlledTime() ); 
        
        if (transfer.getArrivalRunwayOriginalControlledTime() != null)
            tfm.setArrivalRunwayOriginalControlledTime( transfer.getArrivalRunwayOriginalControlledTime() );   
        
        if (transfer.getControlElement() != null)
            tfm.setControlElement( extObjectFactory.createTfmExtensionControlElement( transfer.getControlElement() )); 

        if (transfer.getControlIndicator() != null)
        {
            try
            {
                tfm.setControlIndicator(TfmControlIndicatorType.valueOf(transfer.getControlIndicator()));
            }
            catch( IllegalArgumentException ex )
            {
                log.error( "Unhandled control indicator enum value: " + transfer.getControlIndicator() );
            }            
        }             

        if (transfer.getDiversionIndicator() != null)
        {
            try
            {
                tfm.setDiversionIndicator(TfmDiversionIndicatorType.valueOf(transfer.getDiversionIndicator()));
                tfm.setDiversion( tfm.getDiversionIndicator() != TfmDiversionIndicatorType.NO_DIVERSION );
            }
            catch( IllegalArgumentException ex )
            {
                log.error( "Unhandled diversion enum value: " + transfer.getDiversionIndicator() );
            }            
        }             

        if (transfer.getDiversionCancelFlightIndex() != null)
            tfm.setDiversionCancelFlightRef( transfer.getDiversionCancelFlightIndex() );
       
        if (transfer.getDiversionCancelNewFlightIndex() != null)
            tfm.setDiversionCancelNewFlightRef( transfer.getDiversionCancelNewFlightIndex() );        

        if (transfer.getComputerFacility() != null)
            tfm.setSourceFacility( transfer.getComputerFacility() );         
        
        if (transfer.getComputerId() != null)
            tfm.setComputerId( transfer.getComputerId() ); 
        
//        if (transfer.getArrFlag() != null)
//            tfm.setArrivalFlag (transfer.getArrFlag());
//        
//        if (transfer.getDepFlag() != null)
//            tfm.setDepartureFlag (transfer.getDepFlag());
//        
//        if (transfer.getAltitudeType() != null)
//            tfm.setAltitudeType(transfer.getAltitudeType());
//        
//        if (transfer.getAcEqpPrefix() != null)
//            tfm.setEquipmentPrefix(transfer.getAcEqpPrefix()); 
//        
//        if (transfer.getAcEqpSuffix() != null)
//            tfm.setEquipmentSuffix(transfer.getAcEqpSuffix());
//        
//        if (transfer.getFlightStatus() != null)
//            tfm.setFlightStatus(transfer.getFlightStatus());
//        
        
//        if (transfer.getNumAircraft() != null)
//            tfm.setNumAircraft(transfer.getNumAircraft());
//        
//        if (transfer.getPhysicalClass() != null)
//            tfm.setPhysicalClass(transfer.getPhysicalClass());
//        
//        if (transfer.getSourceMsg() != null)
//            tfm.setSourceMsg(transfer.getSourceMsg());
//        
//        if (transfer.getUserClass() != null)
//            tfm.setUserClass(transfer.getUserClass());
    }
    
    
    private void transformCancelledFromExtension(TfmsFlightTransfer transfer, 
            TfmExtension tfm, MatmFlight flight)
    {
        if(( tfm.isCanceled() != null ) && 
                ( tfm.getMessageTrigger() == TfmMessageTriggerType.FD_FLIGHT_CANCEL_MSG ))
        {
            if( tfm.isCanceled() )
            {
                flight.setCancelled( CancelledType.CANCELLED );
                
                if (flight.getTimestamp() != null)
                {
                    flight.setCancelledTime( transfer.getTimeStamp() );
                }                
            }
            else
            {
                flight.setCancelled( CancelledType.NOT_CANCELLED );
            }
        }
    }    
    
    private void transformControlCancelledFromExtension(TfmExtension tfm, MatmFlight flight)
    {
        if( tfm.getControlIndicator() == TfmControlIndicatorType.CONTROL_CANCELED )
        {
            if( flight.getEstimatedDepartureClearanceTime() == null )
            {
                flight.setEstimatedDepartureClearanceTime( objectFactory.createMatmFlightEstimatedDepartureClearanceTime( null ));
            }
            if( flight.getArrivalRunwayControlledTime() == null )
            {
                flight.setArrivalRunwayControlledTime( objectFactory.createMatmFlightArrivalRunwayControlledTime( null ));
            }            
            if( tfm.getControlElement() == null )
            {
                tfm.setControlElement( extObjectFactory.createTfmExtensionControlElement( null ));
            }     
        }        
    }
    
    private void transformSensitivityFromExtension(TfmExtension tfm, MatmFlight flight)
    {
        flight.setSensitiveDataExternal( TfmsSensitivityUtil.isSensitiveData( tfm.getSensitivity(), tfm.getSensitivityReason() ));
    }    
    
    private void transformFiledScheduledFromExtension(TfmExtension tfm, MatmFlight flight)
    {
        switch( tfm.getMessageType() )
        {
            case FLIGHT_SCHEDULE_ACTIVATE:
                flight.setScheduledFlight( true );
                flight.setFlightType( FlightType.SCHEDULED_AIR_TRANSPORT );
                break;
                
            case FLIGHT_PLAN_INFORMATION:
            case FLIGHT_PLAN_AMENDMENT_INFORMATION:
                flight.setFiledFlight( true );
                break;                
        }
    }
}
