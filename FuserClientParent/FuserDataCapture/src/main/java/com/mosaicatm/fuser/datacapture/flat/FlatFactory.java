package com.mosaicatm.fuser.datacapture.flat;

import com.mosaicatm.fuser.datacapture.io.CaptureData;

public interface FlatFactory
{
    public <T, D> T flatten (CaptureData<D> data);
}
