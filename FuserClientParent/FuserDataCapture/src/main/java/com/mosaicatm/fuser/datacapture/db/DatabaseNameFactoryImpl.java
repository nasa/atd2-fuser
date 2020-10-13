package com.mosaicatm.fuser.datacapture.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.datacapture.CaptureType;

public class DatabaseNameFactoryImpl
implements DatabaseNameFactory
{
    private final Log log = LogFactory.getLog(getClass());
    
    @Override
    public String getDatabaseName(CaptureType type)
    {
        String name = null;
        
        if (type != null)
        {
            switch (type)
            {
                case MATM_FLIGHT:   name = "matm_flight";
                    break;
                case MATM_FLIGHT_ALL:   name = "matm_flight_all";
                    break;                    
                case MATM_FLIGHT_SUMMARY: name = "matm_flight_summary";
                    break;
                case MATM_AIRCRAFT: name = "matm_aircraft";
                    break;
                case MATM_AIRCRAFT_ALL: name = "matm_aircraft_all";
                    break;
                case MATM_AIRCRAFT_REMOVED: name = "matm_aircraft_removed";
                    break;
                case MATM_AIRCRAFT_SUMMARY: name = "matm_aircraft_summary";
                    break;
                case EXT_ADSB:      name = "adsb_extension";
                    break;
                case EXT_ADSB_ALL:      name = "adsb_extension_all";
                    break;                    
                case EXT_ASDEX:     name = "asdex_extension";
                    break;
                case EXT_ASDEX_ALL:     name = "asdex_extension_all";
                    break;                    
                case EXT_CAT11:     name = "cat11_extension";
                    break;
                case EXT_CAT11_ALL:     name = "cat11_extension_all";
                    break;                    
                case EXT_CAT62:     name = "cat62_extension";
                    break;
                case EXT_CAT62_ALL:     name = "cat62_extension_all";
                    break;                    
                case EXT_DERIVED:   name = "derived_extension";
                    break;
                case EXT_DERIVED_ALL:   name = "derived_extension_all";
                    break;                                     
                case EXT_TBFM:      name = "tbfm_extension";
                    break;
                case EXT_TBFM_ALL:      name = "tbfm_extension_all";
                    break;                    
                case EXT_TFM:       name = "tfm_extension";
                    break;
                case EXT_TFM_ALL:       name = "tfm_extension_all";
                    break;
                case EXT_TFM_TRAVERSAL:       name = "tfm_traversal_extension";
                    break;
                case EXT_TFM_TRAVERSAL_ALL:       name = "tfm_traversal_extension_all";
                    break;       
                case EXT_TFM_TFDM:       name = "tfm_tfdm_extension";
                    break;
                case EXT_TFM_TFDM_ALL:       name = "tfm_tfdm_extension_all";
                    break;                      
                case EXT_MATM_AIRLINE_MESSAGE:   name = "matm_airline_message_extension";
                    break;
                case EXT_MATM_AIRLINE_MESSAGE_ALL:   name = "matm_airline_message_extension_all";
                    break;
                case EXT_IDAC:        name="idac_extension";
                    break;
                case EXT_IDAC_ALL:        name="idac_extension_all";
                    break;
                case EXT_SURFACE_MODEL:     name="surface_model_extension";
                    break;
                case EXT_SURFACE_MODEL_ALL:      name="surface_model_extension_all";
                    break;
                case EXT_AEFS:          name="aefs_extension";
                    break;
                case EXT_AEFS_ALL:          name="aefs_extension_all";
                    break;
                case MATM_POSITION_ALL:         name="matm_position_all";
                    break;
                case POSITION:      name="position";
                    break;
                case MATM_FLIGHT_REMOVED:    name="matm_flight_removed";
                    break;
                case MATM_FLIGHT_ALL_JOIN:   name="matm_flight_all_join";
                    break;
                case EXT_SFDPS:     name="sfdps_extension";
                    break;
                case EXT_SFDPS_ALL:     name="sfdps_extension_all";
                    break;
                case EXT_SMES:     name = "smes_extension";
                    break;
                case EXT_SMES_ALL:     name = "smes_extension_all";
                    break;
                case EXT_TMI:     name = "tmi_extension";
                    break;
                case EXT_TMI_ALL:     name = "tmi_extension_all";
                    break;
                default:
                    log.warn("Unable to generate name for type " + type);
            }
        }
        
        return name;
    }

    @Override
    public String getDatabaseName(CaptureType type, String identifier)
    {
        String name = getDatabaseName(type);
        
        if (name != null && !name.trim().isEmpty())
            name = name + "_" + identifier;
        
        return name;
    }
}
