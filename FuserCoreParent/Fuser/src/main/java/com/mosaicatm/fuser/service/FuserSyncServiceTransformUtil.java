package com.mosaicatm.fuser.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.istack.ByteArrayDataSource;

import com.mosaicatm.lib.jaxb.GenericMarshaller;
import com.mosaicatm.matmdata.envelope.MatmTransferEnvelope;


/**
 * This class contains helper methods to transform data for WebService requests.
 */
public class FuserSyncServiceTransformUtil
{
    private static final Logger logger = LoggerFactory.getLogger( FuserSyncServiceTransformUtil.class );

    private static final int ZIP_BUFFER_SIZE = 8_000;

    /**
     * Method to transform data in a {@link MatmTransferEnvelope} into a compressed
     * byte array.
     * 
     * @param envelope The {@link MatmTransferEnvelope} object
     * @param matmEnvelopeMarshaller    The {@link GenericMarshaller} for the {@link MatmTransferEnvelope}
     * @return  A DataHandler that JAXWS will add as an attachment to the SOAP message.
     */
    public DataHandler matmTransferEnvelopeToCompressedByteData(
            MatmTransferEnvelope envelope,
            GenericMarshaller matmEnvelopeMarshaller )
    {
        DataHandler dataHandler = null;

        try( ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                GZIPOutputStream zipOutStream = new GZIPOutputStream( byteOutputStream, ZIP_BUFFER_SIZE ); )
        {
            long startMarshalling = System.currentTimeMillis();

            Marshaller marshaller = matmEnvelopeMarshaller.getMarshaller();
            synchronized( matmEnvelopeMarshaller )
            {
                marshaller.marshal( envelope, zipOutStream );
            }

            if( logger.isDebugEnabled() )
            {
                long marshallDuration = System.currentTimeMillis() - startMarshalling;
                logger.debug( "time to marshall: " + marshallDuration + " ms" );
            }

            zipOutStream.finish();
            byteOutputStream.flush();

            ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource( byteOutputStream.toByteArray(), "application/octet-stream" );
            dataHandler = new DataHandler( byteArrayDataSource );
        }
        catch( IOException | JAXBException e )
        {
            logger.error( "Failed to marshal MatmTransferEnvelope", e );
        }
        
        return dataHandler;
    }
}
