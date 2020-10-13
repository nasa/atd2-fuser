package com.mosaicatm.fuser.client.api.event;

public interface FuserProcessedEventManager <T>
{
	public void registerListener (FuserProcessedEventListener<T> listener);
	public void unregisterListener (FuserProcessedEventListener<T> listener);
}
