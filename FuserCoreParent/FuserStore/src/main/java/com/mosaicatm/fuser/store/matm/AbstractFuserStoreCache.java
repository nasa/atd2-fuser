package com.mosaicatm.fuser.store.matm;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.event.FuserStoreListener;
import com.mosaicatm.matmdata.common.MetaData;

public abstract class AbstractFuserStoreCache <T>
implements FuserStore<T, MetaData>
{
    private final Log logger = LogFactory.getLog(getClass());
    
    private final FuserStore<T, MetaData> delegate;
    private final FuserStore<T, MetaData> cache;
    
    protected AbstractFuserStoreCache(FuserStore<T, MetaData> delegate,
                                      FuserStore<T, MetaData> cache)
    {
        this.delegate = delegate;
        this.cache = cache;
    }
    
    @Override
    public void add(T data)
    {
        getDelegate().add(data);
        cache.add(data);
    }
    
    @Override
    public void update(T data)
    {
        getDelegate().update(data);
        getCache().update(data);
    }
    
    @Override
    public void updateMetaData(T data, MetaData metaData)
    {
        getDelegate().updateMetaData(data, metaData);
        getCache().updateMetaData(data, metaData);        
    }
    
    @Override
    public void updateMetaData(T data, Collection<MetaData> metaDataCollection)
    {
        getDelegate().updateMetaData(data, metaDataCollection);
        getCache().updateMetaData(data, metaDataCollection);
    }

    @Override
    public void remove(T data)
    {
        getDelegate().remove(data);
        getCache().remove(data);
    }

    @Override
    public void removeByKey(String key)
    {      
        getDelegate().removeByKey(key);
        getCache().removeByKey(key);
    }

    @Override
    public void addAll(Collection<T> data)
    {
        getDelegate().addAll(data);
        getCache().addAll(data);
    }

    @Override
    public void removeAll(Collection<T> data)
    {
        getDelegate().removeAll(data);
        getCache().removeAll(data);
    }
    
    @Override
    public T get(Object key)
    {
        return getCache().get(key);
    }
    
    @Override
    public MetaData getMetaData(T data, String propertyName)
    {
        return( getCache().getMetaData( data, propertyName ));
    }

    @Override
    public Collection<T> getAll()
    {
        return getCache().getAll();
    }
    
    @Override
    public Collection<MetaData> getAllMetaData(T data)
    {
        return( getCache().getAllMetaData( data ));
    }

    @Override
    public int size()
    {
        return getCache().size();
    }
    
    @Override
    public int metaDataSize(T data)
    {   
        return( getCache().metaDataSize( data ));
    }    

    @Override
    public void clear()
    {
        getDelegate().clear();
        getCache().clear();
    }

    @Override
    public void lockStore(T data)
    {
        getDelegate().lockStore( data );
        getCache().lockStore( data );
    }

    @Override
    public void unlockStore(T data)
    {
        getDelegate().unlockStore( data );
        getCache().unlockStore( data );
    }
    
    @Override
    public void lockEntireStore()
    {
        getDelegate().lockEntireStore();
        getCache().lockEntireStore();
    }
    
    @Override
    public void unlockEntireStore()
    {
        getDelegate().unlockEntireStore();
        getCache().unlockEntireStore();
    }
    
    @Override
    public void addFuserStoreListener(FuserStoreListener<T> listener)
    {
        delegate.addFuserStoreListener(listener);
    }

    @Override
    public void removeFuserStoreListener(FuserStoreListener<T> listener)
    {
        delegate.removeFuserStoreListener(listener);
    }

    @Override
    public Collection<FuserStoreListener<T>> getFuserStoreListeners()
    {
        return delegate.getFuserStoreListeners();
    }
    
    protected FuserStore<T, MetaData> getDelegate()
    {
        return delegate;
    }
    
    protected FuserStore<T, MetaData> getCache()
    {
        return cache;
    }
}
