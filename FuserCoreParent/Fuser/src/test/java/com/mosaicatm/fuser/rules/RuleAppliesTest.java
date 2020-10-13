package com.mosaicatm.fuser.rules;

import java.util.Arrays;
import org.junit.Test;

import com.mosaicatm.matmdata.flight.MatmFlight;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RuleAppliesTest {

	@Test
	public void testInclusion() 
	{
        //This can be any implementing class, picked AircraftTypeRule for no real reason.
        Rule<MatmFlight> rule = new AircraftTypeRule();
        rule.setActive( true );
        
        String[] includes = { "incl1", "incl2", "incl3" };
        rule.setIncludes( Arrays.asList( includes ));
        
        assertTrue( rule.ruleAppliesToField( "incl1" ));
        assertTrue( rule.ruleAppliesToField( "incl2" ));
        assertTrue( rule.ruleAppliesToField( "incl3" ));
        assertFalse( rule.ruleAppliesToField( "incl4" ));
        
        rule.setIncludesRegex( "incl.*" );
        assertTrue( rule.ruleAppliesToField( "incl1" ));
        assertTrue( rule.ruleAppliesToField( "incl2" ));
        assertTrue( rule.ruleAppliesToField( "incl3" ));        
        assertTrue( rule.ruleAppliesToField( "incl4" ));
	}
    
	@Test
	public void testExclusion() 
	{
        //This can be any implementing class, picked AircraftTypeRule for no real reason.
        Rule<MatmFlight> rule = new AircraftTypeRule();
        rule.setActive( true );
        
        String[] excludes = { "excl1", "excl2", "excl3" };
        rule.setExcludes( Arrays.asList( excludes ));        
        
        assertTrue( rule.ruleAppliesToField( "incl1" ));
        assertFalse( rule.ruleAppliesToField( "excl1" ));
        assertFalse( rule.ruleAppliesToField( "excl2" ));
        assertFalse( rule.ruleAppliesToField( "excl3" ));
        //Test ignored field
        assertFalse( rule.ruleAppliesToField( "timestamp" ));
        
        rule.setExcludesRegex( "excl.*" );
        assertTrue( rule.ruleAppliesToField( "incl1" ));
        assertTrue( rule.ruleAppliesToField( "incl2" ));
        assertTrue( rule.ruleAppliesToField( "incl3" )); 
        assertFalse( rule.ruleAppliesToField( "excl1" ));
        assertFalse( rule.ruleAppliesToField( "excl2" ));
        assertFalse( rule.ruleAppliesToField( "excl3" ));        
        assertFalse( rule.ruleAppliesToField( "excl4" ));
        //Test ignored field
        assertFalse( rule.ruleAppliesToField( "timestamp" ));    
        
        //Run the same tests, with only regex
        rule.setExcludes( null );
        assertTrue( rule.ruleAppliesToField( "incl1" ));
        assertTrue( rule.ruleAppliesToField( "incl2" ));
        assertTrue( rule.ruleAppliesToField( "incl3" )); 
        assertFalse( rule.ruleAppliesToField( "excl1" ));
        assertFalse( rule.ruleAppliesToField( "excl2" ));
        assertFalse( rule.ruleAppliesToField( "excl3" ));        
        assertFalse( rule.ruleAppliesToField( "excl4" ));
        //Test ignored field
        assertFalse( rule.ruleAppliesToField( "timestamp" ));           
	}    
}
