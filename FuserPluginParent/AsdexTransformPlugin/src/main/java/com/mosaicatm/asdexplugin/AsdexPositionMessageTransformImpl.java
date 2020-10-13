package com.mosaicatm.asdexplugin;

import org.pf4j.Extension;

import com.mosaicatm.asdex.transfer.PositionMessage;
import com.mosaicatm.asdexplugin.matm.PositionMessageMatmTransformApi;
import com.mosaicatm.fuser.common.camel.FuserAmqProps;
import com.mosaicatm.fuser.transform.api.AbstractFuserFlightTransformPluginApi;
import com.mosaicatm.fuser.transform.api.FuserPluginProps;
import com.mosaicatm.fuser.transform.api.SourceTransformApi;
import com.mosaicatm.matmdata.flight.MatmFlight;

@Extension
public class AsdexPositionMessageTransformImpl
extends AbstractFuserFlightTransformPluginApi<PositionMessage>
{
    private String sourceStart = "asdex.PositionMessage";

	@Override
	public String getSourceStart()
	{
		return sourceStart;
	}

	@Override
	public String getSourceType()
	{
		return "AsdexPositionMessage";
	}
	
	@Override
    public String getComponentName()
    {
        return "asdex.jms";
    }

	@Override
	public void initialize( FuserPluginProps props )
	{
	    super.initialize(props);
	    
	    String property = getProperty( "fuser.topic.asdex.positionMessage.source" );
	    
	    if (isValidProperty(property))
	        sourceStart = property; 
	}

	@Override
	public SourceTransformApi<PositionMessage,MatmFlight> getSourceTransformApi()
	{
		return new PositionMessageMatmTransformApi();
	}

	@Override
    public FuserAmqProps getFuserAmqProps()
    {
        FuserAmqProps props = new FuserAmqProps();
        props.setUrl( getProperty( "fuser.asdex.jms.url", getDefaultUrl() ) );
        props.setUser( getProperty( "fuser.asdex.jms.user", getDefaultUser() ) );
        props.setPassword( getProperty( "fuser.asdex.jms.password", getDefaultPassword() ) );
        
        return props;
    }
}
