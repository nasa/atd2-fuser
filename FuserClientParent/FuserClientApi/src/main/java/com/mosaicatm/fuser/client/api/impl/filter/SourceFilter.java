package com.mosaicatm.fuser.client.api.impl.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.util.filter.Filter;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class SourceFilter 
implements Filter<MatmFlight>
{
	private boolean active;
	
    private final Log log = LogFactory.getLog(getClass());
    
    private List<String> sourcesOfInterest;
    
    private List<String> sourcesOfUninterest;
    
    public SourceFilter()
    {

    }
    
	@Override
	public boolean doFilter(MatmFlight flight) {
		
		if (!isActive())
			return true;
		
		String target = flight.getLastUpdateSource();
		
		if (sourcesOfInterest != null && sourcesOfInterest.contains(target))
			return true;
		
		if (sourcesOfUninterest != null && !sourcesOfUninterest.contains(target))
			return true;
		
		return false;
	}
	
    public void setSourcesOfInterest(String sources)
    {
        String[] srs = sources.split(",");
        sourcesOfInterest = new ArrayList<String>();
        sourcesOfInterest.addAll(Arrays.asList(srs));
    }
    
    public List<String> getSourcesOfInterest()
    {
    	return sourcesOfInterest;
    }
    
    
    public void setSourcesOfUninterest(String sources)
    {
        String[] srs = sources.split(",");
        sourcesOfUninterest = new ArrayList<String>();
        sourcesOfUninterest.addAll(Arrays.asList(srs));
    }
    
    public List<String> getSourcesOfUnInterest()
    {
    	return sourcesOfUninterest;
    }

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
