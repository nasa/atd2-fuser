package com.mosaicatm.ttpplugin.matm;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aero.faa.nas._4.NasMessageType;

import com.mosaicatm.fuser.transform.api.FlightSourceTransformApiImpl;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.ttp.util.TtpFlightDataWrapper;

public class TtpFlightDataWrapperMatmTransformApi
		extends FlightSourceTransformApiImpl<TtpFlightDataWrapper>
{
    private static Logger logger = LoggerFactory.getLogger( TtpFlightDataWrapperMatmTransformApi.class );

	public TtpFlightDataWrapperMatmTransformApi()
	{
		GenericMarshaller genericMarshaller = null;
		try
		{
			genericMarshaller = new GenericMarshaller(
			        false,
			        TtpFlightDataWrapper.class,
			        NasMessageType.class,
			        aero.fixm.messaging._4.ObjectFactory.class,
			        aero.fixm.flight._4.ObjectFactory.class );
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
		this.setToMatmTransform( new TtpToMatmTransform() );
	}
}
