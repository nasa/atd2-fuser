package com.mosaicatm.fuser.updaters.pre;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class FlightHeadingUpdater
extends AbstractUpdater <MatmFlight, MatmFlight>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private FlightHeadingCalculator flightHeadingCalculator;
    private double latitudeTolerance;
    private double longitudeTolerance;
    
    public FlightHeadingUpdater()
    {
        flightHeadingCalculator = new FlightHeadingCalculator();
        
        latitudeTolerance = 0.000009d;
        longitudeTolerance = 0.000009d;
    }
    
    @Override
    public void update(MatmFlight update, MatmFlight target)
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update flight heading. Update is NULL!");
            return;
        }

        if( target == null )
        {
            target = update;
        }          
        
        try
        {
            Double newHeading = null;
            Position updatePosition = update.getPosition();
            if(updatePosition != null && updatePosition.getHeading() != null)
            {
                newHeading = updatePosition.getHeading();
            }
             
            //if we have a heading no point in continuing
            if(newHeading != null && newHeading != 0.0)
            {
                return;
            }
            
            // Get position, heading info prior to merging updates
            // in order to later calculate heading if necessary        
            if(updatePosition == null || updatePosition.getLatitude() == null
                    || updatePosition.getLongitude() == null)
            {
                //no new position, no point in looking for a heading
                return; 
            }
            
            double[] newPosition = new double[] { updatePosition.getLatitude(), updatePosition.getLongitude() };
            
            
            double[] prevPosition = null;
            
            Position targetPosition = target.getPosition();
            if(targetPosition != null && targetPosition.getLatitude() != null
                    && targetPosition.getLongitude() != null)
            {
                prevPosition = new double[] { targetPosition.getLatitude(), targetPosition.getLongitude() };
            }
            else
            {
                //no previous position. Calculate the heading to the airport
                if(update.getArrivalAerodrome() != null && update.getArrivalAerodrome().getIataName() != null)
                {
                    Double calculatedHeading = flightHeadingCalculator.calculateHeadingToAirport(update.getArrivalAerodrome().getIataName(), newPosition);
                    if(calculatedHeading != null)
                    {
                        updatePosition.setHeading(calculatedHeading);
                    }
                }
                
                return;
            }
            
            //if the position hasn't changed. Use the previous heading if we have one
            Double prevHeading = targetPosition.getHeading();
            if(update.getLastUpdateSource().equals(FuserSource.ASDEX.name()) || isEqual(targetPosition, updatePosition))
            {
                if(prevHeading != null)
                {
                    updatePosition.setHeading(prevHeading);
                    return;
                }
                else
                {
                    //use the heading to the airport if we don't have a previous heading or 
                    //pevious position
                    if(update.getArrivalAerodrome() != null && update.getArrivalAerodrome().getIataName() != null)
                    {
                        Double calculatedHeading = flightHeadingCalculator.calculateHeadingToAirport(update.getArrivalAerodrome().getIataName(), newPosition);
                        
                        //if it is null we were unable to find the airport
                        if(calculatedHeading != null)
                        {
                            updatePosition.setHeading(calculatedHeading);
                        }
                    }
                    return;
                }
            }
            
                
            else 
            {
                //if we made it this far. We don't have a heading in the message. We have
                //both a current and previous position and it has changed
                Double calculatedHeading = flightHeadingCalculator.calculateHeading(prevPosition, newPosition);
                
                //Apply it to the update. It will get merged to the target later
                if (calculatedHeading != null)
                {
                    updatePosition.setHeading(calculatedHeading);
                        
                    if (log.isDebugEnabled())
                        log.debug("Set calculatedHeading=" + calculatedHeading + " for flight " + update.getGufi());
                }    
            }
        }
        catch(Exception e)
        {
            log.error("Error computing heading for " + target.getGufi(), e);
        }
            
    }

    public void setFlightHeadingCalculator(FlightHeadingCalculator flightHeadingCalculator)
    {
        this.flightHeadingCalculator = flightHeadingCalculator;
    }    
    
    private boolean isEqual(Position a, Position b)
    {
        return (Math.abs(a.getLatitude() - b.getLatitude()) < latitudeTolerance) &&
                (Math.abs(a.getLongitude() - b.getLongitude()) < longitudeTolerance);
    }
}
