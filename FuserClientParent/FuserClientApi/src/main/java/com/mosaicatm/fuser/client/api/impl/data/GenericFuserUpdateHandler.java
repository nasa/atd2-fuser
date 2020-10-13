package com.mosaicatm.fuser.client.api.impl.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.FuserClientApi;
import com.mosaicatm.fuser.client.api.data.FuserUpdateHandler;

public class GenericFuserUpdateHandler <T>
implements FuserUpdateHandler <T>
{
	private final Log log = LogFactory.getLog(getClass());

	private FuserClientApi<T> api;
	
	@Override
	public void handleUpdate (T data)
	{
		if (data == null)
			return;
		
		if (api == null)
		{
			log.error ("Error handling update, FuserClientApi is not set");
			return;
		}
		
		T storeFlight = findData (data);
		
		if (storeFlight == null)
		{
			api.handleAdd(data);
		}
		else
		{
			api.handleUpdate(data, storeFlight);
		}
	}
	
	@Override
	public void handleRemove (T data)
	{
		if (api != null)
			api.handleRemove(data);
	}
	
	private T findData (T data)
	{
		T storeData = null;
		
		if (api.getStore() != null)
		{
		    String key = api.getStore().getKey(data);
			if (key != null)
			{
			    storeData = api.getStore().get(key);
			}
		}
		
		return storeData;
	}
	
	public void setFuserClientApi (FuserClientApi<T> api)
	{
		this.api = api;
	}
}
