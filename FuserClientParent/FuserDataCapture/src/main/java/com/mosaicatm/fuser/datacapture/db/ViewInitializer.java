package com.mosaicatm.fuser.datacapture.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.fuser.datacapture.CaptureType;
import com.mosaicatm.fuser.datacapture.db.dao.ViewMapper;
import com.mosaicatm.lib.database.ibatis.mapper.TableMapperFactory;
import com.mosaicatm.lib.database.ibatis.mapper.TableMapperWrapper;

public class ViewInitializer
{
    private final Log log = LogFactory.getLog(getClass());

    private TableMapperFactory tableMapperFactory;
    private DatabaseNameFactory databaseNameFactory;
    private boolean createViews;
    private boolean dbActive;
    
    public void initViews()
    {
        if(dbActive && createViews)
        {
            log.info("Create views for tables...");
            for (CaptureType type : CaptureType.values())
            {
                String tableName = databaseNameFactory.getDatabaseName(type);
                if (tableName != null)
                {
                    createView(tableMapperFactory.getTableMapper(tableName));
                }
            }
            log.info("Finished creating views for tables.");
        }
    }
    
    private void createView(TableMapperWrapper wrapper)
    {
        if (wrapper != null && wrapper.getMapper() != null &&
                        wrapper.getMapper() instanceof ViewMapper)
        {
            log.info("Create View for " + wrapper.getTableName());
            ((ViewMapper)wrapper.getMapper()).createView(wrapper.getTableName());
        }
    }

    public void setTableMapperFactory(TableMapperFactory tableMapperFactory)
    {
        this.tableMapperFactory = tableMapperFactory;
    }

    public void setDatabaseNameFactory(DatabaseNameFactory databaseNameFactory)
    {
        this.databaseNameFactory = databaseNameFactory;
    }

    public void setCreateViews(boolean createViews)
    {
        this.createViews = createViews;
    }

    public boolean isDbActive()
    {
        return dbActive;
    }

    public void setDbActive(boolean dbActive)
    {
        this.dbActive = dbActive;
    }
    
}
