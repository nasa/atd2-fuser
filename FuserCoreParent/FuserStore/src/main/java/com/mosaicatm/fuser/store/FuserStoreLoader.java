package com.mosaicatm.fuser.store;

public interface FuserStoreLoader<T, U> 
{
	public FuserStore<T, U> loadStore ();
}
