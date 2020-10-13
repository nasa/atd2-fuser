package com.mosaicatm.tmaplugin.util;

/**
 *
 * @author jburke
 */
public class SpeedConverterUtil
{
    /**
     * Very simple rule of thumb style converter from mach to knots. 
     * 
     * @param machNumber the mach number
     * @param cruiseAltitudeFeet cruising altitude in feet. If set to null, assumes 36,000 feet.
     * @return the speed in knots
     */
    public static Double machToKnots( Double machNumber, Double cruiseAltitudeFeet )
    {
        if( machNumber == null )
        {
            return( null );
        }
        
        if(( cruiseAltitudeFeet == null ) || ( cruiseAltitudeFeet > 36000. ))
        {
            cruiseAltitudeFeet = 36000.;
        }
        
        double temperature_at_altitude = 15. - ( 6.5 * cruiseAltitudeFeet * 0.0003048 );
        double speed = ( machNumber * 600. ) + temperature_at_altitude + 35.;
        
        return( speed );
    }    
    
    /**
     * Very simple rule of thumb style converter from knots to mach. 
     * 
     * @param knots the mach number
     * @param cruiseAltitudeFeet cruising altitude in feet. If set to null, assumes 36,000 feet.
     * @return the mach number
     */
    public static Double knotsToMach( Double knots, Double cruiseAltitudeFeet )
    {
        if( knots == null )
        {
            return( null );
        }
        
        if(( cruiseAltitudeFeet == null ) || ( cruiseAltitudeFeet > 36000. ))
        {
            cruiseAltitudeFeet = 36000.;
        }
        
        double temperature_at_altitude = 15. - ( 6.5 * cruiseAltitudeFeet * 0.0003048 );       
        double mach = ( knots - temperature_at_altitude - 35. ) / 600.;
        
        return( mach );
    }     
}
