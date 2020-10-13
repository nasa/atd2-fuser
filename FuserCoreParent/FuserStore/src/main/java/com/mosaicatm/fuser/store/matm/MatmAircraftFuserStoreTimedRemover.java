package com.mosaicatm.fuser.store.matm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.common.MetaData;

public class MatmAircraftFuserStoreTimedRemover
extends AbstractFuserStoreTimedRemover<MatmAircraft>
{    
    private final Log log = LogFactory.getLog(getClass());
    
    private long expirationTime = TimeFactory.DAY_IN_MILLIS * 6;
    
    public MatmAircraftFuserStoreTimedRemover (FuserStore<MatmAircraft, MetaData> store)
    {
        super("MatmAircraft", store);
    }    

    @Override
    public boolean isExpired (MatmAircraft aircraft)
    {
        boolean expired = false;
        
        if (aircraft != null &&
            aircraft.getTimestamp() != null)
        {
            long currTime = getClock().getTimeInMillis();
            expired = aircraft.getTimestamp().getTime() < (currTime - expirationTime);
        }
        
        return expired;
    }
    
    public void setExpirationTimeDays (int expirationTime)
    {
        setExpirationTimeMillis(expirationTime * TimeFactory.DAY_IN_MILLIS);
    }
    
    public void setExpirationTimeMillis (long expirationTime)
    {
        this.expirationTime = expirationTime;
    }
}
