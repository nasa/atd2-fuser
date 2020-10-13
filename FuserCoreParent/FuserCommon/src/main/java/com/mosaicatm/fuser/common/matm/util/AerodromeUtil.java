package com.mosaicatm.fuser.common.matm.util;

import com.mosaicatm.matmdata.common.Aerodrome;

public class AerodromeUtil {
    
    
    public static Aerodrome createFromIataName(String iataName)
    {
        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setIataName(iataName);
        return aerodrome;
    }
    
    public static Aerodrome createFromIcaoName(String icaoName)
    {
        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setIcaoName(icaoName);
        return aerodrome;
    }
}
