package org.malibu.dvd.sectionripper.gui.progress;

public class ProgressToken {
	private int percentComplete = 0;
	private String taskName = null;

	public int getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(int percentComplete) {
		this.percentComplete = percentComplete;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
}
