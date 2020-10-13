package com.mosaicatm.fuser.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.common.FuserSourceSystemType;
import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.common.MetaData;

/**
 * This class allows for a heirarchy of data sources for a given field(s). If the 
 * field is avaiable from the first source, it will be used. If not, this rule
 * will check if the field is avaiable from the second source and so until.
 * 
 * If the field is not avaiable from any of the set sources, it will take the
 * field from any source.
 */
public class MultiplePriorityMediationRule<T extends MatmObject> 
extends AbstractRule <T>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private volatile boolean errorLogged = false;
    
    private List<Set<FuserSourceSystemType>> sourcePriorityList = null;
    private MatmIdLookup<Object, String> idLookup;
    private Class ruleClassType;

    @Override
    public Class getRuleClassType()
    {
        return( ruleClassType );
    }
    
    /**
     * Set the ordered list of sources that will be given priority for this rule.
     * 
     * @param sourcePrioritySetList
     */
    public void setSourcePriorityList(List<Set<FuserSourceSystemType>> sourcePrioritySetList)
    {
        this.sourcePriorityList = new ArrayList<>();

        HashSet<FuserSourceSystemType> sourcePrioritySet = null;
        
        for( Set<FuserSourceSystemType> fuserSourceSystemTypeList : sourcePrioritySetList )
        {
            sourcePrioritySet = new HashSet<>();
            this.sourcePriorityList.add( sourcePrioritySet );
            
            for( FuserSourceSystemType fuserSourceSystemType : fuserSourceSystemTypeList )
            {
                sourcePrioritySet.add( fuserSourceSystemType );
            }
        }
    }
    
    /**
     * Expects to receive a list of source priorities in CSV format.  Sources
     * of equal priority should be grouped by square brackets.
     * 
     * e.g. TFM,[TMA,TMA_SWIM,TFM_TFDM],FMC,AIRLINE
     * 
     * @param csvSourcePriorityList
     */
    public void setSourcePriorityListFromCsv(String csvSourcePriorityList)
    {
        if (csvSourcePriorityList != null)
        {
            String[] priorities = csvSourcePriorityList.split(",");

            List<String> sourcePriorityList = new ArrayList<>();
            
            boolean isGroup = false;
            String group = "";
            for (String priority : priorities)
            {                
                priority = priority.trim().toUpperCase();
                
                if (priority.isEmpty())
                    continue;
                
                if (priority.startsWith("["))
                {
                    isGroup = true;
                    priority = priority.replaceAll("\\[", "");
                }
                else if (priority.endsWith("]"))
                {
                    isGroup = false;
                    priority = priority.replaceAll("\\]", "");
                }
                
                if (!group.isEmpty())
                    group += ",";
                
                group += priority;
                
                if (!isGroup)
                {
                    sourcePriorityList.add(group);
                    group = "";
                }
            }
            
            setSourcePriorityListFromString(sourcePriorityList);
        }
    }
    
    /**
     * Set the ordered list of sources that will be given priority for this rule.
     * 
     * @param sourcePriorityList
     */
    public void setSourcePriorityListFromString(List<String> sourcePriorityList)
    {
        ArrayList<Set<FuserSourceSystemType>> sourcePrioritySetListFromString = new ArrayList<>();

        if(( sourcePriorityList != null ) && ( !sourcePriorityList.isEmpty() ))
        {
            HashSet<FuserSourceSystemType> sourcePrioritySet = null;
            for( String fuserSourceSystemSet : sourcePriorityList )
            {
                if( !fuserSourceSystemSet.trim().isEmpty() )
                {
                    sourcePrioritySet = new LinkedHashSet<>();
                    sourcePrioritySetListFromString.add( sourcePrioritySet );

                    for( String sourceSystem : fuserSourceSystemSet.split( "," ))
                    {
                        sourcePrioritySet.add( FuserSourceSystemType.valueOf( sourceSystem.trim() ));
                    }
                }
            }
        }
        
        setSourcePriorityList( sourcePrioritySetListFromString );
    }        
    
    public void setIdLookup(MatmIdLookup<Object, String> idLookup)
    {
        this.idLookup = idLookup;
    }

    public void setRuleClassType( Class ruleClassType )
    {
        this.ruleClassType = ruleClassType;
    }
    
    @Override
    protected boolean handleDifferences(T update, T target,
        MetaData history, String field)
    {
        // If the priority order is not set, apply the update (i.e. do no filter
        // it out
        if (this.sourcePriorityList == null || this.sourcePriorityList.isEmpty()) 
        {
            if (!errorLogged)
            {
                log.error("No priority list set. No filtering of " + field + " will be done.");
                errorLogged = true;
            }
            return true;
        }
        
        // If there is no source meta data for the existing field, go ahead and 
        // apply the update (i.e. don't filter it out)
        if (history == null)
        {
            return true;
        }
        
        // Loop through the source hierarchy and determine if the update should
        // be applied
        Boolean result = null;
        for (Set<FuserSourceSystemType> sourceSystemType : sourcePriorityList)
        {
            result = checkFavoredSource(sourceSystemType, update, history);
            
            if (result != null) 
            {
                if (!result)
                {
                    filter(update, field, history);
                }
                
                return result;
            }
        }
        
        // If the sources heirarchy did not come into play, let the update
        // be applied
        return true;
    }
    
    @Override
    protected boolean handleIdenticals(T update, T target, MetaData history, String field)
    {
        // If the priority order is not set, apply the update
        if (this.sourcePriorityList == null || this.sourcePriorityList.isEmpty()) 
        {
            if (!errorLogged)
            {
                log.error("No priority list set. No filtering of " + field + " will be done.");
                errorLogged = true;
            }
            return true;
        }
        
        // If there is no source meta data for the existing field, go ahead and 
        // apply the update
        if (history == null)
        {
            return true;
        }
        
        // Loop through the source hierarchy and determine if the update should
        // be applied
        Boolean result = null;
        for (Set<FuserSourceSystemType> sourceSystemTypeSet : sourcePriorityList)
        {
            result = checkFavoredSource(sourceSystemTypeSet, update, history);
                
            if (result != null)
            {
                if (!result)
                {
                    filter(update, field, history);
                }                
                return result;
            }
            
        }
        
        // If the sources heirarchy did not come into play, let the update
        // be applied
        return true;
    }
    
    private Boolean checkFavoredSource(Set<FuserSourceSystemType> fuserSourceSystemSet,
            T update,  MetaData history )
    {
        Boolean cumulativeResult = null;
        Boolean result = null;
                
        for( FuserSourceSystemType systemSource : fuserSourceSystemSet )
        {
            result = checkFavoredSource( systemSource.getSource().name(), 
                    systemSource.getSystem(), update, history );
            
            if( result != null )
            {
                if( cumulativeResult == null )
                {
                    cumulativeResult = result;
                }
                else
                {
                    cumulativeResult = cumulativeResult || result;
                }
            }           
        }
        
        return( cumulativeResult );
    }    
    
    /**
     * Check if the field should be filtered based on the favored source and system
     * 
     * @param favoredSource  the source that should be favored
     * @param favoredSystemSuffix  the system that should be favored if the source can
     *                       have more than one system (e.g. AIRLINE source could be
     *                       either FLIGHTHUB or FLIGHTSTATS). This should be null
     *                       if the check should not consider the system
     * @param update   the MATM flight update
     * @param history  the source meta data for the existing flight 
     * 
     * @return null if no decision was made, true if it should be filtered, 
     *         false if the updated value should be kept
     */
    private Boolean checkFavoredSource(String favoredSource, String favoredSystemSuffix,
            T update,  MetaData history)
    {
        String oldSource = history.getSource();
        String oldSystem = history.getSystemType();
        
        String updateSource = update.getLastUpdateSource();
        String updateSystem = update.getSystemId();

        // NOTE: The favored system checks have to exist because the system is 
        // not nulled out for sources without a system. For example, the existing
        // source could be TFM with an existing system of FLIGHTHUB
        
        // If the updated data is from the favored source/system, use it (i.e.
        // don't filter it out)
        if (favoredSource.equals(updateSource) && favoredSystemSuffix == null) 
        {
            return true;
        }
        else if (favoredSource.equals(updateSource) && favoredSystemSuffix != null && 
            updateSystem != null && updateSystem.endsWith(favoredSystemSuffix))
        {
            return true;
        }
        
        // If the existing data is from the favored source/system, keep it (i.e.
        // filter out the updated data)
        else if (favoredSource.equals(oldSource) && favoredSystemSuffix == null)
        {
            return false;
        }
        else if (favoredSource.equals(oldSource) && favoredSystemSuffix != null &&
            oldSystem != null && oldSystem.endsWith(favoredSystemSuffix))
        {
            return false;
        }
        
        // Otherwise, return null indicating no decision can be made based on
        // this favored source/system. More checks may be needed
        return null;
    }
    
    private void filter(T update, String field, MetaData history)
    {
        String updateSource = update.getLastUpdateSource();
        String updateSystem = update.getSystemId();
        
        String oldSource = history.getSource();
        String oldSystem = history.getSystemType();
        
        if (log.isDebugEnabled()) 
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
            
            log.debug("Filtering " + identifier + " " + field + 
                " update source:" + updateSource + "/" + updateSystem + 
                " existing source:" + oldSource + "/" + oldSystem);
        }
        filterUpdateField(update, field);
    }
}
