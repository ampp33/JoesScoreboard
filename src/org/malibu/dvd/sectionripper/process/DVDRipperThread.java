package org.malibu.dvd.sectionripper.process;

import java.awt.Rectangle;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.malibu.dvd.sectionripper.beans.DVDInfo;
import org.malibu.dvd.sectionripper.gui.MessageHandler;
import org.malibu.dvd.sectionripper.gui.OverallProgressDialog;
import org.malibu.dvd.sectionripper.gui.PrimaryWindow;
import org.malibu.dvd.sectionripper.gui.progress.ProgressToken;
import org.malibu.dvd.sectionripper.io.MencoderProgressStreamHandler;
import org.malibu.dvd.sectionripper.utilities.Util;

public class DVDRipperThread implements Runnable {
	
	private static final String CURRENT_CLIP_PROGRESS_PREFIX = "Current Clip Progress: ";
	
	static Logger log = Logger.getLogger(DVDRipperThread.class);
	
	private PrimaryWindow window = null;
	private JTable table;
	
	public DVDRipperThread(PrimaryWindow window, JTable table) {
		this.window = window;
		this.table = table;
	}
	
	public void run() {
		// display progress dialog
		OverallProgressDialog progressDialog = new OverallProgressDialog();
		progressDialog.show();
		
		process(progressDialog);
		
		// hide progress window
		progressDialog.remove();
	}
	
	private void process(OverallProgressDialog progressDialog) {
		// get output directory path
				String outputDirectoryPath = this.window.getOutputDirectory();
				if(!validateOutputDirectory(outputDirectoryPath)) {
					return;
				}
				
				// get all required DVD info
				DVDInfo dvdInfo = null;
				try { 
					dvdInfo = this.window.getDvdInfo();
				} catch (Exception e) {
					MessageHandler.handleException("Error occurred getting DVD info", e);
					return;
				}
				if(!validateDvdInfo(dvdInfo)) {
					return;
				}
				
				// attempt to process!
				ProgressToken overallProgressToken = new ProgressToken();
				progressDialog.registerOverallProgressToken(overallProgressToken);
				boolean runFirstPass = false;
				DefaultTableModel clipTableModel = (DefaultTableModel) this.table.getModel();
				for(int clipIndex = 0; clipIndex < clipTableModel.getRowCount(); clipIndex++) {
					// set overall progress
					overallProgressToken.setPercentComplete((int)(((double)clipIndex / ((double)clipTableModel.getRowCount())) * 100));
					
					// select the current row being processed, and scroll to it if necessary
					this.table.scrollRectToVisible(new Rectangle(this.table.getCellRect(clipIndex, 0, false)));
					this.table.setRowSelectionInterval(clipIndex, clipIndex);
					// get segment information from table
					int clipId = (Integer)clipTableModel.getValueAt(clipIndex, 0);
					String fileName = (String)clipTableModel.getValueAt(clipIndex, 1);
					String startTime = Util.convertSegmentToMencoderFriendlyFormat((String)clipTableModel.getValueAt(clipIndex, 2));
					String endTime = Util.convertSegmentToMencoderFriendlyFormat((String)clipTableModel.getValueAt(clipIndex, 3));
					boolean isProcessedAlready = (Boolean)clipTableModel.getValueAt(clipIndex, 4);
					
					log.info("Attempting to process clip:");
					log.info("fileName: " + fileName);
					log.info("startTime: " + startTime);
					log.info("endTime: " + endTime);
					log.info("isProcessedAlready: " + isProcessedAlready);
					
					// setup clip progress bar
					ProgressToken subtaskProgressToken = new ProgressToken();
					subtaskProgressToken.setPercentComplete(0);
					subtaskProgressToken.setTaskName(CURRENT_CLIP_PROGRESS_PREFIX + fileName + "(clip: " + clipId + ")");
					progressDialog.registerSubtaskProgressToken(subtaskProgressToken);
					
					// skip already processed rows
					if(isProcessedAlready) {
						subtaskProgressToken.setPercentComplete(100);
						continue;
					}
					
					// check if start or end time is longer than the length of the movie
					if(Util.getSegmentLengthInSeconds(startTime) > Double.valueOf(dvdInfo.getMovieLength())
							|| Util.getSegmentLengthInSeconds(endTime) > Double.valueOf(dvdInfo.getMovieLength())) {
						MessageHandler.showMessage("Start or end time longer than movie length!  Verify lengths on clip #: " + clipId);
						subtaskProgressToken.setPercentComplete(100);
						continue;
					}
					
					int segmentDuration = Util.getSegmentLengthInSeconds(endTime) - Util.getSegmentLengthInSeconds(startTime);
					MencoderProgressStreamHandler progressHandler = new MencoderProgressStreamHandler(segmentDuration, subtaskProgressToken);
					
					try {
						// call to rip actual clip from DVD
						Util.ripSegmentFromDVD(progressHandler, fileName + ".avi", outputDirectoryPath, dvdInfo, startTime, endTime, runFirstPass);
					} catch (IOException e) {
						log.error("Error occurred processing clip #" + clipId);
						log.error("Showing user error message: Error occurred processing clip #" + clipId + ", continue processing?");
						// if an error occurred, show a dialog asking if we want to continue processing
						int optionChoice = JOptionPane.showConfirmDialog(
							    this.window,
							    "Error occurred processing clip #" + clipId + ", continue processing?",
							    "Error",
							    JOptionPane.YES_NO_OPTION);
						// if the user selects "no", end processing
						if(optionChoice == 2) {
							log.error("User aborted processing after they ran into an error");
							return;
						}
						log.error("User decided to continue processing after they ran into an error");
						continue;
					} finally {
						subtaskProgressToken.setPercentComplete(100);
						runFirstPass = false;
					}
					
					// mark row as processed (will only get to this point if no errors occurred
					clipTableModel.setValueAt(Boolean.TRUE, clipIndex, 4);
					
					// redraw window so newly checked checkbox is viewable
					this.table.repaint();
				}
				
				// set overall progress to 100%
				overallProgressToken.setPercentComplete(100);
				
				// show window telling user that processing is complete
				MessageHandler.showMessage("Processing complete!");
	}

	private boolean validateOutputDirectory(String outputDirectoryPath) {
		if(outputDirectoryPath == null || outputDirectoryPath.trim().length() == 0) {
			MessageHandler.showErrorMessage("Please set an output directory");
			return false;
		}
		return true;
	}
	
	private boolean validateDvdInfo(DVDInfo dvdInfo) {
		if(dvdInfo == null || dvdInfo.getDriveInfo() == null || dvdInfo.getDriveInfo().getDriveLetter() == null) {
			MessageHandler.showErrorMessage("Unable to collect drive info, do you have a valid DVD drive selected?");
			return false;
		}
		if(dvdInfo.getMovieTrack() == null) {
			MessageHandler.showErrorMessage("Unable to collect DVD info, do you have a valid DVD drive selected?  Is a movie inserted?");
			return false;
		}
		return true;
	}
	
}
