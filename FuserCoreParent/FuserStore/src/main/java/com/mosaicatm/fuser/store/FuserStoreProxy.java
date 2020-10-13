package com.mosaicatm.fuser.store;

import java.util.Collection;

import com.mosaicatm.fuser.store.event.FuserStoreListener;

public class FuserStoreProxy <T, U>
implements FuserStore <T, U>
{
    private FuserStore<T, U> proxy;
    
    public FuserStoreProxy ()
    {
        // default constructor
    }
    
    public FuserStoreProxy (FuserStore<T, U> proxy)
    {
        this.proxy = proxy;
    }
    
    @Override
    public void initialize ()
    {
        if (proxy != null)
            proxy.initialize();
    }
    
    @Override
    public void add(T data) 
    {
        if (proxy != null)
            proxy.add(data);
    }

    @Override
    public void update(T data) 
    {
        if (proxy != null)
            proxy.update(data);
    }
    
    @Override
    public void updateMetaData(T data, U metaData)
    {
        if (proxy != null)
            proxy.updateMetaData(data, metaData);        
    }
    
    @Override
    public void updateMetaData(T data, Collection<U> metaData)
    {
        if (proxy != null)
            proxy.updateMetaData(data, metaData);        
    }

    @Override
    public void remove(T data) 
    {
        if (proxy != null)
            proxy.remove(data);
    }
    
    @Override
    public void removeByKey (String key)
    {
        if (proxy != null)
            proxy.removeByKey(key);
    }

    @Override
    public void addAll(Collection<T> data)
    {
        if (proxy != null)
            proxy.addAll(data);
    }

    @Override
    public void removeAll(Collection<T> data) 
    {
        if (proxy != null)
            proxy.removeAll(data);
    }

    @Override
    public T get(Object key) 
    {    
        T rtnVal = null;
        
        if (proxy != null)
            rtnVal = proxy.get(key);
        
        return rtnVal;
    }
    
    @Override
    public U getMetaData(T data, String propertyName)
    {
        U rtnVal = null;
        
        if (proxy != null)
            rtnVal = proxy.getMetaData(data, propertyName);
        
        return rtnVal;        
    }

    @Override
    public Collection<T> getAll() 
    {
        Collection<T> rtnVal = null;
        
        if (proxy != null)
            rtnVal = proxy.getAll();
        
        return rtnVal;
    }
    
    @Override
    public Collection<U> getAllMetaData(T data)
    {
        Collection<U> rtnVal = null;
        
        if (proxy != null)
            rtnVal = proxy.getAllMetaData(data);
        
        return rtnVal;        
    }

    @Override
    public int size() 
    {
        int size = 0;
        
        if (proxy != null)
            size = proxy.size();
        
        return size;
    }
    
    @Override
    public int metaDataSize(T data)
    {    
        int size = 0;
        
        if (proxy != null)
            size = proxy.metaDataSize( data );
        
        return size;        
    }

    @Override
    public void clear() 
    {
        if (proxy != null)
            proxy.clear();
    }

    @Override
    public void lockStore( T data )
    {
        if (proxy != null)
            proxy.lockStore( data );
    }

    @Override
    public void unlockStore( T data )
    {
        if (proxy != null)
            proxy.unlockStore( data );
    }

    @Override
    public void lockEntireStore()
    {
        if( proxy != null )
        {
            proxy.lockEntireStore();
        }
    }
    
    @Override
    public void unlockEntireStore()
    {
        if( proxy != null )
        {
            proxy.unlockEntireStore();
        }
    }
    
    @Override
    public void addFuserStoreListener(FuserStoreListener<T> listener)
    {
        if (proxy != null)
            proxy.addFuserStoreListener(listener);
    }

    @Override
    public void removeFuserStoreListener(FuserStoreListener<T> listener) 
    {
        if (proxy != null)
            proxy.removeFuserStoreListener(listener);
    }

    @Override
    public Collection<FuserStoreListener<T>> getFuserStoreListeners() 
    {
        Collection<FuserStoreListener<T>> listeners = null;
        
        if (proxy != null)
            listeners = proxy.getFuserStoreListeners();
        
        return listeners;
    }
    
    @Override
    public String getKey(T data)
    {
        if (proxy != null)
            return proxy.getKey(data);
        return null;
    }

    public void setProxy (FuserStore<T, U> proxy)
    {
        this.proxy = proxy;
    }
}
