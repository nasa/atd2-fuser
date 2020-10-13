package com.mosaicatm.fuser.common.matm.util.flight;

import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFlightIdLookup
implements MatmIdLookup<MatmFlight, String>
{
    @Override
    public String getIdentifier(MatmFlight data)
    {
        if (data != null)
            return data.getGufi();
        return null;
    }
}
