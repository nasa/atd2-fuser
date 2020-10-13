package com.mosaicatm.tmaplugin.matm;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.aptcode.AirportCodeException;
import com.mosaicatm.aptcode.AirportCodeTranslator;
import com.mosaicatm.aptcode.AirportCodeTranslatorFactory;
import com.mosaicatm.aptcode.AirportCodeUtil;
import com.mosaicatm.aptcode.data.AirportCodeEntry;
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.EngineClass;
import com.mosaicatm.matmdata.common.MeteredTime;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.TbfmExtension;
import com.mosaicatm.matmdata.flight.extension.TbfmMeterReferencePointType;
import com.mosaicatm.matmdata.flight.extension.TbfmStatusCodeType;
import com.mosaicatm.tma.common.data.AircraftStatus;
import com.mosaicatm.tma.common.data.FlightPlanStatus;
import com.mosaicatm.tma.common.flat.FlatTmaMrpEtaStaSchDataType;
import com.mosaicatm.tma.common.flat.FlatTmaType;
import com.mosaicatm.tma.common.message.GufiTmaType;
import com.mosaicatm.tmaplugin.util.SpeedConverterUtil;

public class GufiTmaTypeToMatmTransform
implements Transformer<MatmFlight, GufiTmaType>
{
    private static final long JAN_1_1990_MILLIS = 631152000000L;

    private static final String[] SECURITY_LEVEL_ALL = { "All", "*" };
    private static final HashSet<String> SECURITY_LEVEL_ALL_MAP = new HashSet<>( Arrays.asList( SECURITY_LEVEL_ALL ));
    private static final String SECURITY_LEVEL_RESTRICTED = "NAS Only";    

    private final Log log = LogFactory.getLog( getClass());

    private final ObjectFactory objectFactory = new ObjectFactory();
    private final com.mosaicatm.matmdata.flight.extension.ObjectFactory tbfmObjectFactory = 
        new com.mosaicatm.matmdata.flight.extension.ObjectFactory();

    private AirportCodeTranslator airportCodeTranslator;

    public GufiTmaTypeToMatmTransform()
    {
    	AirportCodeTranslatorFactory factory = new AirportCodeTranslatorFactory();
    	factory.setPreFetchAll( true );
    	factory.setPreFetchIcaoCodes( true );
    	factory.setPreFetchIataCodes( true );
    	factory.setPreFetchFaaLidCodes( true );
    	
    	try
    	{
    		airportCodeTranslator = factory.create();
    	}
    	catch( AirportCodeException ace )
    	{
    		log.error( "Failed to create the AirportCodeTranslator", ace );
    	}
    }
    
    public void setAirportCodeTranslator( AirportCodeTranslator airportCodeTranslator )
    {
        this.airportCodeTranslator = airportCodeTranslator;
    }

    @Override
    public MatmFlight transform( GufiTmaType gufiTmaType )
    {
        if( gufiTmaType == null )
        {
            return null;
        }

        MatmFlight matm = new MatmFlight();
        matm.setLastUpdateSource( "TMA" );
        matm.setTimestampSource( gufiTmaType.getEnvelopeDate());
        matm.setTimestampSourceReceived( gufiTmaType.getTimestampSourceReceive());
        matm.setTimestampSourceProcessed( gufiTmaType.getTimestampSourceProcess());

        if( gufiTmaType.getEnvelopeSource() != null )
        {
            matm.setSystemId( gufiTmaType.getEnvelopeSource());
        }

        if( gufiTmaType.getEnvelopeDate() != null )
        {
            matm.setTimestamp( gufiTmaType.getEnvelopeDate());
        }

        if( gufiTmaType.getGufi() != null )
        {
            matm.setGufi( gufiTmaType.getGufi());
        }

        if( gufiTmaType.getTmaType() != null )
        {
            FlatTmaType tma = new FlatTmaType( gufiTmaType );

            if( tma.getMsgTime() != null )
            {
                matm.setTimestamp( tma.getMsgTime());
            }

            if( !isNullOrEmptyString( tma.getFltOrAirAid() ))
            {
                matm.setAcid( tma.getFltOrAirAid());
            }

            AircraftType acType = parseAircraftType( tma.getFltTyp());
            if( acType != null )
            {
                if( !isNullOrEmptyString( acType.getType() ))
                {
                    matm.setAircraftType( acType.getType());
                }
                if( !isNullOrEmptyString( acType.getSuffix() ))
                {
                    matm.setAircraftEquipmentQualifier( acType.getSuffix());
                }
            }

            if( tma.getFltEng() != null )
            {
                switch( tma.getFltEng())
                {
                    case JET:
                        matm.setAircraftEngineClass( EngineClass.JET );
                        break;
                    case PISTON:
                        matm.setAircraftEngineClass( EngineClass.PISTON );
                        break;
                    case TURBO_PROP:
                        matm.setAircraftEngineClass( EngineClass.TURBO );
                        break;
                }
            }

            if( !isNullOrEmptyString( tma.getFltBcn()))
            {
                matm.setBeaconCode( String.valueOf( tma.getFltBcn() ));
            }

            //TMA will report the departure/arrival airports as a coordination points when the airport is not known
            if(  !isNullOrEmptyString( tma.getFltOrAirApt()) && ( tma.getFltOrAirApt().length() < 5 ))
            {
                matm.setArrivalAerodrome( getAerodrome( tma.getFltOrAirApt() ));
            }

            if( !isNullOrEmptyString( tma.getFltOrAirDap()) && ( tma.getFltOrAirDap().length() < 5 ))
            {
                matm.setDepartureAerodrome( getAerodrome( tma.getFltOrAirDap() ));                
            }

            if( !isNullOrEmptyString( tma.getFltAra()))
            {
                if(  ! tma.getFltAra().equals( "VFR" ) &&  ! tma.getFltAra().equals( "OTP" ))
                {
                    try
                    {
                        double value = Double.valueOf( tma.getFltAra());

                        if( tma.getFltFps() == FlightPlanStatus.PROPOSED )
                        {
                            matm.setAltitudeRequested( value );
                        }
                        else
                        {
                            matm.setAltitudeAssigned( value );
                        }
                    }
                    catch( NumberFormatException nfe )
                    {
                        log.warn( "Failed to set assigned/requested altitude of " + tma.getFltAra(), nfe );
                    }
                }
            }

            if( !isNullOrEmptyString( tma.getFltSpd() ))
            {
                try
                {
                    double value = Double.valueOf( tma.getFltSpd());

                    //The speed is in mach, not knots
                    if( value < 2 )
                    {
                        Double altitude = matm.getAltitudeAssigned();
                        if( altitude == null )
                        {
                            altitude = matm.getAltitudeRequested();
                        }

                        value = SpeedConverterUtil.machToKnots( value, altitude );
                    }
                    matm.setSpeedFiled( value );
                }
                catch( NumberFormatException nfe )
                {
                    log.warn( "Failed to set filed speed of " + tma.getFltSpd(), nfe );
                }
            }

            if( !isNullOrEmptyString( tma.getFltDrw() ))
            {
                matm.setDepartureRunwayAssigned( parseDepartureRunway( tma.getFltDrw() ));
            }

            if( !isNullOrEmptyString( tma.getFltA10() ))
            {
                matm.setRouteText( tma.getFltA10());
                matm.setRouteTextFiled(tma.getFltA10());
                matm.setFiledFlight( true );
            }

            if( tma.getFltEtd() != null )
            {
                matm.setDepartureRunwayEstimatedTime( tma.getFltEtd());
            }

            if( tma.getFltEtm() != null && gufiTmaType.isNewFlightPlanEtm() )
            {
                matm.setEstimatedDepartureClearanceTime( 
                    objectFactory.createMatmFlightEstimatedDepartureClearanceTime( tma.getFltEtm() ));
            }

            /* Note: P-Time from TBFM is not to be trusted on ATD2 -- it is the pre-departure coordination time. 
               For us, this is generally the TTOT that STBO sends.
               departureStandProposedTime is filtered out in attribute filtering.
             */
            if(( tma.getFltPTime() != null ))
            {
                matm.setDepartureStandProposedTime( tma.getFltPTime() );
            }

            if( tma.getFltAot() != null )
            {
                matm.setDepartureRunwayActualTime( objectFactory.createMatmFlightDepartureRunwayActualTime(tma.getFltAot()));
            }
            
            // Note: if newFlightPlanStd is true and the STD is null, that means the STD has been cancelled
            if( gufiTmaType.isNewFlightPlanStd() || ( tma.getFltStd() != null ))
            {
                MeteredTime metered_time = null;
                if( tma.getFltStd() != null )
                {
                    metered_time = new MeteredTime();
                    metered_time.setValue( tma.getFltStd() );
                }
                
                matm.setDepartureRunwayMeteredTime( objectFactory.createMatmFlightDepartureRunwayMeteredTime( metered_time ));
            }

            Position position = null;

            boolean hasPosition = false;

            if( tma.getTrkAlt() != null )
            {
                position = createIfNull( position );
                position.setAltitude( tma.getTrkAlt().doubleValue());
                hasPosition = true;
            }

            if( tma.getTrkGsp() != null )
            {
                position = createIfNull( position );
                position.setSpeed( tma.getTrkGsp().doubleValue());
                hasPosition = true;
            }

            if( tma.getTrkHdg() != null )
            {
                position = createIfNull( position );
                position.setHeading( tma.getTrkHdg().doubleValue());
                hasPosition = true;
            }

            if( tma.getTrkLatDecimalDegrees() != null )
            {
                position = createIfNull( position );
                position.setLatitude( tma.getTrkLatDecimalDegrees());
                hasPosition = true;
            }

            // need to multiply the longitude by -1 to put it back in the western hemisphere
            if( tma.getTrkLonDecimalDegrees() != null )
            {
                position = createIfNull( position );
                position.setLongitude( tma.getTrkLonDecimalDegrees());
                hasPosition = true;
            }

            if( tma.getTrkTtm() != null )
            {
                position = createIfNull( position );
                position.setTimestamp( tma.getTrkTtm());
                hasPosition = true;
            }

            if( hasPosition && position != null )
            {
                position.setSource( "TMA" );
            }

            if( position != null )
            {
                matm.setPosition( position );
            }

            MatmFlightExtensions extensions = new MatmFlightExtensions();
            TbfmExtension tbfmExt = new TbfmExtension();
            extensions.setTbfmExtension( tbfmExt );
            matm.setExtensions( extensions );            

            transformExtension( tma, position, matm, tbfmExt );
            transformFlatTmaMrpEtaStaSchDataType( tma.getFlatTmaMrpEtaStaSchDataList(), tbfmExt, matm );

            if(( tma.getFltAcs() != null ) && ( tma.getFltAcs() == AircraftStatus.LANDED ) && 
                            ( tbfmExt.getArrivalRunwayEta() != null ))
            {
                matm.setArrivalRunwayActualTime( tbfmExt.getArrivalRunwayEta() );
            }

            // securityLevel not available in the FlatTmaType object
            String securityLevel = gufiTmaType.getSecurityLevel();
            if( securityLevel != null )
            {
                tbfmExt.setSecurityLevel( securityLevel );

                if( SECURITY_LEVEL_ALL_MAP.contains( securityLevel ))
                {
                    matm.setSensitiveDataExternal( false );
                }
                else if( securityLevel.equals( SECURITY_LEVEL_RESTRICTED ))
                {
                    matm.setSensitiveDataExternal( true );
                }
                else
                {
                    log.error( "Unhandled securitiy level: " + securityLevel );
                }
            }  

            // isSyncMessage not (even though it appears to be) available in the FlatTmaType object
            tbfmExt.setSyncMessage( gufiTmaType.isSyncMessage() );
            
            if( tbfmExt.getMeterReferencePointList().size() > 10 )
            {
                log.warn( "Found large MRP list: " + matm.getGufi() + ", size=" + tbfmExt.getMeterReferencePointList().size() );
            }
        }

        logMatmFlight("transformed: ", matm);

        return matm;
    }

    private void logMatmFlight(String header, MatmFlight flight)
    {
        if (log.isDebugEnabled())
        {
            if (flight == null)
            {
                log.debug("NULL");
            }
            else
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                StringBuilder text = new StringBuilder(256);
                text.append(header).append(" ").append(flight.getAcid()).
                append(" ").append(flight.getGufi());
                addAerodromeToLog(text," ",flight.getDepartureAerodrome());
                addAerodromeToLog(text,"-",flight.getArrivalAerodrome());
                if (flight.getExtensions() != null)
                {
                    TbfmExtension tbfm = flight.getExtensions().getTbfmExtension();
                    if (tbfm != null && tbfm.getArrivalMeterFix() != null)
                    {
                        text.append(" arr mfx ").append(tbfm.getArrivalMeterFix());
                        addTimeToLog(text, format, " o4a ", tbfm.getArrivalOuter4ThArcSta());
                        addTimeToLog(text, format, " o3a ", tbfm.getArrivalOuter3RdArcSta());
                        addTimeToLog(text, format, " ooa ", tbfm.getArrivalOuterOuterArcSta());
                        addTimeToLog(text, format, " oma ", tbfm.getArrivalOuterMeterArcSta());
                        addTimeToLog(text, format, " dfx ", tbfm.getArrivalDisplayFixSta());
                        addTimeToLog(text, format, " sfx ", tbfm.getArrivalSchedulingFixSta());
                        addTimeToLog(text, format, " rwy ", tbfm.getArrivalRunwaySta());
                    }
                }
                log.debug(text.toString());
            }
        }

    }

    private void addAerodromeToLog(StringBuilder log, String label, Aerodrome aerodrome)
    {
        if (aerodrome != null)
        {
            if (aerodrome.getIataName() != null)
            {
                log.append(label).append(aerodrome.getIataName());
            }
            else
            {
                log.append(label).append(aerodrome.getIcaoName());
            }
        }
    }
    private void addTimeToLog(StringBuilder log, SimpleDateFormat format, String label, Date date)
    {
        if (date != null) log.append(label).append(format.format(date));
    }

    private Position createIfNull( Position pos )
    {
        Position position = pos;
        if( position == null )
        {
            position = new Position();
        }

        return position;
    }

    private void transformExtension( FlatTmaType tmaType, Position position,
        MatmFlight matm, TbfmExtension tbfmExt )
    {
        if( matm.getSystemId() != null )
        {
            tbfmExt.setSystemId( matm.getSystemId() );
        }
        
        if( position != null )
        {
            tbfmExt.setLastTbfmPosition( position );
        }      

        if( !isNullOrEmptyString( tmaType.getAirTmaId() ))
        {
            tbfmExt.setTmaId( tmaType.getAirTmaId());
        }

        //flt.trw is TRACON scratch pad runway. If this is a NASA modified TBFM, this runway is same as mrp.rwy. 
        //Advise to use mrp.rwy for STBO to identify the associated runway ETA & STA. 
        //Use flt.trw is STBO want to have internal decision made based on what is scratch pad runway        
        if( !isNullOrEmptyString( tmaType.getFltTrw() ))
        {
            matm.setArrivalRunwayScratchpad( tmaType.getFltTrw() );
            tbfmExt.setArrivalRunwayTraconAssigned( tmaType.getFltTrw());
        }

        if( tmaType.getFltRtmDate() != null)
        { 
            tbfmExt.setAircraftReadyTime( tmaType.getFltRtmDate() );
        }

        if( !isNullOrEmptyString( tmaType.getFltDnt() ))
        {
            try
            {
                int dnt = Integer.parseInt( tmaType.getFltDnt() );
                if(( dnt >= 0 ) && ( dnt < TbfmStatusCodeType.values().length ))
                {
                    tbfmExt.setDepartureSchedulingStatusCode( TbfmStatusCodeType.values()[dnt] );                    
                }
                else
                {
                    log.error( "Unable to map departure scheduling status code to enum: " + dnt );
                }
            }
            catch( NumberFormatException ex )
            {
                log.error( "Unable to parse departure scheduling status code: " + tmaType.getFltDnt() );
            }
        }        

        if( !isNullOrEmptyString( tmaType.getFltSid() ))
        {
            tbfmExt.setSwapAircraftId(tmaType.getFltSid() );
        }      
        
        tbfmExt.setEtm( tmaType.getFltEtm() );
        tbfmExt.setNewEtm( tmaType.isNewFlightPlanEtm() );        
        tbfmExt.setNewStd( tmaType.isNewFlightPlanStd() );
        
        if( tmaType.isNewFlightPlanStd() || ( tmaType.getFltStd() != null ))
        {
            tbfmExt.setStd( tbfmObjectFactory.createTbfmExtensionStd( tmaType.getFltStd() ));
        }
        
        
        tbfmExt.setCtm(tmaType.getFltCtm());
        tbfmExt.setNewCtm(tmaType.isNewFlightPlanCtm());
        
        tbfmExt.setDepartureProposedTime(tmaType.getFltPTime());
        
        if( !isNullOrEmptyString( tmaType.getFltFpsValue() ))
        {
            tbfmExt.setFlightPlanStatus( tmaType.getFltFpsValue() );
        }
        
        if( !isNullOrEmptyString( tmaType.getFltAcsValue() ))
        {
            tbfmExt.setAircraftStatus( tmaType.getFltAcsValue() );
        }
        
        if( !isNullOrEmptyString( tmaType.getFltDrw() ))
        {
            // The FltDrw is also being set as the departure runway assigned
            // value on the MatmFlight where extra processing is done to try
            // and format the data as a valid runway.  Storing it in the 
            // extension in the exact format as was received.
            tbfmExt.setDepartureRunway( tmaType.getFltDrw() );
        }
    }

    private void transformFlatTmaMrpEtaStaSchDataType( List<FlatTmaMrpEtaStaSchDataType> mrpEtaStaSchList, 
        TbfmExtension tbfmExt, MatmFlight matm )
    {
        if(( mrpEtaStaSchList == null ) || mrpEtaStaSchList.isEmpty() )
        {
            return;
        }
        Boolean is_arrival = null;
        Boolean is_departure = null;

        MeteredTime metered_time = null;
        for( FlatTmaMrpEtaStaSchDataType mrp_eta_sta_sch : mrpEtaStaSchList )
        {
            is_arrival = mrp_eta_sta_sch.isArrivalMrp();
            is_departure = mrp_eta_sta_sch.isDepartureMrp();

            TbfmMeterReferencePointType mrp = new TbfmMeterReferencePointType();
            mrp.setArtcc( getArtccFromEnvSource( matm.getSystemId() ));
            mrp.setMeterFix( mrp_eta_sta_sch.getMrpMfx() ); 
            mrp.setCategory( String.valueOf( mrp_eta_sta_sch.getMrpCat() ));
            mrp.setTracon( mrp_eta_sta_sch.getMrpTra() );            
            mrp.setGate( mrp_eta_sta_sch.getMrpGat() );
            mrp.setStreamClass( mrp_eta_sta_sch.getMrpScn() );
                        
            tbfmExt.getMeterReferencePointList().add( mrp );
            
            if( is_arrival != null && is_arrival )
            {
                if( mrp_eta_sta_sch.getEtaEtaRwy() != null )
                {
                    matm.setArrivalRunwayEstimatedTime( mrp_eta_sta_sch.getEtaEtaRwy());
                    tbfmExt.setArrivalRunwayEta( mrp_eta_sta_sch.getEtaEtaRwy() );
                }

                if( mrp_eta_sta_sch.getStaStaRwy() != null )
                {
                    metered_time = matm.getArrivalRunwayMeteredTime();
                    if( metered_time == null )
                    {
                        metered_time = new MeteredTime();
                        matm.setArrivalRunwayMeteredTime( metered_time );
                    }
                    metered_time.setValue( mrp_eta_sta_sch.getStaStaRwy());
                }                    

                if( !isNullOrEmptyString( mrp_eta_sta_sch.getMrpMfx() ))
                {
                    tbfmExt.setArrivalMeterFix( mrp_eta_sta_sch.getMrpMfx());
                }

                if( !isNullOrEmptyString( mrp_eta_sta_sch.getMrpSfx() ))
                {
                    tbfmExt.setArrivalSchedulingFix( mrp_eta_sta_sch.getMrpSfx());
                }

                if( !isNullOrEmptyString( mrp_eta_sta_sch.getMrpRwy() ))
                {
                    matm.setArrivalRunwayAssigned( mrp_eta_sta_sch.getMrpRwy() );
                    tbfmExt.setArrivalRunway( mrp_eta_sta_sch.getMrpRwy());
                }

                if( mrp_eta_sta_sch.isSchRfz() != null )
                {
                    tbfmExt.setArrivalRunwayAssignmentFrozen( mrp_eta_sta_sch.isSchRfz());
                }

                if( mrp_eta_sta_sch.getStaStaRwy() != null )
                {
                    tbfmExt.setArrivalRunwaySta( mrp_eta_sta_sch.getStaStaRwy());
                }

                if( mrp_eta_sta_sch.getEtaEtaMfx() != null )
                {
                    tbfmExt.setArrivalMeterFixEta( mrp_eta_sta_sch.getEtaEtaMfx());
                }

                if( mrp_eta_sta_sch.getEtaEtaSfx() != null )
                {
                    tbfmExt.setArrivalSchedulingFixEta( mrp_eta_sta_sch.getEtaEtaSfx());
                }

                if ( mrp_eta_sta_sch.getEtaEtaO4a() != null)
                {
                    tbfmExt.setArrivalOuter4ThArcEta( mrp_eta_sta_sch.getEtaEtaO4a());
                }

                if ( mrp_eta_sta_sch.getEtaEtaO3a() != null)
                {
                    tbfmExt.setArrivalOuter3RdArcEta( mrp_eta_sta_sch.getEtaEtaO3a());
                }

                if ( mrp_eta_sta_sch.getEtaEtaOoa() != null)
                {
                    tbfmExt.setArrivalOuterOuterArcEta( mrp_eta_sta_sch.getEtaEtaOoa());
                }

                if ( mrp_eta_sta_sch.getEtaEtaOma() != null)
                {
                    tbfmExt.setArrivalOuterMeterArcEta( mrp_eta_sta_sch.getEtaEtaOma());
                }

                if ( mrp_eta_sta_sch.getEtaEtaDfx() != null)
                {
                    tbfmExt.setArrivalDisplayFixEta( mrp_eta_sta_sch.getEtaEtaDfx());
                }

                if( mrp_eta_sta_sch.getStaStaSfx() != null )
                {
                    tbfmExt.setArrivalSchedulingFixSta( mrp_eta_sta_sch.getStaStaSfx());
                }

                if ( mrp_eta_sta_sch.getStaStaO4a() != null)
                {
                    tbfmExt.setArrivalOuter4ThArcSta( mrp_eta_sta_sch.getStaStaO4a());
                }

                if ( mrp_eta_sta_sch.getStaStaO3a() != null)
                {
                    tbfmExt.setArrivalOuter3RdArcSta( mrp_eta_sta_sch.getStaStaO3a());
                }

                if ( mrp_eta_sta_sch.getStaStaOoa() != null)
                {
                    tbfmExt.setArrivalOuterOuterArcSta( mrp_eta_sta_sch.getStaStaOoa());
                }

                if ( mrp_eta_sta_sch.getStaStaOma() != null)
                {
                    tbfmExt.setArrivalOuterMeterArcSta( mrp_eta_sta_sch.getStaStaOma());
                }

                if ( mrp_eta_sta_sch.getStaStaDfx() != null)
                {
                    tbfmExt.setArrivalDisplayFixSta( mrp_eta_sta_sch.getStaStaDfx());
                }

                if( mrp_eta_sta_sch.isSchSfz() != null )
                {
                    tbfmExt.setArrivalStasFrozen( mrp_eta_sta_sch.isSchSfz() );

                    metered_time = matm.getArrivalRunwayMeteredTime();
                    if( metered_time == null )
                    {
                        metered_time = new MeteredTime();
                        matm.setArrivalRunwayMeteredTime( metered_time );
                    }
                    metered_time.setFrozen( mrp_eta_sta_sch.isSchSfz() );                    
                }

                if( mrp_eta_sta_sch.isSchSus() != null )
                {
                    tbfmExt.setArrivalSchedulingSuspended( mrp_eta_sta_sch.isSchSus());
                }

                if( mrp_eta_sta_sch.getMrpOma() != null )
                {
                    tbfmExt.setArrivalOuterMeterArc( mrp_eta_sta_sch.getMrpOma() );
                }

                if( mrp_eta_sta_sch.getMrpOoa() != null )
                {
                    tbfmExt.setArrivalOuterOuterArc( mrp_eta_sta_sch.getMrpOoa() );
                }

                if( mrp_eta_sta_sch.getMrpO3A() != null )
                {
                    tbfmExt.setArrivalOuter3RdArc( mrp_eta_sta_sch.getMrpO3A() );
                }

                if( mrp_eta_sta_sch.getMrpO4A() != null )
                {
                    tbfmExt.setArrivalOuter4ThArc( mrp_eta_sta_sch.getMrpO4A() );
                }
                
                if( mrp_eta_sta_sch.getMrpScn() != null )
                {
                    tbfmExt.setArrivalStreamClass( mrp_eta_sta_sch.getMrpScn() );
                }
                
                if( mrp_eta_sta_sch.getMrpTra() != null )
                {
                    tbfmExt.setArrivalTracon( mrp_eta_sta_sch.getMrpTra() );
                }

                if( mrp_eta_sta_sch.getMrpCfg()!= null )
                {
                    tbfmExt.setArrivalConfiguration( mrp_eta_sta_sch.getMrpCfg() );
                }

                if( mrp_eta_sta_sch.getMrpGat() != null )
                {
                    tbfmExt.setArrivalGate( mrp_eta_sta_sch.getMrpGat() );
                }                
            }
            else if( is_departure != null && is_departure )
            {
                if( !isNullOrEmptyString( mrp_eta_sta_sch.getMrpMfx() ))
                {
                    tbfmExt.setDepartureMeterFix( mrp_eta_sta_sch.getMrpMfx());
                }

                if( !isNullOrEmptyString( mrp_eta_sta_sch.getMrpSfx() ))
                {
                    tbfmExt.setDepartureSchedulingFix( mrp_eta_sta_sch.getMrpSfx());
                }

                if( mrp_eta_sta_sch.getEtaEtaMfx() != null )
                {
                    tbfmExt.setDepartureMeterFixEta( mrp_eta_sta_sch.getEtaEtaMfx());
                }

                if( mrp_eta_sta_sch.getEtaEtaSfx() != null )
                {
                    tbfmExt.setDepartureSchedulingFixEta( mrp_eta_sta_sch.getEtaEtaSfx());
                }

                if( mrp_eta_sta_sch.getStaStaSfx() != null )
                {
                    tbfmExt.setDepartureSchedulingFixSta( mrp_eta_sta_sch.getStaStaSfx());
                }

                if( mrp_eta_sta_sch.isSchSfz() != null )
                {
                    tbfmExt.setDepartureStasFrozen( mrp_eta_sta_sch.isSchSfz() );
                    //The metered time would have been picked up as the STD earlier in the standard transform
                    if(( matm.getDepartureRunwayMeteredTime() != null ) && 
                            ( matm.getDepartureRunwayMeteredTime().getValue() != null ))
                    {
                        matm.getDepartureRunwayMeteredTime().getValue().setFrozen( mrp_eta_sta_sch.isSchSfz() );   
                    }
                }

                if( mrp_eta_sta_sch.isSchSus() != null )
                {
                    tbfmExt.setDepartureSchedulingSuspended( mrp_eta_sta_sch.isSchSus());
                }
                
                if( mrp_eta_sta_sch.getMrpScn() != null )
                {
                    tbfmExt.setDepartureStreamClass( mrp_eta_sta_sch.getMrpScn() );
                }
                
                if( mrp_eta_sta_sch.getMrpTra() != null )
                {
                    tbfmExt.setDepartureTracon( mrp_eta_sta_sch.getMrpTra() );
                }

                if( mrp_eta_sta_sch.getMrpCfg()!= null )
                {
                    tbfmExt.setDepartureConfiguration( mrp_eta_sta_sch.getMrpCfg() );
                }

                if( mrp_eta_sta_sch.getMrpGat() != null )
                {
                    tbfmExt.setDepartureGate( mrp_eta_sta_sch.getMrpGat() );
                }
                
                if( mrp_eta_sta_sch.getSchMan() != null )
                {
                    tbfmExt.setManuallyScheduled( mrp_eta_sta_sch.getSchMan() );
                }
            }
        }

        //If we were not able to find RWY fields the first try (which happens if the data is not fully merged),
        //we can try again, relaxing the MRP type detection logic -- there should really only be a single MRP with RWY fields.            
        if(( matm.getArrivalRunwayEstimatedTime() == null ) || 
                        ( matm.getArrivalRunwayMeteredTime() == null ) ||
                        ( matm.getArrivalRunwayAssigned() == null ))
        {
            StringBuilder warning_types = new StringBuilder();
            boolean missing_metered_time = ( matm.getArrivalRunwayMeteredTime() == null );
            for( FlatTmaMrpEtaStaSchDataType mrp_eta_sta_sch : mrpEtaStaSchList )
            {
                if(( matm.getArrivalRunwayEstimatedTime() == null ) && ( mrp_eta_sta_sch.getEtaEtaRwy() != null ))
                {
                    matm.setArrivalRunwayEstimatedTime( mrp_eta_sta_sch.getEtaEtaRwy());
                    tbfmExt.setArrivalRunwayEta( mrp_eta_sta_sch.getEtaEtaRwy() );

                    if( warning_types.length() > 0 )
                    {
                        warning_types.append( "," );
                    }
                    warning_types.append( "ETA" );
                }

                if( missing_metered_time && ( mrp_eta_sta_sch.getStaStaRwy() != null ))
                {
                    metered_time = matm.getArrivalRunwayMeteredTime();
                    if( metered_time == null )
                    {
                        metered_time = new MeteredTime();
                        matm.setArrivalRunwayMeteredTime( metered_time );
                    }
                    metered_time.setValue( mrp_eta_sta_sch.getStaStaRwy());

                    if( warning_types.length() > 0 )
                    {
                        warning_types.append( "," );
                    }
                    warning_types.append( "STA" );                    
                }

                if( missing_metered_time && ( mrp_eta_sta_sch.isSchSfz() != null ))
                {
                    metered_time = matm.getArrivalRunwayMeteredTime();
                    if( metered_time == null )
                    {
                        metered_time = new MeteredTime();
                        matm.setArrivalRunwayMeteredTime( metered_time );
                    }

                    metered_time.setFrozen( mrp_eta_sta_sch.isSchSfz() );
                    tbfmExt.setArrivalStasFrozen( mrp_eta_sta_sch.isSchSfz() );

                    if( warning_types.length() > 0 )
                    {
                        warning_types.append( "," );
                    }
                    warning_types.append( "isSchSfz" );                    
                }                

                if(( matm.getArrivalRunwayAssigned() == null ) && !isNullOrEmptyString( mrp_eta_sta_sch.getMrpRwy() ))
                {
                    matm.setArrivalRunwayAssigned( mrp_eta_sta_sch.getMrpRwy() );
                    tbfmExt.setArrivalRunway( mrp_eta_sta_sch.getMrpRwy());

                    if( warning_types.length() > 0 )
                    {
                        warning_types.append( "," );
                    }
                    warning_types.append( "ArrRwy" );  
                }                       
            }  

            if( log.isDebugEnabled() && ( warning_types.length() > 0 ))
            {
                warning_types.insert( 0, "TBFM elements found [" );
                warning_types.append( "], but not in merged data [" );
                warning_types.append( matm.getSystemId() );
                warning_types.append( "]: " ).append( matm.getGufi() );

                log.debug( warning_types.toString() );
            }
        }
    }

    private static String parseDepartureRunway( String runway )
    {
        //TMA departure runways are funky. Sometimes they are the airport
        //name, underscore, runway e.g IAD_19L
        //Note, they can also be something we cannot handle, e.g. CLT_NORTH, 
        //but hopefully that can be fixed in the TMA adaptation
        if( runway.contains( "_" ))
        {
            runway = runway.substring( runway.lastIndexOf( "_" ) + 1 );
        }

        return ( runway );
    }

    private AircraftType parseAircraftType( String aircraftType )
    {
        if( isNullOrEmptyString( aircraftType ))
        {
            return null;
        }

        String[] tokens = aircraftType.split( "/" );
        AircraftType ac_type = new AircraftType();

        if( tokens.length == 1 )
        {
            ac_type.setType( tokens[0] );
        }
        else if( tokens.length == 2 )
        {
            if( tokens[0].length() > tokens[1].length())
            {
                ac_type.setType( tokens[0] );
                ac_type.setSuffix( tokens[1] );
            }
            else
            {
                ac_type.setPrefix( tokens[0] );
                ac_type.setType( tokens[1] );
            }
        }
        else if( tokens.length == 3 )
        {
            ac_type.setPrefix( tokens[0] );
            ac_type.setType( tokens[1] );
            ac_type.setSuffix( tokens[2] );
        }

        return ( ac_type );
    }

    private class AircraftType
    {
        private String type = null;
        private String prefix = null;
        private String suffix = null;

        public String getPrefix()
        {
            return prefix;
        }

        public void setPrefix( String prefix )
        {
            this.prefix = prefix;
        }

        public String getSuffix()
        {
            return suffix;
        }

        public void setSuffix( String suffix )
        {
            this.suffix = suffix;
        }

        public String getType()
        {
            return type;
        }

        public void setType( String type )
        {
            this.type = type;
        }
    }

    private static boolean isNullOrEmptyString( String string )
    {
        if(( string == null ) || ( string.trim().isEmpty() ))
        {
            return ( true );
        }
        else
        {
            return ( false );
        }
    }

    private Aerodrome getAerodrome( String airport )
    {
        Aerodrome aerodrome = new Aerodrome();

        if( airport != null )
        {
            String iata = null;
            String icao = null;

            if( airportCodeTranslator != null )
            {
                try
                {
                    AirportCodeEntry entry =
                                    airportCodeTranslator.getBestMatchAirportCodeFromFaaSource( airport );

                    if( entry != null )
                    {
                        icao = entry.getIcao();

                        iata = entry.getIata();
                        if( iata == null )
                        {
                            iata = entry.getFaaLid();
                        }
                    }
                }
                catch( AirportCodeException ex )
                {
                    log.error( "Error converting airport code.", ex );
                }
            }
            else
            {
                log.error( "AirportCodeTranslator is null!!!" );
            }

            if(( iata != null ) || ( icao != null ))
            {
                aerodrome.setIataName( iata );
                aerodrome.setIcaoName( icao );
            }
            //the default system behavior is to put the
            //source airport into the IATA field, when IATA and ICAO does not exist.             
            else 
            {
                //We default to IATA unless pretty sure it is ICAO
                if( AirportCodeUtil.isIcaoTextFormat( airport ))
                {
                    aerodrome.setIcaoName( airport );
                }
                else
                {
                    aerodrome.setIataName( airport );
                }
            }
        }

        return( aerodrome );
    }  
    
    private static String getArtccFromEnvSource( String envSrc )
    {
        String artcc = null;
        
        //TMA.ZFW.FAA.GOV
        if(( envSrc != null ) && ( envSrc.indexOf( "TMA.Z" ) == 0 ))
        {
            return( envSrc.substring( 4, 7 ));
        }
        
        return( artcc );
    }
}
