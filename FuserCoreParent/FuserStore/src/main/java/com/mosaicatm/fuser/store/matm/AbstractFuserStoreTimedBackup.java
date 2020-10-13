package com.mosaicatm.fuser.store.matm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.event.FuserStoreListener;
import com.mosaicatm.matmdata.common.MetaData;

/**
 * This class is a wrapper around a FuserStore cache and FuserStore delegate.
 * Writes to the delegate do not happen immediately, instead a list of flights
 * with additions and updates is kept, and when a timer is triggered those modified flights
 * are finally sent to the delegate.
 * 
 * The goal here was to improve performance by reducing the amount of marshalling that was
 * happening in the delegate.
 */
public abstract class AbstractFuserStoreTimedBackup <T>
implements FuserStore<T, MetaData>
{
    private final Log logger = LogFactory.getLog(getClass());
    
    private final FuserStore<T, MetaData> delegate;
    private final FuserStore<T, MetaData> cache;
    
    private final Map<String, T> addedObjects;
    private final Map<String, T> updatedObjects;
    private final Map<String, Map<String, MetaData>> modifiedMetaData;
    
    private final ReentrantLock addedLock = new ReentrantLock();
    private final ReentrantLock updatedLock = new ReentrantLock();
    private final ReentrantLock metaDataLock = new ReentrantLock();

    private int backupFrequency = 30_000;
    
    protected AbstractFuserStoreTimedBackup(FuserStore<T, MetaData> delegate,
                                      FuserStore<T, MetaData> cache)
    {
        this.delegate = delegate;
        this.cache = cache;
        
        this.addedObjects = new HashMap<>();
        this.updatedObjects = new HashMap<>();
        this.modifiedMetaData = new HashMap<>();
        
        // Start a timer that will backup the cached data to redis
        Timer timer = new Timer( "RedisBackupTaskTimer" );
        timer.scheduleAtFixedRate( new BackupToDelegateTask(), backupFrequency, backupFrequency );
    }
    
    @Override
    public void add(T data)
    {
        cache.add(data);
        
        String key = cache.getKey( data );
        addedLock.lock();
        try
        {
            addedObjects.put( key, data );
        }
        finally
        {
            addedLock.unlock();
        }
    }
    
    @Override
    public void update(T data)
    {
        getCache().update(data);
        
        String key = getCache().getKey( data );
        updatedLock.lock();
        try
        {
            updatedObjects.put( key, data );
        }
        finally
        {
            updatedLock.unlock();
        }
    }
    
    @Override
    public void updateMetaData(T data, MetaData metaData)
    {
        getCache().updateMetaData(data, metaData);
        
        String key = getCache().getKey( data );
        metaDataLock.lock();
        try
        {
            Map<String, MetaData> fieldMap = modifiedMetaData.get( key );
            if( fieldMap == null )
            {
                fieldMap = new HashMap<>();
                modifiedMetaData.put( key, fieldMap );
            }
            
            fieldMap.put( metaData.getFieldName(), metaData );
        }
        finally
        {
            metaDataLock.unlock();
        }
    }
    
    @Override
    public void updateMetaData(T data, Collection<MetaData> metaDataCollection)
    {
        getCache().updateMetaData(data, metaDataCollection);
        
        String key = getCache().getKey( data );
        metaDataLock.lock();
        try
        {
            Map<String, MetaData> fieldMap = modifiedMetaData.get( key );
            if( fieldMap == null )
            {
                fieldMap = new HashMap<>();
                modifiedMetaData.put( key, fieldMap );
            }
            
            for( MetaData metaData : metaDataCollection )
            {
                fieldMap.put( metaData.getFieldName(), metaData);
            }
        }
        finally
        {
            metaDataLock.unlock();
        }
    }

    @Override
    public void remove(T data)
    {
        String key = cache.getKey( data );
        removeFromObjectMaps( key );
        
        getDelegate().remove(data);
        getCache().remove(data);
    }

    @Override
    public void removeByKey(String key)
    {
        removeFromObjectMaps( key );
        getDelegate().removeByKey(key);
        getCache().removeByKey(key);
    }

    @Override
    public void addAll(Collection<T> data)
    {
        getCache().addAll(data);
        
        addedLock.lock();
        try
        {
            for( T item : data )
            {
                String key = cache.getKey( item );
                addedObjects.put( key, item );
            }
        }
        finally
        {
            addedLock.unlock();
        }
    }

    @Override
    public void removeAll(Collection<T> data)
    {
        removeFromObjectMaps( data );
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
        addedLock.lock();
        try
        {
            addedObjects.clear();
        }
        finally
        {
            addedLock.unlock();
        }
        
        updatedLock.lock();
        try
        {
            updatedObjects.clear();
        }
        finally
        {
            updatedLock.unlock();
        }
        
        metaDataLock.lock();
        try
        {
            modifiedMetaData.clear();
        }
        finally
        {
            metaDataLock.unlock();
        }

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

    public void setBackupFrequency( int backupFrequency )
    {
        this.backupFrequency = backupFrequency;
    }

    protected FuserStore<T, MetaData> getDelegate()
    {
        return delegate;
    }
    
    protected FuserStore<T, MetaData> getCache()
    {
        return cache;
    }
    
    private void removeFromObjectMaps( String key )
    {
        addedLock.lock();
        try
        {
            addedObjects.remove( key );
        }
        finally
        {
            addedLock.unlock();
        }

        updatedLock.lock();
        try
        {
            updatedObjects.remove( key );
        }
        finally
        {
            updatedLock.unlock();
        }
        
        metaDataLock.lock();
        try
        {
            modifiedMetaData.remove( key );
        }
        finally
        {
            metaDataLock.unlock();
        }
    }
    
    private void removeFromObjectMaps( Collection<T> data )
    {
        removeFromObjectMap( addedObjects, addedLock, data );
        removeFromObjectMap( updatedObjects, updatedLock, data );
        removeFromObjectMap( modifiedMetaData, metaDataLock, data );
    }
    
    private void removeFromObjectMap( Map<String, ? extends Object> objectMap, Lock lock, Collection<T> data )
    {
        lock.lock();
        try
        {
            for( T dataElement : data )
            {
                String key = getCache().getKey( dataElement );
                objectMap.remove( key );
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    private class BackupToDelegateTask extends TimerTask
    {
        @Override
        public void run()
        {
            logger.info( "Running BackupToDelegateTask" );
            
            List<T> flightList = null;
            addedLock.lock();
            try
            {
                flightList = new LinkedList<>( addedObjects.values() );
                addedObjects.clear();
            }
            finally
            {
                addedLock.unlock();
            }

            for( T data : flightList )
            {
                try
                {
                    getDelegate().lockStore( data );
                    getDelegate().add( data );
                }
                finally
                {
                    getDelegate().unlockStore( data );
                }
            }
            
            updatedLock.lock();
            try
            {
                flightList = new LinkedList<>( updatedObjects.values() );
                updatedObjects.clear();
            }
            finally
            {
                updatedLock.unlock();
            }

            for( T data : flightList )
            {
                try
                {
                    getDelegate().lockStore( data );
                    getDelegate().update( data );
                }
                finally
                {
                    getDelegate().unlockStore( data );
                }
            }


            Set<Map.Entry<String, Map<String, MetaData>>> metaDataObjects = null;
            metaDataLock.lock();
            try
            {
                metaDataObjects = new HashSet<>(modifiedMetaData.entrySet());
                modifiedMetaData.clear();
            }
            finally
            {
                metaDataLock.unlock();
            }

            for( Map.Entry<String, Map<String, MetaData>> entry : metaDataObjects )
            {
                T data = getCache().get( entry.getKey() );
                
                try
                {
                    getDelegate().lockStore( data );
                    getDelegate().updateMetaData( data, entry.getValue().values() );
                }
                finally
                {
                    getDelegate().unlockStore( data );
                }
            }

            
            logger.info( "Finished BackupToDelegateTask" );
        }
    }
}
