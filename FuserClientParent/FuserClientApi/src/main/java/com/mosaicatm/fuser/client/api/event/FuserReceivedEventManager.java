package com.mosaicatm.fuser.client.api.event;

public interface FuserReceivedEventManager <T>
{
	public void registerListener (FuserReceivedEventListener<T> listener);
	public void unregisterListener (FuserReceivedEventListener<T> listener);
}
