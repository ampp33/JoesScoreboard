package org.malibu.dvd.sectionripper.gui;

import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.malibu.dvd.sectionripper.gui.progress.ProgressToken;

public class OverallProgressDialog extends Thread {

	private JFrame frame;
	private JProgressBar overallProgressBar = null;
	private JProgressBar currentClipProgressBar = null;
	private JLabel lblSubtaskName = null;
	
	private ProgressToken overallProgressToken = null;
	private ProgressToken subtaskProgressToken = null;

	/**
	 * Create the application.
	 */
	public OverallProgressDialog() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setType(Type.UTILITY);
		frame.setResizable(false);
		frame.setBounds(100, 100, 685, 189);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblProcessing = new JLabel("Processing...");
		lblProcessing.setBounds(294, 13, 142, 16);
		frame.getContentPane().add(lblProcessing);
		
		JLabel lblOverallProgress = new JLabel("Overall Progress");
		lblOverallProgress.setBounds(12, 29, 113, 16);
		frame.getContentPane().add(lblOverallProgress);
		
		lblSubtaskName = new JLabel("");
		lblSubtaskName.setBounds(12, 79, 658, 16);
		frame.getContentPane().add(lblSubtaskName);
		
		overallProgressBar = new JProgressBar();
		overallProgressBar.setBounds(12, 54, 658, 16);
		frame.getContentPane().add(overallProgressBar);
		
		currentClipProgressBar = new JProgressBar();
		currentClipProgressBar.setBounds(12, 101, 658, 16);
		frame.getContentPane().add(currentClipProgressBar);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(573, 127, 97, 25);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// discontinue processing if button is clicked
				remove();
			}
		});
		frame.getContentPane().add(btnCancel);
	}
	
	public void show() {
		frame.setVisible(true);
		start();
	}
	
	public void remove() {
		interrupt();
		frame.setVisible(false);
		frame.dispose();
	}
	
	public void registerOverallProgressToken(ProgressToken overallProgressToken) {
		this.overallProgressToken = overallProgressToken;
	}
	
	public void registerSubtaskProgressToken(ProgressToken subtaskProgressToken) {
		this.subtaskProgressToken = subtaskProgressToken;
	}

	@Override
	public void run() {
		while(!isInterrupted()) {
			// update overall percentage
			if(this.overallProgressToken != null) {
				overallProgressBar.setValue(this.overallProgressToken.getPercentComplete());
			}
			// update subtask percentage
			if(this.subtaskProgressToken != null) {
				lblSubtaskName.setText(this.subtaskProgressToken.getTaskName());
				currentClipProgressBar.setValue(this.subtaskProgressToken.getPercentComplete());
			}
			// sleep half a second so we don't bog down the processor (but be sure to quit execution if interrupted)
			try { Thread.sleep(500); } catch (InterruptedException e) { break; }
		}
	}
}
