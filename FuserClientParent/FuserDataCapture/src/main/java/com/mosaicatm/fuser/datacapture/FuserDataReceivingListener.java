package com.mosaicatm.fuser.datacapture;

import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.common.matm.util.MatmCloner;
import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.fuser.datacapture.store.IdSet;
import com.mosaicatm.fuser.datacapture.store.IdStore;
import com.mosaicatm.lib.messaging.Receiver;
import com.mosaicatm.lib.util.filter.Filter;
import com.mosaicatm.matmdata.common.MatmObject;

public class FuserDataReceivingListener<T extends MatmObject>
implements FuserProcessedEventListener<T>
{
    private final Log log = LogFactory.getLog(getClass());

    private Filter<T> filter;

    private Receiver<DataWrapper<T>> fullDataPostUpdateReceiver;
    private Receiver<DataWrapper<T>> updateDataReceiver;
    private Receiver<DataWrapper<T>> dataRemovalReceiver;

    private CaptureEvaluator<T> captureEvaluator;
    private MatmIdLookup<T, String> matmIdLookup;

    private IdStore idStore;
    private MatmCloner cloner;

    public FuserDataReceivingListener()
    {
        this.cloner = new MatmCloner();
        this.cloner.setActive(true);
    }

    @Override
    public void dataAdded(T data)
    {
        if (data != null && idStore != null && captureEvaluator != null)
        {
            data = cloner.clone(data);

            List<CaptureType> types = captureEvaluator.getCaptureTypes( data );
            IdSet idSet = getIdSet(data, types);

            DataWrapper<T> wrapper = new DataWrapper<>( data, idSet );
            wrapper.addCaptureTypes( types );

            handleUpdateDataReceiver( wrapper );
            handleFullDataPostUpdateReceiver( wrapper );
        }
    }

    @Override
    public void dataUpdated(T afterUpdating, T update)
    {
        if (update != null && idStore != null && captureEvaluator != null)
        {
            update = cloner.clone(update);

            List<CaptureType> types = captureEvaluator.getCaptureTypes( update );
            IdSet idSet = getIdSet(update, types);

            DataWrapper<T> wrapper = new DataWrapper<>( update, idSet );
            wrapper.addCaptureTypes( types );
            boolean updated = handleUpdateDataReceiver( wrapper );

            if (afterUpdating != null && updated)
            {
                afterUpdating = cloner.clone(afterUpdating);

                DataWrapper<T> allWrapper = new DataWrapper<>( afterUpdating, idSet );
                allWrapper.addCaptureTypes( types );
                handleFullDataPostUpdateReceiver( allWrapper );
            }
        }
    }
    
    private IdSet getIdSet(T data, List<CaptureType> captureTypes)
    {
        IdSet idSet = null;
        
        if (data != null && matmIdLookup != null)
        {
            String id = matmIdLookup.getIdentifier(data);
            
            if (id != null && !id.trim().isEmpty())
            {
                if (idStore != null)
                {
                    idSet = idStore.generateRecord(id, captureTypes);   
                }
            }
            else
            {
                log.warn("Unable to lookup data identifier");
            }
        }
        
        return idSet;
    }

    @Override
    public void dataRemoved(T data)
    {
        if (data != null && idStore != null)
        {
            data = cloner.clone(data);

            DataWrapper<T> wrapper = new DataWrapper<>( data );
            wrapper.addCaptureType( CaptureType.MATM_FLIGHT_REMOVED );
            handleDataRemovalReceiver( wrapper );
            idStore.remove(matmIdLookup.getIdentifier(data));
        }
    }

    public void setFullDataPostUpdateReceiver (Receiver<DataWrapper<T>> fullDataPostUpdateReceiver)
    {
        this.fullDataPostUpdateReceiver = fullDataPostUpdateReceiver;
    }    

    public void setUpdateDataReceiver (Receiver<DataWrapper<T>> updateDataReceiver)
    {
        this.updateDataReceiver = updateDataReceiver;
    }    

    public void setDataRemovalReceiver( Receiver<DataWrapper<T>> dataRemovalReceiver )
    {
        this.dataRemovalReceiver = dataRemovalReceiver;
    }

    public void setFilter(Filter<T> filter)
    {
        this.filter = filter;
    }

    private boolean handleData(DataWrapper<T> wrapper, Consumer<DataWrapper<T>> func)
    {
        boolean handled = false;
        if(( wrapper != null ))
        {
            if (canProcess(wrapper))
            {
                func.accept(wrapper);
                handled = true;
            }
        }
        return handled;
    }

    private boolean handleUpdateDataReceiver (DataWrapper<T> wrapper)
    {        
        boolean updated = false;
        if(( updateDataReceiver != null ))
            updated = handleData(wrapper, (w)->{updateDataReceiver.receive(w);});
        return updated;
    }

    private void handleFullDataPostUpdateReceiver (DataWrapper<T> wrapper)
    {        
        if(( fullDataPostUpdateReceiver != null ))
            handleData(wrapper, (w)->{fullDataPostUpdateReceiver.receive(w);});
    }      

    private void handleDataRemovalReceiver( DataWrapper<T> wrapper )
    {
        if( dataRemovalReceiver != null )
            handleData(wrapper, (w)->{dataRemovalReceiver.receive(w);});
    }

    private boolean canProcess(DataWrapper<T> wrapper)
    {
        if (filter == null || wrapper == null)
            return true;

        return filter.doFilter(wrapper.getData());
    }

    public void setIdStore(IdStore idStore)
    {
        this.idStore = idStore;
    }

    public void setCaptureEvaluator(CaptureEvaluator<T> captureEvaluator)
    {
        this.captureEvaluator = captureEvaluator;
    }

    public void setMatmIdLookup(MatmIdLookup<T, String> matmIdLookup)
    {
        this.matmIdLookup = matmIdLookup;
    }
}
