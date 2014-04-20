package org.malibu.dvd.sectionripper.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.malibu.dvd.sectionripper.beans.DVDInfo;
import org.malibu.dvd.sectionripper.beans.DriveInfo;
import org.malibu.dvd.sectionripper.gui.listener.AddClipActionListener;
import org.malibu.dvd.sectionripper.gui.listener.BeginProcessingButtonListener;
import org.malibu.dvd.sectionripper.gui.listener.FindMovieTrackButtonListener;
import org.malibu.dvd.sectionripper.gui.listener.HitEnterKeyListener;
import org.malibu.dvd.sectionripper.gui.listener.OutputDirChooserAction;
import org.malibu.dvd.sectionripper.utilities.Util;

public class PrimaryWindow extends JFrame {

	private static final long serialVersionUID = -6121239762357514634L;
	
	static Logger log = Logger.getLogger(PrimaryWindow.class);
	
	private static final String LOGO_IMG_FILENAME = "logo.png";
	
	private JPanel contentPane;
	private JTable table;
	private JComboBox<DriveInfo> comboBoxDriveList;
	private JTextField textFieldStartTime;
	private JTextField textFieldEndTime;
	private JLabel lblOutputDirText;
	private String outputDirectory = null;
	
	private JLabel lblMovieNameText = null;
	private JLabel lblMovieLengthText = null;
	
	private DVDInfo dvdInfo = null;
	private JTextField textFieldClipName;

	/**
	 * Create the frame.
	 */
	@SuppressWarnings("serial")
	public PrimaryWindow() {
		setResizable(false);
		setTitle("DVD Section Ripper v2.3");
		
		// attempt to set UI look and feel (if this fails, we don't really care)
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 680, 627);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {650, 0};
		gbl_panel.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		try {
			// try to load logo panel all in one shot, if anything fails to load, don't add the panel
			String logoFilePath = Util.getResourceDirectoryPath() + LOGO_IMG_FILENAME;
			JPanel panel_5 = new ImagePanel(logoFilePath);
			GridBagConstraints gbc_panel_5 = new GridBagConstraints();
			gbc_panel_5.insets = new Insets(0, 0, 5, 0);
			gbc_panel_5.fill = GridBagConstraints.BOTH;
			gbc_panel_5.gridx = 0;
			gbc_panel_5.gridy = 0;
			panel.add(panel_5, gbc_panel_5);
			panel_5.setPreferredSize(new Dimension(641, 96));
		} catch (IOException e1) {
			log.warn("Failed to load logo panel", e1);
		}
		
		JPanel panelDvdSelection = new JPanel();
		GridBagConstraints gbc_panelDvdSelection = new GridBagConstraints();
		gbc_panelDvdSelection.fill = GridBagConstraints.BOTH;
		gbc_panelDvdSelection.insets = new Insets(0, 0, 5, 0);
		gbc_panelDvdSelection.gridx = 0;
		gbc_panelDvdSelection.gridy = 1;
		panel.add(panelDvdSelection, gbc_panelDvdSelection);
		panelDvdSelection.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblSelectDvdDrive = new JLabel("Select DVD Drive:");
		panelDvdSelection.add(lblSelectDvdDrive);
		
		this.comboBoxDriveList = new JComboBox<DriveInfo>();
		panelDvdSelection.add(comboBoxDriveList);
		comboBoxDriveList.setMaximumRowCount(20);
		// initialize drive option dropdown
		initializeDriveDropdown(comboBoxDriveList);
		
