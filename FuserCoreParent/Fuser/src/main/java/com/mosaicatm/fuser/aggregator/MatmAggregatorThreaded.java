package com.mosaicatm.fuser.aggregator;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.updaters.Updater;
import com.mosaicatm.lib.messaging.MessageProducer;
import com.mosaicatm.lib.messaging.MessageProducerOwner;
import com.mosaicatm.lib.util.concurrent.BlockingQueueProcessor;
import com.mosaicatm.lib.util.concurrent.Handler;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.common.MetaData;


public class MatmAggregatorThreaded <T extends MatmObject>
        implements Aggregator<T>, MessageProducerOwner
{
    private static final Logger logger = LoggerFactory.getLogger( MatmAggregatorThreaded.class );
    
    private static final String AIRCRAFT_AGGREGATION_TYPE = "MatmAircraft";

    private List<MatmAggregator<T>> aggregators;
    
    private final List<BlockingQueueProcessor<T>> blockingQueueProcessors;
    private int processingThreadCount = 4;
    private int blockingQueueCapacity = 100000;
    
    private MessageProducer messageProducer;
    
    private Timer reportingTimer;
    private int reportingTimeMillis = 10_000;
    
    private String destinationQueue = "seda:fuser.fromFuser.process.batch?size=1000000";
    
    private FuserStore<T,MetaData> fuserStore;
    protected MetaDataManager<T> metaDataManager;
    protected Updater<T, T> flightPreUpdater; //actually a factory of updaters
    protected Updater<T, T> flightPostUpdater; //actually a factory of updaters
    
    private Class<?> aggregationClass = null;


    public MatmAggregatorThreaded( int processingThreadCount, String aggregationType )
    {
        this.processingThreadCount = processingThreadCount;
        blockingQueueProcessors = new ArrayList<>( processingThreadCount );
        aggregators = new ArrayList<>( processingThreadCount );
        
        if( AIRCRAFT_AGGREGATION_TYPE.equals( aggregationType ) )
        {
            aggregationClass = MatmAircraftDiffAggregator.class;
        }
        else
        {
            aggregationClass = MatmDiffAggregator.class;
        }
    }

    public void init()
    {
        for( int x = 0; x < processingThreadCount; x++ )
        {
            try
            {
                MatmAggregator<T> aggregator = (MatmAggregator<T>)aggregationClass.newInstance();
                aggregator.setFlightPostUpdater( flightPostUpdater );
                aggregator.setFlightPreUpdater( flightPreUpdater );
                aggregator.setFuserStore( fuserStore );
                aggregator.setMetaDataManager( metaDataManager );

                aggregators.add( aggregator );
            }
            catch( InstantiationException | IllegalAccessException e )
            {
                logger.error( "Failed to initialize the MatmAggregators", e );
            }
        }
        
        for( int i = 0; i < processingThreadCount; i++ )
        {
            AggregatorHandler handler = new AggregatorHandler( aggregators.get( i ) );
            
            BlockingQueueProcessor<T> blockingQueueProcessor =
                    new BlockingQueueProcessor<>( handler, blockingQueueCapacity );
            blockingQueueProcessor.setName( aggregationClass.getSimpleName() + "_PROC_[" + i + "]" );
            blockingQueueProcessor.start();
            blockingQueueProcessors.add( blockingQueueProcessor );
        }
        
        if( reportingTimer != null )
        {
            reportingTimer.cancel();
        }
        
        reportingTimer = new Timer( "AggregatorTaskReportTimer" );
        reportingTimer.scheduleAtFixedRate( new ExecutorQueueMonitorTask(), 0, reportingTimeMillis );
    }
    
    @Override
    public T aggregate( T update )
    {
        if( update == null )
        {
            return null;
        }
        
        int threadIndex = getThreadIndex( update );

        // Create a task and submit it to the correct BlockingQueueProcessor
        BlockingQueueProcessor<T> bqp = blockingQueueProcessors.get( threadIndex );
        
        try
        {
            bqp.process( update );
        }
        //Catch everything to prevent crazy unhandled exceptions
        catch( Exception ex )
        {
            logger.error( "Error aggregating update!", ex );
        }
        
        return null;
    }

    private int getThreadIndex( T update )
    {
        if( processingThreadCount == 1 )
        {
            return 0;
        }
        
        // Get update key's hashCode, based on the hash value select a thread
        String key = fuserStore.getKey( update );
        int threadIndex = 0;
        if( key != null )
        {
            threadIndex = Math.abs( key.hashCode() % processingThreadCount );
        }

        if( threadIndex >= blockingQueueProcessors.size() )
        {
            logger.warn( "threadIndex is too large. Using default index of 0" );
            threadIndex = 0;
        }
        
        return threadIndex;
    }

    @Override
    public void setMessageProducer( MessageProducer messageProducer )
    {
        this.messageProducer = messageProducer;
    }
    
    public void setDestinationQueue( String destinationQueue )
    {
        this.destinationQueue = destinationQueue;
    }

    public void setReportingTimeMillis( int reportingTimeMillis )
    {
        this.reportingTimeMillis = reportingTimeMillis;
    }


    public void setBlockingQueueCapacity( int blockingQueueCapacity )
    {
        this.blockingQueueCapacity = blockingQueueCapacity;
    }
    
    public void setFuserStore( FuserStore<T, MetaData> store )
    {
        this.fuserStore = store;
    }

    
    public void setMetaDataManager( MetaDataManager<T> metaDataManager )
    {
        this.metaDataManager = metaDataManager;
    }

    public void setFlightPreUpdater( Updater<T, T> flightPreUpdater )
    {
        this.flightPreUpdater = flightPreUpdater;
    }

    public void setFlightPostUpdater( Updater<T, T> flightPostUpdater )
    {
        this.flightPostUpdater = flightPostUpdater;
    }


    private class AggregatorHandler implements Handler<T>
    {
        private MatmAggregator<T> aggregator;

        public AggregatorHandler( MatmAggregator<T> aggregator )
        {
            this.aggregator = aggregator;
        }
        
        @Override
        public void handle( T update )
        {
            if( messageProducer == null || aggregator == null ||
                    destinationQueue == null )
            {
                logger.error( "The Aggregator has not been setup correctly." );
                return;
            }

            MatmObject result = aggregator.aggregate( update );

            if( result != null )
            {
                // Send to seda queue
                messageProducer.publish( destinationQueue, result );
            }
        }

        @Override
        public void onShutdown()
        {
            
        }
    }
    
    private class ExecutorQueueMonitorTask extends TimerTask
    {
        @Override
        public void run()
        {
            int x = 0;
            for( BlockingQueueProcessor<T> bqp : blockingQueueProcessors )
            {
                int queueSize = bqp.size();
                logger.info( "Reporting AggregateProcessThreadQueue #" + x + " queueSize: " + queueSize );
                x++;
            }
        }
    }
}
