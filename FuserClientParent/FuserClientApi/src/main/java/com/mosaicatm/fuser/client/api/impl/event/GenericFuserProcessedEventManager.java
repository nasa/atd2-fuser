package com.mosaicatm.fuser.client.api.impl.event;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventManager;


public class GenericFuserProcessedEventManager <T>
implements FuserProcessedEventManager <T>, FuserProcessedEventListener<T>
{
	private final Log log = LogFactory.getLog(getClass());
	
	private Set<FuserProcessedEventListener<T>> listeners;
	
	public GenericFuserProcessedEventManager ()
	{
		listeners = new HashSet<> ();
	}
	
	@Override
	public void dataAdded(T data)
	{
		if (listeners == null || listeners.isEmpty())
			return;
		
		for (FuserProcessedEventListener<T> listener : listeners)
			listener.dataAdded(data);
	}

	@Override
	public void dataUpdated(T data, T update)
	{
		if (listeners == null || listeners.isEmpty())
			return;
		
		for (FuserProcessedEventListener<T> listener : listeners)
			listener.dataUpdated(data, update);
	}

	@Override
	public void dataRemoved(T data) 
	{
		if (listeners == null || listeners.isEmpty())
			return;
		
		for (FuserProcessedEventListener<T> listener : listeners)
			listener.dataRemoved(data);
	}

	@Override
	public void registerListener(FuserProcessedEventListener<T> listener)
	{
		if (listeners != null && listener != null)
			listeners.add(listener);
	}

	@Override
	public void unregisterListener(FuserProcessedEventListener<T> listener)
	{
		if (listeners != null && listener != null)
			listeners.remove(listener);
	}

}
