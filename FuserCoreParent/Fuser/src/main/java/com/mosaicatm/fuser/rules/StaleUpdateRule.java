package com.mosaicatm.fuser.rules;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.common.MetaData;

public class StaleUpdateRule<T extends MatmObject> 
extends AbstractRule<T>
{
    //Default maximum allowable amount of staleness for an update's timestamp when the source is the same
    public final static long DEFAULT_SAME_SOURCE_ALLOWABLE_STALENESS_MILLIS = 500L;
    
    //Default maximum allowable amount of staleness for an update's timestamp when the source is different
    //  Note: some sources could be on delay intentionally -- e.g. TFMS could be on a 5 minute delay
    //  If we did have a single source or set of sources on delay, it could be a good idea to improve this 
    //  implementation to use a different staleness value for individual sources.
    public final static long DEFAULT_DIFFERENT_SOURCE_ALLOWABLE_STALENESS_MILLIS = 5 * 60 * 1000 + 500L;
    
    private long sameSourcesAllowableStalenessMillis = DEFAULT_SAME_SOURCE_ALLOWABLE_STALENESS_MILLIS;
    private long differentSourcesAllowableStalenessMillis = DEFAULT_DIFFERENT_SOURCE_ALLOWABLE_STALENESS_MILLIS;
    
    private MatmIdLookup<Object, String> idLookup;
    
    private Class ruleClassType;
    
    private final Log log = LogFactory.getLog(getClass());

    public long getDifferentSourcesAllowableStalenessMillis()
    {
        return differentSourcesAllowableStalenessMillis;
    }

    public void setDifferentSourcesAllowableStalenessMillis( long differentSourcesAllowableStalenessMillis )
    {
        this.differentSourcesAllowableStalenessMillis = differentSourcesAllowableStalenessMillis;
    }

    public long getSameSourcesAllowableStalenessMillis()
    {
        return sameSourcesAllowableStalenessMillis;
    }

    public void setSameSourcesAllowableStalenessMillis( long sameSourcesAllowableStalenessMillis )
    {
        this.sameSourcesAllowableStalenessMillis = sameSourcesAllowableStalenessMillis;
    }
    
    @Override
    public Class getRuleClassType()
    {
        return( ruleClassType );
    }     
        
    @Override
    public boolean ruleAppliesToField( String field )
    {
        return( isActive() );
    }
    
    public void setRuleClassType( Class ruleClassType )
    {
        this.ruleClassType = ruleClassType;
    }
    
    @Override
    protected boolean handleDifferences(T update, T target, MetaData history, String field)
    {
        return( filterOutStaleUpdate( update, history, field ));
    }
    
    @Override
    protected boolean handleIdenticals(T update, T target, MetaData history, String field)
    {
        return( filterOutStaleUpdate( update, history, field ));
    }

    private boolean filterOutStaleUpdate(T update, MetaData history, String field)
    {
        if(( history != null ) && ( history.getTimestamp() != null ) && ( update.getTimestamp() != null ))
        {
            boolean sourcesEqual = Objects.equals( update.getLastUpdateSource(), history.getSource() ) && 
                    Objects.equals( update.getSystemId(), history.getSystemType() );

            long minUpdateTime;
            
            if( sourcesEqual )
                minUpdateTime = history.getTimestamp().getTime() - sameSourcesAllowableStalenessMillis;
            else
                minUpdateTime = history.getTimestamp().getTime() - differentSourcesAllowableStalenessMillis;

            if( update.getTimestamp().getTime() < minUpdateTime )
            {
                // If the update's timestamp is older than the last update time, filter it out
                if (log.isDebugEnabled()) 
                {
                    log.debug( "Filtering stale update for " + idLookup.getIdentifier(update) + 
                            ", field=[" + field + "]" +
                            ", source=[" + update.getLastUpdateSource() + "/" + update.getSystemId() + "]" +
                            ", historySource=[" + history.getSource() + "/" + history.getSystemType() + "]" +
                            ", sourcesEqual=[" + sourcesEqual + "]" +
                            ", sourceTimestamp=[" + update.getTimestamp() + "]" +
                            ", historyTimestamp=[" + history.getTimestamp() + "]" +
                            ", staleness=[" + ( history.getTimestamp().getTime() - update.getTimestamp().getTime() ) + "].");
                }

                filterUpdateField(update, field);

                return false;
            }
        }
        
        return true;        
    }
    
    public void setIdLookup(MatmIdLookup<Object, String> idLookup)
    {
        this.idLookup = idLookup;
    }
}
