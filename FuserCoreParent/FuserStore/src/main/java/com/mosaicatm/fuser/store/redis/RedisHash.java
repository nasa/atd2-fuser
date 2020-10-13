package com.mosaicatm.fuser.store.redis;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface RedisHash 
{
    public void setMetaData(String key, String metaProperty, String body);
    public void setMetaData(String key, Map<String, String> updates);
    public String get(String key);
    public String getMetaData(String key, String property);
    public Map<String, String> getAll();
    public Collection<String> getAllMetaData(String key);
    
    public void set(String key, String body);
    public void delete(String key);

    public Set<String> getKeys();
    public boolean keyExists(String key);
    
    public Long length();
    public Long lengthMetaData(String key);
    
    public void clearAll(); 
}