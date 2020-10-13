package com.mosaicatm.fuser.datacapture.handle;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.util.concurrent.Handler;
import com.mosaicatm.matmdata.position.MatmPositionUpdate;

public class CapturePositionHandler
implements Handler<MatmPositionUpdate>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private final List<Handler<MatmPositionUpdate>> handlers;
    private boolean handlerWarningIssued = false;
    private String handlerName = "";
    private String ignoreLastUpdateSource = null;

    public CapturePositionHandler (String handlerName)
    {
        this.handlers = new ArrayList<>();
        this.handlerName = handlerName;
    }
    
    @Override
    public void handle(MatmPositionUpdate data)
    {
        if(( data != null ) && checkHandlers() )
        {
            if(( ignoreLastUpdateSource == null ) || ( data.getLastUpdateSource() == null ) || 
                    ( !ignoreLastUpdateSource.equals( data.getLastUpdateSource() )))
            {
                for (Handler<MatmPositionUpdate> handler : handlers )
                    handler.handle(data);
            }
        }
    }

    @Override
    public void onShutdown()
    {
        // do nothing
    }
    
    public void addHandler (Handler<MatmPositionUpdate> handler)
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
    
    public void removeHandler (Handler<MatmPositionUpdate> handler)
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
    
    public void setHandlers (List<Handler<MatmPositionUpdate>> handlers)
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
