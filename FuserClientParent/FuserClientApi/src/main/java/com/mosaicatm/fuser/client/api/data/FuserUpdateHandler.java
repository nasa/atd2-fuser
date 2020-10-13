package com.mosaicatm.fuser.client.api.data;

public interface FuserUpdateHandler <T>
{
	public void handleUpdate (T update);
	public void handleRemove (T remove);
}
