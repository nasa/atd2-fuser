package com.mosaicatm.fuser.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.aptcode.AirportCodeException;
import com.mosaicatm.aptcode.AirportCodeTranslator;
import com.mosaicatm.aptcode.AirportCodeTranslatorFactory;
import com.mosaicatm.aptcode.AirportCodeUtil;
import com.mosaicatm.aptcode.data.AirportCodeEntry;
import com.mosaicatm.matmdata.common.Aerodrome;

public class FuserAirportCodeTranslator
{
    private final Log log = LogFactory.getLog(getClass());
    
    private AirportCodeTranslator translator;
    
    public void init()
    {
        AirportCodeTranslatorFactory factory = new AirportCodeTranslatorFactory();
        factory.setPreFetchAll(true);
        factory.setPreFetchIcaoCodes(true);
        factory.setPreFetchIataCodes(true);
        factory.setPreFetchFaaLidCodes(true);
        
        try 
        {
            translator = factory.create();
        } 
        catch (AirportCodeException e) 
        {
            log.error("Error initializing airport code translator",e);
        }
    }

    public AirportCodeEntry getBestMatchAirport( Aerodrome aerodrome )
    {
        AirportCodeEntry code = null;
        
        if( aerodrome != null )
        {
            if( translator != null )
            {
                try 
                {
                    //ICAO is most trustworthy...
                    if( translator.isIcao( aerodrome.getIcaoName() ))
                    {
                        code = translator.getAirportCodeFromIcao( aerodrome.getIcaoName() );
                    }
                    else if( aerodrome.getFaaLid() != null )
                    {
                        code = translator.getBestMatchAirportCodeFromFaaSource( aerodrome.getIcaoName() );
                    }                    
                    //We currently store either the actual IATA code, or whatever came in with the IATA field
                    else
                    {
                        code = translator.getBestMatchAirportCodeFromAirlineSource( aerodrome.getIataName() );
                    }
                } 
                catch (AirportCodeException e) 
                {
                    log.error("Error calling airport code translator",e);
                }
            }
            else
            {
                log.error(  "AirportCodeTranslator is null!!!" );
            }
            
            
            //If the code doesn't exist in the lookup, then use the incoming airport code
            if( code == null )
            {
                code = new AirportCodeEntry();
                
                String airport = aerodrome.getIataName();
                if( airport == null )
                {
                    airport = aerodrome.getIcaoName();
                }
                
                //We default to IATA unless pretty sure it is ICAO
                if( AirportCodeUtil.isIcaoTextFormat( airport ))
                {
                    code.setIcao( airport );
                }
                else
                {
                    code.setFaaLid( airport );
                    code.setIata( airport );
                }
            }
        }
        
        return( code );
    }
    
    public boolean isInvalidAerodrome( Aerodrome aerodrome )
    {
        if(( aerodrome.getIcaoName() != null ) &&
                !AirportCodeUtil.isIcaoTextFormat( aerodrome.getIcaoName() ))
        {
            return( true );
        }
        
        if(( aerodrome.getFaaLid() != null ) && 
                !AirportCodeUtil.isFaaLidTextFormat( aerodrome.getFaaLid() ))
        {
            return( true );
        }
        
        if(( aerodrome.getIataName() != null ) &&
                !AirportCodeUtil.isIataTextFormat( aerodrome.getIataName() ) &&
                !AirportCodeUtil.isFaaLidTextFormat( aerodrome.getIataName() ))
        {
            return( true );
        }        
        
        return( false );
    }
}
