package com.mosaicatm.matmdata.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class DateTimeXmlAdapterTest
{
    @Test
    public void test() throws Exception
    {
        DateTimeXmlAdapter adapter = new DateTimeXmlAdapter();
        
        Date value = (new SimpleDateFormat("yyyyMMdd HHmmss")).parse("20170519 034400");
        MatmFlight flight = new MatmFlight();
        flight.setArrivalFixActualTime(value);
        
        // Test Marshalling
        Marshaller marshaller = JAXBContext.newInstance(MatmFlight.class).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        StringWriter outputWriter = new StringWriter();
        marshaller.marshal(flight, outputWriter);
        String xml = outputWriter.toString();
        
        String expected = adapter.marshal(value);
        
        assertTrue(xml.contains(expected));
        
        // Test Unmarshalling
        Unmarshaller unmarshaller = JAXBContext.newInstance(MatmFlight.class).createUnmarshaller();
        
        @SuppressWarnings("unchecked")
        JAXBElement<MatmFlight> jaxFlight = (JAXBElement<MatmFlight>)unmarshaller.unmarshal(new StringReader(xml));
        MatmFlight unmarshaledFlight = jaxFlight.getValue();
        
        assertEquals(value, unmarshaledFlight.getArrivalFixActualTime());
    }
}
