package com.mosaicatm.surveillanceplugin;

import org.pf4j.Extension;

import com.mosaicatm.fuser.common.camel.FuserAmqProps;
import com.mosaicatm.fuser.transform.api.AbstractFuserFlightTransformPluginApi;
import com.mosaicatm.fuser.transform.api.FuserPluginProps;
import com.mosaicatm.fuser.transform.api.SourceTransformApi;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.fusersurveillance.FuserSurveillanceTransferEnvelope;
import com.mosaicatm.surveillanceplugin.matm.FuserSurveillanceEnvelopeMatmTransformApi;

@Extension
public class SurveillanceTransformImpl
extends AbstractFuserFlightTransformPluginApi<FuserSurveillanceTransferEnvelope>
{
    private String sourceStart = "flightHub.SurveillanceMessage";

	@Override
	public String getSourceStart()
	{
		return sourceStart;
	}

	@Override
	public String getSourceType()
	{
		return "Surveillance";
	}
	
	@Override
    public String getComponentName()
    {
        return "surveillance.jms";
    }

	@Override
	public void initialize( FuserPluginProps props )
	{
	    super.initialize(props);
	    
	    String property = getProperty( "fuser.topic.surveillance.fuserSurveillanceTransferEnvelope.source" );
	    
	    if (isValidProperty(property))
	        sourceStart = property; 
	}

	@Override
	public SourceTransformApi<FuserSurveillanceTransferEnvelope,MatmFlight> getSourceTransformApi()
	{
		return new FuserSurveillanceEnvelopeMatmTransformApi();
	}

	@Override
    public FuserAmqProps getFuserAmqProps()
    {
        FuserAmqProps props = new FuserAmqProps();
        props.setUrl( getProperty( "fuser.surveillance.jms.url", getDefaultUrl() ) );
        props.setUser( getProperty( "fuser.surveillance.jms.user", getDefaultUser() ) );
        props.setPassword( getProperty( "fuser.surveillance.jms.password", getDefaultPassword() ) );
        
        return props;
    }
}