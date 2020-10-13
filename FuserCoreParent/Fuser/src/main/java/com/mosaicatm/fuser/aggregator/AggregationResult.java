package com.mosaicatm.fuser.aggregator;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AggregationResult
{

	private final Log log = LogFactory.getLog(getClass());
	
	private Set<String> changes;
	
	private Set<String> identicals;
	
	public AggregationResult()
	{
		changes = new HashSet<String>();
		identicals = new HashSet<String>();
	}
	
	public AggregationResult(Set<String> changes, Set<String> identicals)
	{
		this.changes = changes;
		this.identicals = identicals;
	}
	
	public void addChange(String name)
	{
		if (isValid(name))
		{
			changes.add(name);
		}
		else
		{
			log.warn("Unable to add aggregate change, invalid name: " + name);
		}

	}
	
	public void addIdentical(String name)
	{
		if (isValid(name))
		{
			identicals.add(name);
		}
		else
		{
			log.warn("Unable to add aggregate identical, invalid name: " + name);
		}
	}
	
	private boolean isValid(String name)
	{
		if (name == null || name.trim().isEmpty())
			return false;
		
		return true;
	}
	
	public Set<String> getChanges()
	{
		return this.changes;
	}
	
	public Set<String> getIdenticals()
	{
		return this.identicals;
	}
	
}
