package com.mosaicatm.fuser.datacapture.io.rolling;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.container.io.DelimitedObjectWriter;
import com.mosaicatm.fuser.datacapture.CaptureType;
import com.mosaicatm.fuser.datacapture.flat.FlatFactory;
import com.mosaicatm.fuser.datacapture.io.CaptureData;
import com.mosaicatm.fuser.datacapture.io.DelimitedObjectFactory;
import com.mosaicatm.fuser.datacapture.io.FuserCapture;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.rollingfile.capture.RollingCapture;

public class FuserRollingCsvCapture<T, F>
implements FuserCapture<T>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private FlatFactory flatFactory;
    private DelimitedObjectFactory delimitedFactory;
    private CaptureType captureType;
    
    private boolean active;
    private RollingCapture capture;
    private Clock clock;
    
    public FuserRollingCsvCapture (CaptureType captureType)
    {
        this.captureType = captureType;
    }
    
    @Override
    public void capture(CaptureData<T> captureData)
    {        
        if (captureData != null && captureData.getData() != null)
        {
            captureData.setType(captureType);
            
            F flat = flatFactory.flatten(captureData);
            
            if (flat != null)
            {                
                String csv = convertToCsv(flat);
                
                if (csv != null && !csv.trim().isEmpty())
                {
                    RollingCapture capture = getCapture();

                    if (capture != null)
                    {
                        if (clock != null)
                        {
                            capture.capture(new Date(clock.getTimeInMillis()), csv);
                        }
                        else
                        {
                            capture.capture(csv);
                        }
                    }
                    else
                        log.error("Rolling capture not initialized for " + captureType);
                }
                else
                {
                    log.warn("Unable to produce CSV format for " + captureData.getData());
                }
            }
            else
            {
                log.warn("Unable to flatten data for " + captureData.getData());
            }
        }
    }
    
    public void start ()
    {
        if (!isActive())
            return;
        
        if (capture != null)
        {
            log.info("Starting Rolling CSV capture");
            
            capture.open();
        }
        else
        {
            log.warn("Rolling CSV capture is not defined, failed to open");
        }
    }
    
    public void stop ()
    {
        if (capture != null)
        {
            log.info("Stopping Rolling CSV capture");
            capture.close();
        }
        else
        {
            log.warn("Rolling CSV capture is not defined, failed to close");
        }
    }
    
    protected DelimitedObjectWriter<F> getDelimitedWriter (Writer writer)
    {
        if (delimitedFactory != null)
            return delimitedFactory.getDelimitedObjectWriter(captureType, writer);
        return null;
    }
    
    protected String convertToCsv (F data)
    {
        StringWriter writer = new StringWriter();
        
        DelimitedObjectWriter<F> delim = getDelimitedWriter(writer);
        delim.write(data);
        delim.flush();
        
        return writer.toString();
    }
    
    public boolean isActive ()
    {
        return active;
    }
    
    public void setActive (boolean active)
    {
        this.active = active;
    }
    
    public RollingCapture getCapture ()
    {
        return capture;
    }
    
    public void setCapture (RollingCapture capture)
    {
        this.capture = capture;
    }

    public  DelimitedObjectFactory getDelimitedObjectFactory ()
    {
        return delimitedFactory;
    }
    
    public void setDelimitedObjectFactory (DelimitedObjectFactory delimitedFactory)
    {
        this.delimitedFactory = delimitedFactory;
    }
    
    public void setFlatFactory (FlatFactory flatFactory)
    {
        this.flatFactory = flatFactory;
    }
    
    public void setClock (Clock clock)
    {
        this.clock = clock;
    }
}
