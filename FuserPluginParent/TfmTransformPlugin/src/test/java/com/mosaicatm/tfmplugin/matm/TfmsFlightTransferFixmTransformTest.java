package com.mosaicatm.tfmplugin.matm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertNull;

import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.common.CancelledType;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.TfmControlIndicatorType;
import com.mosaicatm.matmdata.flight.extension.TfmMessageTriggerType;
import com.mosaicatm.matmdata.flight.extension.TfmMessageTypeType;
import com.mosaicatm.matmdata.flight.extension.tfmcomm.TfmSensitivityReasonType;
import com.mosaicatm.matmdata.flight.extension.tfmcomm.TfmSensitivityType;
import com.mosaicatm.tfm.thick.flight.mtfms.data.LatLon;
import com.mosaicatm.tfm.thick.flight.mtfms.data.TfmsFlightTransfer;

public class TfmsFlightTransferFixmTransformTest
{
	@Test
	public void testTfmsFlightTransferMatmTransform ()
	{
		TfmsFlightTransferToMatmTransform toMatmTransform = new TfmsFlightTransferToMatmTransform ();
		MatmToTfmsFlightTransferTransform fromMatmTransform = new MatmToTfmsFlightTransferTransform ();
		
		TfmsFlightTransfer tfms = createTfmsFlightTransfer ();
		
		assertNotNull (tfms);
		
		MatmFlight matm = toMatmTransform.transform(tfms);
		
		assertNotNull (matm);
		
        compareMatmTransform( tfms, matm );
        
		TfmsFlightTransfer transfer = fromMatmTransform.transform (matm);
		
		assertNotNull (transfer);
		
		compareTfmsFlightTransfer (tfms, transfer);
	}
	
	@Test
	public void testTfmsFlightTransformPerformance ()
	{
		TfmsFlightTransferToMatmTransform toMatmTransform = new TfmsFlightTransferToMatmTransform ();
		MatmToTfmsFlightTransferTransform fromMatmTransform = new MatmToTfmsFlightTransferTransform ();
		
		List<TfmsFlightTransfer> transfers = new ArrayList<TfmsFlightTransfer> ();
		List<MatmFlight> matms = new ArrayList<MatmFlight> ();
		
		long maxCount = 1;
		for (int i = 0; i < maxCount; ++i)
			transfers.add (createTfmsFlightTransfer());
		
		long averageTime = 0L;
		for (int i = 0; i < maxCount; ++i)
		{
			long startTime = System.currentTimeMillis();
			MatmFlight matm = toMatmTransform.transform (transfers.get(i));
			long endTime = System.currentTimeMillis() - startTime;
			
			averageTime += endTime;
			
			matms.add(matm);
		}
		
		averageTime = averageTime / maxCount;
		
		System.out.println("Average tfm to matm time: " + averageTime + ", iterations: " + maxCount);
		
		assertTrue (averageTime >= 0L);
		assertTrue (averageTime < 5L);
		
		long averageMatmTime = 0L;
		for (int i = 0; i < maxCount; ++i)
		{
			long startTime = System.currentTimeMillis();
			TfmsFlightTransfer transfer = fromMatmTransform.transform (matms.get(i));
			long endTime = System.currentTimeMillis() - startTime;
			
			averageMatmTime += endTime;
		}
		
		averageMatmTime = averageMatmTime / maxCount;	
		
		System.out.println("Average tfm to MATM time: " + averageMatmTime + ", iterations: " + maxCount);
		
		assertTrue (averageMatmTime >= 0L);
		assertTrue (averageMatmTime < 5L);
	}
	
	@Test
	public void testTfmsFlightCancelTransform ()
	{
		TfmsFlightTransferToMatmTransform toMatmTransform = new TfmsFlightTransferToMatmTransform ();
        
		TfmsFlightTransfer tfms = createTfmsFlightTransfer ();
        tfms.setCanceled( true );
        tfms.setMessageTrigger( TfmMessageTriggerType.FD_FLIGHT_CANCEL_MSG.value() );
		
		MatmFlight matm = toMatmTransform.transform(tfms);        
        assertEquals( CancelledType.CANCELLED, matm.getCancelled() );

        tfms.setCanceled( Boolean.FALSE );
		matm = toMatmTransform.transform(tfms);        
        assertEquals( CancelledType.NOT_CANCELLED, matm.getCancelled() );
        
        tfms.setMessageTrigger( TfmMessageTriggerType.HCS_CANCELLATION_MSG.value() );
		matm = toMatmTransform.transform(tfms);        
        
        assertNull( matm.getCancelled() );        
    }
    
