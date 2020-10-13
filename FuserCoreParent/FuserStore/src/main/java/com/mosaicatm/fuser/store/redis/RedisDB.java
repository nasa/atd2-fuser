package com.mosaicatm.fuser.store.redis;

import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisDB {
    
    public static final long MAX_WAIT_TIMEOUT_MILLISECONDS = 30000;
    public static final int CONNECTION_TIMEOUT_SECONDS_MILLISECONDS = 15000;
    public static final int MAX_TOTAL_IN_POOL = 128;
    public static final int REDIS_PORT = 6379;
    public static final int MAX_DB_INDEX = 15;
    
    private final Log log = LogFactory.getLog(getClass());
    
    private JedisPool pool;
    private JedisPoolConfig config;
    private String host = "localhost";
    private int database = 0;
    private String password = null;
    
    private int port = REDIS_PORT;
    private int connectionTimeout = CONNECTION_TIMEOUT_SECONDS_MILLISECONDS;

    public void init() {
        
        if (config == null)
        {
            config = new JedisPoolConfig();
            config.setMaxWaitMillis(MAX_WAIT_TIMEOUT_MILLISECONDS);
            config.setMaxTotal(MAX_TOTAL_IN_POOL);
        }
        
        if (log.isDebugEnabled())
        {
            log.debug("Initializing RedisDB...");
            log.debug("redis host: " + host);
            log.debug("redis port: " + port);
            log.debug("redis database: " + database);
            log.debug("redis timeout: " + connectionTimeout);
            log.debug("redis maxWait: " + config.getMaxWaitMillis());
            log.debug("redis maxTotal: " + config.getMaxTotal());
            log.debug("...RedisDB Initialized");
        }
        
        pool = new JedisPool(config, host, port, connectionTimeout, password, database);    
        
    }
    
    public void setConfig(JedisPoolConfig config) {
        this.config = config;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public int getPort() {
        return port;
    }

    public int getDatabase()
    {
        return database;
    }

    public void setDatabase( int database )
    {
        this.database = database;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        // An empty password string still triggers jedis to attempt to connect with a password.
        // So make sure we use null instead of an empty string
        if( password != null && password.trim().isEmpty() )
        {
            password = null;
        }

        this.password = password;
    }
    
    /**
     * The connection timeout is defined in milliseconds even though the
     * parameter is an int.
     * 
     * @param connectionTimeout
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    /**
     * Return connection timeout in milliseconds as an int
     * @return
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    
    public Jedis getClient() {
        return pool.getResource();
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /***
     * Clear everything, possibly including data used by other components
     */
    public void flushDb() {
        try (Jedis jedis = pool.getResource()) {
            jedis.flushDB();
        }         
    }
    
    // clears all keys starting with namespace
    public void clearNamespace(String namespace) 
    {
        String matchPattern = KeyGenerator.normalizeNamespace( namespace ) + "*";
        
        Set<String> keys = null;
        try (Jedis jedis = pool.getResource()) {
            keys = jedis.keys(matchPattern);
        }     
        
        if(( keys != null ) && !keys.isEmpty() )
        {
            ArrayList<String> list = new ArrayList<>();
            for( String key : keys ) 
            {
                list.add( key );
                if( list.size() > 500 )
                {
                    deleteKeys( list.toArray( new String [list.size()] ));
                    list = new ArrayList<>();
                }
            }  

            if( !list.isEmpty() )
            {
                deleteKeys( list.toArray( new String [list.size()] ));
            }
        }
    }    
    
    private void deleteKeys( String[] keys ) 
    {
        try (Jedis jedis = pool.getResource()) {
            jedis.del( keys );
        }                 
    }   
}
