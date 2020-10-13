package com.mosaicatm.fuser.client.api.data;

import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;

public interface DataAdder <T>
{
	public void add (T data);
	
	public void setReceivedEventListener (FuserReceivedEventListener<T> eventListener);
	public void setProcessedEventListener (FuserProcessedEventListener<T> eventListener);
}
