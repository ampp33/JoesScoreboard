package org.malibu.joe.scoreboard.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class DeveloperDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 * @throws IOException 
	 */
	public DeveloperDialog() throws IOException {
		setResizable(false);
		setUndecorated(true);
		setBounds(100, 100, 560, 526);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JPanel kevinImagePanel = new ImagePanel("kevin.jpg");
		kevinImagePanel.setBounds(0, 0, 281, 439);
		contentPanel.add(kevinImagePanel);
		
		JPanel joeImagePanel = new ImagePanel("joe.jpg");
		joeImagePanel.setBounds(279, 0, 281, 439);
		contentPanel.add(joeImagePanel);
		
		JLabel lblKevinKunst = new JLabel("Lead Developer");
		lblKevinKunst.setHorizontalAlignment(SwingConstants.CENTER);
		lblKevinKunst.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblKevinKunst.setBounds(52, 450, 200, 14);
		contentPanel.add(lblKevinKunst);
		
		JLabel label = new JLabel("Kevin Kunst");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(52, 470, 200, 14);
		contentPanel.add(label);
		
		JLabel lblJosephKunst = new JLabel("Joseph Kunst");
		lblJosephKunst.setHorizontalAlignment(SwingConstants.CENTER);
		lblJosephKunst.setBounds(331, 470, 200, 14);
		contentPanel.add(lblJosephKunst);
		
		JLabel lblProjectManagement = new JLabel("Project Management / Original Design");
		lblProjectManagement.setHorizontalAlignment(SwingConstants.CENTER);
		lblProjectManagement.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblProjectManagement.setBounds(307, 450, 243, 14);
		contentPanel.add(lblProjectManagement);
		
		JLabel lblVersion = new JLabel("Version: " + ScoreboardGui.VERSION);
		lblVersion.setBounds(245, 470, 67, 14);
		contentPanel.add(lblVersion);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				okButton.addMouseListener(new MouseListener() {
					public void mouseReleased(MouseEvent e) {}
					public void mousePressed(MouseEvent e) {}
					public void mouseExited(MouseEvent e) {}
					public void mouseEntered(MouseEvent e) {}
					public void mouseClicked(MouseEvent e) {
						setVisible(false);
					}
				});
				getRootPane().setDefaultButton(okButton);
			}
		}
		
		
	}
}
