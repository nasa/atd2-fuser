package com.mosaicatm.fuser.datacapture;

public class FuserDataCaptureConfiguration
{
    private String matmPositionJmsUrl;
    private String matmPositionUpdateUrl;
    private String matmPositionUpdateEnvelopeUrl;

    private Boolean clockSyncEnabled;
    private Boolean matmFlightAirportFilterActive=false;
    private Boolean matmPositionUpdateAirportFilterActive=false;
    private Boolean surfaceAirportFilterActive=false;
    private String matmPositionUpdateAirportsOfInterest=null;
    private String matmFlightAirportsOfInterest=null;
    private Boolean syncMatmFlight=false;
    private Boolean syncMatmAircraft=false;
    private Boolean positionEnable=false;
    private Boolean matmPositionEnable=false;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private Boolean dropTables;

    private Boolean captureMatmFlightXml;
    private Boolean captureMatmFlightCsv;
    private Boolean captureMatmFlightAllXml;
    private Boolean captureMatmFlightAllCsv;

    public String getMatmPositionJmsUrl()
    {
        return matmPositionJmsUrl;
    }

    public void setMatmPositionJmsUrl(String matmPositionJmsUrl)
    {
        this.matmPositionJmsUrl = matmPositionJmsUrl;
    }

    public String getMatmPositionUpdateUrl()
    {
        return matmPositionUpdateUrl;
    }

    public void setMatmPositionUpdateUrl(String matmPositionUpdateUrl)
    {
        this.matmPositionUpdateUrl = matmPositionUpdateUrl;
    }

    public String getMatmPositionUpdateEnvelopeUrl()
    {
        return matmPositionUpdateEnvelopeUrl;
    }

    public void setMatmPositionUpdateEnvelopeUrl(String matmPositionUpdateEnvelopeUrl)
    {
        this.matmPositionUpdateEnvelopeUrl = matmPositionUpdateEnvelopeUrl;
    }

    public Boolean getClockSyncEnabled()
    {
        return clockSyncEnabled;
    }

    public void setClockSyncEnabled(Boolean clockSyncEnabled)
    {
        this.clockSyncEnabled = clockSyncEnabled;
    }

    public Boolean getPositionEnable()
    {
        return positionEnable;
    }

    public void setPositionEnable(Boolean positionEnable)
    {
        this.positionEnable = positionEnable;
    }

    public Boolean getMatmPositionEnable()
    {
        return matmPositionEnable;
    }

    public void setMatmPositionEnable(Boolean matmPositionEnable)
    {
        this.matmPositionEnable = matmPositionEnable;
    }

    public String getDbUrl()
    {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl)
    {
        this.dbUrl = dbUrl;
    }

    public String getDbUsername()
    {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername)
    {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword()
    {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword)
    {
        this.dbPassword = dbPassword;
    }

    public Boolean getDropTables()
    {
        return dropTables;
    }

    public void setDropTables(Boolean dropTables)
    {
        this.dropTables = dropTables;
    }

    public Boolean getCaptureMatmFlightXml()
    {
        return captureMatmFlightXml;
    }

    public void setCaptureMatmFlightXml(Boolean captureMatmFlightXml)
    {
        this.captureMatmFlightXml = captureMatmFlightXml;
    }

    public Boolean getCaptureMatmFlightCsv()
    {
        return captureMatmFlightCsv;
    }

    public void setCaptureMatmFlightCsv(Boolean captureMatmFlightCsv)
    {
        this.captureMatmFlightCsv = captureMatmFlightCsv;
    }

    public Boolean getCaptureMatmFlightAllXml()
    {
        return captureMatmFlightAllXml;
    }

    public void setCaptureMatmFlightAllXml(Boolean captureMatmFlightAllXml)
    {
        this.captureMatmFlightAllXml = captureMatmFlightAllXml;
    }

    public Boolean getCaptureMatmFlightAllCsv()
    {
        return captureMatmFlightAllCsv;
    }

    public void setCaptureMatmFlightAllCsv(Boolean captureMatmFlightAllCsv)
    {
        this.captureMatmFlightAllCsv = captureMatmFlightAllCsv;
    }

    public Boolean getSurfaceAirportFilterActive()
    {
        return surfaceAirportFilterActive;
    }

    public void setSurfaceAirportFilterActive(Boolean surfaceAirportFilterActive)
    {
        this.surfaceAirportFilterActive = surfaceAirportFilterActive;
    }

    public String getMatmPositionUpdateAirportsOfInterest()
    {
        return matmPositionUpdateAirportsOfInterest;
    }

    public void setMatmPositionUpdateAirportsOfInterest(String matmPositionUpdateAirportsOfInterest)
    {
        this.matmPositionUpdateAirportsOfInterest = matmPositionUpdateAirportsOfInterest;
    }

    public Boolean getMatmFlightAirportFilterActive()
    {
        return matmFlightAirportFilterActive;
    }

    public void setMatmFlightAirportFilterActive(Boolean matmFlightAirportFilterActive)
    {
        this.matmFlightAirportFilterActive = matmFlightAirportFilterActive;
    }

    public String getMatmFlightAirportsOfInterest()
    {
        return matmFlightAirportsOfInterest;
    }

    public void setMatmFlightAirportsOfInterest(String matmFlightAirportsOfInterest)
    {
        this.matmFlightAirportsOfInterest = matmFlightAirportsOfInterest;
    }

    public Boolean getMatmPositionUpdateAirportFilterActive()
    {
        return matmPositionUpdateAirportFilterActive;
    }

    public void setMatmPositionUpdateAirportFilterActive(Boolean matmPositionUpdateAirportFilterActive)
    {
        this.matmPositionUpdateAirportFilterActive = matmPositionUpdateAirportFilterActive;
    }

    public Boolean getSyncMatmFlight()
    {
        return syncMatmFlight;
    }

    public void setSyncMatmFlight(Boolean syncMatmFlight)
    {
        this.syncMatmFlight = syncMatmFlight;
    }

    public Boolean getSyncMatmAircraft()
    {
        return syncMatmAircraft;
    }

    public void setSyncMatmAircraft(Boolean syncMatmAircraft)
    {
        this.syncMatmAircraft = syncMatmAircraft;
    }

}
