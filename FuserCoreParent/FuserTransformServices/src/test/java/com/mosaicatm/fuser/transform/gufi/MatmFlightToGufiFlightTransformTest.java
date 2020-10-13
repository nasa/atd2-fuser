package com.mosaicatm.fuser.transform.gufi;

import java.util.Date;

import org.junit.Test;

import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import com.mosaicatm.matmdata.flight.extension.AsdexExtension;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;

public class MatmFlightToGufiFlightTransformTest 
{
    private ObjectFactory objectFactory = new ObjectFactory();
    
	@Test
	public void testMatmFlightToGufiFlightTransform ()
	{
		Date lastUpdateTime = new Date (TimeFactory.getDateAsLong("2014-12-27 01:00:00"));
		Date offControlled = new Date (TimeFactory.getDateAsLong("2014-12-27 00:00:00"));
		Date onControlled = new Date (TimeFactory.getDateAsLong("2014-12-27 03:00:00"));
		Date positionTime = new Date (lastUpdateTime.getTime());
		
		MatmFlight flight = new MatmFlight ();
		flight.setLastUpdateSource("ASDEX");
		flight.setGufi("test_gufi");
		flight.setAcid("test_acid");
		flight.setDepartureRunwayTargetedTime(objectFactory.createMatmFlightDepartureRunwayTargetedTime(offControlled));
		flight.setArrivalRunwayTargetedTime(onControlled);
		flight.setAircraftType("A380");
		flight.setBeaconCode(String.valueOf(123));
		Aerodrome deptAerodrome = new Aerodrome();
		deptAerodrome.setIataName("ORD");
		flight.setDepartureAerodrome(deptAerodrome);
		Aerodrome arrAerodrome = new Aerodrome();
		arrAerodrome.setIataName("JFK");
		flight.setArrivalAerodrome(arrAerodrome);
		
		Position position = new Position();
		position.setTimestamp(lastUpdateTime);
		position.setAltitude(Double.valueOf(350));
		position.setLatitude(Double.valueOf(30.123));
		position.setLongitude(Double.valueOf(-100.456));
		position.setTimestamp(positionTime);
		flight.setAircraftAddress(String.valueOf(Long.valueOf(789)));
		
		MatmFlightExtensions extensions = new MatmFlightExtensions();
		AsdexExtension asdex = new AsdexExtension();
		extensions.setAsdexExtension(asdex);
		flight.setExtensions(extensions);
		
		Aerodrome aerodrome = new Aerodrome();
		aerodrome.setIataName("ORD");
		asdex.setAsdexAirport(aerodrome);
		asdex.setTrackId(Integer.valueOf(2007));
		
//		MatmFlightToGufiFlightTransformer transformer = new MatmFlightToGufiFlightTransformer ();
//		GufiFlight gufi = transformer.transform(flight);
//		
//		assertNotNull (gufi);
//		
//		assertEquals (flight.getLastUpdateSource().name(), gufi.getLastMessageType());
//		assertEquals (flight.getGufi(), gufi.getGufi());
//		assertEquals (flight.getAcid(), gufi.getAcid());
//		assertEquals (flight.getOffControlled(), gufi.getLatestDepartureTime());
//		assertEquals (flight.getOnControlled(), gufi.getLatestArrivalTime());
//		assertEquals (flight.getAircraftType(), gufi.getAircraftType());
//		assertEquals (flight.getBeaconCode(), gufi.getBeaconCode());
//		assertEquals (flight.getDepartureAirport(), gufi.getDepartureAirport());
//		assertEquals (flight.getArrivalAirport(), gufi.getDestinationAirport());
//		assertEquals (flight.getLastUpdateTime().longValue(), gufi.getLastMessageTime().getTime());
//		assertEquals (flight.getAltitude().intValue(), gufi.getLatestAltitude().intValue());
//		assertEquals (flight.getLatitude(), gufi.getLatestLatitude());
//		assertEquals (flight.getLongitude(), gufi.getLatestLongitude());
//		assertEquals (flight.getPositionTime(), gufi.getLatestPositionUpdateTime());
//		assertEquals (flight.getPositionTime(), gufi.getLastMessageTimeAsdex());
//		assertEquals (flight.getModeSAddress(), gufi.getModeSAddress());
//		assertEquals (flight.getExtendedFields().getAsdexFields().getAirport(), gufi.getAsdexAirport());
//		assertEquals (flight.getExtendedFields().getAsdexFields().getTrackId(), gufi.getUniqueIdAsdex().intValue());
	}
}
