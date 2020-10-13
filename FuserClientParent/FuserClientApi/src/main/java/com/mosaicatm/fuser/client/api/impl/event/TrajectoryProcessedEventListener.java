package com.mosaicatm.fuser.client.api.impl.event;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventManager;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class TrajectoryProcessedEventListener
implements FuserProcessedEventListener<MatmFlight>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private boolean active;
    
    private FuserProcessedEventManager<MatmFlight> eventManager;
    private FuserClientStore<MatmFlight> store;
    
    public TrajectoryProcessedEventListener ()
    {
        // default constructor
    }
    
    public TrajectoryProcessedEventListener (FuserClientStore<MatmFlight> store)
    {
        this.store = store;
    }
    
    @Override
    public void dataAdded (MatmFlight flightUpdate)
    {
        updateRawTrajectory (flightUpdate);
    }

    @Override
    public void dataUpdated (MatmFlight flight, MatmFlight flightUpdate)
    {
        updateRawTrajectory (flightUpdate);
    }

    @Override
    public void dataRemoved(MatmFlight flight)
    {
        // do nothing on data removal
    }
    
    /**
     * The JAXB merge utility does not know how to handle merging fields in
     * extensions.  If an update with an extension is received it will wipe out
     * any existing extensions in the flight store.  To preserve the trajectory
     * information the the raw trajectory will updated and stored as part of the
     * flight update, so when the merge occurs no information is lost.
     * @param flightUpdate
     */
    private void updateRawTrajectory (MatmFlight flightUpdate)
    {        
        if (!isActive())
            return;
        
        if (flightUpdate == null)
            return;
        
        
        if (isPositionUpdate (flightUpdate))
        {
            MatmFlight flight = getFlight (flightUpdate.getGufi());
            
            if (flight != null)
            {  
                Position newPoint = flightUpdate.getPosition();
                Position lastPoint = getLastRawTrajectoryPoint (flight);
                
                if (!pointsAreEqual(newPoint, lastPoint))
                {                    
                    flight.getTrajectory().add(newPoint);
                    
                    if (log.isDebugEnabled())
                        log.debug("Trajectory length for " + flight.getGufi() + " = " + flight.getTrajectory().size());
                }
            }
            else
            {
                log.warn("Failed to find flight " + flightUpdate.getGufi() + " in local store");
            }
        }
    }
    
    private boolean pointsAreEqual (Position newPoint, Position lastPoint)
    {
        if ((newPoint == null && lastPoint != null) ||
            (newPoint != null && lastPoint == null))
            return false;
        
        return newPoint.equals(lastPoint);
    }
    
    private Position getLastRawTrajectoryPoint (MatmFlight flight)
    {
        List<Position> points = flight.getTrajectory();
        
        if (points != null && !points.isEmpty())
        {
            return points.get(points.size() - 1);
        }
        
        return null;
    }

    
    private boolean isPositionUpdate (MatmFlight flight)
    {
        boolean isPosition = false;
        
        if (flight != null && flight.getPosition() != null)
        {
            isPosition = flight.getPosition().getLatitude() != null &&
                         flight.getPosition().getLongitude() != null &&
                         flight.getPosition().getTimestamp() != null;
        }
        
        return isPosition;
    }

    private MatmFlight getFlight (String gufi)
    {
        MatmFlight storeFlight = null;
        
        if (store != null)
        {
            if (gufi != null && !gufi.trim().isEmpty())
            {
                MatmFlight matmFlight = store.get(gufi);
                
                if (matmFlight != null)
                    storeFlight = matmFlight;
                else
                    log.debug("Failed to find stored flight for " + gufi);
            }
        }
        
        return storeFlight;
    }
    
    public void initialize ()
    {
        if (isActive())
        {
            if (eventManager != null)
                eventManager.registerListener(this);
            else
                log.error("Failed to load trajectory listener, event manager is null");
        }
    }

    public boolean isActive ()
    {
        return active;
    }
    
    public void setActive (boolean active)
    {
        this.active = active;
    }
    
    public void setStore (FuserClientStore<MatmFlight> store)
    {
        this.store = store;
    }
    
    public void setEventManager (FuserProcessedEventManager<MatmFlight> eventManager)
    {
        this.eventManager = eventManager;
    }
}
