package com.mosaicatm.fuser.datacapture.io.batch;

import com.mosaicatm.container.io.DelimitedObjectWriter;
import com.mosaicatm.lib.database.bulk.impl.DBWriter;

public class BulkWriter<T>
implements DBWriter
{
	private DelimitedObjectWriter<T> writer;
	private String onlyIncludedColumnsAsString = null;
	
	public BulkWriter(DelimitedObjectWriter<T> writer)
	{
		this.writer = writer;
	}
	
	public BulkWriter(DelimitedObjectWriter<T> writer, String onlyIncludedColumnsAsString)
	{
		this.writer = writer;
		this.onlyIncludedColumnsAsString = onlyIncludedColumnsAsString;
	}
	
	public void writeLine(T message)
	{
		if (writer != null)
			writer.writeLine(message);
	}
	
	@Override
	public void flush()
	{
		if (writer != null)
			writer.flush();
	}

	@Override
	public void close()
	{
		if (writer != null)
			writer.close();
	}
	
	@Override
	public String getOnlyIncludedColumnsAsString()
	{
		return onlyIncludedColumnsAsString;
	}

}
