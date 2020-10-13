package com.mosaicatm.ttpplugin.matm;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.aptcode.AirportCodeException;
import com.mosaicatm.aptcode.AirportCodeTranslator;
import com.mosaicatm.aptcode.AirportCodeTranslatorFactory;
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.FlightRestrictions;
import com.mosaicatm.matmdata.common.FlightType;
import com.mosaicatm.matmdata.common.MeteredTime;
import com.mosaicatm.matmdata.common.SurfaceFlightState;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;
import com.mosaicatm.matmdata.flight.extension.TfmTfdmExtension;
import com.mosaicatm.ttp.util.TtpFlightDataWrapper;

import aero.faa.nas._4.DiversionRecoveryIndicatorType;
import aero.faa.nas._4.NameValuePairType;
import aero.faa.nas._4.NasArrivalType;
import aero.faa.nas._4.NasDepartureType;
import aero.faa.nas._4.NasDestinationType;
import aero.faa.nas._4.NasFlightIdentificationType;
import aero.faa.nas._4.NasFlightPlanType;
import aero.faa.nas._4.NasFlightType;
import aero.faa.nas._4.NasMessageType;
import aero.faa.nas._4.NasRunwayArrivalEstimatedType;
import aero.faa.nas._4.NasRunwayDepartureEstimatedType;
import aero.faa.nas._4.ReportedTimeType;
import aero.faa.nas._4.TfdmFlightStateType;
import aero.fixm.base._4.DesignatedPointOrNavaidType;
import aero.fixm.base._4.SignificantPointType;
import aero.fixm.flight._4.FlightIdentificationType;

public class TtpToMatmTransform implements Transformer<MatmFlight, TtpFlightDataWrapper>
{
    private static final Logger logger = LoggerFactory.getLogger( TtpToMatmTransform.class );

    private static final String ACTUAL_ARRIVAL_SPOT_KEY = "ACTUAL_ARR_SPOT";
    private static final String ACTUAL_DEPARTURE_SPOT_KEY = "ACTUAL_DEP_SPOT";
    private static final String AIRLINE_KEY = "AIRLINE";
    private static final String ARRIVAL_RUNWAY_ACTUAL_KEY = "ARR_RWY_ACTUAL";
    private static final String ARRIVAL_RUNWAY_PREDICTED_KEY = "ARR_RWY_PREDICTED";
    private static final String DEPARTURE_RUNWAY_ACTUAL_KEY = "DEP_RWY_ACTUAL";
    private static final String DEPARTURE_RUNWAY_PREDICTED_KEY = "DEP_RWY_PREDICTED";
    private static final String GENERAL_AVIATION_CARRIER = "XXX";
    private static final String PREDICTED_ARRIVAL_SPOT_KEY = "PREDICTED_ARR_SPOT";
    private static final String PREDICTED_DEPARTURE_SPOT_KEY = "PREDICTED_DEP_SPOT";
    private static final String TARGETED_OFF_BLOCK_TIME_KEY = "TOBT";
    private static final String TARGETED_TAKEOFF_TIME_KEY = "TTOT";
    private static final String TFDM_ID_CREATOR_KEY = "TFDMIDCreator";
    private static final String UNKNOWN_CARRIER = "---";


    private AirportCodeTranslator airportCodeTranslator;
    private DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
    private ObjectFactory objFactory = new ObjectFactory();


