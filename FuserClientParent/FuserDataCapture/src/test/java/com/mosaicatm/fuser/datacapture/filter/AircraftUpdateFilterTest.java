package com.mosaicatm.fuser.datacapture.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class AircraftUpdateFilterTest
{
    private AircraftUpdateFilter filter;


    @Before
    public void setup(){
        filter = new AircraftUpdateFilter();
    }

    @Test
    public void testFilter()
    {
        MatmAircraft update = new MatmAircraft();
        update.setChanges(Arrays.asList("lastKnownPosition", "timestamp"));

        Set<String> uninterestedFields = new HashSet<String>();
        uninterestedFields.add("lastKnownPosition");
        uninterestedFields.add("timestamp");
        filter.setUninterestedFields(uninterestedFields);

        //Only uninterested fields are included - filter returns false (no valid fields)
        assertFalse(filter.doFilter(update));

        update.setChanges(Arrays.asList("lastKnownPosition", "timestamp", "lastKnownGufi"));

        //Only uninterested fields are included - filter returns true (found valid fields)
        assertTrue(filter.doFilter(update));
    }

    @Test
    public void testEmptyFilter()
    {
        MatmAircraft update = new MatmAircraft();
        update.setChanges(Arrays.asList("lastKnownPosition", "timestamp", "lastKnownGufi"));

        filter.setUninterestedFields(new HashSet<String>());

        //No 'uninterested' field specified - filter returns true (found valid fields)
        assertTrue(filter.doFilter(update));
    }

    @Test
    public void testNullFilter()
    {
        MatmAircraft update = new MatmAircraft();
        update.setChanges(Arrays.asList("lastKnownPosition", "timestamp", "lastKnownGufi"));

        filter.setUninterestedFields(null);

        //No 'uninterested' field specified - filter returns true (found valid fields)
        assertTrue(filter.doFilter(update));
    }

}
