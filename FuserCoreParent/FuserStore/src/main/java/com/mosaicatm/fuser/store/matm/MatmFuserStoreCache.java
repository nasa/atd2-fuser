package com.mosaicatm.fuser.store.matm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

/**
 * The <code>MatmFuserStoreCache</code> provides an in-memory cache in front
 * of a FuserStore delegate (acts like a proxy).  The goal is to reduce remote
 * calls to get information.
 * 
 * @author mgarland
 */
public class MatmFuserStoreCache 
extends AbstractFuserStoreTimedBackup <MatmFlight>
{
    private final Log logger = LogFactory.getLog(getClass());

    public MatmFuserStoreCache(FuserStore<MatmFlight, MetaData> delegate,
                               FuserStore<MatmFlight, MetaData> cache)
    {        
        super(delegate, cache);
        ((MatmFuserStore)getCache()).setSendEvents(true);
    }

    @Override
    public void initialize()
    {
        try
        {
            logger.info( "Initializing MatmFuserStoreCache..." );
            
            lockEntireStore();
            
            getCache().clear();
            
            int metaCount = 0;
            List<MetaData> invalid = null;
            for( MatmFlight flight : getDelegate().getAll() )
            {
                // clears out
                if (flight.getUpdateSources() != null && !flight.getUpdateSources().isEmpty())
                {
                    invalid = new ArrayList<>();
                    for (MetaData updateSource : flight.getUpdateSources())
                    {
                        if (updateSource == null ||
                            (updateSource.getFieldName() == null &&
                             updateSource.getSource() == null &&
                             updateSource.getSystemType() == null &&
                             updateSource.getTimestamp() == null))
                        {
                            logger.warn(flight.getGufi() + " tried restoring a nill update source, " + 
                                        "removing nil from list");
                            invalid.add(updateSource);
                        }
                    }
                    flight.getUpdateSources().removeAll(invalid);
                }
                
                getCache().add( flight );
                
                Collection<MetaData> metaDataCollection = getDelegate().getAllMetaData( flight );
                if( metaDataCollection != null )
                {
                	getCache().updateMetaData(flight, metaDataCollection);
                	metaCount += metaDataCollection.size();
                }
            }
            
            logger.info( "  Flights: " +  size() );
            logger.info( "  Flight MetaData: " +  metaCount );
        }
        finally
        {
            unlockEntireStore();
            logger.info( "... MatmFuserStoreCache Initialization Complete!" );
        }          
    }    

    @Override
    public String getKey(MatmFlight flight)
    {
        if (getDelegate() != null)
            return getDelegate().getKey(flight);
        return null;
    }
}
