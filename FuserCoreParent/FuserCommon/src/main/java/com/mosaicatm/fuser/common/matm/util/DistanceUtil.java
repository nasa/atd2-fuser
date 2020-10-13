package com.mosaicatm.fuser.common.matm.util;

import com.mosaicatm.matmdata.common.Position;

public class DistanceUtil
{
	public final static double EARTH_RADIUS = 3440.07; //distance in nautical miles
	public final static double EARTH_RADIUS_FEET = 20925524.9;    
    
	/**
	 * Calculates the great-circle distance between two points using the "haversine" formula.
	 * The great-circle distance is the shortest distance of the earth's surface. 
	 * 
	 * There is a great web page with a good discussion about mathematical functions on
	 * lat / lon coordinates here: http://www.movable-type.co.uk/scripts/latlong.html
	 * 
	 * @param lat1	latitude of first point in decimal degrees
	 * @param lng1	longitude of first point in decimal degrees
	 * @param lat2	latitude of second point in decimal degrees
	 * @param lng2	longitude of second point in decimal degrees
	 * 
	 * @return The great-circle distance between the points in nautical miles
	 */
	public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) 
    {		
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = EARTH_RADIUS * c;
	
	   
	    return dist;
	}

	/**
	 * Calculates the great-circle distance between two points using the "haversine" formula.
	 * The great-circle distance is the shortest distance of the earth's surface. 
	 * 
	 * There is a great web page with a good discussion about mathematical functions on
	 * lat / lon coordinates here: http://www.movable-type.co.uk/scripts/latlong.html
	 * 
	 * @param position1	first position
	 * @param position2	second position
	 * 
	 * @return The great-circle distance between the points in nautical miles
	 */
	public static Double calculateDistance(Position position1, Position position2) 
    {		
        if(( position1 == null ) || ( position2 == null ) || 
                ( position1.getLatitude() == null ) || ( position1.getLongitude() == null ) ||
                ( position2.getLatitude() == null ) || ( position2.getLongitude() == null ))
        {
            return( null );
        }
        
        return( calculateDistance(
                    position1.getLatitude(), position1.getLongitude(), 
                    position2.getLatitude(), position2.getLongitude() ));
	}    
    
	/**
	 * Calculates the great-circle distance between two points using the "haversine" formula.
	 * The great-circle distance is the shortest distance of the earth's surface. 
	 * 
	 * There is a great web page with a good discussion about mathematical functions on
	 * lat / lon coordinates here: http://www.movable-type.co.uk/scripts/latlong.html
	 * 
	 * @param latitude	point latitude
     * @param longitude	point longitude
	 * @param position	the position
	 * 
	 * @return The great-circle distance between the points in nautical miles
	 */
	public static Double calculateDistance(double latitude, double longitude, Position position) 
    {		
        if(( position == null ) || 
                ( position.getLatitude() == null ) || ( position.getLongitude() == null ))
        {
            return( null );
        }
        
        return( calculateDistance( latitude, longitude, 
                    position.getLatitude(), position.getLongitude() ));
	}     
}
