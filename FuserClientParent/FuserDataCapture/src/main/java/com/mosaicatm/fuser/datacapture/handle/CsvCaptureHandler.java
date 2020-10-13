package com.mosaicatm.fuser.datacapture.handle;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.datacapture.DataWrapper;
import com.mosaicatm.fuser.datacapture.io.CaptureData;
import com.mosaicatm.fuser.datacapture.io.FuserCapture;
import com.mosaicatm.lib.util.concurrent.Handler;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;

public class CsvCaptureHandler
implements Handler<DataWrapper<MatmFlight>>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private FuserCapture<Object> matmFlightCapture;
    private FuserCapture<Object> adsbExtCapture;
    private FuserCapture<Object> asdexExtCapture;
    private FuserCapture<Object> cat11ExtCapture;
    private FuserCapture<Object> cat62ExtCapture;
    private FuserCapture<Object> derivedExtCapture;
    private FuserCapture<Object> tbfmExtCapture;
    private FuserCapture<Object> tfmExtCapture;
    private FuserCapture<Object> tfmTfdmExtCapture;
    private FuserCapture<Object> tfmTraversalExtCapture;
    private FuserCapture<Object> matmAirlineMessageExtCapture;
    private FuserCapture<Object> idacExtCapture;
    private FuserCapture<Object> surfaceModelExtCapture;
    private FuserCapture<Object> aefsExtCapture;
    private FuserCapture<Object> sfdpsExtCapture;
    private FuserCapture<Object> smesExtCapture;
    private FuserCapture<Object> tmiExtCapture;

    @Override
    public void handle(DataWrapper<MatmFlight> wrapper)
    {        
        if (wrapper != null && wrapper.getData() != null)
        {
            MatmFlight flight = wrapper.getData();
            String gufi = flight.getGufi();
            
            CaptureData<Object> data = new CaptureData<>();
            data.setTimestamp(flight.getTimestamp());
            data.setRecordId(UUID.randomUUID().toString());
            data.setGufi(gufi);
            
            if(matmFlightCapture != null)
            {
                data.setData(flight);
                matmFlightCapture.capture(data);
            }
            
            MatmFlightExtensions ext = flight.getExtensions();
            
            if (ext != null)
            {
                if(adsbExtCapture != null)
                {
                    data.setData(ext.getAdsbExtension());
                    adsbExtCapture.capture(data);
                }
                
                if (asdexExtCapture != null)
                {
                    data.setData(ext.getAsdexExtension());
                    asdexExtCapture.capture(data);
                }
                
                if(cat11ExtCapture != null)
                {
                    data.setData(ext.getCat11Extension());
                    cat11ExtCapture.capture(data);
                }
                
                if(cat62ExtCapture != null)
                {
                    data.setData(ext.getCat62Extension());
                    cat62ExtCapture.capture(data);
                }
                
                if (derivedExtCapture != null)
                {
                    data.setData(ext.getDerivedExtension());
                    derivedExtCapture.capture(data);
                }
                
                if (tbfmExtCapture != null)
                {
                    data.setData(ext.getTbfmExtension());
                    tbfmExtCapture.capture(data);
                }
                
                if (tfmExtCapture != null)
                {
                    data.setData(ext.getTfmExtension());
                    tfmExtCapture.capture(data);
                }

                if (tfmTfdmExtCapture != null)
                {
                    data.setData(ext.getTfmTfdmExtension());
                    tfmTfdmExtCapture.capture(data);
                }

                if (tfmTraversalExtCapture != null)
                {
                    data.setData(ext.getTfmsFlightTraversalExtension());
                    tfmTraversalExtCapture.capture(data);
                }
                
                if (matmAirlineMessageExtCapture != null)
                {
                    data.setData(ext.getMatmAirlineMessageExtension());
                    matmAirlineMessageExtCapture.capture(data);
                }
                
                if (idacExtCapture != null)
                {
                    data.setData(ext.getIdacExtension());
                    idacExtCapture.capture(data);
                }
                
                if (surfaceModelExtCapture != null)
                {
                    data.setData(ext.getSurfaceModelExtension());
                    surfaceModelExtCapture.capture(data);
                }
                
                if (aefsExtCapture != null)
                {
                    data.setData(ext.getAefsExtension());
                    aefsExtCapture.capture(data);
                }

                if (sfdpsExtCapture != null)
                {
                    data.setData(ext.getSfdpsExtension());
                    sfdpsExtCapture.capture(data);
                }

                if (smesExtCapture != null)
                {
                    data.setData(ext.getSmesExtension());
                    smesExtCapture.capture(data);
                }

                if (tmiExtCapture != null)
                {
                    data.setData(ext.getTmiExtension());
                    tmiExtCapture.capture(data);
                }
            }
        }
    }

    @Override
    public void onShutdown()
    {
        log.info("Stopping csv capture handler");
    }
    
    public void setMatmFlightCapture(FuserCapture<Object> matmFlightCapture)
    {
        this.matmFlightCapture = matmFlightCapture;
    }

    public void setAdsbExtensionCapture(FuserCapture<Object> adsbExtCapture)
    {
        this.adsbExtCapture = adsbExtCapture;
    }

    public void setAsdexExtensionCapture(FuserCapture<Object> asdexExtCapture)
    {
        this.asdexExtCapture = asdexExtCapture;
    }

    public void setCat11ExtensionCapture(FuserCapture<Object> cat11ExtCapture)
    {
        this.cat11ExtCapture = cat11ExtCapture;
    }

    public void setCat62ExtensionCapture(FuserCapture<Object> cat62ExtCapture)
    {
        this.cat62ExtCapture = cat62ExtCapture;
    }

    public void setDerivedExtensionCapture(FuserCapture<Object> derivedExtCapture)
    {
        this.derivedExtCapture = derivedExtCapture;
    }

    public void setTbfmExtensionCapture(FuserCapture<Object> tbfmExtCapture)
    {
        this.tbfmExtCapture = tbfmExtCapture;
    }

    public void setTfmExtensionCapture(FuserCapture<Object> tfmExtCapture)
    {
        this.tfmExtCapture = tfmExtCapture;
    }
    
    public void setTfmTfdmExtensionCapture(FuserCapture<Object> tfmTfdmExtCapture)
    {
        this.tfmTfdmExtCapture = tfmTfdmExtCapture;
    }    

    public void setTfmTraversalExtensionCapture(FuserCapture<Object> tfmTraversalExtCapture)
    {
        this.tfmTraversalExtCapture = tfmTraversalExtCapture;
    }    
    
    public void setMatmAirlineMessageExtensionCapture(FuserCapture<Object> matmAirlineMessageExtCapture)
    {
        this.matmAirlineMessageExtCapture = matmAirlineMessageExtCapture;
    }

    public FuserCapture<Object> getIdacExtensionCapture()
    {
        return idacExtCapture;
    }

    public void setIdacExtensionCapture(FuserCapture<Object> idacExtCapture)
    {
        this.idacExtCapture = idacExtCapture;
    }
    
    public FuserCapture<Object> getSurfaceModelExtensionCapture()
    {
        return surfaceModelExtCapture;
    }

    public void setSurfaceModelExtensionCapture(FuserCapture<Object> surfaceModelExtCapture)
    {
        this.surfaceModelExtCapture = surfaceModelExtCapture;
    }

    public FuserCapture<Object> getAefsExtensionCapture()
    {
        return aefsExtCapture;
    }

    public void setAefsExtensionCapture(FuserCapture<Object> aefsExtCapture)
    {
        this.aefsExtCapture = aefsExtCapture;
    }

    public FuserCapture<Object> getSfdpsExtensionCapture()
    {
        return sfdpsExtCapture;
    }

    public void setSfdpsExtensionCapture(FuserCapture<Object> sfdpsExtCapture)
    {
        this.sfdpsExtCapture = sfdpsExtCapture;
    }

    public FuserCapture<Object> getSmesExtensionCapture()
    {
        return smesExtCapture;
    }

    public void setSmesExtensionCapture(FuserCapture<Object> smesExtCapture)
    {
        this.smesExtCapture = smesExtCapture;
    }

    public FuserCapture<Object> getTmiExtensionCapture()
    {
        return tmiExtCapture;
    }

    public void setTmiExtensionCapture(FuserCapture<Object> tmiExtCapture)
    {
        this.tmiExtCapture = tmiExtCapture;
    }
}
