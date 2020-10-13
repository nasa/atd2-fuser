package com.mosaicatm.fuser.transform.api;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.lib.jaxb.GenericMarshaller;

public class AbstractSourceTransformApi<E, F>
	implements SourceTransformApi<E, F>
{
    private final Log log = LogFactory.getLog(getClass());

    private Transformer <F, E> toMatmTransform;
    private GenericMarshaller marshaller;
    
    
    @Override
    public List<F> toMatm (E flight)
    {
        F matm = null;
        
        if (isToMatmTransformInitialized())
            matm = toMatmTransform.transform(flight);
        
        return Arrays.asList( matm );
    }
    
    @Override
    public E fromXml (String xml)
    {
        E data = null;
        
        if (marshaller != null)
        {
            try
            {
                data = marshaller.unmarshallToObject(xml);
            }
            catch (Exception e)
            {
                log.error (e.getMessage(), e);
            }
        }
        
        return data;
    }
    
    private boolean isToMatmTransformInitialized ()
    {
        if (toMatmTransform == null)
        {
            log.warn ("MATM to transform is not initialized");
            return false;
        }
        
        return true;
    }

    public Transformer<F, E> getToMatmTransform() {
        return toMatmTransform;
    }


    public void setToMatmTransform(Transformer<F, E> toMatmTransform) {
        this.toMatmTransform = toMatmTransform;
    }


    public GenericMarshaller getMarshaller() {
        return marshaller;
    }


    public void setMarshaller(GenericMarshaller marshaller) {
        this.marshaller = marshaller;
    }
        
}
