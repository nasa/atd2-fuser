package com.mosaicatm.fuser.store.matm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.store.GenericRedisFuserStore;
import com.mosaicatm.fuser.store.SectorAssignmentDataStore;
import com.mosaicatm.fuser.store.redis.RedisHash;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class RedisSectorAssignmentFuserStore
extends GenericRedisFuserStore<MatmSectorAssignment> implements SectorAssignmentDataStore
{
     private final Logger log = LoggerFactory.getLogger(getClass());
    
    private GenericMarshaller sectorAssignmentMarshaller;
    
    public RedisSectorAssignmentFuserStore( int numberOfLocks )
    {
        this(null, numberOfLocks);
    }
    
    public RedisSectorAssignmentFuserStore(RedisHash redisHash, int numberOfLocks)
    {
        super("Redis Sector Assignment Fuser Store", redisHash, numberOfLocks);
    }

    @Override
    public boolean dynamicSectorAssignmentsExist( String artcc )
    {
        throw new UnsupportedOperationException("Unsupported operation: dynamicSectorAssignmentsExist");
    }

    @Override
    public String getDynamicSectorForModule( String moduleName )
    {
        throw new UnsupportedOperationException("Unsupported operation: dynamicSectorAssignmentsExist");
    }
    
    @Override
    public void updateDynamicSectorFixedAirspaceVolumes( MatmSectorAssignment sectorAssignment )
    {
        if(( sectorAssignment == null ) || ( sectorAssignment.getSectorName() == null ))
        {
            return;            
        }
                          
        if(( sectorAssignment.getFixedAirspaceVolumeList() == null ) || 
                sectorAssignment.getFixedAirspaceVolumeList().isEmpty() )
        {
            remove( sectorAssignment );
        }
        else
        {
            update( sectorAssignment );
        }
    }    
    
    @Override
    public void initialize()
    {
        // nothing to initialize
    }
    
    @Override
    public String toXML(MatmSectorAssignment sectorAssignment)
    {
        String xml = null;
        
        if (sectorAssignment != null)
        {
            try
            {
                xml = sectorAssignmentMarshaller.marshall(sectorAssignment);
            }
            catch (Exception e)
            {
                log.error("MatmSectorAssignment to XML error", e);
            }
        }
        
        return xml;
    }
    
    @Override
    public MatmSectorAssignment fromXML(String xml)
    {
        MatmSectorAssignment sectorAssignment = null;
        
        try
        {
            sectorAssignment = sectorAssignmentMarshaller.unmarshallToObject(xml);
        }
        catch (Exception e)
        {
            log.error("XML to MatmSectorAssignment error", e);
        }
        
        return sectorAssignment;
    }
    
    public void setMatmSectorAssignmentMarshaller(GenericMarshaller sectorAssignmentMarshaller)
    {
        this.sectorAssignmentMarshaller = sectorAssignmentMarshaller;
    }
}
