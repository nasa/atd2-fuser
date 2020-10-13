package com.mosaicatm.tfmtfdmplugin.matm;

import java.util.Date;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.mosaicatm.matmdata.flight.MatmFlight;

import gov.nasa.atm.tfm.tfdm.transfer.TfmTfdmFlightTransfer;
import gov.nasa.atm.tfm.tfdm.transfer.TfmTfdmMessageType;
import gov.nasa.atm.tfm.tfdm.transfer.TfmTfdmSensitivityReasonType;
import gov.nasa.atm.tfm.tfdm.transfer.TfmTfdmSensitivityType;
import gov.nasa.atm.tfm.tfdm.transfer.TypeOfFlightType;

public class TfmTfdmFlightTransferToMatmTransformTest
{
    @Test
    public void testTransformFullCircle()
    {
        TfmTfdmFlightTransferToMatmTransform to_matm = new TfmTfdmFlightTransferToMatmTransform();
        MatmToTfmTfdmFlightTransferTransform from_matm = new MatmToTfmTfdmFlightTransferTransform();
        TfmTfdmFlightTransfer flight1 = getSampleData( "GUFI_VALUE", System.currentTimeMillis() );    
        
        assertNotNull( flight1 );

        MatmFlight matm = to_matm.transform( flight1 );
        assertNotNull( matm );
        assertEquals(flight1.getSourceFacility(), matm.getMajorCarrier());
        
        assertTrue( matm.isSensitiveDataExternal() );

        TfmTfdmFlightTransfer flight2 = from_matm.transform( matm );
        assertNotNull( flight2 );
        checkElementsEqual( flight1, flight2 );
    }

    private void checkElementsEqual( TfmTfdmFlightTransfer flight1, TfmTfdmFlightTransfer flight2 )
    {    
        assertEquals( flight1.getAcftRegistrationNumber(), flight2.getAcftRegistrationNumber() );
        assertEquals( flight1.getAcid(), flight2.getAcid() );
        //assertEquals( flight1.getAirline(), flight2.getAirline() );
        assertEquals( flight1.getArrArptIcao(), flight2.getArrArptIcao() );
        assertEquals( flight1.getArrStandAssignment(), flight2.getArrStandAssignment() );
        assertEquals( flight1.getDepArptIcao(), flight2.getDepArptIcao() );
        assertEquals( flight1.getDepStandAssignment(), flight2.getDepStandAssignment() );
        assertEquals( flight1.getFlightRef(), flight2.getFlightRef() );
        assertEquals( flight1.getGufi(), flight2.getGufi() );
        assertEquals( flight1.getIntendedArrSpot(), flight2.getIntendedArrSpot() );
        assertEquals( flight1.getIntendedDeiceLocation(), flight2.getIntendedDeiceLocation() );
        assertEquals( flight1.getIntendedDepSpot(), flight2.getIntendedDepSpot() );
        assertEquals( flight1.getMajorCarrier(), flight2.getMajorCarrier() );
        assertEquals( flight1.getSource(), flight2.getSource() );
        assertEquals( flight1.getSourceFacility(), flight2.getSourceFacility() );
        assertEquals( flight1.getSensitivity(), flight2.getSensitivity() );
        assertEquals( flight1.getSensitivityReason(), flight2.getSensitivityReason() );
        assertEquals( flight1.getAccDepRwyList(), flight2.getAccDepRwyList() );
        assertEquals( flight1.getActualInBlockTime(), flight2.getActualInBlockTime() );
        assertEquals( flight1.getActualLandingTime(), flight2.getActualLandingTime() );
        assertEquals( flight1.getActualOffBlockTime(), flight2.getActualOffBlockTime() );
        assertEquals( flight1.getActualTakeOffTime(), flight2.getActualTakeOffTime() );
        assertEquals( flight1.getArrIntentToHoldInMove(), flight2.getArrIntentToHoldInMove() );
        assertEquals( flight1.getArrIntentToHoldNonMove(), flight2.getArrIntentToHoldNonMove() );
        assertEquals( flight1.getArrStandAvailable(), flight2.getArrStandAvailable() );
        assertEquals( flight1.getCdmPart(), flight2.getCdmPart() );
        assertEquals( flight1.getDepIntentToHoldInMove(), flight2.getDepIntentToHoldInMove() );
        assertEquals( flight1.getDepIntentToHoldNonMove(), flight2.getDepIntentToHoldNonMove() );
        assertEquals( flight1.getDepReadiness(), flight2.getDepReadiness() );
        assertEquals( flight1.getEarliestOffBlockTime(), flight2.getEarliestOffBlockTime() );
        assertEquals( flight1.getFlightCancel(), flight2.getFlightCancel() );
        assertEquals( flight1.getInitialOffBlockTime(), flight2.getInitialOffBlockTime() );
        assertEquals( flight1.getIntentReturnToGate(), flight2.getIntentReturnToGate() );
        assertEquals( flight1.getIntentToBeDeiced(), flight2.getIntentToBeDeiced() );
        assertEquals( flight1.getMsgType(), flight2.getMsgType() );
        assertEquals( flight1.getProjectedWheelsUpTime(), flight2.getProjectedWheelsUpTime() );
        assertEquals( flight1.getSourceTimeStamp(), flight2.getSourceTimeStamp() );
        assertEquals( flight1.getTargetMAEntryTime(), flight2.getTargetMAEntryTime() );
        assertEquals( flight1.getTargetOffBlockTime(), flight2.getTargetOffBlockTime() );
        assertEquals( flight1.getTargetTakeOffTime(), flight2.getTargetTakeOffTime() );
        assertEquals( flight1.getTimestampSourceProcessed(), flight2.getTimestampSourceProcessed() );
        assertEquals( flight1.getTimestampSourceReceived(), flight2.getTimestampSourceReceived() );
        assertEquals( flight1.getTmatMarkedForSub(), flight2.getTmatMarkedForSub() );
        assertEquals( flight1.getTmatRelinquish(), flight2.getTmatRelinquish() );
        assertEquals( flight1.getUnaccDepRwyList(), flight2.getUnaccDepRwyList() );
        
        //Elements that should not be transformed
        assertNotEquals( flight1.getArrArpt(), flight2.getArrArpt() );
        assertNull( flight2.getArrArpt() );
        
        assertNotEquals( flight1.getDepArpt(), flight2.getDepArpt() );
        assertNull( flight2.getDepArpt() );
        
        assertNotEquals( flight1.getFlightType(), flight2.getFlightType() );
        assertNull( flight2.getFlightType() );
        
        assertNotEquals( flight1.getInitialGateTimeOfDeparture(), flight2.getInitialGateTimeOfDeparture() );
        assertNull( flight2.getInitialGateTimeOfDeparture() );
    }        

