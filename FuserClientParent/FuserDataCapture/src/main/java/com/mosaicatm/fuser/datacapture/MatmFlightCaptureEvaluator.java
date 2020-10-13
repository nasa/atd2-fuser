package com.mosaicatm.fuser.datacapture;

import java.util.ArrayList;
import java.util.List;

import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * Class to generate a list valid of capture types from <CODE>MatmFlight</CODE>
 *
 */
public class MatmFlightCaptureEvaluator 
implements CaptureEvaluator<MatmFlight>
{
    private boolean loadMatmFlightAllJoin = true;
    
    public List<CaptureType> getCaptureTypes(MatmFlight flight)
    {
        List<CaptureType>  types = new ArrayList<>();
        
        if (flight != null)
        {
            if (isSyncUpdate(flight) || isSfdpsUpdate(flight) ||
                !isPositionOnlySource(flight))
            {
                types.add(CaptureType.MATM_FLIGHT);
                types.add(CaptureType.MATM_FLIGHT_ALL);
            }
            
            if (isPositionUpdate(flight))
            {
                types.add(CaptureType.POSITION);
                types.add(CaptureType.MATM_POSITION_ALL);
            }
            
            if (isAsdexUpdate(flight))
            {
                types.add(CaptureType.EXT_ASDEX);
                types.add(CaptureType.EXT_ASDEX_ALL);
            }
            
            if (isDerivedUpdate(flight))
            {
                types.add(CaptureType.EXT_DERIVED);
                types.add(CaptureType.EXT_DERIVED_ALL);
            }
            
            if (isTfmUpdate(flight))
            {
                types.add(CaptureType.EXT_TFM);
                types.add(CaptureType.EXT_TFM_ALL);
            }
            
            if (isTfmTfdmUpdate(flight))
            {
                types.add(CaptureType.EXT_TFM_TFDM);
                types.add(CaptureType.EXT_TFM_TFDM_ALL);
            }
            
            if (isTfmTraversalUpdate(flight))
            {
                types.add(CaptureType.EXT_TFM_TRAVERSAL);
                types.add(CaptureType.EXT_TFM_TRAVERSAL_ALL);
            }
            
            if (isAirlineUpdate(flight))
            {
                types.add(CaptureType.EXT_MATM_AIRLINE_MESSAGE);
                types.add(CaptureType.EXT_MATM_AIRLINE_MESSAGE_ALL);
            }
            
            if (isIdacUpdate(flight))
            {
                types.add(CaptureType.EXT_IDAC);
                types.add(CaptureType.EXT_IDAC_ALL);
            }
            
            if (isTbfmUpdate(flight))
            {
                types.add(CaptureType.EXT_TBFM);
                types.add(CaptureType.EXT_TBFM_ALL);
            }
            
            if (isAefsUpdate(flight))
            {
                types.add(CaptureType.EXT_AEFS);
                types.add(CaptureType.EXT_AEFS_ALL);
            }
            
            if (isSurfaceModelUpdate(flight))
            {
                types.add(CaptureType.EXT_SURFACE_MODEL);
                types.add(CaptureType.EXT_SURFACE_MODEL_ALL);
            }
            
            if (isTfmsTraversalUpdate(flight))
            {
                types.add(CaptureType.EXT_TFM_TFDM);
                types.add(CaptureType.EXT_TFM_TFDM_ALL);
            }
            
            if (isCat11Update(flight))
            {
                types.add(CaptureType.EXT_CAT11);
                types.add(CaptureType.EXT_CAT11_ALL);
            }
            
            if (isCat62Update(flight))
            {
                types.add(CaptureType.EXT_CAT62);
                types.add(CaptureType.EXT_CAT62_ALL);
            }

            if (isSfdpsUpdate(flight))
            {
                types.add(CaptureType.EXT_SFDPS);
                types.add(CaptureType.EXT_SFDPS_ALL);
            }

            if (isSmesUpdate(flight))
            {
                types.add(CaptureType.EXT_SMES);
                types.add(CaptureType.EXT_SMES_ALL);
            }

            if (isTmiUpdate(flight))
            {
                types.add(CaptureType.EXT_TMI);
                types.add(CaptureType.EXT_TMI_ALL);
            }

            if (loadMatmFlightAllJoin && !types.isEmpty())
            {
                types.add(CaptureType.MATM_FLIGHT_ALL_JOIN);
            }

        }
        
        return types;
    }
    
    private static boolean isSyncUpdate(MatmFlight flight)
    {
        if (flight != null &&
            FuserSource.SYNC.toString().equals(flight.getLastUpdateSource()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Sources listed in this method will only be written to the position tables,
     * and will not be written to the matm_flight tables.
     *
     * @param flight    A flight object which includes the source type
     * @return  true if this is a position only source
     */
    private static boolean isPositionOnlySource(MatmFlight flight)
    {
        if (flight != null &&
            (FuserSource.ASDEX.toString().equals(flight.getLastUpdateSource()) ||
             FuserSource.FLIGHTHUB_POSITION.toString().equals(flight.getLastUpdateSource()) ||
             FuserSource.CAT_11.toString().equals(flight.getLastUpdateSource()) ||
             FuserSource.CAT_62.toString().equals(flight.getLastUpdateSource()) ||
             FuserSource.ADSB.toString().equals(flight.getLastUpdateSource())
            )
        )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private static boolean isPositionUpdate(MatmFlight flight)
    {
        return (flight != null && flight.getPosition() != null);
    }

    private static boolean isAsdexUpdate(MatmFlight flight)
    {
        if (flight != null && flight.getExtensions() != null &&
            flight.getExtensions().getAsdexExtension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private static boolean isSmesUpdate(MatmFlight flight)
    {
        if (flight != null && flight.getExtensions() != null &&
                flight.getExtensions().getSmesExtension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private static boolean isDerivedUpdate(MatmFlight flight)
    {
        if (flight != null && flight.getExtensions() != null &&
            flight.getExtensions().getDerivedExtension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private static boolean isTfmUpdate(MatmFlight flight)
    {
        if (flight != null &&
            flight.getExtensions() != null &&
            flight.getExtensions().getTfmExtension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private static boolean isTfmTfdmUpdate(MatmFlight flight)
    {
        if (flight != null &&
            flight.getExtensions() != null &&
            flight.getExtensions().getTfmTfdmExtension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private static boolean isTfmTraversalUpdate(MatmFlight flight)
    {
        if (flight != null &&
                flight.getExtensions() != null &&
                flight.getExtensions().getTfmsFlightTraversalExtension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private static boolean isAirlineUpdate(MatmFlight flight)
    {
        if (flight != null &&
            flight.getExtensions() != null &&
            flight.getExtensions().getMatmAirlineMessageExtension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private static boolean isIdacUpdate(MatmFlight flight)
    {
        if (flight != null &&
            flight.getExtensions() != null &&
            flight.getExtensions().getIdacExtension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private static boolean isTbfmUpdate(MatmFlight flight)
    {
        if (flight != null &&
            flight.getExtensions() != null &&
            flight.getExtensions().getTbfmExtension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private static boolean isAefsUpdate(MatmFlight flight)
    {
        if (flight != null &&
            flight.getExtensions() != null &&
            flight.getExtensions().getAefsExtension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private static boolean isSurfaceModelUpdate(MatmFlight flight)
    {
        if (flight != null &&
            flight.getExtensions() != null &&
            flight.getExtensions().getSurfaceModelExtension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private static boolean isSfdpsUpdate(MatmFlight flight)
    {
        if (flight != null &&
            FuserSource.SFDPS.toString().equals(flight.getLastUpdateSource()) &&
            flight.getExtensions() != null &&
            flight.getExtensions().getSfdpsExtension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private static boolean isTfmsTraversalUpdate(MatmFlight flight)
    {
        if (flight != null &&
            flight.getExtensions() != null &&
            flight.getExtensions().getTfmsFlightTraversalExtension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private static boolean isTmiUpdate(MatmFlight flight)
    {
        if (flight != null && flight.getExtensions() != null
            && flight.getExtensions().getTmiExtension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private static boolean isCat11Update(MatmFlight flight)
    {
        if (flight != null &&
            flight.getExtensions() != null &&
            flight.getExtensions().getCat11Extension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private static boolean isCat62Update(MatmFlight flight)
    {
        if (flight != null &&
            flight.getExtensions() != null &&
            flight.getExtensions().getCat62Extension() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setLoadMatmFlightAllJoin(boolean loadMatmFlightAllJoin) {
        this.loadMatmFlightAllJoin = loadMatmFlightAllJoin;
    }
}
