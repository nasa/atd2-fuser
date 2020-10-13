package com.mosaicatm.fuser.transform.api;

import java.util.List;

public interface SourceBatchTransformApi<E,F,K> extends SourceTransformApi<K,F>
{
	public List<K> fromBatch (E batch);
}
