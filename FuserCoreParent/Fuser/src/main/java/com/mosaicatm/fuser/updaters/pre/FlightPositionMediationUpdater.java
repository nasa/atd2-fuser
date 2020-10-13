package com.mosaicatm.fuser.updaters.pre;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * This class will make sure we should transition from one position source to 
 * another to avoid jumpiness. This could happen when a TFM Track hit comes 
 * in after we have asdex coverage. The track hit often lags. For both analysis
 * and display the results will be smoother if we filter this out when 
 * we transtion to a more trusted source.
 * 
 * Ideally this class would be a mediation rule as it is mediating between 
 * multiple different sources of position data. However, this has to run prior 
 * to the FlightHeadingUpdater, so this class is kept as an updater.
 * 
 * @author gorman
 *
 */
public class FlightPositionMediationUpdater
extends AbstractUpdater <MatmFlight, MatmFlight>
{
    
    private Logger log = Logger.getLogger(getClass());    

    private static long SECOND_IN_MILLIS = 1000L;
    private static FuserSource DEFAULT_SOURCE = FuserSource.UNKNOWN;
    private static long DEFAULT_TIMEOUT = SECOND_IN_MILLIS;
    private static int DEFAULT_PRIORITY = Integer.MAX_VALUE;
    
    private Map<FuserSource, SourcePriority> sourceProrityMap;
    private SourcePriority defaultPriority;
        
    public FlightPositionMediationUpdater()
    {
        sourceProrityMap = new HashMap<FuserSource, SourcePriority>();
        
        //default timeout is 1 hour
        defaultPriority = new SourcePriority(DEFAULT_SOURCE, DEFAULT_PRIORITY, 
                DEFAULT_TIMEOUT );
        
        sourceProrityMap.put(FuserSource.ASDEX, 
                new SourcePriority(FuserSource.ASDEX, 120, SECOND_IN_MILLIS * 2));
        
        //FLIGHTHUB_POSITION is the same as ASDEX with additional ramp area coverage
        sourceProrityMap.put(FuserSource.FLIGHTHUB_POSITION, 
                new SourcePriority(FuserSource.FLIGHTHUB_POSITION, 121, SECOND_IN_MILLIS * 2));

        sourceProrityMap.put(FuserSource.SMES,
                new SourcePriority(FuserSource.SMES, 150, SECOND_IN_MILLIS));
        
        sourceProrityMap.put(FuserSource.STARS,  
                new SourcePriority(FuserSource.STARS, 200, SECOND_IN_MILLIS * 15));
        
        sourceProrityMap.put(FuserSource.SFDPS,  
                new SourcePriority(FuserSource.SFDPS, 400, SECOND_IN_MILLIS * 30));
        
        sourceProrityMap.put(FuserSource.TMA,  
                new SourcePriority(FuserSource.TMA, 400, SECOND_IN_MILLIS * 30));
        
        sourceProrityMap.put(FuserSource.TFM,  
                new SourcePriority(FuserSource.TFM, 410, SECOND_IN_MILLIS * 60));
    }

    @Override
    public void update(MatmFlight update, MatmFlight target) {
        //There was just a timeout time for each type of source based on how 
        //often we expect to get each message, and a message type prioritization.
        //Looking forward to all of the sources that we might have, I think that 
        //ADS-B is the highest priority, then ASDE-X, then STARS, then SFDPS, then 
        //TFM. For ADS-B and ASDE-X the timeout time should be about 15 seconds.
        //Probably 30 seconds for STARS and 60 seconds for SFDPS.

        //So, I think that the logic would just be to ignore track hits from a lesser
        //priority source if a track message has been received (and used!) from a 
        //higher priority source within that source's timeout period.
        
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update flight position. Update is NULL!");
            return;
        }
        
        if( target == null )
        {
            target = update;
        }         

        Position currentPosition = target.getPosition();
        if(currentPosition == null)
        {
            //no current position nothing to do.
            return;
        }
        
        Position newPosition = update.getPosition();
        if(newPosition == null)
        {
            //updated position is no null so nothing to do
            return;
        }
        
        //clear out the updatedPosition. It is after the most recent position we received
        //so we don't want to use it.
        if(newPosition.getTimestamp().getTime() < currentPosition.getTimestamp().getTime())
        {
            update.setPosition(null);
            return;
        }
        
        //ADS-B Priority 1
        //ASDE-X priority 2
        //FLIGHTHUB_POSITION priority 2         
        //STARS Priority 3
        //SFDPS Priority 4
        //TFM Priority 5
        
        String newSource = newPosition.getSource();
        String currentSource = currentPosition.getSource();
        
        FuserSource newFuserSource = FuserSource.UNKNOWN;
        FuserSource currentFuserSource = FuserSource.UNKNOWN;
        try{
            newFuserSource = FuserSource.valueOf(newSource);
        }
        catch(Exception e)
        {
            log.warn("Unknown source " + newSource);
        }
        
        try{
            currentFuserSource = FuserSource.valueOf(currentSource);
        }
        catch(Exception e)
        {
            log.warn("Unknown source " + currentSource);
        }
        
        if(newSource.equals(currentSource))
        {
            //no change in source, no need to check for timeout
            return;
        }
        
        SourcePriority currentPriority = sourceProrityMap.get(currentFuserSource);
        SourcePriority newPriority = sourceProrityMap.get(newFuserSource);
        
        if(newPriority == null)
        {
            log.warn(newSource + " is not defined for FlightPositionFiltering, "
                    + "using the default priority " + DEFAULT_PRIORITY + " and"
                    + " default timeout of " + DEFAULT_TIMEOUT);
            
            newPriority = defaultPriority;
        }
        
        if(currentPriority == null )
        {
            log.warn(currentSource + " used from the previous position "
                    + " is not defined for FlightPositionFiltering, "
                    + "using the default priority " + DEFAULT_PRIORITY + " and"
                    + " default timeout of " + DEFAULT_TIMEOUT);
            
            currentPriority = defaultPriority;
        }
        
        //if the new source has a higher priority than our previous source
        //then let's use it. Lower numbers actually indicate higher priority
        if(newPriority.getPriority() <= currentPriority.getPriority())
        {
            return;
        }
        
        //if we got this far we have a new position with a different source than the 
        //previous position. And it has a lower priority. We would only use 
        //the new position if the previous source is past it's timeout, which would 
        //mean we are out of the coverage area for that source or it has become stale
        long timeDiff = newPosition.getTimestamp().getTime() - currentPosition.getTimestamp().getTime();

        //time out exceeded, use the new soure
        if(timeDiff > currentPriority.getTimeout())
        {
            return;
        }
        else
        {
            //timeout of the current position source has not elapsed. We do not want
            //to use the current position. A typical case would be we got a TFM
            //track hit but we want to keep using the ASDEX position.
            update.setPosition(null);
        }
        
    }
    
    private class SourcePriority
    {
        //There was just a timeout time for each type of source based on how 
        //often we expect to get each message, and a message type prioritization.
        //Looking forward to all of the sources that we might have, I think that 
        //ADS-B is the highest priority, then ASDE-X, then STARS, then SFDPS, then 
        //TFM. For ADS-B and ASDE-X the timeout time should be about 15 seconds.
        //Probably 30 seconds for STARS and 60 seconds for SFDPS.                
        private int priority = Integer.MAX_VALUE;
        private long timeout = SECOND_IN_MILLIS * 60;
        private FuserSource source = FuserSource.UNKNOWN;
        
        private SourcePriority(FuserSource source, int priority, long timeout)
        {
            this.source = source;
            this.priority = priority;
            this.timeout = timeout;
        }

        public int getPriority() {
            return priority;
        }
        
        public long getTimeout() {
            return timeout;
        }
        
        public FuserSource getSource()
        {
            return source;
        }
    }
    

}
