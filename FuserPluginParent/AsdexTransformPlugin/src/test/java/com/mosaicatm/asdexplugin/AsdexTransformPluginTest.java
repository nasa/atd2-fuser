package com.mosaicatm.asdexplugin;

import static org.junit.Assert.*;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

//import com.mosaicatm.asdeximport.data.PositionMessageList;
import com.mosaicatm.asdex.transfer.PositionMessageList;



public class AsdexTransformPluginTest
{

    @Test
    public void testMarshal() throws Exception
    {
        String xml = "<position-list airport=\"KDFW\" asdexVersion=\"4.0\" processTime=\"2019-12-31T19:23:03.897Z\" receiveTime=\"2019-12-31T19:23:03.890Z\" xmlns:ns2=\"urn:us:gov:dot:faa:atm:terminal:entities:v2-0:smes:surfacemovementevent\">" + 
                "    <position>" + 
                "        <ns2:acid>AAL2270</ns2:acid>" +
                "        <ns2:altitude>10806.25</ns2:altitude>" + 
                "        <ns2:full>false</ns2:full>" + 
                "        <ns2:heading>186.4764404296875</ns2:heading>" + 
                "        <ns2:latitude>32.95533</ns2:latitude>" + 
                "        <ns2:longitude>-96.90473</ns2:longitude>" + 
                "        <ns2:speed>266</ns2:speed>" + 
                "        <ns2:stddsTrackId>1387760</ns2:stddsTrackId>" + 
                "        <ns2:time>2019-12-31T19:23:04Z</ns2:time>" + 
                "        <ns2:trackNumber>1088</ns2:trackNumber>" + 
                "        <ns2:velocityX>-12.0</ns2:velocityX>" + 
                "        <ns2:velocityY>-136.0</ns2:velocityY>" +
                "        <airport>DFW</airport>" + 
                "        <asdexId>49</asdexId>" + 
                "        <asdexVersion>4.0</asdexVersion>" + 
                "        <filtered>false</filtered>" + 
                "        <lastUpdateTime>1577820184000</lastUpdateTime>" + 
                "        <receivedFullMessage>false</receivedFullMessage>" + 
                "        <sendTo>all</sendTo>" + 
                "        <timeProcessed>2019-12-31T19:23:03.895Z</timeProcessed>" + 
                "        <timestampSourceProcess>2019-12-31T19:23:03.897Z</timestampSourceProcess>" + 
                "        <timestampSourceReceive>2019-12-31T19:23:03.890Z</timestampSourceReceive>" + 
                "    </position>" +
                "</position-list>";
        
        Unmarshaller unmarshaller = JAXBContext.newInstance(PositionMessageList.class).createUnmarshaller();
        
        PositionMessageList message = (PositionMessageList)unmarshaller.unmarshal(new StringReader(xml));
        
        assertEquals( 1, message.getElements().size() );
        assertEquals( "AAL2270", message.getElements().get( 0 ).getAcid() );
    }

}
