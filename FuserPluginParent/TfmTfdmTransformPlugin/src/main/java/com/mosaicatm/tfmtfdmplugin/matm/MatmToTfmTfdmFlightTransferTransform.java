package com.mosaicatm.tfmtfdmplugin.matm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
        
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.common.CancelledType;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.TfmTfdmExtension;
import com.mosaicatm.matmdata.flight.extension.tfmcomm.TfmSensitivityType;

import gov.nasa.atm.tfm.tfdm.transfer.TfmTfdmFlightTransfer;
import gov.nasa.atm.tfm.tfdm.transfer.TfmTfdmMessageType;
import gov.nasa.atm.tfm.tfdm.transfer.TfmTfdmSensitivityReasonType;
import gov.nasa.atm.tfm.tfdm.transfer.TfmTfdmSensitivityType;

public class MatmToTfmTfdmFlightTransferTransform
implements Transformer <TfmTfdmFlightTransfer, MatmFlight>
{
    private final Log log = LogFactory.getLog( getClass() );
    
    @Override
    public TfmTfdmFlightTransfer transform( MatmFlight matm )
    {
        if( matm == null )
        {
            return( null );
        }
        
        TfmTfdmFlightTransfer transfer = new TfmTfdmFlightTransfer();
        
        transformMessageElements( matm, transfer );
        transformFlightIdentifiers( matm, transfer );
        transformAerodromes( matm, transfer );
        transformArrivalElements( matm, transfer );
        transformDepartureElements( matm, transfer );
        transformCancellation( matm, transfer );
        transformExtension( matm, transfer );
        
        return( transfer );
    }

    private void transformMessageElements( MatmFlight matm, TfmTfdmFlightTransfer transfer )
    {
        transfer.setSourceFacility( matm.getSystemId() );
        transfer.setSourceTimeStamp( matm.getTimestampSource() );
        transfer.setTimestampSourceReceived( matm.getTimestampSourceReceived() );
        transfer.setTimestampSourceProcessed( matm.getTimestampSourceProcessed() );
    }    
    
    private void transformFlightIdentifiers( MatmFlight matm, TfmTfdmFlightTransfer transfer )
    {
        transfer.setAcid( matm.getAcid() );
        // Sometimes this has the major, not the carrier. This is inconsistent
        // with the rest of STBO.        
        //transfer.setAirline( matm.getCarrier() );
        transfer.setGufi( matm.getGufi() );
        transfer.setAcftRegistrationNumber( matm.getAircraftRegistration() );
    }
    
    private void transformAerodromes( MatmFlight matm, TfmTfdmFlightTransfer transfer )
    {
        if( matm.getDepartureAerodrome() != null )
        {
            transfer.setDepArpt( matm.getDepartureAerodrome().getIataName() );
            transfer.setDepArptIcao( matm.getDepartureAerodrome().getIcaoName() );
        }
        
        if( matm.getArrivalAerodrome() != null )
        {
            transfer.setArrArpt( matm.getArrivalAerodrome().getIataName() );
            transfer.setArrArptIcao( matm.getArrivalAerodrome().getIcaoName() );
        }        
    }
    
    private void transformDepartureElements( MatmFlight matm, TfmTfdmFlightTransfer transfer )
    {
        if(matm.getDepartureRunwayActualTime() != null)
            transfer.setActualTakeOffTime( matm.getDepartureRunwayActualTime().getValue() );
        
        transfer.setEarliestOffBlockTime( matm.getDepartureStandEarliestTime() );
        transfer.setDepStandAssignment( matm.getDepartureStandAirline() );
        transfer.setIntendedDepSpot( matm.getDepartureSpotAirline() );
    }
    
    private void transformArrivalElements( MatmFlight matm, TfmTfdmFlightTransfer transfer )
    {
        transfer.setActualLandingTime( matm.getArrivalRunwayActualTime() );
        transfer.setActualInBlockTime( matm.getArrivalStandActualTime() );
        transfer.setArrStandAssignment( matm.getArrivalStandAirline() );
        transfer.setIntendedArrSpot( matm.getArrivalSpotAirline() );        
    }
    
    private void transformCancellation( MatmFlight matm, TfmTfdmFlightTransfer transfer )
    {
        if( matm.getCancelled() == CancelledType.CANCELLED )
        {
            transfer.setFlightCancel( true );
        }
    }
    
    private void transformExtension( MatmFlight matm, TfmTfdmFlightTransfer transfer )
    {
        if(( matm.getExtensions() != null ) && 
                ( matm.getExtensions().getTfmTfdmExtension() != null ))
        {
            TfmTfdmExtension tfm_tfdm_ext = matm.getExtensions().getTfmTfdmExtension();

            transfer.setArrStandAssignment( tfm_tfdm_ext.getArrStandAssignment() );
            transfer.setDepStandAssignment( tfm_tfdm_ext.getDepStandAssignment() );    
            transfer.setFlightRef( tfm_tfdm_ext.getFlightRef() );
            transfer.setIntendedArrSpot( tfm_tfdm_ext.getIntendedArrSpot() );
            transfer.setIntendedDeiceLocation( tfm_tfdm_ext.getIntendedDeiceLocation() );
            transfer.setIntendedDepSpot( tfm_tfdm_ext.getIntendedDepSpot() );    
            transfer.setMajorCarrier( tfm_tfdm_ext.getMajorCarrier() );    
            transfer.setSource( tfm_tfdm_ext.getSource() );
            transfer.setSourceFacility( tfm_tfdm_ext.getSourceFacility() );

            transfer.setActualInBlockTime( tfm_tfdm_ext.getActualInBlockTime() );
            transfer.setActualLandingTime( tfm_tfdm_ext.getActualLandingTime() );
            transfer.setActualOffBlockTime( tfm_tfdm_ext.getActualOffBlockTime() );
            transfer.setActualTakeOffTime( tfm_tfdm_ext.getActualTakeOffTime() );
            transfer.setEarliestOffBlockTime( tfm_tfdm_ext.getEarliestOffBlockTime() );    
            transfer.setInitialOffBlockTime( tfm_tfdm_ext.getInitialOffBlockTime() );
            transfer.setTargetMAEntryTime( tfm_tfdm_ext.getTargetMAEntryTime() );
            transfer.setTargetOffBlockTime( tfm_tfdm_ext.getTargetOffBlockTime() );
            transfer.setTargetTakeOffTime( tfm_tfdm_ext.getTargetTakeOffTime() ); 
            transfer.setProjectedWheelsUpTime( tfm_tfdm_ext.getProjectedWheelsUpTime() );    

            transfer.setArrIntentToHoldInMove( tfm_tfdm_ext.isArrIntentToHoldInMove() );
            transfer.setArrIntentToHoldNonMove( tfm_tfdm_ext.isArrIntentToHoldNonMove() );
            transfer.setArrStandAvailable( tfm_tfdm_ext.isArrStandAvailable() );
            transfer.setFlightCancel( tfm_tfdm_ext.isCancelled() );
            transfer.setCdmPart( tfm_tfdm_ext.isCdmParticipant() );
            transfer.setDepIntentToHoldInMove( tfm_tfdm_ext.isDepIntentToHoldInMove() );
            transfer.setDepIntentToHoldNonMove( tfm_tfdm_ext.isDepIntentToHoldNonMove() );
            transfer.setDepReadiness( tfm_tfdm_ext.isDepReadiness() );
            transfer.setIntentReturnToGate( tfm_tfdm_ext.isIntentReturnToGate() );
            transfer.setIntentToBeDeiced( tfm_tfdm_ext.isIntentToBeDeiced() );    
            transfer.setTmatMarkedForSub( tfm_tfdm_ext.isTmatMarkedForSub() );    
            transfer.setTmatRelinquish( tfm_tfdm_ext.isTmatRelinquish() );

            if( tfm_tfdm_ext.getMessageType() != null )
            {
                transfer.setMsgType( TfmTfdmMessageType.fromValue( tfm_tfdm_ext.getMessageType().value() ));
            }
            
            if( tfm_tfdm_ext.getSensitivity() != null )
            {
                transfer.setSensitivity( TfmTfdmSensitivityType.fromValue( tfm_tfdm_ext.getSensitivity().value() ));
            }

            if( tfm_tfdm_ext.getSensitivityReason() != null )
            {
                if(( tfm_tfdm_ext.getSensitivity() == null ) || ( tfm_tfdm_ext.getSensitivity() == TfmSensitivityType.A ))
                {
                    log.error( "Setting sensitiviy reason requires sensitivy to be D or R." );
                }            
                else
                {                
                    transfer.setSensitivityReason( TfmTfdmSensitivityReasonType.fromValue( tfm_tfdm_ext.getSensitivityReason().value() ));
                }
            }            

            if(( tfm_tfdm_ext.getAccDepRwyList() != null ) && !tfm_tfdm_ext.getAccDepRwyList().isEmpty() )
            {
                transfer.setAccDepRwyList( tfm_tfdm_ext.getAccDepRwyList() );
            }
            if(( tfm_tfdm_ext.getUnaccDepRwyList() != null ) && !tfm_tfdm_ext.getUnaccDepRwyList().isEmpty() )
            {
                transfer.setUnaccDepRwyList( tfm_tfdm_ext.getUnaccDepRwyList() );
            }   
        }
    }
}
