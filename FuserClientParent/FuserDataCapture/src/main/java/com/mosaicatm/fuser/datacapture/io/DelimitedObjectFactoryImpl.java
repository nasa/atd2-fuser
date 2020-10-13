package com.mosaicatm.fuser.datacapture.io;

import java.io.Writer;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.container.io.DelimitedObjectWriter;
import com.mosaicatm.fuser.datacapture.CaptureType;
import com.mosaicatm.fuser.datacapture.flat.FlatAdsbExtension;
import com.mosaicatm.fuser.datacapture.flat.FlatAefsExtension;
import com.mosaicatm.fuser.datacapture.flat.FlatAsdexExtension;
import com.mosaicatm.fuser.datacapture.flat.FlatCat11Extension;
import com.mosaicatm.fuser.datacapture.flat.FlatCat62Extension;
import com.mosaicatm.fuser.datacapture.flat.FlatDerivedExtension;
import com.mosaicatm.fuser.datacapture.flat.FlatIdacExtension;
import com.mosaicatm.fuser.datacapture.flat.FlatMatmAircraft;
import com.mosaicatm.fuser.datacapture.flat.FlatMatmAirlineMessageExtension;
import com.mosaicatm.fuser.datacapture.flat.FlatMatmFlight;
import com.mosaicatm.fuser.datacapture.flat.FlatMatmFlightAllJoin;
import com.mosaicatm.fuser.datacapture.flat.FlatPosition;
import com.mosaicatm.fuser.datacapture.flat.FlatSfdpsExtension;
import com.mosaicatm.fuser.datacapture.flat.FlatSmesExtension;
import com.mosaicatm.fuser.datacapture.flat.FlatSurfaceModelExtension;
import com.mosaicatm.fuser.datacapture.flat.FlatTbfmExtension;
import com.mosaicatm.fuser.datacapture.flat.FlatTfmExtension;
import com.mosaicatm.fuser.datacapture.flat.FlatTfmTfdmExtension;
import com.mosaicatm.fuser.datacapture.flat.FlatTfmsFlightTraversalExtension;
import com.mosaicatm.fuser.datacapture.flat.FlatTmiExtension;

