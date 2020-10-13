package com.mosaicatm.fuser.updaters.pre;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.TraversalExtensionElement;
import com.mosaicatm.fuser.common.matm.util.flight.DefaultMatmFlightTimeChooser;

public class TfmTraversalUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{
    private static final Log log = LogFactory.getLog(TfmTraversalUpdater.class);

    @Override
    public void update(MatmFlight update, MatmFlight target)
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update TFM traversal data. Update is NULL!");
            return;
        }
        
        if(( update.getExtensions() == null ) || 
                ( update.getExtensions().getTfmsFlightTraversalExtension() == null ))
        {
            return;
        }           

        // This method checks all the traversal data for missing traversal times, which is required for 
        // FLIGHT_SECTOR messages -- roughly 30% of messages with traversal data.
        // We re-use the calculated ETD if/when it's necessary to compute it.
        Date etd = fixTraversalTimes( update, target, update.getExtensions().getTfmsFlightTraversalExtension().getCenters(), null );
        etd = fixTraversalTimes( update, target, update.getExtensions().getTfmsFlightTraversalExtension().getSectors(), etd );
        etd = fixTraversalTimes( update, target, update.getExtensions().getTfmsFlightTraversalExtension().getFixes(), etd );
        fixTraversalTimes( update, target, update.getExtensions().getTfmsFlightTraversalExtension().getWaypoints(), etd );
    }
    
    private Date fixTraversalTimes( MatmFlight update, MatmFlight target, 
            List<? extends TraversalExtensionElement> traversalList, Date etd )
    {
        Date calcEtd = etd; 
        
        // Compute all traversal times if the first traversal time is null
        if(( traversalList != null ) && !traversalList.isEmpty() && ( traversalList.get( 0 ).getTraversalTime() == null ))
        {
            if( calcEtd == null )
            {
                calcEtd = getEtd( update, target );                
            }
            
            if( calcEtd != null )
            {
                for( TraversalExtensionElement traversal : traversalList )
                {
                    traversal.setTraversalTime( new Date( calcEtd.getTime() + ( traversal.getElapsedSeconds() * 1000 )));
                }
            }
        }
            
        return( calcEtd );
    }
    
    private Date getEtd( MatmFlight update, MatmFlight target )
    {
        Date bestEtd = null;
        
        // The update might have more current data than the target, but we can't use
        // the DefaultMatmFlightTimeChooser best times here, since it might return the scheduled times
        if(( update.getDepartureRunwayActualTime() != null ) && !update.getDepartureRunwayActualTime().isNil() )
        {
            bestEtd = update.getDepartureRunwayActualTime().getValue();
        }
        else if( update.getDepartureRunwayEstimatedTime() != null )
        {
            bestEtd = update.getDepartureRunwayEstimatedTime();
        }     
        else if( target != null )
        {
            bestEtd = DefaultMatmFlightTimeChooser.getBestAvailableOffTime( target );

            if( bestEtd == null )
            {
                bestEtd = DefaultMatmFlightTimeChooser.getBestAvailableOutTime( target );
            }
        }     
        
        if( bestEtd == null )
        {
            bestEtd = DefaultMatmFlightTimeChooser.getBestAvailableOffTime( update );
        }
        
        if( bestEtd == null )
        {
            bestEtd = DefaultMatmFlightTimeChooser.getBestAvailableOutTime( update );
        }        
        
        return( bestEtd );
    }
}
