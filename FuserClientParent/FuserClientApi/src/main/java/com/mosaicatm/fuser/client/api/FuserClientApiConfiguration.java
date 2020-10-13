package com.mosaicatm.fuser.client.api;

public class FuserClientApiConfiguration {

    private String jmsUrl;
    private String jmsUsername;
    private String jmsPassword;
    private String matmFlightUri;
    private String matmTransferEnvelopeUri;
    private String matmAircraftUri;
    private String matmAircraftRemoveUri;
    private String matmAircraftBatchUri;
    private String matmSectorAssignmentBatchUri;
    private Boolean syncEnabled;
    private Boolean aircraftSyncEnabled;
    private Boolean sectorAssignmentSyncEnabled;
    private String syncServiceUrl;
    private String syncServiceOverrideDataSource;
    private Boolean clockSyncEnabled;
    private String clockSyncUri;
    private Integer removeAfterHours=36;
    private Boolean timedRemoverActive=true;
    private Boolean processRawTrajectories=false;

    private Boolean airportFilterActive=false;
    private Boolean surfaceAirportFilterActive=false;
    private String airportsOfInterest=null;

    private Boolean disableCNCheck = true;
    private String keystoreLocation;
    private String keystoreType;
    private String keystorePassword;
    private String trustStoreLocation;
    private String trustStoreType;
    private String trustStorePassword;
    
    private Integer receiverQueueSize;
    
    public Boolean getProcessRawTrajectoriesEnabled () {
        return processRawTrajectories;
    }
    public void setProcessRawTrajectoriesEnabled (Boolean processRawTrajectories) {
        this.processRawTrajectories = processRawTrajectories;
    }

    public String getJmsUrl() {
        return jmsUrl;
    }
    public void setJmsUrl(String jmsUrl) {
        this.jmsUrl = jmsUrl;
    }
    public String getJmsUsername()
    {
        return jmsUsername;
    }
    public void setJmsUsername( String jmsUsername )
    {
        this.jmsUsername = jmsUsername;
    }
    public String getJmsPassword()
    {
        return jmsPassword;
    }
    public void setJmsPassword( String jmsPassword )
    {
        this.jmsPassword = jmsPassword;
    }


