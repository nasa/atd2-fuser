package com.mosaicatm.fuser.transform.batch;

import java.util.List;

public interface SourceBatchTransformApi <E, K> 
{
	public E toBatch (List<K> batch);
	public List<K> fromBatch (E batch);
}
