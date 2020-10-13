package com.mosaicatm.fuser.datacapture;

import java.util.ArrayList;
import java.util.List;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;

/**
 * Class to generate a list valid of capture types from <CODE>MatmFlight</CODE>
 *
 */
public class MatmAircraftCaptureEvaluator 
implements CaptureEvaluator<MatmAircraft>
{
    public List<CaptureType> getCaptureTypes(MatmAircraft aircraft)
    {
        List<CaptureType>  types = new ArrayList<>();
        
        if (aircraft != null)
        {
            types.add(CaptureType.MATM_AIRCRAFT);
            types.add(CaptureType.MATM_AIRCRAFT_ALL);
        }
        
        return types;
    }
}