		JButton btnRescanDrives = new JButton("Rescan Drives");
		btnRescanDrives.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initializeDriveDropdown(comboBoxDriveList);
			}
		});
		panelDvdSelection.add(btnRescanDrives);
		
		JButton btnGetMovieInfo = new JButton("Verify DVD");
		panelDvdSelection.add(btnGetMovieInfo);
		btnGetMovieInfo.addActionListener(new FindMovieTrackButtonListener(this));
		
		JPanel panelDvdInfo = new JPanel();
		GridBagConstraints gbc_panelDvdInfo = new GridBagConstraints();
		gbc_panelDvdInfo.fill = GridBagConstraints.BOTH;
		gbc_panelDvdInfo.insets = new Insets(0, 0, 5, 0);
		gbc_panelDvdInfo.gridx = 0;
		gbc_panelDvdInfo.gridy = 2;
		panel.add(panelDvdInfo, gbc_panelDvdInfo);
		panelDvdInfo.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		panelDvdInfo.add(panel_3, BorderLayout.NORTH);
		
		JLabel lblMovieName = new JLabel("DVD Name:");
		panel_3.add(lblMovieName);
		
		this.lblMovieNameText = new JLabel("");
		panel_3.add(lblMovieNameText);
		
		JPanel panel_4 = new JPanel();
		panelDvdInfo.add(panel_4, BorderLayout.SOUTH);
		
		JLabel lblMovieLength = new JLabel("Length:");
		panel_4.add(lblMovieLength);
		
		this.lblMovieLengthText = new JLabel("");
		panel_4.add(lblMovieLengthText);
		
		
		JPanel panelAddClipInputs = new JPanel();
		GridBagConstraints gbc_panelAddClipInputs = new GridBagConstraints();
		gbc_panelAddClipInputs.fill = GridBagConstraints.BOTH;
		gbc_panelAddClipInputs.insets = new Insets(0, 0, 5, 0);
		gbc_panelAddClipInputs.gridx = 0;
		gbc_panelAddClipInputs.gridy = 3;
		panel.add(panelAddClipInputs, gbc_panelAddClipInputs);
		panelAddClipInputs.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		
		
		JLabel lblClipName = new JLabel("Clip Name:");
		panelAddClipInputs.add(lblClipName);
		
		textFieldClipName = new JTextField();
		panelAddClipInputs.add(textFieldClipName);
		textFieldClipName.setColumns(10);
		
		JLabel lblStartTime = new JLabel("Start Time:");
		panelAddClipInputs.add(lblStartTime);
		
		textFieldStartTime = new JTextField();
		panelAddClipInputs.add(textFieldStartTime);
		textFieldStartTime.setColumns(10);
		
		
		JLabel lblEndTime = new JLabel("End Time:");
		panelAddClipInputs.add(lblEndTime);
		
		textFieldEndTime = new JTextField();
		panelAddClipInputs.add(textFieldEndTime);
		textFieldEndTime.setColumns(10);
		
		JPanel panelAddClipButtons = new JPanel();
		GridBagConstraints gbc_panelAddClipButtons = new GridBagConstraints();
		gbc_panelAddClipButtons.insets = new Insets(0, 0, 5, 0);
		gbc_panelAddClipButtons.fill = GridBagConstraints.BOTH;
		gbc_panelAddClipButtons.gridx = 0;
		gbc_panelAddClipButtons.gridy = 4;
		panel.add(panelAddClipButtons, gbc_panelAddClipButtons);
		panelAddClipButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnAddNewClip = new JButton("Add New Clip");
		panelAddClipButtons.add(btnAddNewClip);
		
		JButton btnDeleteSelectedClips = new JButton("Delete Selected Clips");
		btnDeleteSelectedClips.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// remove selected rows
				int[] selectedRowIndices = table.getSelectedRows();
				for(int i = selectedRowIndices.length - 1; i >= 0; i--) {
					((DefaultTableModel)table.getModel()).removeRow(selectedRowIndices[i]);
				}
			}
		});
		panelAddClipButtons.add(btnDeleteSelectedClips);
		
		JPanel panelClipTable = new JPanel();
		GridBagConstraints gbc_panelClipTable = new GridBagConstraints();
		gbc_panelClipTable.fill = GridBagConstraints.BOTH;
		gbc_panelClipTable.insets = new Insets(0, 0, 5, 0);
		gbc_panelClipTable.gridx = 0;
		gbc_panelClipTable.gridy = 5;
		panel.add(panelClipTable, gbc_panelClipTable);
		panelClipTable.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JTable table_1 = new JTable();
		table_1.setPreferredScrollableViewportSize(new Dimension(625, 200));
		table_1.setFillsViewportHeight(true);
		table_1.getTableHeader().setReorderingAllowed(false);
