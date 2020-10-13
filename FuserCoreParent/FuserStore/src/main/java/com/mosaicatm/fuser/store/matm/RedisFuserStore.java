package com.mosaicatm.fuser.store.matm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.store.GenericRedisFuserStore;
import com.mosaicatm.fuser.store.redis.RedisHash;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class RedisFuserStore 
extends GenericRedisFuserStore<MatmFlight>
{

    private final Log log = LogFactory.getLog(getClass());
    
    private GenericMarshaller flightMarshaller;
    
    public RedisFuserStore( int numberOfLocks )
    {
        this(null, numberOfLocks);
    }
    
    public RedisFuserStore (RedisHash redisHash, int numberOfLocks)
    {
        super ("Redis Flight Fuser Store", redisHash, numberOfLocks);
    }
    
    @Override
    public void initialize ()
    {
        // nothing to initialize
    }
    
    @Override
    public String toXML(MatmFlight flight)
    {
        String xml = null;
        
        if (flight != null)
        {
            try 
            {
                xml = flightMarshaller.marshall(flight);
            } 
            catch (Exception e) 
            {
                log.error("MatmFlight to XML error", e);
            }
        }
        
        return xml;
    }
  
    @Override
    public MatmFlight fromXML(String xml)
    {
        MatmFlight flight = null;
        
        try 
        {
            flight = flightMarshaller.unmarshallToObject(xml);
        } 
        catch (Exception e) 
        {
            log.error("XML to MatmFlight error", e);
        }
        
        return flight;
    }

    public void setMatmFlightMarshaller(GenericMarshaller flightMarshaller)
    {
        this.flightMarshaller = flightMarshaller;
    }
}
