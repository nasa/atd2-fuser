package com.mosaicatm.smesplugin;

import org.pf4j.Extension;

import com.mosaicatm.fuser.common.camel.FuserAmqProps;
import com.mosaicatm.fuser.transform.api.AbstractFuserFlightTransformPluginApi;
import com.mosaicatm.fuser.transform.api.FuserPluginProps;
import com.mosaicatm.fuser.transform.api.SourceTransformApi;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.smes.transfer.SmesMessageTransferEnvelope;
import com.mosaicatm.smesplugin.matm.SmesMessageEnvelopeMatmTransformApi;

@Extension
public class SmesMessageListTransformImpl 
extends AbstractFuserFlightTransformPluginApi<SmesMessageTransferEnvelope> 
{

    private String sourceStart = "smes.message";

    @Override
    public String getSourceStart() 
    {
        return sourceStart;
    }

    @Override
    public String getSourceType() 
    {
        return "SmesMessage";
    }
    
    @Override
    public String getComponentName()
    {
        return "smes.jms";
    }

    @Override
    public void initialize(FuserPluginProps props) 
    {
        super.initialize(props);
        
        String property = getProperty("fuser.topic.smes.smesMessageTransferEnvelope.source");
        
        if (isValidProperty(property))
            sourceStart = property; 
    }

    @Override
    public SourceTransformApi<SmesMessageTransferEnvelope,MatmFlight> getSourceTransformApi() 
    {
        return new SmesMessageEnvelopeMatmTransformApi();
    }

    @Override
    public FuserAmqProps getFuserAmqProps()
    {
        FuserAmqProps props = new FuserAmqProps();
        props.setUrl( getProperty( "fuser.smes.jms.url", getDefaultUrl() ) );
        props.setUser( getProperty( "fuser.smes.jms.user", getDefaultUser() ) );
        props.setPassword( getProperty( "fuser.smes.jms.password", getDefaultPassword() ) );
        
        return props;
    }
}
