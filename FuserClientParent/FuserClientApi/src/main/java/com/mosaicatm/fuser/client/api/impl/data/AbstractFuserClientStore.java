package com.mosaicatm.fuser.client.api.impl.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.data.FuserClientStore;

public abstract class AbstractFuserClientStore<T>
implements FuserClientStore<T>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private Lock lock;
    
    private Map<String, T> store;
    
    protected AbstractFuserClientStore()
    {
        this.lock = new ReentrantLock();
        this.store = new ConcurrentHashMap<>();
    }
    
    @Override
    public void add(T data)
    {
        if (data == null)
            return;
        
        String key = getKey (data);
        
        if (key != null)
            store.put(key, data);
    }

    @Override
    public void update(T data)
    {
        if (data == null)
            return;
        
        String key = getKey (data);
        
        if (key != null)
            store.put(key, data);
    }

    @Override
    public void remove(T data) 
    {
        if (data == null)
            return;
        
        String key = getKey (data);
        
        if (key != null)
            removeByKey (key);
    }
    
    @Override
    public void removeByKey (Object key)
    {
        if (key == null)
            return;
        
        store.remove(key);
    }

    @Override
    public T get (Object key)
    {
        T data = null;
        
        if (key != null)
            data = store.get(key);
        
        return data;
    }
    
    @Override
    public List<T> getAll ()
    {
        return new ArrayList<> (store.values());
    }

    @Override
    public int size()
    {
        return store.size();
    }
    
    @Override
    public void clear ()
    {
        store.clear();
    }

    @Override
    public void lock ()
    {       
        lock.lock();
    }
    
    @Override
    public void unlock ()
    {       
        lock.unlock();
    }
}
