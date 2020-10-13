package com.mosaicatm.fuser.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.fuser.store.event.FuserStoreEvent.FuserStoreEventType;
import com.mosaicatm.fuser.store.redis.RedisHash;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.common.MetaData;

public abstract class GenericRedisFuserStore<T>
extends AbstractFuserStore<T, MetaData>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private GenericMarshaller metaDataMarshaller;
    private MatmIdLookup<T, String> idLookup;
    
    private RedisHash redisHash;
    
    protected GenericRedisFuserStore (String name, int numberOfLocks)
    {
        this(name, null, numberOfLocks);
    }
    
    protected GenericRedisFuserStore (String name, RedisHash redisHash, int numberOfLocks)
    {
        super(name, numberOfLocks);
        this.redisHash = redisHash;
    }
    
    public abstract String toXML(T data);
    public abstract T fromXML(String xml);
    
    @Override
    public void add(T data)
    {
        if (!canProceed())
            return;
        
        String key = getKey(data);
        if(data != null && key != null) 
        {
            try 
            {                
                if (log.isDebugEnabled())
                    log.debug("create:" + key);
                
                String xml = toXML(data);
                redisHash.set(key, xml);
            
                notifyFuserStoreListeners(FuserStoreEventType.ADD, data);    

            } 
            catch (Exception e)
            {
                log.error("Unable to create " + data, e);
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
    public void update(T data)
    {
        if (!canProceed())
            return;
        
        String key = getKey(data);
        if (data != null && key != null )
        {
            String xml = toXML(data);
            redisHash.set(key, xml);
            
            notifyFuserStoreListeners(FuserStoreEventType.UPDATE, data);    
        }        
    }
    
    @Override
    public void updateMetaData(T data, MetaData metaData) 
    {
        if (!canProceed())
            return;        
        
        String key = getKey(data);
        if (data != null && key != null)
        {
            try 
            {                
                if (log.isDebugEnabled())
                    log.debug("updateMetaData:" + key);
                
                String xml = toMetaDataXML( metaData );
                redisHash.setMetaData( key, metaData.getFieldName(), xml );
            } 
            catch (Exception e)
            {
                log.error("Unable to update MetaData", e);
            }
        }
    }
    
    @Override
    public void updateMetaData(T data, Collection<MetaData> metaDataCollection)
    {
        if (!canProceed())
            return;
        
        String key = getKey(data);
        if (data != null && key != null)
        {
            try 
            {                
                if (log.isDebugEnabled())
                    log.debug("updateMetaData:" + key);
                
                Map<String, String> updates = new HashMap<>();
                
                for (MetaData metaData : metaDataCollection)
                {
                    String xml = toMetaDataXML( metaData );
                    if (xml != null)
                        updates.put(metaData.getFieldName().toLowerCase(), xml);
                }
                if (!updates.isEmpty())
                    redisHash.setMetaData( key, updates );
            } 
            catch (Exception e)
            {
                log.error("Unable to update MetaData", e);
            }
        }
    }
    
    @Override
    public void remove(T data)
    {
        if (!canProceed())
            return;
        
        String key = getKey(data);
        if (data != null && key != null)
        {
            removeByKey (key);
        }
    }
    
    @Override
    public void removeAll(Collection<T> dataList)
    {
        if (dataList != null && !dataList.isEmpty())
        {
            for (T data : dataList)
                remove (data);
        }
    }
    
    @Override
    public void removeByKey(String key)
    {
        if (!canProceed())
            return;
        
        if (key != null)
        {
            if (redisHash.keyExists(key))
            {
                T data = fromXML(redisHash.get(key));
                redisHash.delete(key);
                
                notifyFuserStoreListeners(FuserStoreEventType.REMOVE, data);
            }
        }
        
    }
    
    @Override
    public T get(Object key)
    {
        if (!canProceed())
            return null;
        
        T data = null;
        
        if (key != null && redisHash.keyExists(key.toString()))
        {
            String xml = redisHash.get(key.toString());
            data = fromXML(xml);
        }
        return data;
    }
    
    @Override
    public Collection<T> getAll()
    {
        if (!canProceed())
            return null;
        
        long readBeginTime = System.currentTimeMillis();
        Map<String, String> data = redisHash.getAll();
        
        int size = (data != null) ? data.size() : 0; 
        log.info("Retrieval Process: Time to get " + size + " entries " + ( System.currentTimeMillis() - readBeginTime ));
        
        if (data == null)
            return null;
        
        Collection<T> dataList = new ArrayList<> ();
        long xmlBeginTime = 0; 
        long xmlTotalMarshalTime = 0;
        
        for (String xml : data.values())
        {
            xmlBeginTime = System.currentTimeMillis();
            
            T value = fromXML(xml);
            
            xmlTotalMarshalTime += ( System.currentTimeMillis() - xmlBeginTime ); 
            
            if (value != null)
                dataList.add(value);
            
        }
        
        log.info("Retrieval Process: Time to marshal xml " + xmlTotalMarshalTime);
        log.info("Retrieval Process: Total time to convert " + ( System.currentTimeMillis() - readBeginTime ));
        
        return dataList;
    }
    
    @Override
    public MetaData getMetaData(T data, String propertyName) 
    {
        MetaData meta = null;
        
        if (!canProceed())
            return null;        
        
        String key = getKey(data);
        if (data != null && key != null)
        {
            String xml = redisHash.getMetaData( key, propertyName);
            
            if( xml != null )
            {
                meta = fromMetaDataXML( xml );
            }
        }
        
        return( meta );
    }
    
    @Override
    public Collection<MetaData> getAllMetaData(T data)
    {
        if (!canProceed())
            return null;
        
        ArrayList<MetaData> meta_list = null;
        
        String key = getKey(data);
        if (data != null && key != null)
        {        
            Collection<String> meta_collection = redisHash.getAllMetaData(key);
            
            if( meta_collection != null )
            {
                meta_list = new ArrayList<> ();
                for( String xml : meta_collection )
                {
                    MetaData meta = fromMetaDataXML( xml );
                    
                    if( meta != null )
                    {
                        meta_list.add( meta );
                    }
                }
            }
        }
        
        return( meta_list );
    }
    
    @Override
    public int size()
    {
        if (canProceed())
        {
            Long size = redisHash.length();
            
            if (size != null)
                return size.intValue();
        }
        return 0;
    }
    
    @Override
    public int metaDataSize(T data)
    {
        if (canProceed())
        {
            String key = getKey(data);
            if (data != null && key != null)
            {             
                Long size = redisHash.lengthMetaData(key);

                if (size != null)
                    return size.intValue();
            }
        }
        return 0;
    }
    
    @Override
    public void clear()
    {
        if (redisHash != null){
            redisHash.clearAll();
        }
    }
    
    @Override
    public String getKey(T data)
    {
        if (idLookup != null)
            return idLookup.getIdentifier(data);
        return null;
    }
    
    private String toMetaDataXML(MetaData metaData)
    {
        try 
        {
            return( metaDataMarshaller.marshall( metaData ));
        } 
        catch (Exception e) 
        {
            log.error("MetaData to XML error", e);
        }
        
        return( null );
    }
    
    private MetaData fromMetaDataXML(String xml)
    {
        try 
        {
            return (MetaData) metaDataMarshaller.unmarshall(xml);
        } 
        catch (Exception e) 
        {
            log.error("XML to MetaData error", e);
        }
        
        return( null );
    }    
    
    protected boolean canProceed()
    {
        if (redisHash == null)
        {
            log.error("Can't proceed, redis Hash is null");
            return false;
        }
        else
        {
            return true;
        }
    }
    
    public void setRedisHash(RedisHash redisHash)
    {
        this.redisHash = redisHash;
    }

    public void setMetaDataMarshaller( GenericMarshaller metaDataMarshaller )
    {
        this.metaDataMarshaller = metaDataMarshaller;
    }
    
    public void setIdLookup(MatmIdLookup<T, String> idLookup)
    {
        this.idLookup = idLookup;
    }
}
