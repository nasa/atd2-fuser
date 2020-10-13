package com.mosaicatm.surveillanceplugin.matm;

import java.util.Date;

import com.mosaicatm.matmdata.fusersurveillance.FuserSurveillanceData;

public class FuserSurveillanceDataExtension {
	
	public FuserSurveillanceDataExtension(FuserSurveillanceData data){
		this.fuserSurveillanceData = data;
	}
	
	private FuserSurveillanceData fuserSurveillanceData;
	private Date sourceTimestamp;
	private String source;
	private String aerodromeIataName;
	
	public FuserSurveillanceData getFuserSurveillanceData() {
		return fuserSurveillanceData;
	}
	
	public void setFuserSurveillanceData(FuserSurveillanceData fuserSurveillanceData) {
		this.fuserSurveillanceData = fuserSurveillanceData;
	}
	
	public Date getSourceTimestamp() {
		return sourceTimestamp;
	}
	
	public void setSourceTimestamp(Date sourceTimestamp) {
		this.sourceTimestamp = sourceTimestamp;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}

	public String getAerodromeIataName() {
		return aerodromeIataName;
	}

	public void setAerodromeIataName(String aerodromeIataName) {
		this.aerodromeIataName = aerodromeIataName;
	}
}
