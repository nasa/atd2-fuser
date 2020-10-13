package com.mosaicatm.fuser.client.api.event;

public interface FuserReceivedEventListener <T> 
{
	public void receivedAdd (T data);
	public void receivedUpdate (T existingBeforeUpdating, T data);
	public void receivedRemove (T data);
}
