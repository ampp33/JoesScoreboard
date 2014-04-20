package org.malibu.dvd.sectionripper.gui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.malibu.dvd.sectionripper.gui.PrimaryWindow;
import org.malibu.dvd.sectionripper.process.DVDRipperThread;

public class BeginProcessingButtonListener implements ActionListener {
	
	static Logger log = Logger.getLogger(BeginProcessingButtonListener.class);
	
	private PrimaryWindow window = null;
	private JTable table;
	
	public BeginProcessingButtonListener(PrimaryWindow window, JTable table) {
		this.window = window;
		this.table = table;
	}

	public void actionPerformed(ActionEvent arg0) {
		if(checkIfReadyToProcess()) {
			// being processing
			process();
		}
	}
	
	/**
	 * Do last minute checks before we begin processing
	 * 
	 * @return
	 */
	private boolean checkIfReadyToProcess() {
		// check, and warn user if any clip names are the same, and that the last clip
		// with the same name will overwrite the first
		int rowCount = this.table.getModel().getRowCount();
		boolean duplicateClipNamesExist = false;
		List<String> clipNames = new ArrayList<String>();
		for(int i = 0; i < rowCount; i++) {
			// "first" column is the clip name
			String clipName = (String)this.table.getValueAt(i, 1);
			if(clipNames.contains(clipName)) {
				duplicateClipNamesExist = true;
			} else {
				clipNames.add(clipName);
			}
		}
		if(duplicateClipNamesExist) {
			String duplicateClipNameWarningMessage = "Some clips have the same clip name,"
												+ " do you want to continue and have same name clips overwrite eachother?"
												+ " If you're not sure, click 'No' and go back and rename clips that have the same name";
			log.warn("Showing user warning: " + duplicateClipNameWarningMessage);
			int option = JOptionPane.showOptionDialog(window,
					"Potential issue",
					duplicateClipNameWarningMessage,
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, null, null);
			// if user selected "no", discontinue processing
			if(option == 1) {
				return false;
			}
			log.warn("User continuing with duplicate name clips");
		}
		
		return true;
	}
	
	private void process() {
		// create processing thread and "processing" dialog window
		new Thread(new DVDRipperThread(this.window, this.table)).start();
	}
}
