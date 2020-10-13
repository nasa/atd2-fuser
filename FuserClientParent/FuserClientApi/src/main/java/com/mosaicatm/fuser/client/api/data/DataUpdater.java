package com.mosaicatm.fuser.client.api.data;

import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;

public interface DataUpdater <T> 
{
	public void update (T update, T target);
	
	public void setReceivedEventListener (FuserReceivedEventListener<T> eventListener);
	public void setProcessedEventListener (FuserProcessedEventListener<T> eventListener);
}
