package com.mosaicatm.fuser.updaters;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.common.MetaData;

public class UpdaterSweeper <T extends MatmObject>
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private boolean active = true;
    
    private FuserStore<T,MetaData> fuserStore;
        
    private Clock clock;
    
    private long timerStartDelayMillis = 5 * TimeFactory.MINUTE_IN_MILLIS;
    private long timerIntervalMillis = 1 * TimeFactory.MINUTE_IN_MILLIS;
    
    private Timer timer;    
    
    protected Updater<T, T> flightPreUpdater; //actually a factory of updaters
    protected Updater<T, T> flightPostUpdater; //actually a factory of updaters  
    
    public void start ()
    {
        if( !active )
            return;
        
        if (timer != null)
            stop ();
        
        if( timerIntervalMillis > 0 )
        {
            timer = new Timer ();
            timer.scheduleAtFixedRate(new SweeperTask(), timerStartDelayMillis, timerIntervalMillis);
        }
        
        if( flightPreUpdater == null )
        {
            log.warn( "FlightPreUpdater is null!" );
        }  
        
        if( flightPostUpdater == null )
        {
            log.warn( "FlightPostUpdater is null!" );
        }          
    }
    
    public void stop ()
    {
        if (timer != null)
            timer.cancel();
        
        timer = null;
    }     
    
    public void setFuserStore( FuserStore<T, MetaData> store )
    {
        this.fuserStore = store;
    }

    public void setFlightPreUpdater( Updater<T, T> flightPreUpdater )
    {
        this.flightPreUpdater = flightPreUpdater;
    }

    public void setFlightPostUpdater( Updater<T, T> flightPostUpdater )
    {
        this.flightPostUpdater = flightPostUpdater;
    }    
    
    public void setTimerIntervalMillis( long timerIntervalMillis )
    {
        this.timerIntervalMillis = timerIntervalMillis;
    }    

    public void setTimerStartDelayMillis( long timerStartDelayMillis )
    {
        this.timerStartDelayMillis = timerStartDelayMillis;
    }
    
    public void setActive( boolean active )
    {
        this.active = active;
    }    
    
    public void setClock( Clock clock )
    {
        this.clock = clock;
    }    
    
    public void update()
    {
        if( clock == null )
        {
            log.error( "Could not run sweeper: Clock is null!" );
        }
        
        if( fuserStore == null )
        {
            log.error( "Could not run sweeper: FuserStore is null!" );
        }      
        
        log.debug( "Starting updater sweeper ..." );
        
        long startTime = System.currentTimeMillis();
        Date time = new Date( clock.getTimeInMillis() );
        ArrayList<T> currentStates = new ArrayList<>( fuserStore.getAll() );

        for( T currentState : currentStates )
        {
            if( flightPreUpdater != null )
            {
                flightPreUpdater.sweeperUpdate( time, currentState );
            }
            
            if( flightPostUpdater != null )
            {
                flightPostUpdater.sweeperUpdate( time, currentState );
            }              
        }
        
        log.info( "Sweeper updates completed in {} ms.", ( System.currentTimeMillis() - startTime ));
    }
    
    private class SweeperTask
    extends TimerTask
    {
        @Override
        public void run() 
        {
            if (!active)
            {
                return;
            }
            
            try
            {            
                update();
            }
            catch( Exception ex )
            {
                log.error( "Exception in sweeper task: {}", ex.getMessage(), ex );
            }
        }
    }       
}
