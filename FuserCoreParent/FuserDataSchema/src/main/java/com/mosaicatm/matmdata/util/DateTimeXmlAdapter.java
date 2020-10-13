package com.mosaicatm.matmdata.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DateTimeXmlAdapter can be used to bind xs:dateTime to java.util.Date using a common
 * date format.
 * 
 *         <jxb:globalBindings>
          <xjc:javaType name="java.util.Date" xmlType="xs:dateTime"
            adapter="com.mosaicatm.lib.jaxb.DateTimeXmlAdapter"
            />
        </jxb:globalBindings>
 * 
 * @author mgarland
 */
public class DateTimeXmlAdapter extends XmlAdapter<String, Date>
{
    private static final Logger logger = LoggerFactory.getLogger(DateTimeXmlAdapter.class);

    private static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    
    /**
     * Thread local allows for a static DateFormat that is thread safe without the need for locking.
     */
    private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {

        @Override
        protected DateFormat initialValue()
        {
            DateFormat result = new SimpleDateFormat(FORMAT);
            result.setTimeZone(TimeZone.getTimeZone("UTC"));
            return result;
        }
    };
    
    @Override
    public Date unmarshal(String value) throws ParseException
    {
        if(value == null || value.isEmpty())
            return null;
        try
        {
            return dateFormat.get().parse(value);
        }
        catch(ParseException e)
        {
            logger.error("Invalid format for date "+value, e);
            throw e;
        }
    }

    @Override
    public String marshal(Date value)
    {
        if(value == null)
            return null;
        return dateFormat.get().format(value);
    }
}
