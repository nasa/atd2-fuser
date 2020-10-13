package com.mosaicatm.fuser.transform.matm;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.transform.FuserAirportSpecificStateDependentTransformer;
import com.mosaicatm.fuser.transform.FuserAirportSpecificTransformer;
import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.lib.jaxb.GenericMarshaller;

public class SourceMatmTransformApiImpl<E,K>
implements SourceMatmTransformApi<E,K>
{
    private final Log log = LogFactory.getLog(getClass());
    
    private Transformer <E, K> fromMatmTransform;
    private Transformer <K, E> toMatmTransform;
    private GenericMarshaller marshaller;
    
    @Override
    public E fromMatm (K matm)
    {
        E flight = null;
        
        if (isFromMatmTransformInitialized())
            flight = fromMatmTransform.transform(matm);
        
        return flight;
    }
    
    
    @Override
    public K toMatm (E flight)
    {
        K matm = null;
        
        if (isToMatmTransformInitialized())
            matm = toMatmTransform.transform(flight);
                
        return matm;
    }


    @Override
    public E fromMatm(K matm, String airport) {
        E flight = null;
        
        if (isFromMatmTransformInitialized()){
            if (fromMatmTransform instanceof FuserAirportSpecificTransformer<?,?>){
                flight = ((FuserAirportSpecificTransformer<E,K>)fromMatmTransform).transform(matm, airport);
            }
            else flight = fromMatm(matm);
        }
        return flight;
    }
    
    @Override
    public E fromMatm(K matm, List<String> airports) {
        E flight = null;
        
        if (isFromMatmTransformInitialized()){
            if (fromMatmTransform instanceof FuserAirportSpecificTransformer<?,?>){
                flight = ((FuserAirportSpecificTransformer<E,K>)fromMatmTransform).transform(matm, airports);
            }
            else flight = fromMatm(matm);
        }
        return flight;
    }


    @Override
    public K toMatm(E flight, String airport) {
        K matm = null;
        
        if (isToMatmTransformInitialized()){
            if (toMatmTransform instanceof FuserAirportSpecificTransformer<?,?>){
                matm = ((FuserAirportSpecificTransformer<K,E>)toMatmTransform).transform(flight, airport);
            }
            else matm = toMatm(flight);
        }
        return matm;
    }
    
    @Override
    public E fromMatm(K fullStateMatm, K matm, String airport) {
        E flight = null;
        
        if (isFromMatmTransformInitialized()){
            if (fromMatmTransform instanceof FuserAirportSpecificStateDependentTransformer<?,?>){
                flight = ((FuserAirportSpecificStateDependentTransformer<E,K>)fromMatmTransform).
                        transform(fullStateMatm, matm, airport);
            }
            else flight = fromMatm(matm, airport);
        }
        return flight;
    }
    
    @Override
    public E fromMatm(K fullStateMatm, K matm, List<String> airports) {
        E flight = null;
        
        if (isFromMatmTransformInitialized()){
            if (fromMatmTransform instanceof FuserAirportSpecificStateDependentTransformer<?,?>){
                flight = ((FuserAirportSpecificStateDependentTransformer<E,K>)fromMatmTransform).
                        transform(fullStateMatm, matm, airports);
            }
            else flight = fromMatm(matm, airports);
        }
        return flight;
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
                e.printStackTrace();
                log.error (e.getMessage(), e);
            }
        }
        
        return data;
    }

    @Override
    public String toXml (E flight)
    {
        String xml = null;

        if (marshaller != null) {
            try {
                xml = marshaller.marshall(flight);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return xml;
        
    }
    
    
    private boolean isFromMatmTransformInitialized ()
    {
        if (fromMatmTransform == null)
        {
            log.warn ("From MATM transform is not initialized");
            return false;
        }
        
        return true;
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


    public Transformer<E, K> getFromMatmTransform() {
        return fromMatmTransform;
    }


    public void setFromMatmTransform(Transformer<E, K> fromMatmTransform) {
        this.fromMatmTransform = fromMatmTransform;
    }


    public Transformer<K, E> getToMatmTransform() {
        return toMatmTransform;
    }


    public void setToMatmTransform(Transformer<K, E> toMatmTransform) {
        this.toMatmTransform = toMatmTransform;
    }

    public FuserAirportSpecificTransformer<E, K> getFromMatmAirportSpecificTransform() {
        Transformer<E,K> t = getFromMatmTransform();
        if (t != null && t instanceof FuserAirportSpecificTransformer<?,?>){
            return (FuserAirportSpecificTransformer<E,K>) t;
        }
        return null;
    }
    
    public FuserAirportSpecificStateDependentTransformer<E, K> getFromMatmAirportSpecificStateDependentTransform() {
        Transformer<E,K> t = getFromMatmTransform();
        if (t != null && t instanceof FuserAirportSpecificStateDependentTransformer<?,?>){
            return (FuserAirportSpecificStateDependentTransformer<E,K>) t;
        }
        return null;
    }    

    public FuserAirportSpecificTransformer<K, E> getToMatmAirportSpecificTransform() {
        Transformer<K,E> t = getToMatmTransform();
        if (t != null && t instanceof FuserAirportSpecificTransformer<?,?>){
            return (FuserAirportSpecificTransformer<K,E>) t;
        }
        return null;
    }


    public GenericMarshaller getMarshaller() {
        return marshaller;
    }


    public void setMarshaller(GenericMarshaller marshaller) {
        this.marshaller = marshaller;
    }
 
}
