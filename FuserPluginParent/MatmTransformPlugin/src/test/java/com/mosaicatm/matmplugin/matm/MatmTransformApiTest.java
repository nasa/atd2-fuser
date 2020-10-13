package com.mosaicatm.matmplugin.matm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.mosaicatm.fuser.transform.matm.SourceMatmTransformApiImpl;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.flight.MatmFlight;


public class MatmTransformApiTest {

	@Test
	public void testMatmTransformApi() throws Exception
	{
		SourceMatmTransformApiImpl<MatmFlight, MatmFlight> transformApiImpl =
				new SourceMatmTransformApiImpl<MatmFlight, MatmFlight>();
		
		//create and set the marshaller to test to and from XML methods
		GenericMarshaller marshaller = new GenericMarshaller(MatmFlight.class);
		transformApiImpl.setMarshaller(marshaller);
		
		//test to and from marshalling from the API
		testMatmTransformApiToAndFromXml( transformApiImpl );
	}
	
	/**
	 * The test will test using the to and from xml methods of the MatmTransformApi. It assumes the marshaller
	 * has already been set on the api
	 * @param matmTransformApi
	 */
	private void testMatmTransformApiToAndFromXml(SourceMatmTransformApiImpl<MatmFlight, MatmFlight> matmTransformApi)
	{
		MatmFlight matmFlight = MatmTestFlightUtil.createTestMatmFlight();
		
		//get the XML from the flight
		String xml = matmTransformApi.toXml(matmFlight);
		
		assertNotNull(xml);
		
		//create a flight from the XML
		MatmFlight fromXmlFlight = matmTransformApi.fromXml(xml);
		
		assertNotNull(fromXmlFlight);
		
		String xml2 = matmTransformApi.toXml(fromXmlFlight);
		
		//convert the flight to XML that was created from XML for comparison
		assertEquals(xml, xml2);
		
	}
	

}
