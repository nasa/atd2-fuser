package com.mosaicatm.fuser.datacapture.filter;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.util.filter.Filter;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class AircraftUpdateFilter
implements Filter<MatmAircraft>
{
    private final Log log = LogFactory.getLog(getClass());

    private Set<String> uninterestedFields;

    @Override
    public boolean doFilter(MatmAircraft update)
    {
        if(uninterestedFields == null || uninterestedFields.isEmpty()){
            if(log.isDebugEnabled()){
                log.debug("No fields specified to be filtered out: " + update.getRegistration());
            }

            return true;
        }

        if(update != null && update.getChanges() != null && !update.getChanges().isEmpty())
        {
            List<String> changes = update.getChanges();

            for (String change : changes)
            {
                if (!uninterestedFields.contains(change))
                {
                    if(log.isDebugEnabled()){
                        log.debug("Update contains valid field: " + update.getRegistration());
                    }

                    return true;
                }
            }
        }
        if(log.isDebugEnabled()){
            log.debug("Update contains no valid fields. Update will not be processed: " + update.getRegistration());
        }

        return false;
    }


    public void setUninterestedFields(Set<String> uninterestedFields)
    {
        this.uninterestedFields = uninterestedFields;
    }
}
