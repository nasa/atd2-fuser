package com.mosaicatm.fuser.transform;

import java.util.Properties;

import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.direct.DirectComponent;
import org.apache.camel.component.direct.DirectEndpoint;
import org.apache.camel.component.seda.SedaComponent;
import org.apache.camel.component.seda.SedaEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.language.MethodCallExpression;
import org.apache.camel.support.DefaultEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import com.mosaicatm.fuser.transform.api.FuserTransformPluginApi;
import com.mosaicatm.fuser.transform.api.SourceTransformApi;
import com.mosaicatm.lib.camel.RouteErrorHandler;
import com.mosaicatm.lib.camel.component.seda.SedaStatusLoggingComponent;
import com.mosaicatm.performancemonitor.common.metric.MetricEndpointMonitor;

/**
 * Class to add camel routes to the existing CamelContext. These new camel routes are
 * dynamically created from any available plugins that implement the FuserTransformPluginApi
 */
public class FuserTransformCamelRouteBuilder extends RouteBuilder
{
	public static Logger logger = LoggerFactory.getLogger( FuserTransformCamelRouteBuilder.class );
	
	private static final Integer DEFAULT_SEDA_QUEUE_SIZE = 100_000;
	private static final Integer DEFAULT_PLAYBACK_SEDA_QUEUE_SIZE = 1_000;
	
	private FuserTransformPluginApi fuserTransformApi;
	private BeanDefinitionRegistry registry;
	private String sourceCamelComponent;
	private Properties props;
	
	private int sedaQueueSize = DEFAULT_SEDA_QUEUE_SIZE;
	private boolean sedaBlockWhenFull = false;
	
	private RouteErrorHandler routeErrorHandler;
	
	private boolean useDirect = false;


	/**
	 * Constructor
	 * @param fuserTransformApi	An instance of the {@link FuserTransformPluginApi}
	 * @param beanFactory The Spring {@link AutowireCapableBeanFactory}
	 * @param props	Properties from the Fuser
	 */
	public FuserTransformCamelRouteBuilder(
			FuserTransformPluginApi fuserTransformApi,
			AutowireCapableBeanFactory beanFactory,
			Properties props)
	{
		this.fuserTransformApi = fuserTransformApi;
        this.registry = (BeanDefinitionRegistry) beanFactory;
        
        this.props = props;
        sourceCamelComponent = props.getProperty( "fuser.transform.source.camel.component" );
        
        if( sourceCamelComponent == null )
        {
        	sourceCamelComponent = "jms:topic:";
        }
        
        this.routeErrorHandler = new RouteErrorHandler();
	}

	@Override
	public void configure() throws Exception
	{
	    onException(Throwable.class)
	        .handled(true)
	        .process( routeErrorHandler );

	    useDirect = Boolean.parseBoolean( props.getProperty( "fuser.transform.use.direct" ) );
	    
	    Boolean playbackMode = Boolean.valueOf( props.getProperty( "fuser.playback.mode" ) );
	    
	    setSedaQueueProperties( playbackMode );
	    
		String sourceType = fuserTransformApi.getSourceType();
		String sourceStart = setupSourceStart( sourceType );
		
		// Create SourceTransformApiProxy
		String proxyBeanName = "fuser." + sourceType + "." + sourceType + "FlightTransferMatmTransformApiProxy";
		createTransformProxy( fuserTransformApi.getSourceTransformApi(), sourceType + "FlightTransfer", proxyBeanName );
		
		String processEndpointUri = useDirect ? "direct:" : "seda:";
		processEndpointUri += "process." + sourceType + "." + sourceType + "Transfer";
		if( !useDirect )
		{
		    processEndpointUri += "?size=" + sedaQueueSize + "&blockWhenFull=" + sedaBlockWhenFull;
		}
		
		// Setup MatmTransform route
		if( fuserTransformApi.getSourceStart() != null &&
				!fuserTransformApi.getSourceStart().isEmpty() &&
				fuserTransformApi.getSourceTransformApi() != null )
		{
			// create reroute endpoint
		    String rerouteName = null;
		    Endpoint rerouteEndpoint = null;

		    String rerouteUri = useDirect ? "direct:" : "seda-status-logging:";
			rerouteUri += "reroute." + sourceType + "." + sourceType +
			        "Transfer";
			if( !useDirect )
			{
			    rerouteUri += "?name=" + sourceType + "Receiver" +
			        "&loggingPeriod=10000" +
			        "&size=" + sedaQueueSize +
			        "&blockWhenFull=" + sedaBlockWhenFull;
			}
			
			rerouteName = "endpoint." + sourceType + "." + sourceType + "Transfer.reroute";
			if( !useDirect )
			{
			    rerouteEndpoint = createSedaStatusLoggingEndpoint( rerouteUri, rerouteName );
			}
			else
			{
			    rerouteEndpoint = createDirectEndpoint( rerouteUri );
			}
			
			// Route: source -> reroute -> fromXml -> process endpoint
			from( sourceStart )
				.to( rerouteEndpoint );
			
            RouteDefinition routeDef = from( rerouteEndpoint )
                .bean("fuser.core.SyncPoint", "sync")
                .setHeader(
                        "fuser.timestampFuserReceived",
                        new MethodCallExpression( "fuser.core.Clock", "getTimeInMillis") );
            if( playbackMode == null || !playbackMode )
            {
                routeDef.bean("monitor." + rerouteName, "mark(1)");
            }
            routeDef.bean(proxyBeanName, "fromXml")
				.to( processEndpointUri );
		}

		if( fuserTransformApi.getSourceTransformApi() != null )
		{  
		    String fuserReceiveUri;
            switch( fuserTransformApi.getFuserDestinationType() )
            {
                case SECTOR_ASSIGNMENT:
                    fuserReceiveUri = props.getProperty( "fuser.endpoint.toFuser.sectorAssignment.receive" );
                    break;
                    
                case FLIGHT:
                default:
                    fuserReceiveUri = props.getProperty( "fuser.endpoint.toFuser.receive" );
            }
            
			// Route: process endpoint -> toMatm -> FuserMainReceiver
			from( processEndpointUri )
                .bean(proxyBeanName, "toMatm(*)")
                .split( simple( "${body}" ) )
                .to( fuserReceiveUri );
		}
	}
	
