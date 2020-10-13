package com.mosaicatm.sfdpsplugin.matm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.common.ObjectFactory;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.SfdpsExtension;
import com.mosaicatm.matmdata.flight.extension.SfdpsMessageTypeType;
import com.mosaicatm.sfdps.data.transfer.SfdpsFlightTransfer;


/**
 * @author    $Author$
 * @version $Revision$
 */
public class SfdpsFlightTransferToMatmTransform
    implements Transformer <MatmFlight, SfdpsFlightTransfer>
{
    private final Log log = LogFactory.getLog( getClass() );

    private ObjectFactory objectFactory;
    
    public SfdpsFlightTransferToMatmTransform()
    {
        objectFactory = new ObjectFactory();
    }
    
    @Override
    public MatmFlight transform(SfdpsFlightTransfer transfer)
    {
        if (transfer == null)
            return null;

        MatmFlight flight = new MatmFlight();

        flight.setLastUpdateSource(FuserSource.SFDPS.value());
        
        if( transfer.getMessageType() != null )
            flight.setSystemId( FuserSource.SFDPS.value() + "-" + transfer.getMessageType());
        else
            flight.setSystemId( FuserSource.SFDPS.value() );      
        
        flight.setTimestamp( transfer.getTimestamp() );
        flight.setTimestampSource(transfer.getTimestamp());
        flight.setTimestampSourceReceived(transfer.getReceiptTime());
        flight.setTimestampSourceProcessed( transfer.getProcessedTime() );

        flight.setAcid(transfer.getAcid());
        flight.setGufi(transfer.getGufi());

        flight.setCpdlcDclAvailable( transfer.getCpdlcDclAvailable() );

        if( transfer.getDepartureAirportIata() != null || transfer.getDepartureAirportIcao() != null )
        {
            flight.setDepartureAerodrome(createAerodrome(
                    transfer.getDepartureAirportIata(), transfer.getDepartureAirportIcao() ) );
        }
        if( transfer.getArrivalAirportIata() != null || transfer.getArrivalAirportIcao() != null )
        {
            flight.setArrivalAerodrome(createAerodrome(
                    transfer.getArrivalAirportIata(), transfer.getArrivalAirportIcao() ) );
        }

        transformRouteData( transfer, flight );
        transformPositionData( transfer, flight );

        SfdpsExtension sfdpsExtension = new SfdpsExtension();
        MatmFlightExtensions extensions = new MatmFlightExtensions();
        extensions.setSfdpsExtension( sfdpsExtension );
        flight.setExtensions( extensions );

        sfdpsExtension.setFdpsGufi( transfer.getFdpsGufi() );
        sfdpsExtension.setCommCode( transfer.getCommCode() );
        sfdpsExtension.setDatalinkCode( transfer.getDatalinkCode() );
        
        if( transfer.getMessageType() != null )
        {
            sfdpsExtension.setMessageType( SfdpsMessageTypeType.valueOf( transfer.getMessageType() ));
        }
        sfdpsExtension.setOtherDatalinkCapabilities( transfer.getOtherDatalinkCapabilities() );
        sfdpsExtension.setLastSfdpsPosition( flight.getPosition() );
        
        return flight;
    }

    public Aerodrome createAerodrome(String airportIata, String airportIcao )
    {
        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setIataName( airportIata );
        aerodrome.setIcaoName( airportIcao );

        return aerodrome;
    }

    private void transformRouteData(SfdpsFlightTransfer transfer, MatmFlight matm)
    {
        matm.setRouteTextFiled( transfer.getNasRouteField10a() );
        
        // Note: It might actually be a decent idea to null out the local intended 
        // route when a 10a is provided but the 10b is NULL. This is an indication
        // that the most recent local intended route is stale. However, it would
        // require more analysis -- possibly an updater.
        matm.setRouteTextLocalIntended( transfer.getLocalIntendedRouteField10b() );
        
        /* This seemed like a good idea, but I think we may need to compare the 
           ARTCC sending the message relative the the flight's position and state.
           Apparently different ARTCCs can send different 10b's
        
        String fuserRoute = transfer.getLocalIntendedRouteField10b();
        if( fuserRoute == null )
        {
            fuserRoute = transfer.getNasRouteField10a();
        }        
        
        if( fuserRoute != null )
        {
            matm.setRouteText( fuserRoute );
            matm.setFiledFlight( true );
        }
        */
    }

    private void transformPositionData(SfdpsFlightTransfer transfer, MatmFlight matm)
    {
        Position position = null;
        if (transfer.getLatitudeDegrees() != null && transfer.getLongitudeDegrees() != null)
        {
            position = new Position();
            position.setLatitude(transfer.getLatitudeDegrees().doubleValue());
            position.setLongitude(transfer.getLongitudeDegrees().doubleValue());

            if (transfer.getPositionTimestamp() != null)
                position.setTimestamp(transfer.getPositionTimestamp());
            else if( transfer.getTimestamp() != null )
                position.setTimestamp( transfer.getTimestamp() );

            if (transfer.getAltitudeFeet() != null)
            {
                position.setAltitude(transfer.getAltitudeFeet().doubleValue());
            }

            if (transfer.getSpeedKnots() != null)
            {
                position.setSpeed(transfer.getSpeedKnots().doubleValue());
            }
            
            if(( transfer.getControllingSector() != null ) || transfer.isControllingSectorNil() )
            {
                position.setAtcSector( objectFactory.createPositionAtcSector( transfer.getControllingSector() ));
            }

            position.setSource("SFDPS");
            matm.setPosition(position);
        }
    }
}


