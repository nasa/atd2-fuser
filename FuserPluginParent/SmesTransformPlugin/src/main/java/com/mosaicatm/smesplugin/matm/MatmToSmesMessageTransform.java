package com.mosaicatm.smesplugin.matm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.SmesExtension;
import com.mosaicatm.smes.transfer.SmesEventType;
import com.mosaicatm.smes.transfer.SmesMessageTransfer;

public class MatmToSmesMessageTransform implements Transformer<SmesMessageTransfer, MatmFlight> {

    private final Log log = LogFactory.getLog(getClass());
    
    @Override
    public SmesMessageTransfer transform(MatmFlight matm) {
        if (matm == null) {
            return null;
        }

        SmesMessageTransfer msg = new SmesMessageTransfer();

        if (matm.getAircraftAddress() != null) {
            try {
                msg.setAircraftAddress(Long.decode(matm.getAircraftAddress()));
            } 
            catch (NumberFormatException e) {
                log.error("Failed to decode aircraft address " + matm.getAircraftAddress());
            }
        }
        if (matm.getSurfaceAirport() != null) {
            if (matm.getSurfaceAirport().getIcaoName() != null) {
                msg.setAirport(matm.getSurfaceAirport().getIcaoName());
            } else if (matm.getSurfaceAirport().getFaaLid() != null) {
                msg.setAirport(matm.getSurfaceAirport().getFaaLid());
            }
        }

        if (matm.getArrivalAerodrome() != null) {
            if (matm.getArrivalAerodrome().getIcaoName() != null) {
                msg.setAirport(matm.getArrivalAerodrome().getIcaoName());
            } else if (matm.getArrivalAerodrome().getFaaLid() != null) {
                msg.setAirport(matm.getArrivalAerodrome().getFaaLid());
            }
        }

        if (matm.getDepartureAerodrome() != null) {
            if (matm.getDepartureAerodrome().getIcaoName() != null) {
                msg.setDepartureAirport(matm.getDepartureAerodrome().getIcaoName());
            } else if (matm.getDepartureAerodrome().getFaaLid() != null) {
                msg.setDepartureAirport(matm.getDepartureAerodrome().getFaaLid());
            }
        }

        if (matm.getPosition() != null) {
            if (matm.getPosition().getAltitude() != null) {
                msg.setAltitude(matm.getPosition().getAltitude());
            }
            if (matm.getPosition().getLatitude() != null) {
                msg.setLatitudeDegrees(matm.getPosition().getLatitude());
            }
            if (matm.getPosition().getLongitude() != null) {
                msg.setLongitudeDegrees(matm.getPosition().getLongitude());
            }
        }

        if (matm.getBeaconCode() != null) {
            msg.setMode3ACode(matm.getBeaconCode());
        }

        if (matm.getGufi() != null) {
            msg.setGufi(matm.getGufi());
        }

        if (matm.getAcid() != null) {
            msg.setCallSign(matm.getAcid());
        }

        if (matm.getTimestamp() != null) {
            msg.setEventTime(matm.getTimestamp());
        }

        if (matm.getExtensions() != null && matm.getExtensions().getSmesExtension() != null) {
            SmesExtension extension = matm.getExtensions().getSmesExtension();
            if (extension.getStid() != null) {
                msg.setSurfaceTrackIdentifier(extension.getStid());
            }
            if (extension.getOnTime() != null) {
                msg.setEventType(SmesEventType.ON);
                msg.setEventTime(extension.getOnTime());
            }
            if (extension.getOffTime() != null) {
                msg.setEventType(SmesEventType.OFF);
                msg.setEventTime(extension.getOffTime());
            }
            if (extension.getSpotInTime() != null) {
                msg.setEventType(SmesEventType.SPOTIN);
                msg.setEventTime(extension.getSpotInTime());
            }
            if (extension.getSpotOutTime() != null) {
                msg.setEventType(SmesEventType.SPOTOUT);
                msg.setEventTime(extension.getSpotOutTime());
            }
            if (extension.getAirport() != null) {
                msg.setAirport(extension.getAirport());
            }
        }

        return msg;
    }

}
