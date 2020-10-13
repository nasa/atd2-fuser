package com.mosaicatm.fuser.aggregator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.JAXBElement;

import com.mosaicatm.fuser.util.PropertyVisitor;

public class MatmObjectDiff 
extends PropertyVisitor
{    
    /**
     * <p>Generate a list of differences and similarities between two objects,
     * Each field is compared by comparing its Data type.<p>
     * <p>For instance: If fieldA in the update object is different from fieldA in target object</p>
     * <p>Recursively compare the nested fields within fieldA between two objects
     * Unless fieldA is an simple Type or a JAXBElement.
     * The determination of a simple Type is in isSimpleType method</p>
     * <p>JAXBElement is generated from xml binding that needs to be handled a bit differently.
     * When a JAXBElement is detected, the content must be retrieved by calling the value method
     * instead of working directly on the JAXBElement itself. Otherwise, it won't work.
     * Also, Since JAXBElement is designed for full replacement or has nillable ability,
     * its nested fields won't be compared.</p>
     * <p>If the field has different value, a string dot notation of the field will be added to the changes list.
     * If the field has same value, a string bean dot notation of the field will be added to the identicals list.<p>
     * Sample string dot notation: extensions.derivedExtensions.fieldA
     * @param update the update object containing updates for comparison
     * @param target the target object that is being used for modeling
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public AggregationResult compareObjects ( Object update, Object target )
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return compareObjectsHelper( update, target, new AggregationResult(), "" );
    }
    
    private AggregationResult compareObjectsHelper( Object update, Object target, AggregationResult result, String parent )
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        if( target == null )
        {
            if (!parent.isEmpty())
            {
                addChildren( update, result.getChanges(), parent );
            }
            
            return result;
        }
        
        List<Field> fields = getObjectFields(update);
        
        for( Field propNameObject : fields )
        {
            propNameObject.setAccessible(true);
            
            String propertyName = propNameObject.getName();
            if( !propertyName.equals( "serialVersionUID" ))
            {                
                boolean isJAXBElement = false;
                boolean isUpdateNill = false;
                
                Object updateProperty = propNameObject.get(update);
                Object targetProperty = propNameObject.get(target);

                if( updateProperty != null && updateProperty instanceof JAXBElement)
                {
                    JAXBElement<?> updateJAXB = ((JAXBElement<?>)updateProperty);
                    
                    isJAXBElement = true;
                    isUpdateNill = updateJAXB.isNil();
                    propertyName += "." + JAXB_ELEMENT_PROPERTY_NAME;
                    updateProperty = updateJAXB.getValue();
                    if( targetProperty != null )
                    {
                        targetProperty = ((JAXBElement<?>)targetProperty).getValue();
                    }
                }
                
                if (isUpdateNill || updateProperty != null)
                {
                    String path = (parent.isEmpty() ? propertyName : parent + "." + propertyName);
                    
                    if( isUpdateNill || !Objects.equals( updateProperty, targetProperty ))
                    {
                        if ( isJAXBElement || isSimpleType(updateProperty) )
                        {
                            result.addChange( path );
                        }
                        else
                        {
                            compareObjectsHelper(updateProperty, targetProperty, result, path);
                        }
                    }
                    else
                    {
                        if (isJAXBElement || isSimpleType(updateProperty))
                        {
                            result.addIdentical(path);
                        }
                        else
                        {
                            addChildren(updateProperty, result.getIdenticals(), path);
                        }
                    }
                }
            }
        }

        return result;
    }
}
