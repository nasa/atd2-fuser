package com.mosaicatm.fuser.datacapture.db.dao;

import org.apache.ibatis.annotations.Param;

import com.mosaicatm.lib.database.ibatis.mapper.TableMapper;

public interface ViewMapper
extends TableMapper
{
    public void createView(@Param("tableName") String tableName);
}
