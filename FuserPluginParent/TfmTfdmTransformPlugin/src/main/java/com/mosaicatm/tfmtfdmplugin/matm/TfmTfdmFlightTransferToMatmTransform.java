package com.mosaicatm.tfmtfdmplugin.matm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.CancelledType;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.TfmTfdmExtension;
import com.mosaicatm.matmdata.flight.extension.TfmTfdmMessageTypeType;
import com.mosaicatm.matmdata.flight.extension.tfmcomm.TfmSensitivityReasonType;
import com.mosaicatm.matmdata.flight.extension.tfmcomm.TfmSensitivityType;
import com.mosaicatm.tfmtfdmplugin.TfmsSensitivityUtil;

import gov.nasa.atm.tfm.tfdm.transfer.TfmTfdmFlightTransfer;

public class TfmTfdmFlightTransferToMatmTransform
implements Transformer <MatmFlight, TfmTfdmFlightTransfer>
{
    private final Log log = LogFactory.getLog( getClass() );
    
    private ObjectFactory objectFactory;
    
    public TfmTfdmFlightTransferToMatmTransform(){
        objectFactory = new ObjectFactory();
    }
    
    @Override
    public MatmFlight transform(TfmTfdmFlightTransfer transfer) 
    {
        if( transfer == null )
        {
            return( null );
        }
        
        MatmFlight matm = new MatmFlight();    

        transformMessageElements( transfer, matm );
        transformFlightIdentifiers( transfer, matm );
        transformAerodromes( transfer, matm );
        transformArrivalElements( transfer, matm );
        transformDepartureElements( transfer, matm );
        transformCancellation( transfer, matm );
        
        MatmFlightExtensions extensions = new MatmFlightExtensions();
        TfmTfdmExtension tfm_tfdm = new TfmTfdmExtension();
        extensions.setTfmTfdmExtension( tfm_tfdm );
        matm.setExtensions( extensions );
        
        transformExtension( transfer, tfm_tfdm );
        transformSensitivity( tfm_tfdm, matm );
        
        return( matm );
    }

    private void transformMessageElements( TfmTfdmFlightTransfer transfer, MatmFlight matm )
    {
        matm.setLastUpdateSource( FuserSource.TFM_TFDM.value() );
        matm.setSystemId( transfer.getSourceFacility() );
        //Major Carrier is for major airline and length must be three
        if (transfer.getSourceFacility() != null &&
            transfer.getSourceFacility().trim().length() == 3)
        {
            matm.setMajorCarrier(transfer.getSourceFacility().trim());
        }
        matm.setTimestamp( transfer.getSourceTimeStamp() );
        matm.setTimestampSource( transfer.getSourceTimeStamp() );
        matm.setTimestampSourceReceived( transfer.getTimestampSourceReceived() );
        matm.setTimestampSourceProcessed( transfer.getTimestampSourceProcessed() );
    }    
    
    private void transformFlightIdentifiers( TfmTfdmFlightTransfer transfer, MatmFlight matm )
    {
        matm.setAcid( transfer.getAcid() );
        
        // Sometimes this has the major, not the carrier. This is inconsistent
        // with the rest of STBO.
        //matm.setCarrier( transfer.getAirline() );
        matm.setGufi( transfer.getGufi() );
        matm.setAircraftRegistration( transfer.getAcftRegistrationNumber() );
    }
    
    private void transformAerodromes( TfmTfdmFlightTransfer transfer, MatmFlight matm )
    {
        if(( transfer.getDepArpt() != null ) || ( transfer.getDepArptIcao() != null ))
        {
            Aerodrome deptAerodrome = new Aerodrome();
            if( transfer.getDepArptIcao() != null )
            {
                deptAerodrome.setIcaoName( transfer.getDepArptIcao() );
            }
            else if( transfer.getDepArpt() != null )
            {
                deptAerodrome.setIataName( transfer.getDepArpt() );
            }            
            matm.setDepartureAerodrome( deptAerodrome );
        }
        
        if(( transfer.getArrArpt() != null ) || ( transfer.getArrArptIcao() != null ))
        {
            Aerodrome arrAerodrome = new Aerodrome();
            if( transfer.getArrArptIcao() != null )
            {
                arrAerodrome.setIcaoName( transfer.getArrArptIcao() );
            }
            else if( transfer.getArrArpt() != null )
            {
                arrAerodrome.setIataName( transfer.getArrArpt() );
            }            
            matm.setArrivalAerodrome( arrAerodrome );
        }
    }
    
    private void transformDepartureElements( TfmTfdmFlightTransfer transfer, MatmFlight matm )
    {
        if(transfer.getActualTakeOffTime() != null){
            matm.setDepartureRunwayActualTime(objectFactory.createMatmFlightDepartureRunwayActualTime(
                transfer.getActualTakeOffTime()));
        }
        // NOTE: The actual off block time is intentionally not copied to 
        //       the departure stand actual time. It is instead copied to the
        //       extension. The extension value will be used by the
        //       DepartureStandActualTimeUpdater to set the departureStandActualTime
        //       if needed
        matm.setDepartureStandEarliestTime( transfer.getEarliestOffBlockTime() );
        matm.setDepartureStandAirline( transfer.getDepStandAssignment() );
        matm.setDepartureSpotAirline( transfer.getIntendedDepSpot() );
    }
    
    private void transformArrivalElements( TfmTfdmFlightTransfer transfer, MatmFlight matm )
    {
        matm.setArrivalRunwayActualTime( transfer.getActualLandingTime() );
        matm.setArrivalStandActualTime( transfer.getActualInBlockTime() );
        matm.setArrivalStandAirline( transfer.getArrStandAssignment() );
        matm.setArrivalSpotAirline( transfer.getIntendedArrSpot() );        
    }
    
    private void transformCancellation( TfmTfdmFlightTransfer transfer, MatmFlight matm )
    {
        if( transfer.getFlightCancel() != null )
        {
            if( transfer.getFlightCancel() )
            {
                matm.setCancelled( CancelledType.CANCELLED );
                matm.setCancelledTime( transfer.getSourceTimeStamp() );
            }
            else
            {
                matm.setCancelled( CancelledType.NOT_CANCELLED );
            }            
        }
    }
    
    private void transformSensitivity( TfmTfdmExtension tfmTfdm, MatmFlight matm )
    {
        matm.setSensitiveDataExternal( TfmsSensitivityUtil.isSensitiveData( tfmTfdm.getSensitivity(), tfmTfdm.getSensitivityReason() ));
    }    
    
    private void transformExtension( TfmTfdmFlightTransfer transfer, TfmTfdmExtension tfmTfdm )
    {
        tfmTfdm.setArrStandAssignment( transfer.getArrStandAssignment() );
        tfmTfdm.setDepStandAssignment( transfer.getDepStandAssignment() );    
        tfmTfdm.setFlightRef( transfer.getFlightRef() );
        tfmTfdm.setIntendedArrSpot( transfer.getIntendedArrSpot() );
        tfmTfdm.setIntendedDeiceLocation( transfer.getIntendedDeiceLocation() );
        tfmTfdm.setIntendedDepSpot( transfer.getIntendedDepSpot() );    
        tfmTfdm.setMajorCarrier( transfer.getMajorCarrier() );    
        tfmTfdm.setSource( transfer.getSource() );
        tfmTfdm.setSourceFacility( transfer.getSourceFacility() );

        tfmTfdm.setActualInBlockTime( transfer.getActualInBlockTime() );
        tfmTfdm.setActualLandingTime( transfer.getActualLandingTime() );
        tfmTfdm.setActualOffBlockTime( transfer.getActualOffBlockTime() );
        tfmTfdm.setActualTakeOffTime( transfer.getActualTakeOffTime() );
        tfmTfdm.setEarliestOffBlockTime( transfer.getEarliestOffBlockTime() );    
        tfmTfdm.setInitialOffBlockTime( transfer.getInitialOffBlockTime() );
        tfmTfdm.setTargetMAEntryTime( transfer.getTargetMAEntryTime() );
        tfmTfdm.setTargetOffBlockTime( transfer.getTargetOffBlockTime() );
        tfmTfdm.setTargetTakeOffTime( transfer.getTargetTakeOffTime() ); 
        tfmTfdm.setProjectedWheelsUpTime( transfer.getProjectedWheelsUpTime() );    

        tfmTfdm.setArrIntentToHoldInMove( transfer.getArrIntentToHoldInMove() );
        tfmTfdm.setArrIntentToHoldNonMove( transfer.getArrIntentToHoldNonMove() );
        tfmTfdm.setArrStandAvailable( transfer.getArrStandAvailable() );
        tfmTfdm.setCancelled( transfer.getFlightCancel() );
        tfmTfdm.setCdmParticipant( transfer.getCdmPart() );
        tfmTfdm.setDepIntentToHoldInMove( transfer.getDepIntentToHoldInMove() );
        tfmTfdm.setDepIntentToHoldNonMove( transfer.getDepIntentToHoldNonMove() );
        tfmTfdm.setDepReadiness( transfer.getDepReadiness() );
        tfmTfdm.setIntentReturnToGate( transfer.getIntentReturnToGate() );
        tfmTfdm.setIntentToBeDeiced( transfer.getIntentToBeDeiced() );    
        tfmTfdm.setTmatMarkedForSub( transfer.getTmatMarkedForSub() );    
        tfmTfdm.setTmatRelinquish( transfer.getTmatRelinquish() );
        
        if( transfer.getMsgType() != null )
        {
            tfmTfdm.setMessageType( TfmTfdmMessageTypeType.fromValue( transfer.getMsgType().value() ));
        }

        if( transfer.getSensitivity() != null )
        {
            tfmTfdm.setSensitivity( TfmSensitivityType.fromValue( transfer.getSensitivity().value() ));
        }

        if( transfer.getSensitivityReason() != null )
        {
            tfmTfdm.setSensitivityReason( TfmSensitivityReasonType.fromValue( transfer.getSensitivityReason().value() ));
        }        
        
        if(( transfer.getAccDepRwyList() != null ) && !transfer.getAccDepRwyList().isEmpty() )
        {
            tfmTfdm.setAccDepRwyList( transfer.getAccDepRwyList() );
        }
        if(( transfer.getUnaccDepRwyList() != null ) && !transfer.getUnaccDepRwyList().isEmpty() )
        {
            tfmTfdm.setUnaccDepRwyList( transfer.getUnaccDepRwyList() );
        }        
    }
}
