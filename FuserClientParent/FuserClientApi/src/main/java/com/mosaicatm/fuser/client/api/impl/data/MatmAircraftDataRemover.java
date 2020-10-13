package com.mosaicatm.fuser.client.api.impl.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class MatmAircraftDataRemover
extends AbstractDataRemover<MatmAircraft>
{
    private final Log log = LogFactory.getLog(getClass());
    
    public MatmAircraftDataRemover ()
    {
        super (null, null);
    }
    
    public MatmAircraftDataRemover (FuserClientStore<MatmAircraft> store)
    {
        super (store, null);
    }
    
    public MatmAircraftDataRemover (FuserClientStore<MatmAircraft> store, Clock clock)
    {
        super (store, clock);
    }
    
    @Override
    public boolean isExpired(MatmAircraft aircraft)
    {
        // aircraft don't expire
        return false;
    }
}
