package org.malibu.dvd.sectionripper.gui;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

/**
 * Handles displaying messages to user, logging when necessary
 * 
 * @author Ampp33
 *
 */
public class MessageHandler {
	
	static Logger log = Logger.getLogger(MessageHandler.class);
	
	private static JComponent primaryParentComponent = null;
	
	public void setPrimaryParentComponent(JComponent primaryParentComponent) {
		MessageHandler.primaryParentComponent = primaryParentComponent;
	}
	
	public static void showMessage(String text) {
		showMessage(primaryParentComponent, text);
	}
	
	public static void showMessage(JComponent parentWindow, String text) {
		log.info("Showing user info message: " + text);
		JOptionPane.showMessageDialog(parentWindow, text);
	}
	
	public static void showErrorMessage(String text) {
		showErrorMessage(primaryParentComponent, text);
	}
	
	public static void showErrorMessage(JComponent parentWindow, String text) {
		log.error("Showing user error message: " + text);
		JOptionPane.showMessageDialog(parentWindow, text);
	}
	
	public static void handleException(Throwable t) {
		handleException(null, t);
	}
	
	public static void handleException(String message, Throwable t) {
		handleException(primaryParentComponent, message, t);
	}
	
	public static void handleException(JComponent parentWindow, String message, Throwable t) {
		log.error(message, t);
		StringBuilder completeMessage = new StringBuilder();
		completeMessage.append("Something broke");
		if(message != null && message.trim().length() > 0) {
			completeMessage.append(": ");
			completeMessage.append(message);
		}
		if(t != null) {
			completeMessage.append("\n");
			completeMessage.append("Exception message: ");
			completeMessage.append(t.getMessage());
		}
		// add info to contact dev team
		completeMessage.append("\n\n");
		completeMessage.append("If you want to help support this application, \n");
		completeMessage.append("copy the error text in this message box and email it to: ampp33@gmail.com \n");
		showEditablePopupWindow(completeMessage.toString());
	}
	
	private static void showEditablePopupWindow(String text) {
		JPanel panel = new JPanel();
		JTextArea textArea = new JTextArea(text);
		panel.add(textArea);
		JOptionPane.showMessageDialog(null, panel, "My custom dialog", JOptionPane.PLAIN_MESSAGE);
	}
}
