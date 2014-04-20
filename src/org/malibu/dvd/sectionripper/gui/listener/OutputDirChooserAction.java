package org.malibu.dvd.sectionripper.gui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import org.malibu.dvd.sectionripper.gui.PrimaryWindow;

public class OutputDirChooserAction implements ActionListener {
	
	private PrimaryWindow window;
	
	public OutputDirChooserAction(PrimaryWindow window) {
		this.window = window;
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser(); 
	    chooser.setCurrentDirectory(new java.io.File("."));
	    chooser.setDialogTitle("Choose output directory");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    // disable the "All files" option.
	    chooser.setAcceptAllFileFilterUsed(false);
	    if (chooser.showOpenDialog(this.window) == JFileChooser.APPROVE_OPTION) {
	    	this.window.setOutputDirectory(chooser.getSelectedFile().getAbsolutePath());
	    } else {
	    	this.window.setOutputDirectory("<output_directory_not_chosen>");
	    }
	}
}
