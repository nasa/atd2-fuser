package com.mosaicatm.sfdpsplugin.matm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Ignore;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.sfdps.data.transfer.SfdpsFlightTransfer;
import com.mosaicatm.matmdata.common.ObjectFactory;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmToSfdpsFlightTransferTransformTest
	extends MatmToSfdpsFlightTransferTransform
{
    private ObjectFactory commonObjectFactory = new ObjectFactory();
    
	@Test
	public void testTransformTH()
	{
		MatmToSfdpsFlightTransferTransform transform = new MatmToSfdpsFlightTransferTransformTest();
		Date receipt = new Date();
		Date timestamp = convertToDate("2017-04-07T18:29:59.909Z");
		String acid = "AAL43";
		String uuid = "58bd6106-0913-43eb-8c5d-e52f2b5ff3cb";
		String artcc = "ZOB";
		String msg = "TH";
		double latitude = 41.98222;
		double longitude = -83.653056;
		double altitude = 3600.0;
		double speed = 224.0;
        String atcSector = "ZOB55";
		MatmFlight matm = new MatmFlight();
		matm.setTimestampSourceReceived(receipt);
		matm.setTimestampSource(timestamp);
		matm.setAcid(acid);
		matm.setGufi(uuid);
		matm.setLastUpdateSource("SFDPS");
		List<String> centers = new ArrayList<String>();
		centers.add(artcc);
		matm.setCenterList(centers);
		Position position = new Position();
		position.setLatitude(latitude);
		position.setLongitude(longitude);
		position.setAltitude(altitude);
		position.setSpeed(speed);
        position.setAtcSector(commonObjectFactory.createPositionAtcSector(atcSector));
		matm.setPosition(position);
		SfdpsFlightTransfer transfer =  transform.transform(matm);
		assertEquals("receipt time", receipt, transfer.getReceiptTime());
		assertEquals("timestamp", timestamp, transfer.getTimestamp());
		assertEquals("acid", acid, transfer.getAcid());
		assertEquals("gufi", uuid, transfer.getGufi());
		assertEquals("center", artcc, transfer.getCenter());
		assertEquals("latitude", latitude, transfer.getLatitudeDegrees().doubleValue(), 0.0001);
		assertEquals("longitude", longitude, transfer.getLongitudeDegrees().doubleValue(), 0.0001);
		assertEquals("altitude", altitude, transfer.getAltitudeFeet().doubleValue(), 0.1);
		assertEquals("speed", speed, transfer.getSpeedKnots().doubleValue(), 0.1);
        assertEquals("controllingSector", atcSector, transfer.getControllingSector());
	}

	@Test
	public void testTransformFH()
	{
		MatmToSfdpsFlightTransferTransform transform = new MatmToSfdpsFlightTransferTransformTest();
		Date receipt = new Date();
		Date timestamp = convertToDate("2017-04-07T18:00:00.411Z");
		String acid = "DAL72";
		String uuid = "aa85ee0c-32d3-4100-842a-1044f941542e";
		String artcc = "ZTL";
		String msg = "FH";
		String registration = "N920DL";
		Long address = Long.parseLong("ACBE80", 16);
		MatmFlight matm = new MatmFlight();
		matm.setTimestampSourceReceived(receipt);
		matm.setTimestampSource(timestamp);
		matm.setAcid(acid);
		matm.setGufi(uuid);
		matm.setLastUpdateSource("SFDPS");
		matm.setAircraftRegistration(registration);
		matm.setAircraftAddress(address.toString());
		List<String> centers = new ArrayList<String>();
		centers.add(artcc);
		matm.setCenterList(centers);
		Position position = new Position();
		matm.setPosition(position);
		SfdpsFlightTransfer transfer =  transform.transform(matm);
		assertEquals("receipt time", receipt, transfer.getReceiptTime());
		assertEquals("timestamp", timestamp, transfer.getTimestamp());
		assertEquals("acid", acid, transfer.getAcid());
		assertEquals("gufi", uuid, transfer.getGufi());
		assertEquals("center", artcc, transfer.getCenter());
		assertEquals("ac-reg", registration, transfer.getAircraftRegistration());
		assertEquals("ac-addr", address, transfer.getAircraftAddress());
	}

	@Ignore
	private Date convertToDate(String timestamp)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = null;
		try {
			date = sdf.parse(timestamp);
		} catch (ParseException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return date;
	}
}
