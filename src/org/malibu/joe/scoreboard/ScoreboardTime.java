package org.malibu.joe.scoreboard;

public class ScoreboardTime {
	
	private int minutes = 0;
	private int seconds = 0;
	private int milli = 0;
	
	public ScoreboardTime(int minutes, int seconds, int milli) {
		setMinutes(minutes);
		setSeconds(seconds);
		setMilli(milli);
	}
	
	public ScoreboardTime(ScoreboardTime scoreboardTime) {
		setMinutes(scoreboardTime.getMinutes());
		setSeconds(scoreboardTime.getSeconds());
		setMilli(scoreboardTime.getMilli());
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public int getMilli() {
		return milli;
	}

	public void setMilli(int milli) {
		this.milli = milli;
	}

}
