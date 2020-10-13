package com.mosaicatm.tfmtfdmplugin;

import org.pf4j.Extension;

import com.mosaicatm.fuser.common.camel.FuserAmqProps;
import com.mosaicatm.fuser.transform.api.AbstractFuserFlightTransformPluginApi;
import com.mosaicatm.fuser.transform.api.FuserPluginProps;
import com.mosaicatm.fuser.transform.api.SourceTransformApi;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.tfmtfdmplugin.matm.TfmTfdmFlightTransferEnvelopeMatmTransformApi;

import gov.nasa.atm.tfm.tfdm.transfer.TfmTfdmFlightTransferEnvelope;

@Extension
public class TfmTfdmTransformImpl
extends AbstractFuserFlightTransformPluginApi<TfmTfdmFlightTransferEnvelope>
{
    private String sourceStart = "tfmTfdmProcessor.tfmTfdmFlightTransferEnvelope";
    
	@Override
	public String getSourceStart()
	{
		return sourceStart;
	}

	@Override
	public String getSourceType()
	{
		return "TfmTfdm";
	}
	
	@Override
    public String getComponentName()
    {
        return "tfmtfdm.jms";
    }

	@Override
	public void initialize( FuserPluginProps props )
	{
	    super.initialize(props);
	    
	    String property = getProperty( "fuser.topic.tfmtfdm.tfmTfdmFlightTransferEnvelope.source" );
	    
	    if (isValidProperty(property))
	        sourceStart = property; 
	}

	@Override
	public SourceTransformApi<TfmTfdmFlightTransferEnvelope,MatmFlight> getSourceTransformApi()
	{
		return new TfmTfdmFlightTransferEnvelopeMatmTransformApi();
	}

	@Override
    public FuserAmqProps getFuserAmqProps()
    {
        FuserAmqProps props = new FuserAmqProps();
        props.setUrl( getProperty( "fuser.tfmtfdm.jms.url", getDefaultUrl() ) );
        props.setUser( getProperty( "fuser.tfmtfdm.jms.user", getDefaultUser() ) );
        props.setPassword( getProperty( "fuser.tfmtfdm.jms.password", getDefaultPassword() ) );
        
        return props;
    }
}