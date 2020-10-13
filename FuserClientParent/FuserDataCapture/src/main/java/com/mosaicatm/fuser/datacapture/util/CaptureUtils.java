package com.mosaicatm.fuser.datacapture.util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mosaicatm.matmdata.common.MetaData;
import com.mosaicatm.matmdata.flight.extension.IdacExtension.ArtccSchedulingList.ArtccSchedulingData;
import com.mosaicatm.matmdata.flight.extension.IdacExtension.FlowAssignmentList.FlowAssignmentData;

public class CaptureUtils
{
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    // Delimiter between different elements of an object when writing the 
    // object as a string.
    private static final String INTERNAL_DELIMITER = " ";

    // Default value when an element of an object is null or empty when writing
    // the object as a string.
    private static final String DEFAULT_VALUE = "NULL";
    
    private static final Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
    
    // Json components
    // json quote " must be replace with """
    private static final String JSON_QUOTE = "\"";
    private static final String JSON_ESCAPE_QUOTE = "\"\"\"";
    private static final Pattern quotePattern = Pattern.compile(JSON_QUOTE);
    private static final Pattern backwardQuotePattern = Pattern.compile(JSON_ESCAPE_QUOTE);
    
    // json commans ,  must be replace with "," .
    // Also allow a white space between any text and the commas
    // otherwise not working
    private static final String JSON_COMMAS = ",";    
    private static final String JSON_ESCAPE_COMMAS = " \",\" ";
    private static final Pattern commasPattern = Pattern.compile(JSON_COMMAS);
    private static final Pattern backwardCommasPattern = Pattern.compile(JSON_ESCAPE_COMMAS);
    public static String listAsString (List<?> list, String delimiter)
    {
        StringBuilder sb = null;
        
        if (list != null)
        {
            sb = new StringBuilder ();
            
            Iterator<?> it = list.iterator();
            Object data = null;
            
            while (it.hasNext())
            {
            	data = it.next();
            	
            	if (data == null)
                	continue;
            	
                sb.append(data.toString());
                
                if (it.hasNext())
                    sb.append(delimiter);
            }
        }
        else
        {
        	return null;
        }
        
        return (sb.length() > 0 ? sb.toString() : null);
    }
    
    public static String metaDataAsString(List<MetaData> list, String delimiter)
    {
    	StringBuilder sb = null;
        Format formatter = new SimpleDateFormat(DATE_FORMAT);
        
        if (list != null)
        {
            sb = new StringBuilder ();
            
            Iterator<MetaData> it = list.iterator();
            MetaData data = null;
            
            while (it.hasNext())
            {
            	data = it.next();
            	
            	if (data == null)
                	continue;
            	
                sb.append(data.getSource());
                if (data.getSystemType() != null)
                	sb.append("_" + data.getSystemType());
                
                if (data.getTimestamp() != null)
                	sb.append("_" + formatter.format(data.getTimestamp()));
                
                if (it.hasNext())
                    sb.append(delimiter);
            }
        }
        else
        {
        	return null;
        }
        
        return (sb.length() > 0 ? sb.toString() : null);
    }
    
    public static Long durationToMillis (Duration duration)
    {
        return durationToMillis(duration, null);
    }
    
    public static Long durationToMillis (Duration duration, Date date)
    {
        Long millis = null;
        
        if (duration != null)
        {
            if (date == null)
                date = new Date(0);
            
            millis = duration.getTimeInMillis(date);
        }
        
        return millis;
    }

	public static String artccSchedulingListAsString(List<ArtccSchedulingData> list, String delimiter)
	{
    	StringBuilder sb = null;
        
        if (list != null)
        {
            sb = new StringBuilder ();
            
            Iterator<ArtccSchedulingData> it = list.iterator();
            ArtccSchedulingData data = null;
            
            while (it.hasNext())
            {
            	data = it.next();
            	
            	if (data == null)
                	continue;
            	
                if (data.getArtccId() != null)
                	sb.append(data.getArtccId());
                
                if (data.getTmaId() != null)
                {
                	if (sb.length() > 0 )
                		sb.append("_");
                	
            		sb.append(data.getTmaId());
                }
                if (data.getTraconGroup() != null)
                {
                	if (sb.length() > 0)
                		sb.append("_");
                	
                	sb.append(data.getTraconGroup());
                }
                
                if (it.hasNext())
                    sb.append(delimiter);
            }
        }
        else
        {
        	return null;
        }
        
        return (sb.length() > 0 ? sb.toString() : null);
	}

    public static void assignValueToStringBuilder(StringBuilder sb, String value)
    {
        if (sb.length() > 0)
        {
            sb.append(INTERNAL_DELIMITER);
        }
        if (value != null && !value.isEmpty()) 
        {
            sb.append(value);
        }
        else
        {
            sb.append(DEFAULT_VALUE);
        }
    }

    public static String flowAssignmentListAsString(List<FlowAssignmentData> list, String delimiter)
    {
        StringBuilder sb = null;
        Format formatter = new SimpleDateFormat(DATE_FORMAT);
        
        if (list != null)
        {
            sb = new StringBuilder ();
            Iterator<FlowAssignmentData> it = list.iterator();
            FlowAssignmentData data = null;
            
            while (it.hasNext())
            {
                data = it.next();
                
                if (data == null)
                	continue;
                
                if (data.getSuperStreamClassIdentifierId() != null)
                {
                    assignValueToStringBuilder(sb, data.getSuperStreamClassIdentifierId().toString());
                }
                else
                {
                    assignValueToStringBuilder(sb, null);
                }
                
                assignValueToStringBuilder(sb, data.getSuperStreamClassIdentifierName());
                assignValueToStringBuilder(sb, data.getSuperStreamClassIdentifierFacility());
                assignValueToStringBuilder(sb, data.getSuperStreamClassIdentifierTracon());
                assignValueToStringBuilder(sb, data.getCsp());
                
                if (data.getApreqMode() != null)
                {
                    assignValueToStringBuilder(sb, data.getApreqMode().value());
                }
                else
                {
                    assignValueToStringBuilder(sb, null);
                }
                
                if (data.getActivationTime() != null)
                {
                    assignValueToStringBuilder(sb, formatter.format(data.getActivationTime()));
                }
                else
                {
                    assignValueToStringBuilder(sb, null);
                }
                
                if (it.hasNext())
                    sb.append(delimiter);
            }
        }
        else
        {
        	return null;
        }

        return (sb.length() > 0 ? sb.toString() : null);
    }
    
    public static final String toBulkCopyJson(Object object)
    {
        if (object == null)
            return null;
        
        String json = gson.toJson(object);
        
        if (json == null || json.isEmpty())
            return null;
        
        Matcher matcher = quotePattern.matcher(json);
        
        if (matcher.find())
        {
            json = matcher.replaceAll(JSON_ESCAPE_QUOTE);
        }
        
        matcher = commasPattern.matcher(json);
        
        if (matcher.find())
        {
            json = matcher.replaceAll(JSON_ESCAPE_COMMAS);
        }
        
        return json;
    }
    
    public static final String fromBulkCopyJsonToJson(String bulkCopyJson)
    {
        if (bulkCopyJson == null)
            return null;

        String json = bulkCopyJson;
        Matcher matcher = backwardQuotePattern.matcher(json);
        
        if (matcher.find())
        {
            json = matcher.replaceAll(JSON_QUOTE);
        }
        
        matcher = backwardCommasPattern.matcher(json);
        
        if (matcher.find())
        {
            json = matcher.replaceAll(JSON_COMMAS);
        }
        return json;
    }
}
