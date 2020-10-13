package com.mosaicatm.matmplugin.matm;

import java.util.Date;

import com.mosaicatm.fixm.ext.mosaic._3.MatmFlightType;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import com.mosaicatm.fuser.common.matm.util.AerodromeUtil;

public class MatmTestFlightUtil
{
    private static final ObjectFactory objectFactory = new ObjectFactory();
	
	public static MatmFlight createTestMatmFlightMinimal()
	{
		MatmFlight flight = new MatmFlight();
		flight.setAcid("AAL123");		
		
		return flight;
	}
	
	public static MatmFlight createTestMatmFlight()
	{
		MatmFlight flight = new MatmFlight();
        flight.setGufi("test-gufi");
		flight.setLastUpdateSource("FMC");
		flight.setAcid("AAL123");
		flight.setCarrier("AAL");
		
		flight.setDepartureAerodrome(AerodromeUtil.createFromIataName("DFW"));
		flight.setArrivalAerodrome(AerodromeUtil.createFromIataName("BWI"));
		flight.setDepartureRunwayModel(objectFactory.createMatmFlightDepartureRunwayModel("31L"));
		flight.setDepartureStandModel("A13");

		long outTime = (System.currentTimeMillis() / 1000) * 1000;
		long offTime = outTime + (TimeFactory.MINUTE_IN_MILLIS * 30);
		long onTime = offTime + (TimeFactory.HOUR_IN_MILLIS * 4);
		long inTime = onTime + (TimeFactory.MINUTE_IN_MILLIS * 20);

		flight.setDepartureStandEstimatedTime(new Date(outTime));
		flight.setDepartureStandTargetedTime(new Date(outTime + (TimeFactory.MINUTE_IN_MILLIS * 2)));
		flight.setDepartureStandActualTime(objectFactory.createMatmFlightDepartureStandActualTime(new Date(outTime + (TimeFactory.MINUTE_IN_MILLIS * 5))));

		flight.setDepartureRunwayEstimatedTime(new Date(offTime));
		flight.setDepartureRunwayTargetedTime(
		        objectFactory.createMatmFlightDepartureRunwayTargetedTime(
		                new Date(offTime + (TimeFactory.MINUTE_IN_MILLIS * 2))));
		flight.setDepartureRunwayActualTime(objectFactory.createMatmFlightDepartureRunwayActualTime(
		    new Date(offTime + (TimeFactory.MINUTE_IN_MILLIS * 5))));

		flight.setArrivalRunwayEstimatedTime(new Date(onTime));
		flight.setArrivalRunwayTargetedTime(new Date(onTime + (TimeFactory.MINUTE_IN_MILLIS * 2)));
		flight.setArrivalRunwayActualTime(new Date(onTime + (TimeFactory.MINUTE_IN_MILLIS * 5)));

		flight.setArrivalStandEstimatedTime(new Date(inTime));
		flight.setArrivalStandTargetedTime(new Date(inTime + (TimeFactory.MINUTE_IN_MILLIS * 2)));
		flight.setArrivalStandActualTime(new Date(inTime + (TimeFactory.MINUTE_IN_MILLIS * 5)));
		Position position = new Position();
		position.setLatitude(40.0d);
		position.setLongitude(120.0d);
		flight.setPosition(position);
		
		return flight;
	}
	
	public static MatmFlight createTestMatmFlightWithExtensions()
	{
		MatmFlight flight = new MatmFlight();
		flight.setAcid("AAL123");
		flight.setCarrier("AAL");
		flight.setArrivalAerodrome(AerodromeUtil.createFromIataName("BWI"));
		flight.setDepartureAerodrome(AerodromeUtil.createFromIataName("DFW"));
		flight.setDepartureRunwayModel(objectFactory.createMatmFlightDepartureRunwayModel("31L"));
		flight.setDepartureStandModel("A13");

		long outTime = (System.currentTimeMillis() / 1000) * 1000;
		long offTime = outTime + (TimeFactory.MINUTE_IN_MILLIS * 30);
		long onTime = offTime + (TimeFactory.HOUR_IN_MILLIS * 4);
		long inTime = onTime + (TimeFactory.MINUTE_IN_MILLIS * 20);

		flight.setDepartureStandEstimatedTime(new Date(outTime));
		flight.setDepartureStandTargetedTime(new Date(outTime + (TimeFactory.MINUTE_IN_MILLIS * 2)));
		flight.setDepartureStandActualTime(objectFactory.createMatmFlightDepartureStandActualTime(new Date(outTime + (TimeFactory.MINUTE_IN_MILLIS * 5))));

		flight.setDepartureRunwayEstimatedTime(new Date(offTime));
		flight.setDepartureRunwayTargetedTime(
		        objectFactory.createMatmFlightDepartureRunwayTargetedTime(
		                new Date(offTime + (TimeFactory.MINUTE_IN_MILLIS * 2))));
		flight.setDepartureRunwayActualTime(objectFactory.createMatmFlightDepartureRunwayActualTime(
		    new Date(offTime + (TimeFactory.MINUTE_IN_MILLIS * 5))));

		flight.setArrivalRunwayEstimatedTime(new Date(onTime));
		flight.setArrivalRunwayTargetedTime(new Date(onTime + (TimeFactory.MINUTE_IN_MILLIS * 2)));
		flight.setArrivalRunwayActualTime(new Date(onTime + (TimeFactory.MINUTE_IN_MILLIS * 5)));

		flight.setArrivalStandEstimatedTime(new Date(inTime));
		flight.setArrivalStandTargetedTime(new Date(inTime + (TimeFactory.MINUTE_IN_MILLIS * 2)));
		flight.setArrivalStandActualTime(new Date(inTime + (TimeFactory.MINUTE_IN_MILLIS * 5)));
		
		Position position = new Position();
		position.setLatitude(40.0d);
		position.setLongitude(120.0d);
		flight.setPosition(position);
		
		//Add some extensions
		MatmFlightType matmFlightType = new MatmFlightType();
		matmFlightType.getDeparture().setRunwaySource("USER");
		
		return flight;
	}
	
	public static MatmFlight createTestMatmFlightWithPosition ()
	{		
		MatmFlight flight = new MatmFlight();
		flight.setAcid("AAL123");
		flight.setGufi("test-gufi");
		flight.setAircraftType("B757");		
		//flight.setCentre ("ZFW");
		flight.setArrivalAerodrome(AerodromeUtil.createFromIataName("BWI"));
		flight.setDepartureAerodrome(AerodromeUtil.createFromIataName("DFW"));
		flight.setBeaconCode(String.valueOf(Integer.valueOf(1911)));
		flight.setAircraftAddress(String.valueOf(Long.valueOf(1776)));
		
		Position position = new Position();
		position.setAltitude(Double.valueOf(123.0));
		position.setHeading(Double.valueOf(35.6));
		position.setLatitude(Double.valueOf(92.78));
		position.setLongitude(Double.valueOf(-101.847));
		position.setSpeed(Double.valueOf(324.0));
		position.setTimestamp(new Date());
		flight.setPosition(position);
		flight.setTimestamp(new Date());
		
		return flight;
	}
}
