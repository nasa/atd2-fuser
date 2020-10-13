package com.mosaicatm.surveillanceplugin.matm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.transform.api.FlightSourceTransformApiImpl;
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.fusersurveillance.FuserSurveillanceData;
import com.mosaicatm.matmdata.fusersurveillance.FuserSurveillanceMessage;
import com.mosaicatm.matmdata.fusersurveillance.FuserSurveillanceTransferEnvelope;

public class FuserSurveillanceEnvelopeMatmTransformApi extends FlightSourceTransformApiImpl <FuserSurveillanceTransferEnvelope>
{	
	private final Log log = LogFactory.getLog(getClass());

	private Transformer<MatmFlight, FuserSurveillanceDataExtension> singleToMatmTransform;

	public FuserSurveillanceEnvelopeMatmTransformApi()
	{
		GenericMarshaller genericMarshaller = null;
		try
		{
			genericMarshaller = new GenericMarshaller( FuserSurveillanceTransferEnvelope.class );
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
		setSingleToMatmTransform( new FuserSurveillanceDataToMatmTransform() );
	}

	@Override
	public List<MatmFlight> toMatm( FuserSurveillanceTransferEnvelope envelope )
	{
		List<MatmFlight> matm = new ArrayList<>();
		
		List<FuserSurveillanceDataExtension> fuserSurveillances = fromBatch( envelope );
		
		for( FuserSurveillanceDataExtension fuserSurveillanceDataExtension : fuserSurveillances )
		{
			MatmFlight matmFlight = singleToMatmTransform.transform( fuserSurveillanceDataExtension );
			matm.add( matmFlight );
		}
		
		return matm;
	}

	public List<FuserSurveillanceDataExtension> fromBatch(FuserSurveillanceTransferEnvelope envelope) 
	{
		List<FuserSurveillanceDataExtension> rtnBatch = new ArrayList<FuserSurveillanceDataExtension>();

		if (envelope != null)
		{			
			for(FuserSurveillanceMessage message : envelope.getFuserSurveillanceMessage()){
				for(FuserSurveillanceData data : message.getSurveillanceData()){
					FuserSurveillanceDataExtension extension =  new FuserSurveillanceDataExtension(data);
					extension.setSourceTimestamp(message.getSurveillanceSourceTimestamp());
					extension.setSource(message.getSurveillanceSource());
					extension.setAerodromeIataName(message.getSurveillanceSourceAerodromeIataName());
					rtnBatch.add(extension) ;
				}
			}


			if (log.isDebugEnabled())
				log.debug ("Fuser surveillance message batch size: " + rtnBatch.size());
		}

		return rtnBatch;
	}

	public Transformer<MatmFlight, FuserSurveillanceDataExtension> getSingleToMatmTransform()
	{
		return singleToMatmTransform;
	}

	public void setSingleToMatmTransform( Transformer<MatmFlight, FuserSurveillanceDataExtension> singleToMatmTransform )
	{
		this.singleToMatmTransform = singleToMatmTransform;
	}

}