public class DelimitedObjectFactoryImpl
implements DelimitedObjectFactory
{    
    private final Log log = LogFactory.getLog(getClass());
    
    private final String SEQUENCE_ID = "sequenceId";
    
    public <T> DelimitedObjectWriter<T> getDelimitedObjectWriter(
        CaptureType type, Writer writer)
    {
        return getDelimitedObjectWriter(type, writer, null);
    }
    
    public <T> DelimitedObjectWriter<T> getDelimitedObjectWriter(
        CaptureType type, Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<?> delim = null;
        
        if (type != null && writer != null)
        {
            switch (type)
            {
                case MATM_FLIGHT:
                case MATM_FLIGHT_ALL:
                case MATM_FLIGHT_REMOVED:
                case MATM_FLIGHT_SUMMARY:
                    delim = getMatmFlightWriter(writer, fields);
                    break;
                case MATM_AIRCRAFT:
                case MATM_AIRCRAFT_ALL:
                case MATM_AIRCRAFT_REMOVED:
                case MATM_AIRCRAFT_SUMMARY:
                    delim = getMatmAircraftWriter(writer, fields);
                    break;
                case EXT_ADSB:
                case EXT_ADSB_ALL:
                    delim = getAdsbExtensionWriter(writer, fields);
                    break;
                case EXT_ASDEX:
                case EXT_ASDEX_ALL:
                    delim = getAsdexExtensionWriter(writer, fields);
                    break;
                case EXT_CAT11:
                case EXT_CAT11_ALL:
                    delim = getCat11ExtensionWriter(writer, fields);
                    break;
                case EXT_CAT62:
                case EXT_CAT62_ALL:
                    delim = getCat62ExtensionWriter(writer, fields);
                    break;
                case EXT_DERIVED:
                case EXT_DERIVED_ALL:
                    delim = getDerivedExtensionWriter(writer, fields);
                    break;
                case EXT_TBFM:
                case EXT_TBFM_ALL:
                    delim = getTbfmExtensionWriter(writer, fields);
                    break;
                case EXT_TFM:
                case EXT_TFM_ALL:
                    delim = getTfmExtensionWriter(writer, fields);
                    break;
                case EXT_TFM_TFDM:
                case EXT_TFM_TFDM_ALL:
                    delim = getTfmTfdmExtensionWriter(writer, fields);
                    break; 
                case EXT_TFM_TRAVERSAL:
                case EXT_TFM_TRAVERSAL_ALL:
                    delim = getTfmTraversalExtensionWriter(writer, fields);
                    break;                    
                case EXT_MATM_AIRLINE_MESSAGE:
                case EXT_MATM_AIRLINE_MESSAGE_ALL:
                    delim = getMatmAirlineMessageExtensionWriter(writer, fields);
                    break;
                case EXT_IDAC:
                case EXT_IDAC_ALL:
                    delim = getIdacExtensionWriter(writer, fields);
                    break;
                case EXT_SURFACE_MODEL:
                case EXT_SURFACE_MODEL_ALL:
                    delim = getSurfaceModelExtensionWriter(writer, fields);
                    break;
                case EXT_AEFS:
                case EXT_AEFS_ALL:
                    delim = getAefsExtensionWriter(writer, fields);
                    break;
                case EXT_SFDPS:
                case EXT_SFDPS_ALL:
                    delim = getSfdpsExtensionWriter(writer, fields);
                    break;
                case EXT_SMES:
                case EXT_SMES_ALL:
                    delim = getSmesExtensionWriter(writer, fields);
                    break;
                case EXT_TMI:
                case EXT_TMI_ALL:
                    delim = getTmiExtensionWriter(writer, fields);
                    break;
                case POSITION:
                case MATM_POSITION_ALL:
                    delim = getPositionWriter(writer, fields);
                    break;
                case MATM_FLIGHT_ALL_JOIN:
                    delim = getMatmFlightJoinWriter(writer, fields);
                    break;
                default:
                    log.warn("Unable to find delimited writer for " + type);
            }
        }
        
        return (DelimitedObjectWriter<T>)delim;
    }
    
    private DelimitedObjectWriter<FlatMatmFlight> getMatmFlightWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatMatmFlight> delim;
        if (fields != null)
        {
            // need to safeguard that we don't include sequenceId
            // because it is not in FlatMatmFlight but in the matm_flight table
            fields.remove(SEQUENCE_ID);
            delim = new DelimitedObjectWriter<>(writer, FlatMatmFlight.class, ',', fields);
        }
        else
        {
            delim = new DelimitedObjectWriter<>(writer, FlatMatmFlight.class, ',');
        }
        
        delim.setExtractJaxbElementValues( true );
        
        delim.addLongField("departureQueueDurationValue");
        delim.addLongField("departureTaxiUndelayedDurationValue");
        delim.addLongField("departureTaxiEstimatedDurationValue");
        delim.addLongField("departureTaxiActualDurationValue");
        delim.addLongField("longOnBoardDurationValue");
        
        return delim;
    }
    
    private DelimitedObjectWriter<FlatMatmAircraft> getMatmAircraftWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatMatmAircraft> delim;
        if (fields != null)
        {
            // need to safeguard that we don't include sequenceId
            // because it is not in FlatMatmFlight but in the matm_flight table
            fields.remove(SEQUENCE_ID);
            delim = new DelimitedObjectWriter<>(writer, FlatMatmAircraft.class, ',', fields);
        }
        else
        {
            delim = new DelimitedObjectWriter<>(writer, FlatMatmAircraft.class, ',');
        }
        
        delim.setExtractJaxbElementValues( true );
        
        return delim;
    }
    
    private DelimitedObjectWriter<FlatTfmExtension> getTfmExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatTfmExtension> delim;
        if (fields != null)
            delim = new DelimitedObjectWriter<>(writer, FlatTfmExtension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatTfmExtension.class, ',');

        delim.setExtractJaxbElementValues( true );
                
        return delim;
    }

    private DelimitedObjectWriter<FlatTfmTfdmExtension> getTfmTfdmExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatTfmTfdmExtension> delim;
        if (fields != null)
            delim = new DelimitedObjectWriter<>(writer, FlatTfmTfdmExtension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatTfmTfdmExtension.class, ',');

        delim.setExtractJaxbElementValues( true );
        
        return delim;
    }
    
    private DelimitedObjectWriter<FlatTfmsFlightTraversalExtension> getTfmTraversalExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatTfmsFlightTraversalExtension> delim;
        if (fields != null)
            delim = new DelimitedObjectWriter<>(writer, FlatTfmsFlightTraversalExtension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatTfmsFlightTraversalExtension.class, ',');

        delim.setExtractJaxbElementValues( true );
        
        return delim;
    }    
    
    private DelimitedObjectWriter<FlatTbfmExtension> getTbfmExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatTbfmExtension> delim;
        if (fields != null)
            delim = new DelimitedObjectWriter<>(writer, FlatTbfmExtension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatTbfmExtension.class, ',');

        delim.setExtractJaxbElementValues( true );
        
        return delim;
    }
    
    private DelimitedObjectWriter<FlatMatmAirlineMessageExtension> getMatmAirlineMessageExtensionWriter (Writer writer, Set<String> fields)
    {        
        DelimitedObjectWriter<FlatMatmAirlineMessageExtension> delim;
        if (fields != null)
            delim = new DelimitedObjectWriter<>(writer, FlatMatmAirlineMessageExtension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatMatmAirlineMessageExtension.class, ',');

        delim.setExtractJaxbElementValues( true );
        
        return delim;
    }

    private DelimitedObjectWriter<FlatDerivedExtension> getDerivedExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatDerivedExtension> delim;
        if (fields != null)
            delim = new DelimitedObjectWriter<>(writer, FlatDerivedExtension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatDerivedExtension.class, ',');

        delim.setExtractJaxbElementValues( true );
        
        delim.addLongField("departureRunwayCrossedDurationValue");
        delim.addLongField("departureRampAreaDurationValue");
        delim.addLongField("departureRampAreaStopDurationValue");
        delim.addLongField("departureMovementAreaDurationValue");
        delim.addLongField("departureMovementAreaStopDurationValue");
        delim.addLongField("departureRunwayCrossingsDurationValue");
        delim.addLongField("departureTaxiDerivedDurationValue");
        delim.addLongField("departureRunwayOccupancyDurationValue");
        delim.addLongField("departureRunwaySeparationDurationValue");
        delim.addLongField("arrivalRunwaySeparationDurationValue");
        delim.addLongField("arrivalRunwayOccupancyDurationValue");
        delim.addLongField("arrivalMovementAreaStopDurationValue");
        delim.addLongField("arrivalRunwayCrossingsDurationValue");
        delim.addLongField("arrivalRampAreaDurationValue");
        delim.addLongField("arrivalRampAreaStopDurationValue");
        delim.addLongField("arrivalTaxiUndelayedDurationValue");
        delim.addLongField("arrivalTaxiEstimatedDurationValue");
        delim.addLongField("arrivalTaxiActualDurationValue");
        delim.addLongField("arrivalTaxiDerivedDurationValue");
        delim.addLongField("arrivalMovementAreaDurationValue");
        delim.addLongField("arrivalMovementAreaEstimatedDurationValue");
        delim.addLongField("arrivalMovementAreaUndelayedDurationValue");
        delim.addLongField("arrivalRampAreaEstimatedDurationValue");
        delim.addLongField("arrivalRampAreaUndelayedDurationValue");
        
        return delim;
    }

    private DelimitedObjectWriter<FlatCat62Extension> getCat62ExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatCat62Extension> delim;
        if( fields != null )
            delim = new DelimitedObjectWriter<>(writer, FlatCat62Extension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatCat62Extension.class, ',');
        
        delim.setExtractJaxbElementValues( true );

        return delim;
    }
    
    private DelimitedObjectWriter<FlatCat11Extension> getCat11ExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatCat11Extension> delim;
        if( fields != null )
            delim = new DelimitedObjectWriter<>(writer, FlatCat11Extension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatCat11Extension.class, ',');
        
        delim.setExtractJaxbElementValues( true );

        return delim;
    }

    private DelimitedObjectWriter<FlatAsdexExtension> getAsdexExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatAsdexExtension> delim;
        if (fields != null)
            delim = new DelimitedObjectWriter<>(writer, FlatAsdexExtension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatAsdexExtension.class, ',');

        delim.setExtractJaxbElementValues( true );
        
        return delim;
    }

    private DelimitedObjectWriter<FlatAdsbExtension> getAdsbExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatAdsbExtension> delim;
        if( fields != null )
            delim = new DelimitedObjectWriter<>(writer, FlatAdsbExtension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatAdsbExtension.class, ',');

        delim.setExtractJaxbElementValues( true );
        
        return delim;
    }

    private DelimitedObjectWriter<FlatPosition> getPositionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatPosition> delim;
        if (fields != null)
            delim = new DelimitedObjectWriter<>(writer, FlatPosition.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatPosition.class, ',');

        delim.setExtractJaxbElementValues( true );
        
        return delim;
    }
    
    private DelimitedObjectWriter<FlatIdacExtension> getIdacExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatIdacExtension> delim;
        if (fields != null)
            delim = new DelimitedObjectWriter<>(writer, FlatIdacExtension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatIdacExtension.class, ',');

        delim.setExtractJaxbElementValues( true );
        
        return delim;
    }
    
    private DelimitedObjectWriter<FlatSurfaceModelExtension> getSurfaceModelExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatSurfaceModelExtension> delim;
        if (fields != null)
            delim = new DelimitedObjectWriter<>(writer, FlatSurfaceModelExtension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatSurfaceModelExtension.class, ',');

        delim.setExtractJaxbElementValues( true );
        
        return delim;
    }
    
    private DelimitedObjectWriter<FlatAefsExtension> getAefsExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatAefsExtension> delim;
        if (fields != null)
            delim = new DelimitedObjectWriter<>(writer, FlatAefsExtension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatAefsExtension.class, ',');

        delim.setExtractJaxbElementValues( true );
        
        return delim;
    }
    
    private DelimitedObjectWriter<FlatMatmFlightAllJoin> getMatmFlightJoinWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatMatmFlightAllJoin> delim;
        if (fields != null)
        { 
            // need to safeguard that we don't include sequenceId
            // because it is not in FlatMatmFlight but in the matm_flight table
            fields.remove(SEQUENCE_ID);
            delim = new DelimitedObjectWriter<>(writer, FlatMatmFlightAllJoin.class, ',', fields);
        } 
        else
        {
            delim = new DelimitedObjectWriter<>(writer, FlatMatmFlightAllJoin.class, ',');
        }

        delim.setExtractJaxbElementValues( true );
        
        return delim;
    }

    private DelimitedObjectWriter<FlatSfdpsExtension> getSfdpsExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatSfdpsExtension> delim;
        if (fields != null)
            delim = new DelimitedObjectWriter<>(writer, FlatSfdpsExtension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatSfdpsExtension.class, ',');

        delim.setExtractJaxbElementValues( true );
        
        return delim;
    }

    private DelimitedObjectWriter<FlatSmesExtension> getSmesExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatSmesExtension> delim;
        if (fields != null)
            delim = new DelimitedObjectWriter<>(writer, FlatSmesExtension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatSmesExtension.class, ',');
        
        delim.addLongField("stid");

        delim.setExtractJaxbElementValues( true );

        return delim;
    }

    private DelimitedObjectWriter<FlatTmiExtension> getTmiExtensionWriter (Writer writer, Set<String> fields)
    {
        DelimitedObjectWriter<FlatTmiExtension> delim;
        if (fields != null)
            delim = new DelimitedObjectWriter<>(writer, FlatTmiExtension.class, ',', fields);
        else
            delim = new DelimitedObjectWriter<>(writer, FlatTmiExtension.class, ',');

        delim.setExtractJaxbElementValues( true );

        return delim;
    }
}
