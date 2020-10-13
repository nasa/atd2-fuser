package com.mosaicatm.fuser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBException;

import org.apache.camel.Consume;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.EngineClass;
import com.mosaicatm.matmdata.common.WakeTurbulenceCategory;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import com.mosaicatm.matmdata.flight.extension.TfmControlIndicatorType;
import com.mosaicatm.matmdata.flight.extension.TfmMessageTriggerType;
import com.mosaicatm.matmdata.flight.extension.TfmMessageTypeType;
import com.mosaicatm.matmdata.flight.extension.tfmcomm.TfmSensitivityType;
import com.mosaicatm.tfm.thick.flight.mtfms.data.LatLon;
import com.mosaicatm.tfm.thick.flight.mtfms.data.TfmsFlightTransfer;

public class FuserTest extends CamelTestSupport 
{
    private final ObjectFactory objectFactory = new ObjectFactory();
    
    @Produce(uri = "direct:fuser.message")
    protected ProducerTemplate fuserProducer;
    
    @Produce(uri = "direct:tfm.message")
    protected ProducerTemplate tfmProducer;    

    @Consume(uri = "vm:toTfm")
    protected ConsumerTemplate tfmConsumer;    
    
    @Consume(uri = "vm:toFuser")
    protected ConsumerTemplate fuserConsumer;    
    
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint; 
            
    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:tfm.message").to("vm:toTfm");
                from("direct:fuser.message").to("vm:toFuser");
                from("vm:fuser.fromFuser.envelope.fused").
                        to("log:com.mosaicatm.fuser?level=DEBUG&maxChars=200").
                        to("mock:result");
            }
        };
    }     
    
    @BeforeClass
    public static void FuserTestSetup() throws IOException
    {
        FuserMain fuser = new FuserMain ();
        fuser.load( fuser.getProperties( new String[] {"test"}));
    }
    
    /**
     * This will execute the BeforeClass annotation -- to verify that the Spring context loads
     */
    @Test
    public void testStartup() {}
    
    /**
     * This test can be used for load testing of MatmFlight messages -- just comment out the ignore
     * @throws JAXBException
     * @throws ClassNotFoundException
     * @throws Exception
     */
    @Ignore
    @Test
    public void testPerformanceFuserOnly() throws JAXBException, ClassNotFoundException, Exception
    {
        int num_messages = 20000;
        int num_samples = 10;
                
        long total = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        
        for( int i = 0; i < num_samples; i++ )
        {
            long proc_time = sendMatmFlightMsgs( num_messages );
            total = total + proc_time;
            min = Math.min( min, proc_time );
            max = Math.max( max, proc_time );
        }
         
        log.info( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
        log.info( "Ran " + num_samples + " samples. Avg Time = " + ( total / num_samples ) + 
                ", Min Time = " + min + ", Max Time = " + max );
        log.info( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );         
    }  
    
    /**
     * This test can be used for load testing of TfmFlightTransfer messages -- just comment out the ignore
     * @throws JAXBException
     * @throws ClassNotFoundException
     * @throws Exception
     */
    @Ignore
    @Test
    public void testPerformanceFromTfm() throws JAXBException, ClassNotFoundException, Exception
    {
        int num_messages = 20000;        
        int num_samples = 10;
                
        long total = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        
        for( int i = 0; i < num_samples; i++ )
        {
            long proc_time = sendTfmFlightMsgs( num_messages );
            total = total + proc_time;
            min = Math.min( min, proc_time );
            max = Math.max( max, proc_time );
        }
         
        log.info( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
        log.info( "Ran " + num_samples + " samples. Avg Time = " + ( total / num_samples ) + 
                ", Min Time = " + min + ", Max Time = " + max );
        log.info( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );         
    }
    
    private long sendMatmFlightMsgs( int numMessages ) throws JAXBException, ClassNotFoundException, Exception
    {
        GenericMarshaller marshaller = new GenericMarshaller("com.mosaicatm.matmdata.flight.MatmFlight");
        marshaller.setMarshallHeader( false);
        resultEndpoint.reset();
        
        resultEndpoint.setPollingConsumerBlockWhenFull( false );
        resultEndpoint.setPollingConsumerQueueSize(numMessages );
        
        long start_time = System.currentTimeMillis();
        for(int i = 0; i < numMessages; i++ )
        {
            fuserProducer.sendBody( marshaller.marshall( getMatmFlight() ));
        }
        
        long end_time = waitForFuserEndpointToDrain();
        
        resultEndpoint.expectedMinimumMessageCount( 1 );
        resultEndpoint.assertIsSatisfied();
        
        long proc_millis = end_time - start_time;
        log.info( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
        log.info("Processed " + numMessages + " messages in " + 
             ( proc_millis ) + " millis" );
        log.info( "Fused batches = " + resultEndpoint.getReceivedCounter() );
        log.info( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" ); 
        
        return( proc_millis );
    }     
    
    private long sendTfmFlightMsgs( int numMessages ) throws JAXBException, ClassNotFoundException, Exception
    {
        GenericMarshaller marshaller = new GenericMarshaller("com.mosaicatm.tfm.thick.flight.mtfms.data.TfmsFlightTransfer");
        marshaller.setMarshallHeader( false);
        
        resultEndpoint.setPollingConsumerBlockWhenFull( false );
        resultEndpoint.setPollingConsumerQueueSize(numMessages );
        
        long start_time = System.currentTimeMillis();
        for(int i = 0; i < numMessages; i++ )
        {
            tfmProducer.sendBody( marshaller.marshall( getTfmFlight() ));
        }
        
        long end_time = waitForFuserEndpointToDrain();
        
        resultEndpoint.expectedMinimumMessageCount( 1 );
        resultEndpoint.assertIsSatisfied();
        
        long proc_millis = end_time - start_time;
        log.info( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
        log.info("Processed " + numMessages + " messages in " + 
             ( proc_millis ) + " millis" );
        log.info( "Fused batches = " + resultEndpoint.getReceivedCounter() );
        log.info( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" ); 
        
        return( proc_millis );
    }         
    
    private long waitForFuserEndpointToDrain() throws InterruptedException
    {
        resultEndpoint.await();
        
        int interations_to_confirm_data_complete = 25;
        int iterations = 0;
        int current_received_count = resultEndpoint.getReceivedCounter();
        int last_received_count = -1;
        long end_time = System.currentTimeMillis(); 
        
        // It's kinda complicated to verify when the data is done processing.
        // We need to keep checking the endpoint to see if the data is 
        // done processing. (Because the messages are batched, the number
        // of messages is less than what we put in).
        while(( last_received_count != current_received_count ) || 
                ( iterations < interations_to_confirm_data_complete ))
        { 
            iterations++;
            Thread.sleep( 200 );  
            
            last_received_count = current_received_count;
            current_received_count = resultEndpoint.getReceivedCounter();     
            
            if( last_received_count != current_received_count )
            { 
                end_time = System.currentTimeMillis();             
                iterations = 0;
            }            
        }

        return( end_time );
    }
    
    private MatmFlight getMatmFlight()
    {
        MatmFlight flight = new MatmFlight();
        
        flight.setGufi( getRandomGufi() );
        flight.setLastUpdateSource( "TFM" );
        flight.setAcid("AAL123");
        flight.setAircraftType( "B737" );
        flight.setAircraftRegistration("XYZ789");
        flight.setWakeTurbulenceCategory( WakeTurbulenceCategory.LARGE );
        flight.setAircraftEngineClass( EngineClass.JET );
        flight.setArrivalAerodrome(createAerodrome("CLT", null));
        flight.setDepartureAerodrome(createAerodrome("DFW", null));
        
        flight.setDepartureStandActualTime(objectFactory.createMatmFlightDepartureStandActualTime(createRandomDate()));
        flight.setDepartureStandEstimatedTime(createRandomDate());
        flight.setDepartureStandScheduledTime(createRandomDate());
        flight.setDepartureStandProposedTime(createRandomDate());
        
        flight.setArrivalRunwayScheduledTime(createRandomDate());
        flight.setArrivalRunwayEstimatedTime(createRandomDate());
        flight.setArrivalRunwayActualTime(createRandomDate());
        
        flight.setDepartureRunwayScheduledTime(createRandomDate());
        flight.setDepartureRunwayEstimatedTime(createRandomDate());
        flight.setDepartureRunwayActualTime(objectFactory.createMatmFlightDepartureRunwayActualTime(createRandomDate()));
        
        flight.setArrivalStandActualTime(createRandomDate());
        flight.setArrivalStandEstimatedTime(createRandomDate());
        flight.setArrivalStandScheduledTime(createRandomDate());
        flight.setArrivalStandProposedTime(createRandomDate());
        
        flight.setArrivalRunwayActual("13L");
        flight.setDepartureRunwayActual("18R");
        flight.setArrivalStandActual("Z26");
        flight.setDepartureStandActual("A1");
        
        return flight;
    }
    
    private TfmsFlightTransfer getTfmFlight()
    {
		long timestamp = System.currentTimeMillis();
		
		TfmsFlightTransfer transfer = new TfmsFlightTransfer ();
		
		transfer.setAircraftType("B737");
		transfer.setGufi(getRandomGufi());
		transfer.setAcid("PHI005");
		transfer.setArrAirport("MCO");
		transfer.setArrFix("MICKI");
		transfer.setArrFixEstimatedTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1)));
		transfer.setBeaconCode(9876);
		
		LatLon position = new LatLon ();
		position.setLatDeg(32.1234);
		position.setLonDeg(-96.0987);
		transfer.setCurrPos(position);
		
		transfer.setDepAirport("PHL");
		transfer.setDepFix("PODDE");
		transfer.setDepFixEstimatedTime(new Date(timestamp + (TimeFactory.MINUTE_IN_MILLIS * 15)));
		
		transfer.setArrivalStandActualTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 15)));
		transfer.setArrivalStandEstimatedTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 20)));
		transfer.setArrivalStandProposedTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 19)));
		
		transfer.setDepartureRunwayActualTime(new Date(timestamp));
		transfer.setDepartureRunwayEstimatedTime(new Date(timestamp + (TimeFactory.MINUTE_IN_MILLIS * 5)));
		transfer.setDepartureRunwayProposedTime(new Date(timestamp + (TimeFactory.MINUTE_IN_MILLIS * 4)));
        transfer.setDepartureRunwayMeteredTime(new Date(timestamp + (TimeFactory.MINUTE_IN_MILLIS * 7)));
        transfer.setDepartureRunwayScheduledTime(new Date(timestamp + (TimeFactory.MINUTE_IN_MILLIS * 8)));
		
		transfer.setArrivalRunwayActualTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 5)));
		transfer.setArrivalRunwayEstimatedTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 10)));
		transfer.setArrivalRunwayProposedTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 9)));
		transfer.setArrivalRunwayControlledTime( new Date(timestamp - (TimeFactory.MINUTE_IN_MILLIS * 23)));
        transfer.setArrivalRunwayScheduledTime( new Date(timestamp - (TimeFactory.MINUTE_IN_MILLIS * 24)));
        
		transfer.setDepartureStandActualTime(new Date(timestamp - (TimeFactory.MINUTE_IN_MILLIS * 15)));
		transfer.setDepartureStandEstimatedTime(new Date(timestamp - (TimeFactory.MINUTE_IN_MILLIS * 10)));
		transfer.setDepartureStandInitialTime(new Date(timestamp - (TimeFactory.MINUTE_IN_MILLIS * 20)));
		transfer.setDepartureStandProposedTime(new Date(timestamp - (TimeFactory.MINUTE_IN_MILLIS * 21)));
		
		transfer.setRouteString("PHL.PODDE..MICKI.MCO");
		
		transfer.setSpeed(Integer.valueOf(25).shortValue());
        transfer.setSpeedFiled(Integer.valueOf(250).shortValue());
		
		transfer.setTimeStamp(new Date(timestamp));
		
		transfer.setWeightClass("H");
		
		transfer.setFlightIndex(8778);
		transfer.setAltitudeType("B");
        transfer.setAltitude((short) 100 );
        transfer.setAltitudeFiled((short) 150 );
        transfer.setAltitudeRequested((short) 170 );
        transfer.setAltitudeAssigned((short) 180 );
		transfer.setAcEqpPrefix("H");
		transfer.setAcEqpSuffix("Q");
		transfer.setMessageType(TfmMessageTypeType.FLIGHT_TIMES.value());
		transfer.setNumAircraft("9");
		transfer.setPhysicalClass("PISTON");
		transfer.setUserClass("turbo");
        
        transfer.setMessageTrigger( TfmMessageTriggerType.CTOP_ROUTE_ASSIGNMENT.value() );
        transfer.setSensitivity( TfmSensitivityType.A.value() );
        transfer.setSensitivityReason( "FM" );
        transfer.setCanceled( Boolean.FALSE );
        transfer.setCdmParticipant( Boolean.TRUE );
        transfer.setDiversionIndicator( "NO_DIVERSION" );
        transfer.setDiversionCancelFlightIndex( 898989 );
        transfer.setDiversionCancelNewFlightIndex( 77365355 );
        transfer.setArrivalRunwayAirlineTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 30)));
        transfer.setArrivalRunwayOriginalControlledTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 31)));
        transfer.setArrivalStandAirlineTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 32)));
        transfer.setControlElement("CTLELEM");
        transfer.setControlIndicator( TfmControlIndicatorType.CONTROL_ACTIVE.value() );
        transfer.setDepartureRunwayAirlineTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 33)));
        transfer.setDepartureRunwayOriginalControlledTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 34)));
        transfer.setDepartureStandAirlineTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 35)));
        transfer.setFlightCreationTime(new Date(timestamp + (TimeFactory.HOUR_IN_MILLIS * 1) + (TimeFactory.MINUTE_IN_MILLIS * 36)));
		
		List<String> fixes = new ArrayList<String> ();
		fixes.add("PODDE");
		transfer.setListFixes(fixes);
		
		List<String> sectors = new ArrayList<String> ();
		sectors.add("BRAVO");
		transfer.setListSectors(sectors);
		
		return transfer;
	}        
    
    private String getRandomGufi()
    {
        float num_gufis = 100;
        
        return( "GUFI_" + ((int)( Math.random() * num_gufis )));
    }
    
    private Date createRandomDate ()
    {
        Random random = new Random ();
        return new Date(random.nextLong());
    }
    
    private Aerodrome createAerodrome (String iata, String icao)
    {
        Aerodrome aerodrome = new Aerodrome ();
        aerodrome.setIataName(iata);
        aerodrome.setIcaoName(icao);
        
        return aerodrome;
    }
}
