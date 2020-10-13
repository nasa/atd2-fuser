package com.mosaicatm.fuser.client.api.data;

import java.util.List;

public interface FuserClientStore <T> 
{
	public void add (T data);
	public void update (T data);
	public void remove (T data);
	public void removeByKey (Object key);
	
	public String getKey(T data);
	
	public T get (Object key);
	public List<T> getAll ();
	
	public int size ();
	
	public void clear ();
	
	public void lock ();
	public void unlock ();
}
