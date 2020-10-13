package com.mosaicatm.fuser.common.matm.util.flight;

import java.util.ArrayList;
import java.util.List;

import java.util.Objects;

import com.mosaicatm.matmdata.common.FlightPlanType;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.AefsExtension;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;
import com.mosaicatm.matmdata.flight.extension.TfmMessageTriggerType;
import com.mosaicatm.matmdata.flight.extension.TfmMessageTypeType;

public class MatmFlightPlanUtil
{
    private static final TfmMessageTypeType TFM_FLIGHT_PLAN_CANCEL_TYPE = TfmMessageTypeType.FLIGHT_PLAN_CANCELLATION;
    private static final TfmMessageTriggerType TFM_FLIGHT_PLAN_CANCEL_TRIGGER_TYPE = TfmMessageTriggerType.HCS_CANCELLATION_MSG;
    
    public static FlightPlanType transformMatmFlightToFlightPlan( MatmFlight flight )
    {
        FlightPlanType plan = new FlightPlanType();
        
        plan.setAircraftEngineClass( flight.getAircraftEngineClass() );
        plan.setAircraftEquipmentQualifier( flight.getAircraftEquipmentQualifier() );
        plan.setAircraftRegistration( flight.getAircraftRegistration() );
        plan.setAircraftType( flight.getAircraftType() );
        plan.setAltitudeAssigned( flight.getAltitudeAssigned() );
        plan.setAltitudeFiled( flight.getAltitudeFiled() );
        plan.setAltitudeRequested( flight.getAltitudeRequested() );
        plan.setArrivalAerodrome( flight.getArrivalAerodrome() );
        plan.setArrivalFixSourceData( flight.getArrivalFixSourceData() );
        plan.setBeaconCode( flight.getBeaconCode() );
        plan.setComputerId( flight.getComputerId() );
        plan.setDepartureAerodrome( flight.getDepartureAerodrome() );
        plan.setDepartureFixSourceData( flight.getDepartureFixSourceData() );
        plan.setDepartureStandProposedTime( flight.getDepartureStandProposedTime() );
        plan.setEramGufi( flight.getEramGufi() );
        plan.setFlightPlanCanceled( isFlightPlanCancelMessage( flight ));
        plan.setFlightRules( flight.getFlightRules() );
        plan.setFlightType( flight.getFlightType() );
        plan.setLastUpdateSource( flight.getLastUpdateSource() );
        plan.setRouteText( flight.getRouteText() );
        plan.setSourceFacility( flight.getSourceFacility() );
        plan.setSpeedFiled( flight.getSpeedFiled() );
        plan.setStandardInstrumentArrival( flight.getStandardInstrumentArrival() );
        plan.setStandardInstrumentDeparture( flight.getStandardInstrumentDeparture() );
        plan.setSystemId( flight.getSystemId() );
        plan.setTimestampSource( flight.getTimestampSource() );
        plan.setWakeTurbulenceCategory( flight.getWakeTurbulenceCategory() );
        
        return( plan );
    }    
    
    public static MatmFlight transformFlightPlanToMatmFlight( FlightPlanType plan )
    {
        MatmFlight flight = new MatmFlight();
        
        flight.setAircraftEngineClass( plan.getAircraftEngineClass() );
        flight.setAircraftEquipmentQualifier( plan.getAircraftEquipmentQualifier() );
        flight.setAircraftRegistration( plan.getAircraftRegistration() );
        flight.setAircraftType( plan.getAircraftType() );
        flight.setAltitudeAssigned( plan.getAltitudeAssigned() );
        flight.setAltitudeFiled( plan.getAltitudeFiled() );
        flight.setAltitudeRequested( plan.getAltitudeRequested() );
        flight.setArrivalAerodrome( plan.getArrivalAerodrome() );
        flight.setArrivalFixSourceData( plan.getArrivalFixSourceData() );
        flight.setBeaconCode( plan.getBeaconCode() );
        flight.setComputerId( plan.getComputerId() );
        flight.setDepartureAerodrome( plan.getDepartureAerodrome() );
        flight.setDepartureFixSourceData( plan.getDepartureFixSourceData() );
        flight.setDepartureStandProposedTime( plan.getDepartureStandProposedTime() );
        flight.setEramGufi( plan.getEramGufi() );
        flight.setFlightRules( plan.getFlightRules() );
        flight.setFlightType( plan.getFlightType() );
        flight.setLastUpdateSource( plan.getLastUpdateSource() );
        flight.setRouteText( plan.getRouteText() );
        flight.setSourceFacility( plan.getSourceFacility() );
        flight.setSpeedFiled( plan.getSpeedFiled() );
        flight.setStandardInstrumentArrival( plan.getStandardInstrumentArrival() );
        flight.setStandardInstrumentDeparture( plan.getStandardInstrumentDeparture() );
        flight.setSystemId( plan.getSystemId() );
        flight.setTimestampSource( plan.getTimestampSource() );
        flight.setWakeTurbulenceCategory( plan.getWakeTurbulenceCategory() );
        
        return( flight );
    }     
    
