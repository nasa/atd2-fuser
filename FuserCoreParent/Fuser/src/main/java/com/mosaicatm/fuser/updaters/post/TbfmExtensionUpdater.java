package com.mosaicatm.fuser.updaters.post;

import java.util.List;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.updaters.AbstractUpdater;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.TbfmExtension;
import com.mosaicatm.matmdata.flight.extension.TbfmMeterReferencePointType;

public class TbfmExtensionUpdater
extends AbstractUpdater <MatmFlight, MatmFlight>
{
    private final Log log = LogFactory.getLog(getClass());
            
    @Override
    public void update(MatmFlight update, MatmFlight target)
    {
        if (!isActive())
        {
            return;
        }
        
        if( update == null )
        {
            log.error("Cannot update TBFM extension. Update is NULL!");
            return;
        }
        
        // Nothing to do if there is no TBFM extension update
        if(( update.getExtensions() == null ) || 
                ( update.getExtensions().getTbfmExtension() == null ))
        {
            return;
        }        
        
        // Currently, the only TBFM extension data that needs massaging is merging the target MRP List with the update
        if(( target == null ) || 
                ( target.getExtensions() == null ) || 
                ( target.getExtensions().getTbfmExtension() == null ) || 
                ( target.getExtensions().getTbfmExtension().getMeterReferencePointList() == null ) ||
                ( target.getExtensions().getTbfmExtension().getMeterReferencePointList().isEmpty() ) ||
                ( update.getExtensions().getTbfmExtension().getMeterReferencePointList() == null ) ||
                ( update.getExtensions().getTbfmExtension().getMeterReferencePointList().isEmpty() ))
        {
            return;
        }         

        TbfmExtension tbfmUpdate = update.getExtensions().getTbfmExtension();
        TbfmExtension tbfmTarget = target.getExtensions().getTbfmExtension();
        
        List<TbfmMeterReferencePointType> mrpUpdateList = tbfmUpdate.getMeterReferencePointList();
        List<TbfmMeterReferencePointType> mrpTargetList = tbfmTarget.getMeterReferencePointList();

        // The list coming in should be the full state for each artcc. TmaLite takes care of 
        // deleting MRPs that should be deleted. So all this does is merge together the update 
        // with the target's data for other artccs. Keeping the same artcc order.
        
        String updateArtcc = mrpUpdateList.get( 0 ).getArtcc();
        boolean insertBefore = true;
        
        for( int i = 0; i < mrpTargetList.size(); i++ )
        {            
            TbfmMeterReferencePointType targetMrp = mrpTargetList.get( i );
            
            if( !Objects.equals( updateArtcc, targetMrp.getArtcc() ))
            {
                // Insert all the other artcc target MRPs that occur before the update artcc
                if( insertBefore )
                {
                    mrpUpdateList.add( i, (TbfmMeterReferencePointType) targetMrp.clone() );
                }
                // Insert all the other artcc target MRPs that occur after the update artcc
                else
                {
                    mrpUpdateList.add((TbfmMeterReferencePointType) targetMrp.clone() );
                }
            }
            else
            {
                insertBefore = false;
            }
        }   
        
        if( mrpUpdateList.size() > 25 )
        {
            log.warn( "Generated large MRP list: " + update.getGufi() + ", size=" + mrpUpdateList.size() );
        }
    }
}
