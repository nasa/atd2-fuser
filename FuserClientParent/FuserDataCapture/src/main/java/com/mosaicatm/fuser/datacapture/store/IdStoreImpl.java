package com.mosaicatm.fuser.datacapture.store;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.datacapture.CaptureType;

public class IdStoreImpl
implements IdStore
{
    private final Log log = LogFactory.getLog(getClass());
    
    private final Map<String, IdSet> currentStore;
    
    public IdStoreImpl()
    {
        currentStore = new ConcurrentHashMap<>();
    }

    @Override
    public IdSet generateRecord(String gufi, List<CaptureType> types)
    {
        IdSet idSet = null;
        if (gufi != null)
        {
            String recordId = createNewId();

            idSet = currentStore.get(gufi);
            if (idSet != null)
            {
                idSet = new IdSet(gufi, recordId, idSet);
            }
            else
            {
                idSet = new IdSet(gufi, recordId);
            }
 
            populateIds(idSet, types, recordId);
            currentStore.put(gufi, idSet);
        }
        
        return idSet;
    }
    
    private void populateIds(IdSet idSet, List<CaptureType> types, String id)
    { 
        if (idSet != null && types != null && !types.isEmpty())
        {
            for (CaptureType type : types)
            {

                switch (type)
                {
                    case MATM_FLIGHT:
                    case MATM_FLIGHT_ALL:
                    case MATM_FLIGHT_SUMMARY:
                    case MATM_FLIGHT_REMOVED:
                        idSet.setMatmId(id);
                        break;
                    case MATM_AIRCRAFT:
                    case MATM_AIRCRAFT_ALL:
                    case MATM_AIRCRAFT_SUMMARY:
                    case MATM_AIRCRAFT_REMOVED:
                        idSet.setAircraftId(id);
                        break;
                    case EXT_ADSB:
                    case EXT_ADSB_ALL:
                        idSet.setAdsbExtensionId(id);
                        break;
                    case EXT_ASDEX:
                    case EXT_ASDEX_ALL:
                        idSet.setAsdexExtensionId(id);
                        break;
                    case EXT_CAT11:
                    case EXT_CAT11_ALL:
                        idSet.setCat11ExtensionId(id);
                        break;
                    case EXT_CAT62:
                    case EXT_CAT62_ALL:
                        idSet.setCat62ExtensionId(id);
                        break;
                    case EXT_DERIVED:
                    case EXT_DERIVED_ALL:
                        idSet.setDerivedExtensionId(id);
                        break;
                    case EXT_TBFM:
                    case EXT_TBFM_ALL:
                        idSet.setTbfmExtensionId(id);
                        break;
                    case EXT_TFM:
                    case EXT_TFM_ALL:
                        idSet.setTfmExtensionId(id);
                        break;
                    case EXT_TFM_TFDM:
                    case EXT_TFM_TFDM_ALL:
                        idSet.setTfmTfdmExtensionId(id);
                        break;
                    case EXT_TFM_TRAVERSAL:
                    case EXT_TFM_TRAVERSAL_ALL:
                        idSet.setTfmTraversalExtensionId(id);
                        break;
                    case EXT_MATM_AIRLINE_MESSAGE:
                    case EXT_MATM_AIRLINE_MESSAGE_ALL:
                        idSet.setAirlineExtensionId(id);
                        break;
                    case POSITION:
                    case MATM_POSITION_ALL:
                        idSet.setPositionId(id);
                        break;
                    case EXT_IDAC:
                    case EXT_IDAC_ALL:
                        idSet.setIdacExtensionId(id);
                        break;
                    case EXT_SURFACE_MODEL:
                    case EXT_SURFACE_MODEL_ALL:
                        idSet.setSurfaceModelExtensionId(id);
                        break;
                    case EXT_AEFS:
                    case EXT_AEFS_ALL:
                        idSet.setAefsExtensionId(id);
                        break;
                    case MATM_FLIGHT_ALL_JOIN:
                        // don't really need one
                        break;
                    case EXT_SFDPS:
                    case EXT_SFDPS_ALL:
                        idSet.setSfdpsExtensionId(id);
                        break;
                    case EXT_SMES:
                    case EXT_SMES_ALL:
                        idSet.setSmesExtensionId(id);
                        break;
                    case EXT_TMI:
                    case EXT_TMI_ALL:
                        idSet.setTmiExtensionId(id);
                        break;
                    default:
                        log.warn("Unable to request id for " + type);
                }
            }
        }
    }

    @Override
    public IdSet remove(String gufi)
    {
        if (gufi != null)
        {
            return currentStore.remove(gufi);
        }
        
        return null;
    }
    
    private String createNewId()
    {
        return UUID.randomUUID().toString();
    }
    
}
