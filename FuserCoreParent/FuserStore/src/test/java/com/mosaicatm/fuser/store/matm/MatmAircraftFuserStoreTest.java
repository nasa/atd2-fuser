package com.mosaicatm.fuser.store.matm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.aircraft.MatmAircraftIdLookup;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.event.FuserStoreEvent;
import com.mosaicatm.fuser.store.event.FuserStoreListener;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.common.MetaData;

public class MatmAircraftFuserStoreTest
{
    private MatmAircraftFuserStore store;

    @Before
    public void setup()
    {
        int numberOfLocks = 1;
        store = new MatmAircraftFuserStore( numberOfLocks );
        store.setIdLookup(new MatmAircraftIdLookup());
    }

    @Test
    public void testMatmAircraftFuserStore ()
    {		
        MatmAircraft aircraft = createAircraft(UUID.randomUUID().toString(), "A737");

        store.add(aircraft);

        assertEquals (1, store.size());

        MatmAircraft copy = store.get(aircraft.getRegistration());

        assertNotNull (copy);
        assertEquals (aircraft.getRegistration(), copy.getRegistration());
        assertEquals (aircraft.getType(), copy.getType());

        store.remove(aircraft);

        assertEquals (0, store.size());

        MatmAircraft empty = store.get(aircraft.getRegistration());

        assertNull (empty);
    }

    @Test
    public void testMatmAircraftFuserStoreCollections ()
    {
        List<MatmAircraft> aircraftList = new ArrayList<>();
        aircraftList.add(createAircraft (UUID.randomUUID().toString(), "A123"));
        aircraftList.add(createAircraft (UUID.randomUUID().toString(), "B123"));
        aircraftList.add(createAircraft (UUID.randomUUID().toString(), "C123"));

        store.addAll (aircraftList);

        assertEquals (aircraftList.size(), store.size());

        aircraftList.remove(0);
        store.removeAll (aircraftList);

        assertEquals (1, store.size());
    }

    @Test
    public void testMatmFuserStoreClear ()
    {
        List<MatmAircraft> aircraftList = new ArrayList<>();
        aircraftList.add(createAircraft (UUID.randomUUID().toString(), "A123"));
        aircraftList.add(createAircraft (UUID.randomUUID().toString(), "B123"));
        aircraftList.add(createAircraft (UUID.randomUUID().toString(), "C123"));

        store.addAll (aircraftList);

        assertEquals (aircraftList.size(), store.size());

        store.clear();

        assertEquals (0, store.size());
    }

    @Test
    public void testMatmFuserStoreListeners ()
    {
        MatmFuserStoreListener listener = new MatmFuserStoreListener ();
        store.addFuserStoreListener(listener);

        assertEquals (0, listener.getAdded().size());
        assertEquals (0, listener.getRemoved().size());

        MatmAircraft aircraft = createAircraft (UUID.randomUUID().toString(), "C731");

        store.add(aircraft);

        assertEquals (1, store.size());
        assertEquals (1, listener.getAdded().size());
        assertEquals (0, listener.getRemoved().size());

        listener.clear();
        assertEquals (0, listener.getAdded().size());
        assertEquals (0, listener.getRemoved().size());

        store.remove(aircraft);

        assertEquals (0, store.size());
        assertEquals (0, listener.getAdded().size());
        assertEquals (1, listener.getRemoved().size());
    }

    @Test
    public void testMatmFuserStoreLocking ()
    {		
        Thread addThread = new Thread(new AddRunnable(store));

        assertEquals (0, store.size());

        // because the store is locked before the add thread starts, the add
        // should be blocked until the store is unlocked from the current thread
        store.lockEntireStore();
        addThread.start();

        try 
        {
            Thread.sleep(2000);
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
            fail (e.getMessage());
        }

        // size should still be zero since the store has not been unlocked
        assertEquals (0, store.size());

        store.unlockEntireStore();

        try 
        {
            // give the add thread more than enough time to complete after the
            // store has been unlocked
            Thread.sleep(2000);
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
            fail (e.getMessage());
        }

        assertEquals (1, store.size());
    }

    private MatmAircraft createAircraft (String tail, String acType)
    {
        MatmAircraft aircraft = new MatmAircraft();
        aircraft.setRegistration(tail);
        aircraft.setType(acType);

        return aircraft;
    }

    private class AddRunnable
    implements Runnable
    {
        FuserStore<MatmAircraft, MetaData> store;

        public AddRunnable (FuserStore<MatmAircraft, MetaData> store)
        {
            this.store = store;
        }

        @Override
        public void run ()
        {
            MatmAircraft aircraft = createAircraft (UUID.randomUUID().toString(), "MD80");

            // attempt to get the store lock
            store.lockStore( aircraft );
            store.add(aircraft);
            store.unlockStore( aircraft );
        }
    }

    private class MatmFuserStoreListener
    implements FuserStoreListener <MatmAircraft>
    {
        private List<MatmAircraft> added = new ArrayList<> ();
        private List<MatmAircraft> removed = new ArrayList<> ();

        @Override
        public void handleFuserStoreEvent(FuserStoreEvent<MatmAircraft> event)
        {
            if (event == null)
                return;

            switch (event.getEventType())
            {
                case ADD:	added.add(event.getContent());
                break;
                case REMOVE:	removed.add(event.getContent());
                break;
                default:
                    break;
            }
        }

        public void clear ()
        {
            added.clear();
            removed.clear();
        }

        public List<MatmAircraft> getAdded ()
        {
            return added;
        }

        public List<MatmAircraft> getRemoved ()
        {
            return removed;
        }

    }
}
