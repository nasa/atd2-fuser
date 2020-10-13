package com.mosaicatm.fuser.common.matm.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MatmIdLookupFactory
implements MatmIdLookup<Object, String>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private Map<String, MatmIdLookup<Object, String>> factory;
    
    public MatmIdLookupFactory()
    {
        factory = new HashMap<>();
    }
    
    @Override
    public String getIdentifier(Object data)
    {                
        String id = null;
        
        if (data != null)
        {
            MatmIdLookup<Object, String> lookup = factory.get(data.getClass().getName());
            
            if (lookup != null)
            {
                id = lookup.getIdentifier(data);
            }
        }
        
        return id;
    }
    
    @SuppressWarnings("unchecked")
    public void setLookups(Map<String, MatmIdLookup<? extends Object, String>> factory)
    {
        if (factory != null)
        {
            for (String key : factory.keySet())
            {
                this.factory.put(key, (MatmIdLookup<Object, String>)factory.get(key));
            }
        }
    }
}