//		table_1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		table_1.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Clip ID", "Clip Name", "Start Time", "End Time", "Processed?"
			}
		) {
			Class[] columnTypes = new Class[] {
				Integer.class, String.class, String.class, String.class, Boolean.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, true, true, true, true
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		this.table = table_1;
		
		btnAddNewClip.addActionListener(new AddClipActionListener(this, table_1));
		
		JScrollPane scrollPane = new JScrollPane(table_1);
		panelClipTable.add(scrollPane);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		// add ENTER key press listener to user input fields
		KeyListener enterKeyListener = new HitEnterKeyListener(this,this.table, textFieldClipName);
		textFieldClipName.addKeyListener(enterKeyListener);
		textFieldStartTime.addKeyListener(enterKeyListener);
		textFieldEndTime.addKeyListener(enterKeyListener);
		
		JPanel panelStartProcessing = new JPanel();
		GridBagConstraints gbc_panelStartProcessing = new GridBagConstraints();
		gbc_panelStartProcessing.gridx = 0;
		gbc_panelStartProcessing.gridy = 6;
		panel.add(panelStartProcessing, gbc_panelStartProcessing);
		panelStartProcessing.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panelStartProcessing.add(panel_1, BorderLayout.NORTH);
		
		JLabel lblOutputDirectory = new JLabel("Output directory:");
		panel_1.add(lblOutputDirectory);
		
		this.lblOutputDirText = new JLabel("<output_directory_not_chosen>");
		panel_1.add(lblOutputDirText);
		
		JPanel panel_2 = new JPanel();
		panelStartProcessing.add(panel_2, BorderLayout.SOUTH);
		
		JButton btnChooseOutputDirectory = new JButton("Choose Output Directory");
		panel_2.add(btnChooseOutputDirectory);
		btnChooseOutputDirectory.addActionListener(new OutputDirChooserAction(this));
		
		JButton btnStartProcessing = new JButton("Start Processing");
		btnStartProcessing.addActionListener(new BeginProcessingButtonListener(this, this.table));
		panel_2.add(btnStartProcessing);
	}

	private void initializeDriveDropdown(JComboBox<DriveInfo> comboBox) {
		DefaultComboBoxModel<DriveInfo> model = new DefaultComboBoxModel<DriveInfo>();
		DriveInfo[] driveInfoList = Util.getDriveInfo();
		for (DriveInfo driveInfo : driveInfoList) {
			model.addElement(driveInfo);
		}
		comboBox.setModel(model);
	}
	
	public DriveInfo getSelectedDrive() {
		return (DriveInfo)comboBoxDriveList.getSelectedItem();
	}
	
	public String getUserEnteredStartTime() {
		return this.textFieldStartTime.getText();
	}
	
	public String getUserEnteredEndTime() {
		return this.textFieldEndTime.getText();
	}
	
	public String getUserEnteredClipName() {
		return this.textFieldClipName.getText();
	}
	
	public void resetUserEnteredClipParams() {
		log.debug("Reset user entered clip params");
		
		this.textFieldClipName.setText("");
		this.textFieldStartTime.setText("");
		this.textFieldEndTime.setText("");
		// force repaint, because for some reason, if an error dialog had popped
		// up before, these boxes won't appear cleared!
		repaint();
	}
	
	public void detectDVDInfo() throws IOException {
		this.dvdInfo = Util.getDVDInfo(getSelectedDrive());
		updateWindowDVDInfo();
	}
	
	private void updateWindowDVDInfo() {
		if(this.dvdInfo != null) {
			if(this.dvdInfo.getDvdName() != null) {
				this.lblMovieNameText.setText(this.dvdInfo.getDvdName());
			}
			if(this.dvdInfo.getMovieLength() != -1) {
				this.lblMovieLengthText.setText(Util.getMovieLengthAsReadableText(this.dvdInfo.getMovieLength()));
			}
		}
	}
	
	public DVDInfo getDvdInfo() throws IOException {
		if(this.dvdInfo == null) {
			detectDVDInfo();
		}
		return this.dvdInfo;
	}
	
	public void setOutputDirectory(String path) {
		this.outputDirectory = path;
		this.lblOutputDirText.setText(path);
	}
	
	public String getOutputDirectory() {
		return this.outputDirectory;
	}
}
