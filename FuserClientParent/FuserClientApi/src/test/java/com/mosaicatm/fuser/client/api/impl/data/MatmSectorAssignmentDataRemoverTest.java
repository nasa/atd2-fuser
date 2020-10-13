package com.mosaicatm.fuser.client.api.impl.data;

import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.mosaicatm.fuser.client.api.data.DataRemover;
import com.mosaicatm.fuser.client.api.data.FuserClientStore;
import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;
import com.mosaicatm.lib.time.clock.DirectClock;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class MatmSectorAssignmentDataRemoverTest 
{
	private FuserClientStore<MatmSectorAssignment> store;
	private DataRemover<MatmSectorAssignment> remover;
	private DirectClock clock;
    private SimpleDateFormat dateFormat;
	
	@Before
	public void setup () throws ParseException
	{
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
		store = new FuserLockingProxyStore<>(new FuserClientMatmSectorAssignmentStore ());
        
		store.add(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:00" ), "ZOB40" ));
		store.add(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:00" ), "ZOB41" ));
		store.add(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:00" ), "ZOB42" ));
		store.add(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:00" ), "ZOB43" ));
		store.add(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:00" ), "ZOB44" ));        
        store.add(createSectorAssignment(dateFormat.parse( "1980-08-29 00:00:00" ), "ZNY44" ));        
        
		clock = new DirectClock();
        clock.setTimeInMillis(dateFormat.parse( "2020-08-29 12:00:00" ).getTime() );
        
        remover = new MatmSectorAssignmentDataRemover(store, clock);       
	}
	
	@Test
	public void testExpiredRemover() throws ParseException
	{        
        MockEventHandler handler = new MockEventHandler ();
        
		remover.setReceivedEventListener(handler);
		remover.setProcessedEventListener(handler);        
        
		assertEquals(6, store.size());
		
		assertEquals(0, handler.getNotifyEvents().size());
		assertEquals(0, handler.getStoreEvents().size());
        
        remover.remove();
        
		assertEquals(6, store.size());
		
		assertEquals(0, handler.getNotifyEvents().size());
		assertEquals(0, handler.getStoreEvents().size());        
        
        clock.setTimeInMillis(dateFormat.parse( "2020-08-29 12:00:55" ).getTime() );
        
		store.update(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:55" ), "ZOB43" ));
		store.update(createSectorAssignment(dateFormat.parse( "2020-08-29 12:00:55" ), "ZOB44" ));        
        
        remover.remove();
        
		assertEquals (6, store.size());
        
		assertEquals(0, handler.getNotifyEvents().size());
		assertEquals(0, handler.getStoreEvents().size());   
        
        clock.setTimeInMillis(dateFormat.parse( "2020-08-29 12:06:00" ).getTime() );
        
		store.update(createSectorAssignment(dateFormat.parse( "2020-08-29 12:06:00" ), "ZOB43" ));
		store.update(createSectorAssignment(dateFormat.parse( "2020-08-29 12:06:00" ), "ZOB44" ));        
        
        remover.remove();
        
		assertEquals (6, store.size());
        
		assertEquals(0, handler.getNotifyEvents().size());
		assertEquals(0, handler.getStoreEvents().size()); 

        clock.setTimeInMillis(dateFormat.parse( "2020-08-29 12:07:00" ).getTime() );
        
        remover.remove();
        
		assertEquals (3, store.size());
        
		assertEquals(3, handler.getNotifyEvents().size());
		assertEquals(3, handler.getStoreEvents().size());             
	}
	
	@Test
	public void testExpirationReceiveEvents () throws ParseException
	{        
		MockEventHandler handler = new MockEventHandler ();

		remover.setReceivedEventListener(handler);
		remover.setProcessedEventListener(handler);
        
        clock.setTimeInMillis(dateFormat.parse( "2020-08-29 12:00:00" ).getTime() );

		assertEquals (6, store.size());
        
		for (MatmSectorAssignment sector : store.getAll())
		{
		    remover.remove(sector);
		}
		
		assertEquals (0, store.size());
		
		assertEquals (6, handler.getNotifyEvents().size());
		assertEquals (6, handler.getStoreEvents().size());
	}
	
	private MatmSectorAssignment createSectorAssignment (Date timestamp, String sectorName)
	{
	    MatmSectorAssignment sector = new MatmSectorAssignment();
		sector.setTimestamp(timestamp);
                
        sector.setSourceFacility(sectorName.substring(0, 3));
        sector.setSectorName(sectorName);
		
		return sector;
	}
	
	private class MockEventHandler
	implements FuserProcessedEventListener<MatmSectorAssignment>, 
	           FuserReceivedEventListener<MatmSectorAssignment>
	{
		private List<MatmSectorAssignment> storeEvents = new ArrayList<> ();
		private List<MatmSectorAssignment> notifyEvents = new ArrayList<> ();
		
		@Override
		public void receivedAdd(MatmSectorAssignment data)
		{
			// do nothing
		}

		@Override
		public void receivedUpdate(MatmSectorAssignment existingBeforeUpdating, MatmSectorAssignment data)
		{
			// do nothing
		}

		@Override
		public void receivedRemove(MatmSectorAssignment data)
		{
			notifyEvents.add(data);
		}

		@Override
		public void dataAdded(MatmSectorAssignment data)
		{
			// do nothing
		}

		@Override
		public void dataUpdated(MatmSectorAssignment data, MatmSectorAssignment update)
		{
			// do nothing
		}

		@Override
		public void dataRemoved(MatmSectorAssignment data)
		{
			storeEvents.add(data);
		}
		
		public List<MatmSectorAssignment> getStoreEvents ()
		{
			return storeEvents;
		}
		
		public List<MatmSectorAssignment> getNotifyEvents ()
		{
			return notifyEvents;
		}
	}
}
