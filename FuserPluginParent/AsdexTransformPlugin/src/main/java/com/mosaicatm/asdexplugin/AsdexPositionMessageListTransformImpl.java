package com.mosaicatm.asdexplugin;

import org.pf4j.Extension;

import com.mosaicatm.asdex.transfer.PositionMessageList;
import com.mosaicatm.asdexplugin.matm.PositionMessageListMatmTransformApi;
import com.mosaicatm.fuser.common.camel.FuserAmqProps;
import com.mosaicatm.fuser.transform.api.AbstractFuserFlightTransformPluginApi;
import com.mosaicatm.fuser.transform.api.FuserPluginProps;
import com.mosaicatm.fuser.transform.api.SourceTransformApi;
import com.mosaicatm.matmdata.flight.MatmFlight;

@Extension
public class AsdexPositionMessageListTransformImpl
extends AbstractFuserFlightTransformPluginApi<PositionMessageList>
{
    private String sourceStart = "asdex.PositionMessageList";

	@Override
	public String getSourceStart()
	{
		return sourceStart;
	}

	@Override
	public String getSourceType()
	{
		return "AsdexPositionMessageList";
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
	    
	    String property = getProperty( "fuser.topic.asdex.positionMessageList.source" );
	    
	    if (isValidProperty(property))
	        sourceStart = property;
	}

	@Override
	public SourceTransformApi<PositionMessageList,MatmFlight> getSourceTransformApi()
	{
		return new PositionMessageListMatmTransformApi();
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
