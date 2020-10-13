package com.mosaicatm.surveillanceplugin.matm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;

import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.fusersurveillance.FuserSurveillanceData;

public class FuserSurveillanceDataToMatmTransformTest extends FuserSurveillanceDataToMatmTransform{
	
	@Test
	public void testTransform() {
		FuserSurveillanceData surveillance = new FuserSurveillanceData();
		surveillance.setGufi("TEST123");
		surveillance.setLatitudeDegrees(32);
		surveillance.setLongitudeDegrees(-97);
		surveillance.setCallsign("callsign");
		Date timestamp = new Date();
		surveillance.setCallsign("callsign");
		
		FuserSurveillanceDataExtension extension = new FuserSurveillanceDataExtension(surveillance);
		extension.setSourceTimestamp(timestamp);
		
		MatmFlight flight = super.transform(extension);
		
		assertNotNull(flight);
		assertEquals("TEST123", flight.getGufi());
		assertEquals(32.0, flight.getPosition().getLatitude(), 1.0);
		assertEquals(-97, flight.getPosition().getLongitude(), 1.0);
		assertEquals(flight.getTimestamp(), timestamp);
		assertEquals(flight.getAcid(), "callsign");
	}

}
