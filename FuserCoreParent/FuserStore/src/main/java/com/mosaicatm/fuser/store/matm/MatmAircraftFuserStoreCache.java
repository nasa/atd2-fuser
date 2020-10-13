package com.mosaicatm.fuser.store.matm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.common.MetaData;

/**
 * The <code>MatmAircraftFuserStoreCache</code> provides an in-memory cache in front
 * of a FuserStore delegate (acts like a proxy).  The goal is to reduce remote
 * calls to get information.
 */
public class MatmAircraftFuserStoreCache 
extends AbstractFuserStoreTimedBackup <MatmAircraft>
{
    private final Log logger = LogFactory.getLog(getClass());

    public MatmAircraftFuserStoreCache(FuserStore<MatmAircraft, MetaData> delegate,
                                       FuserStore<MatmAircraft, MetaData> cache)
    {        
        super(delegate, cache);
        ((MatmAircraftFuserStore)getCache()).setSendEvents(true);
    }

    @Override
    public void initialize()
    {
        try
        {
            logger.info( "Initializing MatmAircraftFuserStoreCache..." );
            
            lockEntireStore();
            
            getCache().clear();
            
            int metaCount = 0;
            List<MetaData> invalid = null;
            Collection<MetaData> metaDataCollection = null;
            for( MatmAircraft aircraft : getDelegate().getAll() )
            {
                // clears out
                if (aircraft.getUpdateSources() != null && !aircraft.getUpdateSources().isEmpty())
                {
                    invalid = new ArrayList<>();
                    for (MetaData updateSource : aircraft.getUpdateSources())
                    {
                        if (updateSource == null ||
                            (updateSource.getFieldName() == null &&
                             updateSource.getSource() == null &&
                             updateSource.getSystemType() == null &&
                             updateSource.getTimestamp() == null))
                        {
                            logger.warn(aircraft.getRegistration() + " tried restoring a nill update source, " + 
                                        "removing nil from list");
                            invalid.add(updateSource);
                        }
                    }
                    aircraft.getUpdateSources().removeAll(invalid);
                }
                
                getCache().add( aircraft );
                
                metaDataCollection = getDelegate().getAllMetaData( aircraft );
                if( metaDataCollection != null )
                {
                	getCache().updateMetaData(aircraft, metaDataCollection);
                	metaCount += metaDataCollection.size();
                }
            }
            
            logger.info( "  Aircraft: " +  size() );
            logger.info( "  Aircraft MetaData: " +  metaCount );
        }
        finally
        {
            unlockEntireStore();
            logger.info( "... MatmAircraftFuserStoreCache Initialization Complete!" );
        }          
    }    

    @Override
    public String getKey(MatmAircraft aircraft)
    {
        if (getDelegate() != null)
            return getDelegate().getKey(aircraft);
        return null;
    }
}
