package com.mosaicatm.fuser.transform.api;

import java.util.List;

public interface SourceTransformApi<E, F>
{
    public List<F> toMatm (E flight);
    //public List<MatmFlight> toMatm (E flight, String airport);
    
    public E fromXml (String xml);
    //public String toXml (S flight);
    
	//public E toBatch (List<S> batch);
	//public List<S> fromBatch (E batch);
}
