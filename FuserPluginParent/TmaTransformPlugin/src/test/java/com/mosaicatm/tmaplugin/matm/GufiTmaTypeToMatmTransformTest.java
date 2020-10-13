package com.mosaicatm.tmaplugin.matm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.mosaicatm.tmaplugin.matm.GufiTmaTypeToMatmTransform;
import com.mosaicatm.tmaplugin.matm.MatmToGufiTmaTypeTransform;
import com.mosaicatm.tmaplugin.util.SpeedConverterUtil;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.TbfmExtension;
import com.mosaicatm.tma.common.data.AirDataType;
import com.mosaicatm.tma.common.data.AircraftInformation;
import com.mosaicatm.tma.common.data.AircraftStatus;
import com.mosaicatm.tma.common.data.EngineType;
import com.mosaicatm.tma.common.data.FlightPlanCategory;
import com.mosaicatm.tma.common.data.FlightPlanInformation;
import com.mosaicatm.tma.common.data.FlightPlanStatus;
import com.mosaicatm.tma.common.data.SmsRequestStatus;
import com.mosaicatm.tma.common.data.TmaType;
import com.mosaicatm.tma.common.data.TrackInformation;
import com.mosaicatm.tma.common.flat.FlatTmaMrpEtaStaSchDataType;
import com.mosaicatm.tma.common.flat.FlatTmaType;
import com.mosaicatm.tma.common.message.GufiTmaType;

public class GufiTmaTypeToMatmTransformTest
{
    @Test
    public void testTransformFullCircle()
    {
        GufiTmaTypeToMatmTransform toMatmTransform = new GufiTmaTypeToMatmTransform();
        MatmToGufiTmaTypeTransform fromMatmTransform = new MatmToGufiTmaTypeTransform();

        // airport code translator changes the airport name so that the final "checkEquals" fails (DAL != KDAL)
        // The MatmToGufiTmaType prefers the ICAO if available, but original airport was not ICAO name
        toMatmTransform.setAirportCodeTranslator( null );
        
        GufiTmaType gufiTmaType = createGufiTmaType();
        assertNotNull( gufiTmaType );
        checkForNullTmaValues( gufiTmaType );

        MatmFlight matm = toMatmTransform.transform( gufiTmaType );
        assertNotNull( matm );
        checkForNullMatmValues( matm );

        GufiTmaType transGufiTmaType = fromMatmTransform.transform( matm );
        assertNotNull( transGufiTmaType );
        checkForNullTmaValues( transGufiTmaType );
        checkEquals( gufiTmaType, transGufiTmaType );
    }

