package org.malibu.dvd.sectionripper.gui.listener;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTable;

import org.malibu.dvd.sectionripper.gui.PrimaryWindow;

public class HitEnterKeyListener implements KeyListener {
	
	private PrimaryWindow window = null;
	private JTable table = null;
	private Component returnFocusToComponent;
	
	public HitEnterKeyListener(PrimaryWindow window, JTable table, Component returnFocusToComponent) {
		this.window = window;
		this.table = table;
		this.returnFocusToComponent = returnFocusToComponent;
	}

	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	
	public void keyReleased(KeyEvent e) {
		// check if user hit ENTER
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			// act like user hit the "add new clip button"
			new AddClipActionListener(window, table).actionPerformed(null);
			// return focus to "clip name" field
			returnFocusToComponent.requestFocusInWindow();
		}
	}
}
