package com.mosaicatm.fuser.aggregator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.rules.RuleFactory;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.util.PropertyVisitor;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.common.MetaData;

public class MetaDataManager <T extends MatmObject>
extends PropertyVisitor
{
	private final Log log = LogFactory.getLog(getClass());
	
    private FuserStore<T,MetaData> store;
    private RuleFactory<T> ruleFactory;
    
    public FuserStore<T,MetaData> getFuserStore()
    {
        return store;
    }
    
    public void setFuserStore( FuserStore<T,MetaData> store)
    {
        this.store = store;
    }    

    public RuleFactory<T> getRuleFactory()
    {
        return ruleFactory;
    }

    public void setRuleFactory(RuleFactory<T> ruleFactory)
    {
        this.ruleFactory = ruleFactory;
    }    
    
    public int applyRules(T update, T target, AggregationResult result)
    {
        int totalApplied = 0;
        
        String key = store.getKey(update);
        
        if (result == null || result.getChanges() == null
            || update == null || key == null)
        {
            return totalApplied;
        }

        try
        {
            store.lockStore( update );
            if ( store.metaDataSize( update ) <= 0 )
            {    
                create(result.getChanges(), update);
                create(result.getIdenticals(), update);
            }
            else
            {
                // First, we run the changed data elements through the rules that
                // mediate the data element values coming from multiple data sources.
                // The rules may filter out updates to data elements due to 
                // higher priority sources. When updates are applied, we track
                // the data source with meta data.
                List<MetaData> metaDataCollection = new ArrayList<>();
                
                List<String> applied = new ArrayList<String>();
                MetaData data = null;
                
                for (String fieldName : result.getChanges())
                {
                    data = store.getMetaData( update, fieldName );
                    if (data != null && ruleFactory != null)
                    {
                        // when true, we will update the meta data
                        if (ruleFactory.handleDifference(update, target, data, fieldName))
                        {
                            metaDataCollection.add(generateMetaData(update, fieldName));
                        }
                        else
                        {
                            totalApplied++;
                            applied.add(fieldName);
                        }
                    }
                    else
                    {
                        metaDataCollection.add(generateMetaData(update, fieldName));
                    }
                }
                
                result.getChanges().removeAll(applied);
                
                // Now, we still need to check the unchanged (identical) data elements.
                // This step is needed to keep our meta data history up to date, in the case 
                // that a higher priority source sets the same value.
                if (result.getIdenticals() != null && !result.getIdenticals().isEmpty())
                {
                    for (String fieldName : result.getIdenticals())
                    {
                        data = store.getMetaData( update, fieldName );
                        if (data != null && ruleFactory != null)
                        {
                            if (ruleFactory.handleIdentical(update, target, data, fieldName))
                            {
                                // if no rules prevent identical, update existing record
                                // otherwise, no change
                                metaDataCollection.add(generateMetaData(update, fieldName));
                            }
                        }
                        else
                        {
                            metaDataCollection.add(generateMetaData(update, fieldName));
                        }
                    }
                }

                if (!metaDataCollection.isEmpty())
                {
                    store.updateMetaData(update, metaDataCollection);
                }
            }
        }
        finally
        {
            store.unlockStore( update );
        }
        return totalApplied;
    }
    
    public void createRecord(T update) 
    throws IllegalAccessException, InvocationTargetException, 
            NoSuchMethodException, IllegalArgumentException
    {
        Set<String> changes = new TreeSet<String>();
        addChildren(update, changes, "");
        
        create(changes, update);
    }
    
    private void create(Set<String> changes, T update)
    {
        if (changes == null)
            return;
        
        try
        {
            store.lockStore( update );
            
            List<MetaData> metaDataCollection = new ArrayList<MetaData>();
            for (String field : changes)
            {
                MetaData data = generateMetaData(update, field);
                metaDataCollection.add(data);
            }
            store.updateMetaData(update, metaDataCollection);
        }
        finally
        {
            store.unlockStore( update );
        }
    }

    private MetaData generateMetaData(T update, String fieldName)
    {
        MetaData metaData = new MetaData();
        metaData.setFieldName(fieldName);
        metaData.setSource(update.getLastUpdateSource());
        metaData.setSystemType(update.getSystemId());
        
        Date updateTime = update.getTimestamp();
        
        if (updateTime == null)
        {
	        log.warn(store.getKey(update) + 
					 " does not have timestamp for source " + 
					 update.getLastUpdateSource() + 
					 " defaulting to fuser received timestamp");
	        
	        updateTime = update.getTimestampFuserReceived();
        }
        
        // timestamp cannot be later than the received time
        if(( update.getTimestampFuserReceived() != null ) && 
                updateTime.after( update.getTimestampFuserReceived() ))
        {
            updateTime = update.getTimestampFuserReceived();
        }
        
        metaData.setTimestamp(updateTime);

        return metaData;
    }
}
