package com.mosaicatm.matmplugin.matm;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.flight.MatmFlight;


public class MatmToMatmTransform
		implements Transformer <MatmFlight, MatmFlight>
{
    private final Log log = LogFactory.getLog( getClass() );
    
    @Override
    public MatmFlight transform(MatmFlight transfer) 
    {
        return transfer;
    }
 }