    private TfmTfdmFlightTransfer getSampleData( String gufi, long baseTime )
    {
        TfmTfdmFlightTransfer flt = new TfmTfdmFlightTransfer();

        flt.setAcid( "Acid" );
        flt.setAirline( "Airline" );
        flt.setArrArpt( "ArrArpt" );
        flt.setDepArpt( "DepArpt" );
        flt.setGufi( gufi );
        flt.setAcftRegistrationNumber( "FlightAcftDescriptionRegistration" );
        flt.setArrArptIcao( "FlightArrNasAerodromeIcaoCode" );
        flt.setArrStandAssignment( "FlightArrStandAssignment" );
        flt.setDepArptIcao( "FlightDepNasAerodromeIcaoCode" );
        flt.setDepStandAssignment( "FlightDepStandAssignment" );
        flt.setMajorCarrier( "FlightFlightIdentMajorCarrier" );
        flt.setIntendedArrSpot( "FlightIntentedArrSpot" );
        flt.setIntendedDeiceLocation( "FlightIntentedDeiceLocation" );
        flt.setIntendedDepSpot( "FlightIntentedDepSpot" );
        flt.setFlightRef( "FlightRef" );
        flt.setSource( "Source" );
        flt.setSourceFacility( "SFC" );
        flt.setSensitivity( TfmTfdmSensitivityType.R );
        flt.setSensitivityReason( TfmTfdmSensitivityReasonType.FS );

        flt.setCdmPart( true );
        flt.setArrIntentToHoldInMove( true );
        flt.setArrIntentToHoldNonMove( true );
        flt.setArrStandAvailable( true );
        flt.setDepIntentToHoldInMove( true );
        flt.setDepIntentToHoldNonMove( true );
        flt.setDepReadiness( true );
        flt.setIntentReturnToGate( true );
        flt.setIntentToBeDeiced( true );
        flt.setTmatMarkedForSub( true );
        flt.setTmatRelinquish( true );   
        flt.setFlightCancel( true );

        flt.setActualLandingTime( new Date(baseTime + 1000) );
        flt.setActualInBlockTime( new Date(baseTime + 2000) );
        flt.setActualTakeOffTime( new Date(baseTime + 4000) );
        flt.setEarliestOffBlockTime( new Date(baseTime + 5000) );
        flt.setInitialOffBlockTime( new Date(baseTime + 6000) );
        flt.setActualOffBlockTime( new Date(baseTime + 7000) );
        flt.setTargetOffBlockTime( new Date(baseTime + 9000) );
        flt.setTargetMAEntryTime( new Date(baseTime + 10000) );
        flt.setInitialGateTimeOfDeparture( new Date(baseTime + 11000) );
        flt.setTargetTakeOffTime( new Date(baseTime + 12000) );
        flt.setSourceTimeStamp( new Date(baseTime + 13000) );
        flt.setTimestampSourceReceived( new Date(baseTime + 14000) );
        flt.setTimestampSourceProcessed( new Date(baseTime + 15000) );  
        flt.setProjectedWheelsUpTime( new Date(baseTime + 16000) );

        ArrayList<String> rwys = new ArrayList<>();
        rwys.add( "RWY1" );
        rwys.add( "RWY2" );
        flt.setAccDepRwyList( rwys );    
        
        ArrayList<String> rwys2 = new ArrayList<>();
        rwys2.add( "RWY21" );
        rwys2.add( "RWY22" );        
        flt.setUnaccDepRwyList( rwys2 );

        flt.setFlightType( TypeOfFlightType.MILITARY );
        flt.setMsgType( TfmTfdmMessageType.FLTC );
    
        return( flt );
    }
}
