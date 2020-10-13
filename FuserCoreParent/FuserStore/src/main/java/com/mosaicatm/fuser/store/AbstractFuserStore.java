package com.mosaicatm.fuser.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.store.event.FuserStoreEvent;
import com.mosaicatm.fuser.store.event.FuserStoreEvent.FuserStoreEventType;
import com.mosaicatm.fuser.store.event.FuserStoreListener;

public abstract class AbstractFuserStore <T, U>
implements FuserStore <T, U>
{	
	private final Log log = LogFactory.getLog(getClass());
    
	private String storeName = "Fuser Store";

	private int numberOfLocks = 4;
    
    private boolean sendEvents = true;
	
	private List<FuserStoreListener<T>> listeners;

	private Map<Integer, Lock> lockPool = new ConcurrentHashMap<>();

	
	protected AbstractFuserStore (String storeName, int numberOfLocks)
	{
	    this.storeName = storeName;
		this.listeners = new ArrayList<>();
		this.numberOfLocks = numberOfLocks;

		for( int x = 0; x < numberOfLocks; x++ )
		{
		    lockPool.put( x, new ReentrantLock() );
		}
	}
	
    @Override
    public void lockStore( T data )
    {
        String key = getKey( data );
        if (log.isTraceEnabled())
            log.trace ("Locking " + storeName + " for " + key);

        Lock lock = getLock( key );
        
        lock.lock();
    }
    
    @Override
    public void unlockStore( T data ) 
    {
        String key = getKey( data );
        if (log.isTraceEnabled())
            log.trace ("Unlocking " + storeName + " for " + key);

        Lock lock = getLock( key );

        lock.unlock();
    }
	
    @Override
    public void lockEntireStore()
    {
        if( log.isTraceEnabled() )
            log.trace( "Locking the entire " + storeName );

        for( Lock lock : lockPool.values() )
        {
            lock.lock();
        }
    }
    
    public void unlockEntireStore()
    {
        if( log.isTraceEnabled() )
            log.trace( "Unlocking the entire " + storeName );

        for( Lock lock : lockPool.values() )
        {
            lock.unlock();
        }
    }
    
	@Override
	public Collection<FuserStoreListener<T>> getFuserStoreListeners ()
	{
		List<FuserStoreListener<T>> rtnList = new ArrayList<> ();
		
		synchronized (listeners)
		{
			rtnList.addAll(listeners);
		}
		
		return rtnList;
	}

	@Override
	public void addFuserStoreListener(FuserStoreListener<T> listener) 
	{
		if (listener != null)
		{
			synchronized (listeners)
			{
				listeners.add(listener);
			}
		}
	}

	@Override
	public void removeFuserStoreListener(FuserStoreListener<T> listener) 
	{
		if (listener != null)
		{
			synchronized (listeners)
			{
				listeners.remove(listener);
			}
		}
	}
	
	public String getStoreName()
	{
	    return storeName;
	}
	
	protected void notifyFuserStoreListeners(FuserStoreEventType type, T data)
    {
        if (!sendEvents)
            return;
        
        if (type != null && data != null)
        {
            notifyFuserStoreListeners(createEvent(type, data));
        }
    }
	
	protected void notifyFuserStoreListeners (FuserStoreEvent<T> event)
	{
	    if (!sendEvents)
	        return;
	    
		if (event != null)
		{
			Collection<FuserStoreListener<T>> copies = getFuserStoreListeners ();
			
			if (copies != null)
			{
				for (FuserStoreListener<T> listener : copies)
				{
					listener.handleFuserStoreEvent(event);
				}
			}
		}
	}
	
	protected FuserStoreEvent<T> createEvent (FuserStoreEventType type, T content)
	{
		FuserStoreEvent<T> event = null;
		
		if (type != null && content != null)
			event = new FuserStoreEvent<>(type, content);
		
		return event;
	}
	
	public void setSendEvents (boolean sendEvents)
    {
        this.sendEvents = sendEvents;
    }
	
	private Lock getLock( String key )
	{
	    int lockIndex = 0;
	    if( key != null )
	    {
	        lockIndex = Math.abs( key.hashCode() % numberOfLocks );
	    }

        Lock lock = lockPool.get( lockIndex );

        return lock;
	}
}
