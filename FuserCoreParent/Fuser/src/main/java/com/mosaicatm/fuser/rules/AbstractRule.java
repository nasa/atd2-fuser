package com.mosaicatm.fuser.rules;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.aggregator.MatmProperties;
import com.mosaicatm.fuser.util.PropertyVisitor;
import com.mosaicatm.matmdata.common.MatmObject;
import com.mosaicatm.matmdata.common.MetaData;

public abstract class AbstractRule<T extends MatmObject> 
implements Rule<T>
{
    private static final Set<String> EXCLUDE_IGNORE_FIELDS;
    
    private final Log log = LogFactory.getLog(getClass());
    
    private PropertyVisitor propertyVisitor = new PropertyVisitor();

    static
    {
        EXCLUDE_IGNORE_FIELDS = new HashSet<>();
        EXCLUDE_IGNORE_FIELDS.add( MatmProperties.PROPERTY_TIMESTAMP );
        EXCLUDE_IGNORE_FIELDS.add( MatmProperties.PROPERTY_LAST_UPDATE_SOURCE );
        EXCLUDE_IGNORE_FIELDS.add( MatmProperties.PROPERTY_SYSTEM_ID );
    }

    private Set<String> includes = null;
    private Set<String> excludes = null;
    private String includesRegex = null;
    private String excludesRegex = null;
    private String name;
    private boolean active;
    private int priority;
    
    public AbstractRule()
    {     
    }
    
    @Override
    public boolean handleDifference(T update, T target, MetaData history, String field) 
    {
        if (!active)
            return true;
        
        if (ruleAppliesToField(field))
        {
            return handleDifferences(update, target, history, field);
        }
        
        return true;
    }
    
    @Override
    public boolean handleIdentical(T update, T target, MetaData history, String field)
    {
        if (!active)
            return true;
        
        if (ruleAppliesToField(field))
        {
            return handleIdenticals(update, target, history, field);
        }
        
        return true;
    }

    /**
     * Checks to see if a specific field in an update should be filtered out 
     * based on a set of criteria, normally involving the update source and the 
     * source of the existing value. If the field should be filtered out, it will
     * be nulled out in the update.
     * 
     * @param update   the update from an external source
     * @param target   the existing flight object that will be updated
     * @param history  the source for the existing field
     * @param field    the field being checked
     * 
     * @return true if the field was filtered out, false if the field was left untouched
     *         in the update
     */
    protected abstract boolean handleDifferences(T update, T target, MetaData history, String field);
    
    /**
     * The purpose of this method is to maintain 
     * the metaData store and keep it up to date if the update and target
     * messages have the same value requiring update to the MetaData store
     * with corresponding meta data
     * 
     * @param update   the update from an external source
     * @param target   the existing flight object that will be updated
     * @param history  the source for the existing field
     * @param field    the field being checked
     * 
     * @return true if the field is favorable, false if the field is unfavorable
     */
    protected abstract boolean handleIdenticals(T update, T target, MetaData history, String field);

    @Override
    public void filterUpdateField( T update, String field )
    {
        try
        {
            if( field.endsWith( "." + PropertyVisitor.JAXB_ELEMENT_PROPERTY_NAME ))
            {
                try
                {
                    String test_jaxb_parent = field.substring( 0, field.length() - 
                            PropertyVisitor.JAXB_ELEMENT_PROPERTY_NAME.length() - 1 );
                    
                    Object parent_property = PropertyUtils.getProperty( update, test_jaxb_parent );
                    if(( parent_property != null ) && 
                            ( parent_property instanceof JAXBElement ) && 
                            propertyVisitor.isSimpleType(((JAXBElement<?>) parent_property ).getDeclaredType() ))
                    {
                        field = test_jaxb_parent;
                    }                    
                }
                catch( IllegalAccessException | InvocationTargetException | NoSuchMethodException e )
                {
                    log.warn( "Exception while processing field: " + field, e );
                }
            }

            PropertyUtils.setProperty( update, field, null );
        }
        catch( Exception e )
        {
            log.error( "fail to set field " + field + " to null" );
        }
    }

    @Override
    public boolean validateRuleFields() 
    { 
        boolean valid = true;
        
        if( getRuleClassType() == null )
        {
            log.error( "RuleClassType is NULL!" );
            valid = false;
        }
        else
        {
            if((( includes != null ) || ( excludes != null ) || 
                  ( includesRegex != null ) || ( excludesRegex != null )))
            {
                ArrayList<String> fields = new ArrayList<>();

                if( includes != null )
                {
                    fields.addAll( includes );
                }            
                if( excludes != null )
                {
                    fields.addAll( excludes );
                }                             

                Set<String> possibleObjectFieldNames = null;

                try
                {
                    possibleObjectFieldNames = propertyVisitor.getAllFieldNames( getRuleClassType() );
                }
                catch( IllegalAccessException | InvocationTargetException | NoSuchMethodException ex )
                {
                    log.error( "Error deterimining possible field names for: " + getRuleClassType() );
                }

                if( possibleObjectFieldNames != null )
                {
                    for( String field : fields )
                    {    
                        if( !possibleObjectFieldNames.contains( field ))
                        {
                            String errorMsg = "Property [" + field + "] is not an allowable rule field for type " + getRuleClassType();

                            if( possibleObjectFieldNames.contains( field + ".value" ))
                            {
                                errorMsg = errorMsg + ". This property requires adding \".value\"";
                            }

                            log.error( errorMsg );
                            valid = false;
                        }
                    }

                    if(( includesRegex != null ) || ( excludesRegex != null ))
                    {
                        boolean foundIncludeRegex = ( includesRegex == null );
                        boolean foundExcludeRegex = ( excludesRegex == null );

                        for( String possibleField : possibleObjectFieldNames )
                        {
                            if( !foundIncludeRegex )
                            {
                                foundIncludeRegex = possibleField.matches( includesRegex );
                            }
                            if( !foundExcludeRegex )
                            {
                                foundExcludeRegex = possibleField.matches( excludesRegex );
                            }

                            if( foundIncludeRegex && foundExcludeRegex )
                            {
                                break;
                            }
                        }

                        if( !foundIncludeRegex )
                        {
                            log.error( "Include regex [" + includesRegex + "] does not match any rule field for type " + getRuleClassType() );
                            valid = false;
                        }
                        if( !foundExcludeRegex )
                        {
                            log.error( "Exclude regex [" + excludesRegex + "] does not match any rule field for type " + getRuleClassType() );
                            valid = false;
                        }                    
                    }
                }
            }
        }
        
        return( valid );
    }     
    
    @Override
    public void setName( String name )
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean ruleAppliesToField( String field )
    {
        if( active )
        {
            if( testInclusionSet( field ) || testInclusionRegex( field ))
            {
                return ( true );
            }

            //If both exclusion rules are being applied, require both tests
            if(( excludes != null ) && ( excludesRegex != null ))
            {
                if( testExclusionSet( field ) && testExclusionRegex( field ))
                {
                    return ( true );
                }
            }
            else if( testExclusionSet( field ) || testExclusionRegex( field ))
            {
                return ( true );
            }
        }

        return ( false );
    }

    @Override
    public Set<String> getIncludes()
    {
        return includes;
    }

    @Override
    public void setIncludes( Collection<String> includes )
    {
        if(( includes != null ) && !includes.isEmpty() )
        {
            this.includes = new HashSet<>();
            for( String include : includes )
            {
                addInclude( include );
            }
        }
        else
        {
            this.includes = null;
        }
    }

    @Override
    public String getIncludesRegex()
    {
        return includesRegex;
    }

    @Override
    public void setIncludesRegex( String includesRegex )
    {
        if(( includesRegex != null ) && !includesRegex.trim().isEmpty() )
        {
            this.includesRegex = includesRegex.trim();
        }
        else
        {
            this.includesRegex = null;
        }        
    }

    @Override
    public Set<String> getExcludes()
    {
        return excludes;
    }

    @Override
    public void setExcludes( Collection<String> excludes )
    {
        if(( excludes != null ) && !excludes.isEmpty() )
        {
            this.excludes = new HashSet<>();
            for( String exclude : excludes )
            {                
                addExclude( exclude );
            }
        }
        else
        {
            this.excludes = null;
        }
    }

    @Override
    public String getExcludesRegex()
    {
        return excludesRegex;
    }

    @Override
    public void setExcludesRegex( String excludesRegex )
    {
        if(( excludesRegex != null ) && !excludesRegex.trim().isEmpty() )
        {
            this.excludesRegex = excludesRegex.trim();
        }
        else
        {
            this.excludesRegex = null;
        }
    }

    @Override
    public void setActive( boolean active )
    {
        this.active = active;
    }

    @Override
    public boolean isActive()
    {
        return this.active;
    }

    @Override
    public int getPriority()
    {
        return priority;
    }

    @Override
    public void setPriority( int priority )
    {
        this.priority = priority;
    }  
    
    private boolean addInclude( String include )
    {
        if(( include != null ) && ( !include.trim().isEmpty() ))
        {
            include = include.trim();
            
            return includes.add( include );
        }
        else
        {
            return false;
        }
    }
    
    private boolean addExclude( String exclude )
    {
        if(( exclude != null ) && ( !exclude.trim().isEmpty() ))
        {
            exclude = exclude.trim();
            
            if( EXCLUDE_IGNORE_FIELDS.contains( exclude ))
            {
                log.warn( "Exclude filter not required, field already excluded by default: " + exclude );
                return ( false );
            }
            return excludes.add( exclude );
        }
        else
        {
            return( false );
        }
    }    
    
    private boolean testInclusionSet( String field )
    {
        if( field != null )
        {
            return (( includes != null )
                    && ( includes.contains( field )));
        }
        else
        {
            return( false );
        }
    }

    private boolean testInclusionRegex( String field )
    {
        if( field != null )
        {        
            return (( includesRegex != null )
                && ( field.matches( includesRegex )));
        }
        else
        {
            return( false );
        }        
    }

    private boolean testExclusionSet( String field )
    {
        if( field != null )
        {
            return (( excludes != null )
                && ( !excludes.contains( field ) &&  !EXCLUDE_IGNORE_FIELDS.contains( field )));
        }
        else
        {
            return( false );
        }
    }

    private boolean testExclusionRegex( String field )
    {
        if( field != null )
        {        
            return (( excludesRegex != null )
                && ( !field.matches( excludesRegex ) &&  ! EXCLUDE_IGNORE_FIELDS.contains( field )));
        }
        else
        {
            return( false );
        }
    } 
}
