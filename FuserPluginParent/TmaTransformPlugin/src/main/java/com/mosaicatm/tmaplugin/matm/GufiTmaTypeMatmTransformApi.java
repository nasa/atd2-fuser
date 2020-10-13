package com.mosaicatm.tmaplugin.matm;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.transform.api.FlightSourceTransformApiImpl;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.tma.common.message.GufiTmaType;

public class GufiTmaTypeMatmTransformApi extends FlightSourceTransformApiImpl<GufiTmaType>
{
    private static Logger logger = LoggerFactory.getLogger( GufiTmaTypeMatmTransformApi.class );

	public GufiTmaTypeMatmTransformApi()
	{
		GenericMarshaller genericMarshaller = null;
		try
		{
			genericMarshaller = new GenericMarshaller( GufiTmaType.class );
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
		this.setToMatmTransform( new GufiTmaTypeToMatmTransform() );
	}
}
