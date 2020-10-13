package com.mosaicatm.fuser.updaters.pre;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FlightHeadingCalculator
{
	private final Log log = LogFactory.getLog(FlightHeadingCalculator.class);
	
	private Map<String, double[]> adaptationAirportCoordinates;
	
	
	
	public Double calculateHeadingToAirport(String arrAirport, double[] newPosition)	
	{
		Double calculatedHeading = null;
		{
			// Here, flight does not have a heading after merging update.
			// If the update had a position, we need to calculate a heading. 
			if (newPosition != null)
			{			
				// No previous position, so calculate a heading using the newPosition and the coordinates of the destination airport
				double[] airportCoords = null;
				
				if (adaptationAirportCoordinates != null)
				{
					airportCoords = adaptationAirportCoordinates.get(arrAirport);
					
					// Attempt to find coords when arrAirport is value like KJFK but we key in map like JFK
					if (airportCoords == null && arrAirport != null && arrAirport.charAt(0) == 'K')
					{
						airportCoords = adaptationAirportCoordinates.get(arrAirport.substring(1));
					}
				}
				if(airportCoords != null)
				{
					calculatedHeading = calculateHeading(newPosition, airportCoords);
				}
				else
				{
					if (log.isTraceEnabled())
					{
						log.trace("Unable to calculate heading for arrival airport: " + arrAirport);
					}
				}
			}
		}	
		return calculatedHeading;
	}
		
	public void setAdaptationAirportCoordinates(
			Map<String, double[]> adaptationAirportCoordinates) {
		this.adaptationAirportCoordinates = adaptationAirportCoordinates;
	}
	
	/**
	 * Calculates the bearing from point1 to point2
	 * 
	 * @param point1 - Array of double, index 0 = latitude index 1 = longitude
	 * @param point2 - Array of double, index 0 = latitude index 1 = longitude 
	 * @return		The heading in degrees
	 */
	public Double calculateHeading(double[] point1, double[] point2)
	{		
		if (point1 == null || point2 == null)
			return null;
		
		double bearing = 0;
		
		double lat1 = Math.toRadians(point1[0]);
		double lng1 = Math.toRadians(point1[1]); 
		double lat2 = Math.toRadians(point2[0]);
		double lng2 = Math.toRadians(point2[1]); 
		
		double y = Math.sin(lng2 - lng1) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - 
			Math.sin(lat1) * Math.cos(lat2) * Math.cos(lng2 - lng1);
		
		bearing = Math.atan2(y, x);		
		bearing = Math.toDegrees(bearing);
		
		if (bearing < 0d)
			bearing += 360;
		
		return bearing;
	}
	
}
