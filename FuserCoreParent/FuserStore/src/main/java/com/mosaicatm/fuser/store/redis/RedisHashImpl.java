package com.mosaicatm.fuser.store.redis;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;

public class RedisHashImpl
implements RedisHash
{
    private final Log log = LogFactory.getLog(getClass());
    
    private RedisDB db;
    private KeyGenerator keyGen;
    private RedisBulkTask bulkTask;
    
    public RedisHashImpl ()
    {
        this (null, null);
    }
    
    public RedisHashImpl (RedisDB db)
    {
        this (db, null);
    }
    
    public RedisHashImpl(RedisDB db, KeyGenerator keyGen)  
    {
        this.db = db;
        this.keyGen = keyGen;
    }
    
    @Override
    public void set(String key, String body) 
    {    
        if (bulkTask != null)
        {
            bulkTask.addUpdate(keyGen.getGlobalHashKey(), key.toLowerCase(), body);
        }
    }
    
    @Override
    public void setMetaData(String key, String metaProperty, String body) 
    {    
        if (bulkTask != null)
        {
            bulkTask.addUpdate(keyGen.getGufiToMetaDataHashKey(key), metaProperty.toLowerCase(), body);
        }
    }
    
    @Override
    public void setMetaData(String key, Map<String, String> updates)
    {
        if (bulkTask != null)
        {
            bulkTask.addUpdate(keyGen.getGufiToMetaDataHashKey(key), updates);
        }
    }

    @Override
    public boolean keyExists(String key)
    {
        try (Jedis jedis = db.getClient()) 
        {    
            return jedis.hexists(keyGen.getGlobalHashKey(), key.toLowerCase());
        }
        catch (Exception e)
        {
            log.error ("Error checking if key exists in redis hash " + key, e);
        }
        
        return false;
    }

    @Override
    public String get(String key)
    {
        try (Jedis jedis = db.getClient())
        {
            return jedis.hget(keyGen.getGlobalHashKey(), key.toLowerCase());
        }
        catch (Exception e)
        {
            log.error ("Error getting redis hash entry " + key, e);
        }
        
        return null;
    }
    
    @Override
    public String getMetaData(String key, String property)
    {
        try (Jedis jedis = db.getClient())
        {
            return jedis.hget(keyGen.getGufiToMetaDataHashKey( key ), property.toLowerCase());
        }
        catch (Exception e)
        {
            log.error ("Error getting redis hash entry " + key, e);
        }
        
        return null;
    }    
    
    @Override
    public void delete(String key)
    {
        
        if (bulkTask != null)
        {
            bulkTask.addDelete(keyGen.getGlobalHashKey(), key.toLowerCase());
            bulkTask.addDelete(keyGen.getGufiToMetaDataHashKey( key ));
        }
    }
    
    @Override
    public Long length() 
    {
        try (Jedis jedis = db.getClient()) 
        {
            return jedis.hlen(keyGen.getGlobalHashKey());
        }
        catch (Exception e)
        {
            log.error ("Error getting redish hash length", e);
        }
        
        return null;
    }
    
    @Override
    public Long lengthMetaData(String key)
    {
        try (Jedis jedis = db.getClient()) 
        {
            return jedis.hlen(keyGen.getGufiToMetaDataHashKey( key ));
        }
        catch (Exception e)
        {
            log.error ("Error getting redish hash length", e);
        }
        
        return null;
    }    
    
    @Override
    public void clearAll()
    {
        // flushAll wipes everything from all redis databases - including those used by other components
        //db.flushAll();
        try
        {
            db.clearNamespace( keyGen.getGlobalNamespace() );
        }
        catch (Exception e)
        {
            log.error("Error clearing namespace");
        }
    }
    
    @Override
    public Set<String> getKeys() 
    {
        try (Jedis jedis = db.getClient()) 
        {
            return jedis.hkeys(keyGen.getGlobalHashKey());
        }
        catch (Exception e)
        {
            log.error ("Error retrieving redish hash keys", e);
        }
        
        return null;
    }

    @Override
    public Map<String, String> getAll()
    {
        try (Jedis jedis = db.getClient()) 
        {
            return jedis.hgetAll(keyGen.getGlobalHashKey());
        }
        catch (Exception e)
        {
            log.error ("Error retrieving redish hash keys", e);
        }
        
        return null;
    }
    
    @Override
    public Collection<String> getAllMetaData(String key)
    {
        try (Jedis jedis = db.getClient()) 
        {
            Map<String, String> map = jedis.hgetAll( keyGen.getGufiToMetaDataHashKey( key ));
            if( map != null )
            {
                return( map.values() );
            }
        }
        catch (Exception e)
        {
            log.error ("Error retrieving redish hash keys", e);
        }
        
        return null;
    }    
    
    public void setDb (RedisDB db)
    {
        this.db = db;
    }
    
    public RedisDB getDb() 
    {
        return db;
    }

    public void setKeyGen (KeyGenerator keyGen)
    {
        this.keyGen = keyGen;
    }
    
    public KeyGenerator getKeyGen() 
    {
        return keyGen;
    }

    public void setBulkTask(RedisBulkTask bulkTask)
    {
        this.bulkTask = bulkTask;
    }
    
}
