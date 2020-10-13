package com.mosaicatm.fuser.client.api;

import java.util.TimeZone;

import com.mosaicatm.fuser.client.api.event.FuserProcessedEventListener;
import com.mosaicatm.fuser.client.api.event.FuserReceivedEventListener;
import com.mosaicatm.fuser.client.api.impl.FuserClientApiSpringLoader;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.sector.MatmSectorAssignment;

public class FuserClientApiMain
{
    public void initialize (FuserClientApiConfiguration configs)
    {
        FuserClientApiSpringLoader loader = new FuserClientApiSpringLoader ();

        if (configs != null)
            loader.setApiConfiguration(configs);

        loader.setLoadServicesClients(false);

        loader.load();

        DataListener<MatmFlight> flightListener = new DataListener<>("flight listener");
        DataListener<MatmAircraft> aircraftListener = new DataListener<>("aircraft listener");
        DataListener<MatmSectorAssignment> sectorAssignmentListener = new DataListener<>("sector assignment listener");

        FuserClientApi<MatmFlight> flightApi = loader.getApi();
        flightApi.registerProcessedEventListener(flightListener);
        flightApi.registerReceivedEventListener(flightListener);

        FuserClientApi<MatmAircraft> aircraftApi = loader.getAircraftApi();
        aircraftApi.registerProcessedEventListener(aircraftListener);
        aircraftApi.registerReceivedEventListener(aircraftListener);

        FuserClientApi<MatmSectorAssignment> sectorAssignmentApi = loader.getSectorAssignmentApi();
        sectorAssignmentApi.registerProcessedEventListener(sectorAssignmentListener);
        sectorAssignmentApi.registerReceivedEventListener(sectorAssignmentListener);        
        
        //flightApi.start(false);
        //aircraftApi.start(false);
    }

    public static void main (String[] args)
    {    
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        FuserClientApiMain clientApi = new FuserClientApiMain ();
        clientApi.initialize(null);
    }

    private class DataListener <T>
    implements FuserReceivedEventListener<T>,
    FuserProcessedEventListener<T>
    {
        private String name;

        public DataListener(String name)
        {
            this.name = name;
        }

        @Override
        public void dataAdded(T data)
        {
            System.out.println(name + " added data " + data);
        }

        @Override
        public void dataUpdated(T data, T update)
        {
            System.out.println(name + " updated data " + data);
        }

        @Override
        public void dataRemoved(T data)
        {
            System.out.println(name + " removed data " + data);
        }

        @Override
        public void receivedAdd(T data)
        {
            System.out.println(name + " received add " + data);
        }

        @Override
        public void receivedUpdate(T existingBeforeUpdating, T data)
        {
            System.out.println(name + " received update " + data);
        }

        @Override
        public void receivedRemove(T data)
        {
            System.out.println(name + " received remove " + data);
        }
    }
}
