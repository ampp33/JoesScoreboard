package org.malibu.joe.scoreboard.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.malibu.joe.scoreboard.Callback;
import org.malibu.joe.scoreboard.Settings;
import org.malibu.joe.scoreboard.SoundManager;
import org.malibu.joe.scoreboard.Team;
import org.malibu.joe.scoreboard.ScoreboardTime;
import org.malibu.joe.scoreboard.TimerThread;

public class ScoreboardGui extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public static final String VERSION = "2.5.3";

	private JPanel contentPane;
	private JLabel timeMinutesLbl;
	private JLabel timeSecondsLbl;
	private JLabel homeTeamNameLbl;
	private JLabel visitorTeamNameLbl;
	
	private SoundManager goalHornSound;
	private SoundManager periodHornSound;
	private TimerThread timerThread;
	
	private int posX;
	private int posY;
	
	private ScoreboardTime currentTime;
	private ScoreboardTime savedTime;
	
	private boolean timeOut = false;
	private boolean timesUp = true;
	
	private Settings settings;

	/**
	 * Launch the application.
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// use system look and feel
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
					// create the main window!
					ScoreboardGui frame = new ScoreboardGui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws Exception 
	 */
	public ScoreboardGui() throws Exception {
		
		currentTime = new ScoreboardTime(0,0,0);
		final ScoreboardGui gui = this;
		
		// load settings
		this.settings = new Settings();
		this.settings.loadSettings();
		
		// start threads
		if(this.settings.getHomeTeam() == Team.CUSTOM) {
			this.goalHornSound = new SoundManager(new File(this.settings.getHomeTeam().getSoundFilePath()));
		} else {
			this.goalHornSound = new SoundManager(this.settings.getHomeTeam().getSoundFilePath());
		}
		this.periodHornSound = new SoundManager("period.wav");
		
		this.timerThread = new TimerThread();
		new Thread(timerThread).start();
		
		// load fonts
		Font bigLcdFont = Font.createFont(Font.TRUETYPE_FONT, ScoreboardGui.class.getClassLoader().getResourceAsStream("LiquidCrystal-Normal.otf")).deriveFont(140f);
		Font smallLcdFont = Font.createFont(Font.TRUETYPE_FONT, ScoreboardGui.class.getClassLoader().getResourceAsStream("LiquidCrystal-Normal.otf")).deriveFont(120f);
		
		Font teamNameFont = Font.createFont(Font.TRUETYPE_FONT, ScoreboardGui.class.getClassLoader().getResourceAsStream("Freeroad Bold.ttf")).deriveFont(76f);
		
		setUndecorated(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 509);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				posX = e.getX();
				posY = e.getY();
			}
		});
		
		addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent evt) {
				setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
			}
		});
		
		contentPane.setLayout(null);
		
		JPanel backgroundPanel = new ImagePanel("blank_no_teams.jpg");
		backgroundPanel.setBounds(0, 0, 1200, 509);
		contentPane.add(backgroundPanel);
		backgroundPanel.setLayout(null);
		
		final JLabel homeScoreLbl = new JLabel("0");
		homeScoreLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		homeScoreLbl.setForeground(Color.RED);
		homeScoreLbl.setFont(bigLcdFont);
		homeScoreLbl.setBounds(73, 91, 214, 186);
		backgroundPanel.add(homeScoreLbl);
		
		final JLabel visitorScoreLbl = new JLabel("0");
		visitorScoreLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		visitorScoreLbl.setForeground(Color.RED);
		visitorScoreLbl.setFont(bigLcdFont);
		visitorScoreLbl.setBounds(911, 91, 214, 186);
		backgroundPanel.add(visitorScoreLbl);
		
		final JLabel periodLbl = new JLabel("1");
		periodLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		periodLbl.setForeground(Color.GREEN);
		periodLbl.setFont(smallLcdFont);
		periodLbl.setBounds(507, 28, 132, 150);
		backgroundPanel.add(periodLbl);
		
		timeMinutesLbl = new JLabel("00");
		timeMinutesLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		timeMinutesLbl.setForeground(new Color(245, 218, 43));
		timeMinutesLbl.setFont(bigLcdFont);
		timeMinutesLbl.setBounds(335, 289, 214, 186);
		backgroundPanel.add(timeMinutesLbl);
		
		timeSecondsLbl = new JLabel("00");
		timeSecondsLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		timeSecondsLbl.setForeground(new Color(245, 218, 43));
		timeSecondsLbl.setFont(bigLcdFont);
		timeSecondsLbl.setBounds(593, 289, 222, 186);
		backgroundPanel.add(timeSecondsLbl);
		
		homeTeamNameLbl = new JLabel("HOME");
		homeTeamNameLbl.setFont(teamNameFont);
		homeTeamNameLbl.setHorizontalAlignment(SwingConstants.CENTER);
		homeTeamNameLbl.setForeground(Color.WHITE);
		homeTeamNameLbl.setBounds(27, 32, 305, 72);
		backgroundPanel.add(homeTeamNameLbl);
		
		visitorTeamNameLbl = new JLabel("VISITOR");
		visitorTeamNameLbl.setHorizontalAlignment(SwingConstants.CENTER);
		visitorTeamNameLbl.setForeground(Color.WHITE);
		visitorTeamNameLbl.setFont(teamNameFont);
		visitorTeamNameLbl.setBounds(863, 35, 310, 72);
		backgroundPanel.add(visitorTeamNameLbl);
		
		// add key listener to handle key press events
		addKeyListener(new KeyListener() {
			
			public void keyTyped(KeyEvent e) {}
			
			public void keyReleased(KeyEvent e) {
				goalHornSound.stopLoop();
			}
			
			public void keyPressed(KeyEvent e) {
				// adjust home score
				if(e.getKeyCode() == KeyEvent.VK_Q) {
					int score = Integer.parseInt(homeScoreLbl.getText());
					if(score < 99) {
						homeScoreLbl.setText(String.valueOf(score + 1));
					}
				}
				if(e.getKeyCode() == KeyEvent.VK_A) {
					int score = Integer.parseInt(homeScoreLbl.getText());
					if(score > 0) {
						homeScoreLbl.setText(String.valueOf(score - 1));
					}
				}
				
				// adjust visitor score
				if(e.getKeyCode() == KeyEvent.VK_W) {
					int period = Integer.parseInt(periodLbl.getText());
					if(period < 9) {
						periodLbl.setText(String.valueOf(period + 1));
					}
				}
				if(e.getKeyCode() == KeyEvent.VK_S) {
					int period = Integer.parseInt(periodLbl.getText());
					if(period > 1) {
						periodLbl.setText(String.valueOf(period - 1));
					}
				}
				
				// adjust visitor score
				if(e.getKeyCode() == KeyEvent.VK_E) {
					int score = Integer.parseInt(visitorScoreLbl.getText());
					if(score < 99) {
						visitorScoreLbl.setText(String.valueOf(score + 1));
					}
				}
				if(e.getKeyCode() == KeyEvent.VK_D) {
					int score = Integer.parseInt(visitorScoreLbl.getText());
					if(score > 0) {
						visitorScoreLbl.setText(String.valueOf(score - 1));
					}
				}
				
				// start time
				if(e.getKeyCode() == KeyEvent.VK_Z) {
					timerThread.setPause(false);
				}
				
				// pause time
				if(e.getKeyCode() == KeyEvent.VK_X) {
					timerThread.setPause(true);
				}
				
				// reset time
				if(e.getKeyCode() == KeyEvent.VK_C) {
					timeOut = false;
					timesUp = false;
					currentTime = new ScoreboardTime(5, 0, 0);
					
					// set clock, if time is paused
					if(timerThread.isPause()) {
						updateClock();
					}
				}
				
				// prompt time
				if(e.getKeyCode() == KeyEvent.VK_V) {
					String minutesString = JOptionPane.showInputDialog(contentPane, "How many minutes do you want on the clock?", "Minutes", JOptionPane.DEFAULT_OPTION);
					String secondsString = JOptionPane.showInputDialog(contentPane, "How many seconds do you want on the clock?", "Seconds", JOptionPane.DEFAULT_OPTION);
					String milliString = JOptionPane.showInputDialog(contentPane, "How many milliseconds do you want on the clock?", "Milliseconds", JOptionPane.DEFAULT_OPTION);
					
					try {
						int minutes = Integer.parseInt(minutesString);
						int seconds = Integer.parseInt(secondsString);
						int milli = Integer.parseInt(milliString);
						
						currentTime = new ScoreboardTime(minutes, seconds, milli);
						
						timeOut = false;
						timesUp = false;
					} catch (Exception ex) {
						System.err.println("failed to parse number from user input");
						ex.printStackTrace();
					}
					
					// set clock, if time is paused
					if(timerThread.isPause()) {
						updateClock();
					}
				}
				
				// time out
				if(e.getKeyCode() == KeyEvent.VK_T) {
					timeOut = true;
					savedTime = new ScoreboardTime(currentTime);
					currentTime = new ScoreboardTime(0, 30, 0);
					
					// set clock, if time is paused
					if(timerThread.isPause()) {
						updateClock();
					}
				}
				
				// prompt for team names
				if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
					String homeTeamName = JOptionPane.showInputDialog(contentPane, "What's the HOME team name (max 7 characters)?", "Team Name", JOptionPane.DEFAULT_OPTION);
					String visitorTeamName = JOptionPane.showInputDialog(contentPane, "What's the VISITORS team name (max 7 characters)?", "Team Name", JOptionPane.DEFAULT_OPTION);
					
					if(homeTeamName != null) {
						homeTeamName = homeTeamName.trim().toUpperCase();
						if(homeTeamName.length() <= 7 && homeTeamName.length() > 0) {
							homeTeamNameLbl.setText(homeTeamName);
						}
					}
					
					if(visitorTeamName != null) {
						visitorTeamName = visitorTeamName.trim().toUpperCase();
						if(visitorTeamName.length() <= 7 && visitorTeamName.length() > 0) {
							visitorTeamNameLbl.setText(visitorTeamName);
						}
					}
				}
				
				// horn
				if(e.getKeyCode() == KeyEvent.VK_SPACE) {
					if(!goalHornSound.isLooping()) {
						goalHornSound.incrementTimesToPlay();
					}
					goalHornSound.loop();
				}
				
				// hidden developer dialog
				if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
					try {
						DeveloperDialog devDialog = new DeveloperDialog();
						devDialog.setLocationRelativeTo(contentPane);
						devDialog.setVisible(true);
					} catch (Exception ex) {
						System.err.println("error showing developer panel... DAMMIT");
						ex.printStackTrace();
					}
				}
				
				if(e.getKeyCode() == KeyEvent.VK_ALT) {
					try {
						OptionsDialog optionsDialog = new OptionsDialog(gui, settings);
						optionsDialog.setLocationRelativeTo(contentPane);
						optionsDialog.setVisible(true);
					} catch (Exception ex) {
						System.err.println("error showing developer panel... DAMMIT");
						ex.printStackTrace();
					}
				}
				
				// exit
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					// save config file
					settings.saveSettings();
					System.exit(0);
				}
			}
		});
		
		// add timer callbacks (do what we wanna when the clock ticks for each unit)
		this.timerThread.addSecondCallback(new Callback() {
			public void execute() {
				if(currentTime.getSeconds() == 0) {
					if(currentTime.getMinutes() == 1) {
						// prep for the last minute counter clock!
						currentTime = new ScoreboardTime(0, 59, 0);
					} else if (currentTime.getMinutes() > 1) {
						currentTime.setMinutes(currentTime.getMinutes() - 1);
						currentTime.setSeconds(59);
					}
				} else {
					currentTime.setSeconds(currentTime.getSeconds() - 1);
				}
				
				updateClock();
			}
		});
		
		this.timerThread.addMilliCallback(new Callback() {
			public void execute() {
				if(currentTime.getMinutes() == 0) {
					if(currentTime.getMilli() == 0) {
						if(currentTime.getSeconds() == 0) {
							if(timeOut) {
								// if timeout time ends, return clock to it's original
								// value when the timeout was called, and keep the clock ticking
								timeOut = false;
								currentTime = savedTime;
								return;
							}
							if(!timesUp) {
								int period = Integer.parseInt(periodLbl.getText());
								int homeScore = Integer.parseInt(homeScoreLbl.getText());
								int visitorScore = Integer.parseInt(visitorScoreLbl.getText());
								
								// if we're on the 3rd period, and the home team is ahead, and time is up,
								// play the home team's horn instead of the normal horn
								if(period == 3 && homeScore > visitorScore) {
									goalHornSound.play(2);
								} else {
									// blow horn to signal end of period
									periodHornSound.play(1);
								}
							}
							timesUp = true;
						} else {
							currentTime.setMilli(9);
						}
					} else {
						currentTime.setMilli(currentTime.getMilli() - 1);
					}
				}
				
				updateClock();
			}
		});
	}
	
	private void updateClock() {
		// set clock, if there's still at least a minute left
		if(currentTime.getMinutes() > 0) {
			timeMinutesLbl.setText(String.format("%02d", currentTime.getMinutes()));
			timeSecondsLbl.setText(String.format("%02d", currentTime.getSeconds()));
		} else {
			// less than a minute left, jump to the countdown clock
			timeMinutesLbl.setText(String.format("%02d", currentTime.getSeconds()));
			timeSecondsLbl.setText(String.format("%02d", currentTime.getMilli()));
		}
	}
	
	public void setHomeTeam(Team team) throws Exception {
		this.settings.setHomeTeam(team);
		if(team == Team.CUSTOM) {
			this.goalHornSound = new SoundManager(new File(team.getSoundFilePath())	);
		} else {
			this.goalHornSound = new SoundManager(team.getSoundFilePath());
		}
	}
}
