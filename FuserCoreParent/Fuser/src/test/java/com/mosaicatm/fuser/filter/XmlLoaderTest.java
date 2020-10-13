package com.mosaicatm.fuser.filter;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.mosaicatm.matmdata.common.FuserSource;

public class XmlLoaderTest {

	@Test
	public void test() {
		XmlLoader loader = new XmlLoader();
		loader.init("src/test/resources/AttributeFilterTest.xml");
		Map<FuserSource, List<String>> excludesArrival = loader.getExcludesArrival();
		
		assertTrue(excludesArrival.containsKey(FuserSource.ASDEX));
		List<String> asdexAttributes = excludesArrival.get(FuserSource.ASDEX);
		assertTrue(asdexAttributes.size() == 1);
		assertTrue(asdexAttributes.contains("extensions.asdexExtension.lastAsdexPositionArrival"));
		
		Map<FuserSource, List<String>> includesArrival = loader.getIncludesArrival();
        Map<FuserSource, List<String>> includesDeparture = loader.getIncludesDeparture();
		
		assertTrue(includesArrival.containsKey(FuserSource.AIRLINE));
		List<String> airlineAttributes = includesArrival.get(FuserSource.AIRLINE);
		assertTrue(airlineAttributes.size() == 2);
		assertTrue(airlineAttributes.contains("arrivalAerodrome.iataName"));
        assertTrue(airlineAttributes.contains("wakeTurbulenceCategory"));
		        
        assertTrue(includesDeparture.containsKey(FuserSource.AIRLINE));
        airlineAttributes = includesDeparture.get(FuserSource.AIRLINE);
        assertTrue(airlineAttributes.size() == 2);
        assertTrue(airlineAttributes.contains("departureFixActualTime"));
        assertTrue(airlineAttributes.contains("extensions.matmAirlineMessageExtension.dataSource")); 
		
		assertTrue(includesArrival.containsKey(FuserSource.TFM));
		List<String> tfmArrival = includesArrival.get(FuserSource.TFM);
		assertTrue(tfmArrival.size() == 5);
		assertTrue(tfmArrival.contains("extensions.tfmExtension.lastTfmPosition.altitude.arrival"));
		assertTrue(tfmArrival.contains("extensions.tfmExtension.lastTfmPosition.latitude.arrival"));
		assertTrue(tfmArrival.contains("extensions.tfmExtension.lastTfmPosition.longitude.arrival"));
		assertTrue(tfmArrival.contains("extensions.tfmExtension.lastTfmPosition.timestamp.arrival"));
		assertTrue(tfmArrival.contains("extensions.tfmExtension.diversion"));

        List<String> tfmDeparture = includesDeparture.get(FuserSource.TFM);
        assertTrue(tfmDeparture.size() == 5);
        assertTrue(tfmDeparture.contains("extensions.tfmExtension.lastTfmPosition.altitude.departure"));
        assertTrue(tfmDeparture.contains("extensions.tfmExtension.lastTfmPosition.latitude.departure"));
        assertTrue(tfmDeparture.contains("extensions.tfmExtension.lastTfmPosition.longitude.departure"));
        assertTrue(tfmDeparture.contains("extensions.tfmExtension.lastTfmPosition.timestamp.departure"));
        assertTrue(tfmDeparture.contains("extensions.tfmExtension.diversion"));
		
	}

}