    @Test
    public void testTransformGufiTmaTypeToMatmFlight()
    {
        GufiTmaTypeToMatmTransform transform = new GufiTmaTypeToMatmTransform();

        GufiTmaType gufiTmaType = createGufiTmaType();
        assertNotNull( gufiTmaType );
        checkForNullTmaValues( gufiTmaType );

        FlatTmaType flatTma = new FlatTmaType( gufiTmaType );

        MatmFlight flight = transform.transform( gufiTmaType );
        assertNotNull( flight );
        checkForNullMatmValues( flight );

        assertEquals( gufiTmaType.getGufi(), flight.getGufi() );
        assertEquals( flatTma.getFltStd(), flight.getDepartureRunwayMeteredTime().getValue().getValue() );
        assertEquals( flatTma.getFltA10(), flight.getRouteText() );
        assertEquals( flatTma.getFltA10(), flight.getRouteTextFiled() );
        assertEquals( flatTma.getFltOrAirAid(), flight.getAcid() );
        assertEquals( flatTma.getFltOrAirApt(), flight.getArrivalAerodrome().getIataName() );
        //assertEquals (flatTma.getFltAra(), flight.getAltitudeRequested().toString());
        assertEquals( flatTma.getFltAra(), flight.getAltitudeAssigned().toString() );

        double tma_speed_knots = SpeedConverterUtil.machToKnots(
                Double.parseDouble( flatTma.getFltSpd() ), Double.parseDouble( flatTma.getFltAra() ));
        assertEquals( tma_speed_knots, flight.getSpeedFiled(), 0.1 );
        assertEquals( flatTma.getFltDrw(), flight.getDepartureRunwayAssigned() );
        assertEquals( String.valueOf( flatTma.getFltBcn() ), flight.getBeaconCode() );
        assertEquals( flatTma.getFltOrAirDap(), flight.getDepartureAerodrome().getIataName() );
        assertEquals( flatTma.getFltEtd(), flight.getDepartureRunwayEstimatedTime() );
        assertEquals( flatTma.getFltEtm(), flight.getEstimatedDepartureClearanceTime().getValue() );
        assertEquals( flatTma.getFltPTime(), flight.getDepartureStandProposedTime() );
        assertEquals( flatTma.getFltAot(), flight.getDepartureRunwayActualTime().getValue() );
        assertEquals( flatTma.getFltTyp(), flight.getAircraftType() + "/" + flight.getAircraftEquipmentQualifier() );
        assertEquals( flatTma.getFltEng().toString(), flight.getAircraftEngineClass().toString() );
        assertEquals( flatTma.getTrkAlt().floatValue(), flight.getPosition().getAltitude().floatValue(), 0 );
        assertEquals( flatTma.getTrkGsp().floatValue(), flight.getPosition().getSpeed().floatValue(), 0 );
        assertEquals( flatTma.getTrkHdg().floatValue(), flight.getPosition().getHeading().floatValue(), 0 );
        assertEquals( flatTma.getTrkLatDecimalDegrees(), flight.getPosition().getLatitude() );
        assertEquals( flatTma.getTrkLonDecimalDegrees(), flight.getPosition().getLongitude(), 0 );
        assertEquals( flatTma.getTrkTtm(), flight.getPosition().getTimestamp() );

        TbfmExtension tbfmExt = flight.getExtensions().getTbfmExtension();
        assertNotNull( tbfmExt );

        assertEquals( gufiTmaType.getEnvelopeDate(), flight.getTimestamp() );
        assertEquals( gufiTmaType.getEnvelopeSource(), flight.getSystemId() );
//		assertEquals (flatTma.getAirAirTypeValue(), flatTmaExt.getAirDataTypeValue());
//		assertEquals (flatTma.getFltAcsValue(), flatTmaExt.getAircraftStatus());
        //Flight is active, ctm should not match
        assertNotEquals( flatTma.getFltCtm(), flight.getDepartureRunwayProposedTime() );
//		assertEquals (flatTma.getFltFpsValue(), flatTmaExt.getFlightPlanStatus());
//		assertEquals (flatTma.getFltSrsValue(), flatTmaExt.getSmsRequestStatus());
//		assertEquals (flatTma.getFltStd(), flatTmaExt.getOffTimeScheduled());
        assertEquals( flatTma.getAirTmaId(), tbfmExt.getTmaId() );
//		assertEquals (flatTma.getMsgId(), flatTmaExt.getMessageId());
        assertEquals( flatTma.getMsgTime(), flight.getTimestamp() );
        assertEquals( Long.parseLong( flatTma.getFltRtm() ), ( tbfmExt.getAircraftReadyTime().getTime() / 1000 ));
        assertEquals( flatTma.getFltSid(), tbfmExt.getSwapAircraftId() );
        assertEquals( flatTma.getFltDnt(), String.valueOf( tbfmExt.getDepartureSchedulingStatusCode().ordinal() ));
        assertEquals( gufiTmaType.getSecurityLevel(), tbfmExt.getSecurityLevel() );
        assertEquals( gufiTmaType.isSyncMessage(), tbfmExt.isSyncMessage() );
        assertEquals( flatTma.getFltPTime(), tbfmExt.getDepartureProposedTime() );
        assertEquals( flatTma.getFltFpsValue(), tbfmExt.getFlightPlanStatus() );
        assertEquals( flatTma.getFltAcsValue(), tbfmExt.getAircraftStatus() );
        assertEquals( flatTma.getFltDrw(), tbfmExt.getDepartureRunway() );
        
        for( FlatTmaMrpEtaStaSchDataType mrp_eta_sta_sch : flatTma.getFlatTmaMrpEtaStaSchDataList() )
        {
            if( mrp_eta_sta_sch.isArrivalMrp() )
            {
                assertEquals( mrp_eta_sta_sch.getMrpRwy(), flight.getArrivalRunwayAssigned() );
                assertEquals( mrp_eta_sta_sch.getMrpMfx(), tbfmExt.getArrivalMeterFix() );
                assertEquals( mrp_eta_sta_sch.getMrpSfx(), tbfmExt.getArrivalSchedulingFix() );
                assertEquals( mrp_eta_sta_sch.isSchRfz(), tbfmExt.isArrivalRunwayAssignmentFrozen() );
                assertEquals( mrp_eta_sta_sch.isSchSfz(), tbfmExt.isArrivalStasFrozen() );
                assertEquals( mrp_eta_sta_sch.getStaStaSfx(), tbfmExt.getArrivalSchedulingFixSta() );
                assertEquals( mrp_eta_sta_sch.getEtaEtaMfx(), tbfmExt.getArrivalMeterFixEta() );
                assertEquals( mrp_eta_sta_sch.getEtaEtaSfx(), tbfmExt.getArrivalSchedulingFixEta() );
                assertEquals( mrp_eta_sta_sch.getEtaEtaRwy(), flight.getArrivalRunwayEstimatedTime() );
                assertEquals( mrp_eta_sta_sch.getStaStaRwy(), flight.getArrivalRunwayMeteredTime().getValue() );
                assertEquals( mrp_eta_sta_sch.isSchSfz(), flight.getArrivalRunwayMeteredTime().isFrozen() );
            }
            if( mrp_eta_sta_sch.isDepartureMrp() )
            {
                assertEquals( mrp_eta_sta_sch.getMrpMfx(), tbfmExt.getDepartureMeterFix() );
                assertEquals( mrp_eta_sta_sch.getMrpSfx(), tbfmExt.getDepartureSchedulingFix() );
                assertEquals( mrp_eta_sta_sch.isSchSfz(), tbfmExt.isDepartureStasFrozen() );
                assertEquals( mrp_eta_sta_sch.getStaStaSfx(), tbfmExt.getDepartureSchedulingFixSta() );
                assertEquals( mrp_eta_sta_sch.getEtaEtaMfx(), tbfmExt.getDepartureMeterFixEta() );
                assertEquals( mrp_eta_sta_sch.getEtaEtaSfx(), tbfmExt.getDepartureSchedulingFixEta() );
                assertEquals( mrp_eta_sta_sch.isSchSfz(), flight.getDepartureRunwayMeteredTime().getValue().isFrozen() );
                assertEquals( mrp_eta_sta_sch.isSchMan(), tbfmExt.isManuallyScheduled() );
            }
        }
    }
    
