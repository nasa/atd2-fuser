package com.mosaicatm.fuser.transform.util;

public interface Upconverter <T, R>
{
	public R upconvert (T message);
}
