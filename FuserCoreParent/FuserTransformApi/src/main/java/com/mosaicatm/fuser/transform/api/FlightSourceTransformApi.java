package com.mosaicatm.fuser.transform.api;

import java.util.List;

import com.mosaicatm.matmdata.flight.MatmFlight;

public interface FlightSourceTransformApi<E> extends SourceTransformApi<E, MatmFlight>
{
    @Override
    public List<MatmFlight> toMatm (E flight);
    //public List<MatmFlight> toMatm (E flight, String airport);
    
    @Override
    public E fromXml (String xml);
    //public String toXml (S flight);
    
	//public E toBatch (List<S> batch);
	//public List<S> fromBatch (E batch);
}
