package com.mosaicatm.fuser.updaters.post;

import java.util.Date;
import java.util.Map;
import javax.xml.bind.JAXBElement;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;

import com.mosaicatm.fuser.updaters.Updater;
import com.mosaicatm.fuser.util.MessageSender;
import com.mosaicatm.lib.messaging.MessageProducer;
import com.mosaicatm.matmdata.common.CancelledType;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.common.SurfaceFlightState;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;

public class FlightStateUpdaterTest
{
    private static Updater<MatmFlight, MatmFlight> updater;
    private static TestMessageProducer testMessageProducer;
    
    private final ObjectFactory objectFactory = new ObjectFactory();

    @BeforeClass
    public static void init()
    {
        updater = new FlightStateUpdater();

        testMessageProducer = new TestMessageProducer();
        
        MessageSender publisher = new MessageSender();
        publisher.setActive(true);
        publisher.setDestination("FAKE");
        publisher.setMessageProducer(testMessageProducer);
        
        ((FlightStateUpdater)updater).setMessagePublisher(publisher);
    }
    
    @Test
    public void testFlightStateProgression()
    {
        //UNKNOWN,
        //SCHEDULED,
        //FILED,
        //PUSHBACK,
        //RAMP_TAXI_OUT,
        //TAXI_OUT,
        //RETURN_TO_GATE,
        //IN_QUEUE,
        //OFF,
        //ENROUTE,
        //TERMINAL_AIRSPACE,
        //ON_FINAL,
        //AIRBORNE_DROPPED_TRACK,        
        //TAXI_IN,
        //RAMP_TAXI_IN,
        //IN_GATE,
        //SUSPENDED,
        //CANCELLED;          
        
        JAXBElement<Date> jaxbTime = objectFactory.createMatmFlightDepartureRunwayActualTime( new Date( 5 ));
        MatmFlight update = new MatmFlight();
        update.setTimestamp( new Date() );
        
        updater.update( update, null );        
        assertEquals( SurfaceFlightState.UNKNOWN, update.getFuserFlightState() );
        
        update.setScheduledFlight( true );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.SCHEDULED, update.getFuserFlightState() );
        
