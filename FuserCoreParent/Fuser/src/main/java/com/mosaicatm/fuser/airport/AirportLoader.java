package com.mosaicatm.fuser.airport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.mosaicatm.fuser.airport.Adaptation.Airports.Airport;

public class AirportLoader
{
	private Map<String, double[]> adaptationAirportCoordinates;
	private String adaptationFile = "config/fuser/airports/airports.xml";
	
	public void init() throws JAXBException, IOException
	{
		adaptationAirportCoordinates = new HashMap<String, double[]>();
		
		JAXBContext jaxbContext = JAXBContext.newInstance(Adaptation.class);
		Unmarshaller um = jaxbContext.createUnmarshaller();
		
		Resource r = new ClassPathResource(adaptationFile);
		
		Adaptation adaptation = (Adaptation) um.unmarshal(r.getInputStream());
		
		for (Airport apt : adaptation.getAirports().getAirport())
		{
			String name = apt.getName();
			double lat = apt.getLat();
			double lon = apt.getLon() * -1; // ?
			
			adaptationAirportCoordinates.put(name, new double[] { lat, lon });
		}
	}
	
	public Map<String, double[]> getAdaptationAirportCoordinates()
	{
		return adaptationAirportCoordinates;
	}
	
	public void setAdaptationFile(String adaptationFile)
	{
		this.adaptationFile = adaptationFile;
	}	
}
