package com.mosaicatm.fuser.datacapture.flat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.datacapture.io.CaptureData;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.AdsbExtension;
import com.mosaicatm.matmdata.flight.extension.AefsExtension;
import com.mosaicatm.matmdata.flight.extension.AsdexExtension;
import com.mosaicatm.matmdata.flight.extension.Cat11Extension;
import com.mosaicatm.matmdata.flight.extension.Cat62Extension;
import com.mosaicatm.matmdata.flight.extension.DerivedExtension;
import com.mosaicatm.matmdata.flight.extension.IdacExtension;
import com.mosaicatm.matmdata.flight.extension.MatmAirlineMessageExtension;
import com.mosaicatm.matmdata.flight.extension.SfdpsExtension;
import com.mosaicatm.matmdata.flight.extension.SmesExtension;
import com.mosaicatm.matmdata.flight.extension.SurfaceModelExtension;
import com.mosaicatm.matmdata.flight.extension.TbfmExtension;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;
import com.mosaicatm.matmdata.flight.extension.TfmTfdmExtension;
import com.mosaicatm.matmdata.flight.extension.TfmsFlightTraversalExtension;
import com.mosaicatm.matmdata.flight.extension.TmiExtension;
import com.mosaicatm.matmdata.position.MatmPositionUpdate;

