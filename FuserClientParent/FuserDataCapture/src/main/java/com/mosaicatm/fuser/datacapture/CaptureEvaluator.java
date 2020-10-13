package com.mosaicatm.fuser.datacapture;

import java.util.List;

/**
 * Utility to generate a list valid of capture types
 *
 */
public interface CaptureEvaluator<T>
{
    public List<CaptureType> getCaptureTypes(T data);
}
