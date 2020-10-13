package com.mosaicatm.fuser.datacapture.db.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.fuser.datacapture.DataWrapper;
import com.mosaicatm.fuser.datacapture.db.dao.MatmObjectSummaryMapperWrapper;
import com.mosaicatm.lib.database.bulk.listener.BulkLoaderListener;
import com.mosaicatm.matmdata.common.MatmObject;

public class MatmObjectSummaryListener<T extends MatmObject, F>
implements BulkLoaderListener<DataWrapper<T>>
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private MatmObjectSummaryMapperWrapper<T, F> summaryMapperWrapper;
	
	private boolean active;
	
	private Map<String, DataWrapper<T>> flightMap;
	private MatmIdLookup<T, String> matmIdLookup;
	
	private Timer processTimer;
	private Timer reportTimer;
	
	private long batchTimeInterval = 5000L;
	
	private List<DataWrapper<T>> unfinishedJob;
	
	public MatmObjectSummaryListener()
	{
		flightMap = new HashMap<String, DataWrapper<T>>();
		unfinishedJob = null;
	}
	
	public void start()
	{
		if (!active)
			return;
		
		if (processTimer == null)
			processTimer = new Timer("MatmSummaryBatch");
		
		if (reportTimer == null)
			reportTimer = new Timer("MatmSummaryBatchReport");
		
		processTimer.scheduleAtFixedRate(new ProcessTask(), batchTimeInterval, batchTimeInterval);
		reportTimer.scheduleAtFixedRate(new ReportTask(), batchTimeInterval, batchTimeInterval);
		initializeShutDownProcess();
	}
	
	private void initializeShutDownProcess()
	{
		Runtime.getRuntime().addShutdownHook
		(
		  new Thread("MatmSummarySafeShutdownHook")
		  {
		    @Override 
		    public void run()
		    {
		    	List<DataWrapper<T>> finalFlights = new ArrayList<>();
		    	if (unfinishedJob != null)
		    	{
		    		finalFlights.addAll(unfinishedJob);
		    	}
		    	
		    	if (flightMap != null)
		    	{
		    		synchronized(flightMap)
		    		{
		    			finalFlights.addAll(flightMap.values());
		    		}
		    	}
		    	
		    	if (!finalFlights.isEmpty())
		    	{
		    		handleDatabaseUpdates(finalFlights);
		    	}
		    	
		    	log.info("Safe ShutDown completed.");
		    }
		});
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
			if (flightMap != null)
				log.info("Current batch size: {}", flightMap.size());
		}
	}
	
	private void process()
	{
		if (!active)
			return;
		
		List<DataWrapper<T>> list;
		synchronized (flightMap)
		{
			list = new ArrayList<>(flightMap.values());
			unfinishedJob = list;
			flightMap.clear();
		}
			handleDatabaseUpdates(list);
			
			unfinishedJob = null;
		
	}
	
	private void handleDatabaseUpdates(List<DataWrapper<T>> flights)
	{
		try
		{
			summaryMapperWrapper.handle(flights);
		}
		catch (Exception e)
		{
			log.error("Fail to handle data", e);
		}
	}
	
	@Override
	public void handle(List<DataWrapper<T>> messages)
	{
		if (!active || messages == null || summaryMapperWrapper == null)
			return;
		
		synchronized (flightMap)
		{
			for (DataWrapper<T> message : messages)
			{
			    if (message == null || message.getData() == null)
			        continue;
			    
			    String key = matmIdLookup.getIdentifier(message.getData());
				if (key != null)
				{
					flightMap.put(key, message);
				}
			}
		}
		
	}

	@Override
	public void triggerRemoveProcess()
	{
		if (!active || summaryMapperWrapper == null)
			return;
		
		try
		{
			summaryMapperWrapper.handleRemove();
		}
		catch (Exception e)
		{
			log.error("Fail to handle database remove", e);
		}
	}

	public void setSummaryMapperWrapper(MatmObjectSummaryMapperWrapper<T, F> summaryMapperWrapper)
	{
		this.summaryMapperWrapper = summaryMapperWrapper;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public void setBatchTimeInterval(long batchTimeInterval)
	{
		this.batchTimeInterval = batchTimeInterval;
	}

	public void setMatmIdLookup(MatmIdLookup<T, String> matmIdLookup)
	{
	    this.matmIdLookup = matmIdLookup;
	}
}
