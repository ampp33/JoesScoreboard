package org.malibu.dvd.sectionripper.beans;

public class DVDInfo {
	private String dvdName;
	private String movieTrack;
	private double movieLength = -1;
	private String croppingCoordinates = null;
	private DriveInfo driveInfo;
	
	public String getDvdName() {
		return dvdName;
	}
	public void setDvdName(String dvdName) {
		this.dvdName = dvdName;
	}
	public String getMovieTrack() {
		return movieTrack;
	}
	public void setMovieTrack(String movieTrack) {
		this.movieTrack = movieTrack;
	}
	public DriveInfo getDriveInfo() {
		return driveInfo;
	}
	public void setDriveInfo(DriveInfo driveInfo) {
		this.driveInfo = driveInfo;
	}
	public double getMovieLength() {
		return movieLength;
	}
	public void setMovieLength(double movieLength) {
		this.movieLength = movieLength;
	}
	public String getCroppingCoordinates() {
		return croppingCoordinates;
	}
	public void setCroppingCoordinates(String croppingCoordinates) {
		this.croppingCoordinates = croppingCoordinates;
	}
}
