package org.malibu.dvd.sectionripper.main;

import java.awt.EventQueue;
import java.io.IOException;

import org.malibu.dvd.sectionripper.gui.MessageHandler;
import org.malibu.dvd.sectionripper.gui.PrimaryWindow;

public class DVDSectionRipper {
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PrimaryWindow frame = new PrimaryWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					MessageHandler.handleException("Error occurred creating primary window", e);
				}
			}
		});
	}
}
