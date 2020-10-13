package com.mosaicatm.sfdpsplugin.matm;

import com.mosaicatm.fuser.transform.api.AbstractSourceTransformApi;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;
import com.mosaicatm.sfdps.data.transfer.SfdpsSectorAssignmentTransfer;
import com.mosaicatm.sfdps.data.transfer.SfdpsSectorAssignmentTransferEnvelope;

public class SfdpsSectorAssignmentTransferEnvelopeMatmTransformApi 
    extends AbstractSourceTransformApi <SfdpsSectorAssignmentTransferEnvelope, MatmSectorAssignment>
{
    private final Log log = LogFactory.getLog(getClass());

    private Transformer<MatmSectorAssignment, SfdpsSectorAssignmentTransfer> singleToMatmTransform;

    public SfdpsSectorAssignmentTransferEnvelopeMatmTransformApi()
    {
        GenericMarshaller genericMarshaller = null;
        try
        {
            genericMarshaller = new GenericMarshaller( SfdpsSectorAssignmentTransferEnvelope.class );
            genericMarshaller.setMarshallFormatted( false );
            genericMarshaller.setMarshallHeader( false );
            genericMarshaller.setLogValidationErrors( true );
            genericMarshaller.init();
        }
        catch (JAXBException e)
        {
            log.error( "Failed to setup GenericMarshaller", e );
        }
        
        setMarshaller( genericMarshaller );
        setSingleToMatmTransform( new SfdpsSectorAssignmentTransferToMatmTransform() );
    }

    @Override
    public List<MatmSectorAssignment> toMatm (SfdpsSectorAssignmentTransferEnvelope batch)
    {
        List<MatmSectorAssignment> matm = new ArrayList<>();
        
        List<SfdpsSectorAssignmentTransfer> sfdpsSectorAssignmentTransfers = fromBatch( batch );
        
        for( SfdpsSectorAssignmentTransfer sfdpsSectorAssignmentTransfer : sfdpsSectorAssignmentTransfers )
        {
            MatmSectorAssignment matmFlight = singleToMatmTransform.transform( sfdpsSectorAssignmentTransfer );
            matm.add( matmFlight );
        }
        
        return matm;
    }

    public List<SfdpsSectorAssignmentTransfer> fromBatch(SfdpsSectorAssignmentTransferEnvelope envelope) 
    {
        List<SfdpsSectorAssignmentTransfer> rtnBatch = null;
        
        if (envelope != null)
        {            
            rtnBatch = envelope.getSfdpsSectorAssignmentTransferData();
            
            if (log.isDebugEnabled())
                log.debug ("Sfdps batch size: " + rtnBatch.size());
        }
        
        return rtnBatch;
    }

    public Transformer<MatmSectorAssignment, SfdpsSectorAssignmentTransfer> getSingleToMatmTransform()
    {
        return singleToMatmTransform;
    }

    public void setSingleToMatmTransform( Transformer<MatmSectorAssignment, SfdpsSectorAssignmentTransfer> singleToMatmTransform )
    {
        this.singleToMatmTransform = singleToMatmTransform;
    }

}
