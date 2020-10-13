package com.mosaicatm.tmaplugin;

import org.pf4j.Extension;

import com.mosaicatm.fuser.common.camel.FuserAmqProps;
import com.mosaicatm.fuser.transform.api.AbstractFuserFlightTransformPluginApi;
import com.mosaicatm.fuser.transform.api.FuserPluginProps;
import com.mosaicatm.fuser.transform.api.SourceTransformApi;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.tma.common.message.GufiTmaTypeEnvelope;
import com.mosaicatm.tmaplugin.matm.GufiTmaTypeEnvelopeMatmTransformApi;

@Extension
public class TmaTransformImpl
extends AbstractFuserFlightTransformPluginApi<GufiTmaTypeEnvelope>
{
    private String sourceStart = "from.TmaLite.gufi";
    
	@Override
	public String getSourceStart()
	{
		return sourceStart;
	}

	@Override
	public String getSourceType()
	{
		return "Tma";
	}
	
	@Override
    public String getComponentName()
    {
        return "tma.jms";
    }

	@Override
	public void initialize( FuserPluginProps props )
	{
	    super.initialize(props);
	    
	    String property = getProperty( "fuser.topic.tma.gufiTmaTypeEnvelope.source" );
	    
	    if (isValidProperty(property))
	        sourceStart = property; 
	}

	@Override
	public SourceTransformApi<GufiTmaTypeEnvelope,MatmFlight> getSourceTransformApi()
	{
		return new GufiTmaTypeEnvelopeMatmTransformApi();
	}

	@Override
    public FuserAmqProps getFuserAmqProps()
    {
        FuserAmqProps props = new FuserAmqProps();
        props.setUrl( getProperty( "fuser.tma.jms.url", getDefaultUrl() ) );
        props.setUser( getProperty( "fuser.tma.jms.user", getDefaultUser() ) );
        props.setPassword( getProperty( "fuser.tma.jms.password", getDefaultPassword() ) );
        
        return props;
    }
}