package com.mosaicatm.fuser.client.api.event;

public interface FuserSyncCompleteEventManager extends FuserSyncCompleteEventListener
{
    public void registerListener (FuserSyncCompleteEventListener listener);
    public void unregisterListener (FuserSyncCompleteEventListener listener);
}
