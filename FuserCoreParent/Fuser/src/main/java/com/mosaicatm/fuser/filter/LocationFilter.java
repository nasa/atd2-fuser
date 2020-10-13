package com.mosaicatm.fuser.filter;

import java.util.Arrays;
import java.util.List;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class LocationFilter 
implements MatmFilter<MatmFlight>
{
	public List<String> airports;

	private boolean active = false;
	
	@Override
	public MatmFlight filter(MatmFlight flight) 
	{
		if (!isActive() || flight == null) {
			return flight;
		}
		
		if(airports != null && !airports.isEmpty())
		{
			if( isIncludedArrivalAirport(flight)   || 
			    isIncludedDepartureAirport(flight) ||
			    isIncludedSurfaceAirport(flight)) 
			{
				return flight;
			}
		}
		return null;
	}

	private boolean isIncludedArrivalAirport(MatmFlight matm){
		if(matm.getArrivalAerodrome() != null)
			return (airports.contains(matm.getArrivalAerodrome().getIataName()) || airports.contains(matm.getArrivalAerodrome().getIcaoName()));
		return false;
	}

	private boolean isIncludedDepartureAirport(MatmFlight matm){
		if(matm.getDepartureAerodrome() != null)
			return (airports.contains(matm.getDepartureAerodrome().getIataName()) || airports.contains(matm.getDepartureAerodrome().getIcaoName()));
		return false;
	}

	private boolean isIncludedSurfaceAirport (MatmFlight matm) {
		if (matm.getSurfaceAirport() != null )
			return (airports.contains(matm.getSurfaceAirport().getIataName()) || airports.contains(matm.getSurfaceAirport().getIcaoName()));

		return false;
	}
	
	public void setAirports(String airportList) {
		this.airports = Arrays.asList(airportList.split(","));
	}

	@Override
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active)
	{
		this.active = active;
	}
}
