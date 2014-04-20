package org.malibu.dvd.sectionripper.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Stream reader that reads from a stream in a separate thread, saving stream info into the supplied String array
 * 
 * @author Ampp33
 *
 */
public class ProcessStreamHandler extends Thread {
	
	static Logger log = Logger.getLogger(ProcessStreamHandler.class);
	
	private List<String> streamData = null;
	private InputStream stream = null;
	private int numberOfExecutions = 0;
	
	/**
	 * @param streamData String array to load messages from stream into
	 * @param stream stream to read from
	 */
	public ProcessStreamHandler() {
		this.streamData = new ArrayList<String>();
	}
	
	public void processStream(InputStream stream) {
		this.numberOfExecutions++;
		this.stream = stream;
		start();
	}
	
	@Override
	public void run() {
		BufferedReader bufferedStream = null;
		try {
			// create reader to read stream
			bufferedStream = new BufferedReader(new InputStreamReader(this.stream));
			String line = null;
			while((line = bufferedStream.readLine()) != null) {
				addStreamData(line);
			}
		} catch (IOException e) {
			log.error("Error occurred reading process stream...", e);
		} finally {
			// stream cleanup
			if(bufferedStream != null) {
				try {
					bufferedStream.close();
				} catch (IOException e) {}
			}
		}
	}
	
	protected void addStreamData(String data) {
		this.streamData.add(data);
		log.debug("Adding stream data: " + data);
	}
	
	public List<String> getStreamData() {
		return this.streamData;
	}

	public int getNumberOfExecutions() {
		return numberOfExecutions;
	}

	public void setNumberOfExecutions(int numberOfExecutions) {
		this.numberOfExecutions = numberOfExecutions;
	}
}
