package com.mosaicatm.fuser.common.matm.util;

public interface MatmIdLookup<T, R>
{
    public R getIdentifier(T data);
}
