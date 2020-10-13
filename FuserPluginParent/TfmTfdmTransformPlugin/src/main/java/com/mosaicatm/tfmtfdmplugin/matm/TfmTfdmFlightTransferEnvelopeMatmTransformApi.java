package com.mosaicatm.tfmtfdmplugin.matm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.transform.api.FlightSourceTransformApiImpl;
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.flight.MatmFlight;

import gov.nasa.atm.tfm.tfdm.transfer.TfmTfdmFlightTransfer;
import gov.nasa.atm.tfm.tfdm.transfer.TfmTfdmFlightTransferEnvelope;

public class TfmTfdmFlightTransferEnvelopeMatmTransformApi 
		extends FlightSourceTransformApiImpl <TfmTfdmFlightTransferEnvelope>
{
	private final Log log = LogFactory.getLog( getClass() );

	private Transformer<MatmFlight, TfmTfdmFlightTransfer> singleToMatmTransform;

	public TfmTfdmFlightTransferEnvelopeMatmTransformApi()
	{
		GenericMarshaller genericMarshaller = null;
		try
		{
			genericMarshaller = new GenericMarshaller( TfmTfdmFlightTransferEnvelope.class );
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
		setSingleToMatmTransform( new TfmTfdmFlightTransferToMatmTransform() );
	}

    @Override
    public List<MatmFlight> toMatm (TfmTfdmFlightTransferEnvelope batch)
    {
        List<MatmFlight> matm = new ArrayList<>();
        
        List<TfmTfdmFlightTransfer> tfmTfdmFlightTransfers = fromBatch( batch );
        
        for( TfmTfdmFlightTransfer tfmTfdmFlightTransfer : tfmTfdmFlightTransfers)
        {
        	MatmFlight matmFlight = singleToMatmTransform.transform( tfmTfdmFlightTransfer );
        	matm.add( matmFlight );
        }
        
        return matm;
    }

    public List<TfmTfdmFlightTransfer> fromBatch(TfmTfdmFlightTransferEnvelope envelope) 
    {
        List<TfmTfdmFlightTransfer> rtnBatch = null;
        
        if (envelope != null)
        {            
            rtnBatch = envelope.getTfmTfdmFlightTransferList();
            
            if (log.isDebugEnabled())
                log.debug ("TfmTfdm batch size: " + rtnBatch.size());
        }
        
        return rtnBatch;
    }

	public Transformer<MatmFlight, TfmTfdmFlightTransfer> getSingleToMatmTransform()
	{
		return singleToMatmTransform;
	}

	public void setSingleToMatmTransform( Transformer<MatmFlight, TfmTfdmFlightTransfer> singleToMatmTransform )
	{
		this.singleToMatmTransform = singleToMatmTransform;
	}
}
