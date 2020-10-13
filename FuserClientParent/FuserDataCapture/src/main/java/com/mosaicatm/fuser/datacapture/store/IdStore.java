package com.mosaicatm.fuser.datacapture.store;

import java.util.List;

import com.mosaicatm.fuser.datacapture.CaptureType;

public interface IdStore
{
    public IdSet generateRecord(String gufi, List<CaptureType> types);
    
    public IdSet remove(String gufi);
}
