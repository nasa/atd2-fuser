package com.mosaicatm.fuser.datacapture.flat;

import com.mosaicatm.matmdata.flight.extension.LatLonExtension;
import com.mosaicatm.matmdata.flight.extension.TfmsFlightTraversalExtension;
import com.mosaicatm.matmdata.flight.extension.TraversalExtensionElement;
import com.mosaicatm.matmdata.flight.extension.WaypointTraversalExtensionElement;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FlatTfmsFlightTraversalExtensionTest
{
	@Test
	public void testFlatExt()
	{
		TfmsFlightTraversalExtension ext = new TfmsFlightTraversalExtension();
        ext.setSystemId("123");
		ext.setCenters(createElements("C100", 1500, 1, Instant.now()));
		ext.setFixes(createElements("F100", 2500, 2, Instant.now()));
		ext.setSectors(createElements("S100", 3500, 3, Instant.now()));
		ext.setWaypoints(createWaypoints("W100", 4500, 45.0, 55.0, 5));

		FlatTfmsFlightTraversalExtension flat = new FlatTfmsFlightTraversalExtension();
		ext.copyTo(flat);

		assertNotNull(flat.getSystemId());
        assertEquals(ext.getSystemId(), flat.getSystemId());
		assertNotNull(flat.getCenters());
        assertEquals(ext.getCenters(), flat.getCenters());
		assertNotNull(flat.getFixes());
        assertEquals(ext.getFixes(), flat.getFixes());
		assertNotNull(flat.getSectors());
        assertEquals(ext.getSectors(), flat.getSectors());
		assertNotNull(flat.getWaypoints());
        assertEquals(ext.getWaypoints(), flat.getWaypoints());
        
		assertNotNull(flat.getCenterTraversal());
		assertNotNull(flat.getFixTraversal());
		assertNotNull(flat.getSectorTraversal());
		assertNotNull(flat.getWaypointTraversal());
	}

	private List<TraversalExtensionElement> createElements(String name, long elapsed, int sequence,
			Instant traversalTime)
	{
		List<TraversalExtensionElement> centers = new ArrayList<>();
		TraversalExtensionElement center = new TraversalExtensionElement();
		center.setElapsedSeconds(elapsed);
		center.setName(name);
		center.setSequenceNumber(sequence);
		center.setTraversalTime(Date.from(traversalTime));

		centers.add(center);
		return centers;
	}

	private List<WaypointTraversalExtensionElement> createWaypoints(String name, int elapsed,
			double lat, double lon, int sequence)
	{
		List<WaypointTraversalExtensionElement> waypoints = new ArrayList<>();
		WaypointTraversalExtensionElement waypoint = new WaypointTraversalExtensionElement();

		LatLonExtension latlon = new LatLonExtension();
		latlon.setLatDeg(lat);
		latlon.setLonDeg(lon);
		waypoint.setPosition(latlon);
		waypoint.setElapsedSeconds(elapsed);
		waypoint.setName(name);
		waypoint.setSequenceNumber(sequence);
		waypoint.setTraversalTime(Date.from(Instant.now()));

		waypoints.add(waypoint);
		return waypoints;
	}

}