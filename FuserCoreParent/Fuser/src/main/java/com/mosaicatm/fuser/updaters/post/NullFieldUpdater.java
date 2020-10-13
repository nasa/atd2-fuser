package com.mosaicatm.fuser.updaters.post;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class NullFieldUpdater extends AbstractUpdater<MatmFlight, MatmFlight>
{
    private static final Logger logger = LoggerFactory.getLogger( NullFieldUpdater.class );

    List<String> fieldsToNull;

    @Override
    public void update( MatmFlight update, MatmFlight target )
    {
        if( !isActive() || update == null )
        {
            return;
        }
        
        nullOutFields( update );

    }
    
    public void nullOutFields( MatmFlight flight )
    {
        if( fieldsToNull != null )
        {
            for( String value : fieldsToNull )
            {
                try
                {
                    PropertyUtils.setProperty(flight, value, null);
                }
                catch (Exception e)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug( "Unable to access " + value, e );
                    }
                }
            } 
        }
    }

    public void setFieldsToNullFromString( String fieldsToNull )
    {
        if( fieldsToNull == null || fieldsToNull.trim().isEmpty() )
        {
            this.fieldsToNull = null;
            return;
        }
        
        this.fieldsToNull = new ArrayList<>();
        
        String[] fields = fieldsToNull.split( "," );
        for( String field : fields )
        {
            String trimmedField = field.trim();
            if( !trimmedField.isEmpty() )
            {
                this.fieldsToNull.add( trimmedField );
            }
        }
    }
}
