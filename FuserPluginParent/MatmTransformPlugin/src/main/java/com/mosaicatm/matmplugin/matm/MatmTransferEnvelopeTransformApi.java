package com.mosaicatm.matmplugin.matm;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.transform.api.FlightSourceTransformApiImpl;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmTransferEnvelopeTransformApi extends FlightSourceTransformApiImpl<MatmTransferEnvelope>
{
	private final Log log = LogFactory.getLog( getClass() );
	
	public MatmTransferEnvelopeTransformApi()
	{
		GenericMarshaller genericMarshaller = null;
		try
		{
			genericMarshaller = new GenericMarshaller( MatmTransferEnvelope.class );
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
	}
	
	@Override
	public List<MatmFlight> toMatm( MatmTransferEnvelope envelope )
	{
		List<MatmFlight> rtnBatch = null;
		
		if (envelope != null)
		{			
			rtnBatch = envelope.getFlights();
			
			if (log.isDebugEnabled())
				log.debug ("Matm batch size: " + rtnBatch.size());
		}
		
		return rtnBatch;
	}
}