    @Test
    public void testTransformGufiTmaTypeToMatmFlightNotMergedRwyData()
    {
        GufiTmaTypeToMatmTransform transform = new GufiTmaTypeToMatmTransform();

        GufiTmaType gufiTmaType = createGufiTmaType();
        
        assertNotNull( gufiTmaType );

        FlatTmaType flatTma = new FlatTmaType( gufiTmaType );
        
        //Null out the category 
        for( FlatTmaMrpEtaStaSchDataType mrp_eta_sta_sch : flatTma.getFlatTmaMrpEtaStaSchDataList() )
        {
            mrp_eta_sta_sch.setMrpCat( null );
        }
        
        MatmFlight flight = transform.transform( gufiTmaType );
        assertNotNull( flight );

        assertEquals( gufiTmaType.getGufi(), flight.getGufi() );
        
        TbfmExtension tbfmExt = flight.getExtensions().getTbfmExtension();
        assertNotNull( tbfmExt );

        for( FlatTmaMrpEtaStaSchDataType mrp_eta_sta_sch : flatTma.getFlatTmaMrpEtaStaSchDataList() )
        {
            if( mrp_eta_sta_sch.getEtaEtaRwy() != null )
            {
                assertEquals( mrp_eta_sta_sch.getMrpRwy(), flight.getArrivalRunwayAssigned() );
                assertEquals( mrp_eta_sta_sch.getMrpRwy(), tbfmExt.getArrivalRunway() );
                assertEquals( mrp_eta_sta_sch.isSchSfz(), tbfmExt.isArrivalStasFrozen() );
                assertEquals( mrp_eta_sta_sch.isSchSfz(), flight.getArrivalRunwayMeteredTime().isFrozen() );                
                assertEquals( mrp_eta_sta_sch.getEtaEtaRwy(), flight.getArrivalRunwayEstimatedTime() );
                assertEquals( mrp_eta_sta_sch.getStaStaRwy(), flight.getArrivalRunwayMeteredTime().getValue() );
            }
        }
    }    

