package org.malibu.joe.scoreboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class TimerThread implements Runnable {
	
	private boolean run = true;
	private boolean pause = true;
	
	private int lastMinute = 0; /* like all decent coding projects */
	private int lastSecond = 0;
	private int lastMilli = 0;
	
	private List<Callback> minuteCallbacks = new ArrayList<>();
	private List<Callback> secondCallbacks = new ArrayList<>();
	private List<Callback> milliCallbacks = new ArrayList<>();
	
	public void run() {
		while(run) {
			Calendar time = Calendar.getInstance();
			int currentMinute = time.get(Calendar.MINUTE);
			int currentSecond = time.get(Calendar.SECOND);
			int currentMilli = time.get(Calendar.MILLISECOND);
			
			// minute callbacks
			if(lastMinute != currentMinute) {
				lastMinute = currentMinute;
				if(!pause) {
					for (Callback callback : minuteCallbacks) {
						callback.execute();
					}
				}
			}
			
			// second callbacks
			if(lastSecond != currentSecond) {
				lastSecond = currentSecond;
				if(!pause) {
					for (Callback callback : secondCallbacks) {
						callback.execute();
					}
				}
			}
			
			// milli callbacks
			if(lastMilli != currentMilli) {
				lastMilli = currentMilli;
				if(!pause) {
					for (Callback callback : milliCallbacks) {
						callback.execute();
					}
				}
			}
			
			// sleep for short duration
			try { Thread.sleep(50); } catch (InterruptedException e) { run = false; }
		}
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}
	
	public void shutdown() {
		this.run = false;
	}
	
	public void reset() {
		Calendar time = Calendar.getInstance();
		lastMinute = time.get(Calendar.MINUTE);
		lastSecond = time.get(Calendar.SECOND);
		lastMilli = time.get(Calendar.MILLISECOND);
	}
	
	public void addMinuteCallback(Callback callback) {
		this.minuteCallbacks.add(callback);
	}
	
	public void addSecondCallback(Callback callback) {
		this.secondCallbacks.add(callback);
	}
	
	public void addMilliCallback(Callback callback) {
		this.milliCallbacks.add(callback);
	}
	
}
