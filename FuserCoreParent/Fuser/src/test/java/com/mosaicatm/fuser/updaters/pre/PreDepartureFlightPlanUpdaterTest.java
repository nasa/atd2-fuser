package com.mosaicatm.fuser.updaters.pre;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightPlanUtil;
import com.mosaicatm.fuser.util.MessageSender;
import com.mosaicatm.lib.time.clock.SystemClock;
import com.mosaicatm.matmdata.common.FlightPlanListType;
import com.mosaicatm.matmdata.common.SurfaceFlightState;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.AefsExtension;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;
import com.mosaicatm.matmdata.flight.extension.TfmMessageTriggerType;
import com.mosaicatm.matmdata.flight.extension.TfmMessageTypeType;

public class PreDepartureFlightPlanUpdaterTest
{
    private static PreDepartureFlightPlanUpdater updater;
    private static MockMessageSender messageSender;
    
    @BeforeClass
    static public void setup ()
    {
        messageSender = new MockMessageSender();
        
        updater = new PreDepartureFlightPlanUpdater();
        updater.setClock( new SystemClock() );
        updater.setFlightPlanSourcesFromString( "TFM_FLIGHT_PLAN_INFORMATION, TFM_FLIGHT_PLAN_AMENDMENT_INFORMATION, FUSER");
        updater.setMessagePublisher( messageSender );
    }
    
    @Test
    public void testFilterOutPostDepartureUpdate()
    {
        MatmFlight target = getFlight( true, "TFM", "FLIGHT_PLAN_INFORMATION" );
        MatmFlight update = getFlight( false, "TFM", "FLIGHT_PLAN_INFORMATION" );
        
        updater.update( update, target );
        
        assertNull( update.getPreDepartureFlightPlanList() );
        
        target = getFlight( false, "TFM", "FLIGHT_PLAN_INFORMATION" );
        update = getFlight( true, "TFM", "FLIGHT_PLAN_INFORMATION" );
        
        updater.update( update, target );
        
        assertNull( update.getPreDepartureFlightPlanList() ); 
        
        target = getFlight( true, "TFM", "FLIGHT_PLAN_INFORMATION" );
        update = getFlight( true, "TFM", "FLIGHT_PLAN_INFORMATION" );
        
        updater.update( update, target );
        
        assertNull( update.getPreDepartureFlightPlanList() );  
    }
    
    @Test
    public void testFilterOutWrongSourceUpdate()
    {        
        MatmFlight target = getFlight( false, "TMA", "SystemId" );
        MatmFlight update = getFlight( false, "TMA", "SystemId" );
        
        updater.update( update, target );
        
        assertNull( update.getPreDepartureFlightPlanList() );
        assertNull( update.isMultipleFlightPlansIndicator() );
        
        target = getFlight( false, "TMA", "SystemId" );
        update = getFlight( true, "TFM", "FLIGHT_PLAN_INFORMATION" );
        
        updater.update( update, target );
        
        assertNull( update.getPreDepartureFlightPlanList() ); 
        assertNull( update.isMultipleFlightPlansIndicator() );
        
        target = getFlight( true, "TFM", "FLIGHT_PLAN_INFORMATION" );
        update = getFlight( true, "TFM", "FLIGHT_SCHEDULE_ACTIVATE" );
        
        updater.update( update, target );
        
        assertNull( update.getPreDepartureFlightPlanList() ); 
        assertNull( update.isMultipleFlightPlansIndicator() );
    }    
    
    @Test
    public void testSimpleUpdate()
    {
        Date pTime = new Date();
        
        MatmFlight target = getFlight( false, "TMA", "System" );
        MatmFlight update = getFlight( false, "TFM", "FLIGHT_PLAN_AMENDMENT_INFORMATION", pTime, "A" );
        
        updater.update( update, target );
        
        assertEquals( 1, update.getPreDepartureFlightPlanList().getFlightPlans().size() );
        assertEquals( update.getPreDepartureFlightPlanList().getFlightPlans().get( 0 ), MatmFlightPlanUtil.transformMatmFlightToFlightPlan( update )); 
        assertEquals( "A", update.getPreDepartureFlightPlanList().getFlightPlans().get( 0 ).getRouteText() );
        
        // Nothing should happen here -- same data
        target = update;
        update = getFlight( false, "TFM", "FLIGHT_PLAN_AMENDMENT_INFORMATION", pTime, "A" );
        
        updater.update( update, target );
        
        assertNull( update.getPreDepartureFlightPlanList() ); 
        
        // Change the route
        update = getFlight( false, "FUSER", "FUSER_UPDATE", pTime, "B" ); 
        
        updater.update( update, target );
        
        assertEquals( 1, update.getPreDepartureFlightPlanList().getFlightPlans().size() );
        assertEquals( update.getPreDepartureFlightPlanList().getFlightPlans().get( 0 ), MatmFlightPlanUtil.transformMatmFlightToFlightPlan( update ));          
        assertEquals( "B", update.getPreDepartureFlightPlanList().getFlightPlans().get( 0 ).getRouteText() );
        
        // Add a new route
        target = update;
        update = getFlight( new Date(), false, "TFM", "FLIGHT_PLAN_AMENDMENT_INFORMATION", "ZDC", "BBB", pTime, "C" ); 
        
        updater.update( update, target );
        
        assertEquals( 2, update.getPreDepartureFlightPlanList().getFlightPlans().size() );
        assertEquals( update.getPreDepartureFlightPlanList().getFlightPlans().get( 1 ), MatmFlightPlanUtil.transformMatmFlightToFlightPlan( update ));          
        assertEquals( "C", update.getPreDepartureFlightPlanList().getFlightPlans().get( 1 ).getRouteText() );        
    }    

