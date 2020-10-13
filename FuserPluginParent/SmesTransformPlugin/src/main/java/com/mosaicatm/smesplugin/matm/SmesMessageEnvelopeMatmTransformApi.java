package com.mosaicatm.smesplugin.matm;

import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.smes.transfer.SmesMessageTransfer;
import com.mosaicatm.smes.transfer.SmesMessageTransferEnvelope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mosaicatm.fuser.transform.api.FlightSourceTransformApiImpl;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

public class SmesMessageEnvelopeMatmTransformApi extends FlightSourceTransformApiImpl<SmesMessageTransferEnvelope> {

    private final Log log = LogFactory.getLog(getClass());

    private Transformer<MatmFlight, SmesMessageTransfer> singleToMatmTransform;

    public SmesMessageEnvelopeMatmTransformApi() {
        GenericMarshaller genericMarshaller = null;
        try {
            genericMarshaller = new GenericMarshaller(SmesMessageTransferEnvelope.class);
            genericMarshaller.setMarshallFormatted(false);
            genericMarshaller.setMarshallHeader(false);
        } catch (JAXBException jaxbEx) {
            log.error("Failed to setup GenericMarshaller", jaxbEx);
        }

        this.setMarshaller(genericMarshaller);
        this.setSingleToMatmTransform(new SmesMessageToMatmTransform());
    }

    @Override
    public List<MatmFlight> toMatm(SmesMessageTransferEnvelope batch) {
        List<MatmFlight> matm = new ArrayList<>();

        List<SmesMessageTransfer> positionMessages = fromBatch(batch);

        if (positionMessages != null && !positionMessages.isEmpty()) {
            for (SmesMessageTransfer positionMessage : positionMessages) {
                MatmFlight matmFlight = singleToMatmTransform.transform(positionMessage);
                if (matmFlight != null) {
                    matm.add(matmFlight);
                }
            }
        }

        return matm;
    }

    public List<SmesMessageTransfer> fromBatch(SmesMessageTransferEnvelope batch) {
        List<SmesMessageTransfer> rtnBatch = null;

        if (batch != null) {
            rtnBatch = batch.getSmesMessageTransferList();

            if (log.isDebugEnabled()) {
                log.debug("SMES message batch size: " + rtnBatch.size());
            }
        }

        return rtnBatch;
    }

    public Transformer<MatmFlight, SmesMessageTransfer> getSingleToMatmTransform() {
        return singleToMatmTransform;
    }

    public void setSingleToMatmTransform(Transformer<MatmFlight, SmesMessageTransfer> singleToMatmTransform) {
        this.singleToMatmTransform = singleToMatmTransform;
    }
}
