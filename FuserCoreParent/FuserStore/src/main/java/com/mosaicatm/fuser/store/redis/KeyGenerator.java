package com.mosaicatm.fuser.store.redis;

public class KeyGenerator {
	
	private int expireSeconds = 172800;	// 2 days expiration

	private String globalNamespace = "default:";
	private String keyType = null;
	
	public KeyGenerator ()
	{
		this.keyType = "fuser";
	}
	
	public KeyGenerator (String globalNamespace)
	{
		this.globalNamespace = normalizeNamespace (globalNamespace);
		this.keyType = "fuser";
	}
	
	
	public String getGlobalHashKey ()
	{
		return globalNamespace + keyType;
	}
	
	public String getGlobalHashKey (String key)
	{
		return globalNamespace + key;
	}

	public String getGufiToMetaDataHashKey(String gufi) {
		return globalNamespace + "metadata:" + gufi.toLowerCase();
	}    
    
	public String getKeyType ()
	{
		return keyType;
	}
	
	public void setKeyType (String keyType)
	{
		this.keyType = keyType;
	}
	
	public String getGlobalNamespace() {
		return globalNamespace;
	}

	public void setGlobalNamespace(String namespace) {
		this.globalNamespace = normalizeNamespace(namespace);
	}
	
	public static String normalizeNamespace(String namespace) {

		if(namespace == null) {
			namespace = "default:";
		} 
		
		namespace = namespace.toLowerCase();
		
		if(!namespace.endsWith(":")) {
				namespace = namespace + ":";
		}
		
		return namespace;
	}

	public int getExpireSeconds() {
		return expireSeconds;
	}

	public void setExpireSeconds(int expireSeconds) {
		this.expireSeconds = expireSeconds;
	}
	
}
