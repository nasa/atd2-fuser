package com.mosaicatm.fuser.aggregator;

import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jvnet.jaxb2_commons.lang.MergeFrom;

import com.mosaicatm.fuser.common.matm.util.MatmCloner;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.updaters.Updater;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.common.MetaData;

public abstract class MatmAggregator <T extends MatmObject> implements Aggregator<T>
{
    
    private final Log log = LogFactory.getLog(getClass());

    protected FuserStore<T,MetaData> store;
    
    protected MetaDataManager<T> metaDataManager;
    
    protected Updater<T, T> flightPreUpdater; //actually a factory of updaters

    protected Updater<T, T> flightPostUpdater; //actually a factory of updaters
    
    private MatmObjectDiff diff;
    private MatmCloner cloner;


    public MatmAggregator ()
    {
        diff = new MatmObjectDiff();
        cloner = new MatmCloner();
    }
    
    public abstract void copyComponentFromRightToLeft(T left, T right);
    public abstract  void excludeCommonFields( Set<String> fields );
    public abstract T createComponent();
    
    public T aggregate(T update)
    {
        boolean newFlight = false;    
        T target = getTarget(update);

        // Run the pre-updaters. These are run prior to the mediation rules. Any updates made here that survive the 
        // mediation rules will be applied to the existing flight (target)
        runUpdaters(update, target, flightPreUpdater);        
        
        if (target == null)
        {
            newFlight = true;
            target = addNewTarget (update);
        }

        // The aggregateTarget step does the following:
        // 1. Applies mediation logic (the Fuser "rules")
        // 2. Applies the post-updaters
        // 3. Updates the existing (target) flight
        T diff = aggregateTarget (update, target, newFlight);

        if(newFlight)
        {
            return update;
        }
        else
        {
            return diff;
        }
    }

    private T getTarget(T update)
    {
        T target = null;
        
        if (update != null)
        {
            String key = store.getKey(update);
            
            try
            {
                store.lockStore( update );
                target = store.get(key);
            }
            catch (Exception e)
            {
                log.error ("Error retrieving target flight for " + key, e);
            }
            finally
            {
                store.unlockStore( update );
            }
        }
        
        return target;
    }

    public void setFuserStore (FuserStore<T, MetaData> store)
    {
        this.store = store;
    }
    
    private void runUpdaters(T update, T target, Updater<T, T> updater)
    {
        if (updater != null) {
            updater.update(update, target);
        }
    }
    
    /**
     * Overrides the default aggregateTarget method to perform an actual diff
     * before merging updates. Only the diffs will be in the final update
     */
    public T aggregateTarget( T update, T target, boolean newFlight )
    {
        if( update != null && target != null )
        {                      
            AggregationResult result = null;
            try
            {
                T copy = createComponent();
                
                if( newFlight )
                {                 
                    metaDataManager.createRecord( update );                   
                    return mergeTarget(update, target, newFlight);
                }
                else
                {
                    //temporarily copy over
                    copyComponentFromRightToLeft(copy, update);
                    
                    //generate a set of differences and null out existing/identical data
                    result = diff.compareObjects(update, target);
                    
                    //copy back
                    copyComponentFromRightToLeft(update, copy);
                }
            }
            catch( Exception e )
            {
                log.error( "Fail to compare objects in MatmDiffAggregator", e );
            }
            
            if( result != null &&  result.getChanges() != null
                && !result.getChanges().isEmpty())
            {
                
                excludeCommonFields( result.getChanges() );
                excludeCommonFields( result.getIdenticals() );

                if( log.isDebugEnabled() )
                {
                    log.debug( "Diffs found for " + store.getKey(target) );
                }
                
                int total = result.getChanges().size();
                //handle those changes
                int totalApplied = metaDataManager.applyRules( update, target, result );
                
                //if all changes applied, no need to return
                if (total == totalApplied)
                    return ( null );
                
                // set changes to the update
                update.setChanges(new ArrayList<>(result.getChanges()));
                
                //merge changes
                return mergeTarget( update, target, newFlight );
            }
            else
            {
                return ( null );
            }
        }
        
        return update;
    }
    
    
    public T mergeTarget (T update, T target, boolean newFlight)
    {
        if (update != null && target != null)
        {    
            // The post-updaters run after mediation, but before the target is updated
            runUpdaters(update, target, flightPostUpdater);               
            
            //now that all the updaters have run merge the results
            T targ = target;
            T left = update;
            T right = target;
            
            if (targ instanceof MergeFrom)
            {
                ((MergeFrom)targ).mergeFrom(left, right);
            }
            else
            {
                log.error("Merging failed, target " + targ.getClass().getName() + 
                    " is not of type MergeFrom");
            }
            
            try
            {
                store.lockStore( target );
                store.update(target);
            }
            catch (Exception e)
            {
                log.error ("Error updating target", e);
            }
            finally
            {
                store.unlockStore( target );
            }            
            
        }
        return update;
    }
    
    /**
     * Create an entry for the FuserStore (the "target"). This method clones the update, so
     * we are not merging into the update object later on. Returns the clone.
     * @param update    The first MatmObject update for this flight that will be used as the
     *      starting point in the FuserStore to maintain the current state of the flight.
     * @return  The clone of the update parameter, which is the FuserStore instance.
     */
    private T addNewTarget (T update)
    {
        T target = null;
        if (update != null)
        {
            try
            {
                target = cloner.clone( update );
                store.lockStore( target );
                store.add( target );
            }
            catch (Exception e)
            {
                log.error ("Error adding target", e);
            }
            finally
            {
                store.unlockStore( target );
            }
        }

        return target;
    }

    /**
     * Set updater that should run prior to mediation rules and aggregation
     * @param flightUpdater
     */
    public void setFlightPreUpdater(Updater<T, T> flightUpdater)
    {
        this.flightPreUpdater = flightUpdater;
    }

    /**
     * Set updater that should run post mediation rules and aggregation 
     * @param flightUpdater
     */
    public void setFlightPostUpdater(Updater<T, T> flightUpdater)
    {
        this.flightPostUpdater = flightUpdater;
    }

    public MetaDataManager<T> getMetaDataManager()
    {
        return metaDataManager;
    }

    public void setMetaDataManager(MetaDataManager<T> metaDataManager)
    {
        this.metaDataManager = metaDataManager;
    }
}
