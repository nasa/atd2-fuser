package com.mosaicatm.fuser.updaters;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.util.MessageSender;

public abstract class AbstractUpdater<U, T>
implements Updater<U, T>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private boolean active = true;
    private MessageSender messagePublisher;
    
    public boolean isActive()
    {
        return active;
    }
    
    public void setActive(boolean active)
    {
        this.active = active;
    }
    
    public void setMessagePublisher( MessageSender messagePublisher )
    {
        this.messagePublisher = messagePublisher;
    }    
    
    @Override
    public void sweeperUpdate (Date currentTime, U currentState)
    {
        //Default to do nothing
    }
    
    public void injectFuserMessage( U message )
    {
        if( messagePublisher == null )
        {
            log.error( "messagePublisher is null!" );
            return;
        }
        
        try
        {            
            messagePublisher.publish( message );
        }
        catch( Exception ex )
        {
            log.error( "Error injecting message: " + ex.getMessage(), ex );
        }
    }       
}
