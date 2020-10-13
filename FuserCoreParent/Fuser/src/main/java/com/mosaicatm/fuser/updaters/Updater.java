package com.mosaicatm.fuser.updaters;

import java.util.Date;

public interface Updater <U, T>
{
	public void update (U update, T target);
    
    public void sweeperUpdate (Date currentTime, U currentState);
}
