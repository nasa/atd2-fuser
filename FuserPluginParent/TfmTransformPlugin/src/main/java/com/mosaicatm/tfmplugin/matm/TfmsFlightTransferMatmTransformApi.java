package com.mosaicatm.tfmplugin.matm;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.transform.api.FlightSourceTransformApiImpl;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.tfm.thick.flight.mtfms.data.TfmsFlightTransfer;

public class TfmsFlightTransferMatmTransformApi
		extends FlightSourceTransformApiImpl<TfmsFlightTransfer>
{
    private static Logger logger = LoggerFactory.getLogger( TfmsFlightTransferMatmTransformApi.class );

	public TfmsFlightTransferMatmTransformApi( boolean includeFixTraversalData, 
            boolean includeWaypointTraversalData, boolean includeCenterTraversalData, boolean includeSectorTraversalData )
	{
		GenericMarshaller genericMarshaller = null;
		try
		{
			genericMarshaller = new GenericMarshaller( TfmsFlightTransfer.class );
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
		
		TfmsFlightTransferToMatmTransform tfmFlightToMatm = new TfmsFlightTransferToMatmTransform();
		tfmFlightToMatm.setIncludeFixTraversalData( includeFixTraversalData );
        tfmFlightToMatm.setIncludeWaypointTraversalData( includeWaypointTraversalData );
        tfmFlightToMatm.setIncludeCenterTraversalData( includeCenterTraversalData );
        tfmFlightToMatm.setIncludeSectorTraversalData( includeSectorTraversalData );
        
		this.setToMatmTransform( tfmFlightToMatm );
	}
}
