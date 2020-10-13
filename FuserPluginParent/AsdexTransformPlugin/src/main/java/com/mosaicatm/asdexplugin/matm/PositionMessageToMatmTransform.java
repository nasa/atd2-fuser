package com.mosaicatm.asdexplugin.matm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.aptcode.AirportCodeException;
import com.mosaicatm.aptcode.AirportCodeTranslator;
import com.mosaicatm.aptcode.AirportCodeTranslatorFactory;
import com.mosaicatm.aptcode.AirportCodeUtil;
import com.mosaicatm.aptcode.data.AirportCodeEntry;
import com.mosaicatm.asdex.transfer.PositionMessage;
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.AsdexExtension;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;

public class PositionMessageToMatmTransform
implements Transformer <MatmFlight, PositionMessage>
{    
    public static final String SEND_TO_NOT_SENSITIVE = "all";
    public static final String SEND_TO_SENSITIVE = "authorized";
    
    private AirportCodeTranslator airportCodeTranslator;
    private final Log log = LogFactory.getLog(getClass());
    
    public PositionMessageToMatmTransform()
    {
        AirportCodeTranslatorFactory factory = new AirportCodeTranslatorFactory();
        factory.setPreFetchAll(true);
        factory.setPreFetchIcaoCodes(true);
        factory.setPreFetchIataCodes(true);
        factory.setPreFetchFaaLidCodes(true);

        try
        {
            airportCodeTranslator = factory.create();
        }
        catch (AirportCodeException ace)
        {
            log.error("Failed to create the AirportCodeTranslator", ace);
        }
    }

    @Override
    public MatmFlight transform(PositionMessage track)
    {
        if (track == null)
            return null;
        
        MatmFlight matm = new MatmFlight();
        AsdexExtension extension = new AsdexExtension();
        matm.setLastUpdateSource("ASDEX");
        MatmFlightExtensions extensions = new MatmFlightExtensions();
        extensions.setAsdexExtension(extension);
        matm.setExtensions(extensions);

        matm.setTimestampSource(track.getTime());
        matm.setTimestampSourceReceived(track.getTimestampSourceReceive());
        matm.setTimestampSourceProcessed(track.getTimestampSourceProcess());
        
        String sendTo = track.getSendTo();
        if( sendTo != null )
        {
            extension.setSendTo( sendTo );
            
            if( sendTo.equals( SEND_TO_NOT_SENSITIVE ))
            {
                matm.setSensitiveDataExternal( false );
            }
            else if( sendTo.equals( SEND_TO_SENSITIVE ))
            {
                matm.setSensitiveDataExternal( true );
            }
            else
            {
                log.error( "Unhandled sendTo type: " + sendTo );
            }
        }
        
        if (track.getAcid() != null)
            matm.setAcid(track.getAcid());
        
        if (track.getAircraftType() != null)
            matm.setAircraftType(track.getAircraftType());
        
        if (track.getArrivalAirport() != null)
        {
            matm.setArrivalAerodrome(getAerodrome(track.getArrivalAirport()));
        }
        
        if (track.getDepartureAirport() != null)
        {
            matm.setDepartureAerodrome(getAerodrome(track.getDepartureAirport()));
        }
        
        if (track.getTargetType() != null)
        {
            matm.setTargetType(track.getTargetType());
        }
        
        Position position = matm.getPosition();
        if(position == null)
        {
            position = new Position();
        }
        
        if (track.getAltitude() != null)
            position.setAltitude(track.getAltitude().doubleValue());
        
        if (track.getHeading() != null)
            position.setHeading(track.getHeading());
        
        if (track.getSpeed() != null)
            position.setSpeed(track.getSpeed().doubleValue());
        
        if (track.getTime() != null)
        {
            matm.setTimestamp(track.getTime());
            position.setTimestamp(track.getTime());
        }
        
        if (track.getLatitude() != null)
            position.setLatitude(track.getLatitude());
        
        if (track.getLongitude() != null)
            position.setLongitude(track.getLongitude());
        
        
        position.setSource("ASDEX");
        matm.setPosition(position);
        extension.setLastAsdexPosition(position);
    
        
        if (track.getGufi() != null)
            matm.setGufi(track.getGufi());
        
        if (track.getBeaconCode() != null)
            matm.setBeaconCode(String.valueOf(track.getBeaconCode()));
        
        
        if (track.getModeSAddress() != null)
            matm.setAircraftAddress(String.valueOf(track.getModeSAddress()));
        
        
        
//        if (track.getLastUpdateTime() != null)
//            surv.setTimestamp(new Date(track.getLastUpdateTime()));
//        
//        if (track.getVelocityX() != null)
//            surv.setTrackVelocityXValue(track.getVelocityX());
//        
//        if (track.getVelocityY() != null)
//            surv.setTrackVelocityYValue(track.getVelocityY());
//        
//        if (track.isOnGround() != null)
//            surv.setOnGround(track.isOnGround());
//        
//        surv.setFull(track.isFull());
        extension.setTrackId(Integer.valueOf(track.getTrackNumber()));
        
        if (track.getAirport() != null)
        {
            extension.setAsdexAirport(getAerodrome(track.getAirport()));
            
            //setting surface airport
            matm.setSurfaceAirport(getAerodrome(track.getAirport()));
        }
        
        
        /*    
            track.getTimeProcessed();
            track.getAsdexId();
            track.isFiltered();
        */
        
        return matm;
    }

    private Aerodrome getAerodrome( String airport )
    {
        Aerodrome aerodrome = new Aerodrome();

        if( airport != null )
        {
            String iata = null;
            String icao = null;

            if (airportCodeTranslator != null)
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
                log.error("AirportCodeTranslator is null!!!");
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

    public void setAirportCodeTranslator(
        AirportCodeTranslator airportCodeTranslator)
    {
        this.airportCodeTranslator = airportCodeTranslator;
    }

}
