package com.mosaicatm.fuser.common.matm.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.mosaicatm.fuser.common.matm.util.flight.MatmFlightCarrierUtil;
import com.mosaicatm.matmdata.common.FlightType;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.TfmExtension;
import com.mosaicatm.matmdata.flight.extension.TfmTfdmExtension;
import com.mosaicatm.matmdata.flight.extension.tfmcomm.TfmSensitivityReasonType;

public class MatmFlightCarrierUtilTest
{
    @Test
    public void testMatmFlightCarrierUtil()
    {
        MatmFlightCarrierUtil matmFlightCarrierUtil = new MatmFlightCarrierUtil();
                
        MatmFlight flight = new MatmFlight();
        
        // ACIDs that should be GA
        flight.setAcid( "N1443" );
        assertEquals( MatmFlightCarrierUtil.GA_CARRIER, matmFlightCarrierUtil.interpolateCarrier( flight ));
        
        flight.setAcid( "N10A" );
        assertEquals( MatmFlightCarrierUtil.GA_CARRIER, matmFlightCarrierUtil.interpolateCarrier( flight ));

        flight.setAcid( "LN1" );
        assertEquals( MatmFlightCarrierUtil.GA_CARRIER, matmFlightCarrierUtil.interpolateCarrier( flight ));

        flight.setAcid( "RAPTOR1" );
        flight.setFlightType( FlightType.MILITARY );
        assertEquals( MatmFlightCarrierUtil.MILITARY_CARRIER, matmFlightCarrierUtil.interpolateCarrier( flight ));        
        
        flight.setAcid( "BFF555" );
        flight.setFlightType( null );
        assertEquals( "BFF", matmFlightCarrierUtil.interpolateCarrier( flight ));

        flight.setAcid( "EJA33A" );
        assertEquals( "EJA", matmFlightCarrierUtil.interpolateCarrier( flight ));

        flight.setAcid( "AAL101" );
        assertEquals( "AAL", matmFlightCarrierUtil.interpolateCarrier( flight ));

        flight.setAcid( "WOW3KN" );
        assertEquals( "WOW", matmFlightCarrierUtil.interpolateCarrier( flight ));
    }   
    
    @Test
    public void testAcidConversion()
    {
        MatmFlightCarrierUtil matmFlightCarrierUtil = new MatmFlightCarrierUtil();
        
        MatmFlight flight = new MatmFlight();
        
        // ACIDs that should be GA
        flight.setAcid( "N1443" );
        assertTrue(matmFlightCarrierUtil.isGeneralAviation( flight ));
        
        flight.setAcid( "N10A" );
        assertTrue(matmFlightCarrierUtil.isGeneralAviation( flight ));

        flight.setAcid( "LN1" );
        assertTrue(matmFlightCarrierUtil.isGeneralAviation( flight ));

        flight.setAcid( "BFF555" );
        assertTrue(matmFlightCarrierUtil.isGeneralAviation( flight ));

        flight.setAcid( "EJA33A" );
        assertTrue(matmFlightCarrierUtil.isGeneralAviation( flight ));

        
        // ACIDs that should not be GA
        flight.setAcid( "AAL101" );
        assertFalse(matmFlightCarrierUtil.isGeneralAviation( flight ));

        flight.setAcid( "WOW3KN" );
        assertFalse(matmFlightCarrierUtil.isGeneralAviation( flight ));        
    }   
    
    @Test
    public void testAcidConversionScheduled()
    {
        MatmFlightCarrierUtil matmFlightCarrierUtil = new MatmFlightCarrierUtil();
        
        MatmFlight flight = new MatmFlight();
        flight.setScheduledFlight( true );
        
        flight.setAcid( "BFF555" );
        assertFalse(matmFlightCarrierUtil.isGeneralAviation( flight ));
        assertTrue(matmFlightCarrierUtil.isScheduledFlight( flight ));

        flight.setAcid( "EJA33A" );
        assertFalse(matmFlightCarrierUtil.isGeneralAviation( flight ));
        assertTrue(matmFlightCarrierUtil.isScheduledFlight( flight ));

        flight.setAcid( "AAL101" );
        assertFalse(matmFlightCarrierUtil.isGeneralAviation( flight ));
        assertTrue(matmFlightCarrierUtil.isScheduledFlight( flight ));

        flight.setAcid( "WOW3KN" );
        assertFalse(matmFlightCarrierUtil.isGeneralAviation( flight ));        
        assertTrue(matmFlightCarrierUtil.isScheduledFlight( flight ));
    } 

    @Test
    public void testMilitary()
    {
        MatmFlightCarrierUtil matmFlightCarrierUtil = new MatmFlightCarrierUtil();
        
        MatmFlight flight = new MatmFlight();
        
        flight.setAcid( "BOMBER2" );
        //assertFalse( matmFlightCarrierUtil.isGeneralAviation( flight ));
        assertFalse(matmFlightCarrierUtil.isScheduledFlight( flight ));
        assertFalse(matmFlightCarrierUtil.isMilitaryFlight( flight ));

        flight.setExtensions( new MatmFlightExtensions() );
        flight.getExtensions().setTfmExtension( new TfmExtension() );
        flight.getExtensions().getTfmExtension().setSensitivityReason( TfmSensitivityReasonType.FS );
        
        assertFalse(matmFlightCarrierUtil.isMilitaryFlight( flight ));
               
        flight.getExtensions().getTfmExtension().setSensitivityReason( null );
        assertFalse(matmFlightCarrierUtil.isMilitaryFlight( flight ));
        
        flight.getExtensions().setTfmTfdmExtension( new TfmTfdmExtension() );
        flight.getExtensions().getTfmTfdmExtension().setSensitivityReason( TfmSensitivityReasonType.FS );                
        assertFalse(matmFlightCarrierUtil.isMilitaryFlight( flight ));    
    }     
    
    @Test
    public void testConversionFromFile()
    {
        MatmFlightCarrierUtil matmFlightCarrierUtil = new MatmFlightCarrierUtil();
        
        MatmFlight flight = new MatmFlight();

        flight.setAcid( "EJA33A" );
        assertTrue(matmFlightCarrierUtil.isGeneralAviation( flight ));
        
        flight.setAcid( "TST555" );
        assertFalse(matmFlightCarrierUtil.isGeneralAviation( flight ));        
        
        // Now, load the test file with different prefixes
        matmFlightCarrierUtil.setGeneralAviationPrefixesFile( "target/test-classes/GA-Callsign-Prefixes-TEST.txt" );
        
        flight.setAcid( "EJA33A" );
        assertFalse(matmFlightCarrierUtil.isGeneralAviation( flight ));        
        
        flight.setAcid( "TST555" );
        assertTrue(matmFlightCarrierUtil.isGeneralAviation( flight ));        
    }    
}
