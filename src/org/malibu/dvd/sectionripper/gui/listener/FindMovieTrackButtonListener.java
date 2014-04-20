package org.malibu.dvd.sectionripper.gui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.malibu.dvd.sectionripper.beans.DVDInfo;
import org.malibu.dvd.sectionripper.gui.MessageHandler;
import org.malibu.dvd.sectionripper.gui.PrimaryWindow;

public class FindMovieTrackButtonListener implements ActionListener {
	
	static Logger log = Logger.getLogger(FindMovieTrackButtonListener.class);
	
	private PrimaryWindow window = null;
	
	public FindMovieTrackButtonListener(PrimaryWindow window) {
		this.window = window;
	}

	public void actionPerformed(ActionEvent evt) {
		try {
			log.info("Detching DVD info");
			this.window.detectDVDInfo();
			validateDvdInfo(this.window.getDvdInfo());
		} catch (IOException e) {
			MessageHandler.handleException("Error getting DVD info", e);
		}
	}
	
	private boolean validateDvdInfo(DVDInfo dvdInfo) {
		if(dvdInfo == null || dvdInfo.getDriveInfo() == null || dvdInfo.getDriveInfo().getDriveLetter() == null) {
			MessageHandler.showErrorMessage("Unable to collect drive info, do you have a valid DVD drive selected?");
			return false;
		}
		if(dvdInfo.getMovieTrack() == null) {
			MessageHandler.showErrorMessage( "Unable to collect DVD info, do you have a valid DVD drive selected?  Is a movie inserted?");
			return false;
		}
		return true;
	}
}
