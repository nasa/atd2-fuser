package com.mosaicatm.fuser.datacapture.io.rolling;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.datacapture.io.CaptureData;
import com.mosaicatm.fuser.datacapture.io.FuserCapture;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.rollingfile.capture.RollingCapture;

public class FuserRollingXmlCapture<T>
implements FuserCapture<T>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private boolean active;
    private GenericMarshaller marshaller;
    private RollingCapture capture;
    private Clock clock;
    
    @Override
    public void capture(CaptureData<T> captureData)
    {        
        if (!active && captureData != null && captureData.getData() != null)
            return;
        
        String xml = marshall(captureData.getData());
        
        if (xml != null && !xml.trim().isEmpty())
        {
            if (capture != null)
            {
                if (clock != null)
                {
                    capture.capture(new Date(clock.getTimeInMillis()), xml);
                }
                else
                {
                    capture.capture(xml);
                }
            }
        }
    }
    
    private String marshall (T data)
    {
        String xml = null;
        
        if (data != null)
        {
            try
            {
                xml = marshaller.marshall(data);
            }
            catch(Exception e)
            {
                log.error("Error marshalling " + data, e);
            }
        }
        
        return xml;
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

    public void setMarshaller (GenericMarshaller marshaller)
    {
        this.marshaller = marshaller;
    }
    
    public void setCapture (RollingCapture capture)
    {
        this.capture = capture;
    }
    
    public void setClock (Clock clock)
    {
        this.clock = clock;
    }
}
