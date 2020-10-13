package com.mosaicatm.fuser.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.fuser.store.event.FuserStoreEvent.FuserStoreEventType;
import com.mosaicatm.matmdata.common.MetaData;

public abstract class GenericFuserStore<T>
extends AbstractFuserStore<T, MetaData>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private Map<String, T> store;
    private Map<String, Map<String, MetaData>> metaDataStore;
    private MatmIdLookup<T, String> idLookup;
    
    protected GenericFuserStore (String storeName, int numberOfLocks)
    {
        super(storeName, numberOfLocks);
        
        this.store = new ConcurrentHashMap<>();
        this.metaDataStore = new ConcurrentHashMap<>();
    }
    
    @Override
    public void add(T data) 
    {
        String key = getKey(data);
        
        if (data != null && key != null)
        {
            store.put(key, data);
            notifyFuserStoreListeners(FuserStoreEventType.ADD, data);
        }
    }
    
    @Override
    public void update (T data)
    {
        String key = getKey(data);
        if (data != null && key != null)
        {            
            store.put(key, data);
            notifyFuserStoreListeners(FuserStoreEventType.UPDATE, data);
        }
    }
    
    protected Map<String, MetaData> getMetaDataMap(String key)
    {
        Map<String, MetaData> meta_map = null;
        
        if (key != null)
        {
            meta_map = metaDataStore.get(key);
            if (meta_map == null)
            {
                meta_map = new ConcurrentHashMap<>();
                metaDataStore.put(key, meta_map);
            }
        }
        
        return meta_map;
    }
    
    @Override
    public void updateMetaData(T data, MetaData metaData) 
    {
        String key = getKey(data);
        
        if (data != null && key != null && 
            metaData != null && metaData.getFieldName() != null)
        {
            Map<String, MetaData> meta_map = getMetaDataMap(key);
            
            if (meta_map != null)
                meta_map.put( metaData.getFieldName(), metaData );
        }
    }
    
    @Override
    public void updateMetaData(T data, Collection<MetaData> metaDataCollection)
    {
        String key = getKey(data);
        if (data != null && key != null && metaDataCollection != null)
        {
            Map<String, MetaData> meta_map = getMetaDataMap(key);
            
            if (meta_map != null)
            {
                for (MetaData metaData : metaDataCollection)
                {
                    meta_map.put( metaData.getFieldName(), metaData );
                }
            }
        }
    }
    
    @Override
    public void addAll(Collection<T> dataList) 
    {
        if (dataList != null && !dataList.isEmpty())
        {
            for (T data : dataList)
                add (data);
        }
    }
    
    @Override
    public void remove(T data) 
    {
        String key = getKey(data);
        if (data != null && key != null)
        {
            removeByKey (key);
        }
    }
    
    @Override
    public void removeByKey (String key)
    {
        if (key != null)
        {
            metaDataStore.remove(key);
            
            if (store.containsKey(key))
            {
                T data = store.remove(key);
                notifyFuserStoreListeners(FuserStoreEventType.REMOVE, data);
            }
        }
    }
    
    @Override
    public void removeAll(Collection<T> dataList)
    {
        if (dataList != null)
        {
            for (T data : dataList)
                remove (data);
        }
    }
    
    @Override
    public T get(Object key) 
    {    
        T data = null;
        
        if (key != null)
            data = store.get(key);
        
        return data;
    }
    
    @Override
    public MetaData getMetaData(T data, String fieldName) 
    {
        String key = getKey(data);
        if (data != null && key != null && fieldName != null)
        {
            Map<String, MetaData> meta_data = metaDataStore.get(key);
            if(meta_data != null)
            {
                return( meta_data.get(fieldName));
            }
        }
        
        return null;
    }
    
    @Override
    public Collection<T> getAll()
    {
        return new ArrayList<>(store.values());
    }

    @Override
    public Collection<MetaData> getAllMetaData(T data) 
    {
        String key = getKey(data);
        if (data != null && key != null)
        {
            Map<String, MetaData> meta_data = metaDataStore.get(key);
            if( meta_data != null )
            {
                return meta_data.values();
            }
        }
        
        return null;
    }
    
    @Override
    public int size()
    {
        return store.size();
    }
    
    @Override
    public int metaDataSize(T data)
    {   
        String key = getKey(data);
        if (data != null && key != null)
        {        
            Map<String, MetaData> meta_data = metaDataStore.get(key);
            if( meta_data != null )
            {
                return meta_data.size();
            }
        }
        
        return 0;
    }
    
    @Override
    public void clear()
    {
        store.clear();
        metaDataStore.clear();
    }
    
    @Override
    public String getKey(T data)
    {
        if(idLookup != null)
            return idLookup.getIdentifier(data);
        return null;
    }
    
    public void setIdLookup(MatmIdLookup<T, String> idLookup)
    {
        this.idLookup = idLookup;
    }
}
