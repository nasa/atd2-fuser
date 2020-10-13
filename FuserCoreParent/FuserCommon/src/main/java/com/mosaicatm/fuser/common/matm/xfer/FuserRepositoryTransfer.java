package com.mosaicatm.fuser.common.matm.xfer;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

@XmlRootElement
public class FuserRepositoryTransfer
{    
    private ArrayList<MatmFlight> flightList = new ArrayList<>();
    private ArrayList<GufiMetaData> metaDataList = new ArrayList<>();    
    private ArrayList<MatmAircraft> aircraftList = new ArrayList<>();    
    private ArrayList<MatmSectorAssignment> sectorAssignmentList = new ArrayList<>();    
    
    @XmlElementWrapper
    @XmlElement(name="flight")      
    public ArrayList<MatmFlight> getFlightList()
    {
        return flightList;
    }

    public void setFlightList( ArrayList<MatmFlight> flightList )
    {
        this.flightList = flightList;
    }

    @XmlElementWrapper
    @XmlElement(name="metaData")      
    public ArrayList<GufiMetaData> getMetaDataList()
    {
        return metaDataList;
    }

    public void setMetaDataList( ArrayList<GufiMetaData> metaDataList )
    {
        this.metaDataList = metaDataList;
    }

    @XmlElementWrapper
    @XmlElement(name="aircraft")     
    public ArrayList<MatmAircraft> getAircraftList()
    {
        return aircraftList;
    }

    public void setAircraftList( ArrayList<MatmAircraft> aircraftList )
    {
        this.aircraftList = aircraftList;
    }
    
    @XmlElementWrapper
    @XmlElement(name="sector")     
    public ArrayList<MatmSectorAssignment> getSectorAssignmentList()
    {
        return sectorAssignmentList;
    }

    public void setSectorAssignmentList( ArrayList<MatmSectorAssignment> aircraftList )
    {
        this.sectorAssignmentList = aircraftList;
    }    
}
