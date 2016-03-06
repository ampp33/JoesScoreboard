package org.malibu.joe.scoreboard;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class SoundManager {

	private Clip clip = null;
	private boolean looping = false;
	private int timesToPlay = 0;
	
	public SoundManager(String soundFileName) throws Exception {
		System.out.println(soundFileName);
		this.clip = AudioSystem.getClip();
		AudioInputStream inputStream = AudioSystem
				.getAudioInputStream(
						new BufferedInputStream(
								SoundManager.class.getClassLoader().getResourceAsStream(soundFileName)
						)
				);
		clip.open(inputStream);
		init();
	}
	
	public SoundManager(File file) throws Exception {
		this.clip = AudioSystem.getClip();
		AudioInputStream inputStream = AudioSystem
				.getAudioInputStream(
						new BufferedInputStream(
							new FileInputStream(file)
						)
				);
		clip.open(inputStream);
		init();
	}
	
	private void init() {
		clip.addLineListener(new LineListener() {
			@Override
			public void update(LineEvent event) {
				if(event.getType() == LineEvent.Type.START) {
					if(timesToPlay > 0) {
						timesToPlay--;
					}
				} else if(event.getType() == LineEvent.Type.STOP) {
					clip.setFramePosition(0);
					if(timesToPlay > 0 || looping) {
						clip.loop(0);
					}
				}
			}
		});
	}

	public void play(int times) {
		clip.loop(times - 1);
	}
	
	public void incrementTimesToPlay() {
		timesToPlay++;
	}
	
	public void loop() {
		looping = true;
		clip.loop(0);
	}
	
	public void stopLoop() {
		looping = false;
	}
	
	public boolean isLooping() {
		return looping;
	}
	
}