    @Test
    public void testTransformGufiTmaTypeEmptyFiledSpeed()
    {
        GufiTmaTypeToMatmTransform transform = new GufiTmaTypeToMatmTransform();

        GufiTmaType gufiTmaType = createGufiTmaType();
        gufiTmaType.getTmaType().getAir().getFlt().setSpd( null );

        MatmFlight flight = transform.transform( gufiTmaType );
        assertNull( flight.getSpeedFiled() );

        gufiTmaType.getTmaType().getAir().getFlt().setSpd( " " );
        flight = transform.transform( gufiTmaType );
        assertNull( flight.getSpeedFiled() );

        gufiTmaType.getTmaType().getAir().getFlt().setSpd( "500" );
        flight = transform.transform( gufiTmaType );
        assertNotNull( flight.getSpeedFiled() );
    }

    private void checkEquals( GufiTmaType orig, GufiTmaType mod )
    {
        assertNotNull( orig );
        assertNotNull( mod );

        FlatTmaType origTma = new FlatTmaType( orig );
        FlatTmaType modTma = new FlatTmaType( mod );

        assertEquals( orig.getGufi(), mod.getGufi() );
        assertEquals( orig.getSecurityLevel(), mod.getSecurityLevel() );
        assertEquals( orig.isSyncMessage(), mod.isSyncMessage() );
        assertEquals( origTma.getFltStd(), modTma.getFltStd() );
        assertEquals( origTma.getFltEtm(), modTma.getFltEtm() );
        assertEquals( origTma.getFltAot(), modTma.getFltAot() );
        assertEquals( origTma.getFltA10(), modTma.getFltA10() );
        assertEquals( origTma.getFltOrAirAid(), modTma.getFltOrAirAid() );
        assertEquals( origTma.getFltOrAirApt(), modTma.getFltOrAirApt() );
        assertEquals( origTma.getFltAra(), modTma.getFltAra() );

        double mod_speed_knots = SpeedConverterUtil.knotsToMach(
                Double.parseDouble( modTma.getFltSpd() ), Double.parseDouble( modTma.getFltAra() ));
        assertEquals( Double.parseDouble( origTma.getFltSpd() ), mod_speed_knots, 0.01 );
        assertEquals( origTma.getFltDrw(), modTma.getFltDrw() );
        assertEquals( origTma.getFltBcn(), modTma.getFltBcn() );
        assertEquals( origTma.getFltOrAirDap(), modTma.getFltOrAirDap() );
        assertEquals( origTma.getFltEtd(), modTma.getFltEtd() );
        assertEquals( origTma.getFltTyp(), modTma.getFltTyp() );
        assertEquals( origTma.getFltEng(), modTma.getFltEng() );
        assertEquals( origTma.getTrkAlt(), modTma.getTrkAlt() );
        assertEquals( origTma.getTrkGsp(), modTma.getTrkGsp() );
        assertEquals( origTma.getTrkHdg(), modTma.getTrkHdg() );
        assertEquals( origTma.getTrkLatDecimalDegrees(), modTma.getTrkLatDecimalDegrees() );
        assertEquals( origTma.getTrkLonDecimalDegrees(), modTma.getTrkLonDecimalDegrees(), 0 );
        assertEquals( origTma.getTrkTtm(), modTma.getTrkTtm() );
        assertEquals( orig.getEnvelopeDate(), orig.getEnvelopeDate() );
        assertEquals( orig.getEnvelopeSource(), orig.getEnvelopeSource() );
        //assertEquals (origTma.getAirAirTypeValue(), modTma.getAirAirTypeValue());
        //assertEquals (origTma.getFltAcsValue(), modTma.getFltAcsValue());
        assertEquals( origTma.getFltCtm(), modTma.getFltCtm() );
        //assertEquals (origTma.getFltFpsValue(), modTma.getFltFpsValue());
        //assertEquals (origTma.getFltSrsValue(), modTma.getFltSrsValue());
        //assertEquals (origTma.getFltStd(), modTma.getFltStd());
        assertEquals( origTma.getAirTmaId(), modTma.getAirTmaId() );
        //assertEquals (origTma.getMsgId(), modTma.getMsgId());
        assertEquals( origTma.getMsgTime(), modTma.getMsgTime() );
        
        assertEquals( origTma.getFltDnt(), modTma.getFltDnt() );
        assertEquals( origTma.getFltRtm(), modTma.getFltRtm() );
        assertEquals( origTma.getFltSid(), modTma.getFltSid() );

        List<FlatTmaMrpEtaStaSchDataType> orig_mrp_eta_sta_sch_list = origTma.getFlatTmaMrpEtaStaSchDataList();
        List<FlatTmaMrpEtaStaSchDataType> mod_mrp_eta_sta_sch_list = modTma.getFlatTmaMrpEtaStaSchDataList();

        for( int i = 0; i < orig_mrp_eta_sta_sch_list.size(); i ++ )
        {
            FlatTmaMrpEtaStaSchDataType orig_mrp_eta_sta_sch = orig_mrp_eta_sta_sch_list.get( i );
            FlatTmaMrpEtaStaSchDataType mod_mrp_eta_sta_sch = mod_mrp_eta_sta_sch_list.get( i );

            assertEquals( orig_mrp_eta_sta_sch.getMrpMfx(), mod_mrp_eta_sta_sch.getMrpMfx() );
            assertEquals( orig_mrp_eta_sta_sch.getEtaMfx(), mod_mrp_eta_sta_sch.getSchMfx() );
            assertEquals( orig_mrp_eta_sta_sch.getStaMfx(), mod_mrp_eta_sta_sch.getStaMfx() );
            assertEquals( orig_mrp_eta_sta_sch.getSchMfx(), mod_mrp_eta_sta_sch.getSchMfx() );

            assertEquals( orig_mrp_eta_sta_sch.getMrpSfx(), mod_mrp_eta_sta_sch.getMrpSfx() );
            assertEquals( orig_mrp_eta_sta_sch.getMrpRwy(), mod_mrp_eta_sta_sch.getMrpRwy() );

            assertEquals( orig_mrp_eta_sta_sch.getEtaEtaRwy(), mod_mrp_eta_sta_sch.getEtaEtaRwy() );
            assertEquals( orig_mrp_eta_sta_sch.getEtaEtaSfx(), mod_mrp_eta_sta_sch.getEtaEtaSfx() );
            assertEquals( orig_mrp_eta_sta_sch.getEtaEtaMfx(), mod_mrp_eta_sta_sch.getEtaEtaMfx() );

            assertEquals( orig_mrp_eta_sta_sch.getStaStaRwy(), mod_mrp_eta_sta_sch.getStaStaRwy() );
            assertEquals( orig_mrp_eta_sta_sch.getStaStaSfx(), mod_mrp_eta_sta_sch.getStaStaSfx() );

            assertEquals( orig_mrp_eta_sta_sch.isSchRfz(), mod_mrp_eta_sta_sch.isSchRfz() );
            assertEquals( orig_mrp_eta_sta_sch.isSchSfz(), mod_mrp_eta_sta_sch.isSchSfz() );
            assertEquals( orig_mrp_eta_sta_sch.isSchSus(), mod_mrp_eta_sta_sch.isSchSus() );
            assertEquals( orig_mrp_eta_sta_sch.isSchMan(), mod_mrp_eta_sta_sch.isSchMan() );
        }
    }