    private void compareMatmTransform (TfmsFlightTransfer source, MatmFlight compare)
    {
        assertNotNull (source);
        assertNotNull (compare);

        assertNotNull (compare.getLastUpdateSource());
        assertEquals ("TFM", compare.getLastUpdateSource());          
        
        assertNotNull (source.getMessageType());
        assertNotNull (compare.getSystemId());
        assertEquals ("TFM-" + source.getMessageType(), compare.getSystemId());        
        
        assertTrue (compare.isSensitiveDataExternal());      
    }
    
	private void compareTfmsFlightTransfer (TfmsFlightTransfer source, TfmsFlightTransfer compare)
	{
		assertNotNull (source);
		assertNotNull (compare);
		
		assertNotNull (source.getAircraftType());
		assertNotNull (compare.getAircraftType());
		assertEquals (source.getAircraftType(), compare.getAircraftType());
		
		assertNotNull (source.getGufi());
		assertNotNull (compare.getGufi());
		assertEquals (source.getGufi(), compare.getGufi());
		
		assertNotNull (source.getAcid());
		assertNotNull (compare.getAcid());
		assertEquals (source.getAcid(), compare.getAcid());
		
		assertNotNull (source.getArrAirport());
		assertNotNull (compare.getArrAirport());
		assertEquals (source.getArrAirport(), compare.getArrAirport());
		
		assertNotNull (source.getArrFix());
		assertNotNull (compare.getArrFix());
		assertEquals (source.getArrFix(), compare.getArrFix());
		
		assertNotNull (source.getArrFixEstimatedTime());
		assertNotNull (compare.getArrFixEstimatedTime());
		assertEquals (source.getArrFixEstimatedTime(), compare.getArrFixEstimatedTime());
		
		assertNotNull (source.getBeaconCode());
		assertNotNull (compare.getBeaconCode());
		assertEquals (source.getBeaconCode(), compare.getBeaconCode());
		
		assertNotNull (source.getCurrPos());
		assertNotNull (compare.getCurrPos());
		assertNotNull (source.getCurrPos().getLatDeg());
		assertNotNull (compare.getCurrPos().getLatDeg());
		assertEquals (source.getCurrPos().getLatDeg(), compare.getCurrPos().getLatDeg());
		
		assertNotNull (source.getCurrPos().getLonDeg());
		assertNotNull (compare.getCurrPos().getLonDeg());
		assertEquals (source.getCurrPos().getLonDeg(), compare.getCurrPos().getLonDeg());
		
		assertNotNull (source.getDepAirport());
		assertNotNull (compare.getDepAirport());
		assertEquals (source.getDepAirport(), compare.getDepAirport());
		
		assertNotNull (source.getDepFix());
		assertNotNull (compare.getDepFix());
		assertEquals (source.getDepFix(), compare.getDepFix());
		
		assertNotNull (source.getDepFixEstimatedTime());
		assertNotNull (compare.getDepFixEstimatedTime());
		assertEquals (source.getDepFixEstimatedTime(), compare.getDepFixEstimatedTime());
		
		assertNotNull (source.getArrivalStandActualTime());
		assertNotNull (compare.getArrivalStandActualTime());
		assertEquals (source.getArrivalStandActualTime(), compare.getArrivalStandActualTime());
		
		assertNotNull (source.getArrivalStandAirlineTime());
		assertNotNull (compare.getArrivalStandAirlineTime());
		assertEquals (source.getArrivalStandAirlineTime(), compare.getArrivalStandAirlineTime());
		
		assertNotNull (source.getDepartureRunwayActualTime());
		assertNotNull (compare.getDepartureRunwayActualTime());
		assertEquals (source.getDepartureRunwayActualTime(), compare.getDepartureRunwayActualTime());

		assertNotNull (source.getDepartureRunwayEstimatedTime());
		assertNotNull (compare.getDepartureRunwayEstimatedTime());
		assertEquals (source.getDepartureRunwayEstimatedTime(), compare.getDepartureRunwayEstimatedTime());
		
		assertNotNull (source.getArrivalRunwayEstimatedTime());
		assertNotNull (compare.getArrivalRunwayEstimatedTime());
		assertEquals (source.getArrivalRunwayEstimatedTime(), compare.getArrivalRunwayEstimatedTime());
		
		assertNotNull (source.getDepartureStandActualTime());
		assertNotNull (compare.getDepartureStandActualTime());
		assertEquals (source.getDepartureStandActualTime(), compare.getDepartureStandActualTime());
		
		assertNotNull (source.getDepartureStandAirlineTime());
		assertNotNull (compare.getDepartureStandAirlineTime());
		assertEquals (source.getDepartureStandAirlineTime(), compare.getDepartureStandAirlineTime());
		
		assertNotNull (source.getDepartureStandInitialTime());
		assertNotNull (compare.getDepartureStandInitialTime());
		assertEquals (source.getDepartureStandInitialTime(), compare.getDepartureStandInitialTime());

		assertNotNull (source.getRouteString());
		assertNotNull (compare.getRouteString());
		assertEquals (source.getRouteString(), compare.getRouteString());
		
		assertNotNull (source.getSpeed());
		assertNotNull (compare.getSpeed());
		assertEquals (source.getSpeed(), compare.getSpeed());

		assertNotNull (source.getSpeedFiled());
		assertNotNull (compare.getSpeedFiled());
		assertEquals (source.getSpeedFiled(), compare.getSpeedFiled());        
        
		assertNotNull (source.getTimeStamp());
		assertNotNull (compare.getTimeStamp());
		assertEquals (source.getTimeStamp(), compare.getTimeStamp());

		assertNotNull (source.getWeightClass());
		//assertNotNull (compare.getWeightClass());
		//assertEquals (source.getWeightClass(), compare.getWeightClass());
		
		assertNotNull (source.getFlightIndex());
		assertNotNull (compare.getFlightIndex());
		assertEquals (source.getFlightIndex(), compare.getFlightIndex());
		
//		assertNotNull (source.getArrFlag());
//		assertNotNull (compare.getArrFlag());
//		assertEquals (source.getArrFlag(), compare.getArrFlag());
//
//		assertNotNull (source.getDepFlag());
//		assertNotNull (compare.getDepFlag());
//		assertEquals (source.getDepFlag(), compare.getDepFlag());
//		
		assertNotNull (source.getAltitude());
		assertNotNull (compare.getAltitude());
		assertEquals (source.getAltitude(), compare.getAltitude());

//		assertNotNull (source.getAltitudeType());
//		assertNotNull (compare.getAltitudeType());
//		assertEquals (source.getAltitudeType(), compare.getAltitudeType());
        
		assertNotNull (source.getAltitudeFiled());
		assertNotNull (compare.getAltitudeFiled());
		assertEquals (source.getAltitudeFiled(), compare.getAltitudeFiled()); 

		assertNotNull (source.getAltitudeAssigned());
		assertNotNull (compare.getAltitudeAssigned());
		assertEquals (source.getAltitudeAssigned(), compare.getAltitudeAssigned());  
        
		assertNotNull (source.getAltitudeRequested());
		assertNotNull (compare.getAltitudeRequested());
		assertEquals (source.getAltitudeRequested(), compare.getAltitudeRequested());           

		assertNotNull (source.getAcEqpSuffix());
		assertNotNull (compare.getAcEqpSuffix());
		assertEquals (source.getAcEqpSuffix(), compare.getAcEqpSuffix());

		assertNotNull (source.getPhysicalClass());
		assertNotNull (compare.getPhysicalClass());
		assertEquals (source.getPhysicalClass(), compare.getPhysicalClass());        
        
//		assertNotNull (source.getAcEqpPrefix());
//		assertNotNull (compare.getAcEqpPrefix());
//		assertEquals (source.getAcEqpPrefix(), compare.getAcEqpPrefix());
//		
//		
//		assertNotNull (source.getFlightStatus());
//		assertNotNull (compare.getFlightStatus());
//		assertEquals (source.getFlightStatus(), compare.getFlightStatus());
//		
        assertNotNull (source.getMessageType());
        assertNotNull (compare.getMessageType());
        assertEquals (source.getMessageType(), compare.getMessageType());
//		
//		assertNotNull (source.getNumAircraft());
//		assertNotNull (compare.getNumAircraft());
//		assertEquals (source.getNumAircraft(), compare.getNumAircraft());
//		
//		
//		assertNotNull (source.getSourceMsg());
//		assertNotNull (compare.getSourceMsg());
//		assertEquals (source.getSourceMsg(), compare.getSourceMsg());
//		
//		assertNotNull (source.getUserClass());
//		assertNotNull (compare.getUserClass());
//		assertEquals (source.getUserClass(), compare.getUserClass());
		
		assertNotNull (source.getDepartureStandProposedTime());
		assertNotNull (compare.getDepartureStandProposedTime());
		assertEquals (source.getDepartureStandProposedTime(), compare.getDepartureStandProposedTime());      
        
		assertNotNull (source.getArrivalRunwayControlledTime());
		assertNotNull (compare.getArrivalRunwayControlledTime());
		assertEquals (source.getArrivalRunwayControlledTime(), compare.getArrivalRunwayControlledTime());         
        
		assertNotNull (source.getArrivalRunwayProposedTime());
		assertNotNull (compare.getArrivalRunwayProposedTime());
		assertEquals (source.getArrivalRunwayProposedTime(), compare.getArrivalRunwayProposedTime());
		
		assertNotNull (source.getDepartureRunwayProposedTime());
		assertNotNull (compare.getDepartureRunwayProposedTime());
		assertEquals (source.getDepartureRunwayProposedTime(), compare.getDepartureRunwayProposedTime());

		assertNotNull (source.getDepartureRunwayMeteredTime());
		assertNotNull (compare.getDepartureRunwayMeteredTime());
		assertEquals (source.getDepartureRunwayMeteredTime(), compare.getDepartureRunwayMeteredTime());        
        
		assertNotNull (source.getDepartureRunwayScheduledTime());
		assertNotNull (compare.getDepartureRunwayScheduledTime());
		assertEquals (source.getDepartureRunwayScheduledTime(), compare.getDepartureRunwayScheduledTime());           
        
		assertNotNull (source.getArrivalRunwayScheduledTime());
		assertNotNull (compare.getArrivalRunwayScheduledTime());
		assertEquals (source.getArrivalRunwayScheduledTime(), compare.getArrivalRunwayScheduledTime());     
        
		assertNotNull (source.getArrivalStandProposedTime());
		assertNotNull (compare.getArrivalStandProposedTime());
		assertEquals (source.getArrivalStandProposedTime(), compare.getArrivalStandProposedTime());
		
		assertNotNull (source.getListFixes());
		assertNotNull (compare.getListFixes());
		assertFalse (source.getListFixes().isEmpty());
		assertFalse (compare.getListFixes().isEmpty());
		assertEquals (source.getListFixes().size(), compare.getListFixes().size());
		
		assertNotNull (source.getListSectors());
		assertNotNull (compare.getListSectors());
		assertFalse (source.getListSectors().isEmpty());
		assertFalse (compare.getListSectors().isEmpty());
		assertEquals (source.getListSectors().size(), compare.getListSectors().size());
        
		assertNotNull (source.getMessageTrigger());
		assertNotNull (compare.getMessageTrigger());
		assertEquals (source.getMessageTrigger(), compare.getMessageTrigger());

        assertNotNull (source.getSensitivity());
        assertNotNull (compare.getSensitivity());
        assertEquals (source.getSensitivity(), compare.getSensitivity());        

        assertNotNull (source.getSensitivityReason());
        assertNotNull (compare.getSensitivityReason());
        assertEquals (source.getSensitivityReason(), compare.getSensitivityReason());
        
		assertNotNull (source.getCanceled());
		assertNotNull (compare.getCanceled());
		assertEquals (source.getCanceled(), compare.getCanceled());

		assertNotNull (source.getCdmParticipant());
		assertNotNull (compare.getCdmParticipant());
		assertEquals (source.getCdmParticipant(), compare.getCdmParticipant());

		assertNotNull (source.getDiversionIndicator());
		assertNotNull (compare.getDiversionIndicator());
		assertEquals (source.getDiversionIndicator(), compare.getDiversionIndicator());

		assertNotNull (source.getDiversionCancelFlightIndex());
		assertNotNull (compare.getDiversionCancelFlightIndex());
		assertEquals (source.getDiversionCancelFlightIndex(), compare.getDiversionCancelFlightIndex());

		assertNotNull (source.getDiversionCancelNewFlightIndex());
		assertNotNull (compare.getDiversionCancelNewFlightIndex());
		assertEquals (source.getDiversionCancelNewFlightIndex(), compare.getDiversionCancelNewFlightIndex());

		assertNotNull (source.getArrivalRunwayAirlineTime());
		assertNotNull (compare.getArrivalRunwayAirlineTime());
		assertEquals (source.getArrivalRunwayAirlineTime(), compare.getArrivalRunwayAirlineTime());

		assertNotNull (source.getArrivalRunwayOriginalControlledTime());
		assertNotNull (compare.getArrivalRunwayOriginalControlledTime());
		assertEquals (source.getArrivalRunwayOriginalControlledTime(), compare.getArrivalRunwayOriginalControlledTime());

		assertNotNull (source.getArrivalStandAirlineTime());
		assertNotNull (compare.getArrivalStandAirlineTime());
		assertEquals (source.getArrivalStandAirlineTime(), compare.getArrivalStandAirlineTime());

		assertNotNull (source.getControlElement());
		assertNotNull (compare.getControlElement());
		assertEquals (source.getControlElement(), compare.getControlElement());

		assertNotNull (source.getControlIndicator());
		assertNotNull (compare.getControlIndicator());
		assertEquals (source.getControlIndicator(), compare.getControlIndicator());

		assertNotNull (source.getDepartureRunwayAirlineTime());
		assertNotNull (compare.getDepartureRunwayAirlineTime());
		assertEquals (source.getDepartureRunwayAirlineTime(), compare.getDepartureRunwayAirlineTime());

		assertNotNull (source.getDepartureRunwayOriginalControlledTime());
		assertNotNull (compare.getDepartureRunwayOriginalControlledTime());
		assertEquals (source.getDepartureRunwayOriginalControlledTime(), compare.getDepartureRunwayOriginalControlledTime());        
        
		assertNotNull (source.getDepartureStandAirlineTime());
		assertNotNull (compare.getDepartureStandAirlineTime());
		assertEquals (source.getDepartureStandAirlineTime(), compare.getDepartureStandAirlineTime());  

		assertNotNull (source.getFlightCreationTime());
		assertNotNull (compare.getFlightCreationTime());
		assertEquals (source.getFlightCreationTime(), compare.getFlightCreationTime());  
        
        assertNotNull (source.getComputerFacility());
        assertNotNull (compare.getComputerFacility());
        assertEquals (source.getComputerFacility(), compare.getComputerFacility()); 
        
        assertNotNull (source.getComputerId());
        assertNotNull (compare.getComputerId());
        assertEquals (source.getComputerId(), compare.getComputerId()); 
	}
	
