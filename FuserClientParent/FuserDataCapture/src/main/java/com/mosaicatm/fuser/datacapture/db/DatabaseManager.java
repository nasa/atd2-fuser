package com.mosaicatm.fuser.datacapture.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public interface DatabaseManager
{
    public Connection getConnection ();
    public Connection openConnection ();
    public void closeConnection ();
    
    public Statement createStatement();
    public ResultSet executeQuery (String query);
    
    public boolean isExistingTable (String table);
    public List<String> getTableColumns (String table);
}
