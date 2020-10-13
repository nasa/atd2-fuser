package com.mosaicatm.fuser.util;

import java.util.List;

import com.mosaicatm.fuser.store.FuserStore;
import com.mosaicatm.lib.time.Clock;
import com.mosaicatm.lib.time.ClockListener;
import com.mosaicatm.lib.time.ClockSync;

/**
 * A bit of a hack to clear the fuser when the simulation stops.
 * 
 * @author mgarland
 */
public class SimulationClock implements Clock, ClockListener
{
	private long base;
	private long sync;
	private float rate;
	private boolean paused;

	private ClockState state;
	private FuserStore<?, ?> store;

	private String name;

	public void setStore(FuserStore<?, ?> store)
	{
		this.store = store;
	}

	@Override
	public void clockStateChanged(ClockSync sync)
	{
		if(sync.getCurrentTime() != null)
		{
			this.base = sync.getCurrentTime().getTime();
			this.sync = sync.getSystemTime() == null ? getTrueSystemTimeInMillis() : sync.getSystemTime().getTime();
		}
		if(sync.getSpeed() != 0.0f)
		{
			this.rate = sync.getSpeed();
		}
		if(sync.getClockState() != null)
		{
			if(sync.getClockState() == ClockState.PAUSE)
				pause();
			else if(sync.getClockState() == ClockState.STOP)
				stop();
			else
				resume();
			state = sync.getClockState();
		}
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setRate(float rate)
	{
		this.rate = rate;
	}

	@Override
	public float getRate()
	{
		return rate;
	}

	@Override
	public void setOffset(long offset)
	{
		// Ignore
	}

	@Override
	public long getOffset()
	{
		// Ignore
		return 0;
	}

	@Override
	public long getTimeInMillis()
	{
		if(paused)
			return base;
		return base + (long)((getTrueSystemTimeInMillis() - sync) * rate);
	}

	@Override
	public long getTimeInSeconds()
	{
		return getTimeInMillis() / 1000;
	}

	@Override
	public long getTrueSystemTimeInMillis()
	{
		return System.currentTimeMillis();
	}

	@Override
	public ClockState getClockState()
	{
		return state;
	}

	@Override
	public void setClockState(ClockState state)
	{
		this.state = state;
	}

	@Override
	public boolean isPaused()
	{
		return ClockState.PAUSE.equals(state) || ClockState.STOP.equals(state);
	}

	@Override
	public boolean isRunning()
	{
		return !ClockState.PAUSE.equals(state) && !ClockState.STOP.equals(state);
	}

	@Override
	public void start()
	{
		base = System.currentTimeMillis();
		sync = base;
		setClockState(ClockState.START);
	}

	@Override
	public void stop()
	{
        setClockState (ClockState.STOP);
        if(store != null)
        	store.clear();
	}

	@Override
	public boolean addClockListener(ClockListener listener)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeClockListener(ClockListener listener)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<ClockListener> getClockListeners()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long convertToClockTime(long time)
	{
		return time + (long)((getTrueSystemTimeInMillis() - sync) * rate);
	}

	@Override
	public void step(long time)
	{
		// Ignore
	}
	
	private void pause()
	{
		if(!paused)
		{
			base = getTimeInMillis();
			sync = 0;
			paused = true;
		}
	}
	
	private void resume()
	{
		if(paused)
		{
			if(sync == 0)
				sync = getTrueSystemTimeInMillis();
			paused = false;
		}
	}
}