    public static boolean isFlightPlanCancelMessage( MatmFlight flightMsg )
    {
        if( flightMsg.getExtensions() != null )
        {
            if( flightMsg.getExtensions().getTfmExtension() != null )
            {
                TfmExtension tfm = flightMsg.getExtensions().getTfmExtension();

                return( Objects.equals( TFM_FLIGHT_PLAN_CANCEL_TYPE, tfm.getMessageType() ) &&
                        Objects.equals( TFM_FLIGHT_PLAN_CANCEL_TRIGGER_TYPE, tfm.getMessageTrigger() ));
            }
            if( flightMsg.getExtensions().getAefsExtension() != null )
            {
                AefsExtension aefs = flightMsg.getExtensions().getAefsExtension();

                return( Objects.equals( Boolean.TRUE, aefs.isRemoveStripFlag() ));
            }
        }
                
        return( false );
    }
    
    public static FlightPlanType getFlightPlan( String sourceFacility, 
            String computerId, List<FlightPlanType> flightPlanList )
    {
        if( flightPlanList != null )
        {
            for( FlightPlanType plan : flightPlanList )
            {
                if( Objects.equals( plan.getSourceFacility(), sourceFacility ) && 
                        Objects.equals( plan.getComputerId(), computerId ))
                {
                    return( plan );
                }
            }
        }
        
        return( null );
    }    
    
    public static FlightPlanType getLatestActiveFlightPlan( List<FlightPlanType> flightPlanList )
    {
        FlightPlanType latestPlan = null;
        
        if( flightPlanList != null )
        {
            for( FlightPlanType plan : flightPlanList )
            {
                if( !Objects.equals( Boolean.TRUE, plan.isFlightPlanCanceled() ))
                {
                    if(( latestPlan == null ) || 
                            plan.getTimestampSource().after( latestPlan.getTimestampSource() ))
                    {
                        latestPlan = plan;
                    }
                }
            }
        }
        
        return( latestPlan );
    }      

    public static List<FlightPlanType> getCopy( List<FlightPlanType> flightPlanList ) 
    {
        ArrayList<FlightPlanType> copy = new ArrayList<>();
        if( flightPlanList != null )
        {
            for( FlightPlanType plan : flightPlanList )
            {
                copy.add((FlightPlanType) plan.clone() );
            }
        }
        
        return( copy );
    }
    
