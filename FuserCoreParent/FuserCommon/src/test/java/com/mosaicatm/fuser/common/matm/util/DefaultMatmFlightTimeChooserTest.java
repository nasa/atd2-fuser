package com.mosaicatm.fuser.common.matm.util;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.flight.DefaultMatmFlightTimeChooser;

public class DefaultMatmFlightTimeChooserTest
{

	@Test
	public void testFindMinimumDate()
	{
		Date date1 = null;
		Date date2 = null;
		
		assertNull(DefaultMatmFlightTimeChooser.findMinimumDate(date1, date2));
		
		date1 = new Date();
		assertEquals(date1,DefaultMatmFlightTimeChooser.findMinimumDate(date1, date2));
		
		date1 = null;
		date2 = new Date(2L);
		assertEquals(date2, DefaultMatmFlightTimeChooser.findMinimumDate(date1, date2));
		
		date1 = new Date(1L);
		assertEquals(date1, DefaultMatmFlightTimeChooser.findMinimumDate(date1, date2));
		
		date2 = new Date(1L);
		assertEquals(date1, DefaultMatmFlightTimeChooser.findMinimumDate(date1, date2));
		assertEquals(date2, DefaultMatmFlightTimeChooser.findMinimumDate(date1, date2));
		
		
	}

}