    public TtpToMatmTransform()
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
            logger.error( "Failed to create the AirportCodeTranslator", ace );
        }
    }
    
    @Override
    public MatmFlight transform( TtpFlightDataWrapper ttpFlightDataWrapper )
    {
        NasMessageType nasMsg = ttpFlightDataWrapper.getNasMessageType();
        
        if( nasMsg == null || nasMsg.getFlight() == null )
        {
            return null;
        }

        MatmFlight matmFlight = new MatmFlight();
        matmFlight.setLastUpdateSource( "TTP" );

        String messageType = nasMsg.getMetadata().getMessageType();
        NasFlightType nasFlight = nasMsg.getFlight();

        setAcid( matmFlight, nasFlight );
        setAircraftRegistration( matmFlight, nasFlight );
        setArrivalAerodromeIcaoName( matmFlight, nasFlight );
        setArrivalFixSourceData( matmFlight, nasFlight );
        setArrivalMovementAreaActualTime( matmFlight, nasFlight );
        setArrivalRunwayActual( matmFlight, nasFlight );
        setArrivalRunwayActualTime( matmFlight, nasFlight );
        setArrivalRunwaySource( matmFlight, nasFlight );
        setArrivalRunwaySourceData( matmFlight, nasFlight );
        setArrivalRunwayTargetedTime( matmFlight, nasFlight );
        setArrivalSpotActual( matmFlight, nasFlight );
        setArrivalSpotSource( matmFlight, nasFlight );
        setArrivalSpotSourceData( matmFlight, nasFlight );
        setArrivalStandSource( matmFlight, nasFlight );
        setArrivalStandSourceData( matmFlight, nasFlight );
        setArrivalStandActualTime( matmFlight, nasFlight );
        setCarrier( matmFlight, nasFlight );
        setComputerId( matmFlight, nasFlight );
        setCreationTime( matmFlight, nasFlight );
        setDepartureAerodromeIcaoName( matmFlight, nasFlight );
        setDepartureFixSourceData( matmFlight, nasFlight );
        setDepartureMovementAreaActualTime( matmFlight, nasFlight );
        setDepartureMovementAreaSurfaceMeteredTime( matmFlight, nasFlight );
        setDepartureRunwayActual( matmFlight, nasFlight );
        setDepartureRunwayActualTime( matmFlight, nasFlight );
        setDepartureRunwayMeteredTime( matmFlight, nasFlight );
        setDepartureRunwaySource( matmFlight, nasFlight );
        setDepartureRunwaySourceData( matmFlight, nasFlight );
        setDepartureRunwayTargetedTime( matmFlight, nasFlight );
        setDepartureRunwayTargetedTime2( matmFlight, nasFlight );
        setDepartureRunwayTargetedTime3( matmFlight, nasFlight );
        setDepartureRunwayUndelayedTime( matmFlight, nasFlight );
        setDepartureSpotActual( matmFlight, nasFlight );
        setDepartureSpotSource( matmFlight, nasFlight );
        setDepartureSpotSourceData( matmFlight, nasFlight );
        setDepartureStandSource( matmFlight, nasFlight );
        setDepartureStandSourceData( matmFlight, nasFlight );
        setDepartureStandActualTime( matmFlight, nasFlight );
        setDepartureStandEarliestTime( matmFlight, nasFlight );
        setDepartureStandInitialTime( matmFlight, nasFlight );
        setDepartureStandSurfaceMeteredTime( matmFlight, nasFlight );
        setEramGufi( matmFlight, nasFlight );
        setFlightState( matmFlight, nasFlight );
        setGufi( matmFlight, ttpFlightDataWrapper );
        setIntendedDeicingLocation( matmFlight, nasFlight );
        setIsDiversion( matmFlight, nasFlight );
        setMajorCarrier( matmFlight, nasFlight );
        setTimestampSource( matmFlight, ttpFlightDataWrapper );
        setTimestampSourceReceived( matmFlight, ttpFlightDataWrapper );
        setTimestampSourceProcessed( matmFlight, ttpFlightDataWrapper );
        
        return matmFlight;
    }

    /**
     * TTP DeparturePoint -> MatmFlight DepartureAerodromeIcaoName
     */
    private void setDepartureAerodromeIcaoName( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        // Departure
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            
            if( departure.getDeparturePointText() != null )
            {
                try
                {
                    Aerodrome departureAerodrome = new Aerodrome();
                    if( airportCodeTranslator.isIcao( departure.getDeparturePointText() ) )
                    {
                        departureAerodrome.setIcaoName( departure.getDeparturePointText() );
                        matmFlight.setDepartureAerodrome( departureAerodrome );
                    }
                    else if( airportCodeTranslator.isIata( departure.getDeparturePointText() ) )
                    {
                        departureAerodrome.setIataName( departure.getDeparturePointText() );
                        matmFlight.setDepartureAerodrome( departureAerodrome );
                    }
                }
                catch( AirportCodeException e )
                {
                    logger.warn( e.getLocalizedMessage(), e );
                }
            }
        }
    }

    /**
     * TTP DestinationPoint -> MatmFlight ArrivalAerodromeIcaoName
     */
    private void setArrivalAerodromeIcaoName( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDestination() != null && nasFlight.getDestination() instanceof NasDestinationType )
        {
            NasDestinationType destination = (NasDestinationType)nasFlight.getDestination();
            
            if( destination.getDestinationPointText() != null )
            {
                try
                {
                    Aerodrome destinationAerodrome = new Aerodrome();
                    if( airportCodeTranslator.isIcao( destination.getDestinationPointText() ) )
                    {
                        destinationAerodrome.setIcaoName( destination.getDestinationPointText() );
                        matmFlight.setArrivalAerodrome( destinationAerodrome );
                    }
                    else if( airportCodeTranslator.isIata( destination.getDestinationPointText() ) )
                    {
                        destinationAerodrome.setIataName( destination.getDestinationPointText() );
                        matmFlight.setArrivalAerodrome( destinationAerodrome );
                    }
                }
                catch( AirportCodeException e )
                {
                    logger.warn( e.getLocalizedMessage(), e );
                }
            }
        }
    }

    /**
     * TTP Acid -> MatmFlight Acid
     */
    private void setAcid( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getFlightIdentification() != null )
        {
            FlightIdentificationType flightIdType = nasFlight.getFlightIdentification();
            matmFlight.setAcid( flightIdType.getAircraftIdentification() );
        }
    }

    /**
     * TTP AircraftRegistrationMark -> MatmFlight AircraftRegistration
     */
    private void setAircraftRegistration( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAircraft() != null )
        {
            matmFlight.setAircraftRegistration( nasFlight.getAircraft().getRegistration() );
        }
    }

    /**
     * TTP ComputerId -> MatmFlight ComputerId
     */
    private void setComputerId( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getFlightIdentification() != null )
        {
            FlightIdentificationType flightIdType = nasFlight.getFlightIdentification();
            if( flightIdType instanceof NasFlightIdentificationType )
            {
                NasFlightIdentificationType nasFlightIdType = (NasFlightIdentificationType)flightIdType;
                matmFlight.setComputerId( nasFlightIdType.getComputerId() );
            }
        }
    }

    /**
     * TTP InitialGateTimeOfDepature -> MatmFlight DepartureStandInitialTime
     */
    private void setDepartureStandInitialTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            if( departure.getOffBlockTime() != null &&
                    departure.getOffBlockTime().getInitial() != null )
            {
                if( !departure.getOffBlockTime().getInitial().isNil() )
                {
                    matmFlight.setDepartureStandInitialTime( departure.getOffBlockTime().getInitial().getValue().getValue().toGregorianCalendar().getTime() );
                }
                else
                {
                    matmFlight.setDepartureStandInitialTime( null );
                }
            }
        }
    }

    /**
     * TTP ActualOffBlockTime -> MatmFlight DepartureStandActualTime
     */
    private void setDepartureStandActualTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            if( departure.getOffBlockTime() != null &&
                    departure.getOffBlockTime().getActual() != null )
            {
                if( !departure.getOffBlockTime().getActual().isNil() )
                {
                    Date depStandActTime = departure.getOffBlockTime().getActual().getValue().getTime().getValue().toGregorianCalendar().getTime();
                    matmFlight.setDepartureStandActualTime( objFactory.createMatmFlightDepartureStandActualTime( depStandActTime ) );
                }
                else
                {
                    matmFlight.setDepartureStandActualTime( objFactory.createMatmFlightDepartureStandActualTime( null ) );
                }
            }
        }
    }

    /**
     * TTP EarliestOffBlockTime -> MatmFlight DepartureStandEarliestTime
     */
    private void setDepartureStandEarliestTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            if( departure.getOffBlockTime() != null &&
                    departure.getOffBlockTime().getEarliest() != null )
            {
                if( !departure.getOffBlockTime().getEarliest().isNil() )
                {
                    Date depStandEarliestTime = departure.getOffBlockTime().getEarliest().getValue().getTime().getValue().toGregorianCalendar().getTime();
                    matmFlight.setDepartureStandEarliestTime( depStandEarliestTime );
                }
                else
                {
                    matmFlight.setDepartureStandEarliestTime( null );
                }
            }
        }
    }

    /**
     * TTP TargetedOffBlockTime -> MatmFlight DepartureStandSurfaceMeteredTime
     */
    private void setDepartureStandSurfaceMeteredTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
                nasFlight.getAdditionalFlightInformation().getNameValue() != null &&
                !nasFlight.getAdditionalFlightInformation().getNameValue().isEmpty() )
        {
            for( NameValuePairType nameValuePair : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( TARGETED_OFF_BLOCK_TIME_KEY.equals( nameValuePair.getName() ) )
                {
                    if( nameValuePair.getValue() != null &&
                        !nameValuePair.getValue().isEmpty() )
                    {
                        try
                        {
                            Date depStandSurfaceMeteredTime = dateFormat.parse( nameValuePair.getValue() );
                            JAXBElement<Date> value = objFactory.createMatmFlightDepartureStandSurfaceMeteredTime( depStandSurfaceMeteredTime );
                            matmFlight.setDepartureStandSurfaceMeteredTime( value );
                        }
                        catch( ParseException pe )
                        {
                            logger.warn( "Problem parsing the TOBT.", pe );
                        }
                    }
                    else
                    {
                        JAXBElement<Date> value = objFactory.createMatmFlightDepartureStandSurfaceMeteredTime( null );
                        matmFlight.setDepartureStandSurfaceMeteredTime( value );
                    }
                    break;
                }
            }
        }
    }

    /**
     * TTP EramGufi -> MatmFlight EramGufi
     */
    private void setEramGufi( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getFlightPlan() != null )
        {
            NasFlightPlanType flightPlan = nasFlight.getFlightPlan();
            
            matmFlight.setEramGufi( flightPlan.getIdentifier() );
        }
    }

    /**
     * TTP FlightCreationDateTime -> MatmFlight CreationTime
     */
    private void setCreationTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getTfdmFlightCreationTime() != null )
        {
            matmFlight.setCreationTime( nasFlight.getTfdmFlightCreationTime().getValue().toGregorianCalendar().getTime() );
        }
    }

    /**
     * TTP ActualInBlockTime -> MatmFlight ArrivalStandActualTime
     */
    private void setArrivalStandActualTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getArrival() != null && nasFlight.getArrival() instanceof NasArrivalType )
        {
            NasArrivalType arrival = (NasArrivalType)nasFlight.getArrival();
            if( arrival.getActualInBlockTime() != null &&
                    !arrival.getActualInBlockTime().isNil() )
            {
                matmFlight.setArrivalStandActualTime( arrival.getActualInBlockTime().getValue().getTime().getValue().toGregorianCalendar().getTime() );
            }
        }
    }

    /**
     * TTP ActualTakeOffTime -> MatmFlight DepartureRunwayActualTime
     */
    private void setDepartureRunwayActualTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            
            if( departure.getRunwayDepartureTime() != null &&
                    departure.getRunwayDepartureTime().getActual() != null &&
                    !departure.getRunwayDepartureTime().getActual().isEmpty() )
            {
                List<ReportedTimeType> actualRunwayDepTimes = departure.getRunwayDepartureTime().getActual();
                ReportedTimeType lastActualRunwayDepTime = actualRunwayDepTimes.get( actualRunwayDepTimes.size() - 1 );
                Date depRunActTime = lastActualRunwayDepTime.getTime().getValue().toGregorianCalendar().getTime();
                JAXBElement<Date> depRunActTimeJaxb = objFactory.createMatmFlightDepartureRunwayActualTime( depRunActTime );
                matmFlight.setDepartureRunwayActualTime( depRunActTimeJaxb );
            }
        }
    }

    /**
     * TTP EstimatedTimeOfDeparture -> MatmFlight DepartureRunwayTargetedTime
     */
    private void setDepartureRunwayTargetedTime3( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            if( departure.getRunwayDepartureTime() != null &&
                    departure.getRunwayDepartureTime().getEstimated() != null &&
                    !departure.getRunwayDepartureTime().getEstimated().isEmpty() )
            {
                List<NasRunwayDepartureEstimatedType> estimatedDepTimes = departure.getRunwayDepartureTime().getEstimated();
                NasRunwayDepartureEstimatedType lastEstimatedDepTime = estimatedDepTimes.get( estimatedDepTimes.size() - 1 );
                Date depRunwayEstTime = lastEstimatedDepTime.getTime().getValue().toGregorianCalendar().getTime();
                matmFlight.setDepartureRunwayTargetedTime(
                        objFactory.createMatmFlightDepartureRunwayTargetedTime( depRunwayEstTime ) );
            }
        }
    }

    /**
     * TTP TargetedTakeOffTime -> MatmFlight DepartureRunwayTargetedTime
     */
    private void setDepartureRunwayTargetedTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            if( departure.getRunwayDepartureTime() != null &&
                    departure.getRunwayDepartureTime().getTarget() != null )
            {
                if( !departure.getRunwayDepartureTime().getTarget().isNil() )
                {
                    Date depRunwayTargetTime = departure.getRunwayDepartureTime().getTarget().getValue().getValue().toGregorianCalendar().getTime();
                    matmFlight.setDepartureRunwayTargetedTime(
                            objFactory.createMatmFlightDepartureRunwayTargetedTime( depRunwayTargetTime ) );
                }
                else
                {
                    matmFlight.setDepartureRunwayTargetedTime( null );
                }
            }
        }
    }

    /**
     * TTP TargetedTakeOffTime -> MatmFlight DepartureRunwayTargetedTime
     */
    private void setDepartureRunwayTargetedTime2( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
                nasFlight.getAdditionalFlightInformation().getNameValue() != null &&
                !nasFlight.getAdditionalFlightInformation().getNameValue().isEmpty() )
        {
            for( NameValuePairType nameValuePair : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( TARGETED_TAKEOFF_TIME_KEY.equals( nameValuePair.getName() ) )
                {
                    Date depRunwayTargetedTime = null;
                    if( nameValuePair.getValue() != null &&
                            !nameValuePair.getValue().isEmpty() )
                    {
                        try
                        {
                            depRunwayTargetedTime = dateFormat.parse( nameValuePair.getValue() );
                        }
                        catch (ParseException e)
                        {
                            logger.warn( e.getLocalizedMessage(), e );
                        }
                    }

                    matmFlight.setDepartureRunwayTargetedTime(
                            objFactory.createMatmFlightDepartureRunwayTargetedTime( depRunwayTargetedTime ) );
                    
                    break;
                }
            }
        }
    }
    
    /**
     * TTP EarliestFeasibleTakeoffTime -> MatmFlight DepartureRunwayUndelayedTime
     */
    private void setDepartureRunwayUndelayedTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            if( departure.getRunwayDepartureTime() != null &&
                    departure.getRunwayDepartureTime().getEarliest() != null )
            {
                if( !departure.getRunwayDepartureTime().getEarliest().isNil() )
                {
                    Date depRunwayEarliestTime = departure.getRunwayDepartureTime().getEarliest().getValue().getTime().getValue().toGregorianCalendar().getTime();
                    matmFlight.setDepartureRunwayUndelayedTime( depRunwayEarliestTime );
                }
                else
                {
                    matmFlight.setDepartureRunwayUndelayedTime( null );
                }
            }
        }
    }

    /**
     * TTP ActualLandingTime -> MatmFlight ArrivalRunwayActualTime
     */
    private void setArrivalRunwayActualTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getArrival() != null && nasFlight.getArrival() instanceof NasArrivalType )
        {
            NasArrivalType arrival = (NasArrivalType)nasFlight.getArrival();
            if( arrival.getActualRunwayArrivalTime() != null &&
                    !arrival.getActualRunwayArrivalTime().isEmpty() )
            {
                List<ReportedTimeType> actualRunwayArrivalTimes = arrival.getActualRunwayArrivalTime();
                ReportedTimeType lastActualRunwayArrivalTime = actualRunwayArrivalTimes.get( actualRunwayArrivalTimes.size() - 1 );
                matmFlight.setArrivalRunwayActualTime( lastActualRunwayArrivalTime.getTime().getValue().toGregorianCalendar().getTime() );
            }
        }
    }

    /**
     * TTP EstimatedTimeOfArrival -> MatmFlight ArrivalRunwayTargetedTime
     */
    private void setArrivalRunwayTargetedTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDestination() != null && nasFlight.getDestination() instanceof NasDestinationType )
        {
            NasDestinationType destination = (NasDestinationType)nasFlight.getDestination();
            if( destination.getRunwayArrivalTime() != null &&
                    destination.getRunwayArrivalTime().getEstimated() != null &&
                    !destination.getRunwayArrivalTime().getEstimated().isEmpty() )
            {
                List<NasRunwayArrivalEstimatedType> estimatedRunwayArrivalTimes = destination.getRunwayArrivalTime().getEstimated();
                NasRunwayArrivalEstimatedType lastEstimatedRunwayArrivalTime = estimatedRunwayArrivalTimes.get( estimatedRunwayArrivalTimes.size() - 1 );
                matmFlight.setArrivalRunwayTargetedTime( lastEstimatedRunwayArrivalTime.getTime().getValue().toGregorianCalendar().getTime() );
            }
        }
    }

    /**
     * TTP ActualMovementAreaEntryTime -> MatmFlight DepartureMovementAreaActualTime
     */
    private void setDepartureMovementAreaActualTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            if( departure.getMovementAreaActualEntryTime() != null &&
                    departure.getMovementAreaActualEntryTime().getValue() != null )
            {
                Date departureMovementAreaActualTime = departure.getMovementAreaActualEntryTime().getValue().getTime().getValue().toGregorianCalendar().getTime();
                matmFlight.setDepartureMovementAreaActualTime( objFactory.createMatmFlightDepartureMovementAreaActualTime( departureMovementAreaActualTime ) );
            }
        }
    }

    /**
     * TTP TargetMovementAreaEntryTime -> MatmFlight DepartureMovementAreaSurfaceMeteredTime
     */
    private void setDepartureMovementAreaSurfaceMeteredTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            if( departure.getMovementAreaTargetEntryTime() != null &&
                    departure.getMovementAreaTargetEntryTime().getValue() != null )
            {
                if( !departure.getMovementAreaTargetEntryTime().isNil() )
                {
                    Date departureMovementAreaTargetTime = departure.getMovementAreaTargetEntryTime().getValue().getValue().toGregorianCalendar().getTime();
                    matmFlight.setDepartureMovementAreaSurfaceMeteredTime( objFactory.createMatmFlightDepartureMovementAreaSurfaceMeteredTime( departureMovementAreaTargetTime ) );
                }
                else
                {
                    matmFlight.setDepartureMovementAreaSurfaceMeteredTime( objFactory.createMatmFlightDepartureMovementAreaSurfaceMeteredTime( null ) );
                }
            }
        }
    }

    /**
     * TTP AerodromeDepartureFix -> MatmFlight DepartureFixSourceData
     */
    private void setDepartureFixSourceData( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            if( departure.getDepartureFix() != null )
            {
                if( !departure.getDepartureFix().isNil() )
                {
                    SignificantPointType departureFix = departure.getDepartureFix().getValue();
                    if( departureFix instanceof DesignatedPointOrNavaidType )
                    {
                        matmFlight.setDepartureFixSourceData( ((DesignatedPointOrNavaidType)departureFix).getDesignator() );
                    }
                }
                else
                {
                    matmFlight.setDepartureFixSourceData( null );
                }
            }
        }
    }

    /**
     * TTP AerodromeArrivalFix -> MatmFlight ArrivalFixSourceData
     */
    private void setArrivalFixSourceData( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDestination() != null && nasFlight.getDestination() instanceof NasDestinationType )
        {
            NasDestinationType destination = (NasDestinationType)nasFlight.getDestination();
            if( destination.getArrivalFix() != null )
            {
                if( !destination.getArrivalFix().isNil() )
                {
                    SignificantPointType arrivalFix = destination.getArrivalFix().getValue();
                    if( arrivalFix instanceof DesignatedPointOrNavaidType )
                    {
                        matmFlight.setArrivalFixSourceData( ((DesignatedPointOrNavaidType)arrivalFix).getDesignator() );
                    }
                }
                else
                {
                    matmFlight.setArrivalFixSourceData( null );
                }
            }
        }
    }

    /**
     * TTP Airline -> MatmFlight Carrier or FlightType
     */
    private void setCarrier( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
                nasFlight.getAdditionalFlightInformation().getNameValue() != null &&
                !nasFlight.getAdditionalFlightInformation().getNameValue().isEmpty() )
        {
            for( NameValuePairType nameValuePair : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( AIRLINE_KEY.equals( nameValuePair.getName() ) )
                {
                    if( GENERAL_AVIATION_CARRIER.equals( nameValuePair.getValue() ) )
                    {
                        matmFlight.setFlightType( FlightType.GENERAL_AVIATION );
                    }
                    else if( !UNKNOWN_CARRIER.equals( nameValuePair.getValue() ) )
                    {
                        matmFlight.setCarrier( nameValuePair.getValue() );
                    }
                    else
                    {
                        matmFlight.setCarrier( null );
                    }
                    break;
                }
            }
        }
    }

    /**
     * TTP DepartureStandDesignator -> MatmFlight DepartureStandSource
     */
    private void setDepartureStandSource( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            if( departure.getStandInformation() != null &&
                    !departure.getStandInformation().isNil() &&
                    departure.getStandInformation().getValue() != null &&
                    departure.getStandInformation().getValue().getSource() != null)
            {
                matmFlight.setDepartureStandSource( departure.getStandInformation().getValue().getSource().value() );
            }
        }
    }
    
    /**
     * TTP DepartureStand -> MatmFlight DepartureStandSourceData
     */
    private void setDepartureStandSourceData( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            if( departure.getStandInformation() != null &&
                    !departure.getStandInformation().isNil() )
            {
                matmFlight.setDepartureStandSourceData( departure.getStandInformation().getValue().getStandName() );
            }
        }
    }

    /**
     * TTP ArrivalStandDesignator -> MatmFlight ArrivalStandSource
     */
    private void setArrivalStandSource( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDestination() != null && nasFlight.getDestination() instanceof NasDestinationType )
        {
            NasDestinationType destination = (NasDestinationType)nasFlight.getDestination();
            if( destination.getStandInformation() != null &&
                    !destination.getStandInformation().isNil() &&
                    destination.getStandInformation().getValue() != null &&
                    destination.getStandInformation().getValue().getSource() != null )
            {
                matmFlight.setArrivalStandSource( destination.getStandInformation().getValue().getSource().value() );
            }
        }
    }

    /**
     * TTP ArrivalStand -> MatmFlight ArrivalStandSourceData
     */
    private void setArrivalStandSourceData( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDestination() != null && nasFlight.getDestination() instanceof NasDestinationType )
        {
            NasDestinationType destination = (NasDestinationType)nasFlight.getDestination();
            if( destination.getStandInformation() != null &&
                    !destination.getStandInformation().isNil() )
            {
                matmFlight.setArrivalStandSourceData( destination.getStandInformation().getValue().getStandName() );
            }
        }
    }

    /**
     * TTP ExpectedDeicingLocation -> MatmFlight (TfdTfdmExtension) IntendedDeiceLocation
     */
    private void setIntendedDeicingLocation( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            if( departure.getDeicing() != null )
            {
                boolean TfmTfdmExtensionExists = createTfmTfdmExtension( matmFlight );
                if( !TfmTfdmExtensionExists )
                {
                    return;
                }

                matmFlight.getExtensions().getTfmTfdmExtension().setIntendedDeiceLocation( departure.getDeicing().getDeicingLocation() );
            }
        }
    }

    /**
     * TTP ActualDepartureSpot -> MatmFlight DepartureSpotActual
     */
    private void setDepartureSpotActual( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
            nasFlight.getAdditionalFlightInformation().getNameValue() != null )
        {
            for( NameValuePairType entry : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( ACTUAL_DEPARTURE_SPOT_KEY.equals( entry.getName() ) )
                {
                    if( entry.getValue() == null ||
                            entry.getValue().isEmpty() )
                    {
                        matmFlight.setDepartureSpotActual( null );
                    }
                    else
                    {
                        // format of TTP: regionID/Timestamp/Source. Example: "SPOT_3/2018-02-02T18:43:44.770Z/TFDM"
                        // Just use the first part of the departureSpotActual value
                        String[] split = entry.getValue().split( "/" );
                        if( split.length > 0 )
                        {
                            matmFlight.setDepartureSpotActual( split[0] );
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * TTP ActualArrivalSpot -> MatmFlight ArrivalSpotActual
     */
    private void setArrivalSpotActual( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
            nasFlight.getAdditionalFlightInformation().getNameValue() != null )
        {
            for( NameValuePairType entry : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( ACTUAL_ARRIVAL_SPOT_KEY.equals( entry.getName() ) )
                {
                    if( entry.getValue() == null ||
                            entry.getValue().isEmpty() )
                    {
                        matmFlight.setArrivalSpotActual( null );
                    }
                    else
                    {
                        // format of TTP: regionID/Timestamp/Source. Example: "SPOT_3/2018-02-02T18:43:44.770Z/TFDM"
                        // Just use the first part of the arrivalSpotActual value
                        String[] split = entry.getValue().split( "/" );
                        if( split.length > 0 )
                        {
                            matmFlight.setArrivalSpotActual( split[0] );
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * TTP DepartureRunwayActual -> MatmFlight DepartureRunwayActual
     */
    private void setDepartureRunwayActual( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
            nasFlight.getAdditionalFlightInformation().getNameValue() != null )
        {
            for( NameValuePairType entry : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( DEPARTURE_RUNWAY_ACTUAL_KEY.equals( entry.getName() ) )
                {
                    if( entry.getValue() == null ||
                            entry.getValue().isEmpty() )
                    {
                        matmFlight.setDepartureRunwayActual( null );
                    }
                    else
                    {
                        // format of TTP: regionID/Timestamp/Source. Example: "28R/2018-02-02T18:43:44.770Z/TFDM"
                        // Just use the first part of the departureRunwayActual value
                        String[] split = entry.getValue().split( "/" );
                        if( split.length > 0 )
                        {
                            matmFlight.setDepartureRunwayActual( split[0] );
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * TTP ArrivalRunwayActual -> MatmFlight ArrivalRunwayActual
     */
    private void setArrivalRunwayActual( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
            nasFlight.getAdditionalFlightInformation().getNameValue() != null )
        {
            for( NameValuePairType entry : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( ARRIVAL_RUNWAY_ACTUAL_KEY.equals( entry.getName() ) )
                {
                    if( entry.getValue() == null ||
                            entry.getValue().isEmpty() )
                    {
                        matmFlight.setArrivalRunwayActual( null );
                    }
                    else
                    {
                        // format of TTP: regionID/Timestamp/Source. Example: "28R/2018-02-02T18:43:44.770Z/TFDM"
                        // Just use the first part of the arrivalRunwayActual value
                        String[] split = entry.getValue().split( "/" );
                        if( split.length > 0 )
                        {
                            matmFlight.setArrivalRunwayActual( split[0] );
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * TTP DiversionRecoveryStatus -> MatmFlight (TfmExtension) Diversion
     */
    private void setIsDiversion( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDiversionRecoveryIndicator() != null )
        {
            if( DiversionRecoveryIndicatorType.DIVERSION_RECOVERY.equals( nasFlight.getDiversionRecoveryIndicator() ) )
            {
                boolean TfmExtensionExists = createTfmExtension( matmFlight );
                if( !TfmExtensionExists )
                {
                    return;
                }
                
                matmFlight.getExtensions().getTfmExtension().setDiversion( true );
            }
        }
    }

    /**
     * TTP ApprovalRequestReleaseTime -> MatmFlight DepartureRunwayMeteredTime
     */
    private void setDepartureRunwayMeteredTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            if( departure.getApprovalRequestReleaseTime() != null )
            {
                if( !departure.getApprovalRequestReleaseTime().isNil() )
                {
                    Date releaseScheduledRollTime = departure.getApprovalRequestReleaseTime().getValue().getValue().toGregorianCalendar().getTime();
                    MeteredTime metered = new MeteredTime();
                    metered.setValue( releaseScheduledRollTime );
                    matmFlight.setDepartureRunwayMeteredTime( objFactory.createMatmFlightDepartureRunwayMeteredTime( metered ) );
                }
                else
                {
                    matmFlight.setDepartureRunwayMeteredTime( objFactory.createMatmFlightDepartureRunwayMeteredTime( null ));
                }
            }
        }
    }

    /**
     * TTP MajorCarrierIdentifier -> MatmFlight (TfdTfdmExtension) MajorCarrier
     */
    private void setMajorCarrier( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getFlightIdentification() != null &&
                nasFlight.getFlightIdentification() instanceof NasFlightIdentificationType )
        {
            NasFlightIdentificationType nasFlightId = (NasFlightIdentificationType)nasFlight.getFlightIdentification();
            String majorCarrier = nasFlightId.getMajorCarrierIdentifier();

            if( majorCarrier != null &&
                    !majorCarrier.isEmpty() )
            {
                boolean TfmTfdmExtensionExists = createTfmTfdmExtension( matmFlight );
                if( !TfmTfdmExtensionExists )
                {
                    return;
                }

                matmFlight.getExtensions().getTfmTfdmExtension().setMajorCarrier( nasFlightId.getMajorCarrierIdentifier() );
            }
        }
    }

    /**
     * TTP ActualMovementAreaExitTime -> MatmFlight ArrivalMovementAreaActualTime
     */
    private void setArrivalMovementAreaActualTime( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getArrival() != null && nasFlight.getArrival() instanceof NasArrivalType )
        {
            NasArrivalType arrival = (NasArrivalType)nasFlight.getArrival();
            if( arrival.getMovementAreaActualExitTime() != null &&
                    arrival.getMovementAreaActualExitTime().getValue() != null )
            {
                Date arrivalMovementAreaActualTime = arrival.getMovementAreaActualExitTime().getValue().getTime().getValue().toGregorianCalendar().getTime();
                matmFlight.setArrivalMovementAreaActualTime( arrivalMovementAreaActualTime );
            }
        }
    }

    /**
     * TTP PredictedDepartureSpot -> MatmFlight DepartureSpotSource
     */
    private void setDepartureSpotSource( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
            nasFlight.getAdditionalFlightInformation().getNameValue() != null )
        {
            for( NameValuePairType entry : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( PREDICTED_DEPARTURE_SPOT_KEY.equals( entry.getName() ) )
                {
                    if( entry.getValue() == null ||
                            entry.getValue().isEmpty() )
                    {
                        matmFlight.setDepartureSpotSource( objFactory.createMatmFlightDepartureSpotSource( null ) );
                    }
                    else
                    {
                        // Value format = region ID/timestamp/Source   (example: SPOT_3/2018-02-02T18:43:44.770Z/TFDM)
                        String[] split = entry.getValue().split( "/" );
                        matmFlight.setDepartureSpotSource( objFactory.createMatmFlightDepartureSpotSource( split[2] ) );
                    }
                    break;
                }
            }
        }
    }

    /**
     * TTP PredictedDepartureSpot -> MatmFlight DepartureSpotSourceData
     */
    private void setDepartureSpotSourceData( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
            nasFlight.getAdditionalFlightInformation().getNameValue() != null )
        {
            for( NameValuePairType entry : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( PREDICTED_DEPARTURE_SPOT_KEY.equals( entry.getName() ) )
                {
                    if( entry.getValue() == null ||
                            entry.getValue().isEmpty() )
                    {
                        matmFlight.setDepartureSpotSourceData( null );
                    }
                    else
                    {
                        // Value format = region ID/timestamp/Source   (example: SPOT_3/2018-02-02T18:43:44.770Z/TFDM)
                        String[] split = entry.getValue().split( "/" );
                        matmFlight.setDepartureSpotSourceData( objFactory.createMatmFlightDepartureSpotSourceData( split[0] ) );
                    }
                    break;
                }
            }
        }
    }

    /**
     * TTP PredictedArrivalSpotSource -> MatmFlight ArrivalSpotSource
     */
    private void setArrivalSpotSource( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
            nasFlight.getAdditionalFlightInformation().getNameValue() != null )
        {
            for( NameValuePairType entry : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( PREDICTED_ARRIVAL_SPOT_KEY.equals( entry.getName() ) )
                {
                    if( entry.getValue() == null ||
                            entry.getValue().isEmpty() )
                    {
                        matmFlight.setArrivalSpotSource( null );
                    }
                    else
                    {
                        // Value format = region ID/timestamp/Source   (example: SPOT_3/2018-02-02T18:43:44.770Z/TFDM)
                        String[] split = entry.getValue().split( "/" );
                        if( split.length > 0 )
                        {
                            matmFlight.setArrivalSpotSource( split[2] );
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * TTP PredictedArrivalSpot -> MatmFlight ArrivalSpotSourceData
     */
    private void setArrivalSpotSourceData( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
            nasFlight.getAdditionalFlightInformation().getNameValue() != null )
        {
            for( NameValuePairType entry : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( PREDICTED_ARRIVAL_SPOT_KEY.equals( entry.getName() ) )
                {
                    if( entry.getValue() == null ||
                            entry.getValue().isEmpty() )
                    {
                        matmFlight.setArrivalSpotSourceData( null );
                    }
                    else
                    {
                        // Value format = region ID/timestamp/Source   (example: SPOT_3/2018-02-02T18:43:44.770Z/TFDM)
                        String[] split = entry.getValue().split( "/" );
                        if( split.length > 0 )
                        {
                            matmFlight.setArrivalSpotSourceData( split[0] );
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * TTP DepartureRunwayPredicted -> MatmFlight DepartureRunwaySource
     */
    private void setDepartureRunwaySource( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
            nasFlight.getAdditionalFlightInformation().getNameValue() != null )
        {
            for( NameValuePairType entry : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( DEPARTURE_RUNWAY_PREDICTED_KEY.equals( entry.getName() ) )
                {
                    if( entry.getValue() == null ||
                            entry.getValue().isEmpty() )
                    {
                        matmFlight.setDepartureRunwaySource( objFactory.createMatmFlightDepartureRunwaySource( null ) );
                    }
                    else
                    {
                        // Value format = region ID/timestamp/Source   (example: 28R/2018-02-02T18:43:44.770Z/TFDM)
                        String[] split = entry.getValue().split( "/" );
                        if( split.length > 0 )
                        {
                            matmFlight.setDepartureRunwaySource( objFactory.createMatmFlightDepartureRunwaySource( split[2] ) );
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * TTP DepartureRunway -> MatmFlight DepartureRunwaySourceData
     */
    private void setDepartureRunwaySourceData( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
            nasFlight.getAdditionalFlightInformation().getNameValue() != null )
        {
            for( NameValuePairType entry : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( DEPARTURE_RUNWAY_PREDICTED_KEY.equals( entry.getName() ) )
                {
                    if( entry.getValue() == null ||
                            entry.getValue().isEmpty() )
                    {
                        matmFlight.setDepartureRunwaySourceData( objFactory.createMatmFlightDepartureRunwaySourceData( null ));
                    }
                    else
                    {
                        // Value format = region ID/timestamp/Source   (example: 28R/2018-02-02T18:43:44.770Z/TFDM)
                        String[] split = entry.getValue().split( "/" );
                        if( split.length > 0 )
                        {
                            matmFlight.setDepartureRunwaySourceData( objFactory.createMatmFlightDepartureRunwaySourceData( split[0] ) );
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * TTP ArrivalRunwayPredictedSource -> MatmFlight ArrivalRunwaySource
     */
    private void setArrivalRunwaySource( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
            nasFlight.getAdditionalFlightInformation().getNameValue() != null )
        {
            for( NameValuePairType entry : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( ARRIVAL_RUNWAY_PREDICTED_KEY.equals( entry.getName() ) )
                {
                    if( entry.getValue() == null ||
                            entry.getValue().isEmpty() )
                    {
                        matmFlight.setArrivalRunwaySource( null );
                    }
                    else
                    {
                        // Value format = region ID/timestamp/Source   (example: 28R/2018-02-02T18:43:44.770Z/TFDM)
                        String[] split = entry.getValue().split( "/" );
                        if( split.length > 0 )
                        {
                            matmFlight.setArrivalRunwaySource( split[2] );
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * TTP ArrivalRunwayPredicted -> MatmFlight ArrivalRunwaySourceData
     */
    private void setArrivalRunwaySourceData( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
            nasFlight.getAdditionalFlightInformation().getNameValue() != null )
        {
            for( NameValuePairType entry : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( ARRIVAL_RUNWAY_PREDICTED_KEY.equals( entry.getName() ) )
                {
                    if( entry.getValue() == null ||
                            entry.getValue().isEmpty() )
                    {
                        matmFlight.setArrivalRunwaySourceData( null );
                    }
                    else
                    {
                        // Value format = region ID/timestamp/Source   (example: 28R/2018-02-02T18:43:44.770Z/TFDM)
                        String[] split = entry.getValue().split( "/" );
                        if( split.length > 0 )
                        {
                            matmFlight.setArrivalRunwaySourceData( split[0] );
                        }
                    }
                    break;
                }
            }
        }
    }

    private void setRestrictionIds( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        // Departure
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();
            if( departure.getDepartureDelay() != null )
            {
                String tmiIdentifier = departure.getDepartureDelay().getTmiIdentifier();

                String[] tmis = tmiIdentifier.split( "," );
                for( int x = 0; x < tmis.length; x++ )
                {
                    String tmi = tmis[x];
                    tmi = tmi.trim();
                    tmi = tmi.substring( 0, tmi.indexOf( "/" ) );

                    // TODO Figure out what type of TMI this is, and add it to the corresponding list
                }

                // TODO In order to determine if the tmiIdentifier is an apreq_restiction,
                // or ground_stop_restriction, mit_restriction, etc. I have to cross reference
                // with the TTP Traffic Management messages but we're not parsing those yet.
                // So I'm leaving this TODO as a placeholder.

//                FlightRestrictions flightRestrictions = new FlightRestrictions();
//                flightRestrictions.setApreqRestrictionIds( ??? );
//                flightRestrictions.setGroundStopRestrictionIds( ??? );
//                flightRestrictions.setMitRestrictionIds( ??? );
//                matmFlight.setFlightRestrictions( objFactory.createMatmFlightFlightRestrictions( flightRestrictions ) );
            }
        }
    }

    private void setFlightState( MatmFlight matmFlight, NasFlightType nasFlight )
    {
        String tfdmIdCreator = getTfdmIdCreator( nasFlight );
        if( tfdmIdCreator == null )
        {
            return;
        }

        String departureAirport = getDepartureAerodrome( nasFlight );
        String arrivalAirport = getArrivalAerodrome( nasFlight );


        if( nasFlight.getFlightStatus() != null &&
                nasFlight.getFlightStatus().getTfdmFlightState() != null &&
                !nasFlight.getFlightStatus().getTfdmFlightState().isNil() )
        {
            TfdmFlightStateType ttpFlightState = nasFlight.getFlightStatus().getTfdmFlightState().getValue();

            SurfaceFlightState flightState = SurfaceFlightState.UNKNOWN;

            if( ttpFlightState.getValue() != null )
            {
                switch( ttpFlightState.getValue() )
                {
                    case AMA_HOLDING:
                        // No corresponding SurfaceFlightState
                        break;
                    case AMA_TAXI_IN:
                        flightState = SurfaceFlightState.TAXI_IN;
                        break;
                    case AMA_TAXI_OUT:
                        flightState = SurfaceFlightState.TAXI_OUT;
                        break;
                    case ARRIVAL:
                        // No corresponding SurfaceFlightState
                        break;
                    case AT_SPOT_OUT:
                        // No corresponding SurfaceFlightState
                        break;
                    case AT_STAND:
                        flightState = SurfaceFlightState.IN_GATE;
                        break;
                    case DEPARTED:
                        flightState = SurfaceFlightState.OFF;
                        break;
                    case EN_ROUTE:
                        flightState = SurfaceFlightState.ENROUTE;
                        break;
                    case FILED:
                        // No corresponding SurfaceFlightState
                        break;
                    case IN_DEPARTURE_QUEUE:
                        flightState = SurfaceFlightState.IN_QUEUE;
                        break;
                    case LUAW:
                        // No corresponding SurfaceFlightState
                        break;
                    case MISSED_APPROACH:
                        // No corresponding SurfaceFlightState
                        break;
                    case ON_FINAL:
                        flightState = SurfaceFlightState.ON_FINAL;
                        break;
                    case ON_RUNWAY:
                        // No corresponding SurfaceFlightState
                        break;
                    case RAMP_TAXI_IN:
                        flightState = SurfaceFlightState.RAMP_TAXI_IN;
                        break;
                    case RAMP_TAXI_OUT:
                        flightState = SurfaceFlightState.RAMP_TAXI_OUT;
                        break;
                    case SCHEDULED:
                        flightState = SurfaceFlightState.SCHEDULED;
                        break;
                    case STAND_METERING_HOLD:
                        // No corresponding SurfaceFlightState
                        break;
                    case STAND_RETURN:
                        flightState = SurfaceFlightState.RETURN_TO_GATE;
                        break;
                    case TAKEOFF_ROLL:
                        // No corresponding SurfaceFlightState
                        break;
                    case TAKEOFF_ROLL_ABORT:
                        // No corresponding SurfaceFlightState
                        break;
                    default:
                        break;
                }
            }

            if( tfdmIdCreator.equals( arrivalAirport ) )
            {
                matmFlight.setArrivalSurfaceFlightState( flightState );
            }
            else if( tfdmIdCreator.equals( departureAirport ) )
            {
                matmFlight.setDepartureSurfaceFlightState( flightState );
            }
        }
    }

    private void setGufi( MatmFlight matmFlight, TtpFlightDataWrapper ttpFlightDataWrapper )
    {
        matmFlight.setGufi( ttpFlightDataWrapper.getGufi() );
    }
    
    private void setTimestampSource( MatmFlight matmFlight, TtpFlightDataWrapper ttpFlightDataWrapper )
    {
        Long messageTime = ttpFlightDataWrapper.getMessageTime();
        if( messageTime != null && messageTime != 0 )
        {
            matmFlight.setTimestampSource( new Date( messageTime ) );
        }
    }
    
    private void setTimestampSourceReceived( MatmFlight matmFlight, TtpFlightDataWrapper ttpFlightDataWrapper )
    {
        matmFlight.setTimestampSourceReceived( ttpFlightDataWrapper.getTimestampSourceReceived() );
    }
    
    private void setTimestampSourceProcessed( MatmFlight matmFlight, TtpFlightDataWrapper ttpFlightDataWrapper )
    {
        matmFlight.setTimestampSourceProcessed( ttpFlightDataWrapper.getTimestampSourceProcessed() );
    }

    private String getTfdmIdCreator( NasFlightType nasFlight )
    {
        if( nasFlight.getAdditionalFlightInformation() != null &&
            nasFlight.getAdditionalFlightInformation().getNameValue() != null )
        {
            for( NameValuePairType entry : nasFlight.getAdditionalFlightInformation().getNameValue() )
            {
                if( TFDM_ID_CREATOR_KEY.equals( entry.getName() ) )
                {
                    if( entry.getValue() != null &&
                            !entry.getValue().isEmpty() )
                    {
                        return entry.getValue();
                    }
                }
            }
        }

        return null;
    }

    private String getArrivalAerodrome( NasFlightType nasFlight )
    {
        if( nasFlight.getDestination() != null && nasFlight.getDestination() instanceof NasDestinationType )
        {
            NasDestinationType destination = (NasDestinationType)nasFlight.getDestination();

            return destination.getDestinationPointText();
        }

        return null;
    }

    private String getDepartureAerodrome( NasFlightType nasFlight )
    {
        if( nasFlight.getDeparture() != null && nasFlight.getDeparture() instanceof NasDepartureType )
        {
            NasDepartureType departure = (NasDepartureType)nasFlight.getDeparture();

            return departure.getDeparturePointText();
        }

        return null;
    }


    private boolean createTfmTfdmExtension( MatmFlight matmFlight )
    {
        if( matmFlight.getExtensions() == null )
        {
            matmFlight.setExtensions( new MatmFlightExtensions() );
        }
        
        if( matmFlight.getExtensions().getTfmTfdmExtension() == null )
        {
            matmFlight.getExtensions().setTfmTfdmExtension( new TfmTfdmExtension() );
        }

        return matmFlight.getExtensions().getTfmTfdmExtension() != null;
    }

    private boolean createTfmExtension( MatmFlight matmFlight )
    {
        if( matmFlight.getExtensions() == null )
        {
            matmFlight.setExtensions( new MatmFlightExtensions() );
        }
        
        if( matmFlight.getExtensions().getTfmExtension() == null )
        {
            matmFlight.getExtensions().setTfmExtension( new TfmExtension() );
        }
        
        return matmFlight.getExtensions().getTfmExtension() != null;
    }

    public AirportCodeTranslator getAirportCodeTranslator()
    {
        return airportCodeTranslator;
    }

    public void setAirportCodeTranslator( AirportCodeTranslator airportCodeTranslator )
    {
        this.airportCodeTranslator = airportCodeTranslator;
    }
}
