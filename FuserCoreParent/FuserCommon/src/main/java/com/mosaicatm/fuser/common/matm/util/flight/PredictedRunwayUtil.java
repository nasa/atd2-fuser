package com.mosaicatm.fuser.common.matm.util.flight;

import com.mosaicatm.matmdata.flight.MatmFlight;

public class PredictedRunwayUtil
{
    private static final String USER = "user";
    private static final String POSITION_DERIVED = "position_derived";
    private static final String MODEL = "model";
    private static final String DECISION_TREE = "decision_tree";
    private static final String AIRLINE = "airline";

    public static String getPredictedDepartureRunway(MatmFlight flight)
    {
        String predictedRunway = null;

        if(flight.getDepartureRunwaySource() != null && !flight.getDepartureRunwaySource().isNil()){
            String source = flight.getDepartureRunwaySource().getValue();

            if(source != null && !source.trim().isEmpty()){
                if(USER.equalsIgnoreCase(source))
                {
                    predictedRunway = flight.getDepartureRunwayUser();
                }
                else if(AIRLINE.equalsIgnoreCase(source))
                {
                    predictedRunway = flight.getDepartureRunwayAirline();
                }
                else if(DECISION_TREE.equalsIgnoreCase(source))
                {
                    predictedRunway = flight.getDepartureRunwayDecisionTree();
                }
                else if(POSITION_DERIVED.equalsIgnoreCase(source) && flight.getDepartureRunwayPositionDerived() != null
                                && !flight.getDepartureRunwayPositionDerived().isNil())
                {
                    predictedRunway = flight.getDepartureRunwayPositionDerived().getValue();
                }
                else if(MODEL.equalsIgnoreCase(source) && flight.getDepartureRunwayModel() != null
                                && !flight.getDepartureRunwayModel().isNil())
                {
                    predictedRunway = flight.getDepartureRunwayModel().getValue();
                }
            }
        }

        return predictedRunway;
    }

    public static String getPredictedArrivalRunway(MatmFlight flight)
    {
        String predictedRunway = null;

        if(flight.getArrivalRunwaySource() != null){
            String source = flight.getArrivalRunwaySource();

            if(source != null && !source.trim().isEmpty()){
                if(USER.equalsIgnoreCase(source))
                {
                    predictedRunway = flight.getArrivalRunwayUser();
                }
                else if(AIRLINE.equalsIgnoreCase(source))
                {
                    predictedRunway = flight.getArrivalRunwayAirline();
                }
                else if(DECISION_TREE.equalsIgnoreCase(source))
                {
                    predictedRunway = flight.getArrivalRunwayDecisionTree();
                }
                else if(POSITION_DERIVED.equalsIgnoreCase(source))
                {
                    predictedRunway = flight.getArrivalRunwayPositionDerived();
                }
                else if(MODEL.equalsIgnoreCase(source))
                {
                    predictedRunway = flight.getArrivalRunwayModel();
                }
            }
        }

        return predictedRunway;
    }
}
