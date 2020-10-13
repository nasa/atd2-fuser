package com.mosaicatm.fuser.client.api.impl.data;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.data.FuserClientStore;

public class FuserLockingProxyStore <T>
implements FuserClientStore <T>
{
	private final Log log = LogFactory.getLog(getClass());
	
	private FuserClientStore<T> proxy;
	
	public FuserLockingProxyStore (FuserClientStore<T> proxy)
	{
		this.proxy = proxy;
	}
	
	@Override
	public void add(T data)
	{
		if (proxy == null)
			return;
		
		try
		{
			proxy.lock();
			proxy.add(data);
		}
		catch (Exception e)
		{
			log.error ("Error adding store data", e);
		}
		finally
		{
			proxy.unlock();
		}
	}

	@Override
	public void update(T data)
	{
		if (proxy == null)
			return;
		
		try
		{
			proxy.lock();
			proxy.update(data);
		}
		catch (Exception e)
		{
			log.error ("Error updating store data", e);
		}
		finally
		{
			proxy.unlock();
		}	
	}

	@Override
	public void remove(T data)
	{
		if (proxy == null)
			return;
		
		try
		{
			proxy.lock();
			proxy.remove(data);
		}
		catch (Exception e)
		{
			log.error ("Error removing store data", e);
		}
		finally
		{
			proxy.unlock();
		}	
	}

	@Override
	public void removeByKey(Object key)
	{
		if (proxy == null)
			return;
		
		try
		{
			proxy.lock();
			proxy.removeByKey(key);
		}
		catch (Exception e)
		{
			log.error ("Error removing store data by key", e);
		}
		finally
		{
			proxy.unlock();
		}
	}

	@Override
	public T get(Object key)
	{
		T value = null;
		
		if (proxy != null)
		{
			try
			{
				proxy.lock();
				value = proxy.get(key);
			}
			catch (Exception e)
			{
				log.error ("Error getting store data", e);
			}
			finally
			{
				proxy.unlock();
			}
		}
		
		return value;
	}

	@Override
	public List<T> getAll()
	{
		List<T> value = null;
		
		if (proxy != null)
		{
			try
			{
				proxy.lock();
				value = proxy.getAll();
			}
			catch (Exception e)
			{
				log.error ("Error getting all store data", e);
			}
			finally
			{
				proxy.unlock();
			}
		}
		
		return value;
	}

	@Override
	public int size()
	{
		int size = 0;
		
		if (proxy != null)
		{
			try
			{
				proxy.lock();
				size = proxy.size();
			}
			catch (Exception e)
			{
				log.error ("Error getting store size", e);
			}
			finally
			{
				proxy.unlock();
			}
		}
		
		return size;
	}

	@Override
	public void clear()
	{
		if (proxy == null)
			return;
		
		try
		{
			proxy.lock();
			proxy.clear();
		}
		catch (Exception e)
		{
			log.error ("Error clearing store data", e);
		}
		finally
		{
			proxy.unlock();
		}	
	}
	
	@Override
	public String getKey(T data)
	{
	    if (proxy != null)
	        return proxy.getKey(data);
	    return null;
	}

	@Override
	public void lock()
	{
		if (proxy != null)
			proxy.lock();
	}

	@Override
	public void unlock()
	{
		if (proxy != null)
			proxy.unlock();
	}
}
