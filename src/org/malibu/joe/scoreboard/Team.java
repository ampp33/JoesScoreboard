package org.malibu.joe.scoreboard;

public enum Team {
	
	RED_WINGS("Detroit Red Wings", "detroit.wav"),
//	BLUE_JACKETS("Columbus Blue Jackets", "columbus.wav"),
	MAPLE_LEAFS("Toronto Maple Leafs", "toronto.wav"),
	PENGUINS("Pittsburgh Penguins", "pittsburgh.wav"),
	NEUTRAL("Neutral", "neutral.wav"),
	CUSTOM("Custom", "");
	
	private String displayName;
	private String soundFilePath;
	private static String[] displayNameList;
	
	static {
		displayNameList = new String[values().length];
		int i = 0;
		for (Team team : values()) {
			displayNameList[i] = team.displayName;
			i++;
		}
	}
	
	Team(String displayName, String soundFilePath) {
		this.displayName = displayName;
		this.soundFilePath = soundFilePath;
	}
	
	public static String[] displayNames() {
		return displayNameList;
	}
	
	public void setSoundFilePath(String soundFilePath) {
		this.soundFilePath = soundFilePath;
	}
	
	public String getSoundFilePath() {
		return this.soundFilePath;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
}
