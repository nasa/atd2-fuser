package com.mosaicatm.fuser.datacapture.handle;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.datacapture.DataWrapper;
import com.mosaicatm.lib.util.concurrent.Handler;
import com.mosaicatm.matmdata.common.MatmObject;

public class CaptureHandler<T extends MatmObject>
implements Handler<DataWrapper<T>>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private final List<Handler<DataWrapper<T>>> handlers;
    private boolean handlerWarningIssued = false;
    private String handlerName = "";
    private String ignoreLastUpdateSource = null;

    public CaptureHandler (String handlerName)
    {
        this.handlers = new ArrayList<>();
        this.handlerName = handlerName;
    }
    
    @Override
    public void handle(DataWrapper<T> wrapper)
    {
        if(( wrapper != null ) && ( wrapper.getData() != null ) && checkHandlers() )
        {
            T data = wrapper.getData();
            if(( ignoreLastUpdateSource == null ) || ( data.getLastUpdateSource() == null ) || 
                    ( !ignoreLastUpdateSource.equals( data.getLastUpdateSource() )))
            {
                for (Handler<DataWrapper<T>> handler : handlers )
                    handler.handle(wrapper);
            }
        }
    }

    @Override
    public void onShutdown()
    {
        // do nothing
    }
    
    public void addHandler (Handler<DataWrapper<T>> handler)
    {
        if (handler != null)
        {
            synchronized(handlers)
            {
                handlers.add(handler);
            }
            handlerWarningIssued = false;
        }
    }
    
    public void removeHandler (Handler<DataWrapper<T>> handler)
    {
        if (handler != null)
        {
            synchronized(handlers)
            {
                handlers.remove(handler);
            }
            handlerWarningIssued = false;
        }
    }
    
    public void setHandlers (List<Handler<DataWrapper<T>>> handlers)
    {
        if (handlers != null)
        {
            synchronized(handlers)
            {
                handlers.clear();
                handlers.addAll(handlers);
            }
            handlerWarningIssued = false;
        }
    }
    
    public void setIgnoreLastUpdateSource( String ignoreLastUpdateSource )
    {
        if(( ignoreLastUpdateSource != null ) && ( ignoreLastUpdateSource.trim().isEmpty() ))
            ignoreLastUpdateSource = null;
        
        this.ignoreLastUpdateSource = ignoreLastUpdateSource;
    }    
    
    private boolean checkHandlers()
    {
        if(( handlers == null ) || ( handlers.isEmpty() ))
        {
            if( !handlerWarningIssued )
            {
                log.warn("No available handlers for: " + handlerName);
                handlerWarningIssued = true;
            }
            return( false ); 
        }        
        
        return( true ); 
    }
}
