package com.mosaicatm.tfmplugin.matm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.transform.api.FlightSourceTransformApiImpl;
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.tfm.thick.flight.mtfms.data.TfmsFlightTransfer;
import com.mosaicatm.tfm.thick.flight.mtfms.data.TfmsFlightTransferEnvelope;

public class TfmsFlightTransferEnvelopeBatchApi 
	extends FlightSourceTransformApiImpl<TfmsFlightTransferEnvelope>
{
	private final Log log = LogFactory.getLog(getClass());

	private Transformer <MatmFlight, TfmsFlightTransfer> singleToMatmTransform;
	
	public TfmsFlightTransferEnvelopeBatchApi( boolean includeFixTraversalData, 
            boolean includeWaypointTraversalData, boolean includeCenterTraversalData, boolean includeSectorTraversalData )
	{
		GenericMarshaller genericMarshaller = null;
		try
		{
			genericMarshaller = new GenericMarshaller( TfmsFlightTransferEnvelope.class );
			genericMarshaller.setMarshallFormatted( false );
			genericMarshaller.setMarshallHeader( false );
            genericMarshaller.setLogValidationErrors( true );
    	    genericMarshaller.init();
		}
		catch (JAXBException e)
		{
			log.error( "Failed to setup GenericMarshaller", e );
		}
		
		this.setMarshaller( genericMarshaller );
		
		TfmsFlightTransferToMatmTransform tfmFlightToMatm = new TfmsFlightTransferToMatmTransform();
		tfmFlightToMatm.setIncludeFixTraversalData( includeFixTraversalData );
        tfmFlightToMatm.setIncludeWaypointTraversalData( includeWaypointTraversalData );
        tfmFlightToMatm.setIncludeCenterTraversalData( includeCenterTraversalData );
        tfmFlightToMatm.setIncludeSectorTraversalData( includeSectorTraversalData );
        
		this.setSingleToMatmTransform( tfmFlightToMatm );
	}

	public List<TfmsFlightTransfer> fromBatch(TfmsFlightTransferEnvelope envelope) 
	{
		List<TfmsFlightTransfer> rtnBatch = null;
		
		if (envelope != null)
		{			
			rtnBatch = envelope.getTfmsFlightTransferData();
			
			if (log.isDebugEnabled())
				log.debug ("Tfms batch size: " + rtnBatch.size());
		}
		
		return rtnBatch;
	}

	
    @Override
    public List<MatmFlight> toMatm( TfmsFlightTransferEnvelope flight )
    {
        List<MatmFlight> matm = new ArrayList<>();
        
        List<TfmsFlightTransfer> tfmFlightTransfers = fromBatch( flight ); 
        
        for( TfmsFlightTransfer tfmFlight : tfmFlightTransfers )
        {
        	MatmFlight matmFlight = singleToMatmTransform.transform( tfmFlight );
        	matm.add( matmFlight );
        }
        
        return matm;
    }

	public Transformer<MatmFlight, TfmsFlightTransfer> getSingleToMatmTransform()
	{
		return singleToMatmTransform;
	}

	public void setSingleToMatmTransform( Transformer<MatmFlight, TfmsFlightTransfer> singleToMatmTransform )
	{
		this.singleToMatmTransform = singleToMatmTransform;
	}

}
