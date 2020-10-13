package com.mosaicatm.fuser.updaters.post;

import java.util.Date;
import java.util.Map;
import javax.xml.bind.JAXBElement;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mosaicatm.fuser.updaters.Updater;
import com.mosaicatm.fuser.util.FuserAirportCodeTranslator;
import com.mosaicatm.fuser.util.MessageSender;
import com.mosaicatm.lib.messaging.MessageProducer;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;

public class RunwayActualTimeUpdaterTest
{
    private static Updater<MatmFlight, MatmFlight> updater;
    private static TestMessageProducer testMessageProducer;
    
    private final ObjectFactory objectFactory = new ObjectFactory();        

    @BeforeClass
    public static void init()
    {
        updater = new RunwayActualTimeUpdater();
        FuserAirportCodeTranslator fuserAirportCodeTranslator = new FuserAirportCodeTranslator();
        fuserAirportCodeTranslator.init();
        
        ((RunwayActualTimeUpdater)updater).setAirportCodeTranslator( fuserAirportCodeTranslator );
        
        testMessageProducer = new TestMessageProducer();
        
        MessageSender publisher = new MessageSender();
        publisher.setActive(true);
        publisher.setDestination("FAKE");
        publisher.setMessageProducer(testMessageProducer);
        
        ((RunwayActualTimeUpdater)updater).setMessagePublisher(publisher);
    }
    
    @Test
    public void testDepartureTime()
    {
        MatmFlight update = new MatmFlight();
        
        Aerodrome dept = new Aerodrome();
        dept.setFaaLid( "CLT" );
        dept.setIataName( "CLT" );
        dept.setIcaoName( "KCLT" );
        update.setDepartureAerodrome( dept );
        
        // No position
        updater.update( update, null );
        
        assertNull( update.getDepartureRunwayActualTime() );
        
        Position pos = new Position();
        pos.setTimestamp( new Date() );
        pos.setLatitude( 35.22 );
        pos.setLongitude( -80.95 );        
        pos.setAltitude( 800. );
        pos.setSpeed( 20. );
        
        update.setPosition( pos );
        
        // Unknown source        
        updater.update( update, null );
        
        assertNull( update.getDepartureRunwayActualTime() );        

        // Unknown source, but speed and altitude match   
        pos.setSpeed( 81. );
        updater.update( update, null );
        
        assertEquals( pos.getTimestamp(), update.getDepartureRunwayActualTime().getValue() ); 
        
        // Successful detection -- using TFM source
        update.setExtensions( new MatmFlightExtensions() );
        update.getExtensions().setTfmExtension( new TfmExtension() );
        update.getExtensions().getTfmExtension().setLastTfmPosition( pos );
        
        updater.update( update, null );
        
        assertEquals( pos.getTimestamp(), update.getDepartureRunwayActualTime().getValue() );          
        
        // Real event setter overrules
        JAXBElement<Date> extTime = objectFactory.createMatmFlightDepartureRunwayActualTime( new Date( 5 ));
        update.setDepartureRunwayActualTime( extTime );
        updater.update( update, null );        
        
        assertEquals( extTime, update.getDepartureRunwayActualTime() );
        
        // Prevent too high
        update.setDepartureRunwayActualTime( null );
        pos.setAltitude( 20000. );
        updater.update( update, null );
        
        assertNull( update.getDepartureRunwayActualTime() );  
        
        // Back to success
        update.setDepartureRunwayActualTime( null );
        pos.setAltitude( 900. );
        updater.update( update, null );   
        
        assertEquals( pos.getTimestamp(), update.getDepartureRunwayActualTime().getValue() );        
        
        // Not close to airport
        update.setDepartureRunwayActualTime( null );
        pos.setLatitude( 36. );
        updater.update( update, null );   
        
        assertNull( update.getDepartureRunwayActualTime() );  
        
        // Back to success
        update.setDepartureRunwayActualTime( null );
        pos.setLatitude( 35.22 );
        updater.update( update, null );   
        
        assertEquals( pos.getTimestamp(), update.getDepartureRunwayActualTime().getValue() );           
        
        // Allow NILL-ing
        extTime.setValue( null );
        update.setDepartureRunwayActualTime( extTime );
        updater.update( update, null );        
        
        assertNull( update.getDepartureRunwayActualTime().getValue() ); 
    }
    
