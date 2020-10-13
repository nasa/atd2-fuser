package com.mosaicatm.fuser.updaters.pre;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.fuser.util.TimestampSetter;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.common.MetaData;

public class SourceUpdater<T extends MatmObject>
extends AbstractUpdater<T, T> 
{
    private final Log log = LogFactory.getLog(getClass());
    
    private TimestampSetter timestampSetter;
    private MatmIdLookup<Object, String> idLookup;
    
    @Override
    public void update (T update, T target)
    {    
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update source. Update is NULL!");
            return;
        }
        
        if( target == null )
        {
            target = update;
        }          

        //updateSources are based on lastUpdateSource and systemID
        //the list should only ever contain 1 value per lastUpdateSource/systemID
        
        if (idLookup != null && idLookup.getIdentifier(update) != null)
        {
            List<MetaData> updateSources = new ArrayList<MetaData>();
            
            updateSources = new ArrayList<>(target.getUpdateSources());
            
            MetaData updateSource = createUpdateSource(update);
            
            if (updateSource != null && !updateIfExist(updateSources, updateSource)) 
            {
                updateSources.add(updateSource);
            }
    
            update.setUpdateSources(updateSources);
    
            // update the process time
            if (timestampSetter != null)
            {
                timestampSetter.updateTimeFuserProcessed(update);
                target.setTimestampFuserProcessed(update.getTimestampFuserProcessed());
            }
        }
    }
    
    private MetaData createUpdateSource(T update) 
    {
        if (update.getLastUpdateSource() == null)
            return null;
        
        MetaData updateSource = new MetaData();
        updateSource.setSource(update.getLastUpdateSource());
        updateSource.setSystemType(update.getSystemId());
        
        Date updateTime = update.getTimestamp();
        
        if (updateTime == null)
        {
            log.warn(idLookup.getIdentifier(update) + 
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
        
        updateSource.setTimestamp(updateTime);
        
        return updateSource;
    }
    
    private boolean updateIfExist(List<MetaData> targetSources, MetaData updateMetaData)
    {
        if (targetSources == null || targetSources.size() == 0)
            return false;
        
        String updateSource = updateMetaData.getSource();
        String updateSystemType = updateMetaData.getSystemType();

        //require source to continue
        if (updateSource == null)
            return true;
        
        for (MetaData target : targetSources)
        {
            if (updateSource.equals(target.getSource()) && 
                    Objects.equals( updateSystemType, target.getSystemType() ))
            {
                target.setTimestamp(updateMetaData.getTimestamp());
                return true;
            }
        }
        
        return false;
    }

    public void setTimestampSetter(TimestampSetter timestampSetter)
    {
        this.timestampSetter = timestampSetter;
    }

    public void setIdLookup(MatmIdLookup<Object, String> idLookup)
    {
        this.idLookup = idLookup;
    }
}
