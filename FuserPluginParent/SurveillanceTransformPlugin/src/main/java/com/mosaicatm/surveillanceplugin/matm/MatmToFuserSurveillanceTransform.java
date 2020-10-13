package com.mosaicatm.surveillanceplugin.matm;

import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.fusersurveillance.FuserSurveillanceData;

public class MatmToFuserSurveillanceTransform 
implements Transformer <FuserSurveillanceData, MatmFlight>{

	@Override
	public FuserSurveillanceData transform(MatmFlight matm) {
		if (matm == null)
			return null;

		FuserSurveillanceData surveillance = new FuserSurveillanceData();
		
		if(matm.getAcid() != null)
			surveillance.setCallsign(matm.getAcid());
		
		if(matm.getAircraftAddress() != null)
			surveillance.setAddress(Long.parseLong(matm.getAircraftAddress()));

		if(matm.getAircraftType() != null)
			surveillance.setAircraftType(matm.getAircraftType());

		if(matm.getArrivalAerodrome() != null && matm.getArrivalAerodrome().getIataName() != null)
			surveillance.setArrivalAerodromeIataName(matm.getArrivalAerodrome().getIataName());

		if(matm.getBeaconCode() != null)
			surveillance.setBeaconCode(Integer.parseInt(matm.getBeaconCode()));

		if(matm.getCarrier() != null)
			surveillance.setCarrier(matm.getCarrier());

		if(matm.getDepartureAerodrome() != null && matm.getDepartureAerodrome().getIataName() != null)
			surveillance.setDepartureAerodromeIataName(matm.getDepartureAerodrome().getIataName());

		if(matm.getGufi() != null)
			surveillance.setGufi(matm.getGufi());

		if(matm.getAircraftRegistration() != null)
			surveillance.setRegistration(matm.getAircraftRegistration());

		if(matm.getPosition() != null){
			surveillance.setLatitudeDegrees(matm.getPosition().getLatitude());
			surveillance.setLongitudeDegrees(matm.getPosition().getLongitude());

			if(matm.getPosition().getTimestamp() != null)
				surveillance.setPositionTime(matm.getPosition().getTimestamp());

			if(matm.getPosition().getAltitude() != null)
				surveillance.setAltitudeFeet(matm.getPosition().getAltitude());

			if(matm.getPosition().getHeading() != null)
				surveillance.setHeadingDegrees(matm.getPosition().getHeading());

			if(matm.getPosition().getSpeed() != null)
				surveillance.setSpeedKts(matm.getPosition().getSpeed());

		}

		return surveillance;
	}

}
