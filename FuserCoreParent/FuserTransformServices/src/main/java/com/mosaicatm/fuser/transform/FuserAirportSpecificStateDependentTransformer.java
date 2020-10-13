package com.mosaicatm.fuser.transform;

import java.util.List;

public interface FuserAirportSpecificStateDependentTransformer<R, E> 
extends FuserAirportSpecificTransformer<R, E>
{
    public R transform( E fullStateObject, E object, String airport );
    
    public R transform( E fullStateObject, E object, List<String> airports );
}
