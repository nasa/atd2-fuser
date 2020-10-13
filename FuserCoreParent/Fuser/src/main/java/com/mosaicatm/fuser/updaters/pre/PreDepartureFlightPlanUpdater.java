package com.mosaicatm.fuser.updaters.pre;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.FuserSourceSystemType;
import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightPlanUtil;
import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightStateUtil;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.fuser.util.MatmFlightInternalUpdateUtil;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.matmdata.common.FlightPlanListType;
import com.mosaicatm.matmdata.common.FlightPlanType;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 *    This updater is responsible for maintaining a list of pre-departure flight plans for unique sourceFacilty/computerId.
 *    - When the update cancels the "current" flight plan, the updater triggers a MatmFlight message 
 *        to switch all flight plan data to the most recently updated one.
 *    - When the update changes the current sourceFacility/computerId, the updater triggers a MatmFlight message 
 *        to switch all flight plan data to the data for the sourceFacility/ComputerId.
 */
public class PreDepartureFlightPlanUpdater
extends AbstractUpdater <MatmFlight, MatmFlight>
{
    public static final String FUSER_FLIGHT_PLAN_CANCEL_SYSTEM_ID = "FUSER_FLIGHT_PLAN_CANCEL";
    public static final String FUSER_FLIGHT_PLAN_CHANGE_SYSTEM_ID = "FUSER_FLIGHT_PLAN_CHANGE";    
    
    private final Log log = LogFactory.getLog(getClass());
            
    private Set<FuserSourceSystemType> flightPlanSources = new HashSet<>();
    
    private Clock clock;

    public void setClock( Clock clock )
    {
        this.clock = clock;
    }
    
    public void setFlightPlanSources( Set<FuserSourceSystemType> flightPlanSources )
    {
        this.flightPlanSources = flightPlanSources;
    }
    
    public void setFlightPlanSourcesFromString( String flightPlanSourcesString )
    {
        Set<FuserSourceSystemType> fuserSources = new HashSet<>();
        
        if(( flightPlanSourcesString != null ) && ( !flightPlanSourcesString.isEmpty() ))
        {
            for( String fuserSourceSystem : flightPlanSourcesString.split( "," ))
            {
                fuserSourceSystem = fuserSourceSystem.trim();
                
                if( !fuserSourceSystem.isEmpty() )
                {
                    fuserSources.add( FuserSourceSystemType.valueOf( fuserSourceSystem ));
                }
            }            
        }        
        
        setFlightPlanSources( fuserSources );
    }    

    @Override
    public void update(MatmFlight update, MatmFlight target)
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update flight plans. Update is NULL!");
            return;
        }
        
        if( target == null )
        {
            target = update;
        }         

        // We only manage the predeparture flight plan list as key-ed by sourceFacility/computerId
        if(( update.getSourceFacility() == null ) || ( update.getComputerId() == null ))
        {
            return;
        }
        
        // Only manage pre-departure flight plan data
        if( MatmFlightStateUtil.isFlightDeparted( update ) || MatmFlightStateUtil.isFlightDeparted( target ))
        {
            return;
        }
        
        if( flightPlanSources.isEmpty() )
        {
            return;
        }
        
        boolean isFlightPlanSource = false;
        for( FuserSourceSystemType source : flightPlanSources )
        {
            if( source.matches( update ))
            {
                isFlightPlanSource = true;
                break;
            }
        }
        
        // First, update the flight plan list with most current stuff. We only
        // publish a change to the flight plan list if something changed.
        if( updateFlightPlanList( update, target, isFlightPlanSource )) 
        { 
            // These steps are only necessary if there was a change to the flight plan list:
            // Inject a cancel message if necessary
            handleFlightPlanCancel( update, target ); 
            // Set multiple flight plans
            handleMultipleFlightPlansIndicator( update ); 
        }

        // Check the current update's cid/facility and fix the flight plan data if this message would change it.
        if( isFlightPlanSource )
        {
            handleMatmFlightPlanChange( update, target ); 
        }
    }

    // Return true if the preDepartureFlightPlanList data was changed. No need to publish the list otherwise.
    private boolean updateFlightPlanList( MatmFlight update, MatmFlight target, boolean isFlightPlanSource )
    {
        boolean flightPlanDataUpdated = false;
        
        // Only update flight plan data from the flightPlanSource, or if we got a flight plan cancel
        if( isFlightPlanSource || MatmFlightPlanUtil.isFlightPlanCancelMessage( update ))
        {
            // Check if the update has any flight plan data (including cancel info). 
            if( MatmFlightPlanUtil.hasFlightPlanData( update ))
            {
                FlightPlanType updatePlan = null;
                List<FlightPlanType> updatePlans = null;
                
                // Get the preDepartureFlightPlanList from the target or make a new one. It will never be in an incoming update message.
                if(( target.getPreDepartureFlightPlanList() == null ) || target.getPreDepartureFlightPlanList().getFlightPlans().isEmpty() )
                {
                    updatePlans = new ArrayList<>();
                }
                else
                {
                    updatePlans = MatmFlightPlanUtil.getCopy( target.getPreDepartureFlightPlanList().getFlightPlans() );
                    updatePlan = MatmFlightPlanUtil.getFlightPlan( update.getSourceFacility(), update.getComputerId(), updatePlans );
                }
                    
                // Only update the plan data if the matmFlight msg is the primary flight plan source, 
                // or this is the first plan for this sourceFacility/CID (so we don't miss a canceled flight plan)
                if( isFlightPlanSource || ( updatePlan == null ))
                {
                    FlightPlanType updateMsgToFlightPlan = MatmFlightPlanUtil.transformMatmFlightToFlightPlan( update );

                    if( updatePlan == null )
                    {
                        updatePlans.add( updateMsgToFlightPlan );
                        flightPlanDataUpdated = true;
                    }
                    else if( MatmFlightPlanUtil.mergeUpdateIntoTarget( updateMsgToFlightPlan, updatePlan ))
                    {
                        flightPlanDataUpdated = true;                
                    }
                }
                // Cancel existing flight plan, but don't update any data because not flightPlanSource
                else if( Objects.equals( Boolean.FALSE, updatePlan.isFlightPlanCanceled() ) && 
                         MatmFlightPlanUtil.isFlightPlanCancelMessage( update ))
                {
                    updatePlan.setFlightPlanCanceled( true );
                    flightPlanDataUpdated = true;
                }
                
                // And add to updated preDepartureFlightPlanList to the MatmFlight update, but only if something changed.
                if( flightPlanDataUpdated )
                {
                    FlightPlanListType flightPlanListType = new FlightPlanListType();
                    flightPlanListType.setFlightPlans( updatePlans );
                    update.setPreDepartureFlightPlanList( flightPlanListType );
                }
            }
        }
        
        return( flightPlanDataUpdated );
    }

    private void handleFlightPlanCancel( MatmFlight update, MatmFlight target )
    {
        if( MatmFlightPlanUtil.isFlightPlanCancelMessage( update ))
        {
            // Compare to the target, not the update. The update indicates the plan to cancel.
            FlightPlanType currentPlan = null;
            
            if( update.getPreDepartureFlightPlanList() != null )
            {
                currentPlan = MatmFlightPlanUtil.getFlightPlan( 
                    target.getSourceFacility(), target.getComputerId(), update.getPreDepartureFlightPlanList().getFlightPlans() );
            }

            // Check if the current flight plan is canceled
            if(( currentPlan != null ) && Objects.equals( Boolean.TRUE, currentPlan.isFlightPlanCanceled() ))
            {
                // Get the most recent flight plan data to switch to
                FlightPlanType plan = MatmFlightPlanUtil.getLatestActiveFlightPlan( update.getPreDepartureFlightPlanList().getFlightPlans() );
                
                if( plan != null )
                {
                    injectFlightPlanMessage( update.getGufi(), FUSER_FLIGHT_PLAN_CANCEL_SYSTEM_ID, plan );
                }
            }
        }        
    }

    private void handleMultipleFlightPlansIndicator( MatmFlight update )
    {
        if(( update.getPreDepartureFlightPlanList() != null ) && 
                !update.getPreDepartureFlightPlanList().getFlightPlans().isEmpty() )
        {
            // Only set true if a specific facility has more than one computerID
            HashMap<String, Integer> facilityCounter = new HashMap<>();
            
            for( FlightPlanType plan : update.getPreDepartureFlightPlanList().getFlightPlans() )
            {
                if(( plan.getSourceFacility() != null ) && !Objects.equals( Boolean.TRUE, plan.isFlightPlanCanceled() ))
                {
                    Integer count = facilityCounter.get( plan.getSourceFacility() );
                    if( count == null )
                    {
                        count = 0;
                    }
                    
                    count = count + 1;
                    
                    if( count > 1 )
                    {
                        update.setMultipleFlightPlansIndicator( true );
                        return;
                    }
                    
                    facilityCounter.put( plan.getSourceFacility(), count );
                }
            }
        }
        
        update.setMultipleFlightPlansIndicator( false );
    }
    
    private void handleMatmFlightPlanChange( MatmFlight update, MatmFlight target )
    {
        // Check if the current target flight plan is different than the new update
        if(( target.getSourceFacility() != null ) && ( target.getComputerId() != null ) &&
                ( !Objects.equals( update.getSourceFacility(), target.getSourceFacility() ) ||
                  !Objects.equals( update.getComputerId(), target.getComputerId() )))
        {
            // The update only has the preDepartureFlightPlanList if the data has changed
            List<FlightPlanType> flightPlanList = null;
            
            if( update.getPreDepartureFlightPlanList() != null )
            {
                flightPlanList = update.getPreDepartureFlightPlanList().getFlightPlans();
            }
            
            if((( flightPlanList == null ) || flightPlanList.isEmpty() ) && ( target.getPreDepartureFlightPlanList() != null ))
            {
                flightPlanList = target.getPreDepartureFlightPlanList().getFlightPlans();
            }
            
            // Don't want to trigger a new message if this update is a brand new flight plan -- the data is already in the message.
            if(( target.getPreDepartureFlightPlanList() != null ) && 
                    !target.getPreDepartureFlightPlanList().getFlightPlans().isEmpty() && 
                    ( target.getPreDepartureFlightPlanList().getFlightPlans().size() == flightPlanList.size() ))
            {
                FlightPlanType switchToPlan = MatmFlightPlanUtil.getFlightPlan( 
                            update.getSourceFacility(), update.getComputerId(), flightPlanList );

                if( switchToPlan == null )
                {
                    log.warn( "Cannot sync pre-departure flight plan data. No matching sourceFacility/computerId: " + update.getGufi() );
                    return;
                }

                if( switchToPlan.isFlightPlanCanceled() )
                {
                    log.warn( "Cannot sync pre-departure flight plan data. Matching sourceFacility/computerId is canceled: " + update.getGufi() );
                    return;
                }            

                // add a message to switch back to the plan
                injectFlightPlanMessage( update.getGufi(), FUSER_FLIGHT_PLAN_CHANGE_SYSTEM_ID, switchToPlan );
            }
        }
    }
    
    private void injectFlightPlanMessage( String gufi, String systemId, FlightPlanType flightPlan )
    {
        try
        {
            Date timestamp = new Date( clock.getTimeInMillis() );
            
            MatmFlight matm = MatmFlightPlanUtil.transformFlightPlanToMatmFlight( flightPlan );
            
            MatmFlight internalUpdate = MatmFlightInternalUpdateUtil.getFuserInternalUpdate( 
                    timestamp, matm, systemId );            
            matm.mergeFrom( internalUpdate, matm ); 

            if( log.isDebugEnabled() )
            {
                log.debug( "Injecting Fuser message: " + matm.getGufi() + ", " + 
                        matm.getLastUpdateSource() + ", " + matm.getSystemId() + ", " + 
                        matm.getSourceFacility() + ", " + matm.getComputerId()  );
            }
            
            injectFuserMessage( matm );
        }
        catch( Exception ex )
        {
            log.error( "Error injecting flight plan message: " + ex.getMessage(), ex );
        }
    }    
}
