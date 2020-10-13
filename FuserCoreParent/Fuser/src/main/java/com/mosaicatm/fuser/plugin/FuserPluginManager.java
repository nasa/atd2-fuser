package com.mosaicatm.fuser.plugin;

import java.nio.file.Path;

import org.pf4j.CompoundPluginLoader;
import org.pf4j.DefaultPluginLoader;
import org.pf4j.JarPluginLoader;
import org.pf4j.PluginClassLoader;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginLoader;
import org.pf4j.spring.SpringPluginManager;

public class FuserPluginManager extends SpringPluginManager
{
    @Override
	public void init()
	{
		this.loadPlugins();
		this.startPlugins();
	}
	
    // I was getting an error with the PluginClassLoader. So I am creating a "parentFirst" PluginClassLoader
    // here, which seems to fix the issue.
    // Some reading here (https://pf4j.org/doc/troubleshooting.html) indicates that if the same jar is in the main lib folder AND the plugin lib folder
	// then the two instances are not considered the same  (A.jar/PluginApiClass != B.jar/PluginApiClass, when A.jar == B.jar)
	// Creating a "parentFirst" loader fixes this because the plugin and main program now hit the same jars.
	// TODO: Perhaps a better approach is to just require the plugin to set the FuserTransformApi dependency to "<scope>provided</scope>"
	@Override
	protected PluginLoader createPluginLoader()
	{
		return new CompoundPluginLoader()
	            .add(new DefaultPluginLoader(this, pluginClasspath) {

	            	@Override
	            	protected PluginClassLoader createPluginClassLoader( Path pluginPath,
	            			PluginDescriptor pluginDescriptor )
	            	{
	            		return new PluginClassLoader(pluginManager, pluginDescriptor, getClass().getClassLoader(), true);
	            	}
	            })
	            .add(new JarPluginLoader(this));
	}
}