    private void checkForNullMatmValues( MatmFlight flight )
    {

        assertNotNull( flight.getTimestamp() );
        assertNotNull( flight.getSystemId() );
        assertNotNull( flight.getGufi() );
        assertNotNull( flight.isSensitiveDataExternal() );
        assertNotNull( flight.getArrivalRunwayEstimatedTime() );
        assertNotNull( flight.getRouteText() );
        assertNotNull( flight.getRouteTextFiled() );
        assertNotNull( flight.getAcid() );
        assertNotNull( flight.getArrivalAerodrome().getIataName() );
        //assertNotNull (flight.getAltitudeRequested());
        assertNotNull( flight.getAltitudeAssigned() );
        assertNotNull( flight.getSpeedFiled() );
        assertNotNull( flight.getDepartureRunwayAssigned() );
        assertNotNull( flight.getArrivalRunwayAssigned() );
        assertNotNull( flight.getBeaconCode() );
        assertNotNull( flight.getDepartureAerodrome().getIataName() );
        assertNotNull( flight.getDepartureRunwayEstimatedTime() );
        assertNotNull( flight.getEstimatedDepartureClearanceTime() );
        assertNotNull( flight.getAircraftType() );
        assertNotNull( flight.getAircraftEquipmentQualifier() );
        assertNotNull( flight.getAircraftEngineClass() );
        assertNotNull( flight.getPosition().getAltitude() );
        assertNotNull( flight.getPosition().getSpeed() );
        assertNotNull( flight.getPosition().getHeading() );
        assertNotNull( flight.getPosition().getLatitude() );
        assertNotNull( flight.getPosition().getLongitude() );
        assertNotNull( flight.getPosition().getTimestamp() );

//		assertNotNull (flatTmaExt.getEnvelopeTime());
//		assertNotNull (flatTmaExt.getEnvelopeSource());
//		assertNotNull (flatTmaExt.getAirDataTypeValue());
//		assertNotNull (flatTmaExt.getAircraftStatus());
//		assertNotNull (flatTmaExt.getOffTimeCoordinated());
//		assertNotNull (flatTmaExt.getFlightPlanStatus());
//		assertNotNull (flatTmaExt.getSmsRequestStatus());
//		assertNotNull (flatTmaExt.getOffTimeScheduled());
        assertNotNull( flight.getExtensions().getTbfmExtension().getTmaId() );
//		assertNotNull (flatTmaExt.getMessageId());
//		assertNotNull (flatTmaExt.getMessageTime());

        assertNotNull( flight.getExtensions().getTbfmExtension().getArrivalMeterFix() );
        assertNotNull( flight.getExtensions().getTbfmExtension().getArrivalSchedulingFix() );
        assertNotNull( flight.getExtensions().getTbfmExtension().isArrivalRunwayAssignmentFrozen() );
        assertNotNull( flight.getExtensions().getTbfmExtension().isArrivalStasFrozen() );
        assertNotNull( flight.getExtensions().getTbfmExtension().getArrivalSchedulingFixSta() );
        assertNotNull( flight.getExtensions().getTbfmExtension().getArrivalMeterFixEta() );
        assertNotNull( flight.getExtensions().getTbfmExtension().getArrivalSchedulingFixEta() );
    }

