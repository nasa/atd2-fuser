package com.mosaicatm.fuser.sfd;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * If your agency has access to sensitive data and would like to enable 
 * sensitive data filtering then please contact NASA using
 * the contact information in the license agreement.
 */
public class SensitiveFlightDataParser
{    
    private final Log log = LogFactory.getLog( getClass() );

    private List<String> sensitiveAircraftIdsRegex = new ArrayList<>();
    private List<String> nonSensitiveAircraftIdsRegex = new ArrayList<>();
    private List<String> sensitiveAircraftTypes = new ArrayList<>();
    private List<String> sensitiveBeaconCodes = new ArrayList<>();
    private List<String> blockAtIndustryAircraftIds = new ArrayList<>();
    
    private String password = null;    
    private String sfdCycle = null;
    private String defaultSensitiveFlightDataFolder = null;
    private String alternateSensitiveFlightDataFolder = null;
    
    public String getSfdCycle()
    {
        return sfdCycle;
    }
    
    public List<String> getSensitiveAircraftIdsRegex()
    {
        return sensitiveAircraftIdsRegex;
    }

    public List<String> getNonSensitiveAircraftIdsRegex()
    {
        return nonSensitiveAircraftIdsRegex;
    }

    public List<String> getSensitiveAircraftTypes()
    {
        return sensitiveAircraftTypes;
    }

    public List<String> getSensitiveBeaconCodes()
    {
        return sensitiveBeaconCodes;
    }
    
    public List<String> getBlockAtIndustryAircraftIds()
    {
        return blockAtIndustryAircraftIds;
    }    

    public void setPassword( String password )
    {
        this.password = password;
    }

    public void setDefaultSensitiveFlightDataFolder( String defaultSensitiveFlightDataFolder )
    {
        this.defaultSensitiveFlightDataFolder = defaultSensitiveFlightDataFolder;
    }

    public void setAlternateSensitiveFlightDataFolder( String alternateSensitiveFlightDataFolder )
    {
        if(( alternateSensitiveFlightDataFolder == null ) || 
                alternateSensitiveFlightDataFolder.trim().isEmpty() )
        {
            this.alternateSensitiveFlightDataFolder = null;
        }
        else
        {
            this.alternateSensitiveFlightDataFolder = alternateSensitiveFlightDataFolder;
        }
    }    
    
    public boolean parseNewestFile()
    {
        return false;
    }    
}
