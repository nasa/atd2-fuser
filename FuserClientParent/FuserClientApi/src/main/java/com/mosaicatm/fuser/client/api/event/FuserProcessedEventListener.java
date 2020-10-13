package com.mosaicatm.fuser.client.api.event;

public interface FuserProcessedEventListener <T> 
{
	public void dataAdded (T data);
	public void dataUpdated (T afterUpdating, T update);
	public void dataRemoved (T data);
}