	private TfmsFlightTransfer createTfmsFlightTransfer ()
	{
		long timestamp = System.currentTimeMillis();
		
		TfmsFlightTransfer transfer = new TfmsFlightTransfer ();
		
		transfer.setAircraftType("ABC747");
		transfer.setGufi("test-" + timestamp + "-test");
		transfer.setAcid("PHI005");
		transfer.setArrAirport("MCO");
		transfer.setArrFix("MICKI");
		transfer.setArrFixEstimatedTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1)));
		transfer.setBeaconCode(9876);
		
		LatLon position = new LatLon ();
		position.setLatDeg(32.1234);
		position.setLonDeg(-96.0987);
		transfer.setCurrPos(position);
		
		transfer.setDepAirport("PHL");
		transfer.setDepFix("PODDE");
		transfer.setDepFixEstimatedTime(new Date(timestamp + (TimeFactory.MINUTE_IN_MILLIS * 15)));
		
		transfer.setArrivalStandActualTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 15)));
		transfer.setArrivalStandAirlineTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 20)));
		transfer.setArrivalStandProposedTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 19)));
		
		transfer.setDepartureRunwayActualTime(new Date(timestamp));
		transfer.setDepartureRunwayEstimatedTime(new Date(timestamp + (TimeFactory.MINUTE_IN_MILLIS * 5)));
		transfer.setDepartureRunwayProposedTime(new Date(timestamp + (TimeFactory.MINUTE_IN_MILLIS * 4)));
        transfer.setDepartureRunwayMeteredTime(new Date(timestamp + (TimeFactory.MINUTE_IN_MILLIS * 7)));
        transfer.setDepartureRunwayScheduledTime(new Date(timestamp + (TimeFactory.MINUTE_IN_MILLIS * 8)));
		
		transfer.setArrivalRunwayActualTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 5)));
		transfer.setArrivalRunwayEstimatedTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 10)));
		transfer.setArrivalRunwayProposedTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 9)));
		transfer.setArrivalRunwayControlledTime( new Date(timestamp - (TimeFactory.MINUTE_IN_MILLIS * 23)));
        transfer.setArrivalRunwayScheduledTime( new Date(timestamp - (TimeFactory.MINUTE_IN_MILLIS * 24)));
        
		transfer.setDepartureStandActualTime(new Date(timestamp - (TimeFactory.MINUTE_IN_MILLIS * 15)));
		transfer.setDepartureStandAirlineTime(new Date(timestamp - (TimeFactory.MINUTE_IN_MILLIS * 10)));
		transfer.setDepartureStandInitialTime(new Date(timestamp - (TimeFactory.MINUTE_IN_MILLIS * 20)));
		transfer.setDepartureStandProposedTime(new Date(timestamp - (TimeFactory.MINUTE_IN_MILLIS * 21)));
		
		transfer.setRouteString("PHL.PODDE..MICKI.MCO");
		
		transfer.setSpeed(Integer.valueOf(25).shortValue());
        transfer.setSpeedFiled(Integer.valueOf(250).shortValue());
		
		transfer.setTimeStamp(new Date(timestamp));
		
		transfer.setWeightClass("H");
		
		transfer.setFlightIndex(8778);
