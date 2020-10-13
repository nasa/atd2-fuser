package com.mosaicatm.fuser.updaters.post;

import java.util.ArrayList;
import java.util.UUID;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;
import com.mosaicatm.matmdata.flight.extension.TbfmExtension;
import com.mosaicatm.matmdata.flight.extension.TbfmMeterReferencePointType;

public class TbfmExtensionUpdaterTest
{
    private TbfmExtensionUpdater updater;
    
    @Before
    public void setup ()
    {
        updater = new TbfmExtensionUpdater();
    }
    
    @Test
    public void testMrpUpdates()
    {
        MatmFlight update = getMatmFlight( "A", "B", "C" );
        TbfmExtension tbfmUpdate = update.getExtensions().getTbfmExtension();
        
        MatmFlight target = getMatmFlight((String[]) null );
        TbfmExtension tbfmTarget = target.getExtensions().getTbfmExtension();
        
        updater.update( update, target );
        
        assertEquals( 3, tbfmUpdate.getMeterReferencePointList().size() );

        
        update = getMatmFlight((String[]) null );
        tbfmUpdate = update.getExtensions().getTbfmExtension();
        
        target = getMatmFlight( "A", "B", "C" );
        tbfmTarget = target.getExtensions().getTbfmExtension();
        
        updater.update( update, target );
        
        // No update needed -- target already good
        assertEquals( 0, tbfmUpdate.getMeterReferencePointList().size() );    
        
        
        update = getMatmFlight( "D", "D" );
        tbfmUpdate = update.getExtensions().getTbfmExtension();
        
        target = getMatmFlight( "A", "B", "C" );
        tbfmTarget = target.getExtensions().getTbfmExtension();
        
        updater.update( update, target );        
        
        assertEquals( 5, tbfmUpdate.getMeterReferencePointList().size() ); 
        assertEquals( "A", tbfmUpdate.getMeterReferencePointList().get( 0 ).getArtcc() ); 
        assertEquals( "B", tbfmUpdate.getMeterReferencePointList().get( 1 ).getArtcc() ); 
        assertEquals( "C", tbfmUpdate.getMeterReferencePointList().get( 2 ).getArtcc() ); 
        assertEquals( "D", tbfmUpdate.getMeterReferencePointList().get( 3 ).getArtcc() ); 
        assertEquals( "D", tbfmUpdate.getMeterReferencePointList().get( 4 ).getArtcc() ); 
                
        
        update = getMatmFlight( "A", "A" );
        tbfmUpdate = update.getExtensions().getTbfmExtension();
        
        target = getMatmFlight( "A", "B", "C" );
        tbfmTarget = target.getExtensions().getTbfmExtension();
        
        updater.update( update, target );        
        
        assertEquals( 4, tbfmUpdate.getMeterReferencePointList().size() ); 
        assertEquals( "A", tbfmUpdate.getMeterReferencePointList().get( 0 ).getArtcc() ); 
        assertEquals( "A", tbfmUpdate.getMeterReferencePointList().get( 1 ).getArtcc() ); 
        assertEquals( "B", tbfmUpdate.getMeterReferencePointList().get( 2 ).getArtcc() ); 
        assertEquals( "C", tbfmUpdate.getMeterReferencePointList().get( 3 ).getArtcc() ); 
        
        
        update = getMatmFlight( "B", "B" );
        tbfmUpdate = update.getExtensions().getTbfmExtension();
        
        target = getMatmFlight( "A", "B", "C" );
        tbfmTarget = target.getExtensions().getTbfmExtension();
        
        updater.update( update, target );        
        
        assertEquals( 4, tbfmUpdate.getMeterReferencePointList().size() ); 
        assertEquals( "A", tbfmUpdate.getMeterReferencePointList().get( 0 ).getArtcc() ); 
        assertEquals( "B", tbfmUpdate.getMeterReferencePointList().get( 1 ).getArtcc() ); 
        assertEquals( "B", tbfmUpdate.getMeterReferencePointList().get( 2 ).getArtcc() ); 
        assertEquals( "C", tbfmUpdate.getMeterReferencePointList().get( 3 ).getArtcc() ); 
                    
        
        update = getMatmFlight( "C", "C" );
        tbfmUpdate = update.getExtensions().getTbfmExtension();
        
        target = getMatmFlight( "A", "B", "C" );
        tbfmTarget = target.getExtensions().getTbfmExtension();
        
        updater.update( update, target );        
        
        assertEquals( 4, tbfmUpdate.getMeterReferencePointList().size() ); 
        assertEquals( "A", tbfmUpdate.getMeterReferencePointList().get( 0 ).getArtcc() ); 
        assertEquals( "B", tbfmUpdate.getMeterReferencePointList().get( 1 ).getArtcc() ); 
        assertEquals( "C", tbfmUpdate.getMeterReferencePointList().get( 2 ).getArtcc() ); 
        assertEquals( "C", tbfmUpdate.getMeterReferencePointList().get( 3 ).getArtcc() );           
    }
    
    private MatmFlight getMatmFlight( String ... artccList )
    {
        MatmFlight matm = new MatmFlight();
        matm.setGufi("gufi");
        matm.setTimestamp( new Date() );        
        matm.setLastUpdateSource( "TMA" );        
        
        matm.setExtensions( new MatmFlightExtensions() );
        matm.getExtensions().setTbfmExtension( new TbfmExtension() );
        
        if(( artccList != null ) && ( artccList.length > 0 ))
        {
            List<TbfmMeterReferencePointType> mrpUpdateList = new ArrayList<>();
            matm.getExtensions().getTbfmExtension().setMeterReferencePointList( mrpUpdateList );

            for( String artcc : artccList )
            {
                TbfmMeterReferencePointType mrp = new TbfmMeterReferencePointType();
                mrp.setArtcc( artcc );
                mrp.setStreamClass( UUID.randomUUID().toString() );
                mrpUpdateList.add( mrp );
            }
        }
        
        return( matm );
    }
}
