package com.mosaicatm.fuser.updaters.post;

import java.util.Date;
import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.aptcode.data.AirportCodeEntry;
import com.mosaicatm.fuser.common.matm.util.DistanceUtil;
import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightStateUtil;
import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.fuser.util.FuserAirportCodeTranslator;
import com.mosaicatm.fuser.util.MatmFlightInternalUpdateUtil;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;

/**
 * Sets actual departure/arrival runway times from track when missing.
 */
public class RunwayActualTimeUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{
    public final static String FUSER_ACTUAL_ARRIVAL_TIME_SYSTEM_ID = "ARRIVAL_RUNWAY_ACTUAL_TIME";
    
    private final static double IGNORE_ALTITUDE_FEET = 10000;
    private final static long ALLOWABLE_ETA_TRACK_TOLERANCE_MILLIS = 10 * 60 * 1000;
    private final static double ALLOWABLE_AIRPORT_ALTITUDE_DIFF_FEET = 7500;
    private final static double ALLOWABLE_AIRPORT_DISTANCE_NM = 15;
    
    // Thresholds from Kistler's runway detection logic
    private final static double MINIMUM_AIRBORNE_AIRPORT_ALTITUDE_DIFF_FEET = 50;
    private final static double MINIMUM_AIRBORNE_SPEED_KTS = 80;    
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ObjectFactory objectFactory = new ObjectFactory();
    
    private FuserAirportCodeTranslator airportCodeTranslator;
    
    @Override
    public void update(MatmFlight update, MatmFlight target)
    {
        if (!isActive())
        {
            return;
        }    
        
        if( airportCodeTranslator == null )
        {
            log.error("Cannot update actual departure/arrival times. airportCodeTranslator is NULL!");
            return;
        }          
        
        if( update == null )
        {
            log.error("Cannot update actual departure/arrival times. Update is NULL!");
            return;
        }        
        
        // Missing actual departure time
        if(( update.getDepartureRunwayActualTime() == null ) &&
                (( target == null ) || ( target.getDepartureRunwayActualTime() == null )))
        {
            Date derivedTime = deriveDepartureRunwayActualTime( update );
            if( derivedTime != null )
            {
                log.debug( "Setting Fuser derived DepartureRunwayActualTime : {}", derivedTime );
                
                JAXBElement<Date> deptTime = objectFactory.createMatmFlightDepartureRunwayActualTime( derivedTime );
                update.setDepartureRunwayActualTime( deptTime );                
            }
        } 
        
        // Missing actual arrival time
        if(( update.getArrivalRunwayActualTime() == null ) &&
                (( target == null ) || ( target.getArrivalRunwayActualTime() == null )))
        {
            Date derivedTime = deriveArrivalRunwayActualTime( update.getTimestamp(), update );
            if(( derivedTime == null ) && ( target != null ))
            {
                derivedTime = deriveArrivalRunwayActualTime( update.getTimestamp(), target );
            }
            
            if( derivedTime != null )
            {
                log.debug( "Setting Fuser derived ArrivalRunwayActualTime : {}", derivedTime );
                
                update.setArrivalRunwayActualTime( derivedTime );                
            }
        }                 
    }    
    
    @Override
    public void sweeperUpdate( Date currentTime, MatmFlight currentState ) 
    {
        if (!isActive())
        {
            return;
        }        
        
        if( currentState == null || currentTime == null )
        {
            log.error("Cannot update actual departure/arrival times. matmFlight or currentTime is NULL!");
            return;
        }          
        
        Date time = deriveArrivalRunwayActualTime( currentTime, currentState );
        
        if( time != null )
        {
            MatmFlight matm = MatmFlightInternalUpdateUtil.getFuserInternalUpdate( 
                    currentTime, currentState, FUSER_ACTUAL_ARRIVAL_TIME_SYSTEM_ID );
            matm.setArrivalRunwayActualTime( time );

            log.debug( "Injecting Fuser ArrivalRunwayActualTime message: {}, {}, {}, {}", 
                    matm.getGufi(), matm.getLastUpdateSource(), matm.getSystemId(),
                    matm.getArrivalRunwayActualTime() );
           
            injectFuserMessage( matm );
        } 
    }       
    
    public void setAirportCodeTranslator(FuserAirportCodeTranslator airportCodeTranslator)
    {
        this.airportCodeTranslator = airportCodeTranslator;
    }      
    
    // Returns non-null value if actual time is derived
    private Date deriveDepartureRunwayActualTime(MatmFlight matmFlight)
    {
        // Time already set
        if( matmFlight.getDepartureRunwayActualTime() != null &&
                !matmFlight.getDepartureRunwayActualTime().isNil() )
        {
            return( null );
        }        
        
        return( deriveAirbornePositionTimeNearAirport( matmFlight, true ) );
    }
    
