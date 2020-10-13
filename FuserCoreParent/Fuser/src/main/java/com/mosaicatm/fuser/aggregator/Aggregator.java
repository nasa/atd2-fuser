package com.mosaicatm.fuser.aggregator;

public interface Aggregator <T> 
{
	public T aggregate (T data);
}
