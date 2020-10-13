package com.mosaicatm.surveillanceplugin.matm;

import com.mosaicatm.lib.util.Transformer;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.common.Position;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.fusersurveillance.FuserSurveillanceData;

public class FuserSurveillanceDataToMatmTransform 
implements Transformer <MatmFlight, FuserSurveillanceDataExtension>{

	@Override
	public MatmFlight transform(FuserSurveillanceDataExtension extension) {
		if (extension == null || extension.getFuserSurveillanceData() == null)
			return null;

		MatmFlight matm = new MatmFlight();

		matm.setTimestamp(extension.getSourceTimestamp());
		matm.setLastUpdateSource(extension.getSource());
		
		Aerodrome surfaceAerodrome = new Aerodrome();
		surfaceAerodrome.setIataName(extension.getAerodromeIataName());
		matm.setSurfaceAirport(surfaceAerodrome);

		FuserSurveillanceData surveillance = extension.getFuserSurveillanceData();
		matm.setTimestampSource(extension.getSourceTimestamp());
		matm.setTimestampSourceReceived(surveillance.getTimestampSourceReceived());
		matm.setTimestampSourceProcessed(surveillance.getTimestampSourceProcessed());
		if(surveillance.getAddress() != null)
			matm.setAircraftAddress(surveillance.getAddress().toString());

		if(surveillance.getAircraftType() != null)
			matm.setAircraftType(surveillance.getAircraftType());

		if(surveillance.getArrivalAerodromeIataName() != null){
			Aerodrome arrivalAerodrome = new Aerodrome();
			arrivalAerodrome.setIataName(surveillance.getArrivalAerodromeIataName());
			matm.setArrivalAerodrome(arrivalAerodrome);
		}

		if(surveillance.getBeaconCode() != null)
			matm.setBeaconCode(surveillance.getBeaconCode().toString());

		if(surveillance.getCallsign() != null)
			matm.setAcid(surveillance.getCallsign());

		if(surveillance.getCarrier() != null)
			matm.setCarrier(surveillance.getCarrier());

		if(surveillance.getDepartureAerodromeIataName() != null){
			Aerodrome departureAerodrome = new Aerodrome();
			departureAerodrome.setIataName(surveillance.getDepartureAerodromeIataName());
			matm.setDepartureAerodrome(departureAerodrome);
		}


		if(surveillance.getGufi() != null)
			matm.setGufi(surveillance.getGufi());

		if(surveillance.getRegistration() != null)
			matm.setAircraftRegistration(surveillance.getRegistration());

		Position position = new Position();
		position.setLatitude(surveillance.getLatitudeDegrees());
		position.setLongitude(surveillance.getLongitudeDegrees());

		position.setSource(extension.getSource());

		if(surveillance.getPositionTime() != null)
			position.setTimestamp(surveillance.getPositionTime());

		if(surveillance.getAltitudeFeet() != null)
			position.setAltitude(surveillance.getAltitudeFeet());

		if(surveillance.getHeadingDegrees() != null)
			position.setHeading(surveillance.getHeadingDegrees());

		if(surveillance.getSpeedKts() != null)
			position.setSpeed(surveillance.getSpeedKts());

		matm.setPosition(position);

		return matm;
	}

}
