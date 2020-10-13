package com.mosaicatm.sfdpsplugin.matm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.sfdps.data.transfer.SfdpsFlightTransfer;

/**
 * @author $Author$
 * @version $Revision$
 */
public class MatmToSfdpsFlightTransferTransform    implements Transformer<SfdpsFlightTransfer, MatmFlight>
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public SfdpsFlightTransfer transform(MatmFlight matm)
    {
        if (matm == null)
            return null;

        SfdpsFlightTransfer transfer = new SfdpsFlightTransfer();

        if (matm.getTimestampSource() != null)
            transfer.setTimestamp(matm.getTimestampSource());
        
        if (matm.getTimestampSourceReceived() != null)
            transfer.setReceiptTime(matm.getTimestampSourceReceived());

        if (matm.getAcid() != null)
            transfer.setAcid(matm.getAcid());
        
        if (matm.getGufi() != null)
            transfer.setGufi(matm.getGufi());

        if (matm.getCenterList() != null && !matm.getCenterList().isEmpty())
            transfer.setCenter(matm.getCenterList().get(0));

        if (matm.getDepartureAerodrome() != null)
        {
            transfer.setDepartureAirportIata( matm.getDepartureAerodrome().getIataName() );
            transfer.setDepartureAirportIcao( matm.getDepartureAerodrome().getIcaoName() );
        }

        if (matm.getDepartureRunwayActualTime() != null)
            transfer.setDepartureRunwayActualTime( matm.getDepartureRunwayActualTime().getValue() );

        if (matm.getDepartureRunwayEstimatedTime() != null)
            transfer.setDepartureRunwayEstimatedTime(matm.getDepartureRunwayEstimatedTime());

        if (matm.getArrivalAerodrome() != null)
        {
            transfer.setArrivalAirportIata( matm.getArrivalAerodrome().getIataName() );
            transfer.setArrivalAirportIata( matm.getArrivalAerodrome().getIcaoName() );
        }

        if (matm.getArrivalRunwayActualTime() != null)
            transfer.setArrivalRunwayActualTime(matm.getArrivalRunwayActualTime());

        if (matm.getArrivalRunwayEstimatedTime() != null)
            transfer.setArrivalRunwayEstimatedTime(matm.getArrivalRunwayEstimatedTime());

        if (matm.getAircraftRegistration() != null)
            transfer.setAircraftRegistration(matm.getAircraftRegistration());

        if(( matm.getSystemId() != null ) && matm.getSystemId().startsWith( FuserSource.SFDPS.value() + "-" ) && matm.getSystemId().length() > 7 )
           transfer.setMessageType( matm.getSystemId().substring( 6, matm.getSystemId().length() ));
        
        transformAircraftAddress(matm, transfer);

        transformRouteData(matm, transfer);
        transformPositionData(matm, transfer);

        return transfer;
    }

    private void transformAircraftAddress(MatmFlight matm, SfdpsFlightTransfer transfer)
    {
        if (matm.getAircraftAddress() != null)
        {
            try
            {
                transfer.setAircraftAddress(Long.valueOf(matm.getAircraftAddress()));
            }
            catch (NumberFormatException e)
            {
                log.warn(e.getMessage(), e);
                log.warn("Unable to parse aircraft address {} for {}/{}",
                        matm.getAircraftAddress(), matm.getAcid(),
                        matm.getGufi());
            }
        }
    }

    private void transformRouteData(MatmFlight matm, SfdpsFlightTransfer transfer)
    {
        transfer.setNasRouteField10a( matm.getRouteTextFiled() );
        transfer.setLocalIntendedRouteField10b( matm.getRouteTextLocalIntended() );
    }

    private void transformPositionData(MatmFlight matm,    SfdpsFlightTransfer transfer)
    {
        Position matmPosition = matm.getPosition();
        if (matmPosition != null)
        {
            if (matmPosition.getLatitude() != null)
                transfer.setLatitudeDegrees(matmPosition.getLatitude().floatValue());

            if (matmPosition.getLongitude() != null)
                transfer.setLongitudeDegrees(matmPosition.getLongitude().floatValue());

            if (matmPosition.getAltitude() != null)
                transfer.setAltitudeFeet(matmPosition.getAltitude().floatValue());

            if (matmPosition.getSpeed() != null)
                transfer.setSpeedKnots(matmPosition.getSpeed().floatValue());

            if (matmPosition.getTimestamp() != null)
                transfer.setPositionTimestamp(matmPosition.getTimestamp());
            
            if (matmPosition.getAtcSector() != null)
                transfer.setControllingSector(matmPosition.getAtcSector().getValue());
        }
    }
}
