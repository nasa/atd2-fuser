package com.mosaicatm.fuser.store.event;

public interface FuserStoreListener <T>
{
	public void handleFuserStoreEvent (FuserStoreEvent<T> event);
}
