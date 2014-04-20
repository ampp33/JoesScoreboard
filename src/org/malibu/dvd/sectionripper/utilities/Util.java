package org.malibu.dvd.sectionripper.utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.filechooser.FileSystemView;

import org.apache.log4j.Logger;
import org.malibu.dvd.sectionripper.beans.DVDInfo;
import org.malibu.dvd.sectionripper.beans.DriveInfo;
import org.malibu.dvd.sectionripper.io.ProcessStreamHandler;

public class Util {
	
	static Logger log = Logger.getLogger(Util.class);
	
	public static final String UTILITY_DIRECTORY = "utils";
	public static final String RESOURCES_DIRECTORY = "resources";
	
	private static final String TRACK_LENGTH_MESSAGE_PREFIX = "ID_DVD_TITLE_";
	private static final String TRACK_LENGTH_MESSAGE_POSTFIX = "_LENGTH";
	private static final String DVD_INFO_PARAM = "ID_DVD_VOLUME_ID";
	
	private static final String MPLAYER_IDENTIFY_FORMATTED_PARAMS = "\"%s\" -dvd-device %s -nocache -identify -vo NUL -nosound dvd:// -frames 0";
	private static final String MPLAYER_CROPDETECT_FORMATTED_PARAMS = "\"%s\" -dvd-device %s -nocache -vf cropdetect -ao null -vo null -ss %s -frames 50 dvd://%s";
	private static final String PASS1_ENCODING_FORMATTED_PARAMS = "\"%s\" -dvd-device %s dvd://%s -ovc xvid -xvidencopts bvhq=1:chroma_opt:quant_type=mpeg:bitrate=800:pass=1:threads=8:autoaspect -mc 0 -vf harddup -oac copy -ss 0:00 -endpos 1 -o NUL";
	private static final String PASS2_ENCODING_FORMATTED_PARAMS = "\"%s\" -dvd-device %s dvd://%s -ovc xvid -xvidencopts bvhq=1:chroma_opt:quant_type=mpeg:bitrate=800:pass=2:threads=8:autoaspect -mc 0 -vf harddup%s -alang en -nosub -oac mp3lame -lameopts br=120:cbr:vol=0 -ss %s -endpos %s -o \"%s\"";
	
	/**
	 * Gets list of drives on machine
	 * 
	 * @return
	 */
	public static DriveInfo[] getDriveInfo() {
		DriveInfo[] driveInfoList = new DriveInfo[0];
		File[] roots = File.listRoots();
		if(roots != null) {
			driveInfoList = new DriveInfo[roots.length];
			for(int i = 0;i<roots.length;i++) {
				File driveObj = roots[i];
				DriveInfo info = new DriveInfo();
				info.setDriveLetter(driveObj.getAbsolutePath());
				info.setDriveDisplayName(FileSystemView.getFileSystemView().getSystemDisplayName(driveObj));
				info.setDriveType(FileSystemView.getFileSystemView().getSystemTypeDescription(driveObj));
				driveInfoList[i] = info;
			}
		}
		return driveInfoList;
	}
	
	/**
	 * Uses drive letter passed in by driveInfo object, looks for DVD info on that drive,
	 * and returns a populated DVDInfo object if a valid DVD is found
	 * 
	 * @param driveInfo
	 * @return
	 * @throws IOException
	 */
	public static DVDInfo getDVDInfo(DriveInfo driveInfo) throws IOException {
		DVDInfo dvdInfo = null;
		if(driveInfo != null && driveInfo.getDriveLetter() != null) {
			String drivePath = driveInfo.getDriveLetter();
			// run command to get list of dvd track lengths
			String command = String.format(MPLAYER_IDENTIFY_FORMATTED_PARAMS, getUtilDirectoryPath() + "mplayer.exe", drivePath);
			List<String> dvdInfoMessages = runCommandAndGetOutputAsStringArray(command);
			
			double maxTrackLength = -1, currentTrackLength = -1;
			String maxTrackNumber = null;
			String movieName = null;
			Properties dvdProperties = new Properties();
			// loop through messages and find max track length, and return that track number
			for (String infoMsg : dvdInfoMessages) {
				if((currentTrackLength = getTrackLengthFromMplayerMessage(infoMsg)) > maxTrackLength) {
					maxTrackLength = currentTrackLength;
					maxTrackNumber = getTrackNumberFromMplayerMessage(infoMsg);
				}
				// store each parameter in a properties object to be referenced later
				String[] keyAndValue = infoMsg.split("=");
				if(keyAndValue != null && keyAndValue.length == 2 && keyAndValue[0] != null && keyAndValue[1] != null) {
					dvdProperties.put(keyAndValue[0], keyAndValue[1]);
				}
			}
			
			movieName = dvdProperties.getProperty(DVD_INFO_PARAM);
			
			// check if we failed to collect the required info, and return null if so
			if(maxTrackLength == -1 || maxTrackNumber == null) {
				return null;
			}
			
			// populate DVDInfo object with collected information
			dvdInfo = new DVDInfo();
			dvdInfo.setDriveInfo(driveInfo);
			dvdInfo.setDvdName(movieName);
			dvdInfo.setMovieLength(maxTrackLength);
			dvdInfo.setMovieTrack(maxTrackNumber);
			// grab cropping coordinates
//			dvdInfo.setCroppingCoordinates(CROPPING_COORDINATES_PARAM);
		}
		
		return dvdInfo;
	}
	
