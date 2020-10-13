package com.mosaicatm.fuser.store;

import java.util.Collection;

import com.mosaicatm.fuser.store.event.FuserStoreListener;

public interface FuserStore <T, U>
{
    public void add (T data);
    public void update (T data);
    public void updateMetaData(T data, U metaData);
    public void updateMetaData(T data, Collection<U> metaDataCollection);
    public void remove (T data);
    public void removeByKey (String key);
    
    public void addAll (Collection<T> data);
    public void removeAll (Collection<T> data);
    
    public T get (Object key);
    public String getKey (T data);
    public U getMetaData(T data, String propertyName);
    public Collection<T> getAll ();
    public Collection<U> getAllMetaData(T data);
    
    public int size ();
    public int metaDataSize (T data);
    public void clear ();
    
    public void lockStore (T data);
    public void unlockStore (T data);
    public void lockEntireStore();
    public void unlockEntireStore();
    
    public void addFuserStoreListener (FuserStoreListener<T> listener);
    public void removeFuserStoreListener (FuserStoreListener<T> listener);
    public Collection<FuserStoreListener<T>> getFuserStoreListeners ();
    
    public void initialize ();
}