    public String getMatmFlightUri() {
        return matmFlightUri;
    }
    public void setMatmFlightUri(String matmFlightUri) {
        this.matmFlightUri = matmFlightUri;
    }
    public String getMatmTransferEnvelopeUri() {
        return matmTransferEnvelopeUri;
    }
    public void setMatmTransferEnvelopeUri(String matmTransferEnvelopeUri) {
        this.matmTransferEnvelopeUri = matmTransferEnvelopeUri;
    }
    public Boolean getSyncEnabled() {
        return syncEnabled;
    }
    public void setSyncEnabled(Boolean syncEnabled) {
        this.syncEnabled = syncEnabled;
    }
    public Boolean getAircraftSyncEnabled() {
        return aircraftSyncEnabled;
    }
    public void setAircraftSyncEnabled(Boolean aircraftSyncEnabled) {
        this.aircraftSyncEnabled = aircraftSyncEnabled;
    }
    public Boolean getSectorAssignmentSyncEnabled() {
        return sectorAssignmentSyncEnabled;
    }
    public void setSectorAssignmentSyncEnabled(Boolean sectorAssignmentSyncEnabled) {
        this.sectorAssignmentSyncEnabled = sectorAssignmentSyncEnabled;
    }        
    public Boolean getClockSyncEnabled() {
        return clockSyncEnabled;
    }
    public void setClockSyncEnabled(Boolean clockSyncEnabled) {
        this.clockSyncEnabled = clockSyncEnabled;
    }     
    public String getClockSyncUri()
    {
        return clockSyncUri;
    }
    public void setClockSyncUri( String clockSyncUri )
    {
        this.clockSyncUri = clockSyncUri;
    }
    public String getSyncServiceOverrideDataSource() {
        return syncServiceOverrideDataSource;
    }
    public void setSyncServiceOverrideDataSource( String syncServiceOverrideDataSource ) {
        this.syncServiceOverrideDataSource = syncServiceOverrideDataSource;
    }
    public String getSyncServiceUrl() {
        return syncServiceUrl;
    }
    public void setSyncServiceUrl(String syncServiceUrl) {
        this.syncServiceUrl = syncServiceUrl;
    }
    public Integer getRemoveAfterHours() {
        return removeAfterHours;
    }
    public void setRemoveAfterHours(Integer removeAfterHours) {
        this.removeAfterHours = removeAfterHours;
    }
    public Boolean getTimedRemoverActive() {
        return timedRemoverActive;
    }
    public void setTimedRemoverActive(Boolean timedRemoverActive) {
        this.timedRemoverActive = timedRemoverActive;
    }
    public Boolean getAirportFilterActive() {
        return airportFilterActive;
    }
    public void setAirportFilterActive(Boolean airportFilterActive) {
        this.airportFilterActive = airportFilterActive;
    }
    public Boolean getSurfaceAirportFilterActive() {
        return surfaceAirportFilterActive;
    }
    public void setSurfaceAirportFilterActive(Boolean surfaceAirportFilterActive) {
        this.surfaceAirportFilterActive = surfaceAirportFilterActive;
    }
    public String getAirportsOfInterest() {
        return airportsOfInterest;
    }
    /**
     * Comma delimited list of airports
     * @param airportOfInterest
     */
    public void setAirportsOfInterest(String airportsOfInterest) {
        this.airportsOfInterest = airportsOfInterest;
    }
    public String getMatmAircraftUri()
    {
        return matmAircraftUri;
    }
    public void setMatmAircraftUri(String matmAircraftUri)
    {
        this.matmAircraftUri = matmAircraftUri;
    }
    public String getMatmAircraftRemoveUri()
    {
        return matmAircraftRemoveUri;
    }
    public void setMatmAircraftRemoveUri(String matmAircraftRemoveUri)
    {
        this.matmAircraftRemoveUri = matmAircraftRemoveUri;
    }
    public String getMatmAircraftBatchUri()
    {
        return matmAircraftBatchUri;
    }
    public void setMatmAircraftBatchUri(String matmAircraftBatchUri)
    {
        this.matmAircraftBatchUri = matmAircraftBatchUri;
    }
    public String getMatmSectorAssignmentBatchUri()
    {
        return matmSectorAssignmentBatchUri;
    }    
    public void setMatmSectorAssignmentBatchUri(String matmSectorAssignmentBatchUri)
    {
        this.matmSectorAssignmentBatchUri = matmSectorAssignmentBatchUri;
    }    
    
    public Boolean getProcessRawTrajectories()
    {
        return processRawTrajectories;
    }
    public void setProcessRawTrajectories( Boolean processRawTrajectories )
    {
        this.processRawTrajectories = processRawTrajectories;
    }
    public Boolean getDisableCNCheck()
    {
        return disableCNCheck;
    }
    public void setDisableCNCheck( Boolean disableCNCheck )
    {
        this.disableCNCheck = disableCNCheck;
    }
    public String getKeystoreLocation()
    {
        return keystoreLocation;
    }
    public void setKeystoreLocation( String keystoreLocation )
    {
        this.keystoreLocation = keystoreLocation;
    }
    public String getKeystoreType()
    {
        return keystoreType;
    }
    public void setKeystoreType( String keystoreType )
    {
        this.keystoreType = keystoreType;
    }
    public String getKeystorePassword()
    {
        return keystorePassword;
    }
    public void setKeystorePassword( String keystorePassword )
    {
        this.keystorePassword = keystorePassword;
    }
    public String getTrustStoreLocation()
    {
        return trustStoreLocation;
    }
    public void setTrustStoreLocation( String trustStoreLocation )
    {
        this.trustStoreLocation = trustStoreLocation;
    }
    public String getTrustStoreType()
    {
        return trustStoreType;
    }
    public void setTrustStoreType( String trustStoreType )
    {
        this.trustStoreType = trustStoreType;
    }
    public String getTrustStorePassword()
    {
        return trustStorePassword;
    }
    public void setTrustStorePassword( String trustStorePassword )
    {
        this.trustStorePassword = trustStorePassword;
    }
    public Integer getReceiverQueueSize()
    {
        return receiverQueueSize;
    }
    public void setReceiverQueueSize( Integer receiverQueueSize )
    {
        this.receiverQueueSize = receiverQueueSize;
    }
}