    // merge update into the target
    public static boolean mergeUpdateIntoTarget( FlightPlanType update, FlightPlanType target ) 
    {
        boolean updated = false;
        
        if(( update.getAircraftEngineClass() != null ) && 
                !update.getAircraftEngineClass().equals( target.getAircraftEngineClass() ))
        {
            updated = true;
            target.setAircraftEngineClass( update.getAircraftEngineClass() );
        }
        
        if(( update.getAircraftEquipmentQualifier() != null ) &&
                 !update.getAircraftEquipmentQualifier().equals( target.getAircraftEquipmentQualifier() ))
        {
            updated = true;
            target.setAircraftEquipmentQualifier( update.getAircraftEquipmentQualifier() );
        }
            
        if(( update.getAircraftRegistration() != null ) &&
                 !update.getAircraftRegistration().equals( target.getAircraftRegistration() ))
        {
            updated = true;
            target.setAircraftRegistration( update.getAircraftRegistration() );
        }
        
        if(( update.getAircraftType() != null ) &&
                 !update.getAircraftType().equals( target.getAircraftType() ))
        {
            updated = true;
            target.setAircraftType( update.getAircraftType() );
        }
        
        if(( update.getAltitudeAssigned() != null ) &&
                 !update.getAltitudeAssigned().equals( target.getAltitudeAssigned() ))
        {
            updated = true;
            target.setAltitudeAssigned( update.getAltitudeAssigned() );
        }
        
        if(( update.getAltitudeFiled() != null ) &&
                 !update.getAltitudeFiled().equals( target.getAltitudeFiled() ))
        {
            updated = true;
            target.setAltitudeFiled( update.getAltitudeFiled() );
        }
        
        if(( update.getAltitudeRequested() != null ) &&
                 !update.getAltitudeRequested().equals( target.getAltitudeRequested() ))
        {
            updated = true;
            target.setAltitudeRequested( update.getAltitudeRequested() );
        }
        
        if(( update.getArrivalAerodrome() != null ) &&
                 !update.getArrivalAerodrome().equals( target.getArrivalAerodrome() ))
        {
            updated = true;
            target.setArrivalAerodrome( update.getArrivalAerodrome() );
        }
        
        if(( update.getArrivalFixSourceData() != null ) &&
                 !update.getArrivalFixSourceData().equals( target.getArrivalFixSourceData() ))
        {
            updated = true;
            target.setArrivalFixSourceData( update.getArrivalFixSourceData() );
        }
        
        if(( update.getBeaconCode() != null ) &&
                 !update.getBeaconCode().equals( target.getBeaconCode() ))
        {
            updated = true;
            target.setBeaconCode( update.getBeaconCode() );
        }
        
        if(( update.getComputerId() != null ) &&
                 !update.getComputerId().equals( target.getComputerId() ))
        {
            updated = true;
            target.setComputerId( update.getComputerId() );
        }        
        
        if(( update.getDepartureAerodrome() != null ) &&
                 !update.getDepartureAerodrome().equals( target.getDepartureAerodrome() ))
        {
            updated = true;
            target.setDepartureAerodrome( update.getDepartureAerodrome() );
        }
        
        if(( update.getDepartureFixSourceData() != null ) &&
                 !update.getDepartureFixSourceData().equals( target.getDepartureFixSourceData() ))
        {
            updated = true;
            target.setDepartureFixSourceData( update.getDepartureFixSourceData() );
        }
        
        if(( update.getDepartureStandProposedTime() != null ) &&
                 !update.getDepartureStandProposedTime().equals( target.getDepartureStandProposedTime() ))
        {
            updated = true;
            target.setDepartureStandProposedTime( update.getDepartureStandProposedTime() );
        }
        
        if(( update.getEramGufi() != null ) &&
                 !update.getEramGufi().equals( target.getEramGufi() ))
        {
            updated = true;
            target.setEramGufi( update.getEramGufi() );
        }        
        
        // Do not allow un-cancelling a flight plan
        if( Objects.equals( Boolean.TRUE, update.isFlightPlanCanceled() ) &&
                 !Objects.equals( Boolean.TRUE, target.isFlightPlanCanceled() ))
        {
            updated = true;
            target.setFlightPlanCanceled( Boolean.TRUE );
        }
        
        if(( update.getFlightRules() != null ) &&
                 !update.getFlightRules().equals( target.getFlightRules() ))
        {
            updated = true;
            target.setFlightRules( update.getFlightRules() );
        }
        
        if(( update.getFlightType() != null ) &&
                 !update.getFlightType().equals( target.getFlightType() ))
        {
            updated = true;
            target.setFlightType( update.getFlightType() );
        }
        
        if(( update.getLastUpdateSource() != null ) &&
                 !update.getLastUpdateSource().equals( target.getLastUpdateSource() ))
        {
            updated = true;
            target.setLastUpdateSource( update.getLastUpdateSource() );
        }        
        
        if(( update.getRouteText() != null ) &&
                 !update.getRouteText().equals( target.getRouteText() ))
        {
            updated = true;
            target.setRouteText( update.getRouteText() );
        }
        
        if(( update.getSourceFacility() != null ) &&
                 !update.getSourceFacility().equals( target.getSourceFacility() ))
        {
            updated = true;
            target.setSourceFacility( update.getSourceFacility() );
        }          
        
        if(( update.getSpeedFiled() != null ) &&
                 !update.getSpeedFiled().equals( target.getSpeedFiled() ))
        {
            updated = true;
            target.setSpeedFiled( update.getSpeedFiled() );
        }
        
        if(( update.getStandardInstrumentArrival() != null ) &&
                 !update.getStandardInstrumentArrival().equals( target.getStandardInstrumentArrival() ))
        {
            updated = true;
            target.setStandardInstrumentArrival( update.getStandardInstrumentArrival() );
        }
        
        if(( update.getStandardInstrumentDeparture() != null ) &&
                 !update.getStandardInstrumentDeparture().equals( target.getStandardInstrumentDeparture() ))
        {
            updated = true;
            target.setStandardInstrumentDeparture( update.getStandardInstrumentDeparture() );
        }

        if(( update.getSystemId() != null ) &&
                 !update.getSystemId().equals( target.getSystemId() ))
        {
            updated = true;
            target.setSystemId( update.getSystemId() );
        }   
        
        if(( update.getTimestampSource() != null ) &&
                 !update.getTimestampSource().equals( target.getTimestampSource() ))
        {
            //Don't report a data change if only the timestamp changed
            //updated = true;
            target.setTimestampSource( update.getTimestampSource() );
        }  
        
        if(( update.getWakeTurbulenceCategory() != null ) &&
                 !update.getWakeTurbulenceCategory().equals( target.getWakeTurbulenceCategory() ))
        {
            updated = true;
            target.setWakeTurbulenceCategory( update.getWakeTurbulenceCategory() );
        }
        
        return( updated );        
    }
    
