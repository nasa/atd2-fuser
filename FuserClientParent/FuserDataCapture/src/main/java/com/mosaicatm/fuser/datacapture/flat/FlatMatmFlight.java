package com.mosaicatm.fuser.datacapture.flat;

import java.util.Date;

import com.mosaicatm.fuser.datacapture.util.CaptureUtils;
import com.mosaicatm.matmdata.common.FavType;
import com.mosaicatm.matmdata.common.SurfaceFlightState;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class FlatMatmFlight
extends MatmFlight
implements GufiRecord
{
    private static final long serialVersionUID = -1337785100877115125L;

    private String recordIdentifier;
    private Date recordTimestamp;
    
    @Override
    public void setRecordIdentifier (String recordIdentifier)
    {
        this.recordIdentifier = recordIdentifier;
    }
    
    @Override
    public String getRecordIdentifier ()
    {
        return recordIdentifier;
    }
    
    @Override
    public void setRecordTimestamp (Date recordTimestamp)
    {
        this.recordTimestamp = recordTimestamp;
    }
    
    @Override
    public Date getRecordTimestamp ()
    {
        return recordTimestamp;
    }
    
    public Double getPositionAltitude ()
    {
        if (getPosition() != null)
            return getPosition().getAltitude();
        return null;
    }
    
    public Double getPositionHeading ()
    {
        if (getPosition() != null)
            return getPosition().getHeading();
        return null;
    }
    
    public Double getPositionLatitude ()
    {
        if (getPosition() != null)
            return getPosition().getLatitude();
        return null;
    }
    
    public Double getPositionLongitude ()
    {
        if (getPosition() != null)
            return getPosition().getLongitude();
        return null;
    }
    
    public String getPositionSource ()
    {
        if (getPosition() != null)
            return getPosition().getSource();
        return null;
    }
    
    public Double getPositionSpeed ()
    {
        if (getPosition() != null)
            return getPosition().getSpeed();
        return null;
    }
    
    public String getPositionAtcSector ()
    {
        if ((getPosition() != null) && (getPosition().getAtcSector() != null))
            return getPosition().getAtcSector().getValue();
        return null;
    }    
    
    public String getPositionStaticSector ()
    {
        if ((getPosition() != null) && (getPosition().getStaticSector() != null))
            return getPosition().getStaticSector().getValue();
        return null;
    }   
    
    public String getPositionDynamicSector ()
    {
        if ((getPosition() != null) && (getPosition().getDynamicSector() != null))
            return getPosition().getDynamicSector().getValue();
        return null;
    }     

    public String getPositionFav ()
    {
        if ((getPosition() != null) && (getPosition().getFav() != null))
            return getPosition().getFav().getValue();
        return null;
    }   

    public FavType getPositionFavType ()
    {
        if ((getPosition() != null) && (getPosition().getFavType() != null))
            return getPosition().getFavType().getValue();
        return null;
    }     
    
    public String getPositionFavModule ()
    {
        if ((getPosition() != null) && (getPosition().getFavModule() != null))
            return getPosition().getFavModule().getValue();
        return null;
    }       
    
    public Double getPositionDistanceTotal ()
    {
        if (getPosition() != null)
            return getPosition().getDistanceTotal();
        return null;
    }      
    
    public Date getPositionTimestamp ()
    {
        if (getPosition() != null)
            return getPosition().getTimestamp();
        return null;
    }
    
    public String getArrivalAerodromeIataName ()
    {
        if (getArrivalAerodrome() != null)
            return getArrivalAerodrome().getIataName();
        return null;
    }
    
    public String getArrivalAerodromeIcaoName ()
    {
        if (getArrivalAerodrome() != null)
            return getArrivalAerodrome().getIcaoName();
        return null;
    }
    
    public String getDepartureAerodromeIataName ()
    {
        if (getDepartureAerodrome() != null)
            return getDepartureAerodrome().getIataName();
        return null;
    }
    
    public String getDepartureAerodromeIcaoName ()
    {
        if (getDepartureAerodrome() != null)
            return getDepartureAerodrome().getIcaoName();
        return null;
    }

    
    public String getDepartureAerodromeFaaLid ()
    {
        if (getDepartureAerodrome() != null)
            return getDepartureAerodrome().getFaaLid();
        return null;
    }

    public String getArrivalAerodromeFaaLid ()
    {
        if (getArrivalAerodrome() != null)
            return getArrivalAerodrome().getFaaLid();
        return null;
    }    
    
    public String getFixListValues ()
    {
        if (getFixList() != null)
            return CaptureUtils.listAsString(getFixList(), " ");
        return null;
    }
    
    public String getSectorListValues ()
    {
        if (getSectorList() != null)
            return CaptureUtils.listAsString(getSectorList(), " ");
        return null;
    }
    
    public String getCenterListValues ()
    {
        if (getCenterList() != null)
            return CaptureUtils.listAsString(getCenterList(), " ");
        return null;
    }
    
    public Long getDepartureQueueDurationValue ()
    {
        if (getDepartureQueueDuration() != null)
            return CaptureUtils.durationToMillis(getDepartureQueueDuration());
        return null;
    }
    
    public Long getDepartureTaxiUndelayedDurationValue ()
    {
        if (getDepartureTaxiUndelayedDuration() != null)
            return CaptureUtils.durationToMillis(getDepartureTaxiUndelayedDuration());
        return null;
    }
    
    public Long getDepartureTaxiEstimatedDurationValue ()
    {
        if (getDepartureTaxiEstimatedDuration() != null)
            return CaptureUtils.durationToMillis(getDepartureTaxiEstimatedDuration());
        return null;
    }
    
    public Long getDepartureTaxiActualDurationValue ()
    {
        if (getDepartureTaxiActualDuration() != null)
            return CaptureUtils.durationToMillis(getDepartureTaxiActualDuration());
        return null;
    }
    
    public Date getDepartureRunwayMeteredTimeValue ()
    {
        if (getDepartureRunwayMeteredTime() != null && !getDepartureRunwayMeteredTime().isNil())
            return getDepartureRunwayMeteredTime().getValue().getValue();
        return null;
    }
    
    public Date getArrivalRunwayMeteredTimeValue() 
    {
        if (getArrivalRunwayMeteredTime() != null)
            return getArrivalRunwayMeteredTime().getValue();
        return null;
    }
    
    public Boolean getArrivalRunwayMeteredTimeIsFrozen()
    {
        if (getArrivalRunwayMeteredTime() != null)
            return getArrivalRunwayMeteredTime().isFrozen();
        return null;
    }
    
    public Boolean getDepartureRunwayMeteredTimeIsFrozen()
    {
        if (getDepartureRunwayMeteredTime() != null && !getDepartureRunwayMeteredTime().isNil())
            return getDepartureRunwayMeteredTime().getValue().isFrozen();
        return null;
    }
    
    public String getSurfaceAirportName()
    {
        if (getSurfaceAirport() != null)
            return getSurfaceAirport().getIataName();
        return null;
    }
    
    public String getUpdateSourcesValues ()
    {
        if (getUpdateSources() != null)
            return CaptureUtils.metaDataAsString(getUpdateSources(), " ");
        return null;
    }
    
    public Long getLongOnBoardDurationValue()
    {
        if (getLongOnBoardDuration() != null && !getLongOnBoardDuration().isNil())
            return CaptureUtils.durationToMillis(getLongOnBoardDuration().getValue());
        return null;
    }

    public String getControllerClearance()
    {
        if (getControllerClearanceData() != null &&
                        getControllerClearanceData().getControllerClearance() != null)
        {
            return getControllerClearanceData().getControllerClearance();
        }

        return null;
    }

    public String getPreviousControllerClearance()
    {
        if (getControllerClearanceData() != null &&
                        getControllerClearanceData().getPreviousControllerClearance() != null)
        {
            return getControllerClearanceData().getPreviousControllerClearance();
        }

        return null;
    }

    public Date getControllerClearanceTime()
    {
        if (getControllerClearanceData() != null &&
                        getControllerClearanceData().getControllerClearanceTime() != null)
        {
            return getControllerClearanceData().getControllerClearanceTime();
        }

        return null;
    }

    public Boolean isPushbackClearanceUndone()
    {
        if (getControllerClearanceData() != null &&
                        getControllerClearanceData().isPushbackClearanceUndone() != null)
        {
            return getControllerClearanceData().isPushbackClearanceUndone();
        }

        return null;
    }

    public Date getDepartureStandSurfaceMeteredTimeValue ()
    {
        if (getDepartureStandSurfaceMeteredTime() != null)
            return getDepartureStandSurfaceMeteredTime().getValue();
        return null;
    }

    public Date getDepartureMovementAreaSurfaceMeteredTimeValue ()
    {
        if (getDepartureMovementAreaSurfaceMeteredTime() != null)
            return getDepartureMovementAreaSurfaceMeteredTime().getValue();
        return null;
    }

    public Boolean getRunwayOperationalNecessity()
    {
        Boolean isOpNec = null;

        boolean hasArrivalOpNec = (isArrivalRunwayOperationalNecessity() != null);
        boolean hasDepartureOpNec = (isDepartureRunwayOperationalNecessity() != null);

        // If both arrival and departure are set,
        // use the departure flight state until the flight is off
        // then switch to arrival flight state
        if(hasArrivalOpNec && hasDepartureOpNec) 
        {
            if(getDepartureSurfaceFlightState().compareTo(SurfaceFlightState.OFF) < 0) {
                isOpNec = isDepartureRunwayOperationalNecessity();
            }else {
                isOpNec = isArrivalRunwayOperationalNecessity();
            }
        }else if(hasDepartureOpNec) 
        {
            isOpNec = isDepartureRunwayOperationalNecessity();
        }else if(hasArrivalOpNec) 
        {
            isOpNec = isArrivalRunwayOperationalNecessity();
        }

        return isOpNec;
    }

    public String getGateConflictValues()
    {
        String json = null;

        boolean hasArrivalGateConflict = (getArrivalGateConflict() != null && 
                        getArrivalGateConflict().getValue() != null);
        boolean hasDepartureGateConflict = (getDepartureGateConflict() != null && 
                        getDepartureGateConflict().getValue() != null);

        // If both arrival and departure are set,
        // use the departure flight state until the flight is off
        // then switch to arrival flight state
        if(hasArrivalGateConflict && hasDepartureGateConflict) 
        {
            if(getDepartureSurfaceFlightState().compareTo(SurfaceFlightState.OFF) < 0) {
                json = getDepartureGateConflictValues();
            }else {
                json = getArrivalGateConflictValues();
            }
        }else if(hasDepartureGateConflict) 
        {
            json = getDepartureGateConflictValues();
        }else if(hasArrivalGateConflict) 
        {
            json = getArrivalGateConflictValues();
        }

        return json;
    }

    public String getArrivalGateConflictValues()
    {
        String json = null;

        if (getArrivalGateConflict() != null && getArrivalGateConflict().getValue() != null)
        {
            json = CaptureUtils.toBulkCopyJson(getArrivalGateConflict().getValue());
        }

        return json;
    }

    public String getDepartureGateConflictValues()
    {
        String json = null;

        if (getDepartureGateConflict() != null && getDepartureGateConflict().getValue() != null)
        {
            json = CaptureUtils.toBulkCopyJson(getDepartureGateConflict().getValue());
        }

        return json;
    }

    public String getApreqRestrictionIds(){
        String json = null;

        if (getFlightRestrictions() != null && getFlightRestrictions().getValue() != null
                        && getFlightRestrictions().getValue().getApreqRestrictionIds() != null
                        && !getFlightRestrictions().getValue().getApreqRestrictionIds().isEmpty())
        {
            json = CaptureUtils.toBulkCopyJson(getFlightRestrictions().getValue().getApreqRestrictionIds());
        }

        return json;
    }

    public String getMitRestrictionIds(){
        String json = null;

        if (getFlightRestrictions() != null && getFlightRestrictions().getValue() != null
                        && getFlightRestrictions().getValue().getMitRestrictionIds() != null
                        && !getFlightRestrictions().getValue().getMitRestrictionIds().isEmpty())
        {
            json = CaptureUtils.toBulkCopyJson(getFlightRestrictions().getValue().getMitRestrictionIds());
        }

        return json;
    }

    public String getGroundStopRestrictionIds(){
        String json = null;

        if (getFlightRestrictions() != null && getFlightRestrictions().getValue() != null
                        && getFlightRestrictions().getValue().getGroundStopRestrictionIds() != null
                        && !getFlightRestrictions().getValue().getGroundStopRestrictionIds().isEmpty())
        {
            json = CaptureUtils.toBulkCopyJson(getFlightRestrictions().getValue().getGroundStopRestrictionIds());
        }

        return json;
    }

    public String getRunwayRateRestrictionIds(){
        String json = null;

        if (getFlightRestrictions() != null && getFlightRestrictions().getValue() != null
                        && getFlightRestrictions().getValue().getRunwayRateRestrictionIds() != null
                        && !getFlightRestrictions().getValue().getRunwayRateRestrictionIds().isEmpty())
        {
            json = CaptureUtils.toBulkCopyJson(getFlightRestrictions().getValue().getRunwayRateRestrictionIds());
        }

        return json;
    }

    public String getFixClosureIds(){
        String json = null;

        if (getFlightRestrictions() != null && getFlightRestrictions().getValue() != null
                        && getFlightRestrictions().getValue().getFixClosureIds() != null
                        && !getFlightRestrictions().getValue().getFixClosureIds().isEmpty())
        {
            json = CaptureUtils.toBulkCopyJson(getFlightRestrictions().getValue().getFixClosureIds());
        }

        return json;
    }

    public String getDelayTmiType(){
        String tmiType = null;
        if(getFlightDelays() != null && getFlightDelays().getTmiType() != null){
            tmiType = getFlightDelays().getTmiType().toString();
        }
        return tmiType;
    }
    
    public Boolean isDelayReportRequired() 
    {
        Boolean reportRequired = null;
        
        if (getFlightDelays() != null)
            reportRequired = getFlightDelays().isReportRequired();
        
        return reportRequired;
    }
    
    public String getScratchPadValues()
    {
        String json = null;

        if (this.getScratchPad() != null &&
            this.getScratchPad().getEntries() != null &&
            !this.getScratchPad().getEntries().isEmpty())
        {
            json = CaptureUtils.toBulkCopyJson(this.getScratchPad());
        }

        return json;
    }
    
    public String getDelayChargeTo(){
        String chargeTo = null;
        if(getFlightDelays() != null){
            chargeTo = getFlightDelays().getChargeTo();
        }
        return chargeTo;
    }

    public String getDelayImpactingConditionPrimary(){
        String condition = null;
        if(getFlightDelays() != null && getFlightDelays().getImpactingConditionPrimary() != null){
            condition = getFlightDelays().getImpactingConditionPrimary().toString();
        }
        return condition;
    }

    public String getDelayImpactingConditionSecondary(){
        String condition = null;
        if(getFlightDelays() != null){
            condition = getFlightDelays().getImpactingConditionSecondary();
        }
        return condition;
    }

    public String getDelayRemarks(){
        String remarks = null;
        if(getFlightDelays() != null){
            remarks = getFlightDelays().getRemarks();
        }
        return remarks;
    }
    
    public String getFacilityIds(){
        String json = null;

        if ((getFacilityIdList() != null) && 
                (getFacilityIdList().getFacilityIds() != null))
        {
            json = CaptureUtils.toBulkCopyJson(getFacilityIdList());
        }

        return json;
    }    
    
    public String getPreDepartureFlightPlans(){
        String json = null;

        if (getPreDepartureFlightPlanList() != null)
        {
            json = CaptureUtils.toBulkCopyJson(getPreDepartureFlightPlanList());
        }

        return json;
    }
    
    public String getSurfaceFlightState(){
        String rtnState = null;
        SurfaceFlightState flightState = null;

        boolean hasArrivalState = (getArrivalSurfaceFlightState() != null && 
                        !getArrivalSurfaceFlightState().equals(SurfaceFlightState.UNKNOWN));
        boolean hasDepartureState = (getDepartureSurfaceFlightState() != null && 
                        !getDepartureSurfaceFlightState().equals(SurfaceFlightState.UNKNOWN));

        // If both arrival and departure flight states are set,
        // use the departure flight state until the flight is off
        // then switch to arrival flight state
        if(hasArrivalState && hasDepartureState) 
        {
            if(getDepartureSurfaceFlightState().compareTo(SurfaceFlightState.OFF) < 0) {
                flightState = getDepartureSurfaceFlightState();
            }else {
                flightState = getArrivalSurfaceFlightState();
            }
        }else if(hasDepartureState) 
        {
            flightState = getDepartureSurfaceFlightState();
        }else if(hasArrivalState) 
        {
            flightState = getArrivalSurfaceFlightState();
        }

        if (flightState != null)
            rtnState = flightState.toString();
            
        return rtnState;
    }

    public String getPredepartureArrivalAerodromeIataName()
    {
        String name = null;
        if (getPredepartureArrivalAerodrome() != null)
        {
            name = getPredepartureArrivalAerodrome().getIataName();
        }
        return name;
    }

    public String getPredepartureArrivalAerodromeFaaLid()
    {
        String name = null;
        if (getPredepartureArrivalAerodrome() != null)
        {
            name = getPredepartureArrivalAerodrome().getFaaLid();
        }
        return name;
    }
    
    @Override
    public void setSurfaceAirportName( String airportName )
    {
        // do nothing
    }
}
