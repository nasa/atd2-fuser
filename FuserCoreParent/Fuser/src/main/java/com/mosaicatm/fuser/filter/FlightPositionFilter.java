package com.mosaicatm.fuser.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class FlightPositionFilter 
implements MatmFilter<MatmFlight>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private boolean active = false;
    
    @Override
    public MatmFlight filter(MatmFlight flight)
    {
        /**
         *  if any of timestamp, latitude, or longitude is null
         *  filter out the position
         */
        if (flight != null && isActive())
        {
            Position position = flight.getPosition();
            
            if (position != null)
            {
                boolean isFilter = false;
                StringBuilder builder = new StringBuilder("Filtering out position containing: ");
                if (position.getTimestamp() == null)
                {
                    isFilter = true;
                    builder.append("null timestamp, ");
                }
                
                if (position.getLatitude() == null)
                {
                    isFilter = true;
                    builder.append("null latitude, ");
                }
                
                if (position.getLongitude() == null)
                {
                    isFilter = true;
                    builder.append("null longitude, ");
                }
                
                if (isFilter)
                {
                    builder.append("for flight " + flight.getGufi() + " and ");
                    builder.append("source " + flight.getLastUpdateSource());
                    
                    log.warn(builder.toString());
                    flight.setPosition(null);
                }
            }
        }
        
        return flight;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
