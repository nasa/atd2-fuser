package com.mosaicatm.fuser.datacapture;

import com.mosaicatm.lib.time.Clock;

public interface FuserDataCaptureApi<T>
{
	public Clock getClock();
	public void publishToProcess(T data);
	
	public void start();
}
