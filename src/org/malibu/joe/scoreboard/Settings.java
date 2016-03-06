package org.malibu.joe.scoreboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.malibu.joe.scoreboard.gui.ScoreboardGui;
import org.malibu.joe.scoreboard.util.Util;

public class Settings {
	
	private static final String CONFIG_FILE_NAME = "scoreboard.config";
	private static final String CONFIG_HOME_TEAM = "HOME_TEAM";
	private static final String CONFIG_CUSTOM_HORN_PATH = "CUSTOM_HORN_PATH";
	private String configFilePath = Util.getJarDirectoryForClass(ScoreboardGui.class) + File.separator + CONFIG_FILE_NAME;;
	
	private Team homeTeam = Team.RED_WINGS;
	
	public void loadSettings() {
		// load settings
		File configFile = new File(this.configFilePath);
		Properties props = new Properties();
		if(configFile.exists() && configFile.isFile()) {
			try (FileInputStream stream = new FileInputStream(configFile)) {
				props.load(stream);
			} catch (IOException ex) {
				System.err.println("could not load config file, reverting to default values");
				ex.printStackTrace();
			}
		}
		
		// load home team setting
		if(props.getProperty(CONFIG_HOME_TEAM) != null) setHomeTeam(Team.valueOf(props.getProperty(CONFIG_HOME_TEAM)));
		if(getHomeTeam() == null) setHomeTeam(Team.RED_WINGS);
		// load custom goal horn location setting
		if(props.getProperty(CONFIG_CUSTOM_HORN_PATH) != null) {
			File customHornPath = new File(props.getProperty(CONFIG_CUSTOM_HORN_PATH));
			if(customHornPath.exists() && customHornPath.isFile() && customHornPath.getAbsolutePath().toLowerCase().endsWith(".wav")) {
				Team.CUSTOM.setSoundFilePath(customHornPath.getAbsolutePath());
			}
		}
	}
	
	public void saveSettings() {
		Properties props = new Properties();
		props.setProperty(CONFIG_HOME_TEAM, getHomeTeam().toString());
		props.setProperty(CONFIG_CUSTOM_HORN_PATH, Team.CUSTOM.getSoundFilePath());
		try (FileOutputStream fos = new FileOutputStream(new File(this.configFilePath))) {
			// attempt to write config entries to a file
			props.store(fos, "");
		} catch (IOException e) {
			System.err.println("error saving config file");
			e.printStackTrace();
		}
	}

	public Team getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(Team homeTeam) {
		this.homeTeam = homeTeam;
	}
}
