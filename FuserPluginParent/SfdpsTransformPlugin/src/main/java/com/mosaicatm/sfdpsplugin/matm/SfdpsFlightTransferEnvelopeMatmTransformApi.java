package com.mosaicatm.sfdpsplugin.matm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.transform.api.FlightSourceTransformApiImpl;
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.sfdps.data.transfer.SfdpsFlightTransfer;
import com.mosaicatm.sfdps.data.transfer.SfdpsFlightTransferEnvelope;

public class SfdpsFlightTransferEnvelopeMatmTransformApi 
    extends FlightSourceTransformApiImpl <SfdpsFlightTransferEnvelope>
{
    private final Log log = LogFactory.getLog(getClass());

    private Transformer<MatmFlight, SfdpsFlightTransfer> singleToMatmTransform;

    public SfdpsFlightTransferEnvelopeMatmTransformApi()
    {
        GenericMarshaller genericMarshaller = null;
        try
        {
            genericMarshaller = new GenericMarshaller( SfdpsFlightTransferEnvelope.class );
            genericMarshaller.setMarshallFormatted( false );
            genericMarshaller.setMarshallHeader( false );
            genericMarshaller.setLogValidationErrors( true );
            genericMarshaller.init();
        }
        catch (JAXBException e)
        {
            log.error( "Failed to setup GenericMarshaller", e );
        }
        
        setMarshaller( genericMarshaller );
        setSingleToMatmTransform( new SfdpsFlightTransferToMatmTransform() );
    }

    @Override
    public List<MatmFlight> toMatm (SfdpsFlightTransferEnvelope batch)
    {
        List<MatmFlight> matm = new ArrayList<>();
        
        List<SfdpsFlightTransfer> sfdpsFlightTransfers = fromBatch( batch );
        
        for( SfdpsFlightTransfer sfdpsFlightTransfer : sfdpsFlightTransfers )
        {
            MatmFlight matmFlight = singleToMatmTransform.transform( sfdpsFlightTransfer );
            matm.add( matmFlight );
        }
        
        return matm;
    }

    public List<SfdpsFlightTransfer> fromBatch(SfdpsFlightTransferEnvelope envelope) 
    {
        List<SfdpsFlightTransfer> rtnBatch = null;
        
        if (envelope != null)
        {            
            rtnBatch = envelope.getSfdpsFlightTransferData();
            
            if (log.isDebugEnabled())
                log.debug ("Sfdps batch size: " + rtnBatch.size());
        }
        
        return rtnBatch;
    }

    public Transformer<MatmFlight, SfdpsFlightTransfer> getSingleToMatmTransform()
    {
        return singleToMatmTransform;
    }

    public void setSingleToMatmTransform( Transformer<MatmFlight, SfdpsFlightTransfer> singleToMatmTransform )
    {
        this.singleToMatmTransform = singleToMatmTransform;
    }

}
