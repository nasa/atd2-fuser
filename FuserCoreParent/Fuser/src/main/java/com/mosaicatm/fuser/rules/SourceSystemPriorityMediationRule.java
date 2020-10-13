package com.mosaicatm.fuser.rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.common.MetaData;

public class SourceSystemPriorityMediationRule<T extends MatmObject> 
extends AbstractRule<T>
{
    private final Log log = LogFactory.getLog( getClass() );

    private Set<String> updateSourceFilter;
    private String favorSystemIdSuffix;
    private MatmIdLookup<Object, String> idLookup;
    private Class ruleClassType;

    @Override
    public Class getRuleClassType()
    {
        return( ruleClassType );
    }      
    
    @Override
    protected boolean handleDifferences( T update, T target, MetaData history, String field )
    {
        //Apply optional update source filter
        //Favor order: specified system ID suffix
        //when filter out a field, return true
        String updateSource = update.getLastUpdateSource();

        if( ( updateSourceFilter == null )
                || (( updateSource != null ) && updateSourceFilter.contains( updateSource )))
        {
            String updateSystem = update.getSystemId();
            String oldSystem = history.getSystemType();

            if(( updateSystem != null ) && updateSystem.endsWith( favorSystemIdSuffix ))
            {
                return true;
            }

            if(( oldSystem != null ) && oldSystem.endsWith( favorSystemIdSuffix ))
            {
                if( log.isDebugEnabled() )
                {
                    String identifier;
                    if( idLookup != null )
                    {
                        identifier = idLookup.getIdentifier(update);
                    }
                    else
                    {
                        identifier = "NULL idLookup";
                    }                    
                    
                    log.debug( "Filtering " + identifier + " " + field + 
                            " update system:" + updateSystem + " existing system:" + oldSystem );
                }
                filterUpdateField( update, field );
                return false;
            }
        }

        return true;
    }
    
    public void setRuleClassType( Class ruleClassType )
    {
        this.ruleClassType = ruleClassType;
    }
    
    @Override
    protected boolean handleIdenticals(T update, T target, MetaData history, String field)
    {
        String updateSource = update.getLastUpdateSource();

        if( ( updateSourceFilter == null )
                || (( updateSource != null ) && updateSourceFilter.contains( updateSource )))
        {
            String updateSystem = update.getSystemId();
            String oldSystem = history.getSystemType();

            if(( updateSystem != null ) && updateSystem.endsWith( favorSystemIdSuffix ))
            {
                return true;
            }
            
            if(( oldSystem != null ) && oldSystem.endsWith( favorSystemIdSuffix ))
            {
                filterUpdateField( update, field );
                return false;
            }

        }

        // return true if no source priority matters
        // so metaData can be updated
        return true;
    }


    public void setUpdateSources( Collection<String> updateSources )
    {
        this.updateSourceFilter = null;

        if(( updateSources != null ) &&  !updateSources.isEmpty() )
        {
            this.updateSourceFilter = new HashSet<>();
            for( String include : updateSources )
            {
                updateSourceFilter.add( include );
            }
        }
    }

    public void setFavorSystemIdSuffix( String favorSystemIdSuffix )
    {
        this.favorSystemIdSuffix = favorSystemIdSuffix;
    }

    public void setIdLookup(MatmIdLookup<Object, String> idLookup)
    {
        this.idLookup = idLookup;
    }
}
