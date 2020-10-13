package com.mosaicatm.fuser.store;

import java.util.Collection;

public interface StoreRemover <T>
{
	public void start ();    
	public void stop ();    
	public void remove ();
	public void remove (T data);
	public void remove (Collection<T> data);
}
