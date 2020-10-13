package com.mosaicatm.sfdpsplugin;

import org.pf4j.Extension;

import com.mosaicatm.fuser.common.camel.FuserAmqProps;
import com.mosaicatm.fuser.transform.api.AbstractFuserTransformPluginApi;
import com.mosaicatm.fuser.transform.api.FuserPluginProps;
import com.mosaicatm.fuser.transform.api.SourceTransformApi;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;
import com.mosaicatm.sfdps.data.transfer.SfdpsSectorAssignmentTransferEnvelope;
import com.mosaicatm.sfdpsplugin.matm.SfdpsSectorAssignmentTransferEnvelopeMatmTransformApi;

@Extension
public class SfdpsSectorAssignmentEnvelopeTransformImpl
extends AbstractFuserTransformPluginApi<SfdpsSectorAssignmentTransferEnvelope,MatmSectorAssignment>
{
    private String sourceStart = "sfdpsairspace.sfdpsSectorAssignmentTransferEnvelope";

    @Override
    public FuserDestinationType getFuserDestinationType()
    {
        return( FuserDestinationType.SECTOR_ASSIGNMENT );
    }
    
    @Override
    public String getSourceStart()
    {
        return sourceStart;
    }

    @Override
    public String getSourceType()
    {
        return "SfdpsSectorAssignmentTransferEnvelope";
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
        
        String property = getProperty( "fuser.topic.sfdps.sfdpsSectorAssignmentTransferEnvelope.source" );
        
        if (isValidProperty(property))
            sourceStart = property; 
    }

    @Override
    public SourceTransformApi<SfdpsSectorAssignmentTransferEnvelope,MatmSectorAssignment> getSourceTransformApi()
    {
        return new SfdpsSectorAssignmentTransferEnvelopeMatmTransformApi();
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