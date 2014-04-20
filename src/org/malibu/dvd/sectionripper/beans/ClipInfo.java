package org.malibu.dvd.sectionripper.beans;

public class ClipInfo {
	private int clipId;
	private String startTime;
	private String endTime;
	private boolean processed;
	
	public int getClipId() {
		return clipId;
	}
	public void setClipId(int clipId) {
		this.clipId = clipId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public boolean isProcessed() {
		return processed;
	}
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
}
