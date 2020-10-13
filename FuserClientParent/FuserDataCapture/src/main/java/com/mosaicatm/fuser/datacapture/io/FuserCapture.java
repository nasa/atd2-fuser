package com.mosaicatm.fuser.datacapture.io;

public interface FuserCapture<T>
{
    public void capture (CaptureData<T> data);
}
