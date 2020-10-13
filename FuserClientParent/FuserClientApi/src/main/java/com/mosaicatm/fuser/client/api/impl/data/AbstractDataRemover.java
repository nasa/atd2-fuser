package com.mosaicatm.fuser.client.api.impl.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.data.DataRemover;
import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;
import com.mosaicatm.lib.time.Clock;

public abstract class AbstractDataRemover<T>
implements DataRemover<T>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private FuserReceivedEventListener<T> eventNotifier;
    private FuserProcessedEventListener<T> eventListener;
    
    private Clock clock;
    
    private FuserClientStore<T> store;
    
    protected AbstractDataRemover (FuserClientStore<T> store, Clock clock)
    {
        this.store = store;
        this.clock = clock;
    }
    
    public abstract boolean isExpired(T data);
    
    @Override
    public void remove()
    {
        if (!isStoreAvailable())
            return;
        
        List<T> dataList = store.getAll();
        
        if (dataList != null && !dataList.isEmpty())
        {
            List<T> expired = new ArrayList<> ();
            
            for (T data : dataList)
            {
                if (isExpired (data))
                    expired.add(data);
            }
            
            if (!expired.isEmpty())
            {
                for (T rotten : expired)
                {
                    remove (rotten);
                }
            }
        }
    }
    
    @Override
    public void remove (T data)
    {
        if (!isStoreAvailable ())
            return;
        
        if (data == null)
            return;
        
        if (eventNotifier != null)
            eventNotifier.receivedRemove(data);
        
        store.remove(data);
        
        if (eventListener != null)
            eventListener.dataRemoved(data);
    }
    
    private boolean isStoreAvailable ()
    {
        if (store == null)
        {
            log.error ("Remove failed, data store is not available");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void setReceivedEventListener (FuserReceivedEventListener<T> eventNotifier)
    {
        this.eventNotifier = eventNotifier;
    }
    
    @Override
    public void setProcessedEventListener (FuserProcessedEventListener<T> eventListener)
    {
        this.eventListener = eventListener;
    }
    
    public void setStore (FuserClientStore<T> store)
    {
        this.store = store;
    }
    
    public FuserClientStore<T> getStore()
    {
        return store;
    }
    
    public void setClock (Clock clock)
    {
        this.clock = clock;
    }
    
    public Clock getClock()
    {
        return clock;
    }
}
