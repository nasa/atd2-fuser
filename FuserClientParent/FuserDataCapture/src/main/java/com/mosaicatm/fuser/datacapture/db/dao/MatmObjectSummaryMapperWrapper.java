package com.mosaicatm.fuser.datacapture.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mosaicatm.container.io.DelimitedObjectWriter;
import com.mosaicatm.fuser.common.matm.util.MatmIdLookup;
import com.mosaicatm.fuser.datacapture.CaptureType;
import com.mosaicatm.fuser.datacapture.DataWrapper;
import com.mosaicatm.fuser.datacapture.flat.FlatFactory;
import com.mosaicatm.fuser.datacapture.io.CaptureData;
import com.mosaicatm.fuser.datacapture.io.DelimitedObjectFactory;
import com.mosaicatm.lib.database.bulk.BulkDriver;
import com.mosaicatm.lib.database.bulk.impl.BulkBatch;
import com.mosaicatm.lib.database.bulk.impl.BulkBatchString;
import com.mosaicatm.lib.text.io.StringBuilderWriter;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.matmdata.common.MatmObject;

public abstract class MatmObjectSummaryMapperWrapper <T extends MatmObject, F>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private String tableName;
    private String temporaryTableName;
    
    private ViewMapper mapper;
    
    private boolean createTable;
    
    private boolean cleanTable;
    
    private boolean active;
    
    private boolean createViews;
    
    private BulkDriver bulkDriver;
    
    private String removeStatement;
    
    private DelimitedObjectFactory delimitedFactory;
    
    private FlatFactory flatFactory;
    
    private Clock clock;
    
    private int expiredWindowHour = 72; // by default 3 days
    
    private String updateSQL;
    
    private boolean isReady = false;
    
    private MatmIdLookup<T, String> matmIdLookup;
    
    private CaptureType captureType;
    
    public MatmObjectSummaryMapperWrapper(CaptureType captureType)
    {
        this.captureType = captureType;
    }
    
    public abstract ResultSet getExpiredData() throws Exception;
    public abstract String buildRemoveSQL();
    public abstract String buildUpdateSQL();
    
    public void start()
    {
        if (!active)
            return;
        
        if (mapper == null || bulkDriver == null)
        {
            log.error("database is not initialized properly for summary table");
            return;
        }
        
        if (createTable)
        {
            if (cleanTable)
            {
                log.info("Drop table {}", tableName);
                bulkDriver.dropTable(tableName);
            }
            log.info("Create table {}", tableName);
            mapper.createTable(tableName);
        }       
        else if (cleanTable)
        {
            log.info("Clear (truncate) table {}", tableName);
            bulkDriver.clearTable(tableName);
        }
                
        removeStatement = buildRemoveSQL();
    }
    
    public void initViews()
    {
        if (!active)
            return;
        
        if (createViews)
        {
            log.info("Create view for: {}", tableName);
            mapper.createView(tableName);
        }
    }
    
    public void handle(List<DataWrapper<T>> messages) throws Exception
    {
        if (!active || messages == null
            || messages.isEmpty())
        {
            return;
        }
        
        log.trace("handling batch ", messages.size());
        
        long startTime = System.currentTimeMillis();
        
        if (updateSQL == null)
        {
            updateSQL = buildUpdateSQL();
        }
        
        if (!isReady)
        {
            createTempTable();
            isReady = true;
        }
        
        StringBuilder sb = new StringBuilder();
        DelimitedObjectWriter<F> writer = getDelimitedWriter(sb);
        T flight = null;
        String key = null;
        CaptureData<T> data = null;
        F flat = null;
        for (DataWrapper<T> message : messages)
        {
            if (message == null || message.getData() == null)
                continue;
            
            flight = message.getData();
            key = matmIdLookup.getIdentifier(flight);
            
            if (key != null && flight.getTimestamp() != null)
            {
                data = getCaptureData(key, flight);
                flat = flatFactory.flatten(data);
                
                writer.writeLine(flat);                
            }
        }
        
        if (bulkDriver == null)
        {
            log.warn("database not initialize.");
        }
        else
        {
            try
            {
                BulkBatch batch = new BulkBatchString(null, temporaryTableName, sb, null);
                bulkDriver.bulkInsert(temporaryTableName, batch);
                
                bulkDriver.executeUpdate(updateSQL);
                bulkDriver.executeUpdate("TRUNCATE " + temporaryTableName);
            }
            catch (Exception e)
            {
                log.error("fail to execute query.", e);
            }
        }
        
        long endTime = System.currentTimeMillis() - startTime;
        if (endTime > 5000L)
        {
              log.warn("Slow updating " + messages.size()
                    + " flights with total time: " + endTime);
        }
    }
    
    private void createTempTable() throws SQLException
    {
        if (bulkDriver != null && temporaryTableName != null)
        {
            bulkDriver.executeUpdate("DROP TABLE IF EXISTS " + temporaryTableName + " CASCADE;");
            
            StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
            sb.append( temporaryTableName + " (LIKE " + tableName + " INCLUDING constraints)");
            bulkDriver.executeUpdate(sb.toString());
        }
    }

    private CaptureData<T> getCaptureData(String key, T message)
    {
        
        CaptureData<T> captureData = null;
        if (message != null)
        {
            captureData = new CaptureData<>();
            captureData.setTimestamp(message.getTimestamp());
            captureData.setRecordTimestamp(getRecordTimestamp(message));
            captureData.setGufi(key);
            captureData.setData(message);
            captureData.setType(captureType);
            
            String surfaceAirportName = message.getSurfaceAirport() == null ? null : message.getSurfaceAirport().getIataName();
            captureData.setSurfaceAirportName( surfaceAirportName );
        }
        
        return captureData;
    }

    public void handleRemove() throws Exception
    {
        if (!active)
            return;
        
        if (bulkDriver != null)
        {
            log.info("Summary table removal started.");

            ResultSet rs = getExpiredData();
            
            if (rs == null)
            {
                return;
            }
            
            long startTime = System.currentTimeMillis();
            int totalRemoveCount = 0;
            
            StringBuilder builder = new StringBuilder();
            while (rs.next())
            {
                if (builder.length() > 0)
                    builder.append(", ");
                builder.append("'" + rs.getString(1) + "'");
                totalRemoveCount++;
            }
            
            if (builder.length() > 0)
            {
                log.info("removing flights:" + builder.toString());
                String sql = String.format(removeStatement, builder.toString());
                bulkDriver.executeUpdate(sql);
            }
            
            long totalTime = System.currentTimeMillis() - startTime;
            if (totalTime > 5000L)
            {
                log.warn("Slow removing " + totalRemoveCount
                + " flights with total remove time: " + totalTime);
            }
        }
    }
    
    private DelimitedObjectWriter<F> getDelimitedWriter(StringBuilder sb)
    {
        DelimitedObjectWriter<F> delimitedWriter = null;
        if (sb != null)
        {
            delimitedWriter = 
                            delimitedFactory.getDelimitedObjectWriter(captureType, new StringBuilderWriter(sb));
        }
        
        return delimitedWriter;
    }
    
    public Date getRecordTimestamp( T flight )
    {       
        if( clock != null )
        {
            return( new Date( clock.getTimeInMillis() ));
        }
        else if( flight.getTimestamp() != null )
        {
            return( flight.getTimestamp() );
        }
        else
        {
           return( new Date() );
        }
    }
    
    public void setClock(Clock clock)
    {
        this.clock = clock;
    }
    
    public Clock getClock()
    {
        return clock;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }
    
    public String getTableName()
    {
        return tableName;
    }

    public void setMapper(ViewMapper mapper)
    {
        this.mapper = mapper;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public void setBulkDriver(BulkDriver bulkDriver)
    {
        this.bulkDriver = bulkDriver;
    }
    
    public BulkDriver getBulkDriver()
    {
        return bulkDriver;
    }
    
    public void setCreateTable(boolean createTable)
    {
        this.createTable = createTable;
    }
    
    public void setCleanTable(boolean cleanTable)
    {
        this.cleanTable = cleanTable;
    }

    public void setDelimitedFactory(DelimitedObjectFactory delimitedFactory)
    {
        this.delimitedFactory = delimitedFactory;
    }

    public void setFlatFactory(FlatFactory flatFactory)
    {
        this.flatFactory = flatFactory;
    }
    
    public int getExpiredWindowHour()
    {
        return expiredWindowHour;
    }
    
    public void setExpiredWindowHour(int expiredWindowHour)
    {
        this.expiredWindowHour = expiredWindowHour;
    }
    
    public void setExpiredWindowDay(int expiredWindowDay)
    {
        this.expiredWindowHour = expiredWindowDay * 24 ;
    }

    public void setCreateViews(boolean createViews)
    {
        this.createViews = createViews;
    }

    public void setTemporaryTableName(String temporaryTableName)
    {
        this.temporaryTableName = temporaryTableName;
    }
    
    public String getTemporaryTableName()
    {
        return temporaryTableName;
    }

    public void setMatmIdLookup(MatmIdLookup<T, String> matmIdLookup)
    {
        this.matmIdLookup = matmIdLookup;
    }
}
