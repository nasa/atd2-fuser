package com.mosaicatm.tfmplugin.matm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;
import com.mosaicatm.matmdata.flight.extension.tfmcomm.TfmSensitivityType;
import com.mosaicatm.tfm.thick.flight.mtfms.data.LatLon;
import com.mosaicatm.tfm.thick.flight.mtfms.data.TfmsFlightTransfer;

public class MatmToTfmsFlightTransferTransform
implements Transformer <TfmsFlightTransfer, MatmFlight>
{
    private final Log log = LogFactory.getLog( getClass() );
    
    @Override
    public TfmsFlightTransfer transform(MatmFlight matm)
    {
        if (matm == null)
            return null;
        
        TfmsFlightTransfer transfer = new TfmsFlightTransfer ();
        
        transformFlightIdentifiers (matm, transfer);
        transformFlightPlanInfo (matm, transfer);
        transformPositionData (matm, transfer);
        transformFlightTimes (matm, transfer);
        transformMisc (matm,transfer);

        
        transformExtension (matm, transfer);
        
        return transfer;
    }

    private void transformFlightIdentifiers (MatmFlight matm, TfmsFlightTransfer transfer)
    {
        if (matm.getAircraftType() != null)
            transfer.setAircraftType(matm.getAircraftType());

        if (matm.getAircraftEngineClass() != null )
            transfer.setPhysicalClass( matm.getAircraftEngineClass().value() );

        if (matm.getAircraftEquipmentQualifier() != null)
            transfer.setAcEqpSuffix(matm.getAircraftEquipmentQualifier());
        
        if (matm.getAcid() != null)
            transfer.setAcid(matm.getAcid());
        
        if (matm.getBeaconCode() != null)
            transfer.setBeaconCode(Integer.valueOf(matm.getBeaconCode()));
        
        if (matm.getGufi() != null)
            transfer.setGufi(matm.getGufi());
    }
    
    private void transformFlightPlanInfo (MatmFlight matm, TfmsFlightTransfer transfer)
    {
        if (matm.getRouteText() != null)
            transfer.setRouteString(matm.getRouteText());
        
        //if (matm.getWakeTurbulenceCategory() != null)
        //    transfer.setWeightClass(matm.getWakeTurbulenceCategory().getCode());
        
        if (matm.getDepartureAerodrome() != null)
            transfer.setDepAirport(matm.getDepartureAerodrome().getIataName());
        
        if (matm.getArrivalAerodrome() != null)
            transfer.setArrAirport(matm.getArrivalAerodrome().getIataName());
        
        if (matm.getArrivalFixSourceData() != null)
            transfer.setArrFix(matm.getArrivalFixSourceData());
        
        if (matm.getDepartureFixSourceData() != null)
            transfer.setDepFix(matm.getDepartureFixSourceData());
        
        if( matm.getAltitudeAssigned() != null )
            transfer.setAltitudeAssigned((short) ( matm.getAltitudeAssigned().shortValue() / 100 ));
            
        if( matm.getAltitudeRequested() != null )
            transfer.setAltitudeRequested((short) ( matm.getAltitudeRequested().shortValue() / 100 ));
            
        if( matm.getAltitudeFiled() != null )
            transfer.setAltitudeFiled((short) ( matm.getAltitudeFiled().shortValue() / 100 ));
                    
        if( matm.getSpeedFiled() != null )            
            transfer.setSpeedFiled( matm.getSpeedFiled().shortValue() );
    }
    
    private void transformPositionData (MatmFlight matm, TfmsFlightTransfer transfer)
    {
        LatLon position = new LatLon ();        
        
        if(matm.getPosition() != null)
        {
            Position matmPosition = matm.getPosition();
            if (matmPosition.getLatitude() != null)
            position.setLatDeg(matmPosition.getLatitude());
        
            if (matmPosition.getLongitude() != null)
            position.setLonDeg(matmPosition.getLongitude());
        
            transfer.setCurrPos(position);

            if (matmPosition.getAltitude() != null)
                transfer.setAltitude((short) ( matmPosition.getAltitude().shortValue() / 100));            
            
            if (matmPosition.getSpeed() != null)
                transfer.setSpeed(matmPosition.getSpeed().shortValue());
        }
    }
    
    private void transformFlightTimes (MatmFlight matm, TfmsFlightTransfer transfer)
    {
        if (matm.getTimestamp() != null)
            transfer.setTimeStamp(matm.getTimestamp());
        
        transformFixTimes (matm, transfer);
        transformArrivalStandTimes (matm, transfer);
        transformDepartureRunwayTimes (matm, transfer);
        transformArrivalRunwayTimes (matm, transfer);
        transformDepartureStandTimes (matm, transfer);
    }

    private void transformFixTimes (MatmFlight matm, TfmsFlightTransfer transfer)
    {
        if (matm.getArrivalFixEstimatedTime() != null)
            transfer.setArrFixEstimatedTime(matm.getArrivalFixEstimatedTime());
        
        if (matm.getDepartureFixEstimatedTime() != null)
            transfer.setDepFixEstimatedTime(matm.getDepartureFixEstimatedTime());
    }
    
    private void transformDepartureStandTimes (MatmFlight matm, TfmsFlightTransfer transfer)
    {
        if (matm.getExtensions() != null &&
            matm.getExtensions().getTfmExtension() != null)
        {
            transfer.setDepartureStandActualTime(matm.getExtensions().getTfmExtension().getDepartureStandActualTime());
        }
        
        if (matm.getDepartureStandAirlineTime() != null)
            transfer.setDepartureStandAirlineTime(matm.getDepartureStandAirlineTime());
        
        if (matm.getDepartureStandInitialTime() != null)
            transfer.setDepartureStandInitialTime(matm.getDepartureStandInitialTime());
        
        if (matm.getDepartureStandProposedTime() != null)
            transfer.setDepartureStandProposedTime(matm.getDepartureStandProposedTime());
    }
    
    private void transformArrivalRunwayTimes (MatmFlight matm, TfmsFlightTransfer transfer)
    {
        if (matm.getArrivalRunwayScheduledTime() != null)
            transfer.setArrivalRunwayScheduledTime(matm.getArrivalRunwayScheduledTime());        
        
        if (matm.getArrivalRunwayActualTime() != null)
            transfer.setArrivalRunwayActualTime(matm.getArrivalRunwayActualTime());
        
        if (matm.getArrivalRunwayEstimatedTime() != null)
            transfer.setArrivalRunwayEstimatedTime(matm.getArrivalRunwayEstimatedTime());
        
        if (matm.getArrivalRunwayProposedTime() != null)
            transfer.setArrivalRunwayProposedTime(matm.getArrivalRunwayProposedTime());
        
        if(( matm.getArrivalRunwayControlledTime() != null ) && !matm.getArrivalRunwayControlledTime().isNil() && 
                ( matm.getArrivalRunwayControlledTime().getValue() != null ))
            transfer.setArrivalRunwayControlledTime( matm.getArrivalRunwayControlledTime().getValue() );            
    }
    
    private void transformDepartureRunwayTimes (MatmFlight matm, TfmsFlightTransfer transfer)
    {
        if (matm.getDepartureRunwayScheduledTime() != null)
            transfer.setDepartureRunwayScheduledTime(matm.getDepartureRunwayScheduledTime());        
        
        if (matm.getDepartureRunwayActualTime() != null)
            transfer.setDepartureRunwayActualTime(matm.getDepartureRunwayActualTime().getValue());
        
        if (matm.getDepartureRunwayEstimatedTime() != null)
            transfer.setDepartureRunwayEstimatedTime(matm.getDepartureRunwayEstimatedTime());
        
        if (matm.getDepartureRunwayProposedTime() != null)
            transfer.setDepartureRunwayProposedTime(matm.getDepartureRunwayProposedTime());
        
        if (matm.getDepartureRunwayMeteredTime() != null && !matm.getDepartureRunwayMeteredTime().isNil())
        {
            transfer.setDepartureRunwayMeteredTime(matm.getDepartureRunwayMeteredTime().getValue().getValue());    
        }
        
        if(( matm.getEstimatedDepartureClearanceTime() != null ) && !matm.getEstimatedDepartureClearanceTime().isNil() && 
                ( matm.getEstimatedDepartureClearanceTime().getValue() != null ))                
            transfer.setDepartureRunwayControlledTime( matm.getEstimatedDepartureClearanceTime().getValue() );
    }
    
    private void transformArrivalStandTimes (MatmFlight matm, TfmsFlightTransfer transfer)
    {
        if (matm.getArrivalStandActualTime() != null)
            transfer.setArrivalStandActualTime(matm.getArrivalStandActualTime());
        
        if (matm.getArrivalStandAirlineTime() != null)
            transfer.setArrivalStandAirlineTime(matm.getArrivalStandAirlineTime());
        
        if (matm.getArrivalStandProposedTime() != null)
            transfer.setArrivalStandProposedTime(matm.getArrivalStandProposedTime());
    }

    private void transformMisc(MatmFlight matm, TfmsFlightTransfer transfer)
    {
        if (matm.getFixList() != null){
            List<String> fixes = new ArrayList<>();
            fixes.addAll(matm.getFixList());
            transfer.setListFixes(fixes);
        }
        
        if (matm.getSectorList() != null){
            List<String> sectors = new ArrayList<>();
            sectors.addAll(matm.getSectorList());
            transfer.setListSectors(sectors);
        }
        
        if (matm.getCenterList() != null){
            List<String> centers = new ArrayList<>();
            centers.addAll(matm.getCenterList());
            transfer.setListCenters(centers);
        }
    }
    
    private void transformExtension (MatmFlight matm, TfmsFlightTransfer transfer)
    {
        if(matm.getExtensions() != null && matm.getExtensions().getTfmExtension() != null)
        {
            TfmExtension matm_ext = matm.getExtensions().getTfmExtension();
            
            if (matm_ext.getFlightRef() != null)
                transfer.setFlightIndex(matm_ext.getFlightRef());
            
            if (matm_ext.getMessageType() != null)
                transfer.setMessageType(matm_ext.getMessageType().value());    

            if (matm_ext.getFlightCreationTime() != null)
                transfer.setFlightCreationTime(matm_ext.getFlightCreationTime());        

            if (matm_ext.getMessageTrigger() != null)
                transfer.setMessageTrigger(matm_ext.getMessageTrigger().value());

            if (matm_ext.getSensitivity() != null)
                transfer.setSensitivity(matm_ext.getSensitivity().value());

            if (matm_ext.getSensitivityReason() != null)
            {
                if ((matm_ext.getSensitivity() == null) || (matm_ext.getSensitivity() == TfmSensitivityType.A))
                {
                    log.error( "Setting sensitiviy reason requires sensitivy to be D or R." );
                }            
                else
                {
                    transfer.setSensitivityReason(matm_ext.getSensitivityReason().value());            
                }
            }
            
            if (matm_ext.isCanceled() != null)
                transfer.setCanceled(matm_ext.isCanceled());

            if (matm_ext.isCdmParticipant() != null)
                transfer.setCdmParticipant( matm_ext.isCdmParticipant() );

            if (matm_ext.getDepartureRunwayAirlineTime() != null)
                transfer.setDepartureRunwayAirlineTime( matm_ext.getDepartureRunwayAirlineTime() );

            if (matm_ext.getArrivalRunwayAirlineTime() != null)
                transfer.setArrivalRunwayAirlineTime( matm_ext.getArrivalRunwayAirlineTime() );        

            if (matm_ext.getDepartureRunwayOriginalControlledTime() != null)
                transfer.setDepartureRunwayOriginalControlledTime( matm_ext.getDepartureRunwayOriginalControlledTime() ); 

            if (matm_ext.getArrivalRunwayOriginalControlledTime() != null)
                transfer.setArrivalRunwayOriginalControlledTime( matm_ext.getArrivalRunwayOriginalControlledTime() );   

            if(( matm_ext.getControlElement() != null ) && !matm_ext.getControlElement().isNil() && 
                    ( matm_ext.getControlElement().getValue() != null ))                
                transfer.setControlElement( matm_ext.getControlElement().getValue() ); 

            if (matm_ext.getControlIndicator() != null)
                transfer.setControlIndicator( matm_ext.getControlIndicator().value() );         

            if (matm_ext.getDiversionIndicator() != null)
                transfer.setDiversionIndicator( matm_ext.getDiversionIndicator().value() );
                
            if (matm_ext.getDiversionCancelFlightRef() != null)
                transfer.setDiversionCancelFlightIndex( matm_ext.getDiversionCancelFlightRef() );

            if (matm_ext.getDiversionCancelNewFlightRef() != null)
                transfer.setDiversionCancelNewFlightIndex( matm_ext.getDiversionCancelNewFlightRef() ); 
            
            if (matm_ext.getSourceFacility() != null)
                transfer.setComputerFacility( matm_ext.getSourceFacility() );

            if (matm_ext.getComputerId() != null)
                transfer.setComputerId( matm_ext.getComputerId() );             
        }

//        if (tfm.getArrivalFlag() != null)
//            transfer.setArrFlag (tfm.getArrivalFlag());
//        
//        if (tfm.getDepartureFlag() != null)
//            transfer.setDepFlag (tfm.getDepartureFlag());
//        
//        if (tfm.getAltitudeType() != null)
//            transfer.setAltitudeType(tfm.getAltitudeType());
//        
//        if (tfm.getEquipmentPrefix() != null)
//            transfer.setAcEqpPrefix(tfm.getEquipmentPrefix());
//        
//        if (tfm.getEquipmentSuffix() != null)
//            transfer.setAcEqpSuffix(tfm.getEquipmentSuffix());
//        
//        if (tfm.getFlightStatus() != null)
//            transfer.setFlightStatus(tfm.getFlightStatus());
//        
//        
//        if (tfm.getNumAircraft() != null)
//            transfer.setNumAircraft(tfm.getNumAircraft());
//        
//        if (tfm.getPhysicalClass() != null)
//            transfer.setPhysicalClass(tfm.getPhysicalClass());
//        
//        if (tfm.getSourceMsg() != null)
//            transfer.setSourceMsg(tfm.getSourceMsg());
//        
//        if (tfm.getUserClass() != null)
//            transfer.setUserClass(tfm.getUserClass());

        
    }

}
