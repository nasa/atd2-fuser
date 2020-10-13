package com.mosaicatm.fuser.store.matm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.store.GenericFuserStore;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFuserStore
extends GenericFuserStore <MatmFlight>
{
    private final Log log = LogFactory.getLog(getClass());
    
    public MatmFuserStore (int numberOfLocks)
    {
        super ("Fuser Flight Store", numberOfLocks);
    }
    
    @Override
    public void initialize ()
    {
        // in memory map, nothing to initialize
    }
}
