package com.mosaicatm.fuser.transform;

import java.util.List;

import com.mosaicatm.lib.util.Transformer;

public interface FuserAirportSpecificTransformer<R,E> extends Transformer<R,E>{

	public R transform (E object, String airport);
	
	public R transform (E object, List<String> airports);
}
