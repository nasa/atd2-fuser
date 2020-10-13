package com.mosaicatm.fuser.client.api.impl.data;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.data.DataRemover;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class TimedProxyDataRemover <T>
implements DataRemover <T>
{
	private final Log log = LogFactory.getLog(getClass());
	
	private long runInterval = 1 * TimeFactory.MINUTE_IN_MILLIS;
	
	private Timer timer;
	
	private DataRemover<T> proxy;
	
	private boolean active;
	
	public TimedProxyDataRemover (DataRemover<T> proxy)
	{
		this.proxy = proxy;
	}
	
	@Override
	public void remove()
	{
		if (proxy != null)
			proxy.remove();
	}
	
	@Override
	public void remove (T data)
	{
		if (proxy != null)
			proxy.remove(data);
	}

	public void start ()
	{
		if (!active)
			return;
		
		timer = new Timer ("Expired Data Timer");
		timer.scheduleAtFixedRate(new RemoveTask(), runInterval, runInterval);
	}
	
	public void stop ()
	{
		if (timer != null)
			timer.cancel();
		
		timer = null;
	}
	
	@Override
	public void setReceivedEventListener (FuserReceivedEventListener<T> eventNotifier)
	{
		if (proxy != null)
			proxy.setReceivedEventListener(eventNotifier);
	}
	
	@Override
	public void setProcessedEventListener (FuserProcessedEventListener<T> eventListener)
	{
		if (proxy != null)
			proxy.setProcessedEventListener(eventListener);
	}
	
	private class RemoveTask
	extends TimerTask
	{
		@Override
		public void run ()
		{
			if (!active)
				return;
			
			if (log.isDebugEnabled())
				log.debug ("Checking for expired flights");
			
			remove ();
		}
	}
	
	public void setRunIntervalMinutes (int minutes)
	{
		setRunIntervalMillis (minutes * TimeFactory.MINUTE_IN_MILLIS);
	}
	
	public void setRunIntervalMillis (long runInterval)
	{
		this.runInterval = runInterval;
	}
	
	public void setActive (boolean active)
	{
		this.active = active;
	}
}
