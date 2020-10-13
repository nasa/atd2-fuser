package com.mosaicatm.fuser.transform.api;

import com.mosaicatm.matmdata.flight.MatmFlight;

public abstract class AbstractFuserFlightTransformPluginApi<E>
extends AbstractFuserTransformPluginApi<E,MatmFlight>
{
    @Override
    public FuserDestinationType getFuserDestinationType()
    {
        return( FuserDestinationType.FLIGHT );
    }
}