    @Test
    public void testCancel()
    {
        messageSender.clear();
        
        Date pTime = new Date();
        
        MatmFlight target = getFlight( new Date(), false, "TFM", "FLIGHT_PLAN_AMENDMENT_INFORMATION", "ZTL", "777", pTime, "A" );
        target.setPreDepartureFlightPlanList( new FlightPlanListType() );
        target.getPreDepartureFlightPlanList().getFlightPlans().add( MatmFlightPlanUtil.transformMatmFlightToFlightPlan( target ));
        
        MatmFlight update = getFlight( new Date(), false, "TFM", "FLIGHT_PLAN_AMENDMENT_INFORMATION", "ZTL", "778", pTime, "B" );
                
        updater.update( update, target );
        
        assertEquals( 2, update.getPreDepartureFlightPlanList().getFlightPlans().size() );
        assertEquals( "A", update.getPreDepartureFlightPlanList().getFlightPlans().get( 0 ).getRouteText() );
        assertEquals( "B", update.getPreDepartureFlightPlanList().getFlightPlans().get( 1 ).getRouteText() );
        assertTrue( update.isMultipleFlightPlansIndicator() );
        assertFalse( update.getPreDepartureFlightPlanList().getFlightPlans().get( 1 ).isFlightPlanCanceled() );
        assertNull( messageSender.getPublished() );
        
        target = update;
        update = getFlight( false, "TFM", "FLIGHT_PLAN_CANCEL", "ZTL", "778" );
        cancel( update );
        
        updater.update( update, target );
        
        assertEquals( 2, update.getPreDepartureFlightPlanList().getFlightPlans().size() );
        assertEquals( "A", update.getPreDepartureFlightPlanList().getFlightPlans().get( 0 ).getRouteText() );
        assertEquals( "B", update.getPreDepartureFlightPlanList().getFlightPlans().get( 1 ).getRouteText() );
        assertFalse( update.isMultipleFlightPlansIndicator() );
        assertTrue( update.getPreDepartureFlightPlanList().getFlightPlans().get( 1 ).isFlightPlanCanceled() );
        assertEquals( messageSender.getPublished().getRouteText(), update.getPreDepartureFlightPlanList().getFlightPlans().get( 0 ).getRouteText() );
        assertEquals( FuserSource.FUSER.value(), messageSender.getPublished().getLastUpdateSource() );
        assertEquals( PreDepartureFlightPlanUpdater.FUSER_FLIGHT_PLAN_CANCEL_SYSTEM_ID, messageSender.getPublished().getSystemId() );
    }    
    
