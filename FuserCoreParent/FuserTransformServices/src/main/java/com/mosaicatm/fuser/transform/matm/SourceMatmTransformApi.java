package com.mosaicatm.fuser.transform.matm;

import java.util.List;

public interface SourceMatmTransformApi<E,K> {
    public E fromMatm (K matm);
    public K toMatm (E flight);
    
    public E fromMatm (K matm, String airport);
    public E fromMatm (K matm, List<String> airports);
    public K toMatm (E flight, String airport);

    public E fromMatm (K oldMatm, K matm, String airport);
    public E fromMatm(K fullStateMatm, K matm, List<String> airports);
    
    public E fromXml (String xml);
    public String toXml (E flight);
    
}
