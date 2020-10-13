package com.mosaicatm.sfdpsplugin;

import org.pf4j.Extension;

import com.mosaicatm.fuser.common.camel.FuserAmqProps;
import com.mosaicatm.fuser.transform.api.AbstractFuserFlightTransformPluginApi;
import com.mosaicatm.fuser.transform.api.FuserPluginProps;
import com.mosaicatm.fuser.transform.api.SourceTransformApi;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.sfdps.data.transfer.SfdpsFlightTransferEnvelope;
import com.mosaicatm.sfdpsplugin.matm.SfdpsFlightTransferEnvelopeMatmTransformApi;

@Extension
public class SfdpsEnvelopeTransformImpl
extends AbstractFuserFlightTransformPluginApi<SfdpsFlightTransferEnvelope>
{
    private String sourceStart = "sfdpsflight.sfdpsFlightTransferEnvelope";
    
    @Override
    public String getSourceStart()
    {
        return sourceStart;
    }

    @Override
    public String getSourceType()
    {
        return "SfdpsEnvelope";
    }
    
    @Override
    public String getComponentName()
    {
        return "sfdps.jms";
    }

    @Override
    public void initialize( FuserPluginProps props )
    {
        super.initialize(props);
        
        String property = getProperty( "fuser.topic.sfdps.sfdpsFlightTransferEnvelope.source" );
        
        if (isValidProperty(property))
            sourceStart = property; 
    }

    @Override
    public SourceTransformApi<SfdpsFlightTransferEnvelope,MatmFlight> getSourceTransformApi()
    {
        return new SfdpsFlightTransferEnvelopeMatmTransformApi();
    }

    @Override
    public FuserAmqProps getFuserAmqProps()
    {
        FuserAmqProps props = new FuserAmqProps();
        props.setUrl( getProperty( "fuser.sfdps.jms.url", getDefaultUrl() ) );
        props.setUser( getProperty( "fuser.sfdps.jms.user", getDefaultUser() ) );
        props.setPassword( getProperty( "fuser.sfdps.jms.password", getDefaultPassword() ) );
        
        return props;
    }
}