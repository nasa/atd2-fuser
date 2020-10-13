package com.mosaicatm.fuser.store.redis.util;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import com.mosaicatm.fuser.store.redis.RedisDB;

public class RedisDbUtilitiesMain
{
    private static final Log log = LogFactory.getLog( RedisDbUtilitiesMain.class );
    
    public static boolean copyRedisDatabase( String sourceServer, int sourceDatabase,
            String destinationServer, int destinationDatabase )
    {
        log.info( "copyRedisDatabase: sourceServer[" + sourceServer + "]" + 
                ", sourceDatabase[" + sourceDatabase+"]" +    
                ", destinationServer[" + destinationServer + "]" + 
                ", destinationDatabase[" + destinationDatabase+"]" );   
                
        RedisDB source_db = new RedisDB();
        source_db.setHost( sourceServer );
        source_db.setDatabase( sourceDatabase );
        source_db.init();

        RedisDB destination_db = new RedisDB();
        destination_db.setHost( destinationServer );
        destination_db.setDatabase( destinationDatabase );
        destination_db.init();

        try
        {
            Map<String, byte[]> dump_map = getRedisDbKeyDumpMap(source_db);
            
            try( Jedis destination_client = destination_db.getClient(); 
                    Pipeline destination_pipleline = destination_client.pipelined(); )
            {
                log.info( "- Flushing " + destinationServer + ", DB #" + destinationDatabase );
                destination_client.flushDB();
                
                log.info( "- Inserting data to " + destinationServer + ", DB #" + destinationDatabase );
                for( Map.Entry<String, byte[]> entry : dump_map.entrySet() )
                {
                    destination_pipleline.restore( entry.getKey(), 0, entry.getValue() );
                }
                destination_pipleline.sync();
            }
            
            return( true );
        }
        catch( Exception ex )
        {
            log.error( "ERROR: copyRedisDatabase operation failed: " + ex.getMessage(), ex );
        }
        
        return( false );
    }    
    
    public static boolean dumpRedisDatabaseFile( String fileName, String sourceServer, int sourceDatabase ) 
    {
        log.info( "dumpRedisDatabaseFile: fileName[" + fileName + "]" + 
                ", sourceServer[" + sourceServer + "]" + 
                ", sourceDatabase[" + sourceDatabase+"]" );        
        
        RedisDB source_db = new RedisDB();
        source_db.setHost( sourceServer );
        source_db.setDatabase( sourceDatabase );
        source_db.init();

        try
        {
            Map<String, byte[]> dump_map = getRedisDbKeyDumpMap(source_db);
            
            log.info( "- Writing Redis dump to: " + fileName );
            writeDumpToFile( fileName, dump_map );
            
            return( true );
        }
        catch( Exception ex )
        {
            log.error( "ERROR: writeRedisDatabaseFile operation failed: " + ex.getMessage(), ex );
        }
        
        return( false );
    }    
    
    public static boolean loadRedisDatabaseFile( String fileName, String destinationServer, int destinationDatabase ) 
    {
        log.info( "loadRedisDatabaseFile: fileName[" + fileName + "]" + 
                ", destinationServer[" + destinationServer + "]" + 
                ", destinationDatabase[" + destinationDatabase+"]" );
        
        RedisDB destination_db = new RedisDB();
        destination_db.setHost( destinationServer );
        destination_db.setDatabase( destinationDatabase );
        destination_db.init();

        try
        {
            log.info( "- Reading Redis dump file from: " + fileName );
            Map<String, byte[]> dump_map = readDumpFromFile( fileName );            
            
            try( Jedis destination_client = destination_db.getClient(); 
                    Pipeline destination_pipleline = destination_client.pipelined() )
            {
                log.info( "- Flushing " + destinationServer + ", DB #" + destinationDatabase );
                destination_client.flushDB();
                
                log.info( "- Inserting data to " + destinationServer + ", DB #" + destinationDatabase );
                for( Map.Entry<String, byte[]> entry : dump_map.entrySet() )
                {
                    destination_pipleline.restore( entry.getKey(), 0, entry.getValue() );
                }
                destination_pipleline.sync();
            }
            
            return( true );
        }
        catch( Exception ex )
        {
            log.error( "ERROR: readRedisDatabaseFile operation failed: " + ex.getMessage(), ex );
        }
        
        return( false );
    }        
    
