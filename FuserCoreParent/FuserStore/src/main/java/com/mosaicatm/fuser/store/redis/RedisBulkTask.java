package com.mosaicatm.fuser.store.redis;

import java.util.Map;

public interface RedisBulkTask
{
    public void addUpdate(String key, String field, String body);
    
    public void addUpdate(String key, Map<String, String> updates);
    
    public void addDelete(String key, String... fields);
    
    public int updateSize();
    
    public int removeSize();
}
