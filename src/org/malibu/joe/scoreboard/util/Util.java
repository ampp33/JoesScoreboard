package org.malibu.joe.scoreboard.util;

import java.io.File;

public class Util {
	public static final String getJarDirectoryForClass(Class<?> clazz) {
		// get directory .jar file is running from (using substring() to remove leading slash)
		String workingDir = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
		File file = new File(workingDir);
		workingDir = file.getAbsolutePath();
		if(workingDir.startsWith("\\")) {
			workingDir = workingDir.substring(1);
		}
		if(workingDir.endsWith(".")) {
			workingDir = workingDir.substring(0, workingDir.length() - 2);
		}
		return workingDir;
	}
}
