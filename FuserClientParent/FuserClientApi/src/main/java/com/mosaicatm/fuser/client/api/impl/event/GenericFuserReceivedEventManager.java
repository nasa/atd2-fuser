package com.mosaicatm.fuser.client.api.impl.event;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventManager;


public class GenericFuserReceivedEventManager <T>
implements FuserReceivedEventManager <T>, FuserReceivedEventListener<T>
{
	private final Log log = LogFactory.getLog(getClass());
	
	private Set<FuserReceivedEventListener<T>> listeners;
	
	public GenericFuserReceivedEventManager ()
	{
		listeners = new HashSet<> ();
	}
	
	@Override
	public void receivedAdd(T data)
	{
		if (listeners == null || listeners.isEmpty())
			return;
		
		for (FuserReceivedEventListener<T> listener : listeners)
			listener.receivedAdd(data);
	}

	@Override
	public void receivedUpdate(T existingBeforeUpdating, T data)
	{
		if (listeners == null || listeners.isEmpty())
			return;
		
		for (FuserReceivedEventListener<T> listener : listeners)
			listener.receivedUpdate(existingBeforeUpdating, data);
	}

	@Override
	public void receivedRemove(T data) 
	{
		if (listeners == null || listeners.isEmpty())
			return;
		
		for (FuserReceivedEventListener<T> listener : listeners)
			listener.receivedRemove(data);
	}

	@Override
	public void registerListener(FuserReceivedEventListener<T> listener)
	{
		if (listeners != null && listener != null)
			listeners.add(listener);
	}

	@Override
	public void unregisterListener(FuserReceivedEventListener<T> listener)
	{
		if (listeners != null && listener != null)
			listeners.remove(listener);
	}

}
