package com.mosaicatm.fuser.datacapture;

import com.mosaicatm.fuser.client.api.FuserClientApi;
import com.mosaicatm.lib.playback.Playback;
import com.mosaicatm.matmdata.aircraft.MatmAircraft;
import com.mosaicatm.matmdata.flight.MatmFlight;

public interface FuserDataCaptureLoader<T>
{
    public void load ();
    public void loadRuntimeProperties(String[] envProperties);
    public FuserDataCaptureApi<T> getFuserDataCaptureApi ();
    public FuserClientApi<MatmFlight> getFuserClientApi();
    public FuserClientApi<MatmAircraft> getAircraftFuserClientApi();
    public void setFuserDataCaptureApiConfiguration(FuserDataCaptureConfiguration config);
    public Playback getPlayback();
}
