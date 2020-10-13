package com.mosaicatm.fuser.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FuserFilter<T>
implements MatmFilter<T> 
{

	private static final Log log = LogFactory.getLog(FuserFilter.class);

	private boolean active;
	
	private List<MatmFilter<T>> filters = new ArrayList<>();

	public T filter(T matm){
		
		if (!isActive() || matm == null || filters == null || filters.isEmpty()) {
			return matm;
		}
		
		return (applyFilters(matm));

	}

	private T applyFilters(T data) 
	{
		for (MatmFilter<T> filter : filters) 
		{
			if (filter.isActive())
			{
				data = filter.filter(data);
				
				if (data == null) {
					return null;
				}
			}
		}

		return data;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void setFilters(List<MatmFilter<T>> filters)
	{
		this.filters = filters;
	}
	
	public void addFilter(MatmFilter<T> filter)
	{
		if (this.filters == null)
		{
			this.filters = new ArrayList<>();
		}
		this.filters.add(filter);
	}
}
