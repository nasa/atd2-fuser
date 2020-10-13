package com.mosaicatm.tmaplugin.matm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.transform.api.FlightSourceTransformApiImpl;
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.tma.common.message.GufiTmaType;
import com.mosaicatm.tma.common.message.GufiTmaTypeEnvelope;

public class GufiTmaTypeEnvelopeMatmTransformApi
		extends FlightSourceTransformApiImpl <GufiTmaTypeEnvelope>
{
	private final Logger log = LoggerFactory.getLogger( getClass() );

	private Transformer<MatmFlight, GufiTmaType> singleToMatmTransform;

	public GufiTmaTypeEnvelopeMatmTransformApi()
	{
		GenericMarshaller genericMarshaller = null;
		try
		{
			genericMarshaller = new GenericMarshaller( GufiTmaTypeEnvelope.class );
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
		setSingleToMatmTransform( new GufiTmaTypeToMatmTransform() );
	}

    @Override
    public List<MatmFlight> toMatm (GufiTmaTypeEnvelope batch)
    {
        List<MatmFlight> matm = new ArrayList<>();

        List<GufiTmaType> gufiTmaTypes = fromBatch( batch );

        for( GufiTmaType gufiTma : gufiTmaTypes )
        {
            MatmFlight matmFlight = singleToMatmTransform.transform( gufiTma );
            matm.add( matmFlight );
        }

        return matm;
    }

	public List<GufiTmaType> fromBatch(GufiTmaTypeEnvelope envelope)
	{
		List<GufiTmaType> rtnBatch = null;
		
		if (envelope != null)
		{			
			rtnBatch = envelope.getGufiTmaTypeList();
			
			if (log.isDebugEnabled())
				log.debug ("Tma batch size: " + rtnBatch.size());
		}
		
		return rtnBatch;
	}

	public Transformer<MatmFlight, GufiTmaType> getSingleToMatmTransform()
	{
		return singleToMatmTransform;
	}

	public void setSingleToMatmTransform( Transformer<MatmFlight, GufiTmaType> singleToMatmTransform )
	{
		this.singleToMatmTransform = singleToMatmTransform;
	}

}
