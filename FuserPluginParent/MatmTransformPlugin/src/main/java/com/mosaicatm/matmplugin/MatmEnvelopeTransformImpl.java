package com.mosaicatm.matmplugin;

import org.pf4j.Extension;

import com.mosaicatm.fuser.common.camel.FuserAmqProps;
import com.mosaicatm.fuser.transform.api.AbstractFuserFlightTransformPluginApi;
import com.mosaicatm.fuser.transform.api.FuserPluginProps;
import com.mosaicatm.fuser.transform.api.SourceTransformApi;
import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmplugin.matm.MatmTransferEnvelopeTransformApi;

@Extension
public class MatmEnvelopeTransformImpl 
extends AbstractFuserFlightTransformPluginApi<MatmTransferEnvelope>
{
    private String sourceStart = "matm.source.envelope";
    
	@Override
	public String getSourceStart()
	{
		return sourceStart;
	}

	@Override
	public String getSourceType()
	{
		return "MatmEnvelope";
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
	    
	    String property = getProperty( "fuser.topic.matm.source.envelope" );
	    
	    if (isValidProperty(property))
	        sourceStart = property; 
	}

	@Override
	public SourceTransformApi<MatmTransferEnvelope,MatmFlight> getSourceTransformApi()
	{
		return new MatmTransferEnvelopeTransformApi();
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