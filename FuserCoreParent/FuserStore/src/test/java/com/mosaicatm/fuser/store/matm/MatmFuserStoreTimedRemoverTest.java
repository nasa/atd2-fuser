package com.mosaicatm.fuser.store.matm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightIdLookup;
import com.mosaicatm.fuser.store.FuserStoreProxy;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.time.SystemClock;
import com.mosaicatm.lib.util.TimeFactory;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFuserStoreTimedRemoverTest 
{
    private FuserStoreProxy<MatmFlight, MetaData> proxy;

    @Before
    public void setup(){
        int numberOfLocks = 1;
        MatmFuserStore store = new MatmFuserStore( numberOfLocks );
        store.setIdLookup(new MatmFlightIdLookup());
        proxy = new FuserStoreProxy<> (store);
    }

    @Test
    public void testMatmFuserStoreTimedRemover ()
    {
        Clock clock = new SystemClock ();

        MatmFuserStoreTimedRemover remover = new MatmFuserStoreTimedRemover (proxy);

        MatmFlight flight = new MatmFlight();
        flight.setGufi(UUID.randomUUID().toString());
        flight.setTimestamp(new Date(clock.getTimeInMillis() - (48 * TimeFactory.HOUR_IN_MILLIS)));

        proxy.add(flight);

        assertEquals (1, proxy.size());
        assertNotNull (proxy.get(flight.getGufi()));

        remover.remove();

        assertEquals (0, proxy.size());
        assertNull (proxy.get(flight.getGufi()));

        //test removing an arrived flight
        flight = new MatmFlight();
        flight.setGufi(UUID.randomUUID().toString());
        flight.setTimestamp(new Date(clock.getTimeInMillis() - (long)(10.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        flight.setArrivalRunwayActualTime( new Date(clock.getTimeInMillis() - (long)(10.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));

        proxy.add(flight);

        remover.remove();

        assertEquals (0, proxy.size());
        assertNull (proxy.get(flight.getGufi())); 


        //test removing an active flight
        flight = new MatmFlight();
        flight.setGufi(UUID.randomUUID().toString());
        flight.setTimestamp(new Date(clock.getTimeInMillis() - (long)(20.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        Position pos = new Position();
        pos.setTimestamp( new Date(clock.getTimeInMillis() - (long)(20.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        flight.setPosition( pos );

        proxy.add(flight);

        remover.remove();

        assertEquals (0, proxy.size());
        assertNull (proxy.get(flight.getGufi())); 


        //test removing an asdex-only flight
        flight = new MatmFlight();
        flight.setGufi(UUID.randomUUID().toString());
        flight.setTimestamp(new Date(clock.getTimeInMillis() - (long)(1.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        flight.setLastUpdateSource( "ASDEX" );
        pos = new Position();
        pos.setTimestamp( new Date(clock.getTimeInMillis() - (long)(1.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        flight.setPosition( pos );

        proxy.add(flight);

        remover.remove();

        assertEquals (0, proxy.size());
        assertNull (proxy.get(flight.getGufi())); 


        //test not removing a really stale flight, but is departing in future
        flight = new MatmFlight();
        flight.setGufi(UUID.randomUUID().toString());
        flight.setTimestamp(new Date(clock.getTimeInMillis() - (long)(30.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        flight.setDepartureRunwayEstimatedTime( new Date(clock.getTimeInMillis() + (long)( 0.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));

        proxy.add(flight);

        remover.remove();

        assertEquals (1, proxy.size());
        assertNotNull (proxy.get(flight.getGufi()));           

        //test removing a flight that never departed (same flight as above, but departure time in past)
        flight.setDepartureRunwayEstimatedTime( new Date(clock.getTimeInMillis() - (long)( 8.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));

        proxy.add(flight);

        remover.remove();

        assertEquals (0, proxy.size());
        assertNull (proxy.get(flight.getGufi()));    

        //test removing a flight that has no data except for a timestamp 48 hours old
        flight = new MatmFlight();
        flight.setGufi(UUID.randomUUID().toString());
        flight.setTimestamp( new Date(clock.getTimeInMillis() - (long)( 48 * ((double)TimeFactory.HOUR_IN_MILLIS ))));

        proxy.add(flight);    

        remover.remove();

        assertEquals (0, proxy.size());
        assertNull (proxy.get(flight.getGufi())); 


        //test removing a flight that never departed, and has a 48 hour old timestamp, but the ETD is in the future, so we keep it
        flight = new MatmFlight();
        flight.setGufi(UUID.randomUUID().toString());
        flight.setTimestamp( new Date(clock.getTimeInMillis() - (long)( 48 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        flight.setDepartureRunwayEstimatedTime( new Date(clock.getTimeInMillis() + (long)( 8.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));

        proxy.add(flight);    

        remover.remove();

        assertEquals (1, proxy.size());
        assertNotNull (proxy.get(flight.getGufi()));         

        //test removing a flight that should normally be removed, except we got an recent FMC update (that we want to ignore)
        proxy.clear();

        flight = new MatmFlight();
        flight.setGufi(UUID.randomUUID().toString());
        flight.setTimestamp( new Date(clock.getTimeInMillis() ));
        flight.setDepartureRunwayEstimatedTime( new Date(clock.getTimeInMillis() - (long)( 8.5 * ((double)TimeFactory.HOUR_IN_MILLIS ))));

        ArrayList<MetaData> update_sources = new ArrayList();
        MetaData meta = new MetaData();
        meta.setSource( FuserSource.TMA.toString() );
        meta.setTimestamp( new Date(clock.getTimeInMillis() - (long)( 48 * ((double)TimeFactory.HOUR_IN_MILLIS ))));
        update_sources.add( meta );

        meta = new MetaData();
        meta.setSource( FuserSource.FMC.toString() );
        meta.setTimestamp( flight.getTimestamp() );
        update_sources.add( meta );

        flight.setUpdateSources(update_sources);

        proxy.add(flight);    

        remover.remove();

        assertEquals (0, proxy.size());
        assertNull (proxy.get(flight.getGufi()));         
    }

    @Test
    public void testMatmFuserStoreTimedRemoverThread ()
    {
        Clock clock = new SystemClock ();

        MatmFuserStoreTimedRemover remover = new MatmFuserStoreTimedRemover (proxy);
        remover.setCheckIntervalMillis(1000L);

        MatmFlight flight = new MatmFlight();
        flight.setGufi(UUID.randomUUID().toString());
        flight.setTimestamp(new Date(clock.getTimeInMillis() - (48 * TimeFactory.HOUR_IN_MILLIS)));

        proxy.add(flight);

        assertEquals (1, proxy.size());
        assertNotNull (proxy.get(flight.getGufi()));

        remover.start();

        try
        {
            Thread.sleep(2000L);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail (e.getMessage());
        }

        assertEquals (0, proxy.size());
        assertNull (proxy.get(flight.getGufi()));
    }
}
