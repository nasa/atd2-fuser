package com.mosaicatm.fuser.common.matm.util.flight;

import java.util.Collection;
import java.util.function.Function;

import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.flight.MatmFlight;

public class CategoryUtil
{

    public static boolean isDeparture(MatmFlight matm, String airport)
    {
        boolean result = false;
        if (matm != null && airport != null && !airport.trim().isEmpty())
        {
            result = isAirportIncluded(matm.getDepartureAerodrome(), airport);
        }
        
        return result;
    }
    
    public static boolean isArrival(MatmFlight matm, String airport)
    {
        boolean result = false;
        if (matm != null && airport != null && !airport.trim().isEmpty())
        {
            result = isAirportIncluded(matm.getArrivalAerodrome(), airport);
        }
        
        return result;
    }
    
    public static boolean isAirportIncluded(Aerodrome aerodrome, String airport)
    {
        Function<Aerodrome, Boolean> fn = (a) -> {
            if (a != null)
            {
                return airportNamesEquals(a.getIataName(), airport) ||
                       airportNamesEquals(a.getIcaoName(), airport) ||
                       airportNamesEquals(a.getFaaLid(), airport);
            }
            
            return false;
        };
        
        return isAirportMatch(aerodrome, airport, fn);
    }
    
    public static boolean isAirportIncluded(Aerodrome aerodrome, Collection<String> airports)
    {
        CategoryEvaluator fn = (f, a) -> { return isAirportIncluded(f, a); };
        
        return isInCategory(aerodrome, airports, fn);
    }
    
    private static boolean airportNamesEquals(String a, String b)
    {
        if(a == null || b == null)
        {
            return false;
        }
        
        if(a.length() != b.length())
        {
            //  Strip off international prefix K
            if(a.length() == 4 && a.charAt(0) == 'K')
            {
                a = a.substring(1);
            }
            if(b.length() == 4 && b.charAt(0) == 'K')
            {
                b = b.substring(1);
            }
        }
        return a.equals(b);
    }
    
    private static boolean isAirportMatch(Aerodrome aerodrome, String airport, Function<Aerodrome, Boolean> fn)
    {
        if (aerodrome != null && airport != null && !airport.trim().isEmpty())
        {
            return fn.apply(aerodrome);
        }
        
        return false;
    }
    
    private static boolean isInCategory(Aerodrome aerodrome, Collection<String> airports, CategoryEvaluator fn)
    {
        if (airports != null && !airports.isEmpty())
        {
            for (String airport : airports)
            {
                if (fn.isCategory(aerodrome, airport))
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @FunctionalInterface
    private interface CategoryEvaluator
    {
        public boolean isCategory (Aerodrome aerodrome, String airport);
    }
}