    private void checkForNullTmaValues( GufiTmaType gufiTmaType )
    {
        FlatTmaType flatTma = new FlatTmaType( gufiTmaType );

        assertNotNull( gufiTmaType.getGufi() );
        assertNotNull( gufiTmaType.getSecurityLevel() );
        assertNotNull( gufiTmaType.isSyncMessage() );

        assertNotNull( flatTma.getFltStd() );
        assertNotNull( flatTma.getFltA10() );
        assertNotNull( flatTma.getFltOrAirAid() );
        assertNotNull( flatTma.getFltOrAirApt() );
        assertNotNull( flatTma.getFltAra() );
        assertNotNull( flatTma.getFltSpd() );
        assertNotNull( flatTma.getFltDrw() );
        assertNotNull( flatTma.getFltBcn() );
        assertNotNull( flatTma.getFltOrAirDap() );
        assertNotNull( flatTma.getFltEtd() );
        assertNotNull( flatTma.getFltEtm() );
        assertNotNull( flatTma.getFltAot() );
        assertNotNull( flatTma.getFltTyp() );
        assertNotNull( flatTma.getFltEng() );
        assertNotNull( flatTma.getTrkAlt().floatValue() );
        assertNotNull( flatTma.getTrkGsp().floatValue() );
        assertNotNull( flatTma.getTrkHdg().floatValue() );
        assertNotNull( flatTma.getTrkLatDecimalDegrees() );
        assertNotNull( flatTma.getTrkLonDecimalDegrees() );
        assertNotNull( flatTma.getTrkTtm() );
        assertNotNull( gufiTmaType.getEnvelopeDate() );
        assertNotNull( gufiTmaType.getEnvelopeSource() );
//		assertNotNull (flatTma.getAirAirTypeValue());
//		assertNotNull (flatTma.getFltAcsValue());

        //Wec
//		assertNull (flatTma.getFltCtm());
//		assertNotNull (flatTma.getFltFpsValue());
//		assertNotNull (flatTma.getFltSrsValue());
//		assertNotNull (flatTma.getFltStd());
        assertNotNull( flatTma.getAirTmaId() );
//		assertNotNull (flatTma.getMsgId());
        assertNotNull( flatTma.getMsgTime() );
        
        assertNotNull( flatTma.getFltDnt() );
        assertNotNull( flatTma.getFltRtm() );
        assertNotNull( flatTma.getFltSid() );        

        for( FlatTmaMrpEtaStaSchDataType mrp_eta_sta_sch : flatTma.getFlatTmaMrpEtaStaSchDataList() )
        {
            if( mrp_eta_sta_sch.isArrivalMrp() )
            {
                assertNotNull( mrp_eta_sta_sch.getEtaEtaRwy() );
                assertNotNull( mrp_eta_sta_sch.getStaStaRwy() );
                assertNotNull( mrp_eta_sta_sch.getMrpRwy() );
                assertNotNull( mrp_eta_sta_sch.getMrpMfx() );
                assertNotNull( mrp_eta_sta_sch.getMrpSfx() );
                assertNotNull( mrp_eta_sta_sch.isSchRfz() );
                assertNotNull( mrp_eta_sta_sch.isSchSfz() );
                assertNotNull( mrp_eta_sta_sch.getStaStaSfx() );
                assertNotNull( mrp_eta_sta_sch.getEtaEtaMfx() );
                assertNotNull( mrp_eta_sta_sch.getEtaEtaSfx() );
            }

            if( mrp_eta_sta_sch.isDepartureMrp() )
            {
                assertNotNull( mrp_eta_sta_sch.getMrpMfx() );
                assertNotNull( mrp_eta_sta_sch.getMrpSfx() );
                assertNotNull( mrp_eta_sta_sch.isSchRfz() );
                assertNotNull( mrp_eta_sta_sch.isSchSfz() );
                assertNotNull( mrp_eta_sta_sch.getStaStaSfx() );
                assertNotNull( mrp_eta_sta_sch.getEtaEtaMfx() );
                assertNotNull( mrp_eta_sta_sch.getEtaEtaSfx() );
            }
        }
    }

