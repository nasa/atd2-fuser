package com.mosaicatm.fuser.client.api.impl.event;

import java.util.HashSet;
import java.util.Set;

import com.mosaicatm.fuser.client.api.event.FuserSyncCompleteEventListener;
import com.mosaicatm.fuser.client.api.event.FuserSyncCompleteEventManager;

public class GenericFuserSyncCompleteEventManager implements FuserSyncCompleteEventManager
{
    private Set<FuserSyncCompleteEventListener> listeners;

    public GenericFuserSyncCompleteEventManager()
    {
        listeners = new HashSet<> ();
    }

    @Override
    public void syncComplete()
    {
        if (listeners == null || listeners.isEmpty())
        {
            return;
        }

        for( FuserSyncCompleteEventListener listener : listeners )
        {
            listener.syncComplete();
        }
    }

    @Override
    public void registerListener( FuserSyncCompleteEventListener listener )
    {
        if( listeners != null && listener != null )
        {
            listeners.add( listener );
        }
    }

    @Override
    public void unregisterListener( FuserSyncCompleteEventListener listener )
    {
        if( listeners != null && listener != null )
        {
            listeners.remove( listener );
        }
    }

}
