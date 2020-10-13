package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightCarrierUtil;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.CancelledType;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.common.SurfaceFlightState;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;

public class CancellationUpdaterTest 
{
    private final MatmFlightCarrierUtil matmFlightCarrierUtil = new MatmFlightCarrierUtil();
	private final CancellationUpdater cancellationUpdater = new CancellationUpdater();
	private final ObjectFactory objectFactory = new ObjectFactory();

    public CancellationUpdaterTest()
    {
        cancellationUpdater.setMatmFlightCarrierUtil( matmFlightCarrierUtil );
    }
    
	@Test
	public void testFilterOutNoStateChange()
    {
		MatmFlight flight = new MatmFlight();
		Aerodrome aerodrome = new Aerodrome();
		aerodrome.setIataName("DFW");
		flight.setDepartureAerodrome(aerodrome);
        MatmFlight update = new MatmFlight();
        Date cancel_time = new Date();

        // filter out an initial false value
        update.setCancelled( CancelledType.NOT_CANCELLED );
        update.setCancelledTime( cancel_time );
        cancellationUpdater.update( update, flight );
        assertNull( update.getCancelled() );
        assertNull( update.getCancelledTime() );  
        assertNull( update.getArrivalSurfaceFlightState() );
        assertNull( update.getDepartureSurfaceFlightState() );
        
        // Allow an initial true value
        update.setCancelled( CancelledType.CANCELLED );
        update.setCancelledTime( cancel_time );      
        update.setArrivalSurfaceFlightState( null );    
        update.setDepartureSurfaceFlightState( null );
        cancellationUpdater.update( update, flight );
        assertTrue( update.getCancelled() == CancelledType.CANCELLED );
        assertEquals( cancel_time, update.getCancelledTime() );
        assertEquals( SurfaceFlightState.CANCELLED, update.getArrivalSurfaceFlightState() );
        assertEquals( SurfaceFlightState.CANCELLED, update.getDepartureSurfaceFlightState() );        
        
        // Allow if flight is also true
        flight.setCancelled( CancelledType.CANCELLED );
        cancellationUpdater.update( update, flight );
        assertTrue( update.getCancelled() == CancelledType.CANCELLED );
        assertEquals( cancel_time, update.getCancelledTime() );
        assertEquals( SurfaceFlightState.CANCELLED, update.getArrivalSurfaceFlightState() );
        assertEquals( SurfaceFlightState.CANCELLED, update.getDepartureSurfaceFlightState() );
        
        // Allow if flight is false but update is true
        update.setCancelled( CancelledType.CANCELLED );
        update.setCancelledTime( cancel_time );        
        update.setDepartureSurfaceFlightState( null );
        flight.setCancelled( CancelledType.NOT_CANCELLED );
        flight.setCancelledTime( new Date() );
        
        cancellationUpdater.update( update, flight );
        assertTrue( update.getCancelled() == CancelledType.CANCELLED );
        assertEquals( cancel_time, update.getCancelledTime() );
        assertEquals( SurfaceFlightState.CANCELLED, update.getArrivalSurfaceFlightState() );
        assertEquals( SurfaceFlightState.CANCELLED, update.getDepartureSurfaceFlightState() );
        
        // Filter out a false if flight and update are both false 
        update.setDepartureSurfaceFlightState( null );
        update.setArrivalSurfaceFlightState( null );
        update.setCancelled( CancelledType.NOT_CANCELLED );
        flight.setCancelled( CancelledType.NOT_CANCELLED );
        cancellationUpdater.update( update, flight );
        assertNull( update.getCancelled() );
        assertNull( update.getCancelledTime() );  
        assertNull( update.getArrivalSurfaceFlightState() );
        assertNull( update.getDepartureSurfaceFlightState() );
	}
    
	@Test
	public void testUncancelChange()
    {
		MatmFlight flight = new MatmFlight();
        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setIataName("DFW");
        flight.setDepartureAerodrome(aerodrome);
        MatmFlight update = new MatmFlight();
        Date cancel_time = new Date();

        // Trigger an uncancelled state
        flight.setCancelled( CancelledType.CANCELLED );
        flight.setDepartureSurfaceFlightState( SurfaceFlightState.CANCELLED );
        flight.setCancelledTime( cancel_time );        
        update.setCancelled( CancelledType.NOT_CANCELLED );
        
        cancellationUpdater.update( update, flight );
        assertEquals( CancelledType.UNCANCELLED, update.getCancelled() );
        assertEquals( SurfaceFlightState.UNKNOWN, update.getDepartureSurfaceFlightState() );
    }
    
	@Test
	public void testUncancelToNotCancelChange()
    {
		MatmFlight flight = new MatmFlight();
        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setIataName("DFW");
        flight.setDepartureAerodrome(aerodrome);
        MatmFlight update = new MatmFlight();
        Date cancel_time = new Date();

        // Trigger an not cancelled update
        flight.setCancelled( CancelledType.UNCANCELLED );
        flight.setCancelledTime( cancel_time );        
        
        cancellationUpdater.update( update, flight );
        assertEquals( CancelledType.NOT_CANCELLED, update.getCancelled() );
        assertNull( update.getDepartureSurfaceFlightState() );
    }    
    
