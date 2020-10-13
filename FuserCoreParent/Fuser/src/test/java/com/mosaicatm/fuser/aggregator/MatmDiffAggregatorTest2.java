package com.mosaicatm.fuser.aggregator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightIdLookup;
import com.mosaicatm.fuser.rules.RuleFactory;
import com.mosaicatm.fuser.rules.SourceSystemPriorityMediationRule;
import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.fuser.store.matm.MatmFuserStore;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.common.MeteredTime;
import com.mosaicatm.matmdata.flight.MatmFlight;

// Note there is another MatmDiffAggregatorTest in the updaters package
public class MatmDiffAggregatorTest2
{
    @Test
    public void testIdenticalUpdate()
    {
        MetaDataManager<MatmFlight> mgr = new MetaDataManager<>();

        int processingThreadCount = 1;
        MatmAggregator<MatmFlight> aggregator = new MatmDiffAggregator();
        
        MatmFuserStore fuserStore = new MatmFuserStore( processingThreadCount );
        fuserStore.setIdLookup(new MatmFlightIdLookup());
        aggregator.setFuserStore( fuserStore );
        aggregator.setMetaDataManager( mgr );
        mgr.setFuserStore(fuserStore);

        //The baseline flight
        MatmFlight update = new MatmFlight();
        update.setGufi( "GUFI" );
        update.setRouteText( "ROUTE1" );
        
        //First update should work
        MatmFlight new_update = (MatmFlight) update.clone();
        new_update = aggregator.aggregate( new_update );
        assertEquals( new_update.getRouteText(), "ROUTE1" );       
        
        //Second identical update should return null
        new_update = (MatmFlight) update.clone();
        new_update = aggregator.aggregate( new_update );
        assertNull( new_update );
    }
   
    @Ignore
    @Test
    public void testFilterOutDuplicateDataUpdate()
    {
        MetaDataManager<MatmFlight> mgr = new MetaDataManager<>();

        int processingThreadCount = 1;
        MatmAggregator<MatmFlight> aggregator = new MatmDiffAggregator();
        MatmFuserStore fuserStore = new MatmFuserStore( processingThreadCount );
        fuserStore.setIdLookup(new MatmFlightIdLookup());
        aggregator.setFuserStore( fuserStore );
        aggregator.setMetaDataManager( mgr );

        //The baseline flight
        MatmFlight update = new MatmFlight();
        update.setGufi( "GUFI" );
        update.setRouteText( "ROUTE1" );
        
        //First update should work, setting the route to ROUTE1
        MatmFlight new_update = (MatmFlight) update.clone();
        new_update = aggregator.aggregate( new_update );
        assertEquals( new_update.getRouteText(), "ROUTE1" );       
        
        //Second identical update should return null, since both updates are the same
        new_update = (MatmFlight) update.clone();
        new_update = aggregator.aggregate( new_update );
        assertNull( new_update );
        
        //Here, we update the arrival runway scheduled time, but we do not change the route.
        new_update = (MatmFlight) update.clone();
        new_update.setArrivalRunwayScheduledTime( new Date( 50 ));
        new_update = aggregator.aggregate( new_update );
        assertNotNull( new_update );     
        assertEquals( new_update.getArrivalRunwayScheduledTime(), new Date( 50 ));
        assertNull( new_update.getRouteText() );
        assertEquals( new_update.getGufi(), "GUFI" );
    }    
    
    @Ignore
    @Test
    public void testFilterOutDuplicateDataUpdate2()
    {
        int processingThreadCount = 1;
        MatmDiffAggregator aggregator = new MatmDiffAggregator();
        aggregator.setMetaDataManager(new MetaDataManager<MatmFlight>());
        FuserStore<MatmFlight, MetaData> store = new MatmFuserStore( processingThreadCount );
        ((MatmFuserStore)store).setIdLookup(new MatmFlightIdLookup());
        aggregator.setFuserStore(store);
        
        MatmFlight flight = new MatmFlight();
        flight.setGufi("ABC123");
        flight.setDepartureRunwayActual("36R");
        flight.setDepartureRunwayAssigned("36R");
        flight.setCarrier("200");
        
        MatmFlight aggregated = aggregator.aggregate(flight);
        assertEquals("36R", aggregated.getDepartureRunwayActual());
        assertEquals("36R", aggregated.getDepartureRunwayAssigned());
        
        MatmFlight update2 = new MatmFlight();
        update2.setGufi("ABC123");
        update2.setDepartureRunwayActual("36R");
        update2.setDepartureRunwayAssigned("40");
        update2.setCarrier("200");
        
        MatmFlight result = aggregator.aggregate(update2);
        assertEquals("ABC123", result.getGufi());
        assertEquals("40", result.getDepartureRunwayAssigned());
        assertNull(result.getCarrier());
        assertNull(result.getDepartureRunwayActual());
        
        result = aggregator.aggregate(update2);
        assertNull(result);
        
    }
    
