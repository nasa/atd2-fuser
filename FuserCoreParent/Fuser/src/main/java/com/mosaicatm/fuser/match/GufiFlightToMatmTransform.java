package com.mosaicatm.fuser.match;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.aptcode.AirportCodeException;
import com.mosaicatm.aptcode.AirportCodeTranslator;
import com.mosaicatm.aptcode.data.AirportCodeEntry;
import com.mosaicatm.gufiservice.data.GlobalGufiFlight;
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.AsdexExtension;
import com.mosaicatm.matmdata.flight.extension.Cat11Extension;
import com.mosaicatm.matmdata.flight.extension.Cat62Extension;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;

public class GufiFlightToMatmTransform
implements Transformer<MatmFlight, GlobalGufiFlight>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private AirportCodeTranslator airportCodeTranslator;
    
    public MatmFlight transform(GlobalGufiFlight gufiFlight)
    {
        MatmFlight matm = new MatmFlight();
        matm.setGufi(gufiFlight.getGufi());
        matm.setLastUpdateSource(gufiFlight.getLastMessageType());
        matm.setSystemId( gufiFlight.getLastMessageSubType() );
        
        matm.setTimestamp(gufiFlight.getLastMessageTime());

        matm.setAcid(gufiFlight.getAcid());
        matm.setAircraftType(gufiFlight.getAircraftType());
        
        matm.setDepartureAerodrome(getAerodrome(gufiFlight.getDepartureAirport()));
        matm.setArrivalAerodrome(getAerodrome(gufiFlight.getDestinationAirport()));
        matm.setSurfaceAirport( getAerodrome(gufiFlight.getAsdexAirport()));
        
        // not sure if these are mapped correctly since the latest gufi times
        // could come from one of many matm times
        matm.setDepartureRunwayEstimatedTime(gufiFlight.getLatestDepartureTime());
        matm.setArrivalRunwayEstimatedTime(gufiFlight.getLatestArrivalTime());
        
        if (gufiFlight.getModeSAddress() != null)
            matm.setAircraftAddress(gufiFlight.getModeSAddress().toString());
        
        matm.setAircraftRegistration(gufiFlight.getAircraftRegistration());
                

        transformPosition(matm, gufiFlight);
        transformExtensions(matm, gufiFlight);
        
        return matm;
    }
    
    private void transformPosition(MatmFlight matm, GlobalGufiFlight gufiFlight)
    {
        Position position = new Position ();
        position.setLatitude(gufiFlight.getLatestLatitude());
        position.setLongitude(gufiFlight.getLatestLongitude());
        position.setAltitude(gufiFlight.getLatestAltitude());
        position.setTimestamp(gufiFlight.getLatestPositionUpdateTime());
        
        matm.setPosition(position);
    }
    
    private void transformExtensions(MatmFlight matm, GlobalGufiFlight gufiFlight)
    {
        MatmFlightExtensions ext = new MatmFlightExtensions ();
        
        AsdexExtension asdex = new AsdexExtension ();
        
        Cat11Extension cat11 = new Cat11Extension ();
        Cat62Extension cat62 = new Cat62Extension ();
        
        if (gufiFlight.getUniqueIdAsdex() != null)
        {
            asdex.setTrackId(gufiFlight.getUniqueIdAsdex().intValue());
            cat11.setTrackId(gufiFlight.getUniqueIdAsdex().intValue());
            cat62.setTrackId(gufiFlight.getUniqueIdAsdex().intValue());
        }
                
        ext.setAsdexExtension(asdex);
        ext.setCat11Extension(cat11);
        ext.setCat62Extension(cat62);
        
        matm.setExtensions(ext);
    }
    
    private Aerodrome getAerodrome(String airport)
    {
        if (airport != null && !airport.trim().isEmpty())
        {
            //GufiService works with IATA, only need to convert to ICAO
            Aerodrome aerodrome = new Aerodrome ();
            aerodrome.setIataName( airport );
            aerodrome.setIcaoName(airportIcaoCode( airport));
            
            return aerodrome;
        }
        
        return null;
    }
    
    private String airportIataCode(String airport)
    {
        String iata = null;
        
        try
        {
            if (airportCodeTranslator != null)
            {
                iata = airportCodeTranslator.toIata(airport);
            }
            
            if( iata == null )
            {
                iata = airport;
            }
        }
        catch (AirportCodeException ace)
        {
            log.error("Error deriving iata airport code for " + airport, ace);
        }
        
        return iata;
    }
    
    private String airportIcaoCode(String airport)
    {
        String icao = null;
        
        try
        {
            if (airportCodeTranslator != null)
            {
                AirportCodeEntry code = airportCodeTranslator.getBestMatchAirportCodeFromAirlineSource(airport);
                if( code != null )
                {
                    icao = code.getIcao();
                }
            }
        }
        catch (AirportCodeException ace)
        {
            log.error("Error deriving iata airport code for " + airport, ace);
        }
        
        return icao;
    }    
    
    public void setAirportCodeTranslator (AirportCodeTranslator airportCodeTranslator)
    {
        this.airportCodeTranslator = airportCodeTranslator;
    }
}
