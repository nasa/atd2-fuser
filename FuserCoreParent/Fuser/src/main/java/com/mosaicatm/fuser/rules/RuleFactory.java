package com.mosaicatm.fuser.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.common.MetaData;

public class RuleFactory<T>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private List<Rule<T>> rules;

    private boolean active;

    private SortByOrder comparator = new SortByOrder();

    public boolean handleDifference( T update, T target, MetaData history, String field )
    {
        if(canRunRules())
        {
            for( Rule<T> rule : rules )
            {
                if( !rule.handleDifference( update, target, history, field ) )
                {
                    return false;
                }
            }
        }

        return true;
    }
    
    public boolean handleIdentical( T update, T target, MetaData history, String field )
    {
        if(canRunRules())
        {
            for( Rule<T> rule : rules )
            {
                if( !rule.handleIdentical( update, target, history, field ) )
                {
                    return false;
                }
            }
        }

        return true;
    }
    
    public void initialize ()
    {        
        if (rules != null)
        {
            log.info("Initializing " + rules.size() + " rules");
            
            order();
            
            // Validate rules
            log.debug("Rule processing order...");
            for (Rule<T> rule : rules)
            {
                log.debug(rule.getName() + ": " + rule.getPriority() + 
                          ", active: " + rule.isActive());
                if( !rule.validateRuleFields() )
                {
                    throw new IllegalArgumentException( "Invalid rule configuration found in " + rule.getName() );
                }                    
            }
        }
    }
    
    private boolean canRunRules()
    {
        return isActive() && rules != null && !rules.isEmpty();
    }

    public void order()
    {
        if( rules != null )
        {
            Collections.sort( rules, comparator );
        }
    }

    public void add( Rule<T> rule )
    {
        if( rule != null )
        {
            if( rules == null )
            {
                rules = new ArrayList<>();
            }

            rules.add( rule );
        }
    }

    public List<Rule<T>> getRules()
    {
        return rules;
    }

    public void setRules( List<Rule<T>> rules )
    {
        this.rules = rules;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive( boolean active )
    {
        this.active = active;
    }

    class SortByOrder implements Comparator<Rule<T>>
    {
        @Override
        public int compare( Rule<T> rule1, Rule<T> rule2 )
        {
            if( rule1.getPriority() > rule2.getPriority() )
            {
                return 1;
            }

            return -1;
        }
    }
}
