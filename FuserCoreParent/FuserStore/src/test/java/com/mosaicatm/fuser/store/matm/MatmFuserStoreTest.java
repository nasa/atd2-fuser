package com.mosaicatm.fuser.store.matm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightIdLookup;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.event.FuserStoreEvent;
import com.mosaicatm.fuser.store.event.FuserStoreListener;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFuserStoreTest
{
    private MatmFuserStore store;

    @Before
    public void setup(){
        int numberOfLocks = 1;
        store = new MatmFuserStore( numberOfLocks );
        store.setIdLookup(new MatmFlightIdLookup());
    }

    @Test
    public void testMatmFuserStore ()
    {
        MatmFlight flight = createFlight("0000-test-gufi-0000", "AAL123");

        store.add(flight);

        assertEquals (1, store.size());

        MatmFlight copy = store.get(flight.getGufi());

        assertNotNull (copy);
        assertEquals (flight.getGufi(), copy.getGufi());
        assertEquals (flight.getAcid(), copy.getAcid());

        store.remove(flight);

        assertEquals (0, store.size());

        MatmFlight empty = store.get(flight.getGufi());

        assertNull (empty);
    }

    @Test
    public void testMatmFuserStoreCollections ()
    {
        List<MatmFlight> flights = new ArrayList<>();
        flights.add(createFlight ("0000-test-gufi-0000", "AAL0"));
        flights.add(createFlight ("1111-test-gufi-0000", "AAL1"));
        flights.add(createFlight ("2222-test-gufi-2222", "AAL2"));

        store.addAll (flights);

        assertEquals (flights.size(), store.size());

        flights.remove(0);
        store.removeAll (flights);

        assertEquals (1, store.size());
    }

    @Test
    public void testMatmFuserStoreClear ()
    {
        List<MatmFlight> flights = new ArrayList<>();
        flights.add(createFlight ("0000-test-gufi-0000", "AAL0"));
        flights.add(createFlight ("1111-test-gufi-0000", "AAL1"));
        flights.add(createFlight ("2222-test-gufi-2222", "AAL2"));

        store.addAll (flights);

        assertEquals (flights.size(), store.size());

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

        MatmFlight flight = createFlight ("9999-test-gufi-9999", "DAL78");

        store.add(flight);

        assertEquals (1, store.size());
        assertEquals (1, listener.getAdded().size());
        assertEquals (0, listener.getRemoved().size());

        listener.clear();
        assertEquals (0, listener.getAdded().size());
        assertEquals (0, listener.getRemoved().size());

        store.remove(flight);

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

    private MatmFlight createFlight (String gufi, String acid)
    {
        MatmFlight flight = new MatmFlight ();

        flight.setGufi(gufi);
        flight.setAcid(acid);

        return flight;
    }

    private class AddRunnable
    implements Runnable
    {
        FuserStore<MatmFlight, MetaData> store;

        public AddRunnable (FuserStore<MatmFlight, MetaData> store)
        {
            this.store = store;
        }

        @Override
        public void run ()
        {
            MatmFlight flight = createFlight ("9999-test-gufi-9999", "DAL78");

            // attempt to get the store lock
            store.lockStore( flight );
            store.add(flight);
            store.unlockStore( flight );
        }
    }

    private class MatmFuserStoreListener
    implements FuserStoreListener <MatmFlight>
    {
        private List<MatmFlight> added = new ArrayList<> ();
        private List<MatmFlight> removed = new ArrayList<> ();

        @Override
        public void handleFuserStoreEvent(FuserStoreEvent<MatmFlight> event)
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

        public List<MatmFlight> getAdded ()
        {
            return added;
        }

        public List<MatmFlight> getRemoved ()
        {
            return removed;
        }

    }
}
