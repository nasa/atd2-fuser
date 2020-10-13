package com.mosaicatm.fuser.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.mosaicatm.fuser.common.FuserSourceSystemType;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class RuleFactoryTest {

	@Test
	public void test() 
	{
		RuleFactory<MatmFlight> factory = new RuleFactory<>();
		factory.setActive(true);
		factory.setRules(new ArrayList<Rule<MatmFlight>>());
		
		Rule<MatmFlight> rule1 = new MultiplePriorityMediationRule<>();
		rule1.setName("rule1");
		rule1.setPriority(300);
		
		Rule<MatmFlight> rule2 = new AircraftTypeRule();
		rule2.setName("rule2");
		rule2.setPriority(200);
		
		Rule<MatmFlight> rule3 = new SourceSystemPriorityMediationRule<>();
		rule3.setName("rule3");
		rule3.setPriority(100);
		
		factory.add(rule3);
		factory.add(rule2);
		factory.add(rule1);
		
		assertTrue(factory.getRules().size() == 3);
		factory.order();
		assertTrue(factory.getRules().get(0) != null);
		assertTrue(factory.getRules().get(0).getName().equals("rule3"));
		assertTrue(factory.getRules().get(1) != null);
		assertTrue(factory.getRules().get(1).getName().equals("rule2"));
		assertTrue(factory.getRules().get(2) != null);
		assertTrue(factory.getRules().get(2).getName().equals("rule1"));
	}
	
	@Test
	public void RuleFactoryTestIdentical()
	{
		// expecting true if there is more favorable source in the update
		// otherwise false
		
		String field = "acid";
		RuleFactory<MatmFlight> factory = new RuleFactory<>();
		factory.setActive(true);
		factory.setRules(new ArrayList<Rule<MatmFlight>>());
		
		Rule<MatmFlight> rule1 = new MultiplePriorityMediationRule<>();
		rule1.setName("rule1");
		rule1.setPriority(200);
		List<String> list = new ArrayList<String>();
		list.add("acid");
		
		List<Set<FuserSourceSystemType>> favorSources = new ArrayList<>();
        favorSources.add( new HashSet<FuserSourceSystemType>() );
		favorSources.get( 0 ).add(FuserSourceSystemType.TFM);
		
		((MultiplePriorityMediationRule<MatmFlight>)rule1).setSourcePriorityList(favorSources);
		rule1.setIncludes(list);
		rule1.setActive(true);
		
		factory.add(rule1);
        
		Rule<MatmFlight> rule2 = new AircraftTypeRule();
		rule2.setName("rule2");
		rule2.setPriority(300);
		rule2.setIncludes(list);
		rule2.setActive(true);
		factory.add(rule2);        
		
		MatmFlight update = new MatmFlight();
		update.setAcid("acid");
		update.setLastUpdateSource("TMA");
		
		MatmFlight target = new MatmFlight();
		update.setAcid("acid");
		
		MetaData history = new MetaData();
		history.setSource("TFM");
		
		assertFalse(factory.handleIdentical(update, target, history, field));
		
		update.setLastUpdateSource("TFM");
		history.setSource("TMA");
		
		assertTrue(factory.handleIdentical(update, target, history, field));
	}
    
    @Test
    public void FuserSourceSystemTypeMatchTest()
    {
        MatmFlight flt = new MatmFlight();
        flt.setLastUpdateSource( "TFM" );
        
        assertTrue( FuserSourceSystemType.TFM.matches( flt ));
        assertFalse( FuserSourceSystemType.TFM_FLIGHT_PLAN_INFORMATION.matches( flt ));
        
        flt.setSystemId( "FLIGHT_PLAN_INFORMATION" );
        
        assertTrue( FuserSourceSystemType.TFM.matches( flt ));
        assertTrue( FuserSourceSystemType.TFM_FLIGHT_PLAN_INFORMATION.matches( flt ));        
    }
}
