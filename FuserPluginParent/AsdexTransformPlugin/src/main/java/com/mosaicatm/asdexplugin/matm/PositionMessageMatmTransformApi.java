package com.mosaicatm.asdexplugin.matm;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.asdex.transfer.PositionMessage;
import com.mosaicatm.fuser.transform.api.FlightSourceTransformApiImpl;
import com.mosaicatm.lib.jaxb.GenericMarshaller;

public class PositionMessageMatmTransformApi extends FlightSourceTransformApiImpl<PositionMessage>
{
    private static Logger logger = LoggerFactory.getLogger( PositionMessageMatmTransformApi.class );

	public PositionMessageMatmTransformApi()
	{
		GenericMarshaller genericMarshaller = null;
		try
		{
			genericMarshaller = new GenericMarshaller( PositionMessage.class );
			genericMarshaller.setMarshallFormatted( false );
			genericMarshaller.setMarshallHeader( false );
			genericMarshaller.setLogValidationErrors( true );
            genericMarshaller.init();
		}
		catch( JAXBException jaxbEx )
		{
			logger.error( "Failed to setup GenericMarshaller", jaxbEx );
		}
		
		this.setMarshaller( genericMarshaller );
		this.setToMatmTransform( new PositionMessageToMatmTransform() );
	}
}
