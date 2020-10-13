package com.mosaicatm.fuser.store.event;

public class FuserStoreEvent <T>
{
	public enum FuserStoreEventType
	{
		ADD,
		UPDATE,
		REMOVE,
		NOT_SET;
	}
	
	private FuserStoreEventType eventType = FuserStoreEventType.NOT_SET;
	private T content;
	
	public FuserStoreEvent ()
	{
		// default constructor
	}
	
	public FuserStoreEvent (FuserStoreEventType eventType)
	{
		this (eventType, null);
	}
	
	public FuserStoreEvent (FuserStoreEventType eventType, T content)
	{
		this.eventType = eventType;
		this.content = content;
	}
	
	public FuserStoreEventType getEventType ()
	{
		return eventType;
	}
	
	public void setEventType (FuserStoreEventType eventType)
	{
		this.eventType = eventType;
	}
	
	public T getContent ()
	{
		return content;
	}
	
	public void setContent (T content)
	{
		this.content = content;
	}
}
