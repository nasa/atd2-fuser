package com.mosaicatm.fuser.updaters.pre;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.MeteredTime;
import com.mosaicatm.matmdata.common.ReleaseMode;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.IdacExtension;
import com.mosaicatm.matmdata.flight.extension.TbfmExtension;

/**
 * The updater evaluates the last update source and system id to set the 
 * release update source and optionally filter out changes to the
 * departure runway meter time.  The release update source and the departure
 * runway meter time are ultimately used by the AMS component to interpret
 * proper handling of the update.
 * 
 * The expected order of data source priority is:
 * 1. last update source IDAC
 * 2. last update source AMS with system id of AMS-SWIM
 * 3. last update source TMA with system id of TMA.(ZDC|ZTL|ZNY).FAA.GOV-SWIM
 * 
 */
public class ReleaseSourceUpdater
extends AbstractUpdater<MatmFlight, MatmFlight>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private boolean filterAutoReleaseMode;
    
    private final String UNSCHED = "UNSCHED";
    
    private String idacSource = FuserSource.IDAC.name();
    private String swimSource = FuserSource.TMA.name();
    private String amsSource = FuserSource.AMS.name();
    
    private String swimSystemIdPattern = ".*\\.FAA\\.GOV-SWIM";
    private String amsSystemIdPattern = "AMS-SWIM";
    
    @Override
    public void update(MatmFlight update, MatmFlight target) 
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update release source. Update is NULL!");
            return;
        }
        
        if( target == null )
        {
            target = update;
        }   

        if (isIdacUpdate(update))
        {
            if (isIdacScheduled(update))
            {
                getIdacExtension(update).setNegotiatingRelease(true);
            }
            
            Boolean idacUpdateNegotiating = getIdacExtension(update).isNegotiatingRelease();
            
            /*
             * If the target is subject to swim negotiation and an incoming idac
             * update does not represent the start of idac negotiation, then we want
             * to clear out any release update source value.  This will prevent an
             * idac update from triggering any further negotiation logic and possibly
             * clearing the swim negotiation time by accident.
             */
            if (isSwimNegotiatingRelease(target) &&
                (idacUpdateNegotiating == null || !idacUpdateNegotiating.booleanValue()))
            {                
                update.setReleaseUpdateSource(null);
            }
            else
            {
                update.setReleaseUpdateSource(update.getLastUpdateSource());
            }
        }
        else if (isAmsUpdate(update))
        {
            if (isIdacNegotiatingRelease(target))
            {
                update.setReleaseUpdateSource(null);
                update.setDepartureRunwayMeteredTime(null);
            }
            else
            {
                update.setReleaseUpdateSource(update.getSystemId());
            }
        }
        else if (isSwimUpdate(update))
        {
            if (isAmsUpdate(target) ||
                isIdacNegotiatingRelease(target))
            {
                if( log.isDebugEnabled() )
                {
                    log.debug( "GUFI:" + update.getGufi() + " Nulling out TBFM SWIM departure time: AMS target: " + 
                            isAmsUpdate(target) + ", IDAC Negotiating target: " + isIdacNegotiatingRelease(target));
                }
                
                update.setReleaseUpdateSource(null);
                update.setDepartureRunwayMeteredTime(null);
            }
            else
            {
                update.setReleaseUpdateSource(update.getSystemId());
            }
        }
    }
    
    private boolean isIdacUpdate (MatmFlight flight)
    {
        return isValidSource(flight, idacSource, null) &&
               getIdacExtension(flight) != null && 
               getIdacScheduledStatusCode(flight) != null &&
               isValidIdacMode(flight);
    }
    
    private boolean isSwimUpdate (MatmFlight flight)
    {
        return isValidSource(flight, swimSource, swimSystemIdPattern) &&
               ( isSwimCancelDepartureRunwayMeteredTime(flight) || 
                 isValidDate(getDepartureRunwayMeteredTime(flight)));
    }
    
    private boolean isAmsUpdate (MatmFlight flight)
    {
        return isValidSource(flight, amsSource, amsSystemIdPattern) &&
               (isValidDate(getDepartureRunwayMeteredTime(flight)) ||
                isValidDate(getCanceledReleaseTime(flight)));
    }
    
    private boolean isValidIdacMode(MatmFlight flight)
    {
        boolean valid = true;
        
        if (filterAutoReleaseMode)
        {
            ReleaseMode releaseMode = flight.getReleaseMode();
            
            valid = releaseMode != null &&
                    releaseMode == ReleaseMode.AUTO ||
                    releaseMode == ReleaseMode.SEMI;
        }
        
        
        return valid;
    }
    
    private boolean isSwimReleaseSource(MatmFlight flight)
    {
        boolean isSwim = false;
        
        if (flight != null)
        {
            isSwim = flight.getReleaseUpdateSource() != null &&
                     flight.getReleaseUpdateSource().matches(swimSystemIdPattern);
        }
        
        return isSwim;
    }
    
    private boolean isValidSource (MatmFlight flight, String source, String idPattern)
    {
        boolean valid = false;
        
        if (flight != null)
        {
            if (flight.getLastUpdateSource() != null)
                valid = flight.getLastUpdateSource().equals(source);
            
            if (idPattern != null && flight.getSystemId() != null)
                valid = valid && flight.getSystemId().matches(idPattern);
        }
        
        return valid;
    }
    
    private boolean isValidDate (Date date)
    {
        return date != null && date.getTime() > -1L;
    }
    
    private Date getCanceledReleaseTime (MatmFlight flight)
    {
        Date canceled = null;
        
        TbfmExtension ext = getTbfmExtension(flight);
        
        if (ext != null)
        {
            canceled = ext.getCanceledSwimReleaseTime();
        }
        
        return canceled;
    }
    
    private Date getDepartureRunwayMeteredTime (MatmFlight flight)
    {
        Date drmt = null;
        
        if (flight != null)
        {
            if (flight.getDepartureRunwayMeteredTime() != null)
            {
                MeteredTime meterTime = flight.getDepartureRunwayMeteredTime().getValue();

                if (meterTime != null)
                    drmt = meterTime.getValue();
            }
        }
        
        return drmt;
    }
    
    private boolean isSwimCancelDepartureRunwayMeteredTime (MatmFlight flight)
    {
        if (flight != null)
        {
            TbfmExtension tbfm = getTbfmExtension(flight);
            if((tbfm != null) && (tbfm.getStd() != null) && tbfm.getStd().isNil())
            {
                return( true );
            }
            else if(( flight.getDepartureRunwayMeteredTime() != null ) && flight.getDepartureRunwayMeteredTime().isNil() )
            {
                return( true );
            }
        }
        
        return false;
    }    
    
    private boolean isSwimNegotiatingRelease (MatmFlight flight)
    {
        boolean negotiating = false;
        
        if (flight != null)
        {
            negotiating = isSwimReleaseSource(flight) &&
                          ( isSwimCancelDepartureRunwayMeteredTime(flight) || 
                            isValidDate(getDepartureRunwayMeteredTime(flight)));
        }
        
        return negotiating;
    }
    
    private boolean isIdacNegotiatingRelease (MatmFlight flight)
    {
        boolean negotiating = false;
        
        IdacExtension ext = getIdacExtension(flight);
        
        if (ext != null && ext.isNegotiatingRelease() != null)
            negotiating = ext.isNegotiatingRelease().booleanValue();
        
        return negotiating;
    }
    
    private boolean isIdacScheduled(MatmFlight flight)
    {
        String code = getIdacScheduledStatusCode(flight);
        return code != null && !code.equals(UNSCHED);
    }
    
    private String getIdacScheduledStatusCode (MatmFlight flight)
    {
        String code = null;
        
        IdacExtension idac = getIdacExtension(flight);
        
        if (idac != null)
            code = idac.getScheduledStatusCode();
        
        return code;
    }
    
    private TbfmExtension getTbfmExtension (MatmFlight flight)
    {
        TbfmExtension tbfm = null;
        
        if (flight != null && flight.getExtensions() != null)
        {
            tbfm = flight.getExtensions().getTbfmExtension();
        }
        
        return tbfm;
    }
    
    private IdacExtension getIdacExtension (MatmFlight flight)
    {
        IdacExtension idac = null;
        
        if (flight != null && flight.getExtensions() != null)
        {
            idac = flight.getExtensions().getIdacExtension();
        }
        
        return idac;
    }
    
    public void setIdacSource (String idacSource)
    {
        this.idacSource = idacSource;
    }
    
    public void setSwimSource (String swimSource)
    {
        this.swimSource = swimSource;
    }
    
    public void setAmsSource (String amsSource)
    {
        this.amsSource = amsSource;
    }
    
    public void setAmsSystemIdPattern (String amsSystemIdPattern)
    {
        this.amsSystemIdPattern = amsSystemIdPattern;
    }
    
    public void setSwimSystemIdPattern (String systemIdPattern)
    {
        this.swimSystemIdPattern = systemIdPattern;
    }
    
    public void setEnableAutoReleaseModeFilter (boolean filterAutoReleaseMode)
    {
        this.filterAutoReleaseMode = filterAutoReleaseMode;
    }
}
