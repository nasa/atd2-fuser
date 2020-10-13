package com.mosaicatm.fuser.updaters.pre;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.ObjectFactory;
import com.mosaicatm.matmdata.flight.extension.DerivedExtension;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;

public class DepartureStandActualTimeUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{

    private static final Log LOG = LogFactory.getLog(DepartureStandActualTimeUpdater.class);
    
    private boolean allowAirlineTimes = true;
    
    private Clock clock;
    private ObjectFactory matmFlightObjectFactory;
    private com.mosaicatm.matmdata.flight.extension.ObjectFactory extensionObjectFactory;
    
    public DepartureStandActualTimeUpdater(Clock clock){
        this.clock = clock;
        this.matmFlightObjectFactory = new ObjectFactory();
        this.extensionObjectFactory = new com.mosaicatm.matmdata.flight.extension.ObjectFactory();
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
            LOG.error("Cannot update departure stand actual time. Update is NULL!");
            return;
        }
        
        if( target == null )
        {
            target = update;
        }         

        Date aobt = getAirlineActualOutTime(update);
            
        // set the airline time to the derivedExtension
        if (aobt != null)
        {
            MatmFlightExtensions extensions = update.getExtensions();
            if (extensions == null)
            {
                extensions = new MatmFlightExtensions();
                update.setExtensions(extensions);
            }
            
            DerivedExtension derivedExtension = extensions.getDerivedExtension();
            
            if (derivedExtension == null)
            {
                derivedExtension = new DerivedExtension();
                extensions.setDerivedExtension(derivedExtension);
            }
            
            derivedExtension.setDepartureStandAirlineDerivedActualTime(aobt);
        }
            
        // If airline updates are not allowed, null it out
        if (!allowAirlineTimes)
        {
            aobt = null;
        }
        
        // If airline updates are not allowed or no airline update was found,
        // use a model update if present
        if (aobt == null && 
            update.getExtensions() != null && 
            update.getExtensions().getSurfaceModelExtension() != null)
        {
            aobt = update.getExtensions().getSurfaceModelExtension().getOutActualTime();
        }
        
        if (aobt != null)
        {
            update.setDepartureStandActualTime(matmFlightObjectFactory.createMatmFlightDepartureStandActualTime(aobt));
            
            if(target.getDepartureStandActualTime() == null ||
                            target.getDepartureStandActualTime().isNil()){
                setAobtReceivedTime(update);
            }
        }
    }
        
    private FuserSource getFuserSource(String sourceString, String gufi)
    {
        if (sourceString == null) {
            return null;
        }
        
        FuserSource source = null;
        
        try 
        {
            source = FuserSource.fromValue(sourceString);
        } 
        catch (IllegalArgumentException ex)
        {
            LOG.error("Unable to parse fuser source from: " + sourceString + 
                " for flight: " + gufi);
        }
        
        return source;
    }
    
    private void setAobtReceivedTime(MatmFlight update)
    {
        if(clock != null && update != null){
            MatmFlightExtensions extensions = update.getExtensions();
            if(extensions == null){
                extensions = new MatmFlightExtensions();
                update.setExtensions(extensions);
            }
            
            DerivedExtension extension = extensions.getDerivedExtension();
            if(extension == null){
                extension = extensionObjectFactory.createDerivedExtension();
                extensions.setDerivedExtension(extension);
            }
            
            extension.setDepartureStandActualTimeDerivedReceivedTimestamp(
                extensionObjectFactory.createDerivedExtensionDepartureStandActualTimeDerivedReceivedTimestamp(
                    new Date(clock.getTimeInMillis())));
        }
    }
    
    private Date getAirlineActualOutTime(MatmFlight update)
    {
        Date aobt = null;
        
        if (update == null)
        {
            return aobt;
        }
        
        FuserSource source = getFuserSource(update.getLastUpdateSource(), update.getGufi());
        
        if (FuserSource.AIRLINE.equals(source))
        {
            if (update.getExtensions() != null && 
                update.getExtensions().getMatmAirlineMessageExtension() != null)
            {
                aobt = update.getExtensions().getMatmAirlineMessageExtension().getOutTimeActual();
            }
        }
        else if (FuserSource.TFM.equals(source))
        {

            if (update.getExtensions() != null && 
                update.getExtensions().getTfmExtension() != null)
            {
                aobt = update.getExtensions().getTfmExtension().getDepartureStandActualTime();
            }
        }
        else if (FuserSource.TFM_TFDM.equals(source))
        {
            if (update.getExtensions() != null &&
                update.getExtensions().getTfmTfdmExtension() != null)
            {
                aobt = update.getExtensions().getTfmTfdmExtension().getActualOffBlockTime();
            }
        }
        
        return aobt;
    }
    
    /**
     * @param allowAirlineTimes the allowAirlineTimes to set
     */
    public void setAllowAirlineTimes(boolean allowAirlineTimes)
    {
        this.allowAirlineTimes = allowAirlineTimes;
    }

    public void setClock(Clock clock)
    {
        this.clock = clock;
    }
}