	private String setupSourceStart( String sourceType )
	{
		String sourceStart = null;
		
		String overrideSourceCamelComponent = props.getProperty(
		        "fuser.transform." + sourceType + ".source.camel.component" );
		if( overrideSourceCamelComponent != null )
		{
			sourceStart = overrideSourceCamelComponent + fuserTransformApi.getSourceStart();
		}
		else
		{
			sourceStart = sourceCamelComponent + fuserTransformApi.getSourceStart();
		}

		return sourceStart;
	}
	
	private DirectEndpoint createDirectEndpoint( String endpointUri ) throws Exception
	{
	    DirectComponent directComponent = new DirectComponent();
	    directComponent.setCamelContext( getContext() );
	    return (DirectEndpoint)directComponent.createEndpoint( endpointUri );
	}
	
	private SedaEndpoint createSedaEndpoint( String endpointUri, String endpointName ) throws Exception
	{
		// Create Endpoint
        SedaComponent sedaComponent = new SedaComponent();
        sedaComponent.setQueueSize( sedaQueueSize );
		sedaComponent.setCamelContext( getContext() );
		SedaEndpoint rerouteEndpoint = (SedaEndpoint)sedaComponent.createEndpoint( endpointUri );
		
		// Create monitor
		createMonitor( rerouteEndpoint, endpointName );
		
		return rerouteEndpoint;
	}
	
	private SedaEndpoint createSedaStatusLoggingEndpoint( String endpointUri, String endpointName ) throws Exception
	{
		// Create Endpoint
		SedaStatusLoggingComponent sedaComponent = new SedaStatusLoggingComponent();
        sedaComponent.setQueueSize( sedaQueueSize );
		sedaComponent.setCamelContext( getContext() );
		SedaEndpoint envelopeProcessEndpoint = (SedaEndpoint)sedaComponent.createEndpoint( endpointUri );
		
		// Create monitor
		createMonitor( envelopeProcessEndpoint, endpointName );
		
		return envelopeProcessEndpoint;
	}
	
	private void createMonitor( DefaultEndpoint endpoint, String endPointName )
	{
		ConstructorArgumentValues constructorArgs = new ConstructorArgumentValues();
		constructorArgs.addIndexedArgumentValue( 0, endpoint );
		constructorArgs.addIndexedArgumentValue( 1, endPointName );

		GenericBeanDefinition beanDef = new GenericBeanDefinition();
		beanDef.setBeanClass( MetricEndpointMonitor.class );
		beanDef.setFactoryBeanName( "fuser-metrics.core.MetricProxyFactory" );
		beanDef.setFactoryMethodName( "registerEndpoint" );
		beanDef.setConstructorArgumentValues( constructorArgs );

		registry.registerBeanDefinition( "monitor." + endPointName, beanDef );
	}
	
	private void createTransformProxy( SourceTransformApi<?,?> sourceTransform, String name, String beanName )
	{
		if( sourceTransform == null )
			return;
		
		ConstructorArgumentValues constructorArgs = new ConstructorArgumentValues();
		constructorArgs.addIndexedArgumentValue( 0, sourceTransform );
		if( name != null )
		{
			constructorArgs.addIndexedArgumentValue( 1, name );
		}

		GenericBeanDefinition beanDef = new GenericBeanDefinition();
		beanDef.setBeanClass( SourceTransformApi.class );
		beanDef.setFactoryBeanName( "fuser-metrics.core.MetricProxyFactory" );
		beanDef.setFactoryMethodName( "proxySourceTransformApi" );
		beanDef.setConstructorArgumentValues( constructorArgs );

		registry.registerBeanDefinition( beanName, beanDef );
	}
	
	private void setSedaQueueProperties( Boolean playbackMode )
	{
	    if( playbackMode != null && playbackMode )
	    {
	        sedaQueueSize = DEFAULT_PLAYBACK_SEDA_QUEUE_SIZE;
	        sedaBlockWhenFull = true;
	    }

	    String sedaQueueSizeProperty = props.getProperty( "fuser.transform.seda.queue.size" );
	    if( sedaQueueSizeProperty != null && !sedaQueueSizeProperty.trim().isEmpty() )
	    {
	        try
	        {
	            sedaQueueSize = Integer.parseInt( sedaQueueSizeProperty );
	        }
	        catch( NumberFormatException nfe )
	        {
	            logger.warn( "Problem parsing int value from fuser.transform.seda.queue.size", nfe );
	        }
	    }
	}
}
