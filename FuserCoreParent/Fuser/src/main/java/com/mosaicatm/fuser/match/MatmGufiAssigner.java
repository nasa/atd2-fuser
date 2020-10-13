package com.mosaicatm.fuser.match;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.guficlient.GufiClient;
import com.mosaicatm.gufiservice.data.GlobalGufiFlight;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmGufiAssigner
{
    private static final String IGNORE_GUFI = "IGNORE";
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private GufiClient gufiClient;
    private MatmToGufiFlightTransform matmToGufiFlightRequestTransform;
    
    private boolean active;
    
    public MatmGufiAssigner(GufiClient gufiClient)
    {
        this.gufiClient = gufiClient;
        matmToGufiFlightRequestTransform = new MatmToGufiFlightTransform();
    }
    
    //Note: if this is ever updated to perform gufi service updates in addition to requests,
    //we'd want to adjust the transform to not set funky times in the update. 
    //Right now, the transform assumes only requests, not updates.
    public synchronized void assignGufi(MatmFlight matm)
    {
        if (!active)
        {
            return;
        }
        
        if (matm == null || IGNORE_GUFI.equals(matm.getGufi()))
        {
            return;
        }

        String gufi = null;
        GlobalGufiFlight gufiFlight = matmToGufiFlightRequestTransform.transform(matm);
        
        if (matm.getGufi() == null)
        {
            gufi = gufiClient.requestGufi(gufiFlight);
            log.trace("Found gufi {}", gufi);
        }
        else
        {
            gufiClient.updateFlight(gufiFlight);
            gufi = gufiFlight.getGufi();
        }

        matm.setGufi(gufi);
    }
    
    public void setActive(boolean active)
    {
        this.active = active;
    }
    
}
