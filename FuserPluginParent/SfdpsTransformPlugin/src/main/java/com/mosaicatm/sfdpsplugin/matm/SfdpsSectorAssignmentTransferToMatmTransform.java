package com.mosaicatm.sfdpsplugin.matm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;
import com.mosaicatm.sfdps.data.transfer.SfdpsSectorAssignmentTransfer;

public class SfdpsSectorAssignmentTransferToMatmTransform
        implements Transformer <MatmSectorAssignment, SfdpsSectorAssignmentTransfer>
{
	private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
	public MatmSectorAssignment transform( SfdpsSectorAssignmentTransfer sfdps )
	{
		MatmSectorAssignment matm = new MatmSectorAssignment();       
        
        matm.setLastUpdateSource( "SFDPS" );
        matm.setSystemId( "SFDPS-" + sfdps.getMessageType() );
        
        matm.setTimestamp( sfdps.getTimestamp() );
        matm.setTimestampSource( sfdps.getTimestamp() );
        matm.setTimestampSourceProcessed( sfdps.getProcessedTime() );
        matm.setTimestampSourceReceived( sfdps.getReceiptTime() );
        
        matm.setSourceFacility( sfdps.getSourceFacility() );
        matm.setSectorName( sfdps.getSector() );
        matm.setFixedAirspaceVolumeList( sfdps.getFixedAirspaceVolumeList() );
		
		return matm;
	}
}


