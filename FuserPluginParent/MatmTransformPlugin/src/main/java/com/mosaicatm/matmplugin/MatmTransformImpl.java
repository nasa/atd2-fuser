package com.mosaicatm.matmplugin;

import javax.xml.bind.JAXBException;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.common.camel.FuserAmqProps;
import com.mosaicatm.fuser.transform.api.AbstractFuserFlightTransformPluginApi;
import com.mosaicatm.fuser.transform.api.FuserPluginProps;
import com.mosaicatm.fuser.transform.api.FlightSourceTransformApiImpl;
import com.mosaicatm.fuser.transform.api.SourceTransformApi;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.util.MatmFlightNamespacePrefixMapper;
import com.mosaicatm.matmplugin.matm.MatmToMatmTransform;

@Extension
public class MatmTransformImpl
extends AbstractFuserFlightTransformPluginApi<MatmFlight>
{
    private static Logger logger = LoggerFactory.getLogger( MatmTransformImpl.class );

    private String sourceStart = "matm.source";
    
	@Override
	public String getSourceStart()
	{
		return sourceStart;
	}

	@Override
	public String getSourceType()
	{
		return "Matm";
	}

	@Override
    public String getComponentName()
    {
        return "matm.jms";
    }
	
	@Override
	public void initialize( FuserPluginProps props )
	{
	    super.initialize(props);
	    
	    String property = getProperty( "fuser.topic.matm.source" ); 
	    
	    if (isValidProperty(property))
	        sourceStart = property; 
	}

	@Override
	public SourceTransformApi<MatmFlight,MatmFlight> getSourceTransformApi()
	{
		GenericMarshaller genericMarshaller = null;
		try
		{
			genericMarshaller = new GenericMarshaller( MatmFlight.class );
			genericMarshaller.setMarshallFormatted( false );
			genericMarshaller.setMarshallHeader( false );
			genericMarshaller.setNamespacePrefixMapper( new MatmFlightNamespacePrefixMapper() );
            genericMarshaller.setLogValidationErrors( true );
    	    genericMarshaller.init();
		}
		catch (JAXBException e)
		{
		    logger.error( "Failed to setup GenericMarshaller", e );
		}
		
		FlightSourceTransformApiImpl<MatmFlight> transformApiImpl =
				new FlightSourceTransformApiImpl<MatmFlight>();
		
		transformApiImpl.setMarshaller( genericMarshaller );
		transformApiImpl.setToMatmTransform( new MatmToMatmTransform() );
		
		return transformApiImpl;
	}

	@Override
    public FuserAmqProps getFuserAmqProps()
    {
        FuserAmqProps props = new FuserAmqProps();
        props.setUrl( getProperty( "fuser.matm.jms.url", getDefaultUrl() ) );
        props.setUser( getProperty( "fuser.matm.jms.user", getDefaultUser() ) );
        props.setPassword( getProperty( "fuser.matm.jms.password", getDefaultPassword() ) );
        
        return props;
    }
}
