package com.mosaicatm.fuser.client.api;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import com.mosaicatm.fuser.client.api.impl.FuserClientApiSpringLoader;
import com.mosaicatm.matmdata.common.Aerodrome;
import com.mosaicatm.matmdata.flight.MatmFlight;
import com.mosaicatm.matmdata.flight.extension.AsdexExtension;
import com.mosaicatm.matmdata.flight.extension.MatmFlightExtensions;

public class ApiFilterTest {

    @Test
    public void testAdd() {
        FuserClientApiSpringLoader loader = new FuserClientApiSpringLoader();
        loader.setLoadJms(false);
        loader.setLoadRoutes(false);
        loader.setLoadServicesClients(false);

        FuserClientApiConfiguration configuration = new FuserClientApiConfiguration();
        configuration.setAirportFilterActive(true);
        configuration.setAirportsOfInterest("CLT,JFK");

        loader.setApiConfiguration(configuration);
        loader.load();
        FuserClientApi<MatmFlight> api = loader.getApi();

        //arrival flight
        MatmFlight flight = createFlight (UUID.randomUUID().toString(), "TST123","CLT","ATL");
        api.handleAdd(flight);
        assertEquals (1, api.getStore().size());

        //departure flight
        flight = createFlight (UUID.randomUUID().toString(), "TST123","IAD","JFK");
        api.handleAdd(flight);
        assertEquals (2, api.getStore().size());

        //this one does not match our filter
        flight = createFlight (UUID.randomUUID().toString(), "TST123","MEM","IAD");
        api.handleAdd(flight);
        assertEquals (2, api.getStore().size());

        flight = createFlight (UUID.randomUUID().toString(), "TST123",null,null);
    }

    public void testAddWithAsdexFilter() {
        FuserClientApiSpringLoader loader = new FuserClientApiSpringLoader();
        loader.setLoadJms(false);
        loader.setLoadRoutes(false);
        loader.setLoadServicesClients(false);

        FuserClientApiConfiguration configuration = new FuserClientApiConfiguration();
        configuration.setAirportFilterActive(true);
        configuration.setSurfaceAirportFilterActive(true);
        configuration.setAirportsOfInterest("CLT,JFK");

        loader.setApiConfiguration(configuration);
        loader.load();
        FuserClientApi<MatmFlight> api = loader.getApi();

        //arrival flight
        MatmFlight flight = createFlight (UUID.randomUUID().toString(), "TST123","CLT","ATL");
        api.handleAdd(flight);
        assertEquals (1, api.getStore().size());

        //departure flight
        flight = createFlight (UUID.randomUUID().toString(), "TST124","IAD","JFK");
        api.handleAdd(flight);
        assertEquals (2, api.getStore().size());

        //this one does not match our filter
        flight = createFlight (UUID.randomUUID().toString(), "TST124","MEM","IAD");
        api.handleAdd(flight);
        assertEquals (2, api.getStore().size());

        //no airport but we do have an asdex airport
        flight = createFlight (UUID.randomUUID().toString(), "TST125",null,null);
        AsdexExtension extension = new AsdexExtension();
        Aerodrome cltAerodrome = new Aerodrome();
        cltAerodrome.setIataName( "CLT" );
        extension.setAsdexAirport( cltAerodrome );
        flight.setExtensions(new MatmFlightExtensions());
        flight.getExtensions().setAsdexExtension(extension);

        api.handleAdd(flight);
        assertEquals (3, api.getStore().size());

        //no airport but we do have an asdex airport, but it's not one in our filter
        flight = createFlight (UUID.randomUUID().toString(), "TST125",null,null);
        extension = new AsdexExtension();
        Aerodrome altAerodrome = new Aerodrome();
        altAerodrome.setIataName( "ATL" );
        extension.setAsdexAirport( altAerodrome );
        flight.setExtensions(new MatmFlightExtensions());
        flight.getExtensions().setAsdexExtension(extension);

        api.handleAdd(flight);
        assertEquals (3, api.getStore().size());

    }



    @Test
    public void testUpdate() {
        FuserClientApiSpringLoader loader = new FuserClientApiSpringLoader();
        loader.setLoadJms(false);
        loader.setLoadRoutes(false);
        loader.setLoadServicesClients(false);

        FuserClientApiConfiguration configuration = new FuserClientApiConfiguration();
        configuration.setAirportFilterActive(true);
        configuration.setAirportsOfInterest("CLT,JFK");

        loader.load();
        FuserClientApi<MatmFlight> api = loader.getApi();

        //arrival flight
        MatmFlight flight = createFlight (UUID.randomUUID().toString(), "TST123","CLT","ATL");
        api.handleUpdate(flight,flight);
        assertEquals (1, api.getStore().size());

        //departure flight
        flight = createFlight (UUID.randomUUID().toString(), "TST123","IAD","JFK");
        api.handleUpdate(flight,flight);
        assertEquals (2, api.getStore().size());

        //this one does not match our filter
        flight = createFlight (UUID.randomUUID().toString(), "TST123","MEM","IAD");
        api.handleUpdate(flight,flight);
        assertEquals (2, api.getStore().size());
    }




    private MatmFlight createFlight (String gufi, String acid, String arrArpt, String depApt)
    {
        MatmFlight flat = new MatmFlight();
        flat.setGufi(gufi);
        flat.setAcid(acid);
        Aerodrome arrivalAerodrome = new Aerodrome();
        arrivalAerodrome.setIataName( arrArpt );
        flat.setArrivalAerodrome( arrivalAerodrome );
        Aerodrome departureAerodrome = new Aerodrome();
        departureAerodrome.setIataName( depApt );
        flat.setDepartureAerodrome( departureAerodrome );

        return flat;		
    }

}
