package com.mosaicatm.fuser.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.Duration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.beanutils.PropertyUtils;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class PropertyVisitor
{
    public static final String JAXB_ELEMENT_PROPERTY_NAME = "value";
    
    private static final Log LOG = LogFactory.getLog(PropertyVisitor.class);
    
    private Map<String, Boolean> booleanFieldTypeMap = new ConcurrentHashMap<>();
    
    
    public boolean isSimpleType(Class<?> ext)
    {
        return ext.equals( String.class ) || ext.equals( Double.class ) 
                || ext.equals( Date.class ) || ext.equals( Boolean.class )
                || ext.equals( Enum.class ) || ext.isEnum()
                || ext.equals( Integer.class )
                || ext.equals( Long.class ) || ext.equals( Position.class )
                || ext.equals( Duration.class ) || ext.equals( Float.class )
                || ext.equals( List.class );
    }
    
    public boolean isSimpleType(Object ext)
    {
        return ext instanceof String || ext instanceof Double || ext instanceof Date || ext instanceof Boolean
                || ext instanceof Enum || ext instanceof Integer || ext instanceof Long || ext instanceof Position
                || ext instanceof Duration || ext instanceof Float || ext instanceof List;
    }

    public  boolean isMatmObject(Class clazz)
    {
        return clazz.isAssignableFrom(MatmObject.class) ||
               clazz.isAssignableFrom(MatmFlight.class) ||
               clazz.isAssignableFrom(MatmAircraft.class);
    }
    
    public  boolean isMatmObject(Object ext)
    {
        return ext instanceof MatmObject ||
               ext instanceof MatmFlight ||
               ext instanceof MatmAircraft;
    }
    
    public List<Field> getObjectFields(Class clazz)
    {
        List<Field> fieldList = new ArrayList<>();
        
        if (clazz != null)
        {
            if (isMatmObject(clazz))
            {
                fieldList.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
            }
            
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
        
        return fieldList;
    }    
    
    public List<Field> getObjectFields(Object update)
    {
        List<Field> fieldList = new ArrayList<>();
        
        if (update != null)
        {
            if (isMatmObject(update))
            {
                fieldList.addAll(Arrays.asList(update.getClass().getSuperclass().getDeclaredFields()));
            }
            
            fieldList.addAll(Arrays.asList(update.getClass().getDeclaredFields()));
        }
        
        return fieldList;
    }
    
    public Method getBooleanMethod (String fieldName, Object obj)
    {
        String methodName = "is" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
        
        try
        {
            return obj.getClass().getMethod(methodName);
        } 
        catch (Exception e)
        {
            LOG.error("no such getter method " + methodName + "() for field:" + fieldName);
        }
        
        return null;
    }
    
    public void addChildren(Object update, Set<String> result, String parent) 
                    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        if (update == null)
        {
            if ( !parent.isEmpty())
            {
                result.add(parent);
            }
            return;
        }
        
        List<Field> fields = getObjectFields(update);
        
        for (Field propNameObject : fields)
        {
            propNameObject.setAccessible( true );
            
            String propertyName = propNameObject.getName();
            boolean isJAXBElement = false;
            if (!propertyName.equals("serialVersionUID"))
            {
                Object updateProperty = propNameObject.get( update );
                if (updateProperty != null && updateProperty instanceof JAXBElement)
                {
                    isJAXBElement = true;
                    propertyName += "." + JAXB_ELEMENT_PROPERTY_NAME;
                    updateProperty = ((JAXBElement<?>)updateProperty).getValue();
                }

                if (isJAXBElement || updateProperty != null )
                {
                    String path = (parent.isEmpty() ? propertyName : parent + "." + propertyName);
                    if (isJAXBElement || isSimpleType(updateProperty))
                    {
                        result.add(path);
                    }
                    else
                    {
                        addChildren(updateProperty, result, path);
                    }
                }
            }    
        }
    }

    public Set<String> getAllFieldNames(Class classType) 
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        TreeSet<String> names = new TreeSet<>();
        getAllFieldNames( classType, names, "" );
        return( names );
    }
    
    private void getAllFieldNames(Class classType, Set<String> result, String parent) 
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        if (classType == null)
        {
            if ( !parent.isEmpty())
            {
                result.add(parent);
            }
            return;
        }
        
        List<Field> fields = getObjectFields(classType);
        
        for (Field propNameObject : fields)
        {
            String propertyName = propNameObject.getName();
            boolean isJAXBElement = false;
            if (!propertyName.equals("serialVersionUID"))
            {
                if (propNameObject.getType().isAssignableFrom(JAXBElement.class))
                {
                    isJAXBElement = true;
                    propertyName += "." + JAXB_ELEMENT_PROPERTY_NAME;
                }

                String path = (parent.isEmpty() ? propertyName : parent + "." + propertyName);
                if (isJAXBElement || isSimpleType(propNameObject.getType()))
                {
                    result.add(path);
                }
                else
                {
                    getAllFieldNames(propNameObject.getType(), result, path);
                }
            }    
        }
    }    
    
    /**
     * <p>Get the property/value from the object passed in</p>
     * @param object the working object
     * @param propertyName the field name in the dot notation ex: derivedExtension.derivedActualOutTime
     * @param isGetFromTarget true for get property from target object, otherwise from update object
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * 
     */
    public Object getProperty(Object object, String propertyName, String parent)
                    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IllegalArgumentException
    {
        Object property = null;
        
        String path = (parent.isEmpty() ? propertyName : parent + "." + propertyName);
        
        if (object != null)
        {
            Boolean isBooleanField = booleanFieldTypeMap.get( path );
            
            if (object instanceof JAXBElement)
            {
                property = PropertyUtils.getProperty( object, propertyName );
            }
            else if( Boolean.TRUE.equals( isBooleanField ) ||
                    (isBooleanField == null &&
                    PropertyUtils.getPropertyDescriptor( object, propertyName ).getPropertyType() == Boolean.class ) )
            {
                if( isBooleanField == null )
                {
                    booleanFieldTypeMap.put( path, Boolean.TRUE );
                }

                property = getBooleanMethod( propertyName, object ).invoke( object );
            }
            else
            {
                if( isBooleanField == null )
                {
                    booleanFieldTypeMap.put( path, Boolean.FALSE );
                }

                property = PropertyUtils.getProperty( object, propertyName );
            }
        }
        
        return property;
    }
}
