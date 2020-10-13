package com.mosaicatm.fuser.filter;

public interface MatmFilter<T>
{
	public T filter(T flight);
	
	public boolean isActive();
}
