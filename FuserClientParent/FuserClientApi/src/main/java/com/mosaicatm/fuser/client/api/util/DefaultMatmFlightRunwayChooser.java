package com.mosaicatm.fuser.client.api.util;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class DefaultMatmFlightRunwayChooser
{
    
    public static String getBestAvailableRunway(MatmFlight flight)
    {
        // Order: actual, user, airline, position derived, model, decision tree
        String runway = null;
        
        if(flight.getDepartureRunwayActual() != null)
            runway = flight.getDepartureRunwayActual();
        else if(flight.getDepartureRunwayUser() != null)
            runway = flight.getDepartureRunwayUser();
        else if(flight.getDepartureRunwayAirline() != null)
            runway = flight.getDepartureRunwayAirline();
        else if(flight.getDepartureRunwayPositionDerived() != null &&
                        !flight.getDepartureRunwayPositionDerived().isNil())
            runway = flight.getDepartureRunwayPositionDerived().getValue();
        else if(flight.getDepartureRunwayModel() != null &&
                        !flight.getDepartureRunwayModel().isNil())
            runway = flight.getDepartureRunwayModel().getValue();
        else if(flight.getDepartureRunwayDecisionTree() != null)
            runway = flight.getDepartureRunwayDecisionTree();
        
        return runway;
    }
    
    public static String getBestAvailableArrivalRunway(MatmFlight flight)
    {
        // Order: actual, user, airline, position derived, model, decision tree, assigned
        String runway = null;
        
        if(flight.getArrivalRunwayActual() != null)
            runway = flight.getArrivalRunwayActual();
        else if(flight.getArrivalRunwayUser() != null)
            runway = flight.getArrivalRunwayUser();
        else if(flight.getArrivalRunwayAirline() != null)
            runway = flight.getArrivalRunwayAirline();
        else if(flight.getArrivalRunwayPositionDerived() != null)
            runway = flight.getArrivalRunwayPositionDerived();
        else if(flight.getArrivalRunwayModel() != null)
            runway = flight.getArrivalRunwayModel();
        else if(flight.getArrivalRunwayDecisionTree() != null)
            runway = flight.getArrivalRunwayDecisionTree();
        else if(flight.getArrivalRunwayAssigned() != null)
            runway = flight.getArrivalRunwayAssigned();
        return runway;
    }
    
}
