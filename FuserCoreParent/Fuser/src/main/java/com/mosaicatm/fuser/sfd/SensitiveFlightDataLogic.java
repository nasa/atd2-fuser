package com.mosaicatm.fuser.sfd;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * This logic follows that which is described in the FAA document: 
 * "FAA Sensitive Flight Data (SDF) Identification and Protection Process"
 * 
 * If your agency has access to sensitive data and would like to enable 
 * sensitive data filtering then please contact NASA using
 * the contact information in the license agreement.
 */
public class SensitiveFlightDataLogic
{
    private final Set<String> sensitiveAircraftIdsRegexSet = new HashSet<>();
    private final Set<String> nonSensitiveAircraftIdsRegexSet = new HashSet<>();
    private final Set<String> sensitiveAircraftTypesSet = new HashSet<>();
    private final Set<String> sensitiveBeaconCodesSet = new HashSet<>(); 
    private final Set<String> blockAtIndustryAircraftIdsSet = new HashSet<>(); 

    private boolean blockAtIndustryIsSensitive = false;
    
    public synchronized boolean isSensitive( MatmFlight flight )
    {
        return( false );
    }
    
    public synchronized boolean isBlockAtIndustry(MatmFlight flight)
    {
        return (false);
    }

    public void setBlockAtIndustryIsSensitive( boolean blockAtIndustryIsSensitive )
    {
        this.blockAtIndustryIsSensitive = blockAtIndustryIsSensitive;
    }
    
    public boolean getBlockAtIndustryIsSensitive()
    {
        return (blockAtIndustryIsSensitive);
    }
    
    public synchronized void setSensitiveAircraftIdsRegex( Collection<String> sensitiveAircraftIdsRegex )
    {
        this.sensitiveAircraftIdsRegexSet.clear();
        
        if( sensitiveAircraftIdsRegex != null )
        {
            this.sensitiveAircraftIdsRegexSet.addAll( sensitiveAircraftIdsRegex );
        }
    }    

    public synchronized void setNonSensitiveAircraftIdsRegex( Collection<String> nonSensitiveAircraftIdsRegex )
    {
        this.nonSensitiveAircraftIdsRegexSet.clear();
        
        if( nonSensitiveAircraftIdsRegex != null )
        {
            this.nonSensitiveAircraftIdsRegexSet.addAll( nonSensitiveAircraftIdsRegex );
        }        
    }

    public synchronized void setSensitiveAircraftTypes( Collection<String> sensitiveAircraftTypes )
    {
        this.sensitiveAircraftTypesSet.clear();
        
        if( sensitiveAircraftTypes != null )
        {
            this.sensitiveAircraftTypesSet.addAll( sensitiveAircraftTypes );
        }         
    }

    public synchronized void setSensitiveBeaconCodes( Collection<String> sensitiveBeaconCodes )
    {
        this.sensitiveBeaconCodesSet.clear();
        
        if( sensitiveBeaconCodes != null )
        {
            this.sensitiveBeaconCodesSet.addAll( sensitiveBeaconCodes );
        }          
    }
    
    public synchronized void setBlockAtIndustryAircraftIds( Collection<String> blockAtIndustryAircraftIds )
    {
        this.blockAtIndustryAircraftIdsSet.clear();
        
        if( blockAtIndustryAircraftIds != null )
        {
            this.blockAtIndustryAircraftIdsSet.addAll( blockAtIndustryAircraftIds );
        }          
    }    
}
