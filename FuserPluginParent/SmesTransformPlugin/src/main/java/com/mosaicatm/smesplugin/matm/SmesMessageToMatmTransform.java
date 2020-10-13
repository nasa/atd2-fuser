package com.mosaicatm.smesplugin.matm;

import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import com.mosaicatm.matmdata.flight.extension.SmesExtension;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.smes.transfer.SmesEventType;
import com.mosaicatm.smes.transfer.SmesMessageTransfer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SmesMessageToMatmTransform implements Transformer<MatmFlight, SmesMessageTransfer> {

    private final Log log = LogFactory.getLog(getClass());

    private ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public MatmFlight transform(SmesMessageTransfer msg) {
        if (msg == null) {
            return null;
        }

        MatmFlight matm = new MatmFlight();
        matm.setLastUpdateSource(FuserSource.SMES.value());
        matm.setTimestampSource(msg.getEventTime());
        matm.setTimestampSourceReceived(msg.getTimestampSourceReceived());
        matm.setTimestampSourceProcessed(msg.getTimestampSourceProcessed());

        SmesExtension extension = new SmesExtension();
        MatmFlightExtensions extensions = new MatmFlightExtensions();
        extensions.setSmesExtension(extension);
        matm.setExtensions(extensions);

        if (msg.getCallSign() != null) {
            matm.setAcid(msg.getCallSign());
        }
        if (msg.getAircraftAddress() != null) {
            matm.setAircraftAddress(msg.getAircraftAddress().toString());
        }
        if (msg.getMode3ACode() != null) {
            matm.setBeaconCode(msg.getMode3ACode());
        }

        // Arrival
        if (SmesEventType.ON.equalsIgnoreCase(msg.getEventType())) {
            matm.setArrivalRunwayActualTime(msg.getEventTime());
            extension.setOnTime(msg.getEventTime());
        }
        if (msg.getDestinationAirport() != null) {
            Aerodrome arrivalAerodrome = new Aerodrome();
            // use ICAO or FAA lid
            if (msg.getDestinationAirport().length() > 3) {
                arrivalAerodrome.setIcaoName(msg.getDestinationAirport());
            } else {
                arrivalAerodrome.setFaaLid(msg.getDestinationAirport());
            }
            matm.setArrivalAerodrome(arrivalAerodrome);
        }

        // Departure
        if (SmesEventType.OFF.equalsIgnoreCase(msg.getEventType())) {
            matm.setDepartureRunwayActualTime(
                    objectFactory.createMatmFlightDepartureRunwayActualTime(msg.getEventTime()));
            extension.setOffTime(msg.getEventTime());
        }
        if (msg.getDepartureAirport() != null) {
            Aerodrome departureAerodrome = new Aerodrome();
            // use ICAO of FAA lid
            if (msg.getDepartureAirport().length() > 3) {
                departureAerodrome.setIcaoName(msg.getDepartureAirport());
            } else {
                departureAerodrome.setFaaLid(msg.getDepartureAirport());
            }
            matm.setDepartureAerodrome(departureAerodrome);
        }

        if (SmesEventType.SPOTOUT.equalsIgnoreCase(msg.getEventType())) {
            matm.setDepartureMovementAreaActualTime(
                    objectFactory.createMatmFlightDepartureMovementAreaActualTime(msg.getEventTime()));
            extension.setSpotOutTime(msg.getEventTime());
        }
        if (SmesEventType.SPOTIN.equalsIgnoreCase(msg.getEventType())) {
            matm.setArrivalMovementAreaActualTime(msg.getEventTime());
            extension.setSpotInTime(msg.getEventTime());
        }

        Position position = matm.getPosition();
        if (position == null) {
            position = new Position();
        }
        if (msg.getAltitude() != null) {
            position.setAltitude(msg.getAltitude());
        }
        if (msg.getEventTime() != null) {
            matm.setTimestamp(msg.getEventTime());
            position.setTimestamp(msg.getEventTime());
        }
        if (msg.getLatitudeDegrees() != null) {
            position.setLatitude(msg.getLatitudeDegrees());
        }
        if (msg.getLongitudeDegrees() != null) {
            position.setLongitude(msg.getLongitudeDegrees());
        }
        position.setSource(FuserSource.SMES.value());
        matm.setPosition(position);
        extension.setLastSmesPosition(position);

        if (msg.getGufi() != null) {
            matm.setGufi(msg.getGufi());
        }

        if (msg.getSurfaceTrackIdentifier() != null) {
            extension.setStid(msg.getSurfaceTrackIdentifier());
        }

        if (msg.getAirport() != null) {
            //setting surface airport
            Aerodrome surfaceAirport = new Aerodrome();
            // use ICAO or FAA lid
            if (msg.getAirport().length() > 3) {
                surfaceAirport.setIcaoName(msg.getAirport());
            } else {
                surfaceAirport.setFaaLid(msg.getAirport());
            }
            matm.setSurfaceAirport(surfaceAirport);
            extension.setAirport(msg.getAirport());
        }

        return matm;
    }
}
