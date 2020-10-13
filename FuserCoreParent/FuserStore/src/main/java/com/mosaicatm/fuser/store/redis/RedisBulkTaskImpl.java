package com.mosaicatm.fuser.store.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class RedisBulkTaskImpl
implements RedisBulkTask
{

    private final Log log = LogFactory.getLog(getClass());
    
    private RedisDB db;
    
    private Timer processTimer;
    private Timer reportTimer;
    
    private long processInterval = 1000L;
    private long reportInterval = 1000L;
    
    private Map<String, Map<String, String>> updateMap;
    private Map<String, List<String>> removeMap;
    
    // logging properties
    private long max = Long.MIN_VALUE;
    private long min = Long.MAX_VALUE;
    private long totalTime = 0;
    private int  batchCycle = 0;
    private int  totalCount = 0;
    private int  totalBatch = 0;
    private int currentCycle = 0;
    
    public RedisBulkTaskImpl()
    {
        updateMap = new HashMap<String, Map<String, String>>();
        removeMap = new HashMap<String, List<String>>();
    }
    
    public void start()
    {
        if (processTimer == null)
            processTimer = new Timer("RedisTaskProcessTimer");
        
        if (reportTimer == null)
            reportTimer = new Timer("RedisTaskReportTimer");
        
        processTimer.scheduleAtFixedRate(new ProcessTask(),
            processInterval, processInterval);
        
        reportTimer.scheduleAtFixedRate(new ReportTask(),
            reportInterval, reportInterval);
        
        if (reportInterval >= processInterval)
        {
            batchCycle = (int)(reportInterval / processInterval);
        }
        else
        {
            batchCycle = 1;
        }
    }
    
    public void stop()
    {
        if (processTimer != null)
            processTimer.cancel();
        
        if (reportTimer != null)
            reportTimer.cancel();
        
        processTimer = null;
        reportTimer = null;
    }
    
    class ProcessTask
    extends TimerTask
    {
        @Override
        public void run()
        {
            process();
        }
    }
    
    class ReportTask
    extends TimerTask
    {
        @Override
        public void run()
        {
            report();
        }
    }
    
    private void report()
    {
        log.info("Reporting Redis transaction update queue: "
                + updateSize()
                + " remove queue: "
                + removeSize());        
    }
    
    @Override
    public int updateSize()
    {
        synchronized (updateMap)
        {
            return updateMap.size();
        }
    }
    @Override
    public int removeSize()
    {
        synchronized (removeMap)
        {
            return removeMap.size();
        }
    }
    
    private void process()
    {
        long start = System.currentTimeMillis();
        
        Map<String, Map<String, String>> updates;
        Map<String, List<String>> removes;
        synchronized (updateMap)
        {
            synchronized (removeMap)
            {
                updates = new HashMap<>(updateMap);
                updateMap.clear();
                
                removes = new HashMap<>(removeMap);
                removeMap.clear();
            }
        }
        
        int batchCount = 0;
        int totalSize = 0;
        long totalTime = 0L;
        
        if (!updates.isEmpty() || !removes.isEmpty())
        {
            try (Jedis jedis = db.getClient())
            {
                Pipeline pipeline = jedis.pipelined();
                for (Entry<String, Map<String, String>> entry : updates.entrySet())
                {
                    Map<String, String> values = entry.getValue();
                    pipeline.hmset(entry.getKey(), values);  
                    
                    totalSize += values.size();
                    batchCount++;
                }
            
                for (Entry<String, List<String>> remove : removes.entrySet())
                {
                    List<String> value = remove.getValue();
                    if (value == null)
                    {
                        // delete meta data
                        pipeline.del(remove.getKey());
                    }
                    else
                    {
                        // delete flights
                        pipeline.hdel(remove.getKey(),
                                value.toArray(new String[value.size()]));
                    }
                    batchCount++;
                }
                totalSize += removes.size();
                pipeline.sync();
                pipeline.close();
                
                long end = System.currentTimeMillis();
                
                totalTime = end - start;
                
                if (totalTime > processInterval)
                {
                    log.warn("Redis batch took longer than batch interval: " 
                            + "processed " + totalSize + " Redis updates in "
                            + totalTime );
                }
                
                
                if (log.isDebugEnabled())
                {
                    log.debug("Processed " + totalSize + " Redis updates in "
                             + totalTime );
                }
            
            }
            catch (Exception e)
            {
                log.error("Error processing redish hash entry ", e);
            }
        }
        
        updateReportStatus(totalTime, totalSize, batchCount);
    }
    
    private void updateReportStatus(long time, int count, int batchCount)
    {
        currentCycle++;
        
        max = Math.max(time, max);
        min = Math.min(time, min);
        
        totalTime += time;
        totalCount += count;
        totalBatch += batchCount;
        
        if (currentCycle >= batchCycle)
        {
            log.info("Processed " + totalCount
                + " Redis updates in " + totalBatch + " batches"
                + " in " + currentCycle + " runs."
                + " Processing time per each run (min/max/average) = "
                + "(" + min + "/" + max + "/" + (totalTime / currentCycle) + ")");
            
            min = Long.MAX_VALUE;
            max = Long.MIN_VALUE;
            totalTime = 0L;
            totalCount = 0;
            currentCycle = 0;
            totalBatch = 0;
        }
    }
    
    @Override
    public void addUpdate(String key, String field, String body)
    {
        if (key == null || field == null || body == null)
            return;
        
        synchronized (updateMap)
        {
            Map<String, String> target = updateMap.get(key);
            if (target == null)
            {
                target = new HashMap<String, String>();
                updateMap.put(key, target);
            }
            
            target.put(field, body);
        }
    }
    
    @Override
    public void addUpdate(String key, Map<String, String> updates)
    {
        if (key == null || updates == null)
            return;
        
        synchronized (updateMap)
        {
            Map<String, String> target = updateMap.get(key);
            if (target == null)
            {
                target = new HashMap<String, String>();
                updateMap.put(key, target);
            }
            
            target.putAll(updates);
        }
    }
    
    @Override
    public void addDelete(String key, String... fields)
    {
        if (key == null)
            return;
        
        synchronized (updateMap)
        {
            synchronized (removeMap)
            {
                if (fields != null && fields.length > 0)
                {
                    // matm flight only
                    
                    List<String> removes = removeMap.get(key);
                    if (removes == null)
                    {
                        removes = new ArrayList<String>();
                        removeMap.put(key, removes);
                    }
                    
                    Map<String, String> target = updateMap.get(key);
                    for (String field : fields)
                    {
                        if (target != null)
                            target.remove(field);
                        
                        if (!removes.contains(field))
                        {
                            removes.add(field);
                        }
                    }
                }
                else
                {
                    // meta data only
                    updateMap.remove(key);
                    removeMap.put(key, null);
                }
            }
        }
    }

    public void setDb(RedisDB db)
    {
        this.db = db;
    }

    public void setProcessInterval(long processInterval)
    {
        this.processInterval = processInterval;
    }

    public void setReportInterval(long reportInterval)
    {
        this.reportInterval = reportInterval;
    }
}