    @Test
    public void testArrivalTime()
    {
        MatmFlight target = new MatmFlight();
        MatmFlight update = new MatmFlight();
        update.setTimestamp( new Date() );
        
        Aerodrome arr = new Aerodrome();
        arr.setFaaLid( "CLT" );
        arr.setIataName( "CLT" );
        arr.setIcaoName( "KCLT" );
        target.setArrivalAerodrome( arr );
        update.setArrivalAerodrome( arr );
        
        // No position
        updater.update( update, target );
        
        assertNull( update.getArrivalRunwayActualTime() );
        
        Position pos = new Position();
        pos.setTimestamp( new Date() );
        pos.setLatitude( 35.22 );
        pos.setLongitude( -80.95 );        
        pos.setAltitude( 800. );
        
        target.setPosition( pos );
        
        // Unknown source        
        updater.update( update, target );
        
        assertNull( update.getArrivalRunwayActualTime() );        
        
        // Add TFM source
        target.setExtensions( new MatmFlightExtensions() );
        target.getExtensions().setTfmExtension( new TfmExtension() );
        target.getExtensions().getTfmExtension().setLastTfmPosition( pos );
        
        updater.update( update, target );
        
        assertNull( update.getArrivalRunwayActualTime() );
        
        // Update needs to be stale for successful update
        update.setTimestamp( new Date( pos.getTimestamp().getTime() + ( 6 * 60000 )));
        updater.update( update, target );
        
        assertEquals( pos.getTimestamp(), update.getArrivalRunwayActualTime() );          
        
        // Real event setter overrules
        Date extTime = new Date( 5 );
        update.setArrivalRunwayActualTime( extTime );
        updater.update( update, target );        
        
        assertEquals( extTime, update.getArrivalRunwayActualTime() );
        
        // Prevent too high
        update.setArrivalRunwayActualTime( null );
        pos.setAltitude( 20000. );
        updater.update( update, target );
        
        assertNull( update.getArrivalRunwayActualTime() );  
        
        // Back to success
        update.setArrivalRunwayActualTime( null );
        pos.setAltitude( 900. );
        updater.update( update, target );   
        
        assertEquals( pos.getTimestamp(), update.getArrivalRunwayActualTime() );        

        // Use ETA if reasonable
        Date eta = new Date( pos.getTimestamp().getTime() + 5 * 60 * 1000 );
        target.setArrivalRunwayEstimatedTime( eta );
        update.setArrivalRunwayActualTime( null );
        updater.update( update, target );   
        
        assertEquals( eta, update.getArrivalRunwayActualTime() );  

        // Not reasonable ETA        
        eta = new Date( pos.getTimestamp().getTime() + 30 * 60 * 1000 );
        target.setArrivalRunwayEstimatedTime( eta );
        update.setArrivalRunwayActualTime( null );
        updater.update( update, target );   
        
        assertEquals( pos.getTimestamp(), update.getArrivalRunwayActualTime() );  
        
        // Not close to airport
        update.setArrivalRunwayActualTime( null );
        pos.setLatitude( 36. );
        updater.update( update, target );   
        
        assertNull( update.getArrivalRunwayActualTime() );  
    }    
    
    
    @Test
    public void testSweeperUpdate()
    {
        Date posTime = new Date();
        Date staleUpdateTime = new Date( posTime.getTime() + ( 6 * 60000 ));
        
        MatmFlight matmFlight = new MatmFlight();
        matmFlight.setGufi( "GUFI" );
        
        Aerodrome arr = new Aerodrome();
        arr.setFaaLid( "CLT" );
        arr.setIataName( "CLT" );
        arr.setIcaoName( "KCLT" );
        matmFlight.setArrivalAerodrome( arr );
        
        // No position
        updater.sweeperUpdate(staleUpdateTime, matmFlight );
        
        assertNull(matmFlight.getArrivalRunwayActualTime() );
        assertNull( testMessageProducer.getFlightUpdate() );
        
        Position pos = new Position();
        pos.setTimestamp( posTime );
        pos.setLatitude( 35.22 );
        pos.setLongitude( -80.95 );        
        pos.setAltitude( 800. );
        
        matmFlight.setPosition( pos );
        
        // Unknown source        
        updater.sweeperUpdate(staleUpdateTime, matmFlight );
        
        assertNull(matmFlight.getArrivalRunwayActualTime() );      
        assertNull( testMessageProducer.getFlightUpdate() );
        
        // Add TFM source
        matmFlight.setExtensions( new MatmFlightExtensions() );
        matmFlight.getExtensions().setTfmExtension( new TfmExtension() );
        matmFlight.getExtensions().getTfmExtension().setLastTfmPosition( pos );
        
        updater.sweeperUpdate(posTime, matmFlight );
        
        assertNull(matmFlight.getArrivalRunwayActualTime() );
        assertNull( testMessageProducer.getFlightUpdate() );
        
        // Update needs to be stale for successful update
        updater.sweeperUpdate(staleUpdateTime, matmFlight );
        
        assertEquals( "FUSER", testMessageProducer.getFlightUpdate().getLastUpdateSource() );
        assertEquals( RunwayActualTimeUpdater.FUSER_ACTUAL_ARRIVAL_TIME_SYSTEM_ID, testMessageProducer.getFlightUpdate().getSystemId() );        
        assertEquals( matmFlight.getGufi(), testMessageProducer.getFlightUpdate().getGufi() );        
        assertEquals( arr, testMessageProducer.getFlightUpdate().getArrivalAerodrome() );        
        assertEquals( pos.getTimestamp(), testMessageProducer.getFlightUpdate().getArrivalRunwayActualTime() );          
        
        // Real event setter overrules
        testMessageProducer.clear();
        Date extTime = new Date( 5 );
        matmFlight.setArrivalRunwayActualTime( extTime );
        updater.sweeperUpdate(staleUpdateTime, matmFlight );
        
        assertEquals(extTime, matmFlight.getArrivalRunwayActualTime() );
        assertNull( testMessageProducer.getFlightUpdate() );
        
        // Prevent too high
        matmFlight.setArrivalRunwayActualTime( null );
        pos.setAltitude( 20000. );
        updater.sweeperUpdate(staleUpdateTime, matmFlight );
        
        assertNull(matmFlight.getArrivalRunwayActualTime() );  
        assertNull( testMessageProducer.getFlightUpdate() );
        
        // Back to success
        matmFlight.setArrivalRunwayActualTime( null );
        pos.setAltitude( 900. );
        updater.sweeperUpdate(staleUpdateTime, matmFlight );
        
        assertEquals( pos.getTimestamp(), testMessageProducer.getFlightUpdate().getArrivalRunwayActualTime() );    
        
        // Not close to airport
        testMessageProducer.clear();
        matmFlight.setArrivalRunwayActualTime( null );
        pos.setLatitude( 36. );
        updater.sweeperUpdate(staleUpdateTime, matmFlight );
        
        assertNull(matmFlight.getArrivalRunwayActualTime() );  
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
    }
}
