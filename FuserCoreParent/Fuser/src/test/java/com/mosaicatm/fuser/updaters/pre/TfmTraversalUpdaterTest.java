package com.mosaicatm.fuser.updaters.pre;

import java.util.Date;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.TfmsFlightTraversalExtension;
import com.mosaicatm.matmdata.flight.extension.TraversalExtensionElement;
import com.mosaicatm.matmdata.flight.extension.WaypointTraversalExtensionElement;

public class TfmTraversalUpdaterTest
{
    private TfmTraversalUpdater updater;    
    
    @Before
    public void setup()
    {
        updater = new TfmTraversalUpdater();
        updater.setActive(true);
    }
    
    @Test
    public void testNullTraversalUpdate()
    {
        Date actualOff = getDate( "2020-05-05T10:00:00Z" );
        Date estimatedOff = getDate( "2020-05-05T10:10:00Z" );
        
        MatmFlight update = getFlight( actualOff, estimatedOff, 
                null, null, 
                null, null, 
                null, null, 
                null, null  );
                    
        updater.update(update, null);
        
        assertNotNull( update.getExtensions() );
        
        TfmsFlightTraversalExtension traversals = update.getExtensions().getTfmsFlightTraversalExtension();
        assertNotNull( traversals );
        assertTrue( traversals.getFixes().isEmpty() );
        assertTrue( traversals.getWaypoints().isEmpty() );
        assertTrue( traversals.getCenters().isEmpty() );
        assertTrue( traversals.getSectors().isEmpty() );
    }

    
    public void testSkipAlreadySetTraversalUpdate()
    {        
        Date estimatedOff = getDate( "2020-05-05T10:10:00Z" );
        
        MatmFlight update = getFlight( null, estimatedOff, 
                1, 2, 
                3, 4, 
                5, 6, 
                7, 8  );
                    
        updater.update(update, null);
        
        TfmsFlightTraversalExtension traversals = update.getExtensions().getTfmsFlightTraversalExtension();
        
        assertEquals( getDate( "2020-05-05T10:10:01Z" ), traversals.getFixes().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:02Z" ), traversals.getFixes().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:03Z" ), traversals.getWaypoints().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:04Z" ), traversals.getWaypoints().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:05Z" ), traversals.getCenters().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:06Z" ), traversals.getCenters().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:07Z" ), traversals.getSectors().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:08Z" ), traversals.getSectors().get( 1 ).getTraversalTime() );
        
        // Test setting a different departure time doesn't impact already-set traversals
        estimatedOff = getDate( "2020-05-05T20:30:00Z" );
        update.setDepartureRunwayEstimatedTime(estimatedOff);
        updater.update(update, null);
        
        traversals = update.getExtensions().getTfmsFlightTraversalExtension();
        
        assertEquals( getDate( "2020-05-05T10:10:01Z" ), traversals.getFixes().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:02Z" ), traversals.getFixes().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:03Z" ), traversals.getWaypoints().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:04Z" ), traversals.getWaypoints().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:05Z" ), traversals.getCenters().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:06Z" ), traversals.getCenters().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:07Z" ), traversals.getSectors().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:08Z" ), traversals.getSectors().get( 1 ).getTraversalTime() );        
    }
    
    public void testTraversalUpdate()
    {        
        Date estimatedOff = getDate( "2020-05-05T10:10:00Z" );
        
        MatmFlight update = getFlight( null, estimatedOff, 
                1, 2, 
                3, 4, 
                5, 6, 
                7, 8  );
                    
        updater.update(update, null);
        
        TfmsFlightTraversalExtension traversals = update.getExtensions().getTfmsFlightTraversalExtension();
        
        assertEquals( getDate( "2020-05-05T10:10:01Z" ), traversals.getFixes().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:02Z" ), traversals.getFixes().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:03Z" ), traversals.getWaypoints().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:04Z" ), traversals.getWaypoints().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:05Z" ), traversals.getCenters().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:06Z" ), traversals.getCenters().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:07Z" ), traversals.getSectors().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:10:08Z" ), traversals.getSectors().get( 1 ).getTraversalTime() );
        
        Date actualOff = getDate( "2020-05-05T10:00:00Z" );
        update = getFlight( actualOff, estimatedOff, 
                1, 2, 
                3, 4, 
                5, 6, 
                7, 8  );
                    
        updater.update(update, null);
        
        traversals = update.getExtensions().getTfmsFlightTraversalExtension();
        
        assertEquals( getDate( "2020-05-05T10:00:01Z" ), traversals.getFixes().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:00:02Z" ), traversals.getFixes().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:00:03Z" ), traversals.getWaypoints().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:00:04Z" ), traversals.getWaypoints().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:00:05Z" ), traversals.getCenters().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:00:06Z" ), traversals.getCenters().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:00:07Z" ), traversals.getSectors().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T10:00:08Z" ), traversals.getSectors().get( 1 ).getTraversalTime() );       
        
        // Test using target flights departure times
        actualOff = getDate( "2020-05-05T20:00:00Z" );
        
        MatmFlight target = getFlight( actualOff, estimatedOff, 
                1, 2, 
                3, 4, 
                5, 6, 
                7, 8  );
        
        update = getFlight( null, null, 
                1, 2, 
                3, 4, 
                5, 6, 
                7, 8  );
        
        updater.update(update, target);
        
        traversals = update.getExtensions().getTfmsFlightTraversalExtension();
        
        assertEquals( getDate( "2020-05-05T20:00:01Z" ), traversals.getFixes().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T20:00:02Z" ), traversals.getFixes().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T20:00:03Z" ), traversals.getWaypoints().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T20:00:04Z" ), traversals.getWaypoints().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T20:00:05Z" ), traversals.getCenters().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T20:00:06Z" ), traversals.getCenters().get( 1 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T20:00:07Z" ), traversals.getSectors().get( 0 ).getTraversalTime() );
        assertEquals( getDate( "2020-05-05T20:00:08Z" ), traversals.getSectors().get( 1 ).getTraversalTime() );                  
    }  
    
    private Date getDate( String date )
    {
        return( new Date( OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME ).toInstant().toEpochMilli() ));
    }
    
    private MatmFlight getFlight( Date actualOff, Date etd,
            Integer fixSeconds1, Integer fixSeconds2, 
            Integer waypointSeconds1, Integer waypointSeconds2,
            Integer centerSeconds1, Integer centerSeconds2,
            Integer sectorSeconds1, Integer sectorSeconds2 )
    {
        ObjectFactory factory = new ObjectFactory();
        
        MatmFlight matm = new MatmFlight();
        matm.setTimestampSource( new Date() );
        matm.setLastUpdateSource( "TFM" );
        matm.setSystemId( "TFM" );
                
        matm.setDepartureRunwayActualTime( factory.createMatmFlightDepartureRunwayActualTime( actualOff ));
        matm.setDepartureRunwayEstimatedTime( etd );
        
        TfmsFlightTraversalExtension ext = new TfmsFlightTraversalExtension();
        
        if( fixSeconds1 != null )
        {
            TraversalExtensionElement trav = new TraversalExtensionElement();
            trav.setElapsedSeconds( fixSeconds1 );                    
            ext.getFixes().add( trav );
            
            trav = new TraversalExtensionElement();
            trav.setElapsedSeconds( fixSeconds2 );                    
            ext.getFixes().add( trav );
        }
        
        if( waypointSeconds1 != null )
        {
            WaypointTraversalExtensionElement trav = new WaypointTraversalExtensionElement();
            trav.setElapsedSeconds( waypointSeconds1 );                    
            ext.getWaypoints().add( trav );
            
            trav = new WaypointTraversalExtensionElement();
            trav.setElapsedSeconds( waypointSeconds2 );                    
            ext.getWaypoints().add( trav );
        }        
        
        if( centerSeconds1 != null )
        {
            TraversalExtensionElement trav = new TraversalExtensionElement();
            trav.setElapsedSeconds( centerSeconds1 );                    
            ext.getCenters().add( trav );
            
            trav = new TraversalExtensionElement();
            trav.setElapsedSeconds( centerSeconds2 );                    
            ext.getCenters().add( trav );
        }  
        
        if( sectorSeconds1 != null )
        {
            TraversalExtensionElement trav = new TraversalExtensionElement();
            trav.setElapsedSeconds( sectorSeconds1 );                    
            ext.getSectors().add( trav );
            
            trav = new TraversalExtensionElement();
            trav.setElapsedSeconds( sectorSeconds2 );                    
            ext.getSectors().add( trav );
        }         
        
        MatmFlightExtensions extensions = new MatmFlightExtensions();
        extensions.setTfmsFlightTraversalExtension( ext );
        matm.setExtensions(extensions);        
        
        return( matm );
    }
}
