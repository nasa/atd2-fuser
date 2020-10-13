package com.mosaicatm.fuser.filter;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.mosaicatm.fuser.filter.FlightPositionFilter;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class FlightPositionFilterTest
{

	@Test
	public void test()
	{
		FlightPositionFilter positionFilter = new FlightPositionFilter();
		positionFilter.setActive(true);
		
		MatmFlight update = new MatmFlight();
		update.setPosition(new Position());
		
		positionFilter.filter(update);
		
		assertTrue(null == update.getPosition());
		
		Position position = new Position();
		position.setTimestamp(new Date());
		update.setPosition(position);
		positionFilter.filter(update);
		assertTrue(null == update.getPosition());
		
		position = new Position();
		position.setLatitude(100D);
		update.setPosition(position);
		positionFilter.filter(update);
		assertTrue(null == update.getPosition());
		
		position = new Position();
		position.setLongitude(100D);
		update.setPosition(position);
		positionFilter.filter(update);
		assertTrue(null == update.getPosition());
		
		position = new Position();
		position.setTimestamp(new Date());
		position.setLatitude(100D);
		update.setPosition(position);
		positionFilter.filter(update);
		assertTrue(null == update.getPosition());
		
		position = new Position();
		position.setTimestamp(new Date());
		position.setLatitude(100D);
		position.setLongitude(100D);
		update.setPosition(position);
		positionFilter.filter(update);
		Assert.assertEquals(position, update.getPosition());
		
	}

}
