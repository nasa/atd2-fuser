package com.mosaicatm.fuser.datacapture.db.dao;

import java.sql.ResultSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.fuser.datacapture.CaptureType;
import com.mosaicatm.fuser.datacapture.flat.FlatMatmAircraft;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;

public class MatmAircraftSummaryMapperWrapper
extends MatmObjectSummaryMapperWrapper<MatmAircraft, FlatMatmAircraft>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
        
    public MatmAircraftSummaryMapperWrapper()
    {
        super(CaptureType.MATM_AIRCRAFT_SUMMARY);
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
        
        sb.append(" FROM " + getTemporaryTableName() + (" temp WHERE t.registration = temp.registration"));
        sb.append(" RETURNING temp.registration)");
        sb.append(" INSERT INTO " + getTableName() + " SELECT * FROM " + getTemporaryTableName());
        sb.append(" temp WHERE temp.registration NOT IN (SELECT * FROM updated);");
        
        return sb.toString();
    }
    
    @Override
    public ResultSet getExpiredData() throws Exception
    {
        // aircraft don't expire
        return null;
    }
    
    @Override
    public String buildRemoveSQL()
    {
        StringBuilder result = new StringBuilder("DELETE FROM " + getTableName() + " ");
        result.append("WHERE registration in (%s);");
        
        return result.toString();
    }
}
