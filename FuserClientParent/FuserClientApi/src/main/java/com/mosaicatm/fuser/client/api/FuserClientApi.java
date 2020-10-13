package com.mosaicatm.fuser.client.api;

import java.util.List;

import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserSyncCompleteEventListener;
import com.mosaicatm.fuser.services.client.FuserSyncService;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.util.filter.Filter;

public interface FuserClientApi <T> 
{
	public Clock getClock ();
	
	public FuserClientStore<T> getStore ();
	
	public FuserSyncService getSyncService();
		
	public void handleAdd (T data);
	public void handleUpdate (T update, T target);
	public void handleRemove (T data);
	
	public void registerReceivedEventListener (FuserReceivedEventListener<T> eventNotifier);
	public void unregisterReceivedEventListener (FuserReceivedEventListener<T> eventNotifier);
	
	public void registerProcessedEventListener (FuserProcessedEventListener<T> eventListener);
	public void unregisterProcessedEventListener (FuserProcessedEventListener<T> eventListener);
	
	public void registerFuserSyncCompleteEventListener( FuserSyncCompleteEventListener eventListener );
	public void unregisterFuserSyncCompleteEventListener( FuserSyncCompleteEventListener eventListener );
	
	//Had to add a start method to allow listeners to register prior to the sync
	//completing and the sync point unlocking
	public void start(boolean sync);
	
	public void publish(T data);
	public void publishBatch(List<T> data);
	public void publishRemove(T data);
	
	public void addFilter(Filter<T> filter);
	public Filter<T> getFilter();

}
