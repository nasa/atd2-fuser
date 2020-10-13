package com.mosaicatm.surveillanceplugin.matm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.fusersurveillance.FuserSurveillanceData;

public class MatmToFuserSurveillanceTransformTest extends MatmToFuserSurveillanceTransform{

	@Test
	public void testTransform() {
		MatmFlight matm  = new MatmFlight();
		matm.setGufi("TEST123");
		matm.setAcid("acid");
		
		Position position = new Position();
		position.setLatitude(32.0);
		position.setLongitude(-97.0);
		matm.setPosition(position);
		FuserSurveillanceData surveillance = super.transform(matm);
		
		assertNotNull(surveillance);
		assertEquals("TEST123", surveillance.getGufi());
		assertEquals(32, surveillance.getLatitudeDegrees(), 1.0);
		assertEquals(-97, surveillance.getLongitudeDegrees(), 1.0);
		assertEquals("acid", surveillance.getCallsign());
	}
}
