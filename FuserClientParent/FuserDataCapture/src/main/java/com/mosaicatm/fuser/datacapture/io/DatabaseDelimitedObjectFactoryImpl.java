package com.mosaicatm.fuser.datacapture.io;

import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mosaicatm.container.io.DelimitedObjectWriter;
import com.mosaicatm.fuser.datacapture.CaptureType;
import com.mosaicatm.fuser.datacapture.db.DatabaseNameFactory;
import com.mosaicatm.lib.database.bulk.BulkDriver;

public class DatabaseDelimitedObjectFactoryImpl
extends DelimitedObjectFactoryImpl
{
    
    private Map<CaptureType, List<String>> includedFieldMap;
    private DatabaseNameFactory databaseNameFactory;
    private BulkDriver bulkDriver;
    
    public DatabaseDelimitedObjectFactoryImpl ()
    {
        includedFieldMap = new HashMap<>();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> DelimitedObjectWriter<T> getDelimitedObjectWriter(CaptureType type, Writer writer)
    {
        DelimitedObjectWriter<?> delim = super.getDelimitedObjectWriter(type, writer, getFields(type));
                
        return (DelimitedObjectWriter<T>)delim;
    }
    
    private Set<String> getFields (CaptureType type)
    {
        
        List<String> includedFields = getIncludedFields(type);
        Set<String> fields = new LinkedHashSet<>();
        
        if (includedFields != null && !includedFields.isEmpty())
        {
            fields.addAll(includedFields);
        }
        
        return fields;
    }
    
    private List<String> getIncludedFields (CaptureType type)
    {        
        if (includedFieldMap != null &&
            !includedFieldMap.containsKey(type))
        {
            if (type != null && bulkDriver != null && databaseNameFactory != null)
            {
                List<String> included = bulkDriver.getIncludedFields(databaseNameFactory.getDatabaseName(type));
                includedFieldMap.put(type, included);
            }
        }
        
        return includedFieldMap.get(type);
    }
    
    public void setDatabaseNameFactory (DatabaseNameFactory databaseNameFactory)
    {
        this.databaseNameFactory = databaseNameFactory;
    }
    
    public void setBulkDriver(BulkDriver bulkDriver)
    {
        this.bulkDriver = bulkDriver;
    }
}
