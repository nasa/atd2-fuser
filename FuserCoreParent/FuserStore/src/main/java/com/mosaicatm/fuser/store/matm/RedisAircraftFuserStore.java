package com.mosaicatm.fuser.store.matm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.store.GenericRedisFuserStore;
import com.mosaicatm.fuser.store.redis.RedisHash;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class RedisAircraftFuserStore
extends GenericRedisFuserStore<MatmAircraft>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private GenericMarshaller aircraftMarshaller;
    
    public RedisAircraftFuserStore( int numberOfLocks )
    {
        this(null, numberOfLocks);
    }
    
    public RedisAircraftFuserStore(RedisHash redisHash, int numberOfLocks)
    {
        super("Redis Aircraft Fuser Store", redisHash, numberOfLocks);
    }
    
    @Override
    public void initialize()
    {
        // nothing to initialize
    }
    
    @Override
    public String toXML(MatmAircraft aircraft)
    {
        String xml = null;
        
        if (aircraft != null)
        {
            try
            {
                xml = aircraftMarshaller.marshall(aircraft);
            }
            catch (Exception e)
            {
                log.error("MatmAircraft to XML error", e);
            }
        }
        
        return xml;
    }
    
    @Override
    public MatmAircraft fromXML(String xml)
    {
        MatmAircraft aircraft = null;
        
        try
        {
            aircraft = aircraftMarshaller.unmarshallToObject(xml);
        }
        catch (Exception e)
        {
            log.error("XML to MatmAircraft error", e);
        }
        
        return aircraft;
    }
    
    public void setMatmAircraftMarshaller(GenericMarshaller aircraftMarshaller)
    {
        this.aircraftMarshaller = aircraftMarshaller;
    }
}