    private static void writeDumpToFile( String fileName, Map<String, byte[]> dumpMap ) throws IOException
    {
        File file = new File( fileName );
        if( !file.getParentFile().isDirectory() )
        {
            throw new IOException( "File parent directory does not exist! [" + file.getParentFile() + "]" );
        }
        else if( file.exists() )
        {
            log.info( "- Overwriting existing file." );
        }           
        
        try( ObjectOutputStream out = new ObjectOutputStream( new BufferedOutputStream ( new FileOutputStream( file ))))
        {
            out.writeObject( dumpMap );
        }
    }
    
    private static Map<String, byte[]> readDumpFromFile( String fileName ) throws IOException, ClassNotFoundException
    {
        Map<String, byte[]> result = null;
     
        File file = new File( fileName );
        if( !file.exists() )
        {
            throw new IOException( "File parent does not exist! [" + file.getParentFile() + "]" );
        }
        
        try( ObjectInputStream in = new ObjectInputStream( new BufferedInputStream ( new FileInputStream( file ))))
        {
            result = (Map<String, byte[]>) in.readObject();
        }
        
        return( result );
    } 
    
    private static Map<String, byte[]> getRedisDbKeyDumpMap( RedisDB redisDb ) throws IOException
    {
        Map<String, byte[]> dump_map = new HashMap<>();
        
        try( Jedis source_client = redisDb.getClient(); 
                Pipeline source_pipleline = source_client.pipelined() )
        {
            Map<String, Response<byte[]>> key_dump_responses = new HashMap<>();
            
            Set<String> keys = source_client.keys( "*" );
            log.info("- Collecting " + keys.size() + " Redis keys' data from " + 
                    redisDb.getHost() + ", DB #" + redisDb.getDatabase() );

            for( String key : keys )
            {
                key_dump_responses.put( key, source_pipleline.dump( key ));
            }
            source_pipleline.sync();
            
            for( Map.Entry<String, Response<byte[]>> entry : key_dump_responses.entrySet() )
            {
                dump_map.put( entry.getKey(), entry.getValue().get() );
            }            
        }
            
        return( dump_map );
    }
    
    public static void main( String[] args )
    {
        Options options = new Options();

        options.addOption(
                Option.builder( "c" ).
                longOpt( "copy" ).
                desc( "Copy Redis database." ).
                required( false ).
                numberOfArgs( 4 ).
                argName( "<Server> <DB Index> <Target Server> <Target DB Index>" ).
                optionalArg( false ).
                build() );

        options.addOption(
                Option.builder( "d" ).
                longOpt( "dump" ).
                desc( "Dump Redis database." ).
                required( false ).
                numberOfArgs( 3 ).
                argName( "<Server> <DB Index> <File>" ).
                optionalArg( false ).
                build() );

        options.addOption(
                Option.builder( "l" ).
                longOpt( "load" ).
                desc( "Load Redis database." ).
                required( false ).
                numberOfArgs( 3 ).
                argName( "<Server> <DB Index> <File>" ).
                optionalArg( false ).
                build() );

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth( 120 );

        boolean success = false;
        
        try
        {
            CommandLine line = parser.parse( options, args );

            if( line.hasOption( 'c' ) )
            {
                String[] vals = line.getOptionValues( 'c' );
                success = RedisDbUtilitiesMain.copyRedisDatabase(
                        vals[ 0 ],
                        Integer.parseInt( vals[ 1 ]),
                        vals[ 2 ],
                        Integer.parseInt( vals[ 3 ] ));
            }
            else if( line.hasOption( 'd' ) )
            {
                String[] vals = line.getOptionValues( 'd' );
                success = RedisDbUtilitiesMain.dumpRedisDatabaseFile(
                        vals[ 2 ],
                        vals[ 0 ],
                        Integer.parseInt( vals[ 1 ] ));
            }
            else if( line.hasOption( 'l' ) )
            {
                String[] vals = line.getOptionValues( 'l' );
                success = RedisDbUtilitiesMain.loadRedisDatabaseFile(
                        vals[ 2 ],
                        vals[ 0 ],
                        Integer.parseInt( vals[ 1 ] ));
            }
            else
            {
                formatter.printHelp( "redis-utils", options );
            }
        }
        catch( Exception exp )
        {
            log.error( "Parsing failed.  Reason: " + exp.getMessage() );
            log.error( "" );
            formatter.printHelp( "redis-utils", options );
        }  
        
        if( success )
        {        
            System.exit( 0 );
        }
        else
        {
            System.exit( 1 );
        }
    }
}
