package com.mosaicatm.fuser.common.matm.util.aircraft;

import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class MatmAircraftIdLookup
implements MatmIdLookup<MatmAircraft, String>
{
    @Override
    public String getIdentifier(MatmAircraft data)
    {
        if (data != null)
            return data.getRegistration();
        return null;
    }
}
