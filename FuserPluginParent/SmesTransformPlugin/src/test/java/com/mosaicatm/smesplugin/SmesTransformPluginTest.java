package com.mosaicatm.smesplugin;

import static org.junit.Assert.*;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.mosaicatm.smes.transfer.SmesMessageTransferEnvelope;
import org.junit.Test;


public class SmesTransformPluginTest {

    @Test
    public void testMarshal() throws Exception {
        String xml = "" +
                "<smesMessageTransferEnvelope>\n" +
                "    <smesMessageTransferList>\n" +
                "        <smesMessages>\n" +
                "            <aircraftAddress>10753586</aircraftAddress>\n" +
                "            <airport>KMIA</airport>\n" +
                "            <altitude>193.75</altitude>\n" +
                "            <callSign>UAL225T</callSign>\n" +
                "            <departureAirport>KMIA</departureAirport>\n" +
                "            <destinationAirport>KORD</destinationAirport>\n" +
                "            <eramGufi>KR67474200</eramGufi>\n" +
                "            <eventStatus>airborne</eventStatus>\n" +
                "            <eventTime>2020-02-03T21:29:10.645Z</eventTime>\n" +
                "            <eventType>off</eventType>\n" +
                "            <latitudeDegrees>25.80193</latitudeDegrees>\n" +
                "            <longitudeDegrees>-80.27301</longitudeDegrees>\n" +
                "            <mode3ACode>2302</mode3ACode>\n" +
                "            <pastEvents>\n" +
                "                <pastEvents>\n" +
                "                    <eventTime>2020-02-03T21:21:37.636Z</eventTime>\n" +
                "                    <eventType>spotout</eventType>\n" +
                "                </pastEvents>\n" +
                "            </pastEvents>\n" +
                "            <sfdpsGufi>us.fdps.2020-02-03T18:44:34Z.000/17/200</sfdpsGufi>\n" +
                "            <surfaceTrackIdentifier>498776</surfaceTrackIdentifier>\n" +
                "            <timestampSourceProcessed>2020-02-03T21:29:20.015Z</timestampSourceProcessed>\n" +
                "            <timestampSourceReceived>2020-02-03T21:29:10.786Z</timestampSourceReceived>\n" +
                "            <track>169</track>\n" +
                "        </smesMessages>\n" +
                "        <smesMessages>\n" +
                "            <aircraftAddress>10664869</aircraftAddress>\n" +
                "            <airport>KLAS</airport>\n" +
                "            <altitude>2150.0</altitude>\n" +
                "            <callSign>VTE3621</callSign>\n" +
                "            <departureAirport>KSBP</departureAirport>\n" +
                "            <destinationAirport>KLAS</destinationAirport>\n" +
                "            <eramGufi>KL66413200</eramGufi>\n" +
                "            <eventStatus>onramp</eventStatus>\n" +
                "            <eventTime>2020-02-03T21:29:11.799Z</eventTime>\n" +
                "            <eventType>spotin</eventType>\n" +
                "            <latitudeDegrees>36.07901</latitudeDegrees>\n" +
                "            <longitudeDegrees>-115.14247</longitudeDegrees>\n" +
                "            <mode3ACode>4634</mode3ACode>\n" +
                "            <pastEvents>\n" +
                "                <pastEvents>\n" +
                "                    <eventTime>2020-02-03T21:22:58.719Z</eventTime>\n" +
                "                    <eventType>on</eventType>\n" +
                "                </pastEvents>\n" +
                "            </pastEvents>\n" +
                "            <sfdpsGufi>us.fdps.2020-02-03T18:26:53Z.000/11/200</sfdpsGufi>\n" +
                "            <surfaceTrackIdentifier>1187396</surfaceTrackIdentifier>\n" +
                "            <timestampSourceProcessed>2020-02-03T21:29:20.015Z</timestampSourceProcessed>\n" +
                "            <timestampSourceReceived>2020-02-03T21:29:11.931Z</timestampSourceReceived>\n" +
                "            <track>2042</track>\n" +
                "        </smesMessages>\n" +
                "        <smesMessages>\n" +
                "            <aircraftAddress>11387259</aircraftAddress>\n" +
                "            <airport>KDEN</airport>\n" +
                "            <altitude>5350.0</altitude>\n" +
                "            <callSign>AAL2635</callSign>\n" +
                "            <departureAirport>KDEN</departureAirport>\n" +
                "            <destinationAirport>KDFW</destinationAirport>\n" +
                "            <eramGufi>KD72756200</eramGufi>\n" +
                "            <eventStatus>onsurface</eventStatus>\n" +
                "            <eventTime>2020-02-03T21:29:12.654Z</eventTime>\n" +
                "            <eventType>spotout</eventType>\n" +
                "            <latitudeDegrees>39.85206</latitudeDegrees>\n" +
                "            <longitudeDegrees>-104.68338</longitudeDegrees>\n" +
                "            <mode3ACode>2777</mode3ACode>\n" +
                "            <pastEvents/>\n" +
                "            <sfdpsGufi>us.fdps.2020-02-03T20:12:36Z.000/03/200</sfdpsGufi>\n" +
                "            <surfaceTrackIdentifier>132877</surfaceTrackIdentifier>\n" +
                "            <timestampSourceProcessed>2020-02-03T21:29:20.015Z</timestampSourceProcessed>\n" +
                "            <timestampSourceReceived>2020-02-03T21:29:12.741Z</timestampSourceReceived>\n" +
                "            <track>2484</track>\n" +
                "        </smesMessages>        \n" +
                "    </smesMessageTransferList>\n" +
                "    <timestampSourceProcessed>2020-02-03T21:29:20.015Z</timestampSourceProcessed>\n" +
                "    <timestampSourceReceived>2020-02-03T21:29:20.015Z</timestampSourceReceived>\n" +
                "</smesMessageTransferEnvelope>\n";


        Unmarshaller unmarshaller = JAXBContext.newInstance(SmesMessageTransferEnvelope.class).createUnmarshaller();

        SmesMessageTransferEnvelope message = (SmesMessageTransferEnvelope) unmarshaller.unmarshal(new StringReader(xml));

        assertEquals(3, message.getSmesMessageTransferList().size());
        assertEquals("UAL225T", message.getSmesMessageTransferList().get(0).getCallSign());
        assertEquals("VTE3621", message.getSmesMessageTransferList().get(1).getCallSign());
        assertEquals("AAL2635", message.getSmesMessageTransferList().get(2).getCallSign());
    }

}
