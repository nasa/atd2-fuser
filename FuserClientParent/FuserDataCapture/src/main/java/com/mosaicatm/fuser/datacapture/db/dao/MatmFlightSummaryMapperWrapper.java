package com.mosaicatm.fuser.datacapture.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.datacapture.CaptureType;
import com.mosaicatm.fuser.datacapture.flat.FlatMatmFlight;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class MatmFlightSummaryMapperWrapper
extends MatmObjectSummaryMapperWrapper<MatmFlight, FlatMatmFlight>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
        
    public MatmFlightSummaryMapperWrapper()
    {
        super(CaptureType.MATM_FLIGHT_SUMMARY);
    }

    @Override
    public String buildUpdateSQL()
    {
        StringBuilder sb = new StringBuilder("WITH updated AS (UPDATE " +
                getTableName() + " t SET ");
        
        List<String> columns = getBulkDriver().getColumns(getTableName());
        for (String column : columns)
        {
            sb.append(column);
            sb.append(" = ");
            sb.append("temp." + column);
            
            sb.append(",");
        }
        
        sb.setLength(sb.length() - 1);
        
        sb.append(" FROM " + getTemporaryTableName() + (" temp WHERE t.gufi = temp.gufi"));
        sb.append(" RETURNING temp.gufi)");
        sb.append(" INSERT INTO " + getTableName() + " SELECT * FROM " + getTemporaryTableName());
        sb.append(" temp WHERE temp.gufi NOT IN (SELECT * FROM updated);");
        
        return sb.toString();
    }
    
    @Override
    public ResultSet getExpiredData() throws Exception
    {
        // if fails, try again if due to connection issue
        ResultSet rs = null;
        try
        {
            rs = getExpiredFlightsHelper();
        }
        catch (SQLException e)
        {
            if (getBulkDriver().verifyConnection(e))
            {
                rs = getExpiredFlightsHelper();
            }
        }
        return rs;
    }
    
    private ResultSet getExpiredFlightsHelper() throws Exception
    {
        long startTime = System.currentTimeMillis();
        
        long currentTime;
        
        if (getClock() != null)
        {
            currentTime = getClock().getTimeInMillis();
        }
        else
        {
            currentTime = System.currentTimeMillis();
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        calendar.add(Calendar.HOUR_OF_DAY, getExpiredWindowHour() * -1);
        
        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
        ResultSet rs = getBulkDriver().executeQuery("SELECT gufi FROM "
                        + getTableName() + " WHERE timestamp <= '"
                        + timestamp.toString() + "'");
        
        long totalTime = System.currentTimeMillis() - startTime;
        if (totalTime > 5000L)
        {
            log.warn("Slow getting flights from database " + totalTime);
        }
        
        return rs;
    }
    
    @Override
    public String buildRemoveSQL()
    {
        StringBuilder result = new StringBuilder("DELETE FROM " + getTableName() + " ");
        result.append("WHERE gufi in (%s);");
        
        return result.toString();
    }
}
