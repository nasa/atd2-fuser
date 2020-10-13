package com.mosaicatm.fuser.datacapture.db;

import com.mosaicatm.fuser.datacapture.CaptureType;

public interface DatabaseNameFactory
{
    public String getDatabaseName (CaptureType type);
    public String getDatabaseName (CaptureType type, String identifier);
}