    @Test
    public void testUpdateCurrentFlightPlanData()
    {
        messageSender.clear();
        
        Date pTime = new Date();
        
        MatmFlight target = getFlight( new Date(), false, "TFM", "FLIGHT_PLAN_INFORMATION", "ZTL", "222", pTime, "A" );
        target.setPreDepartureFlightPlanList( new FlightPlanListType() );
        target.getPreDepartureFlightPlanList().getFlightPlans().add( MatmFlightPlanUtil.transformMatmFlightToFlightPlan( target ));
        
        MatmFlight update = getFlight( new Date(), false, "TFM", "FLIGHT_PLAN_AMENDMENT_INFORMATION", "ZTL", "777", pTime, "B" );
        
        updater.update( update, target );
        
        assertEquals( 2, update.getPreDepartureFlightPlanList().getFlightPlans().size() );
        assertEquals( "A", update.getPreDepartureFlightPlanList().getFlightPlans().get( 0 ).getRouteText() );
        assertEquals( "B", update.getPreDepartureFlightPlanList().getFlightPlans().get( 1 ).getRouteText() );
        assertTrue( update.isMultipleFlightPlansIndicator() );
        assertFalse( update.getPreDepartureFlightPlanList().getFlightPlans().get( 0 ).isFlightPlanCanceled() );
        assertFalse( update.getPreDepartureFlightPlanList().getFlightPlans().get( 1 ).isFlightPlanCanceled() );
        // Don't publish message -- update is a new flight plan
        assertNull( messageSender.getPublished() );
        
        target = update;
        update = getFlight( new Date(), false, "TFM", "FLIGHT_PLAN_AMENDMENT_INFORMATION", "ZTL", "777", null, "C" );
        
        updater.update( update, target );
        
        assertEquals( 2, update.getPreDepartureFlightPlanList().getFlightPlans().size() );
        assertEquals( "A", update.getPreDepartureFlightPlanList().getFlightPlans().get( 0 ).getRouteText() );
        assertEquals( "C", update.getPreDepartureFlightPlanList().getFlightPlans().get( 1 ).getRouteText() );
        assertTrue( update.isMultipleFlightPlansIndicator() );
        assertFalse( update.getPreDepartureFlightPlanList().getFlightPlans().get( 0 ).isFlightPlanCanceled() );
        assertFalse( update.getPreDepartureFlightPlanList().getFlightPlans().get( 1 ).isFlightPlanCanceled() );
        // Don't publish message -- update facility / CID already matches current data
        assertNull( messageSender.getPublished() );
        
        target = update;
        update = getFlight( new Date(), false, "TFM", "FLIGHT_PLAN_AMENDMENT_INFORMATION", "ZTL", "222", null, null );        
        
        updater.update( update, target );
        
        // Here, there's no update to the pre-departure flight plan list, so no updates there.
        assertNull( update.getPreDepartureFlightPlanList() );
        assertNull( update.isMultipleFlightPlansIndicator() );
        // Publish message -- update facility / CID already has changed
        assertNotNull( messageSender.getPublished() );
        // FUSER update to switch flight to original route
        assertEquals( "A", messageSender.getPublished().getRouteText() );
        assertEquals( FuserSource.FUSER.value(), messageSender.getPublished().getLastUpdateSource() );
        assertEquals( PreDepartureFlightPlanUpdater.FUSER_FLIGHT_PLAN_CHANGE_SYSTEM_ID, messageSender.getPublished().getSystemId() );
    }     
    
    private MatmFlight getFlight( boolean departed, String lastUpdateSource, String systemId )
    {
        return( getFlight( new Date(), departed, lastUpdateSource, systemId, "ZTL", "555", new Date(), "ROUTE" ));
    }    
    
    private MatmFlight getFlight( boolean departed, String lastUpdateSource, String systemId, String sourceFacility, String computerId )
    {
        return( getFlight( new Date(), departed, lastUpdateSource, systemId, sourceFacility, computerId, new Date(), "ROUTE" ));
    }      
    
    private MatmFlight getFlight( boolean departed, String lastUpdateSource, String systemId, Date pTime, String route )
    {
        return( getFlight( new Date(), departed, lastUpdateSource, systemId, "ZTL", "555", pTime, route ));
    }      
    
    private MatmFlight getFlight( Date timestamp, boolean departed, String lastUpdateSource, String systemId, 
            String sourceFacility, String computerId, Date pTime, String route )
    {
        MatmFlight matm = new MatmFlight();
        matm.setTimestampSource( timestamp );
        matm.setLastUpdateSource( lastUpdateSource );
        matm.setSystemId( systemId );
        matm.setSourceFacility( sourceFacility );
        matm.setComputerId( computerId );
        matm.setDepartureStandProposedTime( pTime );
        matm.setRouteText( route );
        
        if( departed )
        {
            matm.setFuserFlightState(SurfaceFlightState.OFF);
        }
        
        return( matm );
    }
    
    private void cancel( MatmFlight matm )
    {
        matm.setExtensions( new MatmFlightExtensions() );
        
        if( matm.getLastUpdateSource().equals( "TFM" ))
        {
            matm.getExtensions().setTfmExtension( new TfmExtension() );
            matm.getExtensions().getTfmExtension().setMessageType( TfmMessageTypeType.FLIGHT_PLAN_CANCELLATION );
            matm.getExtensions().getTfmExtension().setMessageTrigger( TfmMessageTriggerType.HCS_CANCELLATION_MSG );
            matm.getExtensions().getTfmExtension().setSourceFacility( matm.getSourceFacility() );
            matm.getExtensions().getTfmExtension().setComputerId( matm.getComputerId() );
        }
        else
        {
            matm.getExtensions().setAefsExtension( new AefsExtension() );            
            matm.getExtensions().getAefsExtension().setRemoveStripFlag( Boolean.TRUE );
            matm.getExtensions().getAefsExtension().setSourceFacility( matm.getSourceFacility() );
            matm.getExtensions().getAefsExtension().setComputerId( matm.getComputerId() );            
        }
    }
    
    private static class MockMessageSender extends MessageSender<MatmFlight>
    {
        MatmFlight published;
                
        @Override
        public void publish( MatmFlight message ) throws Exception
        {
            published = message;
        }
        
        public MatmFlight getPublished()
        {
            return( published );
        }
        
        public void clear()
        {
            published = null;
        }
    }
}
