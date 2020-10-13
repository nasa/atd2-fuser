
package com.mosaicatm.fuser.filter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.xml.datatype.Duration;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mosaicatm.fuser.util.PropertyVisitor;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.FuserSource;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class AttributeFilter
    implements MatmFilter<MatmFlight>
{
    
    private final Log log = LogFactory.getLog(getClass());

    private final static String DEFAULT = "Default";
    private final static String LOCATION = "Location";
    private final static String AIRPORTNAME = "AirportName";
    private final static String ATTRIBUTEPROPERTYLIST = "AttributePropertyList";
    private boolean active;
    private Map<String, AirportAttributeLists> airportArrivalAttributeLists = new ConcurrentHashMap<String, AirportAttributeLists>();
    private Map<String, AirportAttributeLists> airportDepartureAttributeLists = new ConcurrentHashMap<String, AirportAttributeLists>();
    private String defaultLocation;
    private String attributeMappings;
    private PropertyVisitor propertyVisitor = new PropertyVisitor();
    
    public void init()
    {
        if (active)
        {
            JsonArray airportsXmlLocationJsonArray = null;
            if(attributeMappings != null && attributeMappings.trim().length() > 0)
            {
                try(BufferedReader bufferedReader = new BufferedReader(new FileReader(attributeMappings)))
                {
                    Gson gson = new Gson();
                    JsonElement element = gson.fromJson(bufferedReader, JsonElement.class);
                    JsonObject jsonObject = element.getAsJsonObject();
                    JsonElement attributePropertyList = null;
                    if(jsonObject != null)
                        attributePropertyList = jsonObject.get(ATTRIBUTEPROPERTYLIST);
                    if(attributePropertyList != null)
                        airportsXmlLocationJsonArray = attributePropertyList.getAsJsonArray();
                }
                catch (IOException e)
                {
                    log.error(
                        "Failed to read attribute filter mappings from file " +
                                        attributeMappings + " " + e.getMessage());
                }    
            }
            loadXmlIntoMap(defaultLocation, DEFAULT);
            if(airportsXmlLocationJsonArray != null)
            {
                for (int i = 0; i < airportsXmlLocationJsonArray.size(); i++)
                {
                    if(airportsXmlLocationJsonArray.get(i) != null)
                    {
                        JsonElement item = airportsXmlLocationJsonArray.get(i);
                        JsonObject itemObject = null;
                        if(item != null)
                            itemObject = item.getAsJsonObject();
                        if(itemObject != null)
                        {
                            String location = null;
                            JsonElement locationElement = itemObject.get(LOCATION);
                            if(locationElement != null)
                                location = locationElement.getAsString();

                            String airportName = null;
                            JsonElement airportElement = itemObject.get(AIRPORTNAME);
                            if(airportElement != null)
                                airportName = airportElement.getAsString();
                            
                            if(location != null)
                                location = location.trim();
                            if(airportName != null)
                                airportName = airportName.trim();
                            loadXmlIntoMap(location, airportName);   
                        }
                    }
                }
            }
        }
    }

    private void loadXmlIntoMap(String xmlLocation, String airport)
    {
        if(xmlLocation != null && airport != null)
        {
            XmlLoader loader = new XmlLoader();
            loader.init(xmlLocation);
            AirportAttributeLists arrivalAttributeFilter = new AirportAttributeLists(airport);
            arrivalAttributeFilter
                            .setIncludes(loader.getIncludesArrival());
            arrivalAttributeFilter
                            .setExcludes(loader.getExcludesArrival());
            airportArrivalAttributeLists.put(airport,
                arrivalAttributeFilter);

            AirportAttributeLists departureAttributeFilter = new AirportAttributeLists(airport);
            departureAttributeFilter
                            .setIncludes(loader.getIncludesDeparture());
            departureAttributeFilter
                            .setExcludes(loader.getExcludesDeparture());
            airportDepartureAttributeLists.put(airport,
                departureAttributeFilter);   
        }
    }

    @Override
    public MatmFlight filter(MatmFlight flight)
    {
        if (isActive() && flight != null)
        {
            //find airports and use them to get airport attribute lists
            //Example dep CLT arr DAL
            //get CLTDepLists and DALArrLists
            String arrAirport = findAirport(flight, (f)->f.getArrivalAerodrome());
            String depAirport = findAirport(flight, (f)->f.getDepartureAerodrome());
            AirportAttributeLists arrivalAttributeLists = findFilter(
                airportArrivalAttributeLists, arrAirport);
            AirportAttributeLists departureAttributeLists = findFilter(
                airportDepartureAttributeLists, depAirport);

            //put four list together 
            //now the includeList is combo if CLTDepLists.getIncludeList + DALArrLists.getIncludeList 
            List<String> includeList = mergeAttributeList(
                arrivalAttributeLists.getIncludeAttributesList(flight),
                departureAttributeLists.getIncludeAttributesList(flight));
            
            //Filter:
            try
            {
                boolean filtedArrival = filterExclude(flight, arrivalAttributeLists.getExcludeAttributesList(flight));
                boolean filtedDeparture = filterExclude(flight, departureAttributeLists.getExcludeAttributesList(flight));
                if (!filtedArrival && !filtedDeparture)
                {
                    if (!filterInclude(flight, includeList))
                        return flight;
                }
            }
            catch (Exception e)
            {
                log.error("Attribute filter failed for " + flight.getGufi(), e);
            }

            return flight;
        }

        return flight;
    }

    private List<String> mergeAttributeList(List<String> list1, List<String> list2)
    {
        List<String> attributeList = new ArrayList<String>();
        if(list1 != null)
            attributeList.addAll(list1);
        if (list2 != null)
        {
            for (String attribute : list2)
            {
                if (attribute != null && !attributeList.contains(attribute))
                    attributeList.add(attribute);
            }
        }
        return attributeList;
    }

    private AirportAttributeLists findFilter(Map<String, AirportAttributeLists> filterMap,
        String arrAirport)
    {
        AirportAttributeLists attributeFilter = null;
        if(arrAirport != null)
            attributeFilter = filterMap.get(arrAirport);
        //if not find, use the default one.
        if (attributeFilter == null)
        {
            attributeFilter = filterMap.get(DEFAULT);
            //if default is null create one.
            if(attributeFilter == null)
            {
                AirportAttributeLists defultAttributeFilter = new AirportAttributeLists(DEFAULT);
                filterMap.put(DEFAULT, defultAttributeFilter);
                attributeFilter = defultAttributeFilter;
            }
        }
        return attributeFilter;
    }

    private String findAirport(MatmFlight flight , Function<MatmFlight, Aerodrome> f)
    {
        String airport = null;
        Aerodrome aerodrome = f.apply(flight);
        
        if (aerodrome != null)
        {
            if (aerodrome.getIataName() != null)
            {
                airport = aerodrome.getIataName();
            }
            else if (aerodrome.getIcaoName() != null)
            {
                airport = aerodrome.getIcaoName().substring(1);
            }
        }
        return airport;
    }

    @Override
    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public void setAttributeMappings(String path)
    {
        this.attributeMappings = path;
    }

    public void setIncludesArrival(
        Map<FuserSource, List<String>> includesArrival)
    {
        setIncludesArrival(includesArrival, DEFAULT);
    }

    public void setIncludesArrival(
        Map<FuserSource, List<String>> includesArrival, String airport)
    {
        createFilterIfNotExist(airport);
        AirportAttributeLists arrivalAttributeFilter = findFilter(
            airportArrivalAttributeLists, airport);
        arrivalAttributeFilter.setIncludes(includesArrival);
    }

    public void setIncludesDeparture(
        Map<FuserSource, List<String>> includesDeparture)
    {
        setIncludesDeparture(includesDeparture, DEFAULT);
    }

    public void setIncludesDeparture(
        Map<FuserSource, List<String>> includesDeparture, String airport)
    {
        createFilterIfNotExist(airport);
        AirportAttributeLists departureAttributeFilter = findFilter(
            airportDepartureAttributeLists, airport);
        departureAttributeFilter.setIncludes(includesDeparture);
    }

    public void setExcludesArrival(
        Map<FuserSource, List<String>> excludesArrival)
    {
        setExcludesArrival(excludesArrival, DEFAULT);
    }

    public void setExcludesArrival(
        Map<FuserSource, List<String>> excludesArrival, String airport)
    {
        createFilterIfNotExist(airport);
        AirportAttributeLists arrivalAttributeFilter = findFilter(
            airportArrivalAttributeLists, airport);
        arrivalAttributeFilter.setExcludes(excludesArrival);
    }

    public void setExcludesDeparture(
        Map<FuserSource, List<String>> excludesDeparture)
    {
        setExcludesDeparture(excludesDeparture, DEFAULT);
    }

    public void setExcludesDeparture(
        Map<FuserSource, List<String>> excludesDeparture, String airport)
    {
        createFilterIfNotExist(airport);
        AirportAttributeLists departureAttributeFilter = findFilter(
            airportDepartureAttributeLists, airport);
        departureAttributeFilter.setExcludes(excludesDeparture);
    }
    
    private void createFilterIfNotExist(String airport)
    {
        AirportAttributeLists attributeFilter = new AirportAttributeLists(airport);
        if(!airportArrivalAttributeLists.containsKey(airport))
        {
            airportArrivalAttributeLists.put(airport, attributeFilter);
        }
        
        attributeFilter = new AirportAttributeLists(airport);
        if(!airportDepartureAttributeLists.containsKey(airport))
        {
            airportDepartureAttributeLists.put(airport, attributeFilter);
        }
        
    }

    public Map<String, AirportAttributeLists> getAirportArrivalAttributrFilters()
    {
        return airportArrivalAttributeLists;
    }

    public Map<String, AirportAttributeLists> getAirportDepartureAttributrFilters()
    {
        return airportDepartureAttributeLists;
    }
    
    public boolean filterInclude(MatmFlight flight, List<String> attributes)
        throws IllegalArgumentException,
            IllegalAccessException,
            InvocationTargetException,
            NoSuchMethodException
    {

        if (attributes == null)
        {
            return true;
        }

        List<String> accessible = new ArrayList<String>();
        for (String value : attributes)
        {
            try
            {
                PropertyUtils.getProperty(flight, value);
                accessible.add(value);
            }
            catch (Exception e)
            {
                if (log.isDebugEnabled())
                    log.debug("Unable to access " + value);
            }
        }
        if (accessible.size() > 0)
            return filterIncludeHelper(flight, accessible);

        return false;
    }
    
    private boolean filterIncludeHelper(Object ext, List<String> accessible) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return filterIncludeHelper("", ext, accessible);
    }

    private boolean filterIncludeHelper(String parent, Object ext, List<String> accessible) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        boolean isFilter = false;
        //go through each field within this extension
        //any field that has complex structure, recurse that field
        List<Field> fields = propertyVisitor.getObjectFields(ext);
        
        for (Field field : fields) {
            field.setAccessible(true);
            
            if (field.get(ext) != null && !field.getName().equals("serialVersionUID")) {
                String fieldName = field.getName();
                Object obj;
                try {
                    obj = PropertyUtils.getProperty(ext, fieldName);
                } catch (NoSuchMethodException e) {
                    obj = null;
                }
                String hierarchy = getPath(parent, fieldName);

                if (!isInclude(hierarchy, accessible) && obj != null) {
                    if (!isSimpleType(obj) && isExtensionIncluded(hierarchy, accessible)) {
                        if (filterIncludeHelper(hierarchy, obj, accessible)) {
                            isFilter = true;
                        }
                    }
                    else  {
                        if (log.isDebugEnabled())
                            log.debug("nulling out " + fieldName);
                        
                        PropertyUtils.setProperty(ext, fieldName, null);
                        isFilter = true;
                    }
                }
            }
            
        }
        return isFilter;
    }

    private String getPath(String parent, String fieldName) {
        String hierarchy = parent;
        if (hierarchy.length() > 0) {
            hierarchy += "." + fieldName;
        } else {
            hierarchy = fieldName;
        }
        return hierarchy;
    }

    private boolean isExtensionIncluded(String hierarchy, List<String> accessible) {
        for (String value : accessible) {
            if (value.contains(hierarchy))
                return true;
        }
        return false;
    }

    private boolean isSimpleType(Object ext) {
        if (ext instanceof String || ext instanceof Double || ext instanceof Date
                || ext instanceof Enum || ext instanceof Duration || ext instanceof Long 
                || ext instanceof Integer || ext instanceof Float)
            return true;
        
        return false;
    }

    private boolean isInclude(String name, List<String> accessible) {
        if(accessible != null)
        {
            for (String value : accessible) {
                if (value.equals(name))
                    return true;
            }   
        }
        return false;
    }

    public boolean filterExclude(MatmFlight flight, List<String> attributes) {
        boolean isFiltered = false;
        if(attributes != null)
        {
            for (String value : attributes) {
                try {
                    PropertyUtils.setProperty(flight, value, null);
                    isFiltered = true;
                } catch (Exception e) {
                    if (log.isDebugEnabled())
                        log.debug("Unable to access " + value);
                }
            } 
        }
        return isFiltered;
    }

    public void setDefaultLocation(String defaultLocation)
    {
        this.defaultLocation = defaultLocation;
    }
}