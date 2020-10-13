package com.mosaicatm.fuser.rules;

import java.util.Collection;
import java.util.Set;

import com.mosaicatm.matmdata.common.MetaData;

public interface Rule<T>
{
    /**
     * First, checks to see if the rule applies to the updated field. Then, 
     * checks to see if the field in an update should be filtered out 
     * based on a set of criteria, normally involving the update source and the 
     * source of the existing value. If the field should be filtered out, it will
     * be nulled out in the update and return false. Otherwise, returning true.
     * 
     * @param update   the update from an external source
     * @param target   the existing flight object that will be updated
     * @param history  the source for the existing field
     * @param field    the field being checked
     * 
     * @return true if the field is more favorable, false if the field is not favorable
     */
	public boolean handleDifference( T update, T target, MetaData history, String field );
	
	/**
     * First, checks to see if the rule applies to the updated field. Then, 
     * checks to see if the field in an update is more favorable 
     * based on a set of criteria, normally involving the update source and the 
     * source of the existing value. If the field should be filtered out, it will
     * be nulled out in the update and return false. Otherwise, returning true.
     * 
     * @param update   the update from an external source
     * @param target   the existing flight object that will be updated
     * @param history  the source for the existing field
     * @param field    the field being checked
     * 
     * @return true if the field is more favorable, false if the field is not favorable
     */
	public boolean handleIdentical ( T update, T target, MetaData history, String field );
        
    public Class getRuleClassType();
    public boolean validateRuleFields();
    
    public void setIncludes( Collection<String> includes );
    public Set<String> getIncludes();
    public String getIncludesRegex();
    public void setIncludesRegex( String includesRegex );
    public void setExcludes( Collection<String> excludes );
    public Set<String> getExcludes();
    public String getExcludesRegex();
    public void setExcludesRegex( String excludesRegex );
    public String getName();
    public void setName( String name );
    public boolean ruleAppliesToField( String field );
    public void filterUpdateField( T update, String field );
    public void setActive( boolean active );
    public boolean isActive();
    public void setPriority( int priority );
    public int getPriority();
}
