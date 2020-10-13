package com.mosaicatm.tfmplugin;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.matmdata.flight.extension.tfmcomm.TfmSensitivityReasonType;
import com.mosaicatm.matmdata.flight.extension.tfmcomm.TfmSensitivityType;

public class TfmsSensitivityUtil
{
    private static final Log LOG = LogFactory.getLog( TfmsSensitivityUtil.class );
    
    public static Boolean isSensitiveData( TfmSensitivityType sensitivity, TfmSensitivityReasonType sensitivityReason )
    {
        /*
            Sensitivity:
            R - government agency only 
            A - all users including government
            D - all users except government because duplicate."        
        
            Sensitivity Reason:
            FM - Military flight -- Note: FM = Military is almost always a bug in TFMS data
            FS - Sensitive flight
            DR - data restriction. 
        */
        
        Boolean isSensitiveData = null;
        
        if( sensitivity != null )
        {
            switch( sensitivity )
            {
                case A:
                case D:
                    isSensitiveData = false;
                    break;
                    
                case R:
                    if( sensitivityReason != null )
                    {
                        switch( sensitivityReason )
                        {       
                            case DR:
                                break;
                            
                            case FM:
                            case FS:
                                isSensitiveData = true;
                                break;
                            
                            default:
                                LOG.error( "Unhandled sensitivity reason: " + sensitivityReason );    
                        }
                    }                    
                    break;
                    
                default:
                    LOG.error( "Unhandled sensitivity level: " + sensitivity );
            }
        }        
        
        return( isSensitiveData );
    }         
}