    @Test
    public void testTmaRulesIncludes()
    {
        int processingThreadCount = 1;
        String[] sources1 =
        {
            "IDAC", "TMA"
        };
        String[] fields =
        {
            "arrivalRunwayAssigned", "arrivalRunwayEstimatedTime", "arrivalRunwayMeteredTime",
            "extensions.tbfmExtension.arrivalRunway", "extensions.tbfmExtension.arrivalRunwaySta",
            "extensions.tbfmExtension.arrivalRunwayTraconAssigned", "extensions.tbfmExtension.arrivalRunwayAssignmentFrozen"
        };
        SourceSystemPriorityMediationRule<MatmFlight> rule1 = new SourceSystemPriorityMediationRule<>();
        rule1.setUpdateSources( Arrays.asList( sources1 ) );
        rule1.setFavorSystemIdSuffix( "-SWIM" );
        rule1.setExcludes( Arrays.asList( fields ) );
        rule1.setExcludesRegex( "extensions.idacExtension.*" );
        rule1.setActive( true );
        rule1.setPriority( 70 );

        String[] sources2 =
        {
            "TMA"
        };
        String[] fields2 =
        {
            "arrivalRunwayAssigned", "arrivalRunwayEstimatedTime", "arrivalRunwayMeteredTime",
            "arrivalRunwayMeteredTime.value", "arrivalRunwayMeteredTime.frozen",
            "extensions.tbfmExtension.arrivalRunway", "extensions.tbfmExtension.arrivalRunwaySta",
            "extensions.tbfmExtension.arrivalRunwayTraconAssigned", "extensions.tbfmExtension.arrivalRunwayAssignmentFrozen"
        };
        SourceSystemPriorityMediationRule<MatmFlight> rule2 = new SourceSystemPriorityMediationRule<>();
        rule2.setUpdateSources( Arrays.asList( sources2 ) );
        rule2.setFavorSystemIdSuffix( "-NASA" );
        rule2.setIncludes( Arrays.asList( fields2 ) );
        rule2.setActive( true );
        rule2.setPriority( 80 );

        RuleFactory<MatmFlight> rules = new RuleFactory<>();
        rules.add( rule1 );
        rules.add( rule2 );
        rules.setActive( true );

        MatmFuserStore fuserStore = new MatmFuserStore( processingThreadCount );
        fuserStore.setIdLookup(new MatmFlightIdLookup());
        
        MetaDataManager<MatmFlight> mgr = new MetaDataManager<>();
        mgr.setRuleFactory( rules );
        mgr.setFuserStore(fuserStore);

        MatmAggregator<MatmFlight> aggregator = new MatmDiffAggregator();
        aggregator.setFuserStore( fuserStore );
        aggregator.setMetaDataManager( mgr );

        //The baseline flight -- from NASA source
        MatmFlight update = new MatmFlight();
        update.setGufi( "GUFI" );
        update.setTimestamp( new Date( 0 ) );
        update.setLastUpdateSource( "TMA" );
        update.setSystemId( "TMA-NASA" );
        update.setRouteText( "ROUTE1" );
        MeteredTime metered = new MeteredTime();
        metered.setFrozen( Boolean.FALSE );
        metered.setValue( new Date( 15 ));        
        update.setArrivalRunwayMeteredTime( metered );
        
        MatmFlight new_update = ( MatmFlight ) update.clone();
        new_update.setTimestamp( new Date( 1 ) );
        new_update = aggregator.aggregate( new_update );
        assertEquals( new_update.getRouteText(), "ROUTE1" );

        //Check that the nasa source can override
        new_update = ( MatmFlight ) update.clone();
        new_update.setTimestamp( new Date( 2 ) );
        new_update = aggregator.aggregate( new_update );
        assertNull( new_update );

        //Check that the nasa source can override
        new_update = ( MatmFlight ) update.clone();
        new_update.setTimestamp( new Date( 3 ) );
        new_update.setRouteText( "ROUTE2" );
        new_update = aggregator.aggregate( new_update );
        assertEquals( new_update.getRouteText(), "ROUTE2" );

        //Check that the nasa source can override
        new_update = ( MatmFlight ) update.clone();
        new_update.setTimestamp( new Date( 4 ) );
        new_update.setRouteText( "ROUTE3" );
        new_update = aggregator.aggregate( new_update );
        assertEquals( new_update.getRouteText(), "ROUTE3" );

        //Check that the swim source can override
        new_update = ( MatmFlight ) update.clone();
        new_update.setTimestamp( new Date( 5 ) );
        new_update.setRouteText( "ROUTE4" );
        new_update.setSystemId( "TMA-SWIM" );
        new_update = aggregator.aggregate( new_update );
        assertEquals( new_update.getRouteText(), "ROUTE4" );

        //Check that the nasa source can NOT override after SWIM
        new_update = ( MatmFlight ) update.clone();
        new_update.setTimestamp( new Date( 6 ) );
        new_update.setAcid("ABC");
        new_update.setRouteText( "ROUTE5" );
        new_update = aggregator.aggregate( new_update );
        assertNull( new_update.getRouteText() );

        //Check that the swim source identical update has a null route
        new_update = ( MatmFlight ) update.clone();
        new_update.setTimestamp( new Date( 7 ) );
        new_update.setRouteText( "ROUTE4" );
        new_update.setSystemId( "TMA-SWIM" );
        new_update = aggregator.aggregate( new_update );
        assertNull( new_update );
        
        //New baseline flight -- from SWIM source for testing ETA updates
        update.setLastUpdateSource( "TMA" );
        update.setSystemId( "TMA-SWIM" );
        update.setArrivalRunwayEstimatedTime( new Date( 10 ) );

        //Check that the swim source can set the initial estimated time
        new_update = ( MatmFlight ) update.clone();
        new_update.setTimestamp( new Date( 6 ) );
        new_update = aggregator.aggregate( new_update );
        assertEquals( new_update.getArrivalRunwayEstimatedTime(), new Date( 10 ) );

        //Check that the nasa source can override
        new_update = ( MatmFlight ) update.clone();
        new_update.setSystemId( "TMA-NASA" );
        new_update.setTimestamp( new Date( 7 ) );
        new_update.setArrivalRunwayEstimatedTime( new Date( 11 ) );
        new_update = aggregator.aggregate( new_update );
        assertEquals( new_update.getArrivalRunwayEstimatedTime(), new Date( 11 ) );

        //Check that the swim source can NOT override after NASA
        new_update = ( MatmFlight ) update.clone();
        new_update.setTimestamp( new Date( 8 ) );
        new_update.setAircraftAddress("123");
        new_update.setArrivalRunwayEstimatedTime( new Date( 12 ) );
        new_update = aggregator.aggregate( new_update );
        assertNull( new_update.getArrivalRunwayEstimatedTime() );
    }
    
    
    @Test
    public void testTmaMeteredTimeRule()
    {
        int processingThreadCount = 1;
        String[] sources1 =
        {
            "TMA", "IDAC"
        };
        String[] fields =
        {
            "arrivalRunwayAssigned", "arrivalRunwayEstimatedTime", "arrivalRunwayMeteredTime",
            "arrivalRunwayMeteredTime.value", "arrivalRunwayMeteredTime.frozen",
            "extensions.tbfmExtension.arrivalRunway", "extensions.tbfmExtension.arrivalRunwaySta",
            "extensions.tbfmExtension.arrivalRunwayTraconAssigned", "extensions.tbfmExtension.arrivalRunwayAssignmentFrozen"
        };
        SourceSystemPriorityMediationRule<MatmFlight> rule1 = new SourceSystemPriorityMediationRule<>();
        rule1.setUpdateSources( Arrays.asList( sources1 ) );
        rule1.setFavorSystemIdSuffix( "-SWIM" );
        rule1.setExcludes( Arrays.asList( fields ) );
        rule1.setExcludesRegex( "extensions.idacExtension.*" );
        rule1.setActive( true );
        rule1.setPriority( 70 );

        String[] sources2 =
        {
            "TMA"
        };
        String[] fields2 =
        {
            "arrivalRunwayAssigned", "arrivalRunwayEstimatedTime", "arrivalRunwayMeteredTime",
            "arrivalRunwayMeteredTime.value", "arrivalRunwayMeteredTime.frozen",
            "extensions.tbfmExtension.arrivalRunway", "extensions.tbfmExtension.arrivalRunwaySta",
            "extensions.tbfmExtension.arrivalRunwayTraconAssigned", "extensions.tbfmExtension.arrivalRunwayAssignmentFrozen"
        };
        SourceSystemPriorityMediationRule<MatmFlight> rule2 = new SourceSystemPriorityMediationRule<>();
        rule2.setUpdateSources( Arrays.asList( sources2 ) );
        rule2.setFavorSystemIdSuffix( "-NASA" );
        rule2.setIncludes( Arrays.asList( fields2 ) );
        rule2.setActive( true );
        rule2.setPriority( 80 );

        RuleFactory<MatmFlight> rules = new RuleFactory<>();
        rules.add( rule1 );
        rules.add( rule2 );
        rules.setActive( true );

        MatmFuserStore fuserStore = new MatmFuserStore( processingThreadCount );
        fuserStore.setIdLookup(new MatmFlightIdLookup());
        
        MetaDataManager<MatmFlight> mgr = new MetaDataManager<>();
        mgr.setFuserStore(fuserStore);
        mgr.setRuleFactory( rules );

        MatmAggregator<MatmFlight> aggregator = new MatmDiffAggregator();
        aggregator.setFuserStore( fuserStore );
        aggregator.setMetaDataManager( mgr );

        //The baseline flight -- from SWIM source
        MatmFlight update = new MatmFlight();
        update.setGufi( "GUFI" );
        update.setTimestamp( new Date( 0 ) );
        update.setLastUpdateSource( "TMA" );
        update.setSystemId( "TMA.ZTL.FAA.GOV-SWIM" );
        update.setRouteText( "ROUTE1" );
        MeteredTime metered = new MeteredTime();
        metered.setFrozen( Boolean.TRUE );
        metered.setValue( new Date( 15 ));        
        update.setArrivalRunwayMeteredTime( metered );
        aggregator.aggregate( update );
        
        
        //Make sure NASA can override metered time, but not the route
        MatmFlight new_update = ( MatmFlight ) update.clone();
        new_update.setSystemId( "TMA.ZTL.FAA.GOV-NASA" );
        new_update.setTimestamp( new Date( 2 ) );
        new_update.setRouteText( "ROUTE2" );
        new_update.getArrivalRunwayMeteredTime().setFrozen( Boolean.FALSE );
        new_update.getArrivalRunwayMeteredTime().setValue( new Date( 16 ));
        
        new_update = aggregator.aggregate( new_update );
        assertNull( new_update.getRouteText() );
        assertEquals( new_update.getArrivalRunwayMeteredTime().getValue(), new Date( 16 ));
        assertEquals( new_update.getArrivalRunwayMeteredTime().isFrozen(), Boolean.FALSE);
        
        
        //Make sure SWIM can override route, but not the metered time
        new_update = ( MatmFlight ) update.clone();
        new_update.setSystemId( "TMA.ZTL.FAA.GOV-SWIM" );
        new_update.setTimestamp( new Date( 3 ) );
        new_update.setRouteText( "ROUTE3" );
        new_update.getArrivalRunwayMeteredTime().setFrozen( Boolean.TRUE );
        new_update.getArrivalRunwayMeteredTime().setValue( new Date( 17 ));        
        
        new_update = aggregator.aggregate( new_update );
        assertEquals( new_update.getRouteText(), "ROUTE3" );
        assertNull( new_update.getArrivalRunwayMeteredTime().getValue() );
        assertNull( new_update.getArrivalRunwayMeteredTime().isFrozen() );      
               
        //Make sure this SWIM update returns null
        new_update = ( MatmFlight ) update.clone();
        new_update.setSystemId( "TMA.ZTL.FAA.GOV-SWIM" );
        new_update.setTimestamp( new Date( 5 ) );
        new_update.setRouteText( "ROUTE3" );
        new_update.getArrivalRunwayMeteredTime().setFrozen( Boolean.TRUE );
        new_update.getArrivalRunwayMeteredTime().setValue( new Date( 19 ));                  
        new_update = aggregator.aggregate( new_update );
        assertNull( new_update );
    }    
}
