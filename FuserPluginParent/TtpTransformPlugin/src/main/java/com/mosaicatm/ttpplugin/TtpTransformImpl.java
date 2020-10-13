package com.mosaicatm.ttpplugin;

import org.pf4j.Extension;

import com.mosaicatm.fuser.common.camel.FuserAmqProps;
import com.mosaicatm.fuser.transform.api.AbstractFuserFlightTransformPluginApi;
import com.mosaicatm.fuser.transform.api.FuserPluginProps;
import com.mosaicatm.fuser.transform.api.SourceTransformApi;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.ttp.util.TtpFlightDataWrapper;
import com.mosaicatm.ttpplugin.matm.TtpFlightDataWrapperMatmTransformApi;

@Extension
public class TtpTransformImpl
extends AbstractFuserFlightTransformPluginApi<TtpFlightDataWrapper>
{
    private String sourceStart = "ttpdata.ttpTransfer";

	@Override
	public String getSourceStart()
	{
		return sourceStart;
	}

	@Override
	public String getSourceType()
	{
		return "TtpSingle";
	}
	
	@Override
    public String getComponentName()
    {
        return "ttp.jms";
    }

	@Override
	public void initialize( FuserPluginProps props )
	{
	    super.initialize(props);
	    
	    String property = getProperty( "fuser.topic.ttp.ttpTransfer.source" );
	    
	    if (isValidProperty(property))
	        sourceStart = property;
	}

	@Override
	public SourceTransformApi<TtpFlightDataWrapper,MatmFlight> getSourceTransformApi()
	{
		return new TtpFlightDataWrapperMatmTransformApi();
	}

	@Override
    public FuserAmqProps getFuserAmqProps()
    {
        FuserAmqProps props = new FuserAmqProps();
        props.setUrl( getProperty( "fuser.ttp.jms.url", getDefaultUrl() ) );
        props.setUser( getProperty( "fuser.ttp.jms.user", getDefaultUser() ) );
        props.setPassword( getProperty( "fuser.ttp.jms.password", getDefaultPassword() ) );
        
        return props;
    }
}
