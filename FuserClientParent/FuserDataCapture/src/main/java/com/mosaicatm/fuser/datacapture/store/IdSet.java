package com.mosaicatm.fuser.datacapture.store;

public class IdSet
{
    private final String gufi;
    private final String recordId;
    private String adsbExtensionId;
    private String aefsExtensionId;
    private String asdexExtensionId;
    private String cat11ExtensionId;
    private String cat62ExtensionId;
    private String derivedExtensionId;
    private String idacExtensionId;
    private String airlineExtensionId;
    private String sfdpsExtensionId;
    private String surfaceModelExtensionId;
    private String tbfmExtensionId;
    private String tfmExtensionId;
    private String tfmTfdmExtensionId;
    private String tfmTraversalExtensionId;
    private String smesExtensionId;
    private String tmiExtensionId;
    private String positionId;
    private String matmId;
    private String aircraftId;
    
    public IdSet(String gufi, String recordId)
    {
        this.gufi = gufi;
        this.recordId = recordId;
    }
    
    public IdSet(String gufi, String recordId, IdSet copy)
    {
        this(gufi, recordId);
        matmId = copy.matmId;
        positionId = copy.positionId;
        adsbExtensionId = copy.adsbExtensionId;
        aefsExtensionId = copy.aefsExtensionId;
        airlineExtensionId = copy.airlineExtensionId;
        asdexExtensionId = copy.asdexExtensionId;
        cat11ExtensionId = copy.cat11ExtensionId;
        cat62ExtensionId = copy.cat62ExtensionId;
        derivedExtensionId = copy.derivedExtensionId;
        idacExtensionId = copy.idacExtensionId;
        sfdpsExtensionId = copy.sfdpsExtensionId;
        surfaceModelExtensionId = copy.surfaceModelExtensionId;
        tbfmExtensionId = copy.tbfmExtensionId;
        tfmExtensionId = copy.tfmExtensionId;
        tfmTfdmExtensionId = copy.tfmTfdmExtensionId;
        tfmTraversalExtensionId = copy.tfmTraversalExtensionId;
        smesExtensionId = copy.smesExtensionId;
        tmiExtensionId = copy.tmiExtensionId;
        aircraftId = copy.aircraftId;
    }
    
    public String getAdsbExtensionId()
    {
        return adsbExtensionId;
    }
    
    public void setAdsbExtensionId(String adsbExtensionId)
    {
        this.adsbExtensionId = adsbExtensionId;
    }
    
    public String getAefsExtensionId()
    {
        return aefsExtensionId;
    }
    
    public void setAefsExtensionId(String aefsExtensionId)
    {
        this.aefsExtensionId = aefsExtensionId;
    }
    
    public String getAsdexExtensionId()
    {
        return asdexExtensionId;
    }
    
    public void setAsdexExtensionId(String asdexExtensionId)
    {
        this.asdexExtensionId = asdexExtensionId;
    }
    
    public String getCat11ExtensionId()
    {
        return cat11ExtensionId;
    }
    
    public void setCat11ExtensionId(String cat11ExtensionId)
    {
        this.cat11ExtensionId = cat11ExtensionId;
    }
    
    public String getCat62ExtensionId()
    {
        return cat62ExtensionId;
    }
    
    public void setCat62ExtensionId(String cat62ExtensionId)
    {
        this.cat62ExtensionId = cat62ExtensionId;
    }
    
    public String getDerivedExtensionId()
    {
        return derivedExtensionId;
    }
    
    public void setDerivedExtensionId(String derivedExtensionId)
    {
        this.derivedExtensionId = derivedExtensionId;
    }
    
    public String getIdacExtensionId()
    {
        return idacExtensionId;
    }
    
    public void setIdacExtensionId(String idacExtensionId)
    {
        this.idacExtensionId = idacExtensionId;
    }
    
    public String getAirlineExtensionId()
    {
        return airlineExtensionId;
    }
    
    public void setAirlineExtensionId(String airlineExtensionId)
    {
        this.airlineExtensionId = airlineExtensionId;
    }
    
    public String getSfdpsExtensionId()
    {
        return sfdpsExtensionId;
    }

    public void setSfdpsExtensionId(String sfdpsExtensionId)
    {
        this.sfdpsExtensionId = sfdpsExtensionId;
    }

    public String getSurfaceModelExtensionId()
    {
        return surfaceModelExtensionId;
    }
    
    public void setSurfaceModelExtensionId(String surfaceModelExtensionId)
    {
        this.surfaceModelExtensionId = surfaceModelExtensionId;
    }
    
    public String getTbfmExtensionId()
    {
        return tbfmExtensionId;
    }
    
    public void setTbfmExtensionId(String tbfmExtensionId)
    {
        this.tbfmExtensionId = tbfmExtensionId;
    }
    
    public String getTfmExtensionId()
    {
        return tfmExtensionId;
    }
    
    public void setTfmExtensionId(String tfmExtensionId)
    {
        this.tfmExtensionId = tfmExtensionId;
    }
    
    public String getTfmTfdmExtensionId()
    {
        return tfmTfdmExtensionId;
    }
    
    public void setTfmTfdmExtensionId(String tfmTfdmExtensionId)
    {
        this.tfmTfdmExtensionId = tfmTfdmExtensionId;
    }
    
    public String getTfmTraversalExtensionId()
    {
        return tfmTraversalExtensionId;
    }
    
    public void setTfmTraversalExtensionId(String tfmTraversalExtensionId)
    {
        this.tfmTraversalExtensionId = tfmTraversalExtensionId;
    }

    public String getSmesExtensionId() {
        return smesExtensionId;
    }

    public void setSmesExtensionId(String smesExtensionId) {
        this.smesExtensionId = smesExtensionId;
    }

    public String getTmiExtensionId() {
        return tmiExtensionId;
    }

    public void setTmiExtensionId(String tmiExtensionId) {
        this.tmiExtensionId = tmiExtensionId;
    }

    public String getMatmId()
    {
        return matmId;
    }
    
    public void setMatmId(String matmId)
    {
        this.matmId = matmId;
    }
    
    public String getPositionId()
    {
        return positionId;
    }

    public void setPositionId(String positionId)
    {
        this.positionId = positionId;
    }

    public String getRecordId()
    {
        return recordId;
    }

    public String getGufi()
    {
        return gufi;
    }
    
    public void setAircraftId(String aircraftId)
    {
        this.aircraftId = aircraftId;
    }
    
    public String getAircraftId()
    {
        return aircraftId;
    }

	@Override
	public String toString() {
		return "IdSet [gufi=" + gufi + ", recordId=" + recordId + ", adsbExtensionId=" + adsbExtensionId
				+ ", aefsExtensionId=" + aefsExtensionId + ", asdexExtensionId=" + asdexExtensionId
				+ ", cat11ExtensionId=" + cat11ExtensionId + ", cat62ExtensionId=" + cat62ExtensionId
				+ ", derivedExtensionId=" + derivedExtensionId + ", idacExtensionId=" + idacExtensionId
				+ ", airlineExtensionId=" + airlineExtensionId
				+ ", sfdpsExtensionId=" + sfdpsExtensionId + ", surfaceModelExtensionId=" + surfaceModelExtensionId
				+ ", tbfmExtensionId=" + tbfmExtensionId + ", tfmExtensionId=" + tfmExtensionId
				+ ", tfmTfdmExtensionId=" + tfmTfdmExtensionId + ", positionId=" + positionId
				+ ", matmId=" + matmId + ", aircraftId=" + aircraftId + "]";
	}
}
