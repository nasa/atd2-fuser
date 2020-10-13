package com.mosaicatm.asdexplugin.matm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.asdex.transfer.PositionMessage;
import com.mosaicatm.asdex.transfer.PositionMessageList;
import com.mosaicatm.fuser.transform.api.FlightSourceTransformApiImpl;
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class PositionMessageListMatmTransformApi
		extends FlightSourceTransformApiImpl<PositionMessageList>
{
	private final Log log = LogFactory.getLog(getClass());
	
	private Transformer<MatmFlight, PositionMessage> singleToMatmTransform;
	
	public PositionMessageListMatmTransformApi()
	{
		GenericMarshaller genericMarshaller = null;
		try
		{
			genericMarshaller = new GenericMarshaller( PositionMessageList.class );
			genericMarshaller.setMarshallFormatted( false );
			genericMarshaller.setMarshallHeader( false );
			genericMarshaller.setLogValidationErrors( true );
			genericMarshaller.init();
		}
		catch( JAXBException jaxbEx )
		{
			log.error( "Failed to setup GenericMarshaller", jaxbEx );
		}
		
		this.setMarshaller( genericMarshaller );
		this.setSingleToMatmTransform( new PositionMessageToMatmTransform() );
	}
	
	@Override
	public List<MatmFlight> toMatm (PositionMessageList batch)
	{
		List<MatmFlight> matm = new ArrayList<>();
		
		List<PositionMessage> positionMessages = fromBatch( batch );
		
		for( PositionMessage positionMessage : positionMessages )
		{
			MatmFlight matmFlight = singleToMatmTransform.transform( positionMessage );
			matm.add( matmFlight );
		}
		
		return matm;
	}

	public List<PositionMessage> fromBatch( PositionMessageList batch )
	{
		List<PositionMessage> rtnBatch = null;
		
		if (batch != null)
		{			
			rtnBatch = batch.getElements();
			
			if (log.isDebugEnabled())
				log.debug ("Position message batch size: " + rtnBatch.size());
		}
		
		return rtnBatch;
	}
	
	public Transformer<MatmFlight, PositionMessage> getSingleToMatmTransform()
	{
		return singleToMatmTransform;
	}

	public void setSingleToMatmTransform( Transformer<MatmFlight, PositionMessage> singleToMatmTransform )
	{
		this.singleToMatmTransform = singleToMatmTransform;
	}
}
