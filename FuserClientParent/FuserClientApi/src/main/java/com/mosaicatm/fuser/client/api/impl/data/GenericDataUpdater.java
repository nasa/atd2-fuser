package com.mosaicatm.fuser.client.api.impl.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jvnet.jaxb2_commons.lang.MergeFrom;

import com.mosaicatm.fuser.client.api.data.DataUpdater;
import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;

public class GenericDataUpdater<T>
implements DataUpdater<T>
{
	private Log log = LogFactory.getLog(getClass());
	
	private FuserReceivedEventListener<T> eventNotifier;
	private FuserProcessedEventListener<T> eventListener;
	
	private FuserClientStore<T> store;
	
	public GenericDataUpdater ()
	{
		super ();
	}
	
	public GenericDataUpdater (FuserClientStore<T> store)
	{
		this.store = store;
	}
	
	@Override
	public void update(T update, T target)
	{
		if (update == null || target == null)			
			return;
		
		if (!isStoreAvailable())
			return;
		
		if (eventNotifier != null)
			eventNotifier.receivedUpdate(target, update);
		
		if (target instanceof MergeFrom && update instanceof MergeFrom)
		{
		    ((MergeFrom)target).mergeFrom(update, target);
		}
		
		store.update(target);
		
		if (eventListener != null)
			eventListener.dataUpdated(target, update);
		
	}
	
	private boolean isStoreAvailable ()
	{
		if (store == null)
		{
			log.error ("Add failed, data store is not available");
			return false;
		}
		
		return true;
	}
	
	public void setStore (FuserClientStore<T> store)
	{
		this.store = store;
	}
	
	@Override
	public void setReceivedEventListener (FuserReceivedEventListener<T> eventListener)
	{
		this.eventNotifier = eventListener;
	}
	
	@Override
	public void setProcessedEventListener (FuserProcessedEventListener<T> eventListener)
	{
		this.eventListener = eventListener;
	}

}
