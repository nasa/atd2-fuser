package com.mosaicatm.fuser.datacapture.io;

import java.io.Writer;
import java.util.Set;

import com.mosaicatm.container.io.DelimitedObjectWriter;
import com.mosaicatm.fuser.datacapture.CaptureType;

public interface DelimitedObjectFactory
{
    public <T> DelimitedObjectWriter<T> getDelimitedObjectWriter (CaptureType type, Writer writer);

    public <T> DelimitedObjectWriter<T> getDelimitedObjectWriter (CaptureType type, Writer writer, Set<String> fields);
}
