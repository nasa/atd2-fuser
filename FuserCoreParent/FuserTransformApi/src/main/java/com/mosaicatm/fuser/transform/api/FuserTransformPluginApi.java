package com.mosaicatm.fuser.transform.api;

import org.apache.camel.CamelContext;
import org.pf4j.ExtensionPoint;

public interface FuserTransformPluginApi<E,F> extends ExtensionPoint
{
    public enum FuserDestinationType 
    {
        FLIGHT,
        SECTOR_ASSIGNMENT
    };

	/**
	 * Get the destination type
     * @return the destination type.
	 */    
    public FuserDestinationType getFuserDestinationType();
    
	/**
	 * A topic on which single instances of source messages come into the Fuser.
	 */
	public String getSourceStart();
	
	/**
	 * A name identifying the source type of these messages. Receipt of messages can be enabled / disabled
	 * based on source type in the fuser's properties file.
	 */
	public String getSourceType();
	
	public String getComponentName();
	
	public void initialize( FuserPluginProps props );
	
	public SourceTransformApi<E,F> getSourceTransformApi();
	
	/**
	 * A method to add a CamelComponent to the CamelContext. If the Fuser's default ActiveMQ JMS CamelComponent
	 * will be used this method can do nothing.
	 * 
	 * @param camelContext The Fuser's CamelContext
	 */
	public void addCamelComponent( CamelContext camelContext );
}
