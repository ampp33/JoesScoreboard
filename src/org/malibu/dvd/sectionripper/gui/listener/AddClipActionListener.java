package org.malibu.dvd.sectionripper.gui.listener;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.malibu.dvd.sectionripper.gui.MessageHandler;
import org.malibu.dvd.sectionripper.gui.PrimaryWindow;
import org.malibu.dvd.sectionripper.utilities.Util;

public class AddClipActionListener implements ActionListener {
	
	static Logger log = Logger.getLogger(AddClipActionListener.class);
	
	private PrimaryWindow window = null;
	private JTable table = null;
	
	/**
	 * @param window JWindow that action was called from 
	 * @param table JTable to add clip row to
	 */
	public AddClipActionListener(PrimaryWindow window, JTable table) {
		this.window = window;
		this.table = table;
	}

	public void actionPerformed(ActionEvent e) {
		log.debug("Adding clip to table");
		
		// get clip info from user inputs
		String clipName = this.window.getUserEnteredClipName();
		String startTime = this.window.getUserEnteredStartTime();
		String endTime = this.window.getUserEnteredEndTime();
		
		log.debug("Clip name: " + clipName);
		log.debug("Start time: " + startTime);
		log.debug("End time: " + endTime);
		
		// get next clip ID
		DefaultTableModel model = (DefaultTableModel) this.table.getModel();
		int maxClipId = 0;
		for(int i = 0; i < model.getRowCount(); i++) {
			if((Integer)model.getValueAt(i, 0) > maxClipId) {
				maxClipId = (Integer)model.getValueAt(i, 0);
			}
		}
		log.debug("Clip ID: " + maxClipId);
		
		// if clip name is empty, give it a default name
		if(clipName == null || clipName.trim().length() == 0) {
			clipName = "Clip_" + (maxClipId + 1);
			log.debug("Clip name is empty, using default: " + clipName);
		}
		
		// don't add row if errors were detected in the user input
		if(checkForNewClipParamErrors(clipName, startTime, endTime) == true) {
			log.debug("Clip parameter errors detected");
			return;
		}
		
		log.debug("Adding clip row to table");
		model.addRow(new Object[]{maxClipId + 1, clipName, startTime.trim(), endTime.trim(), false});
		
		// scroll to the newly added row
		log.debug("Scroll JTable to the newly added row");
		this.table.scrollRectToVisible(new Rectangle(this.table.getCellRect(maxClipId, 0, false)));
		
		this.window.resetUserEnteredClipParams();
	}

	/**
	 * Check if clip name, start, or end time is invalid, and if so, return true, and false otherwise
	 * 
	 * @param clipName
	 * @param startTime
	 * @param endTime
	 * @return true if errors exist, false otherwise
	 */
	private boolean checkForNewClipParamErrors(String clipName, String startTime, String endTime) {
		if(!isValidFileName(clipName)) {
			String badFilenameMessage = "Clip name will be used in the filename, and has been detected invalid."
								+ "\nA filename cannot contain any of the following characters: \\ / : * ? \" < > |";
			MessageHandler.showErrorMessage(badFilenameMessage);
			return true;
		}
		if(startTime == null || "".equals(startTime.trim())) {
			MessageHandler.showErrorMessage("Start time not specified");
			return true;
		}
		if(endTime == null || "".equals(endTime.trim())) {
			MessageHandler.showErrorMessage("Snd time not specified");
			return true;
		}
		if(Util.getSegmentLengthInSeconds(startTime) == -1) {
			MessageHandler.showErrorMessage("Start time in incorrect format, must be in format 00:00 or 00 00");
			return true;
		}
		if(Util.getSegmentLengthInSeconds(endTime) == -1) {
			MessageHandler.showErrorMessage("End time in incorrect format, must be in format 00:00 or 00 00");
			return true;
		}
		
		int startTimeInt = Util.getSegmentLengthInSeconds(startTime);
		int endTimeInt = Util.getSegmentLengthInSeconds(endTime);
		if(endTimeInt <= startTimeInt) {
			MessageHandler.showErrorMessage("Invalid duration, end time is before or equal to start time");
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if supplied filename contains all valid characters
	 * 
	 * @param potentialFileName
	 * @return
	 */
	private boolean isValidFileName(String potentialFileName) {
		if(potentialFileName == null || potentialFileName.trim().length() == 0) return false;
		for(int i = 0; i < potentialFileName.length(); i++) {
			if(potentialFileName.charAt(i) == '\\'
					|| potentialFileName.charAt(i) == '/'
					|| potentialFileName.charAt(i) == ':'
					|| potentialFileName.charAt(i) == '*'
					|| potentialFileName.charAt(i) == '?'
					|| potentialFileName.charAt(i) == '"'
					|| potentialFileName.charAt(i) == '<'
					|| potentialFileName.charAt(i) == '>'
					|| potentialFileName.charAt(i) == '|') {
				return false;
			}
		}
		return true;
	}
}
