package com.mosaicatm.fuser.common.matm.xfer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.jaxb.GenericMarshaller;

public class FuserRepositoryTransferFileIO
{
    private final static Log LOG = LogFactory.getLog( FuserRepositoryTransferFileIO.class );
    
    private GenericMarshaller marshaller;
        
    public static File getDefaultCacheFile( String directory, String fileNamePostfix )
    {
        String fileName = "";
        
        if( directory != null )
        {
            fileName = directory;
            
            if( !fileName.endsWith( File.separator ))
            {
                fileName = directory + File.separator;
            }
            
            File checkDir = new File( fileName );
            if( !checkDir.exists() || !checkDir.isDirectory() )
            {
                LOG.error( "Directory does not exist :" + fileName );
                return( null );
            }
        }
        
        fileName = fileName + "fuser_cache";
        
        if( fileNamePostfix != null )
        {
            fileName = fileName + "_" + fileNamePostfix;
        }
        
        fileName = fileName + ".xml.gz";
        
        return( new File( fileName ));
    }              
    
    public FuserRepositoryTransfer loadXmlCache( File file )
    {
        if( getMarshaller() == null )
        {
            LOG.error( "Cannot read cache file : marshaller is NULL!" );
            return null;
        }           

        if(( file == null ) || !file.exists() || !file.isFile() )
        {
            LOG.error( "Cannot read cache file! Cache file does not exist: " + file );
            return null;
        }
                    
        FuserRepositoryTransfer cache = null;
                
        try
        {
            try( FileInputStream is = new FileInputStream( file );
                 BufferedInputStream bis = new BufferedInputStream( is );
                 GZIPInputStream gzipIntputStream = new GZIPInputStream( bis )) 
            {
                cache = getMarshaller().unmarshallToObject( gzipIntputStream );    
            }                        
        }
        catch( Exception ex )
        {
            LOG.error( "Error reading cache file: " + ex.toString(), ex);
        }  
        
        return( cache );
    }

    public void writeXmlCache( FuserRepositoryTransfer fuserRepositoryTransfer, File file )
    {
        if( fuserRepositoryTransfer == null )
        {
            LOG.error( "Cannot write cache file: fuserRepositoryTransfer is NULL!" );
            return;
        }                        
        
        if( getMarshaller() == null )
        {
            LOG.error( "Cannot write cache file: marshaller is NULL!" );
            return;
        }          
        
        if( file == null )
        {
            LOG.error( "Cannot write cache file!" );
            return;
        }

        try
        {                                   
            try( FileOutputStream os = new FileOutputStream( file );
                 BufferedOutputStream bos = new BufferedOutputStream( os );
                 GZIPOutputStream gzipOutputStream = new GZIPOutputStream( bos )) 
            {
                getMarshaller().getMarshaller().marshal( fuserRepositoryTransfer, gzipOutputStream );
            }                        
        }
        catch( IOException | JAXBException ex )
        {
            LOG.error( "Error writing cache file: " + ex.toString(), ex );
        }            
    }       

    private GenericMarshaller getMarshaller()
    {
        if( marshaller == null )
        {
            try
            {
                marshaller = new GenericMarshaller( FuserRepositoryTransfer.class );
                marshaller.setMarshallFormatted( false );
            }
            catch( JAXBException ex )
            {
                LOG.error( "Error instantiating marshaller: " + ex.toString() );
            }
        }
        
        return( marshaller );
    }        
}