    // Returns non-null value if actual time is derived
    private Date deriveArrivalRunwayActualTime( Date currentTime, MatmFlight matmFlight )
    {
        // Time already set
        if( matmFlight.getArrivalRunwayActualTime() != null )
        {
            return( null );
        }        
        
        // Require stale airborne sourced track data to trigger arrival time
        if( !MatmFlightStateUtil.hasStaleAirborneDataSourceTrack( currentTime, matmFlight ))
        {
            return( null );
        }          
        
        Date positionTime = deriveAirbornePositionTimeNearAirport( matmFlight, false );
        
        if( positionTime != null )
        {
            Date eta = matmFlight.getArrivalRunwayEstimatedTime();

            // use the current ETA if it's reasonable
            if(( eta != null ) && 
                    !eta.before( positionTime ) &&
                    (( eta.getTime() - positionTime.getTime() ) <= ALLOWABLE_ETA_TRACK_TOLERANCE_MILLIS ))
            {
                return( eta );
            }
            else
            {
                return( positionTime );
            }
        }
        
        return( null );
    }    
    
    private Date deriveAirbornePositionTimeNearAirport( MatmFlight matmFlight, boolean deriveDepartureTime )
    {    
        // Missing position data
        if(( matmFlight.getPosition() == null ) || ( matmFlight.getPosition().getTimestamp() == null ))
        {
            return( null );
        }
        
        // Too high to derive an actual time -- simple check for efficiency
        if(( matmFlight.getPosition().getAltitude() == null ) || 
                ( matmFlight.getPosition().getAltitude() > IGNORE_ALTITUDE_FEET ))
        {
            return( null );
        }                        
            
        AirportCodeEntry airportCode;

        if( deriveDepartureTime )
        {
            airportCode = airportCodeTranslator.getBestMatchAirport( matmFlight.getDepartureAerodrome() );
        }
        else
        {
            airportCode = airportCodeTranslator.getBestMatchAirport( matmFlight.getArrivalAerodrome() );
        }
        
        // Allow a departure time from any source if it fits the criteria
        if( deriveDepartureTime && isAirbornePositionNearAirport( matmFlight.getPosition(), airportCode, false ))
        {
            return( matmFlight.getPosition().getTimestamp() );
        }
        
        // No airborne sourced track detected -- from TFM, SFDPS, or TBFM
        if( !MatmFlightStateUtil.hasAirborneDataSourceTrack( matmFlight ))
        {
            return( null );
        }         

        Position position = MatmFlightStateUtil.getLatestAirborneDataSourceTrackPosition( matmFlight );

        if(( position != null ) && isAirbornePositionNearAirport( position, airportCode, true ))
        {
            return( position.getTimestamp() );
        }
        
        return( null );
    }
    
    private boolean isAirbornePositionNearAirport( Position position, AirportCodeEntry airportCode, boolean isAirborneDataSourceTrack )
    {
        if(( position == null ) || ( airportCode == null ) || ( position.getAltitude() == null ))
        {
            return( false );
        }
        
        // Too high to derive an actual time
        if( position.getAltitude() > IGNORE_ALTITUDE_FEET )
        {
            return( false );
        }          
        
        Double distanceNm = null;
        Double elevationDiffFeet = null;           
        
        if(( airportCode.getLatitude() != null ) && ( airportCode.getLongitude() != null ))
        {
            distanceNm = DistanceUtil.calculateDistance( airportCode.getLatitude(), airportCode.getLongitude(), position );
        }

        if(( airportCode.getElevationFeet() != null ) && ( position.getAltitude() != null ))
        {            
            elevationDiffFeet = Math.abs( airportCode.getElevationFeet() - position.getAltitude() );
        }                
        
        // Too far from airport
        if(( distanceNm == null ) || ( distanceNm > ALLOWABLE_AIRPORT_DISTANCE_NM ))
        {
            return( false );
        }       
        
        if( elevationDiffFeet != null )
        {       
            if( elevationDiffFeet > ALLOWABLE_AIRPORT_ALTITUDE_DIFF_FEET )
            {
                return( false );
            }
            
            if( isAirborneDataSourceTrack )
            {
                return( true );
            }
            
            // Flight is airborne based on elevation and speed, regardless of source
            return(( elevationDiffFeet > MINIMUM_AIRBORNE_AIRPORT_ALTITUDE_DIFF_FEET ) && 
                        (( position.getSpeed() == null ) || ( position.getSpeed() > MINIMUM_AIRBORNE_SPEED_KTS )));
        }
        
        return( true );        
    }
}
