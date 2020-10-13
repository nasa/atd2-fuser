package com.mosaicatm.fuser.store.matm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.StoreRemover;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.time.SystemClock;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.common.MetaData;

public abstract class AbstractFuserStoreTimedRemover<T>
implements StoreRemover <T>
{
    private final Log log = LogFactory.getLog(getClass());
    private final FuserStore<T, MetaData> store;

    private long checkInterval = 1 * TimeFactory.MINUTE_IN_MILLIS;
    
    private boolean active = true;
    
    private String name;
    private Clock clock = new SystemClock ();
    private Timer timer;
    
    public AbstractFuserStoreTimedRemover (String name, FuserStore<T, MetaData> store)
    {
        this.name = name;
        this.store = store;
    }
    
    public abstract boolean isExpired(T data);
    
    @Override
    public void start ()
    {
        if (timer != null)
            stop ();
        
        timer = new Timer ();
        timer.scheduleAtFixedRate(new RemoveTask(), checkInterval, checkInterval);
    }
    
    @Override
    public void stop ()
    {
        if (timer != null)
            timer.cancel();
        
        timer = null;
    }
    
    @Override
    public void remove ()
    {
        if (!isStoreAvailable ())
            return;
        
        long startReadTime = System.currentTimeMillis();
        long startRemoveTime = 0;
        long totalRemoveTime = 0;
        int initialSize = 0;        
        int totalRemoved = 0;
        
        log.info(name + " Timed Removal Process: Started at time " + new Date( clock.getTimeInMillis() ));
        
        Collection<T> dataList = getData();
        
        if (dataList != null && !dataList.isEmpty())
        {
            initialSize = dataList.size();
                    
            List<T> toBeRemoved = new ArrayList<>();
            
            for (T data : dataList)
            {
                startRemoveTime = System.currentTimeMillis();
                if (isExpired (data))
                {
                    toBeRemoved.add(data);
                    totalRemoved++;
                }
                
                totalRemoveTime += ( System.currentTimeMillis() - startRemoveTime );
            }
            
            remove(toBeRemoved);
        }
        
        log.info(name + " Timed Removal Process: Removed " + totalRemoved + " out of " + initialSize + " elements" );
        log.info(name + " Timed Removal Process: Time for data removal check " + totalRemoveTime);
        log.info(name + " Timed Removal Process: Total time for removal process " + ( System.currentTimeMillis() - startReadTime ) );
    }
    
    @Override
    public void remove (T data)
    {        
        if (!isStoreAvailable ())
            return;
        
        if (data == null)
            return;
    
        if (log.isDebugEnabled())
        {
            String key = store.getKey(data);
            log.debug (name + " Removing data " + key);
        }
        
        try
        {
            store.lockStore(data);
            store.remove(data);
        }
        finally
        {
            store.unlockStore(data);
        }
    }
    
    @Override
    public void remove (Collection<T> dataList)
    {
        if (!isStoreAvailable ())
            return;
        
        if (dataList == null || dataList.isEmpty())
            return;
        
        log.info(name + " Removing " + dataList.size() + " elements");
        
        if (log.isDebugEnabled())
        {
            for (T data : dataList)
            {
                String key = store.getKey(data);
                log.debug(name + " Removing data for " + key);
            }
        }
        
        try
        {
            store.lockEntireStore();
            store.removeAll(dataList);
        }
        finally
        {
            store.unlockEntireStore();
        }
    }
    
    private Collection<T> getData ()
    {
        Collection<T> data = null;
        
        try
        {
            store.lockEntireStore();
            data = new ArrayList<>(store.getAll());
        }
        finally
        {
            store.unlockEntireStore();
        }
        
        return data;
    }
    
    private boolean isStoreAvailable ()
    {
        if (!active)
        {
            log.info(name + "Remove not active");
            return false;
        }
        
        if (store == null)
        {
            log.error (name + " Remove failed, flight store is not available");
            return false;
        }
        
        return true;
    }
    
    public void setCheckIntervalHours (int hours)
    {
        setCheckIntervalMillis (hours * TimeFactory.HOUR_IN_MILLIS);
    }
    
    public void setCheckIntervalMinutes (int minutes)
    {
        setCheckIntervalMillis (minutes * TimeFactory.MINUTE_IN_MILLIS);
    }
    
    public void setCheckIntervalMillis (long checkInterval)
    {
        this.checkInterval = checkInterval;
    }
    
    public void setClock (Clock clock)
    {
        this.clock = clock;
    }
    
    public Clock getClock()
    {
        return clock;
    }
    
    public String getName()
    {
        return name;
    }
    
    public boolean isActive()
    {
        return active;
    }
    
    public void setActive (boolean active)
    {
        this.active = active;
    } 
    
    private class RemoveTask
    extends TimerTask
    {
        @Override
        public void run() 
        {
            if (!active)
                return;
            
            if (log.isDebugEnabled())
                log.debug (name + " Checking for potential removes");
            
            remove ();
        }
    }
}
