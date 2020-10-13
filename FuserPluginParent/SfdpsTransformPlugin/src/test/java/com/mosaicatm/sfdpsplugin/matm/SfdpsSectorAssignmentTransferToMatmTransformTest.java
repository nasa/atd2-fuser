package com.mosaicatm.sfdpsplugin.matm;

import java.util.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.mosaicatm.matmdata.sector.MatmSectorAssignment;
import com.mosaicatm.sfdps.data.transfer.SfdpsSectorAssignmentTransfer;

public class SfdpsSectorAssignmentTransferToMatmTransformTest
{
	@Test
	public void testTransform()
	{
		SfdpsSectorAssignmentTransferToMatmTransform transform = new SfdpsSectorAssignmentTransferToMatmTransform();
        
        SfdpsSectorAssignmentTransfer transfer = new SfdpsSectorAssignmentTransfer();
		Date receipt = new Date(5);
        Date processed = new Date(6);
		Date timestamp = new Date(7);
		String artcc = "ZOB";
		String msg = "SH";        
        String sector = "ZOB55";
        List<String> fixedAirspaceVolumeList = Arrays.asList( "ZOB5500", "ZOB5501", "ZOB5502" );
        
		transfer.setReceiptTime(receipt);
        transfer.setProcessedTime(processed);
		transfer.setTimestamp(timestamp);
		transfer.setMessageType(msg);
        transfer.setSourceFacility(artcc);
        transfer.setSector(sector);
        transfer.setFixedAirspaceVolumeList(fixedAirspaceVolumeList);

        MatmSectorAssignment matm = transform.transform( transfer );
                
		assertEquals("timestampSourceProcessed", processed, matm.getTimestampSourceProcessed());
        assertEquals("timestampSourceReceived", receipt, matm.getTimestampSourceReceived());
		assertEquals("timestamp", timestamp, matm.getTimestampSource());
		assertEquals("lastUpdateSource", "SFDPS", matm.getLastUpdateSource());
        assertEquals("systemId", "SFDPS-" + msg, matm.getSystemId());
		assertEquals("sectorName", sector, matm.getSectorName() );
		assertEquals("sourceFacility", artcc, matm.getSourceFacility());
		assertEquals("fixedAirspaceVolumeList", fixedAirspaceVolumeList, matm.getFixedAirspaceVolumeList());
	}
}
