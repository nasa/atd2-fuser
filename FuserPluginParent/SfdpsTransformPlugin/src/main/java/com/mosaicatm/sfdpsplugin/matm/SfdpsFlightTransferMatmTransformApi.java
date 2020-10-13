package com.mosaicatm.sfdpsplugin.matm;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.transform.api.FlightSourceTransformApiImpl;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.sfdps.data.transfer.SfdpsFlightTransfer;

public class SfdpsFlightTransferMatmTransformApi extends FlightSourceTransformApiImpl<SfdpsFlightTransfer>
{
    private static Logger logger = LoggerFactory.getLogger( SfdpsFlightTransferMatmTransformApi.class );

    public SfdpsFlightTransferMatmTransformApi()
    {
        GenericMarshaller genericMarshaller = null;
        try
        {
            genericMarshaller = new GenericMarshaller( SfdpsFlightTransfer.class );
            genericMarshaller.setMarshallFormatted( false );
            genericMarshaller.setMarshallHeader( false );
            genericMarshaller.setLogValidationErrors( true );
            genericMarshaller.init();
        }
        catch (JAXBException e)
        {
            logger.error( "Failed to setup GenericMarshaller", e );
        }
        
        this.setMarshaller( genericMarshaller );
        this.setToMatmTransform( new SfdpsFlightTransferToMatmTransform() );
    }
}
