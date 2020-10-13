package com.mosaicatm.fuser.store.matm;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.sector.MatmSectorAssignmentIdLookup;
import com.mosaicatm.fuser.store.FuserStoreProxy;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;
import com.mosaicatm.lib.time.clock.DirectClock;

public class MatmSectorAssignmentFuserStoreTimedRemoverTest
{
    private DirectClock clock;
    private FuserStoreProxy<MatmSectorAssignment, MetaData> proxy;
    private MatmSectorAssignmentFuserStoreTimedRemover remover;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Before
    public void setup() throws ParseException
    {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        clock = new DirectClock();
        
        MatmSectorAssignmentFuserStore store = new MatmSectorAssignmentFuserStore(1);
        store.setIdLookup(new MatmSectorAssignmentIdLookup());
        proxy = new FuserStoreProxy<>(store);
        
        remover = new MatmSectorAssignmentFuserStoreTimedRemover(proxy);
        remover.setActive(true);
        remover.setExpirationTimeMillis(TimeFactory.MINUTE_IN_MILLIS * 5);
        remover.setClock(clock);        
    }
    
    @Test
    public void testMatmSectorAssignmentFuserStoreTimedRemover() throws ParseException
    {        
        clock.setTimeInMillis(dateFormat.parse( "2020-08-29 12:00:00" ).getTime() );
        
		proxy.add(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:00" ), "ZOB40" ));
		proxy.add(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:00" ), "ZOB41" ));
		proxy.add(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:00" ), "ZOB42" ));
		proxy.add(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:00" ), "ZOB43" ));
		proxy.add(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:00" ), "ZOB44" ));        
        proxy.add(createSectorAssignment(dateFormat.parse( "1980-08-29 00:00:00" ), "ZNY44" ));           
        
		assertEquals(6, proxy.size());
        assertNotNull(proxy.get("ZOB40"));
        assertNotNull(proxy.get("ZOB41"));        
        assertNotNull(proxy.get("ZOB42"));        
        assertNotNull(proxy.get("ZOB43"));        
        assertNotNull(proxy.get("ZOB44"));        
        assertNotNull(proxy.get("ZNY44"));                
        
        remover.remove();
        
		assertEquals(6, proxy.size());
		
        clock.setTimeInMillis(dateFormat.parse( "2020-08-29 12:00:55" ).getTime() );
        
		proxy.update(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:55" ), "ZOB43" ));
		proxy.update(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:55" ), "ZOB44" ));        
        
        remover.remove();
        
		assertEquals (6, proxy.size());
        
        clock.setTimeInMillis(dateFormat.parse( "2020-08-29 12:06:00" ).getTime() );
        
		proxy.update(createSectorAssignment(dateFormat.parse( "2020-08-29 12:06:00" ), "ZOB43" ));
		proxy.update(createSectorAssignment(dateFormat.parse( "2020-08-29 12:06:00" ), "ZOB44" ));        
        
        remover.remove();
        
		assertEquals (6, proxy.size());

        clock.setTimeInMillis(dateFormat.parse( "2020-08-29 12:07:00" ).getTime() );
        
        remover.remove();
        
		assertEquals (3, proxy.size());
        
        assertNotNull(proxy.get("ZOB43"));        
        assertNotNull(proxy.get("ZOB44"));        
        assertNotNull(proxy.get("ZNY44"));           
    }
    
    @Test
    public void testMatmSectorAssignmentFuserStoreTimedRemoverThread() throws ParseException
    {
        clock.setTimeInMillis(dateFormat.parse( "2020-08-29 15:00:00" ).getTime() );
        
		proxy.add(createSectorAssignment(dateFormat.parse( "2020-08-29 15:00:00" ), "ZOB40" ));
		proxy.add(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:00" ), "ZOB41" ));
        
        assertEquals(2, proxy.size());
        assertNotNull(proxy.get("ZOB40"));
        assertNotNull(proxy.get("ZOB41"));
        
        remover.setCheckIntervalMillis(500L);
        remover.start();
        
        try
        {
            Thread.sleep(2000L);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        
        assertEquals(1, proxy.size());
        assertNull(proxy.get("ZOB41"));
    }
    
	private MatmSectorAssignment createSectorAssignment (Date timestamp, String sectorName)
	{
	    MatmSectorAssignment sector = new MatmSectorAssignment();
		sector.setTimestamp(timestamp);
                
        sector.setSourceFacility(sectorName.substring(0, 3));
        sector.setSectorName(sectorName);
        sector.getFixedAirspaceVolumeList().add( sectorName + "00M1" );
		
		return sector;
	}    
}
