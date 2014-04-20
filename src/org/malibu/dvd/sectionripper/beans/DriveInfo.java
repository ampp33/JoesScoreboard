package org.malibu.dvd.sectionripper.beans;

public class DriveInfo {
	private String driveLetter = null;
	private String driveType = null;
	private String driveDisplayName = null;
	
	public String getDriveLetter() {
		return driveLetter;
	}
	public void setDriveLetter(String driveLetter) {
		this.driveLetter = driveLetter;
	}
	public String getDriveType() {
		return driveType;
	}
	public void setDriveType(String driveType) {
		this.driveType = driveType;
	}
	public String getDriveDisplayName() {
		return driveDisplayName;
	}
	public void setDriveDisplayName(String driveDisplayName) {
		this.driveDisplayName = driveDisplayName;
	}
	@Override
	public String toString() {
		return getDriveDisplayName();
	}
}