	/**
	 * Converts length (in seconds) to a human readable text
	 * ex: 33 minutes 14 seconds
	 * 
	 * @param length
	 * @return
	 */
	public static String getMovieLengthAsReadableText(double length) {
		if(length > 0) {
			double lengthInMinutes = length/60.0;
			int seconds = (int)Math.ceil((lengthInMinutes - ((int)lengthInMinutes)) * 60.0);
			return ((int)lengthInMinutes) + " minutes " + seconds + " seconds";
		} else {
			return "";
		}
	}
	
	/**
	 * Attempts to get a track length from the passed in mplayer message
	 * @param message
	 * @return
	 */
	private static double getTrackLengthFromMplayerMessage(String message) {
		// check if info message contains track length info
		if(message != null && message.startsWith(TRACK_LENGTH_MESSAGE_PREFIX) && message.contains(TRACK_LENGTH_MESSAGE_POSTFIX)) {
			return Double.parseDouble(message.substring(message.indexOf("=") + 1));
		}
		return -1;
	}
	
	/**
	 * Attempts to get a track number from the passed in mplayer message
	 * 
	 * @param message
	 * @return
	 */
	private static String getTrackNumberFromMplayerMessage(String message) {
		// check if info message contains track length info
		if(message != null && message.startsWith(TRACK_LENGTH_MESSAGE_PREFIX) && message.contains(TRACK_LENGTH_MESSAGE_POSTFIX)) {
			String choppedMessage = message.substring(TRACK_LENGTH_MESSAGE_PREFIX.length());
			choppedMessage = choppedMessage.substring(0, message.indexOf("_") - 1);
			return choppedMessage;
		}
		return null;
	}
	
	public static void ripSegmentFromDVD(ProcessStreamHandler handler, String segmentName, String outputDirectory, DVDInfo dvdInfo, String startTime, String endTime, boolean isFirstPass) throws IOException {
		String outputFilePath = outputDirectory + "\\" + segmentName;
		
		// check if we need to run our first pass still, and if so, do it
		if(isFirstPass) {
			try {
				String command = String.format(PASS1_ENCODING_FORMATTED_PARAMS, getUtilDirectoryPath() + "mencoder.exe", dvdInfo.getDriveInfo().getDriveLetter(), dvdInfo.getMovieTrack());
				runCommandAndGetOutputAsStringArray(command);
			} catch (Exception ex) {
				throw new IOException("Failed to run first pass", ex);
			}
		}
		
		// attempt to get crop parameter
		String cropParameter = "";
		try {
			//cropParameter = getCropParameter(dvdInfo, startTime);
		} catch (Exception ex) {
			log.error("Failed to get crop parameter", ex);
		}
		
		// run second pass, and actually rip segment
		int segmentDuration = Util.getSegmentLengthInSeconds(endTime) - Util.getSegmentLengthInSeconds(startTime);
		try {
			String command = String.format(PASS2_ENCODING_FORMATTED_PARAMS, getUtilDirectoryPath() + "mencoder.exe", dvdInfo.getDriveInfo().getDriveLetter(), dvdInfo.getMovieTrack(), "", startTime, segmentDuration, outputFilePath);
			runCommandAndGetOutputAsStringArray(command, handler, null);
		} catch (Exception ex) {
			throw new IOException("Failed to run second pass/rip segment.  error: " + ex.getMessage());
		}
		
		// check that output file exists, and if not, throw an exception
		if(!new File(outputDirectory).exists()) {
			throw new IOException("No file produced for segment: " + segmentName);
		}
	}
	