    // merge update into the target
    public static boolean merge( FlightPlanType update, MatmFlight target ) 
    {
        boolean updated = false;
        
        if(( update.getAircraftEngineClass() != null ) && 
                !update.getAircraftEngineClass().equals( target.getAircraftEngineClass() ))
        {
            updated = true;
            target.setAircraftEngineClass( update.getAircraftEngineClass() );
        }
        
        if(( update.getAircraftEquipmentQualifier() != null ) &&
                 !update.getAircraftEquipmentQualifier().equals( target.getAircraftEquipmentQualifier() ))
        {
            updated = true;
            target.setAircraftEquipmentQualifier( update.getAircraftEquipmentQualifier() );
        }
            
        if(( update.getAircraftRegistration() != null ) &&
                 !update.getAircraftRegistration().equals( target.getAircraftRegistration() ))
        {
            updated = true;
            target.setAircraftRegistration( update.getAircraftRegistration() );
        }
        
        if(( update.getAircraftType() != null ) &&
                 !update.getAircraftType().equals( target.getAircraftType() ))
        {
            updated = true;
            target.setAircraftType( update.getAircraftType() );
        }
        
        if(( update.getAltitudeAssigned() != null ) &&
                 !update.getAltitudeAssigned().equals( target.getAltitudeAssigned() ))
        {
            updated = true;
            target.setAltitudeAssigned( update.getAltitudeAssigned() );
        }
        
        if(( update.getAltitudeFiled() != null ) &&
                 !update.getAltitudeFiled().equals( target.getAltitudeFiled() ))
        {
            updated = true;
            target.setAltitudeFiled( update.getAltitudeFiled() );
        }
        
        if(( update.getAltitudeRequested() != null ) &&
                 !update.getAltitudeRequested().equals( target.getAltitudeRequested() ))
        {
            updated = true;
            target.setAltitudeRequested( update.getAltitudeRequested() );
        }
        
        if(( update.getArrivalAerodrome() != null ) &&
                 !update.getArrivalAerodrome().equals( target.getArrivalAerodrome() ))
        {
            updated = true;
            target.setArrivalAerodrome( update.getArrivalAerodrome() );
        }
        
        if(( update.getArrivalFixSourceData() != null ) &&
                 !update.getArrivalFixSourceData().equals( target.getArrivalFixSourceData() ))
        {
            updated = true;
            target.setArrivalFixSourceData( update.getArrivalFixSourceData() );
        }
        
        if(( update.getBeaconCode() != null ) &&
                 !update.getBeaconCode().equals( target.getBeaconCode() ))
        {
            updated = true;
            target.setBeaconCode( update.getBeaconCode() );
        }
        
        if(( update.getComputerId() != null ) &&
                 !update.getComputerId().equals( target.getComputerId() ))
        {
            updated = true;
            target.setComputerId( update.getComputerId() );
        }   
        
        if(( update.getDepartureAerodrome() != null ) &&
                 !update.getDepartureAerodrome().equals( target.getDepartureAerodrome() ))
        {
            updated = true;
            target.setDepartureAerodrome( update.getDepartureAerodrome() );
        }
        
        if(( update.getDepartureFixSourceData() != null ) &&
                 !update.getDepartureFixSourceData().equals( target.getDepartureFixSourceData() ))
        {
            updated = true;
            target.setDepartureFixSourceData( update.getDepartureFixSourceData() );
        }
        
        if(( update.getDepartureStandProposedTime() != null ) &&
                 !update.getDepartureStandProposedTime().equals( target.getDepartureStandProposedTime() ))
        {
            updated = true;
            target.setDepartureStandProposedTime( update.getDepartureStandProposedTime() );
        }
        
        if(( update.getEramGufi() != null ) &&
                 !update.getEramGufi().equals( target.getEramGufi() ))
        {
            updated = true;
            target.setEramGufi( update.getEramGufi() );
        }        
        
        if(( update.getFlightRules() != null ) &&
                 !update.getFlightRules().equals( target.getFlightRules() ))
        {
            updated = true;
            target.setFlightRules( update.getFlightRules() );
        }
        
        if(( update.getFlightType() != null ) &&
                 !update.getFlightType().equals( target.getFlightType() ))
        {
            updated = true;
            target.setFlightType( update.getFlightType() );
        }
        
        if(( update.getLastUpdateSource() != null ) &&
                 !update.getLastUpdateSource().equals( target.getLastUpdateSource() ))
        {
            updated = true;
            target.setLastUpdateSource( update.getLastUpdateSource() );
        }        
        
        if(( update.getRouteText() != null ) &&
                 !update.getRouteText().equals( target.getRouteText() ))
        {
            updated = true;
            target.setRouteText( update.getRouteText() );
        }
        
        if(( update.getSourceFacility() != null ) &&
                 !update.getSourceFacility().equals( target.getSourceFacility() ))
        {
            updated = true;
            target.setSourceFacility( update.getSourceFacility() );
        }           
        
        if(( update.getSpeedFiled() != null ) &&
                 !update.getSpeedFiled().equals( target.getSpeedFiled() ))
        {
            updated = true;
            target.setSpeedFiled( update.getSpeedFiled() );
        }
        
        if(( update.getStandardInstrumentArrival() != null ) &&
                 !update.getStandardInstrumentArrival().equals( target.getStandardInstrumentArrival() ))
        {
            updated = true;
            target.setStandardInstrumentArrival( update.getStandardInstrumentArrival() );
        }
        
        if(( update.getStandardInstrumentDeparture() != null ) &&
                 !update.getStandardInstrumentDeparture().equals( target.getStandardInstrumentDeparture() ))
        {
            updated = true;
            target.setStandardInstrumentDeparture( update.getStandardInstrumentDeparture() );
        }
        
        if(( update.getSystemId() != null ) &&
                 !update.getSystemId().equals( target.getSystemId() ))
        {
            updated = true;
            target.setSystemId( update.getSystemId() );
        }
        
        if(( update.getTimestampSource() != null ) &&
                 !update.getTimestampSource().equals( target.getTimestampSource() ))
        {
            //Don't report a data change if only the timestamp changed
            //updated = true;
            target.setTimestampSource( update.getTimestampSource() );
        }        
        
        if(( update.getWakeTurbulenceCategory() != null ) &&
                 !update.getWakeTurbulenceCategory().equals( target.getWakeTurbulenceCategory() ))
        {
            updated = true;
            target.setWakeTurbulenceCategory( update.getWakeTurbulenceCategory() );
        }
        
        return( updated );        
    }    
    
