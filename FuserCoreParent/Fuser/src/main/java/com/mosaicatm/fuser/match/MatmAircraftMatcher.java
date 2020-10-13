package com.mosaicatm.fuser.match;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class MatmAircraftMatcher
{    
    public synchronized void assignRegistration(MatmAircraft aircraft)
    {
        /*
         * Eventually some matching logic may need to implemented here so that
         * any aircraft without a registration number will be assigned one.  For
         * now we're assuming that all aircraft coming in will have a valid
         * registration number.
         */
    }
}
