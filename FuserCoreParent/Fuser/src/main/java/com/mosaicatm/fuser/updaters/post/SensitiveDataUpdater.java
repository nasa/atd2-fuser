package com.mosaicatm.fuser.updaters.post;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.sfd.SensitiveFlightDataLogic;
import com.mosaicatm.fuser.sfd.SensitiveFlightDataParser;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class SensitiveDataUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{
    private final Log log = LogFactory.getLog( getClass() );

    private SensitiveFlightDataLogic sensitiveFlightDataLogic = null;
    private SensitiveFlightDataParser sensitiveFlightDataParser = null;
    private String currentSensitiveDataCycle = null;      
    
    @Override
    synchronized public void update( MatmFlight update, MatmFlight target )
    {
        if( !isActive() )
        {
            return;
        }
        
        if( !checkInitialized( "Cannot update sensitiveData" ))
        {
            return;            
        }        
        
        if( update == null )
        {
            log.error( "Cannot update sensitiveData : update is NULL!" );
            return;
        }
          
        if( currentSensitiveDataCycle == null )
        {
            log.error( "Cannot update sensitiveData : currentSensitiveDataCycle is NULL! " +
                    "Has a sensitive flight data file been parsed?" );
            return;
        }        
        
        // Computations are fairly expensive. Only compute if we need to.
        if( isSfdCheckRequired( update, target ))
        {
            update.setSensitiveDataCycle( currentSensitiveDataCycle );
            update.setSensitiveData( sensitiveFlightDataLogic.isSensitive( update ));
            update.setBlockAtIndustry( sensitiveFlightDataLogic.isBlockAtIndustry( update ));
            
            if( log.isDebugEnabled() )
            {
                if(update.isSensitiveData())
                    log.debug( "Updating sensitive data to true for flight: " + update.getGufi() );
                if(update.isBlockAtIndustry())
                    log.debug( "Updating block at industry to true for flight: " + update.getGufi() );
            }              
        }
    }
    
    synchronized public void setSensitiveFlightDataLogic( SensitiveFlightDataLogic sensitiveFlightDataLogic )
    {
        this.sensitiveFlightDataLogic = sensitiveFlightDataLogic;
    }

    synchronized public void setSensitiveFlightDataParser( SensitiveFlightDataParser sensitiveFlightDataParser )
    {
        this.sensitiveFlightDataParser = sensitiveFlightDataParser;
    }
    
    synchronized public void readSensitiveFlightDataFile()
    {
        if( !isActive() )
        {
            return;
        }        
        
        if( !checkInitialized( "Cannot check for new sensitive flight data" ))
        {
            return;            
        }        
        
        if( sensitiveFlightDataParser.parseNewestFile() )
        {
            String newSfdCycle = sensitiveFlightDataParser.getSfdCycle();
            if( newSfdCycle != null && !Objects.equals( newSfdCycle, currentSensitiveDataCycle ))
            {
                log.info( "New sensitive data found, updating to cycle : " +  newSfdCycle );
                
                currentSensitiveDataCycle = newSfdCycle;
                
                sensitiveFlightDataLogic.setNonSensitiveAircraftIdsRegex( 
                        sensitiveFlightDataParser.getNonSensitiveAircraftIdsRegex() );
                sensitiveFlightDataLogic.setSensitiveAircraftIdsRegex( 
                        sensitiveFlightDataParser.getSensitiveAircraftIdsRegex() );
                sensitiveFlightDataLogic.setSensitiveAircraftTypes( 
                        sensitiveFlightDataParser.getSensitiveAircraftTypes() );
                sensitiveFlightDataLogic.setSensitiveBeaconCodes( 
                        sensitiveFlightDataParser.getSensitiveBeaconCodes() );
                sensitiveFlightDataLogic.setBlockAtIndustryAircraftIds( 
                        sensitiveFlightDataParser.getBlockAtIndustryAircraftIds() );
            }
        }
        else
        {
            log.error( "Error reading sensitive flight data." );
        }
    }
    
    private boolean isSfdCheckRequired( MatmFlight update, MatmFlight target )
    {
        if(( target == null ) || 
                ( target.getSensitiveDataCycle() == null ) || 
                ( target.isSensitiveData() == null ))
        {
            return( true );
        }
        
        if( sensitiveFlightDataLogic.getBlockAtIndustryIsSensitive() &&
            Objects.equals(Boolean.TRUE, target.isBlockAtIndustry()) &&
            !Objects.equals(Boolean.TRUE, target.isSensitiveData()) )
        {
            return( true );
        }
        
        if( !Objects.equals( currentSensitiveDataCycle, target.getSensitiveDataCycle() ))
        {
            return( true );
        }
        
        if(( update.getAcid() != null ) && 
                !Objects.equals( update.getAcid(), target.getAcid() ))
        {
            return( true );
        }
        
        if(( update.getAircraftType() != null ) && 
                !Objects.equals( update.getAircraftType(), target.getAircraftType() ))
        {
            return( true );
        }        
        
        if(( update.getBeaconCode() != null ) && 
                !Objects.equals( update.getBeaconCode(), target.getBeaconCode() ))
        {
            return( true );
        }         
        
        return( false );
    }
    
    private boolean checkInitialized( String msg )
    {
        if( sensitiveFlightDataLogic == null )
        {
            log.error( msg + ": sensitiveFlightDataLogic is NULL!" );
            return( false );            
        }
        
        if( sensitiveFlightDataParser == null )
        {
            log.error( msg + ": sensitiveData, sensitiveFlightDataParser is NULL!" );
            return( false );
        }           
        
        return( true );
    }
}
