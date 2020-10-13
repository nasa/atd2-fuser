package com.mosaicatm.fuser.filter;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class AirportAttributeLists
{

    private final Log log = LogFactory.getLog(getClass());
    private Map<FuserSource, List<String>> includes;
    private Map<FuserSource, List<String>> excludes;
    private String airport;
    
    public AirportAttributeLists(String airport) {
        this.airport = airport;
    }

    public List<String> getExcludeAttributesList(MatmFlight flight)
    {
        List<String> values = null;
        try {
            values = excludes.get(FuserSource.fromValue(flight.getLastUpdateSource()));
        } catch (Exception e) {
            log.error("Unknown Source: " + flight.getLastUpdateSource());
        }
        return values;
    }

    public List<String> getIncludeAttributesList(MatmFlight flight)
    {
        List<String> values = null;
        try {
            values = includes.get(FuserSource.fromValue(flight.getLastUpdateSource()));
        } catch (Exception e) {
            log.error("Unknown Source: " + flight.getLastUpdateSource());
        }
        return values;
    }

    public Map<FuserSource, List<String>> getIncludes() {
        return includes;
    }

    public void setIncludes(Map<FuserSource, List<String>> includes) {
        this.includes = includes;
    }

    public Map<FuserSource, List<String>> getExcludes() {
        return excludes;
    }

    public void setExcludes(Map<FuserSource, List<String>> excludes) {
        this.excludes = excludes;
    }
    
    public String getAirport()
    {
        return this.airport;
    }
}