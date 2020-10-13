package com.mosaicatm.fuser.updaters.pre;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class DepartureFixUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{
    private final String PRE_FIX_IN_ROUTE_STRING = "\\.";
    private final String POST_FIX_IN_ROUTE_STRING = "(\\d){0,1}\\.";
    
    private static final Log log = LogFactory.getLog(DepartureFixUpdater.class);

    @Override
    public void update(MatmFlight update, MatmFlight target)
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update departure fix. Update is NULL!");
            return;
        }
        
        if( target == null )
        {
            target = update;
        }           

        String fixUpdate = update.getDepartureFixSourceData();

        if(fixUpdate != null && !fixUpdate.trim().isEmpty()){
            String route = update.getRouteText();

            if(route == null || route.trim().isEmpty()){
                route = target.getRouteText();
            }

            if(!isFixInRouteText(fixUpdate, route)){
                update.setDepartureFixSourceData(null);
            }
        }
    }
    
    private boolean isFixInRouteText(String fix, String route){
        if(route != null && !route.trim().isEmpty()){
            Pattern routePattern = Pattern.compile(PRE_FIX_IN_ROUTE_STRING + fix + POST_FIX_IN_ROUTE_STRING);

            if(routePattern.matcher(route).find()){
                if(log.isDebugEnabled()){
                    log.debug("Fix update " + fix + " is allowed. Included in route string: " + route);
                }
                return true;
            }
        }
        
        if (log.isDebugEnabled())
        {
            log.debug("Fix update " + fix + " not allowed. Not included in route string: " + route);
        }
        
        return false;
    }
}
