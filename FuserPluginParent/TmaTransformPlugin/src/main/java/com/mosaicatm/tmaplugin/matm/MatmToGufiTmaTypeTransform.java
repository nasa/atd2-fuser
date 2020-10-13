package com.mosaicatm.tmaplugin.matm;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.lib.coord.WorldCoord;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.common.SurfaceFlightState;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.TbfmExtension;
import com.mosaicatm.tma.common.data.AircraftStatus;
import com.mosaicatm.tma.common.data.EngineType;
import com.mosaicatm.tma.common.data.FlightPlanCategory;
import com.mosaicatm.tma.common.data.FlightPlanStatus;
import com.mosaicatm.tma.common.data.TmaType;
import com.mosaicatm.tma.common.flat.CreateIfNullFlatTmaType;
import com.mosaicatm.tma.common.flat.FlatTmaMrpEtaStaSchDataType;
import com.mosaicatm.tma.common.flat.FlatTmaType;
import com.mosaicatm.tma.common.message.GufiTmaType;

public class MatmToGufiTmaTypeTransform
        implements Transformer<GufiTmaType, MatmFlight>
{
    private final Log log = LogFactory.getLog( getClass() );

    @Override
    public GufiTmaType transform( MatmFlight matm )
    {
        if( matm == null )
        {
            return null;
        }

        GufiTmaType gufiTmaType = new GufiTmaType( new TmaType() );
        FlatTmaType tma = new CreateIfNullFlatTmaType( gufiTmaType );

        if( matm.getTimestamp() != null )
        {
            gufiTmaType.setEnvelopeDate( matm.getTimestamp() );
            tma.setMsgTime( matm.getTimestamp() );
        }

        if( matm.getSystemId() != null )
        {
            gufiTmaType.setEnvelopeSource( matm.getSystemId() );
        }

        //Transform extension first -- we will use some values later
        transformExtension( gufiTmaType, matm );

        if( matm.getGufi() != null )
        {
            gufiTmaType.setGufi( matm.getGufi() );
        }

        if( matm.getAcid() != null )
        {
            tma.setFltAndAirAid( matm.getAcid() );
        }

        if( matm.getAircraftType() != null && matm.getAircraftEquipmentQualifier() != null )
        {
            tma.setFltTyp( matm.getAircraftType() + "/" + matm.getAircraftEquipmentQualifier() );
        }
        else if( matm.getAircraftType() != null )
        {
            tma.setFltTyp( matm.getAircraftType() );
        }

        if( matm.getAircraftEngineClass() != null )
        {
            switch( matm.getAircraftEngineClass() )
            {
                case JET:
                    tma.setFltEng( EngineType.JET );
                    break;
                case PISTON:
                    tma.setFltEng( EngineType.PISTON );
                    break;
                case TURBO:
                    tma.setFltEng( EngineType.TURBO_PROP );
                    break;
            }
        }

        if( matm.getBeaconCode() != null )
        {
            tma.setFltBcn( matm.getBeaconCode() );
        }

        if( matm.getArrivalAerodrome() != null ) 
        {
            if( matm.getArrivalAerodrome().getIcaoName() != null )
            {
                tma.setFltAndAirApt( matm.getArrivalAerodrome().getIcaoName() );
            }
            else
            {
                tma.setFltAndAirApt( matm.getArrivalAerodrome().getIataName() );
            }
        }

        if( matm.getDepartureAerodrome() != null ) 
        {
            if( matm.getDepartureAerodrome().getIcaoName() != null ) 
            {
                tma.setFltAndAirDap( matm.getDepartureAerodrome().getIcaoName() );
            }
            else
            {
                tma.setFltAndAirDap( matm.getDepartureAerodrome().getIataName() );
            }
        }

        if( matm.getAltitudeAssigned() != null )
        {
            tma.setFltAra( String.valueOf( matm.getAltitudeAssigned() ));
        }
        else if( matm.getAltitudeRequested() != null )
        {
            tma.setFltAra( String.valueOf( matm.getAltitudeRequested() ));
        }

        if( matm.getSpeedFiled() != null )
        {
            tma.setFltSpd( String.valueOf( matm.getSpeedFiled() ));
        }

        if( matm.getDepartureRunwayAssigned() != null )
        {
            tma.setFltDrw( matm.getDepartureRunwayAssigned() );
        }

        if( matm.getRouteTextFiled() != null )
        {
            tma.setFltA10( matm.getRouteTextFiled() );
        }
        else if( matm.getRouteText() != null )
        {
            tma.setFltA10( matm.getRouteText() );
        }

        if( matm.getDepartureRunwayEstimatedTime() != null )
        {
            tma.setFltEtd( matm.getDepartureRunwayEstimatedTime() );
        }

        if(( matm.getEstimatedDepartureClearanceTime() != null ) && !matm.getEstimatedDepartureClearanceTime().isNil() && 
                ( matm.getEstimatedDepartureClearanceTime().getValue() != null ))              
        {
            tma.setFltEtm( matm.getEstimatedDepartureClearanceTime().getValue() );
        }

        // Need a heuristic for knowing if the flight has departed, since we cannot get the flight state from MatmFlight.
        // Coordination time should only be P-Time if the flight has yet to depart
        if( ( matm.getDepartureStandProposedTime() != null )
                && ( matm.getDepartureRunwayActualTime() == null ) )
        {
            if( ( matm.getDepartureSurfaceFlightState() == null )
                    || ( matm.getDepartureSurfaceFlightState() == SurfaceFlightState.IN_QUEUE )
                    || ( matm.getDepartureSurfaceFlightState() == SurfaceFlightState.PUSHBACK )
                    || ( matm.getDepartureSurfaceFlightState() == SurfaceFlightState.RAMP_TAXI_OUT )
                    || ( matm.getDepartureSurfaceFlightState() == SurfaceFlightState.RETURN_TO_GATE )
                    || ( matm.getDepartureSurfaceFlightState() == SurfaceFlightState.SCHEDULED )
                    || ( matm.getDepartureSurfaceFlightState() == SurfaceFlightState.SUSPENDED )
                    || ( matm.getDepartureSurfaceFlightState() == SurfaceFlightState.CANCELLED )
                    || ( matm.getDepartureSurfaceFlightState() == SurfaceFlightState.TAXI_OUT )
                    || ( matm.getDepartureSurfaceFlightState() == SurfaceFlightState.UNKNOWN ) )
            {
                tma.setFltPTime( matm.getDepartureStandProposedTime() );                
                tma.setFltFps( FlightPlanStatus.PROPOSED );
                tma.setFltAcs( AircraftStatus.PROPOSED );
                
                String apt = matm.getDepartureAerodrome().getIcaoName();
                if( apt == null )
                {
                    apt = matm.getDepartureAerodrome().getIataName();
                }
                tma.setFltCfx( apt );
            }
        }

        if( matm.getDepartureRunwayActualTime() != null)
        {
            tma.setFltAot( matm.getDepartureRunwayActualTime().getValue() );
        }

        List<FlatTmaMrpEtaStaSchDataType> mrp_eta_sta_sch_list = tma.getFlatTmaMrpEtaStaSchDataList();
        FlatTmaMrpEtaStaSchDataType mrp_eta_sta_sch = mrp_eta_sta_sch_list.get( 0 );

        if( matm.getDepartureRunwayMeteredTime() != null )
        {
            if( matm.getDepartureRunwayMeteredTime().isNil() )
            {
                tma.setNewFlightPlanStd( true );
                tma.setFltStd( null );
            }
            else
            {
                tma.setFltStd( matm.getDepartureRunwayMeteredTime().getValue().getValue() );
                mrp_eta_sta_sch.setSchSfz( matm.getDepartureRunwayMeteredTime().getValue().isFrozen() );
            }
        }

        //handle an MRP list with either 1 or 2 elements
        mrp_eta_sta_sch = mrp_eta_sta_sch_list.get( mrp_eta_sta_sch_list.size() - 1 );
        if( matm.getArrivalRunwayAssigned() != null )
        {
            mrp_eta_sta_sch.setMrpRwy( matm.getArrivalRunwayAssigned() );
        }

        if( matm.getArrivalRunwayEstimatedTime() != null )
        {
            mrp_eta_sta_sch.setEtaEtaRwy( matm.getArrivalRunwayEstimatedTime() );
        }

        if( matm.getArrivalRunwayMeteredTime() != null )
        {
            mrp_eta_sta_sch.setStaStaRwy( matm.getArrivalRunwayMeteredTime().getValue() );
            mrp_eta_sta_sch.setSchSfz( matm.getArrivalRunwayMeteredTime().isFrozen() );
        }

        if( matm.getPosition() != null )
        {
            Position position = matm.getPosition();
            if( position.getAltitude() != null )
            {
                tma.setTrkAlt( position.getAltitude().floatValue() );
            }

            if( position.getSpeed() != null )
            {
                tma.setTrkGsp( position.getSpeed().floatValue() );
            }

            if( position.getHeading() != null )
            {
                tma.setTrkHdg( position.getHeading().floatValue() );
            }

            if( position.getLatitude() != null && position.getLongitude() != null )
            {
                WorldCoord pos = WorldCoord.getCoordFromDecimalDegrees( position.getLatitude(),
                        position.getLongitude() );

                if( pos != null )
                {
                    String latDms = pos.getDMSLatString();
                    String lonDms = pos.getDMSLonString();

                    if( latDms != null )
                    {
                        tma.setTrkLat( latDms );
                    }

                    if( lonDms != null )
                    {
                        tma.setTrkLon( lonDms );
                    }
                }
            }

            if( position.getTimestamp() != null )
            {
                tma.setTrkTtm( position.getTimestamp() );
            }
        }

        return gufiTmaType;
    }

    private void transformExtension( GufiTmaType gufiTmaType, MatmFlight matm )
    {
        if( gufiTmaType == null )
        {
            return;
        }

        if( matm.getExtensions() != null && matm.getExtensions().getTbfmExtension() != null )
        {
            TbfmExtension tbfmExt = matm.getExtensions().getTbfmExtension();

            FlatTmaType tmaType = new CreateIfNullFlatTmaType( gufiTmaType );

            if( tbfmExt.getTmaId() != null )
            {
                tmaType.setAirTmaId( tbfmExt.getTmaId() );
            }
            
            if( tbfmExt.getSecurityLevel() != null )
            {
                gufiTmaType.setSecurityLevel( tbfmExt.getSecurityLevel() );
            }

            if( tbfmExt.getArrivalRunwayTraconAssigned() != null )
            {
                tmaType.setFltTrw( tbfmExt.getArrivalRunwayTraconAssigned() );
            }
            
            if( tbfmExt.getAircraftReadyTime() != null )
            {
                tmaType.setFltRtm( String.valueOf( tbfmExt.getAircraftReadyTime().getTime() / 1000L ));
            }            
            
            if( tbfmExt.getDepartureSchedulingStatusCode() != null )
            {
                tmaType.setFltDnt( String.valueOf( tbfmExt.getDepartureSchedulingStatusCode().ordinal() ));
            }
            
            if( tbfmExt.getSwapAircraftId() != null )
            {
                tmaType.setFltSid( tbfmExt.getSwapAircraftId() );
            }

            List<FlatTmaMrpEtaStaSchDataType> mrp_eta_sta_sch_list = tmaType.getFlatTmaMrpEtaStaSchDataList();
            FlatTmaMrpEtaStaSchDataType mrp_eta_sta_sch = mrp_eta_sta_sch_list.get( 0 );
            mrp_eta_sta_sch.setMrpCat( FlightPlanCategory.DEPARTURE );

            // Add a single departure MRP
            if( tbfmExt.getDepartureMeterFix() != null )
            {
                mrp_eta_sta_sch.setMrpMfx( tbfmExt.getDepartureMeterFix() );
                mrp_eta_sta_sch.setEtaMfx( tbfmExt.getDepartureMeterFix() );
                mrp_eta_sta_sch.setStaMfx( tbfmExt.getDepartureMeterFix() );
                mrp_eta_sta_sch.setSchMfx( tbfmExt.getDepartureMeterFix() );
            }

            if( tbfmExt.getDepartureSchedulingFix() != null )
            {
                mrp_eta_sta_sch.setMrpSfx( tbfmExt.getDepartureSchedulingFix() );
            }

            if( tbfmExt.getDepartureMeterFixEta() != null )
            {
                mrp_eta_sta_sch.setEtaEtaMfx( tbfmExt.getDepartureMeterFixEta() );
            }

            if( tbfmExt.getDepartureSchedulingFixEta() != null )
            {
                mrp_eta_sta_sch.setEtaEtaSfx( tbfmExt.getDepartureSchedulingFixEta() );
            }

            if( tbfmExt.getDepartureSchedulingFixSta() != null )
            {
                mrp_eta_sta_sch.setStaStaSfx( tbfmExt.getDepartureSchedulingFixSta() );
            }

            if( tbfmExt.isDepartureStasFrozen() != null )
            {
                mrp_eta_sta_sch.setSchSfz( tbfmExt.isDepartureStasFrozen() );
            }

            if( tbfmExt.isDepartureSchedulingSuspended() != null )
            {
                mrp_eta_sta_sch.setSchSus( tbfmExt.isDepartureSchedulingSuspended() );
            }

            //Add a new arrival MRP if there was a departure MRP
            if( mrp_eta_sta_sch.getMrpMfx() != null )
            {
                if( tbfmExt.getArrivalMeterFix() != null )
                {
                    if( tbfmExt.getArrivalMeterFix().equals( tbfmExt.getDepartureMeterFix() ))
                    {
                        mrp_eta_sta_sch.setMrpCat( FlightPlanCategory.DEPARTURE_ARRIVAL );
                    }
                    else
                    {
                        mrp_eta_sta_sch = tmaType.addFlatTmaMrpEtaStaSchDataType();
                        mrp_eta_sta_sch.setMrpCat( FlightPlanCategory.ARRIVAL );
                    }
                }
            }

            if( tbfmExt.getArrivalMeterFix() != null )
            {
                mrp_eta_sta_sch.setMrpMfx( tbfmExt.getArrivalMeterFix() );
                mrp_eta_sta_sch.setEtaMfx( tbfmExt.getArrivalMeterFix() );
                mrp_eta_sta_sch.setStaMfx( tbfmExt.getArrivalMeterFix() );
                mrp_eta_sta_sch.setSchMfx( tbfmExt.getArrivalMeterFix() );
            }

            if( tbfmExt.getArrivalSchedulingFix() != null )
            {
                mrp_eta_sta_sch.setMrpSfx( tbfmExt.getArrivalSchedulingFix() );
            }

            if( tbfmExt.getArrivalRunway() != null )
            {
                mrp_eta_sta_sch.setMrpRwy( tbfmExt.getArrivalRunway() );
            }

            if( tbfmExt.isArrivalRunwayAssignmentFrozen() != null )
            {
                mrp_eta_sta_sch.setSchRfz( tbfmExt.isArrivalRunwayAssignmentFrozen() );
            }

            if( tbfmExt.getArrivalMeterFixEta() != null )
            {
                mrp_eta_sta_sch.setEtaEtaMfx( tbfmExt.getArrivalMeterFixEta() );
            }

            if( tbfmExt.getArrivalSchedulingFixEta() != null )
            {
                mrp_eta_sta_sch.setEtaEtaSfx( tbfmExt.getArrivalSchedulingFixEta() );
            }

            if( tbfmExt.getArrivalSchedulingFixSta() != null )
            {
                mrp_eta_sta_sch.setStaStaSfx( tbfmExt.getArrivalSchedulingFixSta() );
            }

            if( tbfmExt.isArrivalStasFrozen() != null )
            {
                mrp_eta_sta_sch.setSchSfz( tbfmExt.isArrivalStasFrozen() );
            }

            if( tbfmExt.isArrivalSchedulingSuspended() != null )
            {
                mrp_eta_sta_sch.setSchSus( tbfmExt.isArrivalSchedulingSuspended() );
            }
            
            if( tbfmExt.isManuallyScheduled() != null )
            {
                mrp_eta_sta_sch.setSchMan( tbfmExt.isManuallyScheduled() );
            }
            
            if( tbfmExt.isSyncMessage() != null )
            {
                gufiTmaType.setSyncMessage( tbfmExt.isSyncMessage() );
            }
            
            if( tbfmExt.isNewEtm() != null )
            {
                gufiTmaType.setNewFlightPlanEtm( tbfmExt.isNewEtm() );
            }
            
            if( tbfmExt.isNewStd() != null )
            {
                gufiTmaType.setNewFlightPlanStd( tbfmExt.isNewStd() );
            }
            
            if( tbfmExt.getCtm() != null )
            {
                tmaType.setFltCtm( tbfmExt.getCtm() );
            }
            
            if( tbfmExt.isNewCtm() != null )
            {
                tmaType.setNewFlightPlanCtm( tbfmExt.isNewCtm() );
            }
            
            if( tbfmExt.getDepartureProposedTime() != null )
            {
                tmaType.setFltPTime( tbfmExt.getDepartureProposedTime() );
            }
            
            if( tbfmExt.getFlightPlanStatus() != null)
            {
                tmaType.setFltFpsValue( tbfmExt.getFlightPlanStatus() );
            }
            
            if( tbfmExt.getAircraftStatus() != null )
            {
                tmaType.setFltAcsValue( tbfmExt.getAircraftStatus() );
            }
            
            // check both the extension and tmaType since this could have
            // already been transformed from the departure runway assigned valu
            if( tbfmExt.getDepartureRunway() != null && 
                tmaType.getFltDrw() != null )
            {
                tmaType.setFltDrw( tbfmExt.getDepartureRunway() );
            }
        }
    }
}