public class FlatFactoryImpl
implements FlatFactory
{
    private final Log log = LogFactory.getLog(getClass());
    private boolean filterUpdateSources = true;
    private boolean filterFacilityIdList = false;
    private boolean filterPreDepartureFlightPlanList = false;
    private boolean adsbExtEnabled = true;
    private boolean asdexExtEnabled = true;
    private boolean cat11ExtEnabled = true;
    private boolean cat62ExtEnabled = true;
    private boolean derivedExtEnabled = true;
    private boolean tbfmExtEnabled = true;
    private boolean tfmExtEnabled = true;
    private boolean tfmTfdmExtEnabled = true;
    private boolean tfmTraversalExtEnabled = false;
    private boolean matmAirlineMsgExtEnabled = true;
    private boolean idacExtEnabled = true;
    private boolean surfaceModelExtEnabled = true;
    private boolean aefsExtEnabled = true;
    private boolean sfdpsExtEnabled = true;
    private boolean smesExtEnabled = true;
    private boolean tmiExtEnabled = true;


    @SuppressWarnings("unchecked")
    @Override
    public <T, D> T flatten(CaptureData<D> captureData)
    {
        Object flat = null;

        Object data = captureData.getData();

        if (captureData != null && captureData.getType() != null && data != null)
        {            
            switch (captureData.getType())
            {
                case MATM_FLIGHT:
                case MATM_FLIGHT_ALL:
                case MATM_FLIGHT_SUMMARY:
                    flat = flattenMatmFlight((MatmFlight)data);
                    break;
                case MATM_AIRCRAFT:
                case MATM_AIRCRAFT_ALL:
                case MATM_AIRCRAFT_SUMMARY:
                case MATM_AIRCRAFT_REMOVED:
                    flat = flattenMatmAircraft((MatmAircraft)data);
                    break;
                case EXT_ADSB:
                case EXT_ADSB_ALL:
                    if( adsbExtEnabled )
                    {
                        flat = flattenAdsbExtension((AdsbExtension)data);
                    }
                    break;
                case EXT_ASDEX:
                case EXT_ASDEX_ALL:
                    if( asdexExtEnabled )
                    {
                        flat = flattenAsdexExtension((AsdexExtension)data);
                    }
                    break;
                case EXT_CAT11:
                case EXT_CAT11_ALL:
                    if( cat11ExtEnabled )
                    {
                        flat = flattenCat11Extension((Cat11Extension)data);
                    }
                    break;
                case EXT_CAT62:
                case EXT_CAT62_ALL:
                    if( cat62ExtEnabled )
                    {
                        flat = flattenCat62Extension((Cat62Extension)data);
                    }
                    break;
                case EXT_DERIVED:
                case EXT_DERIVED_ALL:
                    if( derivedExtEnabled )
                    {
                        flat = flattenDerivedExtension((DerivedExtension)data);
                    }
                    break;
                case EXT_TBFM:
                case EXT_TBFM_ALL:
                    if( tbfmExtEnabled )
                    {
                        flat = flattenTbfmExtension((TbfmExtension)data);
                    }
                    break;
                case EXT_TFM:
                case EXT_TFM_ALL:
                    if( tfmExtEnabled )
                    {
                        flat = flattenTfmExtension((TfmExtension)data);
                    }
                    break;   
                case EXT_TFM_TFDM:
                case EXT_TFM_TFDM_ALL:
                    if( tfmTfdmExtEnabled )
                    {
                        flat = flattenTfmTfdmExtension((TfmTfdmExtension)data);
                    }
                    break;    
                case EXT_TFM_TRAVERSAL:
                case EXT_TFM_TRAVERSAL_ALL:
                    if( tfmTraversalExtEnabled )
                    {
                        flat = flattenTfmFlightTraversalExtension((TfmsFlightTraversalExtension)data);
                    }
                    break;                 
                case EXT_MATM_AIRLINE_MESSAGE:
                case EXT_MATM_AIRLINE_MESSAGE_ALL:
                    if( matmAirlineMsgExtEnabled )
                    {
                        flat = flattenMatmAirlineMessageExtension((MatmAirlineMessageExtension)data);
                    }
                    break;
                case POSITION:
                case MATM_POSITION_ALL:
                    flat = flattenPosition((MatmPositionUpdate)data);
                    break;
                case EXT_IDAC:
                case EXT_IDAC_ALL:
                    if( idacExtEnabled )
                    {
                        flat = flattenIdacExtension((IdacExtension) data);
                    }
                    break;
                case EXT_SURFACE_MODEL:
                case EXT_SURFACE_MODEL_ALL:
                    if( surfaceModelExtEnabled )
                    {
                        flat = flattenSurfaceModelExtension((SurfaceModelExtension) data);
                    }
                    break;
                case MATM_FLIGHT_REMOVED:
                    flat = flattenMatmFlight((MatmFlight)data);
                    break;
                case EXT_AEFS:
                case EXT_AEFS_ALL:
                    if( aefsExtEnabled )
                    {
                        flat = flattenAefsExtension((AefsExtension) data);
                    }
                    break;
                case MATM_FLIGHT_ALL_JOIN:
                    flat = data;
                    break;
                case EXT_SFDPS:
                case EXT_SFDPS_ALL:
                    if( sfdpsExtEnabled )
                    {
                        flat = flattenSfdpsExtension( (SfdpsExtension)data );
                    }
                    break;
                case EXT_SMES:
                case EXT_SMES_ALL:
                    if( smesExtEnabled )
                    {
                        flat = flattenSmesExtension( (SmesExtension)data );
                    }
                    break;
                case EXT_TMI:
                case EXT_TMI_ALL:
                    if (tmiExtEnabled)
                    {
                        flat = flattenTmiExtension((TmiExtension) data);
                    }
                    break;
                default:
                    log.warn("Unable to flatten " + captureData.getType());
            }
        }

        if (flat != null && (flat instanceof Record))
        {
            ((Record)flat).setRecordIdentifier(captureData.getRecordId());
            ((Record)flat).setRecordTimestamp(captureData.getRecordTimestamp());
            ((Record)flat).setTimestamp(captureData.getTimestamp());
            
            if (flat instanceof GufiRecord)
            {
                if (captureData.getGufi() != null)
                {
                    ((GufiRecord)flat).setGufi(captureData.getGufi());
                }
                ((Record)flat).setSurfaceAirportName(captureData.getSurfaceAirportName());
            }
        }

        return (T)flat;
    }

    private FlatMatmFlight flattenMatmFlight (MatmFlight data)
    {
        FlatMatmFlight flat = null;

        if (data != null)
        {
            flat = new FlatMatmFlight ();
            data.copyTo(flat);
            
            if (filterUpdateSources)
            {
                flat.setUpdateSources(null);
            }
            if( filterFacilityIdList )
            {
                flat.setFacilityIdList( null );
            }
            if( filterPreDepartureFlightPlanList )
            {
                flat.setPreDepartureFlightPlanList( null );
            }
        }
        return flat;
    }
    
    private FlatMatmAircraft flattenMatmAircraft (MatmAircraft data)
    {
        FlatMatmAircraft flat = null;
        
        if (data != null)
        {
            flat = new FlatMatmAircraft();
            data.copyTo(flat);
            
            if (filterUpdateSources)
            {
                flat.setUpdateSources(null);
            }
        }
        
        return flat;
    }

    private FlatAdsbExtension flattenAdsbExtension (AdsbExtension data)
    {
        FlatAdsbExtension flat = null;

        if (data != null)
        {
            flat = new FlatAdsbExtension ();
            data.copyTo(flat);
        }

        return flat;
    }

    private FlatAsdexExtension flattenAsdexExtension (AsdexExtension data)
    {
        FlatAsdexExtension flat = null;

        if (data != null)
        {
            flat = new FlatAsdexExtension ();
            data.copyTo(flat);
        }

        return flat;
    }

    private FlatCat11Extension flattenCat11Extension (Cat11Extension data)
    {
        FlatCat11Extension flat = null;

        if (data != null)
        {
            flat = new FlatCat11Extension ();
            data.copyTo(flat);
        }

        return flat;
    }

    private FlatCat62Extension flattenCat62Extension (Cat62Extension data)
    {
        FlatCat62Extension flat = null;

        if (data != null)
        {
            flat = new FlatCat62Extension ();
            data.copyTo(flat);
        }

        return flat;
    }

    private FlatDerivedExtension flattenDerivedExtension (DerivedExtension data)
    {
        FlatDerivedExtension flat = null;

        if (data != null)
        {
            flat = new FlatDerivedExtension ();
            data.copyTo(flat);
        }
        return flat;
    }

    private FlatTbfmExtension flattenTbfmExtension (TbfmExtension data)
    {
        FlatTbfmExtension flat = null;

        if (data != null)
        {
            flat = new FlatTbfmExtension ();
            data.copyTo(flat);
        }

        return flat;
    }

    private FlatTfmExtension flattenTfmExtension (TfmExtension data)
    {
        FlatTfmExtension flat = null;

        if (data != null)
        {
            flat = new FlatTfmExtension ();
            data.copyTo(flat);
        }

        return flat;
    }

    private FlatTfmTfdmExtension flattenTfmTfdmExtension (TfmTfdmExtension data)
    {
        FlatTfmTfdmExtension flat = null;

        if (data != null)
        {
            flat = new FlatTfmTfdmExtension ();
            data.copyTo(flat);
        }

        return flat;
    }    
    
    private FlatTfmsFlightTraversalExtension flattenTfmFlightTraversalExtension(TfmsFlightTraversalExtension data)
    {
        FlatTfmsFlightTraversalExtension flat = null;

        if (data != null)
        {
            flat = new FlatTfmsFlightTraversalExtension();
            data.copyTo(flat);
        }

        return flat;
    }

    private FlatMatmAirlineMessageExtension flattenMatmAirlineMessageExtension (MatmAirlineMessageExtension data)
    {
        FlatMatmAirlineMessageExtension flat = null;

        if (data != null)
        {
            flat = new FlatMatmAirlineMessageExtension ();
            data.copyTo(flat);
        }

        return flat;
    }

    private FlatIdacExtension flattenIdacExtension(IdacExtension data)
    {
        FlatIdacExtension flat = null;

        if (data != null)
        {
            flat = new FlatIdacExtension();
            data.copyTo(flat);
        }

        return flat;
    }

    private FlatSurfaceModelExtension flattenSurfaceModelExtension(SurfaceModelExtension data)
    {
        FlatSurfaceModelExtension flat = null;

        if (data != null)
        {
            flat = new FlatSurfaceModelExtension();
            data.copyTo(flat);
        }

        return flat;
    }

    private FlatAefsExtension flattenAefsExtension(AefsExtension data)
    {
        FlatAefsExtension flat = null;

        if (data != null)
        {
            flat = new FlatAefsExtension();
            data.copyTo(flat);
        }

        return flat;
    }

    private FlatPosition flattenPosition(MatmPositionUpdate data)
    {
        FlatPosition flat = null;

        if (data != null)
        {
            flat = new FlatPosition();
            data.copyTo(flat);
        }

        return flat;
    }
    
    private FlatSfdpsExtension flattenSfdpsExtension( SfdpsExtension data )
    {
        FlatSfdpsExtension flat = null;

        if (data != null)
        {
            flat = new FlatSfdpsExtension();
            data.copyTo(flat);
        }

        return flat;
    }

    private FlatSmesExtension flattenSmesExtension( SmesExtension data )
    {
        FlatSmesExtension flat = null;

        if (data != null)
        {
            flat = new FlatSmesExtension();
            data.copyTo(flat);
        }

        return flat;
    }

    private FlatTmiExtension flattenTmiExtension(TmiExtension data)
    {
        FlatTmiExtension flat = null;

        if (data != null)
        {
            flat = new FlatTmiExtension();
            data.copyTo(flat);
        }

        return flat;
    }
    
    public void setFilterUpdateSources(boolean filterUpdateSources)
    {
        this.filterUpdateSources = filterUpdateSources;
    }

    public void setFilterFacilityIdList( boolean filterFacilityIdList )
    {
        this.filterFacilityIdList = filterFacilityIdList;
    }

    public void setFilterPreDepartureFlightPlanList( boolean filterPreDepartureFlightPlanList )
    {
        this.filterPreDepartureFlightPlanList = filterPreDepartureFlightPlanList;
    }

    public void setAdsbExtEnabled( boolean adsbExtEnabled )
    {
        this.adsbExtEnabled = adsbExtEnabled;
    }

    public void setAsdexExtEnabled( boolean asdexExtEnabled )
    {
        this.asdexExtEnabled = asdexExtEnabled;
    }

    public void setCat11ExtEnabled( boolean cat11ExtEnabled )
    {
        this.cat11ExtEnabled = cat11ExtEnabled;
    }

    public void setCat62ExtEnabled( boolean cat62ExtEnabled )
    {
        this.cat62ExtEnabled = cat62ExtEnabled;
    }

    public void setDerivedExtEnabled( boolean derivedExtEnabled )
    {
        this.derivedExtEnabled = derivedExtEnabled;
    }

    public void setTbfmExtEnabled( boolean tbfmExtEnabled )
    {
        this.tbfmExtEnabled = tbfmExtEnabled;
    }

    public void setTfmExtEnabled( boolean tfmExtEnabled )
    {
        this.tfmExtEnabled = tfmExtEnabled;
    }

    public void setTfmTfdmExtEnabled( boolean tfmTfdmExtEnabled )
    {
        this.tfmTfdmExtEnabled = tfmTfdmExtEnabled;
    }

    public void setTfmTraversalExtEnabled( boolean tfmTraversalExtEnabled )
    {
        this.tfmTraversalExtEnabled = tfmTraversalExtEnabled;
    }

    public void setMatmAirlineMsgExtEnabled( boolean matmAirlineMsgExtEnabled )
    {
        this.matmAirlineMsgExtEnabled = matmAirlineMsgExtEnabled;
    }

    public void setIdacExtEnabled( boolean idacExtEnabled )
    {
        this.idacExtEnabled = idacExtEnabled;
    }

    public void setSurfaceModelExtEnabled( boolean surfaceModelExtEnabled )
    {
        this.surfaceModelExtEnabled = surfaceModelExtEnabled;
    }

    public void setAefsExtEnabled( boolean aefsExtEnabled )
    {
        this.aefsExtEnabled = aefsExtEnabled;
    }

    public void setSfdpsExtEnabled( boolean sfdpsExtEnabled )
    {
        this.sfdpsExtEnabled = sfdpsExtEnabled;
    }

    public void setSmesExtEnabled( boolean smesExtEnabled )
    {
        this.smesExtEnabled = smesExtEnabled;
    }

    public void setTmiExtEnabled(boolean tmiExtEnabled)
    {
        this.tmiExtEnabled = tmiExtEnabled;
    }
}
