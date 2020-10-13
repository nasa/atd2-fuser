package com.mosaicatm.fuser.datacapture.io.rolling;

import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.container.io.DelimitedObjectWriter;
import com.mosaicatm.fuser.datacapture.CaptureType;
import com.mosaicatm.fuser.datacapture.io.DelimitedObjectFactory;

public class FileHeaderFactory
{
    private final Log log = LogFactory.getLog(getClass());
    
    private DelimitedObjectFactory delimitedFactory;
    private CaptureType captureType;
    
    public FileHeaderFactory(DelimitedObjectFactory delimitedFactory)
    {
        this.delimitedFactory = delimitedFactory;
    }
    
    public FileHeaderFactory(DelimitedObjectFactory delimitedFactory, 
                             CaptureType captureType)
    {
        this(delimitedFactory);
        
        this.captureType = captureType;
    }
    
    public String getFileHeader ()
    {
        return getFileHeader(captureType);
    }
    
    public String getFileHeader (CaptureType captureType)
    {
        String header = null;
        
        if (captureType != null)
        {            
            StringWriter writer = new StringWriter();
            
            DelimitedObjectWriter<?> delim = getDelimitedWriter(captureType, writer);
            delim.writeHeaders();
            delim.flush();
            
            header = writer.toString();
        }
        
        return header;
    }
    
    protected DelimitedObjectWriter<?> getDelimitedWriter (CaptureType captureType, Writer writer)
    {
        if (delimitedFactory != null)
            return delimitedFactory.getDelimitedObjectWriter(captureType, writer);
        return null;
    }
}
