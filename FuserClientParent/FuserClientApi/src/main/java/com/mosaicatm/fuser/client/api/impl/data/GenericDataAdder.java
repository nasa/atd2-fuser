package com.mosaicatm.fuser.client.api.impl.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.data.DataAdder;
import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;
import com.mosaicatm.fuser.client.api.util.CloneUtil;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class GenericDataAdder<T> 
implements DataAdder <T>
{
	private final Log log = LogFactory.getLog(getClass());
	
	private FuserReceivedEventListener<T> eventNotifier;
	private FuserProcessedEventListener<T> eventListener;
	
	private FuserClientStore<T> store;
	
	public GenericDataAdder ()
	{
		super ();
	}
	
	public GenericDataAdder (FuserClientStore<T> store)
	{
		this.store = store;
	}
	
	@Override
	public void add (T data)
	{
		if (data == null)
			return;
		
		if (!isStoreAvailable())
			return;
		
		T clone = CloneUtil.cloneObject(data);
		
		if (eventNotifier != null)
			eventNotifier.receivedAdd(clone);
		
		store.add(data);
		
		if (eventListener != null)
			eventListener.dataAdded(clone);
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
	public void setReceivedEventListener (FuserReceivedEventListener<T> eventNotifier)
	{
		this.eventNotifier = eventNotifier;
	}
	
	@Override
	public void setProcessedEventListener (FuserProcessedEventListener<T> eventListener)
	{
		this.eventListener = eventListener;
	}
}
