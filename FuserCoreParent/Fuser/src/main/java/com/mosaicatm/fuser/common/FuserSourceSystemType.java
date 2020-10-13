package com.mosaicatm.fuser.common;

import java.util.Objects;

import com.mosaicatm.fuser.transform.matm.airline.AirlineDataSource;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.SfdpsMessageTypeType;
import com.mosaicatm.matmdata.flight.extension.TfmMessageTypeType;

public enum FuserSourceSystemType
{
    ASDEX (FuserSource.ASDEX, null),

    /**
     * Any AIRLINE system (both FlightHub and FlightStats)
     */
    AIRLINE (FuserSource.AIRLINE, null),

    /**
     * Only FlightHub
     */
    AIRLINE_FLIGHTHUB (FuserSource.AIRLINE, AirlineDataSource.FLIGHTHUB.name()),

    /**
     * Only FlightHub EOBT source
     */
    AIRLINE_FLIGHTHUB_EOBT (FuserSource.AIRLINE, AirlineDataSource.FLIGHTHUB_EOBT.name()),

    /**
     * Only FlightStats
     */
    AIRLINE_FLIGHTSTATS (FuserSource.AIRLINE, AirlineDataSource.FLIGHTSTATS.name()),

    FLIGHTHUB_POSITION (FuserSource.FLIGHTHUB_POSITION, null),
    FMC (FuserSource.FMC, null),
    FUSER (FuserSource.FUSER, null),
    IDAC (FuserSource.IDAC, null),
    STARS (FuserSource.STARS, null),
    
    SFDPS (FuserSource.SFDPS, null),    
    SFDPS_AH (FuserSource.SFDPS, SfdpsMessageTypeType.AH.name()),
    SFDPS_BA (FuserSource.SFDPS, SfdpsMessageTypeType.BA.name()),
    SFDPS_CL (FuserSource.SFDPS, SfdpsMessageTypeType.CL.name()),
    SFDPS_DH (FuserSource.SFDPS, SfdpsMessageTypeType.DH.name()),
    SFDPS_ET (FuserSource.SFDPS, SfdpsMessageTypeType.ET.name()),
    SFDPS_FH (FuserSource.SFDPS, SfdpsMessageTypeType.FH.name()),
    SFDPS_HF (FuserSource.SFDPS, SfdpsMessageTypeType.HF.name()),
    SFDPS_HH (FuserSource.SFDPS, SfdpsMessageTypeType.HH.name()),
    SFDPS_HP (FuserSource.SFDPS, SfdpsMessageTypeType.HP.name()),
    SFDPS_HT (FuserSource.SFDPS, SfdpsMessageTypeType.HT.name()),
    SFDPS_HU (FuserSource.SFDPS, SfdpsMessageTypeType.HU.name()),
    SFDPS_HV (FuserSource.SFDPS, SfdpsMessageTypeType.HV.name()),
    SFDPS_HX (FuserSource.SFDPS, SfdpsMessageTypeType.HX.name()),
    SFDPS_HZ (FuserSource.SFDPS, SfdpsMessageTypeType.HZ.name()),
    SFDPS_IH (FuserSource.SFDPS, SfdpsMessageTypeType.IH.name()),
    SFDPS_LH (FuserSource.SFDPS, SfdpsMessageTypeType.LH.name()),
    SFDPS_NI (FuserSource.SFDPS, SfdpsMessageTypeType.NI.name()),
    SFDPS_NL (FuserSource.SFDPS, SfdpsMessageTypeType.NL.name()),            
    SFDPS_NP (FuserSource.SFDPS, SfdpsMessageTypeType.NP.name()),
    SFDPS_NU (FuserSource.SFDPS, SfdpsMessageTypeType.NU.name()),
    SFDPS_OH (FuserSource.SFDPS, SfdpsMessageTypeType.OH.name()),
    SFDPS_PH (FuserSource.SFDPS, SfdpsMessageTypeType.PH.name()),
    SFDPS_PT (FuserSource.SFDPS, SfdpsMessageTypeType.PT.name()),
    SFDPS_RE (FuserSource.SFDPS, SfdpsMessageTypeType.RE.name()),
    SFDPS_RH (FuserSource.SFDPS, SfdpsMessageTypeType.RH.name()),
    SFDPS_TH (FuserSource.SFDPS, SfdpsMessageTypeType.TH.name()),
    SFDPS_BATCH_TH (FuserSource.SFDPS, SfdpsMessageTypeType.BATCH_TH.name()),
    SFDPS_DBRTFPI (FuserSource.SFDPS, SfdpsMessageTypeType.DBRTFPI.name()),

