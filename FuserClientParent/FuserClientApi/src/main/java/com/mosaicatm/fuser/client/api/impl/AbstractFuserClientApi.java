package com.mosaicatm.fuser.client.api.impl;

import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.activation.DataHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.FuserClientApi;
import com.mosaicatm.fuser.client.api.data.DataAdder;
import com.mosaicatm.fuser.client.api.data.DataRemover;
import com.mosaicatm.fuser.client.api.data.DataUpdater;
import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventManager;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventManager;
import com.mosaicatm.fuser.client.api.event.FuserSyncCompleteEventListener;
import com.mosaicatm.fuser.client.api.event.FuserSyncCompleteEventManager;
import com.mosaicatm.lib.messaging.MessageProducer;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.util.concurrent.BlockingQueueProcessor;
import com.mosaicatm.lib.util.concurrent.Handler;
import com.mosaicatm.lib.util.filter.Filter;
import com.mosaicatm.lib.util.filter.LogicalFilter;
import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;

public abstract class AbstractFuserClientApi<T>
implements FuserClientApi<T>
{
    private final Log log = LogFactory.getLog(getClass());

    private static final int ZIP_BUFFER_SIZE = 8_000;

    private Clock clock;
    private DataAdder<T> dataAdder;
    private DataUpdater<T> dataUpdater;
    private DataRemover<T> dataRemover;
    private LogicalFilter<T> filter;
    private FuserClientStore<T> store;
    private FuserProcessedEventManager<T> processedEventManager;
    private FuserReceivedEventManager<T> receivedEventManager;
    protected FuserSyncCompleteEventManager fuserSyncCompleteEventManager;
    
    private MessageProducer messageProducer;
    private String dataPublishEndpoint;
    private String dataBatchPublishEndpoint;
    private String dataPublishRemoveEndpoint;
    
    private boolean publishActive = false;
    private GenericMarshaller matmFlightEnvMarshaller = null;
    
    protected Object publishSyncObject = new Object();

    private BlockingQueueProcessor<T> publishQueueProcessor;
    private BlockingQueueProcessor<List<T>> publishBatchQueueProcessor;
    private BlockingQueueProcessor<T> publishRemoveQueueProcessor;


    public AbstractFuserClientApi()
    {
        publishQueueProcessor = new BlockingQueueProcessor<>( new PublishQueueHandler() );
        publishBatchQueueProcessor = new BlockingQueueProcessor<>( new PublishBatchQueueHandler() );
        publishRemoveQueueProcessor = new BlockingQueueProcessor<>( new PublishRemoveQueueHandler() );

        publishQueueProcessor.wait( publishSyncObject );
        publishBatchQueueProcessor.wait( publishSyncObject );
        publishRemoveQueueProcessor.wait( publishSyncObject );

        publishQueueProcessor.start();
        publishBatchQueueProcessor.start();
        publishRemoveQueueProcessor.start();
    }
    
    public abstract MatmTransferEnvelope generateEnvelope(List<T> batch);
    
    @Override
    public Clock getClock() 
    {
        return clock; 
    }
    
    @Override
    public FuserClientStore<T> getStore()
    {
        return store;
    }
    
    @Override
    public void handleAdd(T data)
    {
        if(filter != null && !filter.doFilter(data))
        {
            if(log.isDebugEnabled())
                log.debug("Data is being filtered " + data);
            return;
        }

        if (dataAdder != null)
            dataAdder.add(data);
    }
    
    @Override
    public void handleUpdate(T update, T target)
    {
        if(filter != null && !filter.doFilter(update))
        {
            if(log.isDebugEnabled())
                log.debug("Data is being filtered " + update);
            return;
        }

        if (dataUpdater != null)
            dataUpdater.update(update, target);
    }
    
    @Override
    public void handleRemove(T data) 
    {
        if (dataRemover != null)
            dataRemover.remove(data);
    }
    
    @Override
    public void registerReceivedEventListener(FuserReceivedEventListener<T> eventListener)
    {
        if (eventListener == null)
            return;

        if (receivedEventManager != null)
            receivedEventManager.registerListener(eventListener);
    }

    @Override
    public void unregisterReceivedEventListener(FuserReceivedEventListener<T> eventListener)
    {
        if (eventListener != null)
            return;

        if (receivedEventManager != null)
            receivedEventManager.unregisterListener(eventListener);
    }

    @Override
    public void registerProcessedEventListener(FuserProcessedEventListener<T> eventListener) 
    {
        if (eventListener == null)
            return;

        if (processedEventManager != null)
            processedEventManager.registerListener(eventListener);
    }

    @Override
    public void unregisterProcessedEventListener(FuserProcessedEventListener<T> eventListener)
    {
        if (eventListener == null)
            return;

        if (processedEventManager != null)
            processedEventManager.unregisterListener(eventListener);
    }
    
    @Override
    public void registerFuserSyncCompleteEventListener( FuserSyncCompleteEventListener eventListener )
    {
        if( eventListener == null || fuserSyncCompleteEventManager == null )
        {
            return;
        }

        fuserSyncCompleteEventManager.registerListener( eventListener );
    }
    
    @Override
    public void unregisterFuserSyncCompleteEventListener( FuserSyncCompleteEventListener eventListener )
    {
        if( eventListener == null || fuserSyncCompleteEventManager == null )
        {
            return;
        }
        
        fuserSyncCompleteEventManager.unregisterListener( eventListener );
    }
    
    @Override
    public void publish(T data) 
    {
        if( publishActive )
        {
            publishQueueProcessor.process( data );
        }
    }

    @Override
    public void publishBatch(List<T> batch) 
    {
        if( publishActive )
        {
            publishBatchQueueProcessor.process( batch );
        }
    }

    @Override
    public void publishRemove(T data) 
    {
        if( publishActive )
        {
            publishRemoveQueueProcessor.process( data );
        }
    }
    
    public void unlockPublishers()
    {
        if( publishSyncObject != null )
        {
            synchronized (publishSyncObject)
            {
                publishSyncObject.notifyAll();
            }
        }
    }
    
    @Override
    public void addFilter(Filter<T> f) 
    {
        if (filter != null && f != null)
            filter.addFilter(f);
    }
    
    public Filter<T> getFilter()
    {
        return filter;
    }
    
    public void setFilter(LogicalFilter<T> filter)
    {
        this.filter = filter;
    }
    
    public void setClock (Clock clock)
    {
        this.clock = clock;
    }

    public void setStore (FuserClientStore<T> store)
    {
        this.store = store;
    }

    public void setDataAdder (DataAdder<T> dataAdder)
    {
        this.dataAdder = dataAdder;
    }

    public void setDataUpdater (DataUpdater<T> dataUpdater)
    {
        this.dataUpdater = dataUpdater;
    }

    public void setDataRemover (DataRemover<T> dataRemover)
    {
        this.dataRemover = dataRemover;
    }
    
    public void setProcessedEventManager (FuserProcessedEventManager<T> eventManager)
    {
        this.processedEventManager = eventManager;
    }

    public void setReceivedEventManager (FuserReceivedEventManager<T> eventManager)
    {
        this.receivedEventManager = eventManager;
    }
    
    public void setFuserSyncCompleteEventManager( FuserSyncCompleteEventManager eventManager )
    {
        this.fuserSyncCompleteEventManager = eventManager;
    }

    public void setMessageProducer(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }
    
    public void setDataPublishEndpoint(String dataPublishEndpoint) {
        this.dataPublishEndpoint = dataPublishEndpoint;
    }

    public void setDataBatchPublishEndpoint(String dataBatchPublishEndpoint) {
        this.dataBatchPublishEndpoint = dataBatchPublishEndpoint;
    }

    public void setDataPublishRemoveEndpoint(String dataPublishRemoveEndpoint) {
        this.dataPublishRemoveEndpoint = dataPublishRemoveEndpoint;
    }
    
    public void setPublishActive( boolean publishActive )
    {
        this.publishActive = publishActive;
    }

    public void setMatmFlightEnvMarshaller( GenericMarshaller matmFlightEnvMarshaller )
    {
        this.matmFlightEnvMarshaller = matmFlightEnvMarshaller;
    }

    protected MatmTransferEnvelope processDataHandler( DataHandler dataHandler )
    {
        if( dataHandler == null || matmFlightEnvMarshaller == null )
        {
            return null;
        }

        MatmTransferEnvelope matmTransferEnv = null;

        try( InputStream inputStream = dataHandler.getInputStream();
                GZIPInputStream zipInputStream = new GZIPInputStream( inputStream, ZIP_BUFFER_SIZE ); )
        {            
            matmTransferEnv = matmFlightEnvMarshaller.unmarshallToObject( zipInputStream );
        }
        catch( Exception e )
        {
            log.error( "Unable to process incoming sync flight data", e );
        }

        return matmTransferEnv;
    }
    
    private class PublishQueueHandler implements Handler<T>
    {
        @Override
        public void handle( T data )
        {
            if( messageProducer != null )
            {
                if (dataPublishEndpoint != null) 
                {
                    messageProducer.publish(dataPublishEndpoint, data);
                } 
                else 
                {
                    log.error("Call to publish(" + data.getClass() + ") but dataPublishEndpoint is not configured");
                }
            } 
            else 
            {
                log.error("Call to publish(" + data.getClass() + ") but no MessageProducer instance is configured");
            }
        }

        @Override
        public void onShutdown()
        {

        }
    }

    private class PublishBatchQueueHandler implements Handler<List<T>>
    {

        @Override
        public void handle( List<T> batch )
        {
            if( messageProducer != null )
            {
                if (dataBatchPublishEndpoint != null) 
                {
                    if(batch != null && !batch.isEmpty())
                    {
                        MatmTransferEnvelope envelope = generateEnvelope(batch);

                        if (envelope != null)
                        {
                            messageProducer.publish(dataBatchPublishEndpoint, envelope);
                        }
                    }
                    else
                    {
                        log.warn("Call to publishBatch but the batch is empty.");
                    }
                } 
                else 
                {
                    log.error("Call to publishBatch but dataBatchPublishEndpoint is not configured");
                }
            }
            else {
                log.error("Call to publishBatch but no MessageProducer instance is configured");
            }
        }

        @Override
        public void onShutdown()
        {

        }
    }

    private class PublishRemoveQueueHandler implements Handler<T>
    {

        @Override
        public void handle( T data )
        {            
            if( messageProducer != null )
            {
                if (dataPublishRemoveEndpoint != null) 
                {
                    messageProducer.publish(dataPublishRemoveEndpoint, data);
                } 
                else 
                {
                    log.error("Call to remove(" + data.getClass() + ")but dataPublishRemoveEndpoint is not configured");
                }
            }
            else 
            {
                log.error("Call to remove(" + data.getClass() + ") but no MessageProducer instance is configured");
            }
        }

        @Override
        public void onShutdown()
        {

        }
    }
}