        // Surface flight update
        update.setFuserFlightState( null );
        update.setDepartureSurfaceFlightState( SurfaceFlightState.SCHEDULED );
        update.setArrivalSurfaceFlightState( SurfaceFlightState.SCHEDULED );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.SCHEDULED, update.getFuserFlightState() );        
        
        update.setFiledFlight( true );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.FILED, update.getFuserFlightState() );        
        
        update.setDepartureStandActualTime( jaxbTime );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.PUSHBACK, update.getFuserFlightState() );  
        
        // Surface flight update
        update.setFuserFlightState( null );
        update.setDepartureSurfaceFlightState( SurfaceFlightState.PUSHBACK );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.PUSHBACK, update.getFuserFlightState() );            
                
        // Surface flight update
        update.setFuserFlightState( null );
        update.setDepartureSurfaceFlightState( SurfaceFlightState.RAMP_TAXI_OUT );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.RAMP_TAXI_OUT, update.getFuserFlightState() );                  
        
        update.setDepartureMovementAreaActualTime( jaxbTime );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.TAXI_OUT, update.getFuserFlightState() );         
        
        // Surface flight update
        update.setFuserFlightState( null );
        update.setDepartureSurfaceFlightState( SurfaceFlightState.TAXI_OUT );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.TAXI_OUT, update.getFuserFlightState() );           
        
        // Surface flight update
        update.setFuserFlightState( null );
        update.setDepartureSurfaceFlightState( SurfaceFlightState.TAXI_OUT );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.TAXI_OUT, update.getFuserFlightState() );         
        
        // Surface flight update
        update.setFuserFlightState( null );
        update.setDepartureSurfaceFlightState( SurfaceFlightState.RETURN_TO_GATE );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.RETURN_TO_GATE, update.getFuserFlightState() );           
                
        // Surface flight update
        update.setFuserFlightState( null );
        update.setDepartureSurfaceFlightState( SurfaceFlightState.IN_QUEUE );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.IN_QUEUE, update.getFuserFlightState() );           
        
        // Surface flight update
        update.setFuserFlightState( null );
        update.setDepartureSurfaceFlightState( SurfaceFlightState.SUSPENDED );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.SUSPENDED, update.getFuserFlightState() );         
        
        update.setCancelled(CancelledType.CANCELLED);
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.CANCELLED, update.getFuserFlightState() ); 
        
        // Surface flight update
        update.setFuserFlightState( null );
        update.setDepartureSurfaceFlightState( SurfaceFlightState.CANCELLED );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.CANCELLED, update.getFuserFlightState() );           
        
        update.setDepartureRunwayActualTime( jaxbTime );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.OFF, update.getFuserFlightState() ); 
        
        // Surface flight update
        update.setFuserFlightState( null );
        update.setDepartureSurfaceFlightState( SurfaceFlightState.OFF );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.OFF, update.getFuserFlightState() );           
        
        update.setPosition( new Position() );
        update.getPosition().setTimestamp( new Date() );
        update.getPosition().setAltitude( 20000. );
        update.setExtensions( new MatmFlightExtensions() );
        update.getExtensions().setTfmExtension( new TfmExtension() );
        update.getExtensions().getTfmExtension().setLastTfmPosition( update.getPosition() );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.ENROUTE, update.getFuserFlightState() );  
        
        // Surface flight update
        update.setFuserFlightState( null );
        update.setDepartureSurfaceFlightState( SurfaceFlightState.ENROUTE );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.ENROUTE, update.getFuserFlightState() );          
        
        // Surface flight update
        update.setFuserFlightState( null );
        update.setArrivalSurfaceFlightState( SurfaceFlightState.TERMINAL_AIRSPACE );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.TERMINAL_AIRSPACE, update.getFuserFlightState() );  

        // Surface flight update
        update.setFuserFlightState( null );
        update.setArrivalSurfaceFlightState( SurfaceFlightState.ON_FINAL );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.ON_FINAL, update.getFuserFlightState() );          
                
        MatmFlight target = (MatmFlight) update.clone();
        target.setFuserFlightState( null );
        target.getPosition().setTimestamp( new Date( 5000 ));
        target.getExtensions().getTfmExtension().setLastTfmPosition( target.getPosition() );        
        
        update.setPosition( null );
        update.getExtensions().getTfmExtension().setLastTfmPosition( null );
        update.setFuserFlightState( null );
        updater.update( update, target );
        assertEquals( SurfaceFlightState.AIRBORNE_DROPPED_TRACK, update.getFuserFlightState() );           
        
        update.setArrivalRunwayActualTime( new Date() );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.TAXI_IN, update.getFuserFlightState() );  
        
        // Surface flight update
        update.setFuserFlightState( null );
        update.setArrivalSurfaceFlightState( SurfaceFlightState.TAXI_IN );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.TAXI_IN, update.getFuserFlightState() );          
        
        update.setArrivalMovementAreaActualTime( new Date() );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.RAMP_TAXI_IN, update.getFuserFlightState() );  
        
        // Surface flight update
        update.setFuserFlightState( null );
        update.setArrivalSurfaceFlightState( SurfaceFlightState.RAMP_TAXI_IN );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.RAMP_TAXI_IN, update.getFuserFlightState() );         
        
        update.setArrivalStandActualTime( new Date() );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.IN_GATE, update.getFuserFlightState() );   
        
        // Surface flight update
        update.setFuserFlightState( null );
        update.setArrivalSurfaceFlightState( SurfaceFlightState.IN_GATE );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.IN_GATE, update.getFuserFlightState() );          
        
        // And now, reverse it...
        target.setDepartureSurfaceFlightState( null );
        target.setArrivalSurfaceFlightState( null );        
        update.setDepartureSurfaceFlightState( null );
        update.setArrivalSurfaceFlightState( null );
        
        update.setArrivalStandActualTime( null );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.RAMP_TAXI_IN, update.getFuserFlightState() ); 
        
        update.setArrivalMovementAreaActualTime( null );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.TAXI_IN, update.getFuserFlightState() );        
        
        update.setArrivalRunwayActualTime( null );
        update.setFuserFlightState( null );
        updater.update( update, target );
        assertEquals( SurfaceFlightState.AIRBORNE_DROPPED_TRACK, update.getFuserFlightState() );          
        
        update.setPosition((Position) target.getPosition().clone() );
        update.getPosition().setTimestamp( new Date() );
        update.getExtensions().getTfmExtension().setLastTfmPosition( update.getPosition() );  
        update.setFuserFlightState( null );        
        updater.update( update, target );
        assertEquals( SurfaceFlightState.ENROUTE, update.getFuserFlightState() );  

        update.setPosition( null );
        update.getExtensions().getTfmExtension().setLastTfmPosition( null );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.OFF, update.getFuserFlightState() );    
        
        update.setDepartureRunwayActualTime( null );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.CANCELLED, update.getFuserFlightState() );    
        
        update.setCancelled( CancelledType.NOT_CANCELLED );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.TAXI_OUT, update.getFuserFlightState() ); 
        
        update.setDepartureMovementAreaActualTime( null );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.PUSHBACK, update.getFuserFlightState() );    

        update.setDepartureStandActualTime( null );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.FILED, update.getFuserFlightState() );       
        
        update.setFiledFlight( false );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.SCHEDULED, update.getFuserFlightState() );       
        
        update.setScheduledFlight( false );
        update.setFuserFlightState( null );
        updater.update( update, null );
        assertEquals( SurfaceFlightState.UNKNOWN, update.getFuserFlightState() );         
    }       
    
    @Test
    public void testTaxiInSweeperUpdate()
    {
        Date updateTime = new Date();
        
        MatmFlight target = new MatmFlight();
        target.setGufi( "GUFI" );
        target.setArrivalRunwayActualTime( updateTime );
        target.setFuserFlightState( SurfaceFlightState.TAXI_IN );
        
        // Stale position data
        Position pos = new Position();
        pos.setTimestamp( new Date( updateTime.getTime() - ( 7 * 60000 )));
        pos.setLatitude( 35.22 );
        pos.setLongitude( -80.95 );        
        pos.setAltitude( 1200. );
        
        target.setPosition( pos );  
        target.setExtensions( new MatmFlightExtensions() );
        target.getExtensions().setTfmExtension( new TfmExtension() );
        target.getExtensions().getTfmExtension().setLastTfmPosition( pos );        
                
        MatmFlight update = new MatmFlight();
        update.setGufi( "GUFI" );
        update.setFuserFlightState( SurfaceFlightState.AIRBORNE_DROPPED_TRACK );
        
        updater.update( update, target );
        
        assertNull( update.getFuserFlightState() );       
    }    
    
    @Test
    public void testSweeperUpdate()
    {
        Date updateTime = new Date();
        
        MatmFlight matmFlight = new MatmFlight();
        matmFlight.setGufi( "GUFI" );
               
        updater.sweeperUpdate( updateTime, matmFlight );
        
        assertNull( matmFlight.getFuserFlightState() );
        assertNull( testMessageProducer.getFlightUpdate() );   
        
        matmFlight.setFuserFlightState( SurfaceFlightState.ENROUTE );
        updater.sweeperUpdate( updateTime, matmFlight );
        
        assertEquals( SurfaceFlightState.ENROUTE, matmFlight.getFuserFlightState() );
        assertNull( testMessageProducer.getFlightUpdate() );          
        
        Position pos = new Position();
        pos.setTimestamp( updateTime );
        pos.setLatitude( 35.22 );
        pos.setLongitude( -80.95 );        
        pos.setAltitude( 18000. );
        
        matmFlight.setPosition( pos );  
        matmFlight.setExtensions( new MatmFlightExtensions() );
        matmFlight.getExtensions().setTfmExtension( new TfmExtension() );
        matmFlight.getExtensions().getTfmExtension().setLastTfmPosition( pos );
        
        updater.sweeperUpdate( updateTime, matmFlight );
        
        assertEquals( SurfaceFlightState.ENROUTE, matmFlight.getFuserFlightState() );
        assertNull( testMessageProducer.getFlightUpdate() ); 
        
        pos.setTimestamp( new Date( updateTime.getTime() - ( 2 * 60000 )));

        updater.sweeperUpdate( updateTime, matmFlight );

        assertEquals( SurfaceFlightState.ENROUTE, matmFlight.getFuserFlightState() );
        assertNull( testMessageProducer.getFlightUpdate() ); 

        pos.setTimestamp( new Date( updateTime.getTime() - ( 7 * 60000 )));
        
        updater.sweeperUpdate( updateTime, matmFlight );
        
        assertEquals( SurfaceFlightState.ENROUTE, matmFlight.getFuserFlightState() );
        assertEquals( "FUSER", testMessageProducer.getFlightUpdate().getLastUpdateSource() );
        assertEquals( FlightStateUpdater.FLIGHT_STATE_DROP_TRACK_SYSTEM_ID, testMessageProducer.getFlightUpdate().getSystemId() );        
        assertEquals( matmFlight.getGufi(), testMessageProducer.getFlightUpdate().getGufi() );        
        assertEquals( SurfaceFlightState.AIRBORNE_DROPPED_TRACK, testMessageProducer.getFlightUpdate().getFuserFlightState() );    
        
        testMessageProducer.clear();
        matmFlight.setFuserFlightState( SurfaceFlightState.TERMINAL_AIRSPACE );
        updater.sweeperUpdate( updateTime, matmFlight );

        assertEquals( SurfaceFlightState.TERMINAL_AIRSPACE, matmFlight.getFuserFlightState() );
        assertEquals( "FUSER", testMessageProducer.getFlightUpdate().getLastUpdateSource() );
        assertEquals( FlightStateUpdater.FLIGHT_STATE_DROP_TRACK_SYSTEM_ID, testMessageProducer.getFlightUpdate().getSystemId() );        
        assertEquals( matmFlight.getGufi(), testMessageProducer.getFlightUpdate().getGufi() );        
        assertEquals( SurfaceFlightState.AIRBORNE_DROPPED_TRACK, testMessageProducer.getFlightUpdate().getFuserFlightState() );    
        
        testMessageProducer.clear();
        matmFlight.setFuserFlightState( SurfaceFlightState.OFF );
        updater.sweeperUpdate( updateTime, matmFlight );

        assertEquals( SurfaceFlightState.OFF, matmFlight.getFuserFlightState() );   
        assertNull( testMessageProducer.getFlightUpdate() ); 
    }
    
    private static class TestMessageProducer implements MessageProducer
    {
        private MatmFlight flightUpdate;
                
        @Override
        public void publish( String arg0, Object arg1 )
        {
            flightUpdate = (MatmFlight) arg1;
        }

        @Override
        public void publish( String arg0, Object arg1, Map<String, Object> arg2 )
        {
            flightUpdate = (MatmFlight) arg1;
        }        

        public MatmFlight getFlightUpdate()
        {
            return flightUpdate;
        }
        
        public void clear()
        {
            flightUpdate = null;
        }

        @Override
        public <T> T request( String arg0, Object arg1 )
        {
            // TODO Auto-generated method stub
            return null;
        }        
    }
}
