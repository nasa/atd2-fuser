package com.mosaicatm.sfdpsplugin.matm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.mosaicatm.sfdps.data.transfer.SfdpsFlightTransfer;
import org.junit.Ignore;
import org.junit.Test;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class SfdpsFlightTransferToMatmTransformTest
	extends SfdpsFlightTransferToMatmTransform
{
	@Test
	public void testTransformTH()
	{
		SfdpsFlightTransferToMatmTransform transform = new SfdpsFlightTransferToMatmTransformTest();
		SfdpsFlightTransfer transfer = new SfdpsFlightTransfer();
		Date receipt = new Date();
		Date timestamp = convertToDate("2017-04-07T18:29:59.909Z");
		String acid = "AAL43";
		String uuid = "58bd6106-0913-43eb-8c5d-e52f2b5ff3cb";
		String artcc = "ZOB";
		String msg = "TH";
		float latitude = 41.98222f;
		float longitude = -83.653056f;
		float altitude = 3600.0f;
		float speed = 224.0f;
        String atcSector = "ZOB55";
		transfer.setReceiptTime(receipt);
		transfer.setTimestamp(timestamp);
		transfer.setAcid(acid);
		transfer.setGufi(uuid);
		transfer.setCenter(artcc);
		//msg-type not transformed, in TFM the msg-type is put into an extension
		transfer.setMessageType(msg);
		transfer.setLatitudeDegrees(latitude);
		transfer.setLongitudeDegrees(longitude);
		transfer.setAltitudeFeet(altitude);
		transfer.setSpeedKnots(speed);
        transfer.setControllingSector(atcSector);
		MatmFlight matm = transform.transform(transfer);
		assertEquals("receipt time", receipt, matm.getTimestampSourceReceived());
		assertEquals("timestamp", timestamp, matm.getTimestampSource());
		assertEquals("acid", acid, matm.getAcid());
		assertEquals("gufi", uuid, matm.getGufi());
		assertEquals("last-source", "SFDPS", matm.getLastUpdateSource());
        assertEquals("messageType", msg, matm.getExtensions().getSfdpsExtension().getMessageType().name());
		assertEquals("latitude", latitude, matm.getPosition().getLatitude().floatValue(), 0.0001);
		assertEquals("longitude", longitude, matm.getPosition().getLongitude().floatValue(), 0.0001);
		assertEquals("altitude", altitude, matm.getPosition().getAltitude().floatValue(), 0.1);
		assertEquals("speed", speed, matm.getPosition().getSpeed().floatValue(), 0.1);
        assertEquals("atcSector", atcSector, matm.getPosition().getAtcSector().getValue());        
	}

	@Test
	public void testTransformFH()
	{
		SfdpsFlightTransferToMatmTransform transform = new SfdpsFlightTransferToMatmTransformTest();
		SfdpsFlightTransfer transfer = new SfdpsFlightTransfer();
		Date receipt = new Date();
		Date timestamp = convertToDate("2017-04-07T18:00:00.411Z");
		String acid = "DAL72";
		String uuid = "aa85ee0c-32d3-4100-842a-1044f941542e";
		String artcc = "ZTL";
		String msg = "FH";
		String registration = "N920DL";
		Long address = Long.parseLong("ACBE80", 16);
		transfer.setReceiptTime(receipt);
		transfer.setTimestamp(timestamp);
		transfer.setAcid(acid);
		transfer.setGufi(uuid);
		transfer.setCenter(artcc);
		transfer.setAircraftRegistration(registration);
		transfer.setAircraftAddress(address);
		//msg-type not transformed, in TFM the msg-type is put into an extension
		transfer.setMessageType(msg);
		MatmFlight matm = transform.transform(transfer);
		assertEquals("receipt time", receipt, matm.getTimestampSourceReceived());
		assertEquals("timestamp", timestamp, matm.getTimestampSource());
		assertEquals("acid", acid, matm.getAcid());
		assertEquals("gufi", uuid, matm.getGufi());
		assertEquals("last-source", "SFDPS", matm.getLastUpdateSource());
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
