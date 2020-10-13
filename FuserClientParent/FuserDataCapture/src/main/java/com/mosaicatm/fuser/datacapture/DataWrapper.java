package com.mosaicatm.fuser.datacapture;

import java.util.ArrayList;
import java.util.List;

import com.mosaicatm.fuser.datacapture.store.IdSet;

public class DataWrapper<T>
{
    private final T data;
    
    private final List<CaptureType> types;
    
    private final IdSet idSet;
    
    public DataWrapper(T data)
    {
        this(data, null);
    }
    
    public DataWrapper(T data, IdSet idSet)
    {
        this.data = data;
        this.idSet = idSet;
        types = new ArrayList<>();
    }

    /**
     * Get a copy of the capture types
     * @return the a list of capture types
     */
    public List<CaptureType> getCaptureTypes()
    {
        return new ArrayList<>(types);
    }

    /**
     * Add this type to the list of capture types if not null and not included
     * 
     * @param type the capture type to add
     */
    public void addCaptureType(CaptureType type)
    {
        if (type != null && !contains(type))
        {
            types.add(type);
        }
    }
    
    /**
     * Check if this type is contained this list of capture types
     * @param type the type to check
     * @return true if containing the type, false otherwise
     */
    public boolean contains(CaptureType type)
    {
        if (type != null)
        {
            return types.contains(type);
        }
        
        return false;
    }
    
    /**
     * Add a list of types to the current list of capture types,
     * only none-null and none-existing type will be added.
     * 
     * @param types the list of capture types to add
     */
    public void addCaptureTypes(List<CaptureType> types)
    {
        if (types != null)
        {
            for (CaptureType type : types)
            {
                addCaptureType(type);
            }
        }
    }

    /**
     * @return the flight
     */
    public T getData()
    {
        return data;
    }

    /**
     * @return the idSet
     */
    public IdSet getIdSet()
    {
        return idSet;
    }
    
}
