package com.mosaicatm.fuser.common.matm.util;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmCloner
{
    private boolean isActive = true;
    
    public MatmFlight cloneFlight( MatmFlight flight )
    {
        return clone(flight);
    }   
    
    public MatmAircraft cloneAircraft ( MatmAircraft aircraft )
    {
        return clone(aircraft);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T clone (MatmObject data)
    {
        if( !isActive || ( data == null ))
        {
            return( null );
        }
        else
        {
            T clone = (T) data.clone();
            return( clone );
        }
    }

    public boolean isActive()
    {
        return isActive;
    }

    public void setActive( boolean isActive )
    {
        this.isActive = isActive;
    }
}
