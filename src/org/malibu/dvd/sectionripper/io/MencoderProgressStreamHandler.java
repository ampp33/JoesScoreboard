package org.malibu.dvd.sectionripper.io;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.malibu.dvd.sectionripper.gui.progress.ProgressToken;

public class MencoderProgressStreamHandler extends ProcessStreamHandler {
	
	private ProgressToken progressToken = null;
	private int segmentLengthInSeconds = 0;
	List<String> streamData = null;
	
	public MencoderProgressStreamHandler(int segmentLengthInSeconds, ProgressToken progressToken) {
		this.segmentLengthInSeconds = segmentLengthInSeconds;
		this.progressToken = progressToken;
	}

	@Override
	protected void addStreamData(String data) {
		
		double positionInSeconds = getPositionInSeconds(data);
		if(positionInSeconds != -1) {
			progressToken.setPercentComplete((int)(((double)positionInSeconds/(double)segmentLengthInSeconds) * 100));
		}
		
		super.addStreamData(data);
	}
	
	private double getPositionInSeconds(String text) {
		double result = -1;
		Pattern pattern = Pattern.compile("^Pos:[ ]+([\\d\\.]+)s.+$");
	    Matcher matcher = pattern.matcher(text);
	    if (matcher.find()) {
	    	try {
	    		result = Double.valueOf(matcher.group(1));
	    	} catch (Exception ex) {}
	    }
	    return result;
	}
}
