package com.mosaicatm.asdexplugin.matm;

import com.mosaicatm.asdex.transfer.PositionMessage;
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.AsdexExtension;

public class MatmToPositionMessageTransform
implements Transformer <PositionMessage, MatmFlight>
{    
    @Override
    public PositionMessage transform(MatmFlight matm)
    {
        if (matm == null)
            return null;
        
        PositionMessage track = new PositionMessage();
        
        if (matm.getAcid() != null)
            track.setAcid(matm.getAcid());
        
        if (matm.getAircraftType() != null)
            track.setAircraftType(matm.getAircraftType());
        
        if (matm.getSurfaceAirport() != null)
            track.setAirport(matm.getSurfaceAirport().getIataName());
        
        if (matm.getArrivalAerodrome() != null && matm.getArrivalAerodrome().getIataName() != null)
            track.setArrivalAirport(matm.getArrivalAerodrome().getIataName());
        
        if (matm.getDepartureAerodrome() != null && matm.getDepartureAerodrome().getIataName() != null)
            track.setDepartureAirport(matm.getDepartureAerodrome().getIataName());
        
        if (matm.getPosition() != null)
        {
            if(matm.getPosition().getAltitude() != null)
            {
                track.setAltitude(matm.getPosition().getAltitude());
            }
            if(matm.getPosition().getHeading() != null)
            {
                track.setHeading(matm.getPosition().getHeading());
            }
            if (matm.getPosition().getSpeed() != null)
                track.setSpeed(matm.getPosition().getSpeed().intValue());
            
            if (matm.getPosition().getLatitude() != null)
                track.setLatitude(matm.getPosition().getLatitude() );
            
            if (matm.getPosition().getLongitude() != null)
                track.setLongitude(matm.getPosition().getLongitude());
            
            if (matm.getPosition().getTimestamp() != null)
                track.setLastUpdateTime(matm.getPosition().getTimestamp().getTime());
        }
        
        if (matm.getBeaconCode() != null)
            track.setBeaconCode(Integer.valueOf(matm.getBeaconCode()));
        
        if (matm.getGufi() != null)
            track.setGufi(matm.getGufi());
        
        if (matm.getAircraftAddress() != null)
            track.setModeSAddress(Long.valueOf(matm.getAircraftAddress()));
        
        if (matm.getTimestamp() != null)
            track.setTime(matm.getTimestamp());
        
        if ( matm.isSensitiveDataExternal() != null )
        {
            if( matm.isSensitiveDataExternal() )
            {
                track.setSendTo( PositionMessageToMatmTransform.SEND_TO_SENSITIVE );
            }
            else
            {
                track.setSendTo( PositionMessageToMatmTransform.SEND_TO_NOT_SENSITIVE );
            }
        }
        
        if(matm.getExtensions() != null && matm.getExtensions().getAsdexExtension() != null)
        {
            AsdexExtension extension =  matm.getExtensions().getAsdexExtension();
            if(extension.getTrackId() != null)
            {
                track.setTrackNumber(extension.getTrackId().shortValue());
            }
        }
        
        
        return track;
    }

}