	@Test
	public void testIgnoreCancel()
    {
		MatmFlight flight = new MatmFlight();
        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setIataName("DFW");
        flight.setDepartureAerodrome(aerodrome);
        MatmFlight update = new MatmFlight();
        Date cancel_time = new Date( 100 );

        // Test ignore by update actual departure
        update.setDepartureRunwayActualTime(objectFactory.createMatmFlightDepartureRunwayActualTime(new Date( 101 )));
        update.setCancelled( CancelledType.CANCELLED );
        update.setCancelledTime( cancel_time );    
        
        cancellationUpdater.update( update, flight );
        assertNull( update.getCancelled() );
        assertNull( update.getCancelledTime() );
        assertNull( update.getDepartureSurfaceFlightState() );
        
        // Test ignore by target actual departure
        update.setDepartureRunwayActualTime( null );
        update.setCancelled( CancelledType.CANCELLED );
        update.setCancelledTime( cancel_time );     
        update.setDepartureSurfaceFlightState( null );
        flight.setDepartureRunwayActualTime(objectFactory.createMatmFlightDepartureRunwayActualTime(new Date( 101 )));
        
        cancellationUpdater.update( update, flight );
        assertNull( update.getCancelled() );
        assertNull( update.getCancelledTime() );
        assertNull( update.getDepartureSurfaceFlightState() );   
        
        // Reinit and make sure not filtered
        update.setCancelled( CancelledType.CANCELLED );
        update.setCancelledTime( cancel_time );     
        update.setDepartureSurfaceFlightState( null );        
        flight.setDepartureRunwayActualTime( null );
        
        cancellationUpdater.update( update, flight );
        assertTrue( update.getCancelled() == CancelledType.CANCELLED );
        assertEquals( cancel_time, update.getCancelledTime() );
        assertEquals( SurfaceFlightState.CANCELLED, update.getDepartureSurfaceFlightState() ); 
        
        // Test ignore by GA
        update.setAcid( "N893ST" );
        update.setCancelled( CancelledType.CANCELLED );
        update.setCancelledTime( cancel_time );
        update.setDepartureSurfaceFlightState( null );
        
        cancellationUpdater.update( update, flight );
        assertNull( update.getCancelled() );
        assertNull( update.getCancelledTime() );
        assertNull( update.getDepartureSurfaceFlightState() );      
        
        // Test not ignore for major
        update.setAcid( "AAL100" );
        update.setCancelled( CancelledType.CANCELLED );
        update.setCancelledTime( cancel_time );        
        
        cancellationUpdater.update( update, flight );
        assertTrue( update.getCancelled() == CancelledType.CANCELLED );
        assertEquals( cancel_time, update.getCancelledTime() );
        assertEquals( SurfaceFlightState.CANCELLED, update.getDepartureSurfaceFlightState() );          
    }     
    
    @Test
    public void testUncancelByStatus()
    {
		MatmFlight flight = new MatmFlight();
        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setIataName("DFW");
        flight.setArrivalAerodrome(aerodrome);
        MatmFlight update = new MatmFlight();
        Date cancel_time = new Date( 100 );

        // Test uncancelling a flight with actual gate out time
        //   First try won't cancel because the gate out times are the same 
        update.setDepartureStandActualTime( objectFactory.createMatmFlightDepartureStandActualTime( new Date( 101 ) ));
        update.setCancelled( CancelledType.CANCELLED );
        update.setCancelledTime( cancel_time );          
        flight.setDepartureStandActualTime( objectFactory.createMatmFlightDepartureStandActualTime( new Date( 101 ) ));
        flight.setCancelled( CancelledType.CANCELLED );
        flight.setDepartureSurfaceFlightState( SurfaceFlightState.CANCELLED );
        flight.setArrivalSurfaceFlightState( SurfaceFlightState.CANCELLED );
        flight.setCancelledTime( cancel_time );    
        
        cancellationUpdater.update( update, flight );
        assertTrue( update.getCancelled() == CancelledType.CANCELLED );
        assertNull( update.getCancelledTime() );
        assertEquals( SurfaceFlightState.CANCELLED, update.getArrivalSurfaceFlightState() );         
        
        // This one should uncancel, because the actual out time is later
        update.setDepartureStandActualTime( objectFactory.createMatmFlightDepartureStandActualTime( new Date( 102 ) ));
        update.setCancelled( CancelledType.CANCELLED );
        update.setCancelledTime( cancel_time );         
        
        cancellationUpdater.update( update, flight );        
        
        assertTrue( update.getCancelled() == CancelledType.UNCANCELLED );
        assertNull( update.getCancelledTime() );
        assertEquals( SurfaceFlightState.UNKNOWN, update.getArrivalSurfaceFlightState() );       
        
        // Test uncancelling a flight with actual departure status
        update = new MatmFlight();
        TfmExtension ext = new TfmExtension();
        ext.setLastTfmPosition( new Position() );
        update.setExtensions( new MatmFlightExtensions() );
        update.getExtensions().setTfmExtension( ext );         
        update.setTimestamp( new Date ( 200 ));
        flight.setDepartureStandActualTime( null );
        flight.setCancelled( CancelledType.CANCELLED );
        flight.setCancelledTime( cancel_time );          
        
        cancellationUpdater.update( update, flight );        
        
        assertTrue( update.getCancelled() == CancelledType.UNCANCELLED );
        assertNull( update.getCancelledTime() );
        assertEquals( SurfaceFlightState.UNKNOWN, update.getArrivalSurfaceFlightState() );         
    }  
}