    private GufiTmaType createGufiTmaType()
    {
        long time = System.currentTimeMillis();

        GufiTmaType gufiTma = new GufiTmaType();
        gufiTma.setEnvelopeSource( "test_envelope_source" );
        gufiTma.setEnvelopeDate( new Date( time ) );
        gufiTma.setGufi( UUID.randomUUID().toString() );
        gufiTma.setNewFlightPlanEtm(true);
        gufiTma.setNewFlightPlanStd(true);
        gufiTma.setSecurityLevel( "All" );
        gufiTma.setSyncMessage( true );

        FlatTmaType tma = new FlatTmaType();
        gufiTma.setTmaType( tma.getTmaType() );

        tma.getTmaType().setAir( new AircraftInformation() );
        tma.getTmaType().getAir().setFlt( new FlightPlanInformation() );
        tma.getTmaType().getAir().setTrk( new TrackInformation() );

        tma.setAirAirType( AirDataType.NEW );
        tma.setFltStd( new Date( time + TimeFactory.MINUTE_IN_MILLIS * 2 ) );
        tma.setFltA10( "DFW..DAL" );
        tma.setFltAcs( AircraftStatus.TRACKED );
        tma.setFltAndAirAid( "AAL123" );
        tma.setFltAndAirApt( "DAL" );
        tma.setFltAra( "30000.0" );
        tma.setFltSpd( ".8" ); //Test for mach conversion
        tma.setFltDrw( "19L" );
        tma.setFltBcn( "7" );
        tma.setFltCtm( new Date( time + ( TimeFactory.MINUTE_IN_MILLIS * 10 ) ) );
        tma.setFltAndAirDap( "DFW" );
        tma.setFltEtd( new Date( time + ( TimeFactory.MINUTE_IN_MILLIS * 20 ) ) );
        tma.setFltEtm( new Date( time + ( TimeFactory.MINUTE_IN_MILLIS * 30 ) ) );
        tma.setFltAot( new Date( time + ( TimeFactory.MINUTE_IN_MILLIS * 31 ) ) );
        tma.setFltFps( FlightPlanStatus.ACTIVE );
        tma.setFltSrs( SmsRequestStatus.PENDING );
        tma.setFltStd( new Date( time + ( TimeFactory.MINUTE_IN_MILLIS * 40 ) ) );
        tma.setFltTrw( "18C" );
        tma.setFltTyp( "B777/Q" );
        tma.setFltEng( EngineType.JET );
        tma.setAirTmaId( "123456789" );
        tma.setFltRtm( "1477327260" );
        tma.setFltSid( "SWAP999" );
        tma.setFltDnt( "6" );
        tma.setTrkAlt( Float.valueOf( 12.4f ) );
        tma.setTrkGsp( Float.valueOf( 124f ) );
        tma.setTrkHdg( Float.valueOf( 13f ) );
        tma.setTrkLat( "325527N" );
        tma.setTrkLon( "0970554W" );
        tma.setTrkTtm( new Date( time + ( TimeFactory.MINUTE_IN_MILLIS * 5 ) ) );
        tma.setMsgId( "73367" );
        tma.setMsgTime( new Date( time ) );
        tma.setFltPTime( new Date( time + ( TimeFactory.MINUTE_IN_MILLIS * 41 )));

        //MRP data
        FlatTmaMrpEtaStaSchDataType mrp = tma.addFlatTmaMrpEtaStaSchDataType();
        mrp.setMrpCat( FlightPlanCategory.DEPARTURE );
        mrp.setMrpMfx( "test_departure_fix" );
        mrp.setEtaMfx( "test_departure_fix" );
        mrp.setStaMfx( "test_departure_fix" );
        mrp.setSchMfx( "test_departure_fix" );
        mrp.setMrpSfx( "SDFIX" );
        mrp.setSchSfz( Boolean.FALSE );
        mrp.setSchSus( Boolean.TRUE );
        mrp.setSchMan( Boolean.FALSE );
        mrp.setStaStaSfx( new Date( time + ( TimeFactory.MINUTE_IN_MILLIS * 33 ) ) );
        mrp.setEtaEtaMfx( new Date( time + ( TimeFactory.MINUTE_IN_MILLIS * 34 ) ) );
        mrp.setEtaEtaSfx( new Date( time + ( TimeFactory.MINUTE_IN_MILLIS * 35 ) ) );

        mrp = tma.addFlatTmaMrpEtaStaSchDataType();
        mrp.setMrpCat( FlightPlanCategory.ARRIVAL );
        mrp.setMrpRwy( "1R" );
        mrp.setMrpMfx( "test_arrival_fix" );
        mrp.setEtaMfx( "test_arrival_fix" );
        mrp.setStaMfx( "test_arrival_fix" );
        mrp.setSchMfx( "test_arrival_fix" );
        mrp.setEtaEtaRwy( new Date( time + TimeFactory.MINUTE_IN_MILLIS * 7 ) );
        mrp.setStaStaRwy( new Date( time + TimeFactory.MINUTE_IN_MILLIS * 71 ) );
        mrp.setMrpSfx( "SAFIX" );
        mrp.setSchRfz( Boolean.FALSE );
        mrp.setSchSfz( Boolean.TRUE );
        mrp.setSchSus( Boolean.FALSE );
        mrp.setStaStaSfx( new Date( time + ( TimeFactory.MINUTE_IN_MILLIS * 43 ) ) );
        mrp.setEtaEtaMfx( new Date( time + ( TimeFactory.MINUTE_IN_MILLIS * 44 ) ) );
        mrp.setEtaEtaSfx( new Date( time + ( TimeFactory.MINUTE_IN_MILLIS * 45 ) ) );

        return gufiTma;
    }
}
