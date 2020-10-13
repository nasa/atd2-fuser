package com.mosaicatm.fuser.datacapture.handle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.datacapture.DataWrapper;
import com.mosaicatm.fuser.datacapture.io.CaptureData;
import com.mosaicatm.fuser.datacapture.io.FuserCapture;
import com.mosaicatm.lib.util.concurrent.Handler;

public class XmlCaptureHandler<T>
implements Handler<DataWrapper<T>>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private FuserCapture<T> xmlCapture;
    
    @Override
    public void handle(DataWrapper<T> data)
    {
        if (data != null && data.getData() != null)
        {
            if (xmlCapture != null)
            {
                xmlCapture.capture(new CaptureData<T>(data.getData()));
            }
        }
    }

    @Override
    public void onShutdown()
    {
        // do nothing
    }

    public void setXmlCapture (FuserCapture<T> xmlCapture)
    {
        this.xmlCapture = xmlCapture;
    }
}
