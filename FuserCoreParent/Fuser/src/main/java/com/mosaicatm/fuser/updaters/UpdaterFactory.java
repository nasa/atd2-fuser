package com.mosaicatm.fuser.updaters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdaterFactory 
implements Updater <Object, Object>
{	
	private List<Updater <Object, Object>> updaters;

	/** A finalUpdater is an updater that must run last after all other updaters */
	private Updater<Object, Object> finalUpdater;
	
	public UpdaterFactory()
	{
	
	}
	
	@Override
	public void update(Object update, Object target)
	{
		//headingUpdater.update(update, target);
		//FlightType result = defaultUpdater.update(update, target);
		
		//not sure it really matters that much that we do this after
		//but does seem like we need to update the update and then
		//merge it back into the target (aggregated data) again
		//commonFieldUpdater.update(update, target);
		for(Updater <Object, Object> updater : updaters)
		{
			updater.update(update, target);
		}
		
		if( finalUpdater != null )
		{
		    finalUpdater.update(update, target);
		}
		
	}
	
    @Override
    public void sweeperUpdate (Date currentTime, Object currentState)
    {
		for(Updater <Object, Object> updater : updaters)
		{
			updater.sweeperUpdate(currentTime, currentState);
		}
		
		if( finalUpdater != null )
		{
		    finalUpdater.sweeperUpdate(currentTime, currentState);
		}
    }     
    
	public void setUpdaters(List<Updater <Object, Object>> updaters)
	{
		this.updaters = updaters;
	}

	public synchronized void addUpdater(Updater <Object, Object> updater)
	{
		if(updaters == null)
			updaters = new ArrayList<>();
		
		updaters.add(updater);
	}
	
	public void setFinalUpdater( Updater<Object, Object> finalUpdater )
	{
	    this.finalUpdater = finalUpdater;
	} 
}