    TFM (FuserSource.TFM, null),
    TFM_ARRIVAL_INFORMATION (FuserSource.TFM, TfmMessageTypeType.ARRIVAL_INFORMATION.name()),
    TFM_BEACON_CODE_INFORMATION (FuserSource.TFM, TfmMessageTypeType.BEACON_CODE_INFORMATION.name()),
    TFM_BOUNDARY_CROSSING_UPDATE (FuserSource.TFM, TfmMessageTypeType.BOUNDARY_CROSSING_UPDATE.name()),
    TFM_DEPARTURE_INFORMATION (FuserSource.TFM, TfmMessageTypeType.DEPARTURE_INFORMATION.name()),
    TFM_FLIGHT_CONTROL (FuserSource.TFM, TfmMessageTypeType.FLIGHT_CONTROL.name()),
    TFM_FLIGHT_CREATE (FuserSource.TFM, TfmMessageTypeType.FLIGHT_CREATE.name()),
    TFM_FLIGHT_MODIFY (FuserSource.TFM, TfmMessageTypeType.FLIGHT_MODIFY.name()),
    TFM_FLIGHT_PLAN_AMENDMENT_INFORMATION (FuserSource.TFM, TfmMessageTypeType.FLIGHT_PLAN_AMENDMENT_INFORMATION.name()),
    TFM_FLIGHT_PLAN_CANCELLATION (FuserSource.TFM, TfmMessageTypeType.FLIGHT_PLAN_CANCELLATION.name()),
    TFM_FLIGHT_PLAN_INFORMATION (FuserSource.TFM, TfmMessageTypeType.FLIGHT_PLAN_INFORMATION.name()),
    TFM_FLIGHT_ROUTE (FuserSource.TFM, TfmMessageTypeType.FLIGHT_ROUTE.name()),
    TFM_FLIGHT_SCHEDULE_ACTIVATE (FuserSource.TFM, TfmMessageTypeType.FLIGHT_SCHEDULE_ACTIVATE.name()),
    TFM_FLIGHT_SECTORS (FuserSource.TFM, TfmMessageTypeType.FLIGHT_SECTORS.name()),
    TFM_FLIGHT_TIMES (FuserSource.TFM, TfmMessageTypeType.FLIGHT_TIMES.name()),
    TFM_OCEANIC_REPORT (FuserSource.TFM, TfmMessageTypeType.OCEANIC_REPORT.name()),
    TFM_TRACK_INFORMATION (FuserSource.TFM, TfmMessageTypeType.TRACK_INFORMATION.name()),

    TFM_TFDM (FuserSource.TFM_TFDM, null ),

    /**
     * Any TMA source
     */
    TMA (FuserSource.TMA, null),

    /**
     * Only NASA TMA sources
     */
    TMA_NASA (FuserSource.TMA, "-NASA"),

    /**
     * Only TMA data from SWIM
     */
    TMA_SWIM (FuserSource.TMA, "-SWIM"),

    AMS_SWIM (FuserSource.AMS, "-SWIM"),

    ADSB (FuserSource.ADSB, null),
    CAT11 (FuserSource.CAT_11, null),
    CAT62 (FuserSource.CAT_62, null),

    AEFS (FuserSource.AEFS, null),

    SMES (FuserSource.SMES, null),

    UNKNOWN (FuserSource.UNKNOWN, null);

    private final FuserSource source;
    private final String systemSuffix;

    private FuserSourceSystemType(FuserSource source, String system)
    {
        this.source = source;
        this.systemSuffix = system;
    }

    public FuserSource getSource()
    {
        return source;
    }

    public String getSystem()
    {
        return systemSuffix;
    }
    
    public boolean matches( MatmFlight flight )
    {
        if( Objects.equals( source.value(), flight.getLastUpdateSource() ))
        {
            if( systemSuffix == null )
            {
                return( true );
            }
            else if( flight.getSystemId() != null )
            {
                return( flight.getSystemId().endsWith( systemSuffix ));
            }
        }
        
        return( false );
    }
}
