package com.mosaicatm.fuser.datacapture;

import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mosaicatm.lib.playback.Playback;
import com.mosaicatm.matmdata.position.MatmPositionUpdate;

public class FuserDataCaptureMain 
{
	
    private static final Log log = LogFactory.getLog(FuserDataCaptureMain.class);
	
	
	public static void main(String[] args)
	{		
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
				
		FuserDataCaptureLoader<MatmPositionUpdate> loader = new FuserDataCaptureLoaderImpl();
		loader.loadRuntimeProperties(args);
		loader.load();
		FuserDataCaptureApi<MatmPositionUpdate> api = loader.getFuserDataCaptureApi();
		api.start();

		Playback playback = loader.getPlayback();
		if( playback != null && playback.isActive() )
		{
		    try
            {
		        playback.publishStartTime();
                playback.startPlayback();
            }
		    catch (Exception e)
            {
                log.error( "Playback failed", e );
            }
		}
	}
}
