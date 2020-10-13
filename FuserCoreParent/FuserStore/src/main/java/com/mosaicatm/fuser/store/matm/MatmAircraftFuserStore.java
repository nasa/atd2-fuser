package com.mosaicatm.fuser.store.matm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.store.AbstractFuserStore;
import com.mosaicatm.fuser.store.GenericFuserStore;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class MatmAircraftFuserStore
extends GenericFuserStore <MatmAircraft>
{
    private final Log log = LogFactory.getLog(getClass());
    
    public MatmAircraftFuserStore (int numberOfLocks)
    {
        super ("Fuser Aircraft Store", numberOfLocks);
    }
    
    @Override
    public void initialize ()
    {
        // in memory map, nothing to initialize
    }
}