    public static boolean hasFlightPlanData( MatmFlight flightMsg )
    {
        if( flightMsg.getAircraftEngineClass() != null )
            return( true );
        
        if( flightMsg.getAircraftEquipmentQualifier() != null )
            return( true );
            
        if( flightMsg.getAircraftRegistration() != null )
            return( true );
        
        if( flightMsg.getAircraftType() != null )
            return( true );
        
        if( flightMsg.getAltitudeAssigned() != null )
            return( true );
        
        if( flightMsg.getAltitudeFiled() != null )
            return( true );
        
        if( flightMsg.getAltitudeRequested() != null )
            return( true );
        
        if( flightMsg.getArrivalAerodrome() != null )
            return( true );
        
        if( flightMsg.getArrivalFixSourceData() != null )
            return( true );
        
        if( flightMsg.getBeaconCode() != null )
            return( true );
        
        if( flightMsg.getDepartureAerodrome() != null )
            return( true );
        
        if( flightMsg.getDepartureFixSourceData() != null )
            return( true );
        
        if( flightMsg.getDepartureStandProposedTime() != null )
            return( true );
        
        if( Objects.equals( Boolean.TRUE, isFlightPlanCancelMessage( flightMsg )))
            return( true );
        
        if( flightMsg.getFlightRules() != null )
            return( true );
        
        if( flightMsg.getFlightType() != null )
            return( true );
        
        if( flightMsg.getRouteText() != null )
            return( true );
        
        if( flightMsg.getSpeedFiled() != null )
            return( true );
        
        if( flightMsg.getStandardInstrumentArrival() != null )
            return( true );
        
        if( flightMsg.getStandardInstrumentDeparture() != null )
            return( true );

        if( flightMsg.getWakeTurbulenceCategory() != null )
            return( true );
        
        return( false );
    }       
}
