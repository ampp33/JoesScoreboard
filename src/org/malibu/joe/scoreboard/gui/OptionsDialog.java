package org.malibu.joe.scoreboard.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import org.malibu.joe.scoreboard.Settings;
import org.malibu.joe.scoreboard.Team;

public class OptionsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	
	private ScoreboardGui parent;
	
	/**
	 * Create the dialog.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public OptionsDialog(final ScoreboardGui parentGui, final Settings settings) {
		
		this.parent = parentGui;
		
		setResizable(false);
		setBackground(Color.WHITE);
		setBounds(100, 100, 230, 342);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblHomeTeamHorn = new JLabel("Home Team Goal Horn");
		lblHomeTeamHorn.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblHomeTeamHorn.setBounds(10, 11, 200, 14);
		contentPanel.add(lblHomeTeamHorn);
		
		final JLabel customHornFileLocationLbl = new JLabel("");
		customHornFileLocationLbl.setBounds(10, 55, 189, 20);
		contentPanel.add(customHornFileLocationLbl);
		
		final JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(Team.displayNames()));
		if(settings != null && settings.getHomeTeam() != null) {
			comboBox.setSelectedItem(settings.getHomeTeam().getDisplayName());
			if(settings.getHomeTeam() == Team.CUSTOM) {
				customHornFileLocationLbl.setText(settings.getHomeTeam().getSoundFilePath());
			}
		}
		comboBox.setBounds(10, 29, 189, 20);
		contentPanel.add(comboBox);
		
		comboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Team selectedTeam = null;
				for (Team team : Team.values()) {
					if(team.getDisplayName().equals(((JComboBox<Team>)e.getSource()).getSelectedItem())) {
						selectedTeam = team;
						break;
					}
				}
				if(selectedTeam == Team.CUSTOM) {
					// custom team seleted, show dialog box to allow user to pick a custom wav file
					JFileChooser chooser = new JFileChooser(); 
				    chooser.setCurrentDirectory(new java.io.File("."));
				    chooser.setDialogTitle("Choose .wav file to use as goal horn");
				    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				    // only allow users to see dirs and .wav files
				    chooser.setFileFilter(new FileFilter() {
						@Override
						public String getDescription() {return null;}
						
						@Override
						public boolean accept(File f) {
							return f.isDirectory() || f.getAbsolutePath().toLowerCase().endsWith(".wav");
						}
					});
				    // disable the "All files" option.
				    chooser.setAcceptAllFileFilterUsed(false);
				    if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
				    	// file chosen
				    	File selectedFile = chooser.getSelectedFile();
				    	Team.CUSTOM.setSoundFilePath(selectedFile.getAbsolutePath());
				    	customHornFileLocationLbl.setText(selectedFile.getAbsolutePath());
				    } else {
				    	// file not chosen, choose the Red Wings instead :P
				    	selectedTeam = Team.RED_WINGS;
				    }
				}
				try {
					parent.setHomeTeam(selectedTeam);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(parent, "Error setting home team horn, the horn probably won't work now!");
				}
			}
		});
		
		JPanel buttonPane = new JPanel();
		buttonPane.setBackground(Color.WHITE);
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			JButton okButton = new JButton("OK");
			okButton.setActionCommand("OK");
			buttonPane.add(okButton);
			
			okButton.addMouseListener(new MouseListener() {
				public void mouseReleased(MouseEvent e) {	}
				public void mousePressed(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {	}
				public void mouseEntered(MouseEvent e) {}
				
				public void mouseClicked(MouseEvent e) {
					setVisible(false);
				}
			});
			getRootPane().setDefaultButton(okButton);
		}
	}
}