//		transfer.setArrFlag("A");
//		transfer.setDepFlag("D");
		transfer.setAltitudeType("B");
        transfer.setAltitude((short) 100 );
        transfer.setAltitudeFiled((short) 150 );
        transfer.setAltitudeRequested((short) 170 );
        transfer.setAltitudeAssigned((short) 180 );
		transfer.setAcEqpPrefix("H");
		transfer.setAcEqpSuffix("Q");
//		transfer.setFlightStatus("Off");
		transfer.setMessageType(TfmMessageTypeType.FLIGHT_TIMES.value());
		transfer.setNumAircraft("9");
		transfer.setPhysicalClass("PISTON");
//		transfer.setSourceMsg("source");
		transfer.setUserClass("turbo");
        transfer.setComputerFacility( "computerFacility" );
        transfer.setComputerId( "computerId" );
        
        transfer.setMessageTrigger( TfmMessageTriggerType.CTOP_ROUTE_ASSIGNMENT.value() );
        transfer.setSensitivity( TfmSensitivityType.R.value() );
        transfer.setSensitivityReason( TfmSensitivityReasonType.FM.value() );
        transfer.setCanceled( Boolean.FALSE );
        transfer.setCdmParticipant( Boolean.TRUE );
        transfer.setDiversionIndicator( "NO_DIVERSION" );
        transfer.setDiversionCancelFlightIndex( 898989 );
        transfer.setDiversionCancelNewFlightIndex( 77365355 );
        transfer.setArrivalRunwayAirlineTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 30)));
        transfer.setArrivalRunwayOriginalControlledTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 31)));
        transfer.setArrivalStandAirlineTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 32)));
        transfer.setControlElement("CTLELEM");
        transfer.setControlIndicator( TfmControlIndicatorType.CONTROL_ACTIVE.value() );
        transfer.setDepartureRunwayAirlineTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 33)));
        transfer.setDepartureRunwayOriginalControlledTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 34)));
        transfer.setDepartureStandAirlineTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 35)));
        transfer.setFlightCreationTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 36)));
		
		List<String> fixes = new ArrayList<String> ();
		fixes.add("PODDE");
		transfer.setListFixes(fixes);
		
		List<String> sectors = new ArrayList<String> ();
		sectors.add("BRAVO");
		transfer.setListSectors(sectors);
		
		return transfer;
	}
}