	private static String getCropParameter(DVDInfo dvdInfo, String startTime) throws IOException {
		String cropParam = null;
		List<String> output = new ArrayList<String>();
		try {
			String command = String.format(MPLAYER_CROPDETECT_FORMATTED_PARAMS, getUtilDirectoryPath() + "mplayer.exe", dvdInfo.getDriveInfo().getDriveLetter(), startTime, dvdInfo.getMovieTrack());
			output = runCommandAndGetOutputAsStringArray(command);
		} catch (Exception ex) {
			throw new IOException("failed to determine crop settings.  error: " + ex.getMessage());
		}
		for (String outputText : output) {
			if(outputText != null && outputText.startsWith("[CROP]")) {
				// parse crop text
				int start = outputText.indexOf("-vf ");
				int end = outputText.lastIndexOf(')');
				if(start != -1 && end != -1 && end > start) {
					cropParam = outputText.substring(start + "-vf ".length(), end);
					// quit looping, we found what we're looking for
					break;
				}
			}
		}
		if(cropParam == null) {
			throw new IOException("Failed to retrieve crop parameters");
		}
		return cropParam;
	}
	
	private static String getJarDirectory() {
		// get directory .jar file is running from (using substring() to remove leading slash)
		String workingDir = Util.class.getProtectionDomain().getCodeSource().getLocation().getPath();
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
	
	/**
	 * Gets absolute path of the "utils" directory used by this program, where this
	 * directory should contain all necessary utlities and binaries for inspecting and ripping
	 * a DVD
	 * 
	 * @return
	 */
	private static String getUtilDirectoryPath() {
		return getJarDirectory() + "\\" + UTILITY_DIRECTORY + "\\";
	}
	
	public static String getResourceDirectoryPath() {
		return getJarDirectory() + "\\" + RESOURCES_DIRECTORY + "\\";
	}
	
	private static final int[] SEGMENT_DIGIT_MULTIPLIERS = {1,60,3600,24,365};
	
	public static int getSegmentLengthInSeconds(String segment) {
		// make sure string isn't empty
		if(segment == null || "".equals(segment.trim())) return -1;
		// split digits by colon or space
		segment = segment.trim();
		String[] items = segment.split(":| ");
		// verify items between spacers are actually numbers
		for(int i = 0; i < items.length; i++) {
			try {
				Integer.valueOf(items[i]);
			} catch (NumberFormatException ex) {
				return -1;
			}
		}
		// convert items into seconds
		int seconds = 0;
		for(int i = items.length - 1, j = 0; i >= 0 && j < SEGMENT_DIGIT_MULTIPLIERS.length; i--, j++) {
			seconds += Integer.valueOf(items[i]) * SEGMENT_DIGIT_MULTIPLIERS[j];
		}
		return seconds;
	}
	
	public static String convertSegmentToMencoderFriendlyFormat(String segment) {
		if(Util.getSegmentLengthInSeconds(segment) != -1) {
			segment = segment.trim();
			return segment.replaceAll(" ", ":");
		}
		return null;
	}
	
	private static List<String> runCommandAndGetOutputAsStringArray(String command) throws IOException {
		return runCommandAndGetOutputAsStringArray(command, null, null);
	}
	
	/**
	 * Runs the passed in command and returns the commands stdout output lines as a list
	 * 
	 * @param commands
	 * @return
	 * @throws IOException
	 */
	private static List<String> runCommandAndGetOutputAsStringArray(String command, ProcessStreamHandler stdoutHandler, ProcessStreamHandler stderrHandler) throws IOException {
		Runtime r = Runtime.getRuntime();
		log.info("Running command: " + command);
		// run command
		Process p = null;
		try {
			p = r.exec(command);
		} catch (Throwable e) {
			log.error("Error executing command...", e);
			// if an exception is thrown, be sure to destroy the running process
			if(p != null) {
				p.destroy();
			}
		}
		
		// collect stdout and stderr messages via handlers as the process runs
		if(stdoutHandler == null) {
			stdoutHandler = new ProcessStreamHandler();
		}
		if(stderrHandler == null) {
			stderrHandler = new ProcessStreamHandler();
		}
		stderrHandler.processStream(p.getErrorStream());
		stdoutHandler.processStream(p.getInputStream());
		
		
		// wait for command to complete
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// check if exit value does not indicate success, and output error messages to console
		if(p.exitValue() != 0) {
			log.error("Error running command: " + command);
			for (String errMsg : stderrHandler.getStreamData()) {
				log.error(errMsg);
			}
			return null;
		}
		
		return stdoutHandler.getStreamData();
	}
}
